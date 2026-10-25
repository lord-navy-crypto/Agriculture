package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.microbes.NaturalMicrobeCatalog;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Native, read-only catalogue of actual NCBI-identified bacteria/phage.
 *
 * Habitat means DOCUMENTED ASSOCIATION, not an assertion of occurrence in
 * the current Minecraft biome; no breeding, infection, or world generation.
 */
public final class NaturalMicrobeScreen extends Screen {
    private final Screen parent;
    private final List<NaturalMicrobeCatalog.Organism> entries;
    private int index;

    public NaturalMicrobeScreen(Screen parent) {
        super(Component.translatable("screen.gugugaga.microbes.title"));
        this.parent=parent;
        this.entries=NaturalMicrobeCatalog.all().values().stream()
                .sorted(Comparator.comparing(
                        NaturalMicrobeCatalog.Organism::scientificName))
                .toList();
    }

    @Override protected void init() {
        int cell=Math.max(65,Math.min(150,(width-40)/3));
        int start=(width-3*cell)/2;
        int y=height-30;
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.onehealth.previous"),
                button->index=Math.floorMod(index-1,entries.size()))
                .bounds(start,y,cell-3,20).build());
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.onehealth.next"),
                button->index=(index+1)%entries.size())
                .bounds(start+cell,y,cell-3,20).build());
        addRenderableWidget(Button.builder(
                Component.translatable("screen.gugugaga.onehealth.back"),
                button->Minecraft.getInstance().setScreen(parent))
                .bounds(start+cell*2,y,cell-3,20).build());
    }

    @Override public void render(GuiGraphics graphics,int mouseX,
                                 int mouseY,float partialTick) {
        graphics.fill(0,0,width,height,0xDD12222A);
        super.render(graphics,mouseX,mouseY,partialTick);
        int x=Math.max(14,(width-Math.min(540,width-28))/2);
        int textWidth=Math.max(160,Math.min(540,width-28));
        var organism=entries.get(index);
        graphics.drawCenteredString(font,title,width/2,11,0xFFE6E9D0);
        graphics.drawCenteredString(font,Component.literal(
                organism.scientificName()),width/2,28,0xFFD2E8B1);
        graphics.drawCenteredString(font,Component.literal(
                "NCBI: "+organism.ncbiTaxonId()+"     "+
                (index+1)+"/"+entries.size()),
                width/2,43,0xFFB9C7C8);
        graphics.drawWordWrap(font,Component.translatable(
                "screen.gugugaga.microbes.kind",
                organism.kind().name()),x,62,textWidth,0xFFE1E4DC);
        graphics.drawWordWrap(font,Component.translatable(
                "screen.gugugaga.microbes.habitat",
                organism.documentedHabitats().toString()),
                x,92,textWidth,0xFFC1D6C5);
        if(!organism.documentedHosts().isEmpty())
            graphics.drawWordWrap(font,Component.translatable(
                    "screen.gugugaga.microbes.host",
                    organism.documentedHosts().toString()),
                    x,125,textWidth,0xFFD9CFA3);
        graphics.drawWordWrap(font,Component.translatable(
                "screen.gugugaga.microbes.not_observed"),
                x,Math.max(151,height-85),textWidth,0xFFFFCA89);
        graphics.drawWordWrap(font,Component.translatable(
                "screen.gugugaga.microbes.source",
                Integer.toString(organism.ncbiTaxonId())),
                x,Math.max(179,height-57),textWidth,0xFFAEC7D2);
    }

    @Override public boolean isPauseScreen(){return false;}
    @Override public void onClose(){Minecraft.getInstance().setScreen(parent);}
}
