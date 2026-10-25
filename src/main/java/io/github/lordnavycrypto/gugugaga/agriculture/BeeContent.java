package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Bee ecotype tags and the multi-hive apiary placement kit. */
public final class BeeContent {
    private BeeContent() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredBlock<BeeNestHabitatBlock> BUMBLEBEE_HABITAT =
            habitat("bumblebee_habitat", "bumblebee", false);
    public static final DeferredBlock<BeeNestHabitatBlock> MASON_BEE_HABITAT =
            habitat("mason_bee_habitat", "mason_bee", false);
    public static final DeferredBlock<BeeNestHabitatBlock> SQUASH_BEE_HABITAT =
            habitat("squash_bee_habitat", "squash_bee", true);
    public static final DeferredBlock<BeeNestHabitatBlock> LEAFCUTTER_BEE_HABITAT =
            habitat("leafcutter_bee_habitat", "leafcutter_bee", false);

    public static final DeferredItem<Item> BUMBLEBEE_HABITAT_ITEM = habitatItem("bumblebee_habitat", BUMBLEBEE_HABITAT);
    public static final DeferredItem<Item> MASON_BEE_HABITAT_ITEM = habitatItem("mason_bee_habitat", MASON_BEE_HABITAT);
    public static final DeferredItem<Item> SQUASH_BEE_HABITAT_ITEM = habitatItem("squash_bee_habitat", SQUASH_BEE_HABITAT);
    public static final DeferredItem<Item> LEAFCUTTER_BEE_HABITAT_ITEM = habitatItem("leafcutter_bee_habitat", LEAFCUTTER_BEE_HABITAT);

    private static DeferredBlock<BeeNestHabitatBlock> habitat(String id, String beeId, boolean ground) {
        return BLOCKS.register(id, () -> new BeeNestHabitatBlock(beeId, ground,
                BlockBehaviour.Properties.of().mapColor(ground ? MapColor.DIRT : MapColor.WOOD)
                        .strength(ground ? 0.6F : 1.5F).noOcclusion().sound(ground ? SoundType.GRAVEL : SoundType.WOOD)));
    }

