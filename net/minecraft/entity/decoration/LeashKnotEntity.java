package net.minecraft.entity.decoration;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.FenceBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.Sounds;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
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
		this.teleporting = true;
	}

	@Override
	public void updatePosition(double x, double y, double z) {
		super.updatePosition((double)MathHelper.floor(x) + 0.5, (double)MathHelper.floor(y) + 0.5, (double)MathHelper.floor(z) + 0.5);
	}

	@Override
	protected void updateAttachmentPosition() {
		this.x = (double)this.pos.getX() + 0.5;
		this.y = (double)this.pos.getY() + 0.5;
		this.z = (double)this.pos.getZ() + 0.5;
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
	public void onBreak(@Nullable Entity entity) {
		this.playSound(Sounds.ENTITY_LEASHKNOT_BREAK, 1.0F, 1.0F);
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
	public boolean interact(PlayerEntity player, Hand hand) {
		if (this.world.isClient) {
			return true;
		} else {
			boolean bl = false;
			double d = 7.0;
			List<MobEntity> list = this.world
				.getEntitiesInBox(MobEntity.class, new Box(this.x - 7.0, this.y - 7.0, this.z - 7.0, this.x + 7.0, this.y + 7.0, this.z + 7.0));

			for (MobEntity mobEntity : list) {
				if (mobEntity.isLeashed() && mobEntity.getLeashOwner() == player) {
					mobEntity.attachLeash(this, true);
					bl = true;
				}
			}

			if (!bl) {
				this.remove();
				if (player.abilities.creativeMode) {
					for (MobEntity mobEntity2 : list) {
						if (mobEntity2.isLeashed() && mobEntity2.getLeashOwner() == this) {
							mobEntity2.detachLeash(true, false);
						}
					}
				}
			}

			return true;
		}
	}

	@Override
	public boolean isPosValid() {
		return this.world.getBlockState(this.pos).getBlock() instanceof FenceBlock;
	}

	public static LeashKnotEntity create(World world, BlockPos pos) {
		LeashKnotEntity leashKnotEntity = new LeashKnotEntity(world, pos);
		world.spawnEntity(leashKnotEntity);
		leashKnotEntity.onPlace();
		return leashKnotEntity;
	}

	@Nullable
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

	@Override
	public void onPlace() {
		this.playSound(Sounds.ENTITY_LEASHKNOT_PLACE, 1.0F, 1.0F);
	}
}
