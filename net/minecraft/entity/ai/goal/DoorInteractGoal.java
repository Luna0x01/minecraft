package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

public abstract class DoorInteractGoal extends Goal {
	protected MobEntity mob;
	protected BlockPos pos = BlockPos.ORIGIN;
	protected DoorBlock doorBlock;
	boolean shouldStop;
	float xOffset;
	float zOffset;

	public DoorInteractGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		if (!(mobEntity.getNavigation() instanceof MobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
		}
	}

	@Override
	public boolean canStart() {
		if (!this.mob.horizontalCollision) {
			return false;
		} else {
			MobNavigation mobNavigation = (MobNavigation)this.mob.getNavigation();
			Path path = mobNavigation.getCurrentPath();
			if (path != null && !path.isFinished() && mobNavigation.canEnterOpenDoors()) {
				for (int i = 0; i < Math.min(path.getCurrentNode() + 2, path.getNodeCount()); i++) {
					PathNode pathNode = path.getNode(i);
					this.pos = new BlockPos(pathNode.posX, pathNode.posY + 1, pathNode.posZ);
					if (!(this.mob.squaredDistanceTo((double)this.pos.getX(), this.mob.y, (double)this.pos.getZ()) > 2.25)) {
						this.doorBlock = this.getDoorAt(this.pos);
						if (this.doorBlock != null) {
							return true;
						}
					}
				}

				this.pos = new BlockPos(this.mob).up();
				this.doorBlock = this.getDoorAt(this.pos);
				return this.doorBlock != null;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.shouldStop;
	}

	@Override
	public void start() {
		this.shouldStop = false;
		this.xOffset = (float)((double)((float)this.pos.getX() + 0.5F) - this.mob.x);
		this.zOffset = (float)((double)((float)this.pos.getZ() + 0.5F) - this.mob.z);
	}

	@Override
	public void tick() {
		float f = (float)((double)((float)this.pos.getX() + 0.5F) - this.mob.x);
		float g = (float)((double)((float)this.pos.getZ() + 0.5F) - this.mob.z);
		float h = this.xOffset * f + this.zOffset * g;
		if (h < 0.0F) {
			this.shouldStop = true;
		}
	}

	private DoorBlock getDoorAt(BlockPos pos) {
		Block block = this.mob.world.getBlockState(pos).getBlock();
		return block instanceof DoorBlock && block.getMaterial() == Material.WOOD ? (DoorBlock)block : null;
	}
}
