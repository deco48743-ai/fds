package dev.asianricefinder.scan;

import dev.asianricefinder.data.ChunkKey;
import dev.asianricefinder.data.ChunkScanResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.WorldChunk;

/** Performs one bounded scan at a time on Minecraft's client thread. */
public final class ChunkScanner {
    private final GeodeDetector geodes = new GeodeDetector();

    public ChunkScanResult scan(ClientWorld level, ChunkKey key, int scanDepth, long tick) {
        WorldChunk chunk = level.getChunkManager().getWorldChunk(key.x(), key.z());
        if (chunk == null) return new ChunkScanResult(key, java.util.List.of(), false, tick);
        if (MinecraftClient.getInstance().player == null) return new ChunkScanResult(key, java.util.List.of(), false, tick);
        int playerY = (int) Math.floor(MinecraftClient.getInstance().player.getY());
        int minY = Math.max(level.getBottomY(), playerY - scanDepth);
        int maxY = Math.min(level.getTopY() - 1, playerY + scanDepth);
        return new ChunkScanResult(key, geodes.detect(level, chunk, key, minY, maxY), true, tick);
    }
}
