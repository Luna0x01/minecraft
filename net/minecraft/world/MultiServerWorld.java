package net.minecraft.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.VillageState;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ReadOnlyLevelProperties;

public class MultiServerWorld extends ServerWorld {
	public MultiServerWorld(MinecraftServer minecraftServer, SaveHandler saveHandler, DimensionType dimensionType, ServerWorld serverWorld, Profiler profiler) {
		super(minecraftServer, saveHandler, serverWorld.method_16399(), new ReadOnlyLevelProperties(serverWorld.method_3588()), dimensionType, profiler);
		serverWorld.method_8524().addListener(new WorldBorderListener() {
			@Override
			public void onSizeChange(WorldBorder border, double newSize) {
				MultiServerWorld.this.method_8524().setSize(newSize);
			}

			@Override
			public void onInterpolateSize(WorldBorder border, double oldSize, double targetSize, long time) {
				MultiServerWorld.this.method_8524().interpolateSize(oldSize, targetSize, time);
			}

			@Override
			public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
				MultiServerWorld.this.method_8524().setCenter(centerX, centerZ);
			}

			@Override
			public void onWarningTimeChanged(WorldBorder border, int newTime) {
				MultiServerWorld.this.method_8524().setWarningTime(newTime);
			}

			@Override
			public void onWarningBlocksChanged(WorldBorder border, int warningBlocks) {
				MultiServerWorld.this.method_8524().setWarningBlocks(warningBlocks);
			}

			@Override
			public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
				MultiServerWorld.this.method_8524().setDamagePerBlock(damagePerBlock);
			}

			@Override
			public void onSafeZoneChanged(WorldBorder border, double safeZone) {
				MultiServerWorld.this.method_8524().setSafeZone(safeZone);
			}
		});
	}

	@Override
	protected void method_2132() {
	}

	public MultiServerWorld method_21265() {
		String string = VillageState.getId(this.dimension);
		VillageState villageState = this.method_16398(DimensionType.OVERWORLD, VillageState::new, string);
		if (villageState == null) {
			this.villageState = new VillageState(this);
			this.method_16397(DimensionType.OVERWORLD, string, this.villageState);
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
