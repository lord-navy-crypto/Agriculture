package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.microbes.OneHealthEvidenceCatalog;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthHostEvidenceCatalog;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthHostEvidenceCatalog.Relation;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Read-only host-virus-evidence relationship viewer, NOT a spread network. */
public final class OneHealthHostScreen extends Screen {
    private final Screen parent;
    private int profileIndex;
    private int relationIndex;
    private boolean sourcePage;
    private static final String[] PROFILES={
        "lyssavirus_rabies","avian_influenza_a_interface"
    };
    public OneHealthHostScreen(Screen parent, String initialProfile) {
        super(Component.translatable("screen.gugugaga.onehealth.host.title"));
        this.parent=parent;
        profileIndex=PROFILES[1].equals(initialProfile)?1:0;
    }
    private List<Relation> relations() {
        return OneHealthHostEvidenceCatalog.byProfile(PROFILES[profileIndex]);
    }
    private void move(int n) {
        int size=relations().size();
        if(size>0)relationIndex=Math.floorMod(relationIndex+n,size);
    }
    private void button(String key,int x,int y,int w,Runnable task) {
        addRenderableWidget(Button.builder(
            Component.translatable("screen.gugugaga.onehealth.host."+key),
            b->task.run()).bounds(x,y,w-2,20).build());
    }
    @Override protected void init() {
        int w=Math.max(42,Math.min(98,(width-24)/6));
        int x=(width-w*6)/2,y=height-27;
        button("previous",x,y,w,()->move(-1));
        button("next",x+w,y,w,()->move(1));
        button("virus",x+w*2,y,w,()->{profileIndex=(profileIndex+1)%2;relationIndex=0;});
        button("source",x+w*3,y,w,()->sourcePage=!sourcePage);
        button("timeline",x+w*4,y,w,()->Minecraft.getInstance().setScreen(
            new OneHealthHistoryScreen(this,PROFILES[profileIndex])));
        button("back",x+w*5,y,w,()->Minecraft.getInstance().setScreen(parent));
    }
    private static Component tr(String s) {
        return Component.translatable("screen.gugugaga.onehealth.host."+s);
    }
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float delta) {
        g.fill(0,0,width,height,0xE014212B);
        super.render(g,mouseX,mouseY,delta);
        g.drawCenteredString(font,title,width/2,12,0xFFE1F0EB);
        List<Relation> entries=relations();
        if(entries.isEmpty())return;
        Relation relation=entries.get(Math.floorMod(relationIndex,entries.size()));
        var profile=OneHealthEvidenceCatalog.byId(relation.profileId());
        boolean zh=Minecraft.getInstance().getLanguageManager()
            .getSelected().startsWith("zh");
        int x=Math.max(12,(width-Math.min(490,width-24))/2);
        int wrap=Math.max(100,Math.min(490,width-24));
        g.drawCenteredString(font,Component.literal((relationIndex+1)+"/"+
            entries.size()),width/2,29,0xFFA8C5D1);
        g.drawCenteredString(font,tr("hostlabel"),width/2,50,0xFFADDAD5);
        g.drawWordWrap(font,Component.literal(relation.host(zh)),
            x,65,wrap,0xFFFFDC9F);
        g.fill(width/2-1,93,width/2+1,104,0xFF7EA9AD);
        g.drawCenteredString(font,tr("association"),width/2,106,0xFF8DBFC7);
        g.drawCenteredString(font,Component.literal(profile.scientificName()),
            width/2,124,0xFFDFE9F1);
        g.fill(width/2-1,139,width/2+1,152,0xFF7EA9AD);
        if(sourcePage) {
            g.drawWordWrap(font,Component.literal(relation.sourceName()),
                x,159,wrap,0xFFE7DFB4);
            g.drawWordWrap(font,Component.literal(relation.sourceUrl()),
                x,185,wrap,0xFFB0D7E4);
        } else {
            g.drawWordWrap(font,Component.literal(relation.scope(zh)),
                x,157,wrap,0xFFE7DFB4);
            g.drawWordWrap(font,Component.literal(relation.caveat(zh)),
                x,Math.max(192,height-68),wrap,0xFFFFC88C);
        }
        g.drawCenteredString(font,tr("notspread"),width/2,height-43,0xFFEBB780);
    }
    @Override public boolean isPauseScreen(){return false;}
    @Override public void onClose(){Minecraft.getInstance().setScreen(parent);}
}
