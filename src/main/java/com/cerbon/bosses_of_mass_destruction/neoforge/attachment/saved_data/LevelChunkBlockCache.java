package com.cerbon.bosses_of_mass_destruction.neoforge.attachment.saved_data;

import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.util.datafix.DataFixTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.function.Supplier;

public class LevelChunkBlockCache extends SavedData {
    public static final SavedDataType<LevelChunkBlockCache> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "bomd_level_chunk_block_cache"),
            LevelChunkBlockCache::new,
            Codec.unit(() -> new LevelChunkBlockCache()),
            DataFixTypes.NO_OPS
    );
    public static LevelChunkBlockCache INSTANCE;
    private static final String DATA_KEY = "bomd_level_chunk_block_cache";
    private ChunkBlockCache chunkBlockCache;

    public LevelChunkBlockCache() {
        super();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {return tag;}

    public static ChunkBlockCache get(Level level) {
        LevelChunkBlockCache levelChunkBlockCache = getSavedData(level);

        if (levelChunkBlockCache.chunkBlockCache == null)
            levelChunkBlockCache.chunkBlockCache = new ChunkBlockCache();

        return levelChunkBlockCache.chunkBlockCache;
    }

    private static LevelChunkBlockCache getSavedData(Level level) {
        return level.isClientSide() ? INSTANCE : ((ServerLevel) level).getDataStorage().computeIfAbsent(TYPE, DATA_KEY);
    }
}
