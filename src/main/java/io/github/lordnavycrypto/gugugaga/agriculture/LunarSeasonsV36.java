package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Lightweight 32-day, four-moon agricultural calendar.
 *
 * Four vanilla 8-day lunar cycles form one game year. The ordinary biomes
 * retain exactly 8 days per season; exceptional habitats alter local season
 * lengths while still totaling 32 days. Does not change weather, biome
 * definitions, actual moon position, or other seasonal mods' state.
 *
 * Temperature below is a GAMEPLAY INDEX, not a real thermometer or Celsius.
 * Does not tick globally; invoked only when an immature crop attempts growth.
 */
public final class LunarSeasonsV36 {
    private LunarSeasonsV36() {}

    public static final int DAYS_PER_YEAR=32;
    public static final int DAYS_PER_MOON=8;
    public enum Season { SPRING, SUMMER, AUTUMN, WINTER }
    public enum Habitat { TEMPERATE, TROPICAL, ARID, COLD, WET }

    public static long absoluteDay(ServerLevel level) {
        return Math.floorDiv(level.getDayTime(), 24000L);
    }
    public static int yearDay(long absoluteDay) {
        return Math.floorMod(absoluteDay, DAYS_PER_YEAR);
    }
    public static int moonPhase(long absoluteDay) {
        // Java Minecraft phase 0 = full moon, phase 4 = new moon.
        return Math.floorMod(absoluteDay, DAYS_PER_MOON);
    }
    public static long year(long absoluteDay) {
        return Math.floorDiv(absoluteDay, DAYS_PER_YEAR) + 1;
    }
    public static Season worldSeason(long absoluteDay) {
        return Season.values()[yearDay(absoluteDay) / DAYS_PER_MOON];
    }
    public static Habitat habitat(String biomeId) {
        String id=biomeId.substring(biomeId.lastIndexOf(':')+1);
        if (id.contains("frozen") || id.contains("snowy") || id.contains("ice_spikes")
                || id.contains("taiga") || id.contains("grove") || id.contains("peaks")
                || id.contains("windswept")) return Habitat.COLD;
        if (id.contains("desert") || id.contains("badlands")
                || id.contains("savanna")) return Habitat.ARID;
        if (id.contains("jungle") || id.contains("bamboo")) return Habitat.TROPICAL;
        if (id.contains("swamp") || id.contains("mangrove")) return Habitat.WET;
        return Habitat.TEMPERATE;
    }
    /** All profiles preserve the same 32-day global year. */
    public static int[] seasonLengths(Habitat habitat) {
        return switch(habitat) {
            case TEMPERATE -> new int[]{8,8,8,8};
            case TROPICAL -> new int[]{8,10,8,6};
            case ARID -> new int[]{7,11,8,6};
            case COLD -> new int[]{7,6,8,11};
            case WET -> new int[]{9,9,8,6};
        };
    }
    public static Season localSeason(long day, Habitat habitat) {
        int n=yearDay(day);
        int[] lengths=seasonLengths(habitat);
        for (Season season: Season.values()) {
            if (n < lengths[season.ordinal()]) return season;
            n -= lengths[season.ordinal()];
        }
        return Season.WINTER;
    }
    public static int localDayOfSeason(long day, Habitat habitat) {
        int n=yearDay(day);
        for(int days:seasonLengths(habitat)) {
            if(n<days)return n+1;
            n-=days;
        }
        return 1;
    }
    public static String biomeId(ServerLevel level,BlockPos pos) {
        return level.getBiome(pos).unwrapKey().map(k -> k.location().toString())
                .orElse("minecraft:plains");
    }

    /** A crop-cover shortcut, not a full thermodynamic room simulator. */
    private static boolean hasGlassCanopy(ServerLevel level,BlockPos crop) {
        for(int dy=2;dy<=4;dy++) {
            BlockPos check=crop.above(dy);
            if(!level.hasChunkAt(check))return false;
            if(level.getBlockState(check).getBlock() instanceof AbstractGlassBlock)
                return true;
        }
        return false;
    }
    private static boolean hasLocalHeat(ServerLevel level,BlockPos crop) {
        // Only 12 bounded positions, no full room flood-fill or chunk loading.
        int[][] offsets={
            {1,0,0},{-1,0,0},{0,0,1},{0,0,-1},
            {2,0,0},{-2,0,0},{0,0,2},{0,0,-2},
            {1,1,0},{-1,1,0},{0,1,1},{0,1,-1}
        };
        for(int[] o:offsets) {
            BlockPos p=crop.offset(o[0],o[1],o[2]);
            if(!level.hasChunkAt(p))continue;
            BlockState state=level.getBlockState(p);
            if(state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)
                    || state.is(Blocks.FURNACE) || state.is(Blocks.BLAST_FURNACE)
                    || state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.LAVA))
                return true;
        }
        return false;
    }
    public static boolean protectedGrowingSpace(ServerLevel level,BlockPos pos) {
        return hasGlassCanopy(level,pos) && hasLocalHeat(level,pos);
    }

    /**
     * Season and daylight gate on top of old soil, water and biome checks.
     * "cool", "warm", "dry", "wet", "temperate" are horticultural
     * groups, not exact laboratory germination data.
     */
    public static boolean allowsGrowth(String group, ServerLevel level,
                                       BlockPos pos, RandomSource random) {
        if(level.dimension()!=Level.OVERWORLD)return true;
        if(level.getMaxLocalRawBrightness(pos)<9)return false;
        Habitat habitat=habitat(biomeId(level,pos));
        Season season=localSeason(absoluteDay(level),habitat);
        boolean sheltered=protectedGrowingSpace(level,pos);
        float probability=switch(season) {
            case SPRING -> switch(group) {
                case "cool","wet" -> 1.0F;
                default -> 0.85F;
            };
            case SUMMER -> switch(group) {
                case "cool" -> 0.50F;
                case "warm","dry" -> 1.0F;
                default -> 0.85F;
            };
            case AUTUMN -> switch(group) {
                case "warm" -> 0.48F;
                case "cool" -> 0.82F;
                default -> 0.78F;
            };
            case WINTER -> switch(group) {
                case "cool" -> 0.28F;
                case "warm","wet" -> 0.05F;
                default -> 0.14F;
            };
        };
        if(season==Season.WINTER) {
            if(habitat==Habitat.COLD) probability*=0.24F;
            if(habitat==Habitat.TROPICAL)probability=Math.max(probability,0.68F);
            if(habitat==Habitat.ARID)probability=Math.max(probability,0.38F);
            if(sheltered)probability=Math.max(probability,0.82F);
        }
        if(season==Season.SUMMER && habitat==Habitat.ARID && "cool".equals(group))
            probability*=0.60F;
        return random.nextFloat()<probability;
    }
}
