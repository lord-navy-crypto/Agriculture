package io.github.lordnavycrypto.gugugaga.agriculture;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/**
 * Server-authoritative full edible-registry and recipe scanner. A complete
 * deterministic snapshot is rebuilt on server start and datapack reload.
 * No per-tick registry scan or client recipe-manager access is required.
 */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID)
public final class NutritionFoodScannerV415 {
    public static final String REPORT_NAME = "nutrition_food_compat_report.json";
    private static final int LIMIT_RECIPES = 100_000;
    private static final int LIMIT_INGREDIENTS = 32;
    private static final int LIMIT_ALTERNATIVES = 128;
    private static final int PASSES = 8;
    private static volatile Map<String, Result> serverProfiles = Map.of();
    private static volatile Map<String, int[]> clientProfiles = Map.of();

    public record Result(int[] points, String source, String recipeId,
                         int outputCount, int totalSlots, int knownSlots,
                         JsonArray ingredients) {
        public Result {
            points = Arrays.copyOf(points, points.length);
        }
    }

    private record InputSlot(List<String> alternatives) {}
    private record InputRecipe(String id, String output, int outputCount,
                               List<InputSlot> slots) {}

    private NutritionFoodScannerV415() {}

    public static int[] onServer(String id) {
        Result result = serverProfiles.get(id);
        return result == null ? null : Arrays.copyOf(result.points(), result.points().length);
    }

    public static int[] onClient(String id) {
        int[] values = clientProfiles.get(id);
        return values == null ? null : Arrays.copyOf(values, values.length);
    }

    public static void setClientSnapshot(Map<String, int[]> snapshot) {
        Map<String, int[]> copy = new HashMap<>();
        snapshot.forEach((key, value) -> {
            if (value != null && value.length == 27)
                copy.put(key, Arrays.copyOf(value, 27));
        });
        clientProfiles = Map.copyOf(copy);
    }

    /** The network handler uses this to replace each reconnect/reload snapshot. */
    public static void resetClientSnapshot() { clientProfiles = Map.of(); }

