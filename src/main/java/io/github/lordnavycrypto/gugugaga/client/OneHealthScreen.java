package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.microbes.OneHealthEvidenceCatalog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Native, read-only H-menu science atlas of real animal/human health facts.
 * No client packet, random infection, spawn loop or server health state.
 */
public final class OneHealthScreen extends Screen {
    private final Screen parent;
    private int recordIndex;
    private int sectionIndex;

    public OneHealthScreen(Screen parent) {
        super(Component.translatable("screen.gugugaga.onehealth.title"));
        this.parent=parent;
    }

    @Override
    protected void init() {
        int y=height-32;
        int cell=Math.max(54,Math.min(100,(width-32)/5));
        int x=(width-cell*5)/2;
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.onehealth.previous"),
                b->recordIndex=Math.floorMod(recordIndex-1,
                        OneHealthEvidenceCatalog.all().size()))
                .bounds(x,y,cell-2,20).build());
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.onehealth.next"),
                b->recordIndex=(recordIndex+1)%
                        OneHealthEvidenceCatalog.all().size())
                .bounds(x+cell,y,cell-2,20).build());
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.onehealth.details"),
                b->sectionIndex=(sectionIndex+1)%3)
                .bounds(x+cell*2,y,cell-2,20).build());
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.onehealth.history"),
                b->Minecraft.getInstance().setScreen(new OneHealthHistoryScreen(
                        this,OneHealthEvidenceCatalog.all().get(recordIndex).id())))
                .bounds(x+cell*3,y,cell-2,20).build());
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.onehealth.back"),
                b->Minecraft.getInstance().setScreen(parent))
                .bounds(x+cell*4,y,cell-2,20).build());
    }

    @Override
    public void render(GuiGraphics g,int mouseX,int mouseY,float partialTick) {
        g.fill(0,0,width,height,0xD0101E28);
        super.render(g,mouseX,mouseY,partialTick);
        int textX=Math.max(12,(width-Math.min(570,width-24))/2);
        int wrap=Math.max(180,Math.min(570,width-24));
        var profile=OneHealthEvidenceCatalog.all().get(recordIndex);
        g.drawCenteredString(font,title,width/2,12,0xFFDAEDEE);
        g.drawCenteredString(font,Component.literal(profile.scientificName()),
                width/2,30,0xFFE9D89A);
        g.drawCenteredString(font,Component.literal(
                "NCBI taxonomy: "+profile.ncbiTaxonId()
                +"   ("+(recordIndex+1)+"/"+OneHealthEvidenceCatalog.all().size()+")"),
                width/2,44,0xFFB5C7D5);
        if(sectionIndex==0) {
            g.drawWordWrap(font,Component.literal("Evidence scope: "+profile.scope()),
                    textX,62,wrap,0xFFD6DCE4);
            g.drawWordWrap(font,Component.literal(
                    "Documented host categories: "+profile.documentedContexts()),
                    textX,112,wrap,0xFFD6DCE4);
            g.drawWordWrap(font,Component.translatable(
                    "screen.gugugaga.onehealth.noautomatic"),
                    textX,Math.max(155,height-72),wrap,0xFFFFC37D);
        } else if(sectionIndex==1) {
            g.drawWordWrap(font,Component.literal(
                    "Human health: "+profile.humanImpact()),
                    textX,61,wrap,0xFFE3E5E8);
            g.drawWordWrap(font,Component.literal(
                    "Animal health: "+profile.animalImpact()),
                    textX,107,wrap,0xFFE3E5E8);
            g.drawWordWrap(font,Component.literal(
                    "Documented interface types: "+
                    profile.documentedInterfaceCategories()),
                    textX,153,wrap,0xFFCFDEDD);
        } else {
            g.drawWordWrap(font,Component.literal(profile.uncertainty()),
                    textX,62,wrap,0xFFEAD9AA);
            g.drawWordWrap(font,Component.literal(
                    "Taxonomy: NCBI "+profile.ncbiTaxonId()),
                    textX,130,wrap,0xFFE3E5E8);
            g.drawWordWrap(font,Component.literal(
                    "Public health reference: "+
                    (recordIndex==0?"CDC Rabies":"WHO Animal Influenza")),
                    textX,148,wrap,0xFFE3E5E8);
            g.drawWordWrap(font,Component.translatable(
                    "screen.gugugaga.onehealth.sources"),
                    textX,168,wrap,0xFFB3CBD4);
        }
    }

    @Override public boolean isPauseScreen(){return false;}
    @Override public void onClose(){Minecraft.getInstance().setScreen(parent);}
}
