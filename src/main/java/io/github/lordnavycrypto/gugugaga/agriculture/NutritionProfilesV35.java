package io.github.lordnavycrypto.gugugaga.agriculture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Explicit, shipped food-to-game-nutrient catalog. All numbers are small game
 * contribution points (not milligrams, calories, RDAs or medical advice).
 * Missing third-party mod foods use a neutral fallback without false claims.
 */
public final class NutritionProfilesV35 {
    private NutritionProfilesV35() {}
    public static final String RESOURCE="data/gugugaga/nutrition/food_profiles_v3_5.json";
    public static final String[] NUTRIENTS={
        "carbohydrate","protein","fat","fiber","hydration",
        "vitamin_a","vitamin_b1","vitamin_b2","vitamin_b3","vitamin_b5",
        "vitamin_b6","vitamin_b7","vitamin_b9","vitamin_b12","vitamin_c",
        "vitamin_d","vitamin_e","vitamin_k",
        "calcium","iron","magnesium","potassium","zinc","iodine",
        "selenium","phosphorus","sodium"
    };
    private static final Map<String, int[]> VALUES=load();

    private static Map<String,int[]> load() {
        try (InputStream source=NutritionProfilesV35.class.getClassLoader()
                .getResourceAsStream(RESOURCE)) {
            if(source==null) throw new IllegalStateException("Missing nutrition catalog: "+RESOURCE);
            JsonObject doc=JsonParser.parseReader(
                    new InputStreamReader(source,StandardCharsets.UTF_8)).getAsJsonObject();
            if(doc.get("schema_version").getAsInt()!=1)
                throw new IllegalStateException("Unsupported nutrition catalog schema");
            JsonArray data=doc.getAsJsonArray("nutrients");
            if(data.size()!=NUTRIENTS.length)
                throw new IllegalStateException("Nutrition metric count changed unexpectedly");
            for (int i=0;i<NUTRIENTS.length;i++)
                if(!NUTRIENTS[i].equals(data.get(i).getAsJsonObject().get("id").getAsString()))
                    throw new IllegalStateException("Nutrition profile metric order differs: "+i);
            Map<String,int[]> templates=new HashMap<>();
            for (Map.Entry<String,JsonElement> entry: doc.getAsJsonObject("profiles").entrySet()) {
                int[] profile=new int[NUTRIENTS.length];
                for (Map.Entry<String,JsonElement> field:entry.getValue().getAsJsonObject().entrySet()) {
                    int n=index(field.getKey());
                    int points=field.getValue().getAsInt();
                    if(n<0 || points<0 || points>8)
                        throw new IllegalStateException("Invalid game nutrient "+field.getKey());
                    profile[n]=points;
                }
                templates.put(entry.getKey(),profile);
            }
            Map<String,int[]> result=new HashMap<>();
            for (Map.Entry<String,JsonElement> entry:doc.getAsJsonObject("foods").entrySet()) {
                int[] values=new int[NUTRIENTS.length];
                for (JsonElement group:entry.getValue().getAsJsonArray()) {
                    int[] profile=templates.get(group.getAsString());
                    if(profile==null) throw new IllegalStateException("Unknown food profile");
                    for(int i=0;i<values.length;i++)
                        values[i]=Math.min(10,values[i]+profile[i]);
                }
                result.put(entry.getKey(),values);
            }
            if(result.size()<400) throw new IllegalStateException("Incomplete food catalog");
            return Map.copyOf(result);
        } catch (Exception e) {
            throw new IllegalStateException("Could not load shipped food nutrient catalog",e);
        }
    }
    public static int index(String id) {
        for(int i=0;i<NUTRIENTS.length;i++) if(NUTRIENTS[i].equals(id))return i;
        return -1;
    }
    public static int[] contribution(String itemId) {
        int[] values=VALUES.get(itemId);
        if(values==null) {
            // Unknown edible modded foods count towards broad gameplay variety
            // without making an unsupported micronutrient claim.
            int[] neutral=new int[NUTRIENTS.length];
            neutral[0]=1;neutral[4]=1;
            return neutral;
        }
        return Arrays.copyOf(values,values.length);
    }
    public static int catalogSize() {return VALUES.size();}
}
