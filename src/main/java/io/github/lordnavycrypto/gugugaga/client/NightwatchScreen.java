package io.github.lordnavycrypto.gugugaga.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Unified in-game control surface; normal play never requires typed commands. */
public final class NightwatchScreen extends Screen {
    public NightwatchScreen() {
        super(Component.translatable("screen.gugugaga.nightwatch.title"));
    }

    @Override
    protected void init() {
        int buttonWidth=Math.min(190,(width-32)/2);
        int gap=8;
        int left=(width-(buttonWidth*2+gap))/2;
        int right=left+buttonWidth+gap;
        int row=height/2-75;
        int step=25;
        addCommandButton("screen.gugugaga.nightwatch.join","gugugaga play join",left,row,buttonWidth);
        addCommandButton("screen.gugugaga.nightwatch.leave","gugugaga play leave",right,row,buttonWidth);
        addCommandButton("screen.gugugaga.nightwatch.listen","gugugaga play listen-panel",left,row+step,buttonWidth);
        addCommandButton("screen.gugugaga.nightwatch.help","gugugaga play help",right,row+step,buttonWidth);
        addCommandButton("screen.gugugaga.nightwatch.aid_on","gugugaga play aid on",left,row+step*2,buttonWidth);
        addCommandButton("screen.gugugaga.nightwatch.aid_off","gugugaga play aid off",right,row+step*2,buttonWidth);
        addCommandButton("screen.gugugaga.nightwatch.scare_on","gugugaga play scares on",left,row+step*3,buttonWidth);
        addCommandButton("screen.gugugaga.nightwatch.scare_off","gugugaga play scares off",right,row+step*3,buttonWidth);
        addCommandButton("screen.gugugaga.nightwatch.journal","gugugaga journal",left,row+step*4,buttonWidth);
        addCommandButton("screen.gugugaga.nightwatch.quiet_on","gugugaga quiet on",right,row+step*4,buttonWidth);
        addCommandButton("screen.gugugaga.nightwatch.quiet_off","gugugaga quiet off",left,row+step*5,buttonWidth);
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.nightwatch.close"),
                button->Minecraft.getInstance().setScreen(null))
                .bounds(right,row+step*5,buttonWidth,20).build());
    }

    private void addCommandButton(String label,String command,int x,int y,int w) {
        addRenderableWidget(Button.builder(Component.translatable(label),
                button->runCommand(command)).bounds(x,y,w,20).build());
    }

    private static void runCommand(String command) {
        Minecraft mc=Minecraft.getInstance();
        if(mc.getConnection()!=null && mc.player!=null)
            mc.getConnection().sendCommand(command);
        mc.setScreen(null);
    }

    @Override
    public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick) {
        graphics.fill(0,0,width,height,0xC010141C);
        super.render(graphics,mouseX,mouseY,partialTick);
        drawCenteredString(graphics,font,title,width/2,height/2-132,0xFFF0F1F5);
        drawCenteredString(graphics,font,
                Component.translatable("screen.gugugaga.nightwatch.subtitle"),
                width/2,height/2-108,0xFFD4D9E2);
        drawCenteredString(graphics,font,
                Component.translatable("screen.gugugaga.nightwatch.footer"),
                width/2,height/2+86,0xFFB9C0CC);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }
}
