# Java Chess - 1v1 Local Two-Player Game

A complete, feature-rich, and visually polished Chess game designed specifically for 1v1 local play on the same machine with a friend, built purely using **Java and Java Swing / 2D** with **zero external dependencies**.

---

## 🌟 Key Features

### 👑 Complete FIDE Chess Engine
- **Full Legal Move Generation & Filtering:**
  - Standard piece movements: King, Queen, Rook, Bishop, Knight, Pawn.
  - Pin detection (pinned pieces cannot move off their pin line to expose King).
- **Special Moves:**
  - **Castling:** Kingside (`O-O`) and Queenside (`O-O-O`) with official FIDE rules (king and rook must not have moved, transit and destination squares cannot be attacked, cannot castle while in check).
  - **En Passant:** Capturing pawns that moved two squares on the immediate previous ply.
  - **Pawn Promotion:** Visual modal dialog to promote pawns to **Queen**, **Rook**, **Bishop**, or **Knight**.
- **Check & Checkmate Detection:**
  - Visual radial glow highlighting the King in check.
  - Automated checkmate detection with victory announcement.
- **Draw Conditions:**
  - Stalemate (no legal moves while not in check).
  - Insufficient Material (K vs K, K+B vs K, K+N vs K, K+B vs K+B same-colored bishops).
  - 50-Move Rule (100 half-moves without pawn moves or captures).
  - Threefold Repetition (exact board state repeated 3 times).
  - Mutual Draw Agreement (interactive offer/accept dialog).

### 🎨 Visual & Audio Experience
- **High-Fidelity Vector Pieces:**
  - Custom Java2D vector artwork rendered with anti-aliasing, linear gradients, outlines, and drop shadows.
  - Crisp and scalable at any board resolution without depending on OS unicode fonts or external PNG/SVG files.
- **Multiple Board Themes:**
  - *Classic Wood* (Walnut & Almond)
  - *Tournament Green* (Forest & Cream)
  - *Modern Slate* (Ice & Slate Blue)
  - *Midnight Dark* (Charcoal & Graphite)
- **Procedural Tactile Sound Effects:**
  - Synthesized via the standard `javax.sound.sampled` API (no external audio files needed):
    - Move knock
    - Capture thud
    - Check warning chime
    - Castling slide
    - Game over fanfare
- **Interactive Board:**
  - Both **Click-to-Move** and **Drag-and-Drop** piece control.
  - Centered legal move dots for empty squares; hollow perimeter rings for captures.
  - Last-move square highlights (from and to).
  - Hover highlights and hand cursors.
  - Board flip button (switch between White or Black perspective) and optional **Auto-Flip** each turn for hotseat play!

### ⏱️ Clocks & Game Info Panel
- **Chess Clocks:**
  - Untimed (casual friend play)
  - 3 min (Blitz)
  - 5 min (Blitz)
  - 10 min (Rapid)
  - 15 min + 10s increment
  - 30 min (Classical)
- **Move History Log:**
  - Standard Algebraic Notation (SAN) table (e.g., `e4`, `Nf3`, `Bxf7+`, `O-O`, `Qh7#`) with auto-scroll.
- **Captured Pieces & Advantage Tracker:**
  - Miniature display of captured pieces for each side.
  - Real-time material difference badge (e.g., `+3`).
- **Game Controls:**
  - `New Game` (Ctrl+N)
  - `Undo Move` (Ctrl+Z)
  - `Flip Board` (Ctrl+F)
  - `Offer Draw` (Ctrl+D)
  - `Resign` (Ctrl+R)

---

## 🚀 How to Run

### Method 1: Double-Click Launcher (Easiest)
Simply double-click **`run.bat`** (or **`ChessGame.jar`**) in the project folder.

### Method 2: Command Line (PowerShell / Command Prompt)
Run the executable JAR directly:
```bash
java -jar ChessGame.jar
```

Or compile and run directly from source:
```bash
# Compile
javac -d bin src\com\chess\model\*.java src\com\chess\audio\*.java src\com\chess\ui\*.java src\com\chess\controller\*.java src\com\chess\Main.java

# Run
java -cp bin com.chess.Main
```

### Method 3: PowerShell Script
```powershell
.\run.ps1
```

---

## 🧪 Verification & Test Suite
The project includes a 40-assertion automated test suite testing pawn pushes, double pushes, en passant, promotion, castling restrictions, pins, Fool's mate, Scholar's mate, stalemate, 50-move rule, and insufficient material.

To run the verification suite:
```bash
java -cp bin com.chess.test.ChessEngineTest
```

---

## 📁 Project Architecture
```
Chess Game/
├── src/
│   └── com/chess/
│       ├── Main.java                 # Application entry point
│       ├── model/                    # Pure chess engine & rules
│       │   ├── PieceColor.java       # WHITE, BLACK
│       │   ├── PieceType.java        # PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
│       │   ├── Square.java           # 8x8 coordinates, algebraic converter
│       │   ├── Piece.java            # Piece model
│       │   ├── MoveType.java         # NORMAL, CAPTURE, CASTLE, EN_PASSANT, PROMOTION
│       │   ├── Move.java             # Move representation and SAN generator
│       │   ├── Board.java            # Complete chess board state & move generator
│       │   ├── GameState.java        # Turn & game outcome states
│       │   └── MoveHistory.java      # Undo/redo stack and repetition tracking
│       ├── audio/
│       │   └── SoundManager.java     # Procedural audio synthesizer
│       ├── ui/                       # Swing & Java2D Presentation Layer
│       │   ├── ChessTheme.java       # Color palettes
│       │   ├── PiecePainter.java     # High-fidelity vector piece renderer
│       │   ├── BoardPanel.java       # Interactive 8x8 chessboard
│       │   ├── ClockPanel.java       # Chess timer with increment support
│       │   ├── CapturedPanel.java    # Captured pieces & material score
│       │   ├── MoveHistoryPanel.java # SAN move log table
│       │   ├── PromotionDialog.java  # Visual piece selection popup
│       │   ├── InfoPanel.java        # Sidebar controls & status
│       │   └── ChessFrame.java       # Main desktop application window
│       ├── controller/
│       │   └── GameController.java   # Coordinates game loop & interactions
│       └── test/
│           └── ChessEngineTest.java  # 40-test automated verification suite
├── bin/                              # Compiled .class files
├── ChessGame.jar                     # Standalone double-clickable JAR
├── run.bat                           # Windows batch launcher
├── compile.bat                       # Build script
├── run.ps1                           # PowerShell launcher
└── README.md                         # Documentation
```
