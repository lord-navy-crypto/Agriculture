package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Read-only game nutrition dashboard. Data always arrives from the server;
 * the GUI never claims to measure real calories, health, body or deficiencies.
 */
public final class NutritionScreenClientV35 extends Screen {
    private static final int[][] PAGES={{0,5},{5,12},{12,18},{18,23},{23,27},{0,0}};
    private final int[] values;
    private int page=5;
    private final int water;
    private final int salt;
    private Button back;
    private Button next;

    private NutritionScreenClientV35(int[] snapshot,int water,int salt) {
        super(Component.translatable("message.gugugaga.v35_gui_title"));
        this.values=Arrays.copyOf(snapshot,27);
        this.water=Math.max(0,Math.min(100,water));
        this.salt=Math.max(0,Math.min(100,salt));
    }
    public static void open(int[] scores,int water,int salt) {
        if(scores.length != 27) return;
        Minecraft.getInstance().setScreen(new NutritionScreenClientV35(scores,water,salt));
    }

    @Override
    protected void init() {
        int x=width/2-180;
        int bottom=Math.min(height-22,height/2+126);
        back=addRenderableWidget(Button.builder(Component.literal("◀"),
                b -> setPage((page+PAGES.length-1)%PAGES.length))
                .bounds(x+14,bottom-18,48,20).build());
        next=addRenderableWidget(Button.builder(Component.literal("▶"),
                b -> setPage((page+1)%PAGES.length))
                .bounds(x+300,bottom-18,48,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"),
                b -> onClose()).bounds(width/2-42,bottom-18,84,20).build());
    }
    private void setPage(int newPage) {page=Math.floorMod(newPage,PAGES.length);}

    @Override
    public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick) {
        renderBackground(graphics,mouseX,mouseY,partialTick);
        int x=width/2-180, y=Math.max(6,height/2-135);
        int bottom=Math.min(height-22,height/2+126);
        graphics.fill(x,y,x+360,bottom+3,0xE5222B28);
        graphics.fill(x,y,x+360,y+2,0xFF8EA889);
        graphics.drawCenteredString(font,title,width/2,y+12,0xFFF3E8CF);
        Component category=Component.translatable("message.gugugaga.v35_page_"+page);
        graphics.drawCenteredString(font,category,width/2,y+31,0xFFD7C19C);
        if(page==5) {
            drawVital(graphics,x+15,y+70,"water_level",water);
            drawVital(graphics,x+15,y+107,"sodium_balance",salt);
            Component status=Component.translatable(water<=15
                    ?"message.gugugaga.v37_thirst_low":salt<=8
                    ?"message.gugugaga.v37_salt_low":salt>85
                    ?"message.gugugaga.v37_salt_high":"message.gugugaga.v37_balanced");
            graphics.drawCenteredString(font,status,width/2,y+165,0xFFF5C69F);
        }
        int start=PAGES[page][0],end=PAGES[page][1];
        int lineY=y+54;
        for(int i=start;i<end;i++) {
            String id=NutritionProfilesV35.NUTRIENTS[i];
            Component label=Component.translatable("nutrition.gugugaga."+id);
            int value=Math.max(0,Math.min(100,values[i]));
            graphics.drawString(font,label,x+15,lineY,0xFFE6EEE1,false);
            int left=x+230;
            graphics.fill(left,lineY,left+100,lineY+9,0xFF43514B);
            graphics.fill(left,lineY,left+value,lineY+9,0xFF93AD89);
            graphics.drawString(font,Integer.toString(value),left+104,lineY,0xFFF1E8D8,false);
            lineY+=Math.max(18,Math.min(23,(bottom-y-108)/Math.max(1,end-start)));
        }
        graphics.drawCenteredString(font,Component.translatable(
                "message.gugugaga.v35_gui_note"),width/2,bottom-43,0xFFB8B8A9);
        graphics.drawCenteredString(font,Component.literal((page+1)+" / "+PAGES.length),
                width/2,bottom-30,0xFFDDD7BF);
        super.render(graphics,mouseX,mouseY,partialTick);
    }
    private void drawVital(GuiGraphics g,int x,int y,String id,int value) {
        g.drawString(font,Component.translatable("nutrition.gugugaga."+id),x,y,0xFFE6EEE1,false);
        g.fill(x,y+14,x+200,y+26,0xFF43514B);
        g.fill(x,y+14,x+value*2,y+26,0xFF83B8D0);
        g.drawString(font,value+" / 100",x+219,y+16,0xFFEEE5D6,false);
    }
    @Override
    public boolean isPauseScreen() {return false;}
}
