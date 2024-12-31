package net.minecraft.server.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEventListener;

public class ServerWorldManager implements WorldEventListener {
	private MinecraftServer server;
	private ServerWorld world;

	public ServerWorldManager(MinecraftServer minecraftServer, ServerWorld serverWorld) {
		this.server = minecraftServer;
		this.world = serverWorld;
	}

	@Override
	public void addParticle(int id, boolean bl, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... args) {
	}

	@Override
	public void onEntitySpawned(Entity entity) {
		this.world.getEntityTracker().startTracking(entity);
	}

	@Override
	public void onEntityRemoved(Entity entity) {
		this.world.getEntityTracker().method_2101(entity);
		this.world.getScoreboard().resetEntityScore(entity);
	}

	@Override
	public void playSound(String name, double x, double y, double z, float volume, float pitch) {
		this.server
			.getPlayerManager()
			.sendToAround(
				x, y, z, volume > 1.0F ? (double)(16.0F * volume) : 16.0, this.world.dimension.getType(), new PlaySoundIdS2CPacket(name, x, y, z, volume, pitch)
			);
	}

	@Override
	public void playSound(PlayerEntity except, String name, double x, double y, double z, float volume, float pitch) {
		this.server
			.getPlayerManager()
			.sendToAround(
				except, x, y, z, volume > 1.0F ? (double)(16.0F * volume) : 16.0, this.world.dimension.getType(), new PlaySoundIdS2CPacket(name, x, y, z, volume, pitch)
			);
	}

	@Override
	public void onRenderRegionUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
	}

	@Override
	public void onBlockUpdate(BlockPos pos) {
		this.world.getPlayerWorldManager().method_10748(pos);
	}

	@Override
	public void onLightUpdate(BlockPos pos) {
	}

	@Override
	public void playMusicDisc(String id, BlockPos pos) {
	}

	@Override
	public void processWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data) {
		this.server
			.getPlayerManager()
			.sendToAround(
				player,
				(double)pos.getX(),
				(double)pos.getY(),
				(double)pos.getZ(),
				64.0,
				this.world.dimension.getType(),
				new WorldEventS2CPacket(eventId, pos, data, false)
			);
	}

	@Override
	public void processGlobalEvent(int eventId, BlockPos pos, int j) {
		this.server.getPlayerManager().sendToAll(new WorldEventS2CPacket(eventId, pos, j, true));
	}

	@Override
	public void setBlockBreakInfo(int entityId, BlockPos pos, int progress) {
		for (ServerPlayerEntity serverPlayerEntity : this.server.getPlayerManager().getPlayers()) {
			if (serverPlayerEntity != null && serverPlayerEntity.world == this.world && serverPlayerEntity.getEntityId() != entityId) {
				double d = (double)pos.getX() - serverPlayerEntity.x;
				double e = (double)pos.getY() - serverPlayerEntity.y;
				double f = (double)pos.getZ() - serverPlayerEntity.z;
				if (d * d + e * e + f * f < 1024.0) {
					serverPlayerEntity.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(entityId, pos, progress));
				}
			}
		}
	}
}
