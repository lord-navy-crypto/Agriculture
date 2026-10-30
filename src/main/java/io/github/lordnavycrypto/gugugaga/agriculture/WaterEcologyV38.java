package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * Game-specific salinity metadata for manually collected field water.
 * Never changes vanilla water blocks or the ordinary Minecraft water bucket.
 * ppt and millibucket units are gameplay approximations, not field testing.
 */
public final class WaterEcologyV38 {
    private WaterEcologyV38() {}
    public static final int VOLUME_MB=1000;
    public static final String SALINITY="gugugaga_salinity_ppt";
    public static final String VOLUME="gugugaga_volume_mb";
    public static final String BIOME="gugugaga_source_biome";

    public static int salinityAt(Level level,BlockPos pos) {
        if(level.getBiome(pos).is(BiomeTags.IS_OCEAN)) {
            String id=level.getBiome(pos).unwrapKey()
                    .map(key->key.location().getPath()).orElse("");
            if(id.contains("warm_ocean"))return 37;
            if(id.contains("frozen_ocean") || id.contains("cold_ocean"))return 33;
            return 35;
        }
        String id=level.getBiome(pos).unwrapKey()
                .map(key->key.location().getPath()).orElse("");
        // Only named coastal beaches receive brackish game-water metadata.
        // Non-ocean river/swamp water remains freshwater by default.
        if(id.equals("beach") || id.equals("snowy_beach"))return 12;
        return 0;
    }

    public static ItemStack collectedBucket(Level level,BlockPos pos) {
        ItemStack stack=new ItemStack(WaterV38Content.SAMPLED_WATER_BUCKET.get());
        CompoundTag tag=new CompoundTag();
        tag.putInt(SALINITY,salinityAt(level,pos));
        tag.putInt(VOLUME,VOLUME_MB);
        tag.putString(BIOME,level.getBiome(pos).unwrapKey()
                .map(key->key.location().toString()).orElse("unknown"));
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
        return stack;
    }

    public static int salinity(ItemStack stack) {
        CustomData data=stack.get(DataComponents.CUSTOM_DATA);
        if(data==null)return 0;
        return Math.max(0,Math.min(40,data.copyTag().getInt(SALINITY)));
    }
    public static int volume(ItemStack stack) {
        CustomData data=stack.get(DataComponents.CUSTOM_DATA);
        if(data==null)return 0;
        return Math.max(0,Math.min(VOLUME_MB,data.copyTag().getInt(VOLUME)));
    }
    public static boolean valid(ItemStack stack) {
        return stack.is(WaterV38Content.SAMPLED_WATER_BUCKET.get())
                && volume(stack)==VOLUME_MB;
    }
}
