package net.minecraft.server;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.EndBiomeDecorator;
import net.minecraft.world.gen.feature.FillerBlockFeature;

public enum DragonRespawnAnimationStatus {
	START {
		@Override
		public void play(ServerWorld world, DragonRespawnAnimation battle, List<EndCrystalEntity> crystals, int animationTicks, BlockPos pos) {
			BlockPos blockPos = new BlockPos(0, 128, 0);

			for (EndCrystalEntity endCrystalEntity : crystals) {
				endCrystalEntity.setBeamTarget(blockPos);
			}

			battle.skipTo(PREPARING_TO_SUMMON_PILLARS);
		}
	},
	PREPARING_TO_SUMMON_PILLARS {
		@Override
		public void play(ServerWorld world, DragonRespawnAnimation battle, List<EndCrystalEntity> crystals, int animationTicks, BlockPos pos) {
			if (animationTicks < 100) {
				if (animationTicks == 0 || animationTicks == 50 || animationTicks == 51 || animationTicks == 52 || animationTicks >= 95) {
					world.syncGlobalEvent(3001, new BlockPos(0, 128, 0), 0);
				}
			} else {
				battle.skipTo(SUMMONING_PILLARS);
			}
		}
	},
	SUMMONING_PILLARS {
		@Override
		public void play(ServerWorld world, DragonRespawnAnimation battle, List<EndCrystalEntity> crystals, int animationTicks, BlockPos pos) {
			int i = 40;
			boolean bl = animationTicks % i == 0;
			boolean bl2 = animationTicks % i == i - 1;
			if (bl || bl2) {
				FillerBlockFeature.class_2756[] lvs = EndBiomeDecorator.method_11545(world);
				int j = animationTicks / i;
				if (j < lvs.length) {
					FillerBlockFeature.class_2756 lv = lvs[j];
					if (bl) {
						for (EndCrystalEntity endCrystalEntity : crystals) {
							endCrystalEntity.setBeamTarget(new BlockPos(lv.method_11826(), lv.method_11830() + 1, lv.method_11828()));
						}
					} else {
						int k = 10;

						for (BlockPos.Mutable mutable : BlockPos.mutableIterate(
							new BlockPos(lv.method_11826() - k, lv.method_11830() - k, lv.method_11828() - k),
							new BlockPos(lv.method_11826() + k, lv.method_11830() + k, lv.method_11828() + k)
						)) {
							world.setAir(mutable);
						}

						world.createExplosion(null, (double)((float)lv.method_11826() + 0.5F), (double)lv.method_11830(), (double)((float)lv.method_11828() + 0.5F), 5.0F, true);
						FillerBlockFeature fillerBlockFeature = new FillerBlockFeature();
						fillerBlockFeature.method_11823(lv);
						fillerBlockFeature.method_11825(true);
						fillerBlockFeature.method_11824(new BlockPos(0, 128, 0));
						fillerBlockFeature.generate(world, new Random(), new BlockPos(lv.method_11826(), 45, lv.method_11828()));
					}
				} else if (bl) {
					battle.skipTo(SUMMONING_DRAGON);
				}
			}
		}
	},
	SUMMONING_DRAGON {
		@Override
		public void play(ServerWorld world, DragonRespawnAnimation battle, List<EndCrystalEntity> crystals, int animationTicks, BlockPos pos) {
			if (animationTicks >= 100) {
				battle.skipTo(END);
				battle.method_11810();

				for (EndCrystalEntity endCrystalEntity : crystals) {
					endCrystalEntity.setBeamTarget(null);
					world.createExplosion(endCrystalEntity, endCrystalEntity.x, endCrystalEntity.y, endCrystalEntity.z, 6.0F, false);
					endCrystalEntity.remove();
				}
			} else if (animationTicks >= 80) {
				world.syncGlobalEvent(3001, new BlockPos(0, 128, 0), 0);
			} else if (animationTicks == 0) {
				for (EndCrystalEntity endCrystalEntity2 : crystals) {
					endCrystalEntity2.setBeamTarget(new BlockPos(0, 128, 0));
				}
			} else if (animationTicks < 5) {
				world.syncGlobalEvent(3001, new BlockPos(0, 128, 0), 0);
			}
		}
	},
	END {
		@Override
		public void play(ServerWorld world, DragonRespawnAnimation battle, List<EndCrystalEntity> crystals, int animationTicks, BlockPos pos) {
		}
	};

	private DragonRespawnAnimationStatus() {
	}

	public abstract void play(ServerWorld world, DragonRespawnAnimation battle, List<EndCrystalEntity> crystals, int animationTicks, BlockPos pos);
}
