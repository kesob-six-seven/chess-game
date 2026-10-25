# Practice Project (Chess Game Using Java)

A lightweight 1v1 local chess game built purely with Java and Swing. Zero external dependencies.

---

## Features
- **Full Chess Rules**: Legal move generation, checks, checkmate, stalemate, castling, en passant, and pawn promotion.
- **Visuals & Audio**: Custom Java2D vector pieces, board coordinates, move indicators, and synthesized sound effects.
- **1v1 Local Controls**: Board flipping, optional auto-flip per turn, chess clocks (blitz/rapid/classical/untimed), move history log (SAN), captured pieces tracker, and undo move.
- **Board Themes**: Classic Wood, Tournament Green, Modern Slate, and Midnight Dark.

---

## Quick Start

### Run
Double-click `run.bat` or run:
```bash
java -jar ChessGame.jar
```

### Compile & Run from Source
```bash
javac -d bin src\com\chess\model\*.java src\com\chess\audio\*.java src\com\chess\ui\*.java src\com\chess\controller\*.java src\com\chess\Main.java
java -cp bin com.chess.Main
```

### Run Tests
```bash
java -cp bin com.chess.test.ChessEngineTest
```
