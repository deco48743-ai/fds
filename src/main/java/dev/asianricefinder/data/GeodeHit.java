package dev.asianricefinder.data;

import net.minecraft.util.math.BlockPos;

public record GeodeHit(BlockPos center, ChunkKey owner, int evidenceBlocks, boolean underDeepslate) { }
