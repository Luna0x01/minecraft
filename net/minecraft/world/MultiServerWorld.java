package net.minecraft.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.VillageState;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.level.ReadOnlyLevelProperties;

public class MultiServerWorld extends ServerWorld {
	private ServerWorld world;

	public MultiServerWorld(MinecraftServer minecraftServer, SaveHandler saveHandler, int i, ServerWorld serverWorld, Profiler profiler) {
		super(minecraftServer, saveHandler, new ReadOnlyLevelProperties(serverWorld.getLevelProperties()), i, profiler);
		this.world = serverWorld;
		serverWorld.getWorldBorder().addListener(new WorldBorderListener() {
			@Override
			public void onSizeChange(WorldBorder border, double newSize) {
				MultiServerWorld.this.getWorldBorder().setSize(newSize);
			}

			@Override
			public void onInterpolateSize(WorldBorder border, double oldSize, double targetSize, long time) {
				MultiServerWorld.this.getWorldBorder().interpolateSize(oldSize, targetSize, time);
			}

			@Override
			public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
				MultiServerWorld.this.getWorldBorder().setCenter(centerX, centerZ);
			}

			@Override
			public void onWarningTimeChanged(WorldBorder border, int newTime) {
				MultiServerWorld.this.getWorldBorder().setWarningTime(newTime);
			}

			@Override
			public void onWarningBlocksChanged(WorldBorder border, int warningBlocks) {
				MultiServerWorld.this.getWorldBorder().setWarningBlocks(warningBlocks);
			}

			@Override
			public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
				MultiServerWorld.this.getWorldBorder().setDamagePerBlock(damagePerBlock);
			}

			@Override
			public void onSafeZoneChanged(WorldBorder border, double safeZone) {
				MultiServerWorld.this.getWorldBorder().setSafeZone(safeZone);
			}
		});
	}

	@Override
	protected void method_2132() {
	}

	@Override
	public World getWorld() {
		this.persistentStateManager = this.world.getPersistentStateManager();
		this.scoreboard = this.world.getScoreboard();
		this.field_12435 = this.world.method_11487();
		String string = VillageState.getId(this.dimension);
		VillageState villageState = (VillageState)this.persistentStateManager.getOrCreate(VillageState.class, string);
		if (villageState == null) {
			this.villageState = new VillageState(this);
			this.persistentStateManager.replace(string, this.villageState);
		} else {
			this.villageState = villageState;
			this.villageState.setWorld(this);
		}

		return this;
	}

	public void method_12763() {
		this.dimension.method_11790();
	}
}
