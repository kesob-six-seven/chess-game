@echo off
echo ========================================================
echo        Building Java Chess (1v1 Local)
echo ========================================================

if not exist bin mkdir bin

echo Compiling Java source files...
javac -d bin src\com\chess\model\*.java src\com\chess\audio\*.java src\com\chess\ui\*.java src\com\chess\controller\*.java src\com\chess\test\*.java src\com\chess\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Packaging executable JAR ChessGame.jar...
echo Main-Class: com.chess.Main> manifest.txt
echo.>> manifest.txt
jar cfm ChessGame.jar manifest.txt -C bin .

del manifest.txt 2>nul

echo [SUCCESS] Build completed! You can now run run.bat or double-click ChessGame.jar.
pause
