package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Best-effort compatibility for edible items supplied by other mods.
 *
 * Resolution order is deliberately conservative:
 * 1) exact shipped Agriculture food profile (handled by NutritionProfilesV35);
 * 2) conventional Common/Forge food item tags;
 * 3) registry-path category hints;
 * 4) the existing neutral fallback.
 *
 * This produces abstract game points only. It does not infer real nutritional
 * composition and never overrides an exact profile.
 */
public final class NutritionFoodCompatibilityV415 {
    private NutritionFoodCompatibilityV415() {}

    private record FoodTag(String category, TagKey<Item> tag) {}

    private static TagKey<Item> tag(String namespace, String path) {
        return TagKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private static FoodTag t(String category, String namespace, String path) {
        return new FoodTag(category, tag(namespace, path));
    }

    private static final FoodTag[] TAGS = {
        t("vegetable", "c", "foods/vegetables"),
        t("vegetable", "forge", "vegetables"),
        t("vegetable", "forge", "foods/vegetables"),
        t("leafy", "c", "foods/leafy_vegetables"),
        t("leafy", "forge", "leafy_vegetables"),
        t("fruit", "c", "foods/fruits"),
        t("fruit", "forge", "fruits"),
        t("berries", "c", "foods/berries"),
        t("berries", "forge", "berries"),
        t("grain", "c", "foods/grains"),
        t("grain", "forge", "grains"),
        t("grain", "forge", "grain"),
        t("root", "c", "foods/roots"),
        t("root", "forge", "roots"),
        t("legume", "c", "foods/beans"),
        t("legume", "forge", "beans"),
        t("seed", "c", "foods/seeds"),
        t("seed", "forge", "seeds"),
        t("seed", "c", "foods/nuts"),
        t("seed", "forge", "nuts"),
        t("meat", "c", "foods/raw_meats"),
        t("meat", "c", "foods/cooked_meats"),
        t("meat", "forge", "raw_meats"),
        t("meat", "forge", "cooked_meats"),
        t("meat", "forge", "meat"),
        t("seafood", "c", "foods/raw_fishes"),
        t("seafood", "c", "foods/cooked_fishes"),
        t("seafood", "c", "foods/fishes"),
        t("seafood", "forge", "fish"),
        t("seafood", "forge", "fishes"),
        t("egg", "c", "foods/eggs"),
        t("egg", "forge", "eggs"),
        t("dairy", "c", "foods/milk"),
        t("dairy", "c", "foods/cheese"),
        t("dairy", "forge", "milk"),
        t("dairy", "forge", "cheese"),
        t("mushroom", "c", "foods/mushrooms"),
        t("mushroom", "forge", "mushrooms"),
        t("seaweed", "c", "foods/seaweeds"),
        t("seaweed", "forge", "seaweeds"),
        t("honey", "c", "foods/honey"),
        t("honey", "forge", "honey"),
        t("herb", "c", "foods/herbs"),
        t("herb", "forge", "herbs"),
        t("sweet", "c", "foods/sweets"),
        t("sweet", "forge", "sweets"),
        t("beverage", "c", "foods/beverages"),
        t("beverage", "forge", "beverages"),
        t("beverage", "c", "foods/soups"),
        t("beverage", "forge", "soups"),
        t("vegetable", "c", "foods/salads"),
        t("vegetable", "forge", "salads"),
        t("grain", "c", "foods/breads"),
        t("grain", "forge", "bread")
    };

    /** Returns the merged category profile, or null when no category matched. */
    public static int[] infer(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        Set<String> categories = new LinkedHashSet<>();
        for (FoodTag candidate : TAGS) {
            if (stack.is(candidate.tag())) categories.add(candidate.category());
        }
        if (categories.isEmpty()) {
            String path = net.minecraft.core.registries.BuiltInRegistries.ITEM
                    .getKey(stack.getItem()).getPath().toLowerCase(java.util.Locale.ROOT);
            inferFromPath(path, categories);
        }
        if (categories.isEmpty()) return null;

        int[] result = new int[NutritionProfilesV35.NUTRIENTS.length];
        for (String category : categories) {
            int[] profile = NutritionProfilesV35.categoryContribution(category);
            for (int i = 0; i < result.length; i++) {
                // Water and sodium remain owned by WaterSaltV37.
                if (NutritionPhysiologyV39.linked(i)) continue;
                result[i] = Math.min(10, result[i] + profile[i]);
            }
        }
        return result;
    }

    public static boolean hasInferredProfile(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        for (FoodTag candidate : TAGS) {
            if (stack.is(candidate.tag())) return true;
        }
        Set<String> categories = new LinkedHashSet<>();
        String path = net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getKey(stack.getItem()).getPath().toLowerCase(java.util.Locale.ROOT);
        inferFromPath(path, categories);
        return !categories.isEmpty();
    }

    /** Separate water/sodium fallback for the independent WaterSaltV37 model. */
    public static int inferredWater(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 1;
        if (matchesAny(stack, "c", "foods/beverages", "foods/soups", "foods/juices")
                || matchesAny(stack, "forge", "beverages", "soups", "drinks")) return 4;
        if (matchesAny(stack, "c", "foods/fruits", "foods/berries", "foods/vegetables")
                || matchesAny(stack, "forge", "fruits", "berries", "vegetables")) return 2;
        String path = net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getKey(stack.getItem()).getPath().toLowerCase(java.util.Locale.ROOT);
        if (contains(path, "juice", "tea", "coffee", "drink", "soup", "stew", "broth", "smoothie", "milkshake")) return 4;
        if (contains(path, "fruit", "berry", "berries", "melon", "cucumber", "lettuce", "tomato", "salad")) return 2;
        return 1;
    }

    public static int inferredSodium(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 1;
        if (matchesAny(stack, "c", "foods/salted", "foods/pickled")
                || matchesAny(stack, "forge", "salted", "pickled")) return 3;
        String path = net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getKey(stack.getItem()).getPath().toLowerCase(java.util.Locale.ROOT);
        if (contains(path, "salted", "pickle", "brine", "soy_sauce", "bacon", "ham", "cured")) return 3;
        if (contains(path, "cheese", "sausage", "jerky", "smoked")) return 2;
        return 1;
    }

    public static boolean hasWaterSaltInference(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (inferredWater(stack) != 1 || inferredSodium(stack) != 1) return true;
        return hasInferredProfile(stack);
    }

    private static boolean matchesAny(ItemStack stack, String namespace, String... paths) {
        for (String path : paths) {
            if (stack.is(tag(namespace, path))) return true;
        }
        return false;
    }

    private static void inferFromPath(String path, Set<String> out) {
        // Name hints are intentionally broad and low-confidence. Exact profiles
        // and declared tags always win, and only abstract category points result.
        if (contains(path, "fish", "cod", "salmon", "tuna", "shrimp", "crab", "lobster", "squid", "clam", "oyster", "anchov")) out.add("seafood");
        if (contains(path, "beef", "pork", "chicken", "mutton", "lamb", "sausage", "bacon", "steak", "meat", "ham")) out.add("meat");
        if (contains(path, "egg")) out.add("egg");
        if (contains(path, "milk", "cheese", "cream", "yogurt", "butter")) out.add("dairy");
        if (contains(path, "mushroom", "fungus", "truffle")) out.add("mushroom");
        if (contains(path, "seaweed", "kelp", "nori", "algae")) out.add("seaweed");
        if (contains(path, "bean", "lentil", "chickpea", "green_pea", "split_pea", "peas", "pea_", "soy", "tofu")) out.add("legume");
        if (contains(path, "nut", "almond", "walnut", "peanut", "seed")) out.add("seed");
        if (contains(path, "leaf", "lettuce", "spinach", "kale", "cabbage", "bok_choy")) out.add("leafy");
        if (contains(path, "carrot", "potato", "beet", "turnip", "radish", "taro", "yam", "root")) out.add("root");
        if (contains(path, "vegetable", "veggie", "salad", "tomato", "onion", "pepper", "cucumber", "broccoli", "corn", "pumpkin")) out.add("vegetable");
        if (contains(path, "berry", "berries", "strawberry", "blueberry", "raspberry", "cranberry")) out.add("berries");
        if (contains(path, "fruit", "apple", "orange", "lemon", "lime", "grape", "peach", "pear", "mango", "melon", "banana")) out.add("fruit");
        if (contains(path, "bread", "toast", "noodle", "pasta", "rice", "oat", "wheat", "flour", "porridge", "sandwich", "bun")) out.add("grain");
        if (contains(path, "honey")) out.add("honey");
        if (contains(path, "herb", "basil", "mint", "cilantro", "parsley", "thyme", "rosemary")) out.add("herb");
        if (contains(path, "soup", "stew", "broth", "juice", "tea", "coffee", "drink", "smoothie", "milkshake")) out.add("beverage");
        if (contains(path, "cookie", "cake", "pie", "candy", "chocolate", "sweet", "dessert", "tart", "pudding")) out.add("sweet");
        if (out.isEmpty() && contains(path, "grain", "barley", "buckwheat", "quinoa", "amaranth")) out.add("grain");
    }

    private static boolean contains(String value, String... tokens) {
        for (String token : tokens) if (value.contains(token)) return true;
        return false;
    }
}
