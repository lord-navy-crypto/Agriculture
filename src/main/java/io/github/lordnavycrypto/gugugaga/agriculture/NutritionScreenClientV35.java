package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.Arrays;
import java.util.Locale;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;

/**
 * Server-snapshot-only nutrition dashboard.
 *
 * <p>Uses native vanilla font rendering and integer GUI coordinates only.
 * No blur post-process, texture scaling, pose scaling, or client-side
 * recomputation is used. Press C or the Copy button to export every exact
 * server-synchronized value plus display diagnostics.</p>
 */
public final class NutritionScreenClientV35 extends Screen {
    private static final int[][] PAGES={{0,5},{5,12},{12,18},{18,23},{23,27},{0,0}};
    private static final int PANEL_MAX_WIDTH=392;
    private static final int PANEL_MAX_HEIGHT=276;
    private static final int TEXT=0xFFF1F5EA;
    private static final int DETAIL=0xFFE9CF9A;
    private static final int GOOD=0xFF91E2A5;
    private static final int BAR_BASE=0xFF43514B;
    private static final int BAR_FILL=0xFF95C5A0;

    private final int[] values;
    private final int water;
    private final int salt;
    private int page=0;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private String feedback="";
    private int feedbackTicks;

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
        int navWidth=34;
        int copyWidth=54;
        int doneWidth=54;
        int gap=3;
        int rowWidth=navWidth+copyWidth+doneWidth+navWidth+gap*3;
        int x=panelX+(panelWidth-rowWidth)/2;
        addRenderableWidget(Button.builder(Component.literal("<"),b->setPage(page-1))
                .bounds(x,buttonY,navWidth,20).build());
        x+=navWidth+gap;
        addRenderableWidget(Button.builder(Component.literal("Copy"),b->copySnapshot())
                .bounds(x,buttonY,copyWidth,20).build());
        x+=copyWidth+gap;
        addRenderableWidget(Button.builder(Component.literal("Done"),b->onClose())
                .bounds(x,buttonY,doneWidth,20).build());
        x+=doneWidth+gap;
        addRenderableWidget(Button.builder(Component.literal(">"),b->setPage(page+1))
                .bounds(x,buttonY,navWidth,20).build());
    }

    @Override
    public void tick() {
        super.tick();
        if(feedbackTicks>0)feedbackTicks--;
        if(feedbackTicks==0)feedback="";
    }

    private void setPage(int newPage) {
        page=Math.floorMod(newPage,PAGES.length);
    }

    @Override
    public boolean keyPressed(int keyCode,int scanCode,int modifiers) {
        if(keyCode==GLFW.GLFW_KEY_C) {
            copySnapshot();
            return true;
        }
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

    private void copySnapshot() {
        Minecraft minecraft=Minecraft.getInstance();
        Window window=minecraft.getWindow();
        StringBuilder out=new StringBuilder(4096);
        out.append("AGRICULTURE NUTRITION SNAPSHOT\n");
        out.append("Minecraft: ").append(SharedConstants.getCurrentVersion().getName()).append('\n');
        out.append("Display framebuffer: ").append(window.getWidth()).append('x').append(window.getHeight()).append('\n');
        out.append("GUI scale: ").append(String.format(Locale.ROOT,"%.3f",window.getGuiScale())).append('\n');
        out.append("GUI scaled dimensions: ").append(window.getGuiScaledWidth()).append('x')
                .append(window.getGuiScaledHeight()).append('\n');
        out.append("Data source: server-synchronized snapshot; scores are abstract game points, not medical measurements.\n\n");
        out.append("NUTRIENT SCORES\n");
        for(int i=0;i<values.length;i++) {
            String id=NutritionProfilesV35.NUTRIENTS[i];
            String label=Component.translatable("nutrition.gugugaga."+id).getString();
            out.append(String.format(Locale.ROOT,"%02d. %s [%s]: %d/100%n",i+1,label,id,values[i]));
        }
        out.append("\nWATER LEVEL: ").append(water).append("/100\n");
        out.append("SODIUM BALANCE: ").append(salt).append("/100\n");
        out.append("Displayed page: ").append(page+1).append('/').append(PAGES.length).append('\n');
        minecraft.keyboardHandler.setClipboard(out.toString());
        feedback="COPIED • 27 SCORES + WATER + SODIUM";
        feedbackTicks=80;
    }

    @Override
    public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick) {
        // Deliberately bypass Screen.renderBackground(): it may invoke the
        // vanilla GUI blur post-process. Draw an opaque backdrop instead.
        graphics.fill(0,0,width,height,0xFF080D0B);
        int x=panelX,y=panelY,w=panelWidth,h=panelHeight;
        graphics.fill(x,y,x+w,y+h,0xFF192621);
        graphics.fill(x,y,x+w,y+2,0xFFB8D7A3);

        drawCenteredFitted(graphics,title,y+9,w-18,TEXT);
        Component category=feedbackTicks>0?Component.literal(feedback)
                :Component.translatable("message.gugugaga.v35_page_"+page);
        drawCenteredFitted(graphics,category,y+28,w-18,feedbackTicks>0?GOOD:DETAIL);

        if(page==5)drawWaterSalt(graphics);
        else drawNutrients(graphics);

        if(h<220) {
            graphics.drawString(font,(page+1)+"/"+PAGES.length,x+w-38,y+9,DETAIL,false);
        } else {
            drawCenteredFitted(graphics,Component.literal((page+1)+" / "+PAGES.length),
                    y+h-53,w-18,DETAIL);
            drawCenteredFitted(graphics,Component.translatable("message.gugugaga.v35_gui_note"),
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
            Component name=Component.translatable("nutrition.gugugaga."+NutritionProfilesV35.NUTRIENTS[i]);
            drawFitted(graphics,name,nameX,yy,nameWidth,TEXT);
            int barY=yy+1;
            graphics.fill(barX,barY,barX+barWidth,barY+9,BAR_BASE);
            graphics.fill(barX,barY,barX+(barWidth*value/100),barY+9,BAR_FILL);
            graphics.drawString(font,Integer.toString(value),barX+barWidth+8,yy,TEXT,false);
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
        drawFitted(graphics,Component.translatable("nutrition.gugugaga."+id),x,y,rowWidth,TEXT);
        int barWidth=Math.max(30,rowWidth-78);
        graphics.fill(x,y+15,x+barWidth,y+27,BAR_BASE);
        graphics.fill(x,y+15,x+(barWidth*value/100),y+27,0xFF83B8D0);
        graphics.drawString(font,value+" / 100",x+barWidth+9,y+17,TEXT,false);
    }

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