    /** Copies server-owned derived data; no NBT mutation occurs. */
    public static Map<String, int[]> serverSnapshot() {
        Map<String, int[]> copy = new HashMap<>();
        serverProfiles.forEach((id, profile) -> copy.put(id, Arrays.copyOf(profile.points(), 27)));
        return copy;
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        rebuild(event.getServer());
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        MinecraftServer server = event.getPlayerList().getServer();
        // Null means datapack reload / all-player synchronization; a login
        // only needs a snapshot transfer, not another full recipe scan.
        if (event.getPlayer() == null) rebuild(server);
        if (event.getPlayer() != null) {
            NutritionFoodSyncV415.send(event.getPlayer());
        } else {
            for (var player : server.getPlayerList().getPlayers())
                NutritionFoodSyncV415.send(player);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        serverProfiles = Map.of();
    }

    public static synchronized void rebuild(MinecraftServer server) {
        if (server == null) return;
        System.out.println("[Backroom Agriculture] Starting server nutrition food registry and recipe scan");
        List<InputRecipe> recipes = new ArrayList<>();
        int inspectedRecipes = 0;
        // RecipeManager is accessed only on the logical server after tags
        // and datapack recipes are available.
        for (RecipeHolder<?> holder : server.getRecipeManager().getRecipes()) {
            if (++inspectedRecipes > LIMIT_RECIPES) break;
            Recipe<?> recipe = holder.value();
            ItemStack output;
            try { output = recipe.getResultItem(server.registryAccess()); }
            catch (RuntimeException incompatibleRecipe) { continue; }
            if (output.isEmpty() || !output.has(DataComponents.FOOD)) continue;
            List<Ingredient> ingredients;
            try { ingredients = recipe.getIngredients(); }
            catch (RuntimeException incompatibleRecipe) { continue; }
            if (ingredients.isEmpty() || ingredients.size() > LIMIT_INGREDIENTS) continue;
            List<InputSlot> slots = new ArrayList<>();
            boolean invalid = false;
            for (Ingredient ingredient : ingredients) {
                if (ingredient.isEmpty()) continue;
                ItemStack[] alternatives;
                try { alternatives = ingredient.getItems(); }
                catch (RuntimeException incompatibleIngredient) {
                    invalid = true;
                    break;
                }
                if (alternatives.length == 0 || alternatives.length > LIMIT_ALTERNATIVES) {
                    invalid = true;
                    break;
                }
                Set<String> ids = new HashSet<>();
                for (ItemStack alt : alternatives) {
                    if (!alt.isEmpty()) ids.add(itemId(alt));
                }
                if (ids.isEmpty()) { invalid = true; break; }
                slots.add(new InputSlot(ids.stream().sorted().toList()));
            }
            if (invalid || slots.isEmpty()) continue;
            recipes.add(new InputRecipe(holder.id().toString(), itemId(output),
                    Math.max(1, output.getCount()), List.copyOf(slots)));
        }
        recipes.sort(Comparator.comparing(InputRecipe::id));

        Map<String, Result> built = new HashMap<>();
        for (int pass = 0; pass < PASSES; pass++) {
            int added = 0;
            for (InputRecipe entry : recipes) {
                if (NutritionProfilesV35.hasProfile(entry.output())
                        || built.containsKey(entry.output())) continue;
                Result calculated = calculate(entry, built);
                if (calculated != null) {
                    built.put(entry.output(), calculated);
                    added++;
                }
            }
            if (added == 0) break;
        }
        serverProfiles = Map.copyOf(built);
        writeReport(server, inspectedRecipes, recipes, built);
        System.out.println("[Backroom Agriculture] Nutrition food scan: "
                + BuiltInRegistries.ITEM.size() + " registered items, "
                + recipes.size() + " supported edible recipes, "
                + built.size() + " recipe-derived profiles. Report: " + REPORT_NAME);
    }

    private static Result calculate(InputRecipe recipe, Map<String, Result> earlier) {
        // Each ingredient slot represents one unit. Repeated slots therefore
        // preserve multiplicity. The recipe's output stack count divides the
        // resulting points per edible item.
        double[] sums = new double[27];
        int known = 0;
        JsonArray ingredientDetails = new JsonArray();
        for (InputSlot slot : recipe.slots()) {
            double[] choices = new double[27];
            int recognizedAlternatives = 0;
            JsonArray options = new JsonArray();
            for (String id : slot.alternatives()) {
                JsonObject alt = new JsonObject();
                alt.addProperty("item", id);
                int[] values = baseOrDerived(id, earlier);
                if (values == null) {
                    alt.addProperty("resolved", false);
                } else {
                    alt.addProperty("resolved", true);
                    recognizedAlternatives++;
                    for (int i = 0; i < 27; i++)
                        if (!NutritionPhysiologyV39.linked(i))
                            choices[i] += values[i];
                }
                options.add(alt);
            }
            JsonObject detail = new JsonObject();
            detail.add("alternatives", options);
            detail.addProperty("recognized_alternatives", recognizedAlternatives);
            detail.addProperty("ingredient_quantity", 1);
            detail.addProperty("quantity_per_output", 1.0 / recipe.outputCount());
            detail.addProperty("total_alternatives", slot.alternatives().size());
            ingredientDetails.add(detail);
            // Large unresolved tag alternatives are not reliable enough to infer.
            if (recognizedAlternatives * 2 < slot.alternatives().size()) continue;
            known++;
            for (int i = 0; i < 27; i++)
                sums[i] += choices[i] / recognizedAlternatives;
        }
        int total = recipe.slots().size();
        // Reject low-coverage recipes instead of inventing precise nutrients.
        if (known == 0 || known * 3 < total * 2) return null;
        int[] inferred = new int[27];
        boolean positive = false;
        for (int i = 0; i < 27; i++) {
            if (NutritionPhysiologyV39.linked(i)) continue;
            inferred[i] = Math.max(0, Math.min(10,
                    (int) Math.round(sums[i] / recipe.outputCount())));
            if (inferred[i] > 0) positive = true;
        }
        if (!positive) return null;
        return new Result(inferred, "recipe", recipe.id(), recipe.outputCount(),
                total, known, ingredientDetails);
    }

    private static int[] baseOrDerived(String id, Map<String, Result> derived) {
        if (NutritionProfilesV35.hasProfile(id))
            return NutritionProfilesV35.contribution(id);
        Result existing = derived.get(id);
        if (existing != null) return existing.points();
        ResourceLocation key = ResourceLocation.tryParse(id);
        if (key == null || !BuiltInRegistries.ITEM.containsKey(key)) return null;
        ItemStack stack = BuiltInRegistries.ITEM.get(key).getDefaultInstance();
        // Non-edible craft ingredients can still have recognized ingredient
        // categories (milk, eggs, flour etc.); unknown nonfood inputs remain
        // unresolved rather than receiving generic nutrition.
        return NutritionFoodCompatibilityV415.infer(stack);
    }

    private static String itemId(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static void writeReport(MinecraftServer server, int inspectedRecipes,
                                    List<InputRecipe> recipes, Map<String, Result> derived) {
        JsonObject doc = new JsonObject();
        doc.addProperty("schema", "backroom-agriculture-food-compat-v4.15");
        doc.addProperty("registry_item_count", BuiltInRegistries.ITEM.size());
        doc.addProperty("recipes_inspected", inspectedRecipes);
        doc.addProperty("supported_edible_recipes", recipes.size());
        doc.addProperty("exact_catalog_profiles", NutritionProfilesV35.catalogSize());
        doc.addProperty("recipe_derived_profiles", derived.size());
        int edible = 0, exact = 0, fromRecipe = 0, inferred = 0, neutral = 0;
        JsonArray items = new JsonArray();
        List<Item> sorted = new ArrayList<>();
        BuiltInRegistries.ITEM.forEach(sorted::add);
        sorted.sort(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()));
        for (Item item : sorted) {
            ItemStack stack = item.getDefaultInstance();
            if (!stack.has(DataComponents.FOOD)) continue;
            edible++;
            String id = itemId(stack);
            Result r = derived.get(id);
            String source;
            int[] points;
            if (NutritionProfilesV35.hasProfile(id)) {
                exact++;
                source = "exact";
                points = NutritionProfilesV35.contribution(id);
            } else if (r != null) {
                fromRecipe++;
                source = "recipe";
                points = r.points();
            } else {
                points = NutritionFoodCompatibilityV415.infer(stack);
                if (points == null) {
                    neutral++;
                    source = "neutral";
                    points = NutritionProfilesV35.contribution(id);
                } else {
                    inferred++;
                    source = "tag_or_item_id";
                }
            }
            JsonObject row = new JsonObject();
            row.addProperty("item", id);
            row.addProperty("source", source);
            JsonArray nutrients = new JsonArray();
            for (int point : points) nutrients.add(point);
            row.add("nutrients_27", nutrients);
            if (r != null && source.equals("recipe")) {
                row.addProperty("recipe", r.recipeId());
                row.addProperty("output_count", r.outputCount());
                row.addProperty("ingredient_slots", r.totalSlots());
                row.addProperty("recognized_slots", r.knownSlots());
                row.add("ingredient_alternatives", r.ingredients());
            }
            items.add(row);
        }
        doc.addProperty("edible_items", edible);
        doc.addProperty("exact", exact);
        doc.addProperty("derived_from_recipe", fromRecipe);
        doc.addProperty("inferred_from_tags_or_id", inferred);
        doc.addProperty("neutral_unclassified", neutral);
        doc.addProperty("coverage_percent", edible == 0 ? 100.0
                : 100.0 * (edible - neutral) / edible);
        doc.addProperty("limitations", "Game points only. Recipe slot counts are used as ingredient quantity; unknown ingredient alternatives are recorded; dynamic recipes without declared ingredients are not inferred.");
        doc.add("items", items);
        Path path = server.getWorldPath(LevelResource.ROOT).resolve(REPORT_NAME);
        try {
            Files.writeString(path,
                    new GsonBuilder().setPrettyPrinting().create().toJson(doc),
                    StandardCharsets.UTF_8);
        } catch (IOException error) {
            System.err.println("[Backroom Agriculture] Could not write nutrition report to "
                    + path + ": " + error.getMessage());
        }
    }
}
