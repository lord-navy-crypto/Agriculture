package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/**
 * Server-snapshot-only nutrition dashboard.
 *
 * <p>All coordinates are Minecraft's scaled GUI coordinates, and all glyphs
 * use the vanilla font at native 1:1 pixel size. The backdrop is a flat
 * translucent fill rather than a world-blur post-processing pass: this keeps
 * labels, values and widgets on a single crisp GUI layer even alongside other
 * mods' HUD renderers. No client-derived nutrition scores exist.</p>
 */
public final class NutritionScreenClientV35 extends Screen {
    private static final int[][] PAGES={{0,5},{5,12},{12,18},{18,23},{23,27},{0,0}};
    private static final int PANEL_MAX_WIDTH=392;
    private static final int PANEL_MAX_HEIGHT=276;
    private static final int TEXT=0xFFF1F5EA;
    private static final int DETAIL=0xFFE9CF9A;
    private static final int BAR_BASE=0xFF43514B;
    private static final int BAR_FILL=0xFF95C5A0;

    private final int[] values;
    private final int water;
    private final int salt;
    // The N key opens the actual nutrient list first. Hydration remains page 6.
    private int page=0;

    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;

    private NutritionScreenClientV35(int[] snapshot,int water,int salt) {
        super(Component.translatable("message.gugugaga.v35_gui_title"));
        this.values=Arrays.copyOf(snapshot,27);
        this.water=Math.max(0,Math.min(100,water));
        this.salt=Math.max(0,Math.min(100,salt));
    }

    public static void open(int[] scores,int water,int salt) {
        if(scores==null || scores.length!=27)return;
        Minecraft.getInstance().setScreen(new NutritionScreenClientV35(scores,water,salt));
    }

    /** Bounds are recomputed after GUI Scale / window changes. */
    private void updatePanelBounds() {
        panelWidth=Math.min(PANEL_MAX_WIDTH,Math.max(1,width-16));
        panelHeight=Math.min(PANEL_MAX_HEIGHT,Math.max(1,height-12));
        panelX=(width-panelWidth)/2;
        panelY=(height-panelHeight)/2;
    }

    @Override
    protected void init() {
        updatePanelBounds();
        int buttonY=panelY+panelHeight-27;
        addRenderableWidget(Button.builder(Component.literal("<"),
                b -> setPage(page-1))
                .bounds(panelX+9,buttonY,44,20).build());
        addRenderableWidget(Button.builder(Component.literal(">"),
                b -> setPage(page+1))
                .bounds(panelX+panelWidth-53,buttonY,44,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"),
                b -> onClose())
                .bounds(width/2-38,buttonY,76,20).build());
    }

    private void setPage(int newPage) {
        page=Math.floorMod(newPage,PAGES.length);
    }

    @Override
    public boolean keyPressed(int keyCode,int scanCode,int modifiers) {
        if(keyCode==GLFW.GLFW_KEY_LEFT) {
            setPage(page-1);
            return true;
        }
        if(keyCode==GLFW.GLFW_KEY_RIGHT) {
            setPage(page+1);
            return true;
        }
        return super.keyPressed(keyCode,scanCode,modifiers);
    }