    private static DeferredItem<Item> habitatItem(String id, DeferredBlock<? extends Block> block) {
        return ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static final DeferredItem<Item> MEADOW_BEE_TAG = ITEMS.register(
            "meadow_bee_tag", () -> new BeeEcotypeItem("meadow", new Item.Properties()));
    public static final DeferredItem<Item> ORCHARD_BEE_TAG = ITEMS.register(
            "orchard_bee_tag", () -> new BeeEcotypeItem("orchard", new Item.Properties()));
    public static final DeferredItem<Item> BERRY_BEE_TAG = ITEMS.register(
            "berry_bee_tag", () -> new BeeEcotypeItem("berry", new Item.Properties()));
    public static final DeferredItem<Item> LARGE_APIARY_KIT = ITEMS.register(
            "large_apiary_kit", () -> new LargeApiaryKitItem(new Item.Properties()));

    /** Distinct nectar-source bottles use the native drinkable honey item behavior. */
    public static final DeferredItem<HoneyBottleItem> CLOVER_HONEY_BOTTLE = floralHoney("clover_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> LAVENDER_HONEY_BOTTLE = floralHoney("lavender_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> BUCKWHEAT_HONEY_BOTTLE = floralHoney("buckwheat_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> SUNFLOWER_HONEY_BOTTLE = floralHoney("sunflower_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> DANDELION_HONEY_BOTTLE = floralHoney("dandelion_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> POPPY_HONEY_BOTTLE = floralHoney("poppy_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> ALLIUM_HONEY_BOTTLE = floralHoney("allium_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> CORNFLOWER_HONEY_BOTTLE = floralHoney("cornflower_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> AZURE_BLUET_HONEY_BOTTLE = floralHoney("azure_bluet_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> BLUE_ORCHID_HONEY_BOTTLE = floralHoney("blue_orchid_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> LILY_OF_THE_VALLEY_HONEY_BOTTLE = floralHoney("lily_of_the_valley_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> OXEYE_DAISY_HONEY_BOTTLE = floralHoney("oxeye_daisy_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> TORCHFLOWER_HONEY_BOTTLE = floralHoney("torchflower_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> RED_TULIP_HONEY_BOTTLE = floralHoney("red_tulip_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> ORANGE_TULIP_HONEY_BOTTLE = floralHoney("orange_tulip_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> WHITE_TULIP_HONEY_BOTTLE = floralHoney("white_tulip_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> PINK_TULIP_HONEY_BOTTLE = floralHoney("pink_tulip_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> WITHER_ROSE_HONEY_BOTTLE = floralHoney("wither_rose_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> BLACKBERRY_HONEY_BOTTLE = floralHoney("blackberry_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> BLACKCURRANT_HONEY_BOTTLE = floralHoney("blackcurrant_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> CHIVE_HONEY_BOTTLE = floralHoney("chive_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> PUMPKIN_BLOSSOM_HONEY_BOTTLE = floralHoney("pumpkin_blossom_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> WILDFLOWER_HONEY_BOTTLE = floralHoney("wildflower_honey_bottle");

    /**
     * Fantasy status effects follow the vanilla suspicious-stew design pattern.
     * Real floral honey does not confer these superhuman powers. All effects
     * are limited to level I and a brief duration for reasonable gameplay.
     */
    private static DeferredItem<HoneyBottleItem> floralHoney(String name) {
        FoodProperties.Builder food = new FoodProperties.Builder().alwaysEdible();
        switch (name) {
            case "clover_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.JUMP, 200, 0), 1.0F);
            case "lavender_honey_bottle" -> food.nutrition(5)
                    .saturationModifier(0.35F)
                    .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0), 1.0F);
            case "buckwheat_honey_bottle" -> food.nutrition(7)
                    .saturationModifier(0.15F)
                    .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0), 1.0F);
            case "sunflower_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.25F)
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 240, 0), 1.0F);
            case "dandelion_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 7, 0), 1.0F);
            case "poppy_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 0), 1.0F);
            case "allium_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 80, 0), 1.0F);
            case "cornflower_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.JUMP, 120, 0), 1.0F);
            case "azure_bluet_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 160, 0), 1.0F);
            case "blue_orchid_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 7, 0), 1.0F);
            case "lily_of_the_valley_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F); // poison applied after vanilla honey's poison-clearing step
            case "oxeye_daisy_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 160, 0), 1.0F);
            case "torchflower_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 0), 1.0F);
            case "red_tulip_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 180, 0), 1.0F);
            case "orange_tulip_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 180, 0), 1.0F);
            case "white_tulip_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 180, 0), 1.0F);
            case "pink_tulip_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 180, 0), 1.0F);
            case "wither_rose_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.20F)
                    .effect(() -> new MobEffectInstance(MobEffects.WITHER, 160, 0), 1.0F);
            case "blackberry_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.30F)
                    .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 160, 0), 1.0F);
            case "blackcurrant_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.30F)
                    .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 1.0F);
            case "chive_honey_bottle" -> food.nutrition(5)
                    .saturationModifier(0.35F)
                    .effect(() -> new MobEffectInstance(MobEffects.JUMP, 100, 0), 1.0F);
            case "pumpkin_blossom_honey_bottle" -> food.nutrition(7)
                    .saturationModifier(0.25F)
                    .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0), 1.0F);
            case "wildflower_honey_bottle" -> food.nutrition(6)
                    .saturationModifier(0.35F);
            default -> throw new IllegalArgumentException("Unknown floral honey: " + name);
        }
        return ITEMS.register(name, () -> name.equals("lily_of_the_valley_honey_bottle")
                ? new PoisonFlowerHoneyBottleItem(new Item.Properties()
                        .stacksTo(16).food(food.build()))
                : new HoneyBottleItem(new Item.Properties()
                        .stacksTo(16).food(food.build())));
    }

    public static final DeferredItem<Item> HONEY_HARVESTER = ITEMS.register(
            "honey_harvester", () -> new HoneyHarvesterItem(new Item.Properties()));
}
