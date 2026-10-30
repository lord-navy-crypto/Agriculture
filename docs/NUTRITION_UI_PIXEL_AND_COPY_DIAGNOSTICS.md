# Nutrition UI Pixel and Copy Diagnostics — 3.7.0-rc.3

## User-visible controls
- Press **N** through the existing key mapping to open the nutrition screen.
- Press **C** or click **Copy** to copy the full server-synchronized snapshot.
- The clipboard report contains all 27 game nutrient scores, water, sodium, Minecraft version, framebuffer size, GUI scale and scaled GUI dimensions.
- These are abstract game points, not real-world nutritional or medical measurements.

## Rendering contract
- Vanilla font at its native size; no pose-stack scale is applied to text.
- Integer GUI coordinates for text and bars.
- Opaque screen backdrop; do not call the Screen background-blur rendering path.
- The panel and button row are bounded by the current scaled viewport.
- Labels are fitted to their own slots; exact numeric values are printed separately.

## What this does not prove
Source-level tests and a successful NeoForge server boot do not prove the user's local macOS GPU/window pipeline is crisp. If the display still looks uniformly blurred, press **C**, paste the copied report into the support conversation, and include an F2 screenshot. The report exposes the actual GUI scale and framebuffer-to-GUI dimensions so the next investigation can distinguish a scale mismatch from a global post-processing or driver issue.