    @Override
    public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick) {
        // Do not call renderBackground(): a world-blur pass plus foreign HUD
        // layers can obscure the entire foreground on NeoForge 1.21.1.
        // Native-pixel, integer-aligned fills and font rendering stay crisp.
        graphics.fill(0,0,width,height,0xB0000000);
        int x=panelX,y=panelY,w=panelWidth,h=panelHeight;
        graphics.fill(x,y,x+w,y+h,0xFF192621);
        graphics.fill(x,y,x+w,y+2,0xFFB8D7A3);

        drawCenteredFitted(graphics,title,y+9,w-18,TEXT);
        Component category=Component.translatable("message.gugugaga.v35_page_"+page);
        drawCenteredFitted(graphics,category,y+28,w-18,DETAIL);

        if(page==5) {
            drawWaterSalt(graphics);
        } else {
            drawNutrients(graphics);
        }

        // All footer positions stay within the visible panel, independent of
        // GUI Scale. Navigation buttons are rendered afterwards by Screen.
        if(h<220) {
            // Compact GUI: keep the content region tall enough for seven real
            // readings. Page index moves to the header, not over the rows.
            graphics.drawString(font,(page+1)+"/"+PAGES.length,
                    x+w-38,y+9,DETAIL,false);
        } else {
            drawCenteredFitted(graphics,Component.literal((page+1)+" / "+PAGES.length),
                    y+h-53,w-18,DETAIL);
            drawCenteredFitted(graphics,
                    Component.translatable("message.gugugaga.v35_gui_note"),
                    y+h-41,w-18,0xFFDCE5D9);
        }
        super.render(graphics,mouseX,mouseY,partialTick);
    }

    private void drawNutrients(GuiGraphics graphics) {
        int start=PAGES[page][0];
        int end=PAGES[page][1];
        int count=end-start;
        if(count==0)return;
        int x=panelX,y=panelY,w=panelWidth,h=panelHeight;
        int rowStart=y+51;
        int rowEnd=y+h-(h<220?37:67);
        int rowHeight=Math.max(10,(rowEnd-rowStart)/count);
        int scoreWidth=Math.max(27,font.width("100"));
        int barWidth=Math.max(38,Math.min(100,w/4));
        int barX=x+w-12-scoreWidth-8-barWidth;
        int nameX=x+14;
        int nameWidth=Math.max(12,barX-nameX-9);

        for(int i=start;i<end;i++) {
            int index=i-start;
            int yy=rowStart+index*rowHeight;
            int value=Math.max(0,Math.min(100,values[i]));
            Component name=Component.translatable(
                    "nutrition.gugugaga."+NutritionProfilesV35.NUTRIENTS[i]);
            drawFitted(graphics,name,nameX,yy,nameWidth,TEXT);
            int barY=yy+1;
            graphics.fill(barX,barY,barX+barWidth,barY+9,BAR_BASE);
            graphics.fill(barX,barY,barX+(barWidth*value/100),barY+9,BAR_FILL);
            // A separate high-contrast numeric readout must never be replaced
            // by a bar alone, a blurred shader, or a hidden internal value.
            graphics.drawString(font,Integer.toString(value),
                    barX+barWidth+8,yy,TEXT,false);
        }
    }

    private void drawWaterSalt(GuiGraphics graphics) {
        int x=panelX,y=panelY,w=panelWidth;
        boolean compact=panelHeight<220;
        drawVital(graphics,x+15,y+(compact?46:59),w-30,"water_level",water);
        drawVital(graphics,x+15,y+(compact?82:104),w-30,"sodium_balance",salt);
        Component status=Component.translatable(water<=15
                ?"message.gugugaga.v37_thirst_low":salt<=8
                ?"message.gugugaga.v37_salt_low":salt>85
                ?"message.gugugaga.v37_salt_high":"message.gugugaga.v37_balanced");
        drawCenteredFitted(graphics,status,y+(compact?115:156),w-24,DETAIL);
    }

    private void drawVital(GuiGraphics graphics,int x,int y,int rowWidth,String id,int value) {
        drawFitted(graphics,Component.translatable("nutrition.gugugaga."+id),
                x,y,rowWidth,TEXT);
        int barWidth=Math.max(30,rowWidth-78);
        graphics.fill(x,y+15,x+barWidth,y+27,BAR_BASE);
        graphics.fill(x,y+15,x+(barWidth*value/100),y+27,0xFF83B8D0);
        graphics.drawString(font,value+" / 100",x+barWidth+9,y+17,TEXT,false);
    }

    /** Text is clipped to its own slot, not drawn over neighbouring values. */
    private void drawFitted(GuiGraphics graphics,Component text,int x,int y,int maxWidth,int color) {
        String value=text.getString();
        if(font.width(value)>maxWidth) {
            String suffix="...";
            value=font.plainSubstrByWidth(value,Math.max(0,maxWidth-font.width(suffix)))+suffix;
        }
        graphics.drawString(font,value,x,y,color,false);
    }

    private void drawCenteredFitted(GuiGraphics graphics,Component text,int y,int maxWidth,int color) {
        String value=text.getString();
        if(font.width(value)>maxWidth) {
            String suffix="...";
            value=font.plainSubstrByWidth(value,Math.max(0,maxWidth-font.width(suffix)))+suffix;
        }
        graphics.drawCenteredString(font,value,width/2,y,color);
    }

    @Override
    public boolean isPauseScreen() {return false;}
}
