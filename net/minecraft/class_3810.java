package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class class_3810 {
	private int field_19061;

	public int method_17278(World world, boolean bl, boolean bl2) {
		if (!bl) {
			return 0;
		} else {
			Random random = world.random;
			this.field_19061--;
			if (this.field_19061 > 0) {
				return 0;
			} else {
				this.field_19061 = this.field_19061 + (60 + random.nextInt(60)) * 20;
				if (world.method_8520() < 5 && world.dimension.isOverworld()) {
					return 0;
				} else {
					int i = 0;

					for (PlayerEntity playerEntity : world.playerEntities) {
						if (!playerEntity.isSpectator()) {
							BlockPos blockPos = new BlockPos(playerEntity);
							if (!world.dimension.isOverworld() || blockPos.getY() >= world.method_8483() && world.method_8555(blockPos)) {
								LocalDifficulty localDifficulty = world.method_8482(blockPos);
								if (localDifficulty.method_15040(random.nextFloat() * 3.0F)) {
									ServerStatHandler serverStatHandler = ((ServerPlayerEntity)playerEntity).getStatHandler();
									int j = MathHelper.clamp(serverStatHandler.method_21434(Stats.CUSTOM.method_21429(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
									int k = 24000;
									if (random.nextInt(j) >= 72000) {
										BlockPos blockPos2 = blockPos.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
										BlockState blockState = world.getBlockState(blockPos2);
										FluidState fluidState = world.getFluidState(blockPos2);
										if (MobSpawnerHelper.method_16407(blockState, fluidState)) {
											EntityData entityData = null;
											int l = 1 + random.nextInt(localDifficulty.method_15539().getId() + 1);

											for (int m = 0; m < l; m++) {
												PhantomEntity phantomEntity = new PhantomEntity(world);
												phantomEntity.refreshPositionAndAngles(blockPos2, 0.0F, 0.0F);
												entityData = phantomEntity.initialize(localDifficulty, entityData, null);
												world.method_3686(phantomEntity);
											}

											i += l;
										}
									}
								}
							}
						}
					}

					return i;
				}
			}
		}
	}
}
