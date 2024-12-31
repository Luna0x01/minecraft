package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityLocations;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ZombieSiegeManager {
	private final World world;
	private boolean spawned;
	private int field_3684 = -1;
	private int field_3685;
	private int field_3686;
	private Village village;
	private int field_3688;
	private int field_3689;
	private int field_3690;

	public ZombieSiegeManager(World world) {
		this.world = world;
	}

	public void method_2835() {
		if (this.world.isDay()) {
			this.field_3684 = 0;
		} else if (this.field_3684 != 2) {
			if (this.field_3684 == 0) {
				float f = this.world.method_16349(0.0F);
				if ((double)f < 0.5 || (double)f > 0.501) {
					return;
				}

				this.field_3684 = this.world.random.nextInt(10) == 0 ? 1 : 2;
				this.spawned = false;
				if (this.field_3684 == 2) {
					return;
				}
			}

			if (this.field_3684 != -1) {
				if (!this.spawned) {
					if (!this.method_2837()) {
						return;
					}

					this.spawned = true;
				}

				if (this.field_3686 > 0) {
					this.field_3686--;
				} else {
					this.field_3686 = 2;
					if (this.field_3685 > 0) {
						this.method_2838();
						this.field_3685--;
					} else {
						this.field_3684 = 2;
					}
				}
			}
		}
	}

	private boolean method_2837() {
		for (PlayerEntity playerEntity : this.world.playerEntities) {
			if (!playerEntity.isSpectator()) {
				this.village = this.world.getVillageState().method_11062(new BlockPos(playerEntity), 1);
				if (this.village != null && this.village.getDoorsAmount() >= 10 && this.village.method_2824() >= 20 && this.village.getPopulationSize() >= 20) {
					BlockPos blockPos = this.village.getMinPos();
					float f = (float)this.village.getRadius();
					boolean bl = false;

					for (int i = 0; i < 10; i++) {
						float g = this.world.random.nextFloat() * (float) (Math.PI * 2);
						this.field_3688 = blockPos.getX() + (int)((double)(MathHelper.cos(g) * f) * 0.9);
						this.field_3689 = blockPos.getY();
						this.field_3690 = blockPos.getZ() + (int)((double)(MathHelper.sin(g) * f) * 0.9);
						bl = false;

						for (Village village : this.world.getVillageState().method_2843()) {
							if (village != this.village && village.method_11052(new BlockPos(this.field_3688, this.field_3689, this.field_3690))) {
								bl = true;
								break;
							}
						}

						if (!bl) {
							break;
						}
					}

					if (bl) {
						return false;
					}

					Vec3d vec3d = this.method_11059(new BlockPos(this.field_3688, this.field_3689, this.field_3690));
					if (vec3d != null) {
						this.field_3686 = 0;
						this.field_3685 = 20;
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean method_2838() {
		Vec3d vec3d = this.method_11059(new BlockPos(this.field_3688, this.field_3689, this.field_3690));
		if (vec3d == null) {
			return false;
		} else {
			ZombieEntity zombieEntity;
			try {
				zombieEntity = new ZombieEntity(this.world);
				zombieEntity.initialize(this.world.method_8482(new BlockPos(zombieEntity)), null, null);
			} catch (Exception var4) {
				var4.printStackTrace();
				return false;
			}

			zombieEntity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, this.world.random.nextFloat() * 360.0F, 0.0F);
			this.world.method_3686(zombieEntity);
			BlockPos blockPos = this.village.getMinPos();
			zombieEntity.setPositionTarget(blockPos, this.village.getRadius());
			return true;
		}
	}

	@Nullable
	private Vec3d method_11059(BlockPos pos) {
		for (int i = 0; i < 10; i++) {
			BlockPos blockPos = pos.add(this.world.random.nextInt(16) - 8, this.world.random.nextInt(6) - 3, this.world.random.nextInt(16) - 8);
			if (this.village.method_11052(blockPos) && MobSpawnerHelper.method_16404(EntityLocations.class_3464.ON_GROUND, this.world, blockPos, null)) {
				return new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
			}
		}

		return null;
	}
}
