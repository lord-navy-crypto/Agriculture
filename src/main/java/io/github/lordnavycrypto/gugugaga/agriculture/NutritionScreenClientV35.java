package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/**
 * Read-only, server-authored nutrition dashboard. Always render at the
 * Minecraft GUI's native text scale: shrinking the font blurs/loses glyphs.
 *
 * The row viewport scrolls instead of piling widgets, labels and bars on top
 * of each other at large GUI scales or short window heights.
 * The values are fictional 0..100 game scores, never clinical nutrition data.
 */
public final class NutritionScreenClientV35 extends Screen {
    private static final int[][] PAGES={{0,5},{5,12},{12,18},{18,23},{23,27},{0,0},{0,0},{0,27}};
    private static final int ROW_HEIGHT=23;
    private final int[] values;
    private final int water;
    private final int salt;
    private final int[] feedback;
    private int page=5;
    private int scroll=0;
    private Button scrollUp;
    private Button scrollDown;

    private NutritionScreenClientV35(int[] snapshot,int water,int salt,int[] feedback) {
        super(Component.translatable("message.gugugaga.v35_gui_title"));
        this.values=Arrays.copyOf(snapshot,27);
        this.water=clamp(water);
        this.salt=clamp(salt);
        this.feedback=Arrays.copyOf(feedback,NutritionFeedbackV38.SNAPSHOT_SIZE);
    }
    private static int clamp(int value) {return Math.max(0,Math.min(100,value));}

    public static void open(int[] scores,int water,int salt,int[] feedback) {
        if(scores==null || scores.length!=27 || feedback==null
                || feedback.length!=NutritionFeedbackV38.SNAPSHOT_SIZE)return;
        Minecraft.getInstance().setScreen(new NutritionScreenClientV35(
                scores,water,salt,feedback));
    }

    private int panelWidth() {return Math.max(120,Math.min(440,width-12));}
    private int panelHeight() {return Math.max(105,Math.min(305,height-12));}
    private int panelX() {return (width-panelWidth())/2;}
    private int panelY() {return (height-panelHeight())/2;}
    private int contentTop() {return panelY()+47;}
    private int contentBottom() {return panelY()+panelHeight()-45;}
    private int visibleRows() {
        return Math.max(1,(contentBottom()-contentTop()-3)/ROW_HEIGHT);
    }
    private int rowCount() {
        return page==7?27:page==6?NutritionFeedbackV38.GROUP_COUNT:page==5?3:PAGES[page][1]-PAGES[page][0];
    }
    private int maxScroll() {return Math.max(0,rowCount()-visibleRows());}
    private void setScroll(int target) {
        scroll=Math.max(0,Math.min(maxScroll(),target));
        syncScrollButtons();
    }
    private void syncScrollButtons() {
        if(scrollUp!=null)scrollUp.active=scroll>0;
        if(scrollDown!=null)scrollDown.active=scroll<maxScroll();
    }
    private void setPage(int newPage) {
        page=Math.floorMod(newPage,PAGES.length);
        setScroll(0);
    }

    @Override
    protected void init() {
        int x=panelX(), y=panelY(), w=panelWidth(), h=panelHeight();
        boolean compact=w<280;
        int side=compact?28:40, scrollW=compact?24:30, doneW=compact?54:80;
        int gap=compact?6:9;
        int total=2*side+2*scrollW+doneW+4*gap;
        int pos=x+(w-total)/2;
        addRenderableWidget(Button.builder(Component.literal("<"),
                b -> setPage(page-1)).bounds(pos,y+h-25,side,19).build());
        pos+=side+gap;
        scrollUp=addRenderableWidget(Button.builder(Component.literal("^"),
                b -> setScroll(scroll-1)).bounds(pos,y+h-25,scrollW,19).build());
        pos+=scrollW+gap;
        addRenderableWidget(Button.builder(Component.translatable("gui.done"),
                b -> onClose()).bounds(pos,y+h-25,doneW,19).build());
        pos+=doneW+gap;
        scrollDown=addRenderableWidget(Button.builder(Component.literal("v"),
                b -> setScroll(scroll+1)).bounds(pos,y+h-25,scrollW,19).build());
        pos+=scrollW+gap;
        addRenderableWidget(Button.builder(Component.literal(">"),
                b -> setPage(page+1)).bounds(pos,y+h-25,side,19).build());
        setScroll(scroll);
    }

