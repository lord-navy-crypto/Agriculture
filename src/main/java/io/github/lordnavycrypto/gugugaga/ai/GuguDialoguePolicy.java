package io.github.lordnavycrypto.gugugaga.ai;

import java.util.Locale;

/**
 * Local offline conversational character. Input is a private, explicit
 * player command only; there is NO listener for general chat messages.
 *
 * Neither the deterministic mode nor optional local AI can issue commands,
 * load chunks, alter files, imitate system warnings, or change world rules.
 */
public final class GuguDialoguePolicy {
    public static final int MAX_INPUT = 160;
    public static final int MAX_OUTPUT = 220;
    public static final int COOLDOWN_TICKS = 20*8;
    private GuguDialoguePolicy() {}

    public static String cleanInput(String text) {
        if(text==null)return "";
        StringBuilder b=new StringBuilder(Math.min(MAX_INPUT,text.length()));
        for(int i=0;i<text.length() && b.length()<MAX_INPUT;i++) {
            char c=text.charAt(i);
            if(c=='\u00a7') { if(i+1<text.length())i++; continue; }
            if(Character.isISOControl(c))continue;
            b.append(c);
        }
        return b.toString().trim();
    }

    public static String safeAnswer(String text) {
        if(text==null)return "";
        String sanitized=cleanOutput(text,MAX_OUTPUT);
        // Make sure the answer is fiction and not formatted as a command.
        if(sanitized.startsWith("/")||sanitized.startsWith("!"))
            sanitized="Gugu: "+sanitized;
        return sanitized;
    }

    private static String cleanOutput(String text,int limit) {
        StringBuilder b=new StringBuilder(Math.min(text.length(),limit));
        for(int i=0;i<text.length() && b.length()<limit;i++){
            char c=text.charAt(i);
            if(c=='\u00a7') { if(i+1<text.length())i++; continue; }
            if(Character.isISOControl(c))continue;
            b.append(c);
        }
        return b.toString().trim();
    }

    public static String reply(String message,int clueCount,
            boolean coldBiome,long playerSeed) {
        String value=cleanInput(message).toLowerCase(Locale.ROOT);
        if(value.isBlank())
            return "Gugu... Gaga? Ask me something.";
        if(contains(value,"hello","hi","hey","你好","嗨","您好","咕咕","gugu")) {
            return coldBiome
                    ? "Gugu... Gaga. The snow makes every footprint easier to remember."
                    : "Gugu... Gaga. A stranger this far from the snow?";
        }
        if(contains(value,"name","who are you","是谁","什么名字","你是谁","名字"))
            return "They call me Gugu-Gaga. But I don't remember who started it.";
        if(contains(value,"snow","cold","ice","winter","雪","冰","冷","冬"))
            return "I know the northern wind. It has been whispering your footsteps.";
        if(contains(value,"hot","desert","sand","warm","热","沙漠","太阳"))
            return "Too warm... I rarely visit places like this. Yet here I am.";
        if(contains(value,"scared","afraid","fear","害怕","恐怖","吓"))
            return "Then we can be quiet for a while. The silence is part of the story.";
        if(contains(value,"journal","clue","evidence","线索","档案","日记","调查"))
            return clueCount>=3
                    ? "You found three things. Did you notice what all of them have in common?"
                    : "Watch when I look away. Your journal may have an answer.";
        if(contains(value,"why","where","什么","为什么","哪里","在哪","怎么"))
            return coldBiome
                    ? "Some questions should be asked after the snow settles."
                    : "Far from the cold, the echoes sound different.";
        int variant=(int)Math.floorMod(playerSeed^value.hashCode(),4L);
        return switch(variant) {
            case 0 -> "Gugu... Gaga. I heard that question before. Somewhere.";
            case 1 -> "You can talk to me. That doesn't mean I know every answer.";
            case 2 -> "The world is very quiet when nobody is looking.";
            default -> "Gugu-gaga... I wonder what you will notice next.";
        };
    }

    private static boolean contains(String input,String... words) {
        for(String word:words)if(input.contains(word))return true;
        return false;
    }
}
