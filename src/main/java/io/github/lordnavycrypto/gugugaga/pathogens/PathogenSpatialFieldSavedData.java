package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Per-dimension SavedData: sparse map of 4x4 block areas and 15 independent
 * abstract game pressure values. No per-pathogen Block, BlockEntity or Entity.
 */
public final class PathogenSpatialFieldSavedData extends SavedData {
    private static final String FILE_ID="gugugaga_spatial_pathogen_field_v1";
    private static final Factory<PathogenSpatialFieldSavedData> FACTORY=
            new Factory<>(PathogenSpatialFieldSavedData::new,
                    PathogenSpatialFieldSavedData::load);
    private Map<PathogenSpatialFieldMath.Cell,int[]> cells=new HashMap<>();
    private int cursor;

    private PathogenSpatialFieldSavedData(){}
    public static PathogenSpatialFieldSavedData forLevel(ServerLevel level){
        return level.getDataStorage().computeIfAbsent(FACTORY,FILE_ID);
    }
    public int size(){return cells.size();}
    public int get(PathogenSpatialFieldMath.Cell cell,int channel){
        int[] row=cells.get(cell);
        return row==null?0:row[channel];
    }
    public Map<PathogenSpatialFieldMath.Cell,int[]> snapshot(){
        return PathogenSpatialFieldMath.immutableCopy(cells);
    }
    public boolean set(PathogenSpatialFieldMath.Cell cell,int channel,int value){
        var next=PathogenSpatialFieldMath.withPressure(cells,cell,channel,value);
        if(value>0 && !cells.containsKey(cell) && next.size()==cells.size())
            return false;
        cells=next;
        setDirty();
        return true;
    }
    public PathogenSpatialFieldMath.Frame step(long gameStep,
            java.util.function.Predicate<PathogenSpatialFieldMath.Cell> loadedActive){
        var frame=PathogenSpatialFieldMath.step(cells,cursor,64,gameStep,loadedActive);
        cells=frame.cells();
        cursor=frame.nextCursor();
        if(frame.moved()>0||frame.faded()>0)setDirty();
        return frame;
    }

    public static PathogenSpatialFieldSavedData load(CompoundTag nbt,
            HolderLookup.Provider registry){
        var value=new PathogenSpatialFieldSavedData();
        ListTag list=nbt.getList("Cells",Tag.TAG_COMPOUND);
        for(int i=0;i<list.size()&&value.cells.size()<PathogenSpatialFieldMath.MAX_CELLS;i++){
            CompoundTag row=list.getCompound(i);
            int[] levels=row.getIntArray("Pressure");
            if(levels.length!=PathogenSpatialFieldMath.CHANNELS)continue;
            try{
                var c=new PathogenSpatialFieldMath.Cell(
                        row.getInt("CellX"),row.getInt("CellZ"));
                var before=PathogenSpatialFieldMath.withPressure(
                    Map.of(),c,0,0); // force bounds path on first access
                boolean nonzero=false,valid=true;
                for(int n:levels){
                    if(n<0||n>PathogenSpatialFieldMath.MAX_PRESSURE)valid=false;
                    if(n>0)nonzero=true;
                }
                if(valid&&nonzero)value.cells.put(c,levels.clone());
            }catch(IllegalArgumentException ignored){
                // Invalid or stale records are ignored without crashing world loads.
            }
        }
        value.cursor=Math.max(0,nbt.getInt("Cursor"));
        return value;
    }
    @Override public CompoundTag save(CompoundTag result,
            HolderLookup.Provider registry){
        ListTag list=new ListTag();
        for(var cell:cells.entrySet()){
            CompoundTag row=new CompoundTag();
            row.putInt("CellX",cell.getKey().x());
            row.putInt("CellZ",cell.getKey().z());
            row.putIntArray("Pressure",cell.getValue());
            list.add(row);
        }
        result.put("Cells",list);
        result.putInt("Cursor",cursor);
        return result;
    }
}