    private Component rowLabel(int row) {
        if(page==6)return Component.translatable("feedback.gugugaga.group_"+row);
        if(page==7)return Component.translatable("nutrition.gugugaga."+NutritionProfilesV35.NUTRIENTS[row]);
        if(page==5) {
            if(row==0)return Component.translatable("nutrition.gugugaga.water_level");
            if(row==1)return Component.translatable("nutrition.gugugaga.sodium_balance");
            return status();
        }
        int index=PAGES[page][0]+row;
        return Component.translatable("nutrition.gugugaga."+NutritionProfilesV35.NUTRIENTS[index]);
    }
    private Component status() {
        return Component.translatable(water<=15
                ?"message.gugugaga.v37_thirst_low":salt<=8
                ?"message.gugugaga.v37_salt_low":salt>85
                ?"message.gugugaga.v37_salt_high":"message.gugugaga.v37_balanced");
    }
    private int rowValue(int row) {
        if(page==6)return clamp(feedback[row]);
        if(page==7)return clamp(values[row]);
        if(page==5)return row==0?water:salt;
        return clamp(values[PAGES[page][0]+row]);
    }
    private Component fitted(Component label,int maxWidth) {
        if(font.width(label)<=maxWidth)return label;
        String dots="...";
        return Component.literal(font.plainSubstrByWidth(label.getString(),
                Math.max(0,maxWidth-font.width(dots)))+dots);
    }
    private void centered(GuiGraphics graphics,Component label,int center,int y,int maxWidth,int color) {
        graphics.drawCenteredString(font,fitted(label,maxWidth),center,y,color);
    }

