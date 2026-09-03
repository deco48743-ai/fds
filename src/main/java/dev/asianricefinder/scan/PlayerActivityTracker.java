package dev.asianricefinder.scan;

import dev.asianricefinder.data.ChunkKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Samples visible player movement; it has no packet or server-side inputs. */
public final class PlayerActivityTracker {
    private final Map<UUID, Sample> samples = new HashMap<>();

    public void sample(ClientWorld level, long tick) {
        samples.keySet().removeIf(id -> level.getPlayerByUuid(id) == null);
        for (PlayerEntity player : level.getPlayers()) {
            if (player == MinecraftClient.getInstance().player) continue;
            Vec3d position = player.getPos();
            Sample old = samples.get(player.getUUID());
            boolean moving = old != null && old.position.squaredDistanceTo(position) > 1.0 && tick - old.tick <= 40;
            samples.put(player.getUuid(), new Sample(position, tick, moving));
        }
    }

    public Activity activityNear(ClientWorld level, ChunkKey key, int radius) {
        double x = key.minBlockX() + 8.0, z = key.minBlockZ() + 8.0;
        int players = 0, moving = 0;
        double radiusSquared = (double) radius * radius;
        for (PlayerEntity player : level.getPlayers()) {
            if (player == MinecraftClient.getInstance().player) continue;
            double dx = player.getX() - x, dz = player.getZ() - z;
            if (dx * dx + dz * dz > radiusSquared) continue;
            players++;
            Sample sample = samples.get(player.getUuid());
            if (sample != null && sample.moving) moving++;
        }
        return new Activity(players, players * 3 + moving * 3);
    }

    public void clear() { samples.clear(); }
    public record Activity(int players, int score) { }
    private record Sample(Vec3d position, long tick, boolean moving) { }
}
