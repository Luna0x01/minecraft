package net.minecraft.server.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.Sound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldEventListener;

public class ServerWorldManager implements WorldEventListener {
	private final MinecraftServer server;
	private final ServerWorld world;

	public ServerWorldManager(MinecraftServer minecraftServer, ServerWorld serverWorld) {
		this.server = minecraftServer;
		this.world = serverWorld;
	}

	@Override
	public void method_3746(ParticleEffect particleEffect, boolean bl, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public void method_13696(ParticleEffect particleEffect, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public void onEntitySpawned(Entity entity) {
		this.world.getEntityTracker().startTracking(entity);
		if (entity instanceof ServerPlayerEntity) {
			this.world.dimension.method_11786((ServerPlayerEntity)entity);
		}
	}

	@Override
	public void onEntityRemoved(Entity entity) {
		this.world.getEntityTracker().method_2101(entity);
		this.world.getScoreboard().resetEntityScore(entity);
		if (entity instanceof ServerPlayerEntity) {
			this.world.dimension.method_11787((ServerPlayerEntity)entity);
		}
	}

	@Override
	public void method_3747(@Nullable PlayerEntity playerEntity, Sound sound, SoundCategory soundCategory, double d, double e, double f, float g, float h) {
		this.server
			.getPlayerManager()
			.method_12828(
				playerEntity,
				d,
				e,
				f,
				g > 1.0F ? (double)(16.0F * g) : 16.0,
				this.world.dimension.method_11789(),
				new PlaySoundIdS2CPacket(sound, soundCategory, d, e, f, g, h)
			);
	}

	@Override
	public void onRenderRegionUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
	}

	@Override
	public void method_11493(BlockView blockView, BlockPos blockPos, BlockState blockState, BlockState blockState2, int i) {
		this.world.getPlayerWorldManager().method_10748(blockPos);
	}

	@Override
	public void onLightUpdate(BlockPos pos) {
	}

	@Override
	public void method_8572(Sound sound, BlockPos blockPos) {
	}

	@Override
	public void processWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data) {
		this.server
			.getPlayerManager()
			.method_12828(
				player,
				(double)pos.getX(),
				(double)pos.getY(),
				(double)pos.getZ(),
				64.0,
				this.world.dimension.method_11789(),
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
