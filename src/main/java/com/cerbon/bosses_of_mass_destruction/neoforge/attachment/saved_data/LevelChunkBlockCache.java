package com.cerbon.bosses_of_mass_destruction.neoforge.attachment.saved_data;

import com.cerbon.bosses_of_mass_destruction.capability.ChunkBlockCache;
import com.cerbon.bosses_of_mass_destruction.util.BMDConstants;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
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
    public static final Codec<LevelChunkBlockCache> CACHE_CODEC = new Codec<LevelChunkBlockCache>() {
        @Override
        public <T> DataResult<T> encode(LevelChunkBlockCache cache, DynamicOps<T> ops, T prefix) {
            return DataResult.success(prefix);
        }

        @Override
        public <T> DataResult<Pair<LevelChunkBlockCache, T>> decode(DynamicOps<T> ops, T input) {
            return DataResult.success(new Pair<>(new LevelChunkBlockCache(), input));
        }
    };

    public static final SavedDataType<LevelChunkBlockCache> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(BMDConstants.MOD_ID, "bomd_level_chunk_block_cache"),
            LevelChunkBlockCache::new,
            CACHE_CODEC,
            DataFixTypes.LEVEL
    );
    public static LevelChunkBlockCache INSTANCE;
    private static final String DATA_KEY = "bomd_level_chunk_block_cache";
    private ChunkBlockCache chunkBlockCache;

    public LevelChunkBlockCache() {
        super();
    }

    public static ChunkBlockCache get(Level level) {
        LevelChunkBlockCache levelChunkBlockCache = getSavedData(level);

        if (levelChunkBlockCache.chunkBlockCache == null)
            levelChunkBlockCache.chunkBlockCache = new ChunkBlockCache();

        return levelChunkBlockCache.chunkBlockCache;
    }

    private static LevelChunkBlockCache getSavedData(Level level) {
        return level.isClientSide() ? INSTANCE : ((ServerLevel) level).getDataStorage().computeIfAbsent(TYPE);
    }
}
