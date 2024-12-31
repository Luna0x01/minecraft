package net.minecraft.entity.ai.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobNavigation extends EntityNavigation {
	protected LandPathNodeMaker nodeMaker;
	private boolean avoidSunlight;

	public MobNavigation(MobEntity mobEntity, World world) {
		super(mobEntity, world);
	}

	@Override
	protected PathNodeNavigator createNavigator() {
		this.nodeMaker = new LandPathNodeMaker();
		this.nodeMaker.setCanEnterOpenDoors(true);
		return new PathNodeNavigator(this.nodeMaker);
	}

	@Override
	protected boolean isAtValidPosition() {
		return this.mob.onGround
			|| this.canSwim() && this.isInLiquid()
			|| this.mob.hasVehicle() && this.mob instanceof ZombieEntity && this.mob.vehicle instanceof ChickenEntity;
	}

	@Override
	protected Vec3d getPos() {
		return new Vec3d(this.mob.x, (double)this.getPathfindingY(), this.mob.z);
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
		if (this.avoidSunlight) {
			if (this.world.hasDirectSunlight(new BlockPos(MathHelper.floor(this.mob.x), (int)(this.mob.getBoundingBox().minY + 0.5), MathHelper.floor(this.mob.z)))) {
				return;
			}

			for (int i = 0; i < this.currentPath.getNodeCount(); i++) {
				PathNode pathNode = this.currentPath.getNode(i);
				if (this.world.hasDirectSunlight(new BlockPos(pathNode.posX, pathNode.posY, pathNode.posZ))) {
					this.currentPath.setNodeCount(i - 1);
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
			if (!this.allVisibleAreSafe(i, (int)origin.y, j, sizeX, sizeY, sizeZ, origin, d, e)) {
				return false;
			} else {
				sizeX -= 2;
				sizeZ -= 2;
				double h = 1.0 / Math.abs(d);
				double k = 1.0 / Math.abs(e);
				double l = (double)(i * 1) - origin.x;
				double m = (double)(j * 1) - origin.z;
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

					if (!this.allVisibleAreSafe(i, (int)origin.y, j, sizeX, sizeY, sizeZ, origin, d, e)) {
						return false;
					}
				}

				return true;
			}
		}
	}

	private boolean allVisibleAreSafe(int centerX, int centerY, int centerZ, int sizeX, int sizeY, int sizeZ, Vec3d entityPos, double lookVecX, double lookVecZ) {
		int i = centerX - sizeX / 2;
		int j = centerZ - sizeZ / 2;
		if (!this.allVisibleArePassable(i, centerY, j, sizeX, sizeY, sizeZ, entityPos, lookVecX, lookVecZ)) {
			return false;
		} else {
			for (int k = i; k < i + sizeX; k++) {
				for (int l = j; l < j + sizeZ; l++) {
					double d = (double)k + 0.5 - entityPos.x;
					double e = (double)l + 0.5 - entityPos.z;
					if (!(d * lookVecX + e * lookVecZ < 0.0)) {
						Block block = this.world.getBlockState(new BlockPos(k, centerY - 1, l)).getBlock();
						Material material = block.getMaterial();
						if (material == Material.AIR) {
							return false;
						}

						if (material == Material.WATER && !this.mob.isTouchingWater()) {
							return false;
						}

						if (material == Material.LAVA) {
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

	public void method_11027(boolean value) {
		this.nodeMaker.method_9300(value);
	}

	public boolean method_11032() {
		return this.nodeMaker.method_9303();
	}

	public void setCanPathThroughDoors(boolean value) {
		this.nodeMaker.setCanOpenDoors(value);
	}

	public void setCanEnterOpenDoors(boolean value) {
		this.nodeMaker.setCanEnterOpenDoors(value);
	}

	public boolean canEnterOpenDoors() {
		return this.nodeMaker.canEnterOpenDoors();
	}

	public void setCanSwim(boolean value) {
		this.nodeMaker.setCanSwim(value);
	}

	public boolean canSwim() {
		return this.nodeMaker.canSwim();
	}

	public void setAvoidSunlight(boolean avoidSunlight) {
		this.avoidSunlight = avoidSunlight;
	}
}
