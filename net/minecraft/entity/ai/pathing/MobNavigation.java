package net.minecraft.entity.ai.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobNavigation extends EntityNavigation {
	private boolean avoidSunlight;

	public MobNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	protected PathNodeNavigator createNavigator() {
		this.field_14600 = new LandPathNodeMaker();
		this.field_14600.method_11916(true);
		return new PathNodeNavigator(this.field_14600);
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.mob.onGround || this.canSwim() && this.isInLiquid() || this.mob.hasMount();
	}

	@Override
	protected Vec3d getPos() {
		return new Vec3d(this.mob.x, (double)this.getPathfindingY(), this.mob.z);
	}

	@Override
	public PathMinHeap method_13108(BlockPos blockPos) {
		if (this.world.getBlockState(blockPos).getMaterial() == Material.AIR) {
			BlockPos blockPos2 = blockPos.down();

			while (blockPos2.getY() > 0 && this.world.getBlockState(blockPos2).getMaterial() == Material.AIR) {
				blockPos2 = blockPos2.down();
			}

			if (blockPos2.getY() > 0) {
				return super.method_13108(blockPos2.up());
			}

			while (blockPos2.getY() < this.world.getMaxBuildHeight() && this.world.getBlockState(blockPos2).getMaterial() == Material.AIR) {
				blockPos2 = blockPos2.up();
			}

			blockPos = blockPos2;
		}

		if (!this.world.getBlockState(blockPos).getMaterial().isSolid()) {
			return super.method_13108(blockPos);
		} else {
			BlockPos blockPos3 = blockPos.up();

			while (blockPos3.getY() < this.world.getMaxBuildHeight() && this.world.getBlockState(blockPos3).getMaterial().isSolid()) {
				blockPos3 = blockPos3.up();
			}

			return super.method_13108(blockPos3);
		}
	}

	@Override
	public PathMinHeap method_13109(Entity entity) {
		return this.method_13108(new BlockPos(entity));
	}

	private int getPathfindingY() {
		if (this.mob.isTouchingWater() && this.canSwim()) {
			int i = (int)this.mob.getBoundingBox().minY;
			Block block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.mob.x), i, MathHelper.floor(this.mob.z))).getBlock();
			int j = 0;

			while (block == Blocks.FLOWING_WATER || block == Blocks.WATER) {
				block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.mob.x), ++i, MathHelper.floor(this.mob.z))).getBlock();
				if (++j > 16) {
					return (int)this.mob.getBoundingBox().minY;
				}
			}

			return i;
		} else {
			return (int)(this.mob.getBoundingBox().minY + 0.5);
		}
	}

	@Override
	protected void adjustPath() {
		super.adjustPath();

		for (int i = 0; i < this.field_14599.method_11936(); i++) {
			PathNode pathNode = this.field_14599.method_11925(i);
			PathNode pathNode2 = i + 1 < this.field_14599.method_11936() ? this.field_14599.method_11925(i + 1) : null;
			BlockState blockState = this.world.getBlockState(new BlockPos(pathNode.posX, pathNode.posY, pathNode.posZ));
			Block block = blockState.getBlock();
			if (block == Blocks.CAULDRON) {
				this.field_14599.method_11926(i, pathNode.method_11907(pathNode.posX, pathNode.posY + 1, pathNode.posZ));
				if (pathNode2 != null && pathNode.posY >= pathNode2.posY) {
					this.field_14599.method_11926(i + 1, pathNode2.method_11907(pathNode2.posX, pathNode.posY + 1, pathNode2.posZ));
				}
			}
		}

		if (this.avoidSunlight) {
			if (this.world.hasDirectSunlight(new BlockPos(MathHelper.floor(this.mob.x), (int)(this.mob.getBoundingBox().minY + 0.5), MathHelper.floor(this.mob.z)))) {
				return;
			}

			for (int j = 0; j < this.field_14599.method_11936(); j++) {
				PathNode pathNode3 = this.field_14599.method_11925(j);
				if (this.world.hasDirectSunlight(new BlockPos(pathNode3.posX, pathNode3.posY, pathNode3.posZ))) {
					this.field_14599.method_11931(j - 1);
					return;
				}
			}
		}
	}

	@Override
	protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ) {
		int i = MathHelper.floor(origin.x);
		int j = MathHelper.floor(origin.z);
		double d = target.x - origin.x;
		double e = target.z - origin.z;
		double f = d * d + e * e;
		if (f < 1.0E-8) {
			return false;
		} else {
			double g = 1.0 / Math.sqrt(f);
			d *= g;
			e *= g;
			sizeX += 2;
			sizeZ += 2;
			if (!this.method_13106(i, (int)origin.y, j, sizeX, sizeY, sizeZ, origin, d, e)) {
				return false;
			} else {
				sizeX -= 2;
				sizeZ -= 2;
				double h = 1.0 / Math.abs(d);
				double k = 1.0 / Math.abs(e);
				double l = (double)i - origin.x;
				double m = (double)j - origin.z;
				if (d >= 0.0) {
					l++;
				}

				if (e >= 0.0) {
					m++;
				}

				l /= d;
				m /= e;
				int n = d < 0.0 ? -1 : 1;
				int o = e < 0.0 ? -1 : 1;
				int p = MathHelper.floor(target.x);
				int q = MathHelper.floor(target.z);
				int r = p - i;
				int s = q - j;

				while (r * n > 0 || s * o > 0) {
					if (l < m) {
						l += h;
						i += n;
						r = p - i;
					} else {
						m += k;
						j += o;
						s = q - j;
					}

					if (!this.method_13106(i, (int)origin.y, j, sizeX, sizeY, sizeZ, origin, d, e)) {
						return false;
					}
				}

				return true;
			}
		}
	}

	private boolean method_13106(int i, int j, int k, int l, int m, int n, Vec3d vec3d, double d, double e) {
		int o = i - l / 2;
		int p = k - n / 2;
		if (!this.allVisibleArePassable(o, j, p, l, m, n, vec3d, d, e)) {
			return false;
		} else {
			for (int q = o; q < o + l; q++) {
				for (int r = p; r < p + n; r++) {
					double f = (double)q + 0.5 - vec3d.x;
					double g = (double)r + 0.5 - vec3d.z;
					if (!(f * d + g * e < 0.0)) {
						LandType landType = this.field_14600.method_11914(this.world, q, j - 1, r, this.mob, l, m, n, true, true);
						if (landType == LandType.WATER) {
							return false;
						}

						if (landType == LandType.LAVA) {
							return false;
						}

						if (landType == LandType.OPEN) {
							return false;
						}

						landType = this.field_14600.method_11914(this.world, q, j, r, this.mob, l, m, n, true, true);
						float h = this.mob.method_13075(landType);
						if (h < 0.0F || h >= 8.0F) {
							return false;
						}

						if (landType == LandType.DAMAGE_FIRE || landType == LandType.DANGER_FIRE || landType == LandType.DAMAGE_OTHER) {
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	private boolean allVisibleArePassable(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d entityPos, double lookVecX, double lookVecZ) {
		for (BlockPos blockPos : BlockPos.iterate(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
			double d = (double)blockPos.getX() + 0.5 - entityPos.x;
			double e = (double)blockPos.getZ() + 0.5 - entityPos.z;
			if (!(d * lookVecX + e * lookVecZ < 0.0)) {
				Block block = this.world.getBlockState(blockPos).getBlock();
				if (!block.blocksMovement(this.world, blockPos)) {
					return false;
				}
			}
		}

		return true;
	}

	public void setCanPathThroughDoors(boolean value) {
		this.field_14600.method_11919(value);
	}

	public void setCanEnterOpenDoors(boolean value) {
		this.field_14600.method_11916(value);
	}

	public boolean canEnterOpenDoors() {
		return this.field_14600.method_11920();
	}

	public void setCanSwim(boolean value) {
		this.field_14600.method_11921(value);
	}

	public boolean canSwim() {
		return this.field_14600.method_11923();
	}

	public void setAvoidSunlight(boolean avoidSunlight) {
		this.avoidSunlight = avoidSunlight;
	}
}
