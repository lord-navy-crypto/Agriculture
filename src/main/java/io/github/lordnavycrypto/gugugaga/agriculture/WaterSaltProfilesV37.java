package io.github.lordnavycrypto.gugugaga.agriculture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;

/** Stable per-item water and sodium game points. Never claimed as ml or mg. */
public final class WaterSaltProfilesV37 {
    private WaterSaltProfilesV37(){}
    private record Contribution(int water,int sodium) {}
    private static final Map<String,Contribution> FOODS=load();
    private static Map<String,Contribution> load() {
        String name="data/gugugaga/nutrition/water_salt_profiles_v3_7.json";
        try(InputStream stream=WaterSaltProfilesV37.class.getClassLoader().getResourceAsStream(name)) {
            if(stream==null)throw new IllegalStateException("Missing "+name);
            JsonObject json=JsonParser.parseReader(new InputStreamReader(
                    stream,StandardCharsets.UTF_8)).getAsJsonObject();
            if(json.get("schema_version").getAsInt()!=1)
                throw new IllegalStateException("Unknown hydration data schema");
            Map<String,Contribution> result=new HashMap<>();
            for(Map.Entry<String,JsonElement> item:json.getAsJsonObject("foods").entrySet()) {
                JsonObject v=item.getValue().getAsJsonObject();
                int w=v.get("water").getAsInt(),s=v.get("sodium").getAsInt();
                if(w<0||w>40||s<0||s>15)
                    throw new IllegalStateException("Invalid water/sodium score "+item.getKey());
                result.put(item.getKey(),new Contribution(w,s));
            }
            if(result.size()<400)throw new IllegalStateException("Missing fluid food entries");
            return Map.copyOf(result);
        }catch(Exception e){throw new IllegalStateException("Invalid food hydration catalog",e);}
    }
    public static int water(String id) {
        Contribution c=FOODS.get(id);
        return c==null?1:c.water();
    }
    public static int sodium(String id) {
        Contribution c=FOODS.get(id);
        return c==null?1:c.sodium();
    }
}
