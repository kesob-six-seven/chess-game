@echo off
if not exist ChessGame.jar (
    echo ChessGame.jar not found. Compiling first...
    call compile.bat
)

echo Starting Java Chess 1v1 Local...
start javaw -jar ChessGame.jar
