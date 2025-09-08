# dummy-pdf-sizegen

Small CLI tool (`dummy-pdf-sizegen`) to produce a PDF file of a specified size. The program writes a minimal PDF then pads the file to reach the exact byte count you requested.

## Quick start

From project root:

### Linux / macOS

```bash
# build the JAR
./gradlew build

# run (example, fractional MB)
./dummy-pdf-sizegen 47.7    # interpreted as 47.7 MiB (binary)

# run (example, integer)
./dummy-pdf-sizegen 33      # interpreted as 33 MB (decimal) so Explorer will show ~33 MB
```

### Windows

```batch
# build the JAR
.\gradlew.bat build

# run (example, fractional MB)
.\dummy-pdf-sizegen.bat 47.7

# run (example, integer)
.\dummy-pdf-sizegen.bat 33
```

`./dummy-pdf-sizegen` (for Linux/macOS) and `dummy-pdf-sizegen.bat` (for Windows) are small wrappers in the project root that call the built JAR. You can move them to a PATH directory to run globally.

## Input rules
- Integer input with no decimal point (e.g. `33`) is treated as *decimal MB* (1 MB = 1,000,000 bytes). This makes OS/Explorer display round to that MB value.
- Fractional input (e.g. `47.7` or `47,7`) is treated as *binary MiB* (1 MiB = 1,048,576 bytes).

## Output
- The generated file is written as `dummy.pdf` in the project root, and then renamed to include its size, like `dummy-48-86MB.pdf`.
- The program prints a friendly block with:
  - Bytes
  - Binary (MiB)
  - Decimal (MB)
  - Explorer/OS display (rounded)
  - A small note which target mode was used

## Caveats
- Padding is implemented by appending zero bytes after a minimal valid PDF. Most PDF readers ignore trailing bytes and the file will open normally, but the padding is not embedded inside the PDF structure. If you require a strictly internal PDF stream of an exact size (no trailing bytes), the code can be changed to embed a padding stream instead.

dummy-pdf-sizegen 33
## Customization
To install the wrapper to a system path (requires sudo) you have a few safe options. Important: do NOT simply copy the wrapper to `/usr/local/bin` without also ensuring the wrapper can find the JAR — copying a script that uses a relative `build/libs/...` path will make it look for the JAR under `/usr/local/bin/build/libs/...` and fail with "Unable to access jarfile /usr/local/bin/build/libs/...".

Recommended: install a symlink (keeps the repo copy as the single source of truth)

```bash
# build the jar first
./gradlew build

# create a global symlink (preferred)
sudo ln -sf "$PWD/dummy-pdf-sizegen" /usr/local/bin/dummy-pdf-sizegen
```

Alternative: install a copy (safe if you prefer a standalone copy)

```bash
# copy the wrapper and set executable bit
sudo install -m 0755 dummy-pdf-sizegen /usr/local/bin/dummy-pdf-sizegen
```

User-local install (no sudo)

```bash
mkdir -p ~/.local/bin
cp dummy-pdf-sizegen ~/.local/bin/
chmod 0755 ~/.local/bin/dummy-pdf-sizegen
# add ~/.local/bin to PATH if needed
```

Homebrew users: prefer `/opt/homebrew/bin` as the install location on Apple Silicon.

Troubleshooting
- If you see "Unable to access jarfile /usr/local/bin/build/libs/...": remove the broken copy and install a symlink instead:

```bash
sudo rm -f /usr/local/bin/dummy-pdf-sizegen
sudo ln -s "$PWD/dummy-pdf-sizegen" /usr/local/bin/dummy-pdf-sizegen
```

- Make sure you built the JAR before running the wrapper:

```bash
./gradlew clean build
```

After installation you can run from anywhere:

```bash
dummy-pdf-sizegen 33
```

---

Example output (for a run targeting 46.6 MiB):

```
============================================================
📄 dummy-48-86MB.pdf — Final Size
──────────────────────────────────────────────
• Binary (MiB):         46.60 MiB   (1 MiB = 1,048,576 bytes)

• Bytes:                48,863,642

• Decimal (MB):         48.86 MB    (1 MB  = 1,000,000 bytes)

• Explorer/OS actual size display:  49 MB (rounded)
============================================================
(Target mode used: binary (MiB))
```

License: see `LICENSE` (MIT) in the project root.

## License & notes
- Small utility for testing and fixtures. Use as needed.