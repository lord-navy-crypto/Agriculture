import io.github.lordnavycrypto.gugugaga.ai.GuguDialoguePolicy;
public final class GuguDialoguePolicySmoke {
    private static void yes(boolean b,String msg){if(!b)throw new AssertionError(msg);}
    public static void main(String[] args){
        yes(GuguDialoguePolicy.cleanInput(null).isEmpty(),"Null input");
        yes(GuguDialoguePolicy.cleanInput(" \u00a7a Hello\n World ").equals("Hello World"),
                "Remove chat formatting and controls");
        yes(GuguDialoguePolicy.cleanInput("x".repeat(500)).length()==160,
                "Question length capped");
        yes(GuguDialoguePolicy.safeAnswer("y".repeat(1000)).length()<=220,
                "AI output bounded");
        yes(!GuguDialoguePolicy.safeAnswer("\u00a7c!!\nhello").contains("\u00a7"),
                "No injected formatting/control characters");
        yes(GuguDialoguePolicy.reply("你好",0,true,4).contains("snow"),
                "Gugu speaks appropriately in cold worlds");
        yes(GuguDialoguePolicy.reply("hi",0,false,4).contains("snow"),
                "Gugu contrasts hot and cold worlds");
        yes(GuguDialoguePolicy.reply("Who are you?",0,true,5)
                .contains("Gugu-Gaga"),"Original fictional identity");
        yes(GuguDialoguePolicy.reply("我的线索",3,true,5)
                .contains("three"),"Journal evidence-aware reply");
        yes(GuguDialoguePolicy.reply("沙漠",0,false,5)
                .contains("warm"),"Desert penguin dialogue");
        yes(GuguDialoguePolicy.reply("anything else?",0,false,7).equals(
                GuguDialoguePolicy.reply("anything else?",0,false,7)),
                "Deterministic offline dialogue replay");
        for(int i=0;i<100000;i++){
            String input=(i%2==0?"雪 你是谁？":"What is here")+"-"+i;
            String answer=GuguDialoguePolicy.reply(input,i%4,i%3==0,i);
            yes(!answer.isBlank()&&answer.length()<=220,"Dialogue response safe");
        }
        System.out.println("GUGUGAGA_DIALOGUE_OFFLINE_PASS 100000 interactions");
    }
}
