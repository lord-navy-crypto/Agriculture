package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.microbes.OneHealthHistoryCatalog;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthHistoryCatalog.Kind;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthHistoryCatalog.Event;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Native read-only historical chronology; no player/world infection effects. */
public final class OneHealthHistoryScreen extends Screen {
    private final Screen parent;
    private static final String[] PROFILES = {
        null, "lyssavirus_rabies", "avian_influenza_a_interface"
    };
    private static final Kind[] KINDS = {
        null, Kind.ANIMAL_REPORT, Kind.HUMAN_REPORT, Kind.PUBLIC_HEALTH
    };
    private int profileIndex, kindIndex, eventIndex;
    private boolean sourcePage;

    public OneHealthHistoryScreen(Screen parent, String initialProfile) {
        super(Component.translatable("screen.gugugaga.onehealth.history.title"));
        this.parent = parent;
        if (PROFILES[1].equals(initialProfile)) profileIndex = 1;
        if (PROFILES[2].equals(initialProfile)) profileIndex = 2;
    }
    private List<Event> records() {
        return OneHealthHistoryCatalog.filter(PROFILES[profileIndex], KINDS[kindIndex]);
    }
    private void move(int delta) {
        int count = records().size();
        if (count > 0) eventIndex = Math.floorMod(eventIndex + delta, count);
    }
    private void button(String key, int x, int y, int w, Runnable action) {
        addRenderableWidget(Button.builder(
            Component.translatable("screen.gugugaga.onehealth.history." + key),
            b -> action.run()).bounds(x, y, w - 2, 20).build());
    }
    @Override protected void init() {
        int w = Math.max(42, Math.min(98, (width - 24) / 6));
        int x = (width - w * 6) / 2, y = height - 27;
        button("previous", x, y, w, () -> move(-1));
        button("next", x+w, y, w, () -> move(1));
        button("virus", x+w*2, y, w, () -> {
            profileIndex=(profileIndex+1)%PROFILES.length; eventIndex=0;
        });
        button("category", x+w*3, y, w, () -> {
            kindIndex=(kindIndex+1)%KINDS.length; eventIndex=0;
        });
        button("source", x+w*4, y, w, () -> sourcePage=!sourcePage);
        button("back", x+w*5, y, w, () -> Minecraft.getInstance().setScreen(parent));
    }
    private static Component tr(String key) {
        return Component.translatable("screen.gugugaga.onehealth.history." + key);
    }
    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float tick) {
        g.fill(0,0,width,height,0xE0122129);
        super.render(g, mouseX, mouseY, tick);
        g.drawCenteredString(font,title,width/2,12,0xFFE3ECED);
        String profile=profileIndex==0?"all":profileIndex==1?"rabies":"avian";
        String kind=switch(kindIndex) {
            case 1 -> "animal";
            case 2 -> "human";
            case 3 -> "publichealth";
            default -> "all";
        };
        g.drawCenteredString(font,
            tr("filter."+profile).copy().append(" / ").append(tr("filter."+kind)),
            width/2,30,0xFFB9CBD7);
        List<Event> entries=records();
        if(entries.isEmpty()) {
            g.drawCenteredString(font,tr("empty"),width/2,height/2,0xFFFFC987);
            return;
        }
        Event event=entries.get(Math.floorMod(eventIndex,entries.size()));
        int textX=Math.max(12,(width-Math.min(490,width-24))/2);
        int wrap=Math.max(100,Math.min(490,width-24));
        int left=textX+16,right=Math.min(width-25,textX+wrap-16);
        g.fill(left,52,right,54,0xFF526B76);
        for(int i=0;i<entries.size();i++) {
            int dot=entries.size()==1?(left+right)/2
                :left+(right-left)*i/(entries.size()-1);
            g.fill(dot-3,49,dot+4,57,
                i==eventIndex?0xFFFFD585:0xFF91BCC4);
        }
        g.drawCenteredString(font,Component.literal(event.reportDate()+"  ("+
            (eventIndex+1)+"/"+entries.size()+")"),width/2,63,0xFFB9D9D9);
        boolean zh=Minecraft.getInstance().getLanguageManager()
            .getSelected().startsWith("zh");
        g.drawWordWrap(font,Component.literal(event.title(zh)),
            textX,83,wrap,0xFFFFDEA5);
        if(sourcePage) {
            g.drawWordWrap(font,tr("sourcelabel"),textX,111,wrap,0xFFC7E1E4);
            g.drawWordWrap(font,Component.literal(event.authority()),
                textX,132,wrap,0xFFD9E5E9);
            g.drawWordWrap(font,Component.literal(event.sourceUrl()),
                textX,155,wrap,0xFFADD8E1);
            g.drawWordWrap(font,tr("sourcecaveat"),
                textX,Math.max(190,height-65),wrap,0xFFFFC38B);
        } else {
            g.drawWordWrap(font,Component.literal(event.region()+" | "+event.host(zh)),
                textX,111,wrap,0xFFC7E1E4);
            g.drawWordWrap(font,Component.literal(event.description(zh)),
                textX,143,wrap,0xFFE2EAEF);
            g.drawWordWrap(font,Component.literal(event.caveat(zh)),
                textX,Math.max(190,height-65),wrap,0xFFFFC38B);
        }
    }
    @Override public boolean isPauseScreen() { return false; }
    @Override public void onClose() { Minecraft.getInstance().setScreen(parent); }
}
