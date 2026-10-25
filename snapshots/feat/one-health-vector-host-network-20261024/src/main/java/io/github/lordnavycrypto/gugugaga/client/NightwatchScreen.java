package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.network.NightwatchActionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Full in-game control panel: built from native buttons and real packets.
 * Not a facade that enters slash commands into the chat connection.
 */
public final class NightwatchScreen extends Screen {
    public NightwatchScreen() {
        super(Component.translatable("screen.gugugaga.nightwatch.title"));
    }

    @Override
    protected void init() {
        int buttonWidth=Math.min(180,(width-36)/2);
        int gap=8;
        int left=(width-(buttonWidth*2+gap))/2;
        int right=left+buttonWidth+gap;
        int y=height/2-100, step=26;
        addAction("screen.gugugaga.nightwatch.join",
                NightwatchActionPacket.JOIN,left,y,buttonWidth);
        addAction("screen.gugugaga.nightwatch.leave",
                NightwatchActionPacket.LEAVE,right,y,buttonWidth);
        addAction("screen.gugugaga.nightwatch.status",
                NightwatchActionPacket.STATUS,left,y+step,buttonWidth);
        addAction("screen.gugugaga.nightwatch.quiet_on",
                NightwatchActionPacket.QUIET_ON,right,y+step,buttonWidth);
        addAction("screen.gugugaga.nightwatch.quiet_off",
                NightwatchActionPacket.QUIET_OFF,left,y+step*2,buttonWidth);
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.nightwatch.close"),
                button->onClose()).bounds(right,y+step*2,buttonWidth,20).build());
        // The server checks the operator level again: no client trust.
        addAction("screen.gugugaga.nightwatch.server_on",
                NightwatchActionPacket.SERVER_ON,left,y+step*3,buttonWidth);
        addAction("screen.gugugaga.nightwatch.server_off",
                NightwatchActionPacket.SERVER_OFF,right,y+step*3,buttonWidth);
        addAction("screen.gugugaga.nightwatch.journal",
                NightwatchActionPacket.JOURNAL,left,y+step*4,buttonWidth);
        addAction("screen.gugugaga.nightwatch.journal_on",
                NightwatchActionPacket.JOURNAL_ON,right,y+step*4,buttonWidth);
        addAction("screen.gugugaga.nightwatch.journal_off",
                NightwatchActionPacket.JOURNAL_OFF,left,y+step*5,buttonWidth);
        // Real One Health encyclopedia: a local native read-only page,
        // not a disguised slash-command or an infection event.
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.onehealth.open"),
                button->Minecraft.getInstance().setScreen(new OneHealthScreen(this)))
                .bounds(right,y+step*5,buttonWidth,20).build());
        // The original real NCBI microbes were previously backend-only.
        // This opens their evidence-first field guide, not an exposure event.
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.microbes.open"),
                button->Minecraft.getInstance().setScreen(new NaturalMicrobeScreen(this)))
                .bounds(left,y+step*6,buttonWidth,20).build());
    }

    private void addAction(String label,int action,int x,int y,int buttonWidth) {
        addRenderableWidget(Button.builder(Component.translatable(label),
                button->{
                    PacketDistributor.sendToServer(new NightwatchActionPacket(action));
                    Minecraft.getInstance().setScreen(null);
                }).bounds(x,y,buttonWidth,20).build());
    }

    @Override
    public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick) {
        graphics.fill(0,0,width,height,0xC0181D27);
        super.render(graphics,mouseX,mouseY,partialTick);
        graphics.drawCenteredString(font,title,width/2,height/2-130,0xFFF4F1E8);
        graphics.drawCenteredString(font,
                Component.translatable("screen.gugugaga.nightwatch.subtitle"),
                width/2,height/2-110,0xFFCCD1D7);
        graphics.drawCenteredString(font,
                Component.translatable("screen.gugugaga.nightwatch.hint"),
                width/2,height/2+92,0xFFF0D48D);
        graphics.drawCenteredString(font,
                Component.translatable("screen.gugugaga.nightwatch.footer"),
                width/2,height/2+110,0xFFAEB7C5);
    }

    @Override
    public boolean isPauseScreen(){return false;}

    @Override
    public void onClose(){Minecraft.getInstance().setScreen(null);}
}
