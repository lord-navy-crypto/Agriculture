#!/usr/bin/env python3
"""Regressions for high-GUI-scale N-key nutrition text, buttons, and scores.

Source/layout checks do not assert a Minecraft client screenshot pass.
"""
from pathlib import Path

root=Path(__file__).resolve().parents[1]
java=root/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
screen=(java/"NutritionScreenClientV35.java").read_text("utf-8")
inventory=(java/"NutritionInventoryButtonV37.java").read_text("utf-8")
hud=(java/"NutritionHudV37.java").read_text("utf-8")
request=(java/"NutritionNetworkV34.java").read_text("utf-8")
response=(java/"NutritionNetworkV35.java").read_text("utf-8")
assert "private int page=0;" in screen, "N key should show the nutrient list"
assert "PAGES={{0,5},{5,12},{12,18},{18,23},{23,27},{0,0}}" in screen
assert "panelWidth=Math.min(PANEL_MAX_WIDTH,Math.max(1,width-16))" in screen
assert "panelHeight=Math.min(PANEL_MAX_HEIGHT,Math.max(1,height-12))" in screen
assert "renderBackground(graphics" not in screen, "blurred GUI background may obscure text"
assert "pose().scale(" not in screen, "fractional font scaling blurs pixel glyphs"
assert "font.plainSubstrByWidth" in screen, "names must not overlap numeric slots"
assert "Integer.toString(value)" in screen, "always show exact server numeric score"
assert "drawVital(graphics" in screen and 'value+" / 100"' in screen
assert "GLFW.GLFW_KEY_LEFT" in screen and "GLFW.GLFW_KEY_RIGHT" in screen
assert "PAGES.length" in screen
assert 'Button.builder(Component.literal("Copy"),b->copySnapshot())' in screen
assert "GLFW.GLFW_KEY_C" in screen and "private void copySnapshot()" in screen
assert "keyboardHandler.setClipboard(out.toString())" in screen
assert "Display framebuffer:" in screen and "GUI scaled dimensions:" in screen
assert "NutritionProfilesV35.NUTRIENTS[i]" in screen
assert "renderBackground(graphics" not in screen
assert "graphics.fill(0,0,width,height,0xFF080D0B)" in screen
assert "graphics.pose().scale(" not in screen
assert "ScreenEvent.Init.Post" in inventory
assert "Math.min(screen.width-74" in inventory, "inventory button must stay visible"
assert "minecraft.font.plainSubstrByWidth" in hud
assert "NutritionV34.show(player)" not in request, "N key must not open legacy chat report"
assert "new NutritionNetworkV35.State(NutritionV34.snapshot(player)" in request
assert "if (data.scores().length != 27)" in response
assert "playToClient(State.TYPE,State.STREAM_CODEC" in response

# Reproduce the exact integer panel geometry from the source without a GPU.
# Assert that all seven score values stay above footer/nav at these scales.
for width,height in ((320,180),(320,240),(426,240),(480,270),
                     (854,480),(1920,1080)):
    w=min(392,max(1,width-16))
    h=min(276,max(1,height-12))
    x=(width-w)//2
    y=(height-h)//2
    button_y=y+h-27
    row_width=34+54+54+34+3*3
    row_x=x+(w-row_width)//2
    left=(row_x,row_x+34)
    copy=(left[1]+3,left[1]+3+54)
    done=(copy[1]+3,copy[1]+3+54)
    right=(done[1]+3,done[1]+3+34)
    assert 0<=x and x+w<=width, (width,height,"panel x")
    assert 0<=y and y+h<=height, (width,height,"panel y")
    assert 0<=left[0] and left[1]<=width, (width,height,"left")
    assert 0<=right[0] and right[1]<=width, (width,height,"right")
    assert left[1]<copy[0] and copy[1]<done[0] and done[1]<right[0], (width,height,"buttons")
    assert row_x>=x and right[1]<=x+w, (width,height,"button row inside panel")
    assert button_y+20<=height, (width,height,"button y")
    row_start=y+51
    row_end=y+h-(37 if h<220 else 67)
    row_height=max(10,(row_end-row_start)//7)
    last_row=y+51+6*row_height
    assert last_row+10<button_y, (width,height,"score vs nav")
    if h>=220:
        assert last_row+10<y+h-53, (width,height,"score vs page count")
print("NUTRITION_UI_READABILITY_OK: 27 server scores + 2 vitals, 6 pages, "
      "native unblurred font, clipped labels, no duplicate chat, scaled layout.")
