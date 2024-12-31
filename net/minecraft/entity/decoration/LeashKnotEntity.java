package net.minecraft.entity.decoration;

import net.minecraft.block.FenceBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LeashKnotEntity extends AbstractDecorationEntity {
	public LeashKnotEntity(World world) {
		super(world);
	}

	public LeashKnotEntity(World world, BlockPos blockPos) {
		super(world, blockPos);
		this.updatePosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
		float f = 0.125F;
		float g = 0.1875F;
		float h = 0.25F;
		this.setBoundingBox(new Box(this.x - 0.1875, this.y - 0.25 + 0.125, this.z - 0.1875, this.x + 0.1875, this.y + 0.25 + 0.125, this.z + 0.1875));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
	}

	@Override
	public void setDirection(Direction direction) {
	}

	@Override
	public int getWidth() {
		return 9;
	}

	@Override
	public int getHeight() {
		return 9;
	}

	@Override
	public float getEyeHeight() {
		return -0.0625F;
	}

	@Override
	public boolean shouldRender(double distance) {
		return distance < 1024.0;
	}

	@Override
	public void onBreak(Entity entity) {
	}

	@Override
	public boolean saveToNbt(NbtCompound nbt) {
		return false;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	public boolean openInventory(PlayerEntity player) {
		ItemStack itemStack = player.getStackInHand();
		boolean bl = false;
		if (itemStack != null && itemStack.getItem() == Items.LEAD && !this.world.isClient) {
			double d = 7.0;

			for (MobEntity mobEntity : this.world.getEntitiesInBox(MobEntity.class, new Box(this.x - d, this.y - d, this.z - d, this.x + d, this.y + d, this.z + d))) {
				if (mobEntity.isLeashed() && mobEntity.getLeashOwner() == player) {
					mobEntity.attachLeash(this, true);
					bl = true;
				}
			}
		}

		if (!this.world.isClient && !bl) {
			this.remove();
			if (player.abilities.creativeMode) {
				double e = 7.0;

				for (MobEntity mobEntity2 : this.world.getEntitiesInBox(MobEntity.class, new Box(this.x - e, this.y - e, this.z - e, this.x + e, this.y + e, this.z + e))) {
					if (mobEntity2.isLeashed() && mobEntity2.getLeashOwner() == this) {
						mobEntity2.detachLeash(true, false);
					}
				}
			}
		}

		return true;
	}

	@Override
	public boolean isPosValid() {
		return this.world.getBlockState(this.pos).getBlock() instanceof FenceBlock;
	}

	public static LeashKnotEntity create(World world, BlockPos pos) {
		LeashKnotEntity leashKnotEntity = new LeashKnotEntity(world, pos);
		leashKnotEntity.teleporting = true;
		world.spawnEntity(leashKnotEntity);
		return leashKnotEntity;
	}

	public static LeashKnotEntity getOrCreate(World world, BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();

		for (LeashKnotEntity leashKnotEntity : world.getEntitiesInBox(
			LeashKnotEntity.class, new Box((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)
		)) {
			if (leashKnotEntity.getTilePos().equals(pos)) {
				return leashKnotEntity;
			}
		}

		return null;
	}
}
