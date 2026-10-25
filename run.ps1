# Launch script for Java Chess
if (-not (Test-Path "ChessGame.jar")) {
    Write-Host "Compiling chess game..." -ForegroundColor Cyan
    if (-not (Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }
    $sources = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
    & javac -d bin $sources
    Set-Content -Path "manifest.txt" -Value "Main-Class: com.chess.Main`n"
    & jar cfm ChessGame.jar manifest.txt -C bin .
    Remove-Item "manifest.txt" -ErrorAction SilentlyContinue
}

Write-Host "Launching Java Chess 1v1 Local..." -ForegroundColor Green
Start-Process "javaw" -ArgumentList "-jar", "ChessGame.jar"