    @Override
    public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick) {
        // Opaque panel over a flat dimmed scene. Do not invoke the 1.21.1
        // menu-background blur in addition to the text-bearing foreground.
        graphics.fill(0,0,width,height,0xBD11191A);
        int x=panelX(),y=panelY(),w=panelWidth(),h=panelHeight();
        graphics.fill(x,y,x+w,y+h,0xF5273531);
        graphics.fill(x,y,x+w,y+2,0xFF97C9A3);
        centered(graphics,title,x+w/2,y+8,w-20,0xFFFFFFFF);
        Component category=Component.translatable("message.gugugaga.v35_page_"+page);
        Component heading=Component.literal((page+1)+" / "+PAGES.length+"  ").append(category);
        centered(graphics,heading,x+w/2,y+27,w-28,0xFFFFFFD2);

        // Keep all text at 1x; clip overlong text and expose it in tooltips.
        // Rows never draw outside the independently sized content viewport.
        scroll=Math.min(scroll,maxScroll());
        int top=contentTop(), bottom=contentBottom();
        int limit=Math.min(rowCount(),scroll+visibleRows());
        Component hover=null;
        for(int row=scroll;row<limit;row++) {
            int line=top+3+(row-scroll)*ROW_HEIGHT;
            if(line+ROW_HEIGHT>bottom+3)break;
            boolean statusRow=page==5 && row==2;
            graphics.fill(x+9,line-2,x+w-9,line+ROW_HEIGHT-3,
                    (row%2==0)?0xFF334640:0xFF2A3A35);
            Component label=rowLabel(row);
            int labelX=x+16;
            if(statusRow) {
                Component visible=fitted(label,w-32);
                graphics.drawString(font,visible,labelX,line+5,0xFFFFDC9D,false);
                if(mouseX>=x+9 && mouseX<x+w-9 && mouseY>=line && mouseY<line+ROW_HEIGHT)
                    hover=label;
            } else {
                int value=rowValue(row);
                String numeric=value+" / 100";
                int numericWidth=font.width(numeric);
                int valueRight=x+w-16;
                int barRight=valueRight-numericWidth-11;
                int barWidth=Math.max(23,Math.min(110,(w-45)/3));
                int barLeft=barRight-barWidth;
                int nutrientIndex=page<5?PAGES[page][0]+row:page==7?row:-1;
                int state=page==6?feedback[NutritionFeedbackV38.GROUP_COUNT+row]:
                        nutrientIndex>=0?feedback[NutritionFeedbackV38.NUTRIENT_STATES_OFFSET+nutrientIndex]:0;
                int textColor=state==-2?0xFFFFB5A9
                        :state==-1?0xFFFFDEA6:0xFFFFFFFF;
                int labelWidth=Math.max(18,barLeft-labelX-(page==6?24:9));
                graphics.drawString(font,fitted(label,labelWidth),labelX,line+5,
                        textColor,false);
                if(page==6 || page==7) {
                    String indicator=state==-2?"!!":state==-1?"!":state==1?"+":"=";
                    graphics.drawString(font,indicator,barLeft-18,line+5,textColor,false);
                }
                graphics.fill(barLeft,line+7,barRight,line+15,0xFF182923);
                int fillWidth=barWidth*value/100;
                if(fillWidth>0)graphics.fill(barLeft,line+7,barLeft+fillWidth,line+15,
                        page==5?0xFF80C6E9:textColor==0xFFFFFFFF?0xFF9ED89A:textColor);
                graphics.drawString(font,numeric,valueRight-numericWidth,line+5,
                        0xFFFFF2D8,false);
                if(mouseX>=x+9 && mouseX<x+w-9 && mouseY>=line && mouseY<line+ROW_HEIGHT) {
                    if(page==6) {
                        int stage=feedback[NutritionFeedbackV38.GROUP_COUNT+row];
                        int days=feedback[NutritionFeedbackV38.GROUP_COUNT*2+row];
                        hover=Component.translatable("feedback.gugugaga.tooltip",
                                label,numeric,Component.translatable(
                                        "feedback.gugugaga.stage_"+(stage+2)),days);
                    } else if(nutrientIndex>=0){
                        int days=feedback[NutritionFeedbackV38.NUTRIENT_DAYS_OFFSET+nutrientIndex];
                        String id=NutritionProfilesV35.NUTRIENTS[nutrientIndex];
                        Component pair=Component.translatable("feedback.gugugaga.effect_pair",
                                Component.translatable("effect.minecraft."+NutritionEffectsV310.positiveKey(nutrientIndex)),
                                Component.translatable("effect.minecraft."+NutritionEffectsV310.negativeKey(nutrientIndex)));
                        hover=Component.translatable("feedback.gugugaga.nutrient_tooltip",
                                label,numeric,
                                Component.translatable("feedback.gugugaga.role."+id),
                                Component.translatable("feedback.gugugaga.stage_"+(state+2)),
                                days,NutritionPhysiologyV39.requiredDays(nutrientIndex),pair);
                    } else if(page==5 && row<2){
                        int effectIndex=row==0?NutritionPhysiologyV39.WATER_INDEX:NutritionPhysiologyV39.SODIUM_INDEX;
                        Component pair=Component.translatable("feedback.gugugaga.effect_pair",
                                Component.translatable("effect.minecraft."+NutritionEffectsV310.positiveKey(effectIndex)),
                                Component.translatable("effect.minecraft."+NutritionEffectsV310.negativeKey(effectIndex)));
                        hover=Component.translatable("feedback.gugugaga.water_salt_tooltip",label,numeric,pair);
                    } else hover=Component.literal(label.getString()+" : "+numeric);
                }
            }
        }
        Component note=Component.translatable(page==7
                ?"feedback.gugugaga.effects_note":page==6
                ?"feedback.gugugaga.note":"message.gugugaga.v35_gui_note");
        centered(graphics,note,x+w/2,y+h-39,w-20,0xFFE2E6D7);
        syncScrollButtons();
        super.render(graphics,mouseX,mouseY,partialTick);
        if(hover!=null)graphics.renderTooltip(font,hover,mouseX,mouseY);
    }
    @Override
    public boolean mouseScrolled(double mouseX,double mouseY,double scrollX,double scrollY) {
        if(scrollY!=0 && maxScroll()>0) {
            setScroll(scroll+(scrollY>0?-1:1));
            return true;
        }
        return super.mouseScrolled(mouseX,mouseY,scrollX,scrollY);
    }
    @Override
    public boolean keyPressed(int keyCode,int scanCode,int modifiers) {
        if(keyCode==GLFW.GLFW_KEY_DOWN || keyCode==GLFW.GLFW_KEY_PAGE_DOWN) {
            setScroll(scroll+1);
            return true;
        }
        if(keyCode==GLFW.GLFW_KEY_UP || keyCode==GLFW.GLFW_KEY_PAGE_UP) {
            setScroll(scroll-1);
            return true;
        }
        if(keyCode==GLFW.GLFW_KEY_RIGHT) {setPage(page+1);return true;}
        if(keyCode==GLFW.GLFW_KEY_LEFT) {setPage(page-1);return true;}
        return super.keyPressed(keyCode,scanCode,modifiers);
    }
    @Override
    public boolean isPauseScreen() {return false;}
}
