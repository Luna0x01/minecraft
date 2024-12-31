package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class PistonBlockEntity extends BlockEntity implements Tickable {
	private BlockState pushedBlock;
	private Direction direction;
	private boolean extending;
	private boolean source;
	private float progress;
	private float lastProgress;
	private List<Entity> affectedEntities = Lists.newArrayList();

	public PistonBlockEntity() {
	}

	public PistonBlockEntity(BlockState blockState, Direction direction, boolean bl, boolean bl2) {
		this.pushedBlock = blockState;
		this.direction = direction;
		this.extending = bl;
		this.source = bl2;
	}

	public BlockState getPushedBlock() {
		return this.pushedBlock;
	}

	@Override
	public int getDataValue() {
		return 0;
	}

	public boolean isExtending() {
		return this.extending;
	}

	public Direction getFacing() {
		return this.direction;
	}

	public boolean isSource() {
		return this.source;
	}

	public float getAmountExtended(float progress) {
		if (progress > 1.0F) {
			progress = 1.0F;
		}

		return this.lastProgress + (this.progress - this.lastProgress) * progress;
	}

	public float getRenderOffsetX(float progress) {
		return this.extending
			? (this.getAmountExtended(progress) - 1.0F) * (float)this.direction.getOffsetX()
			: (1.0F - this.getAmountExtended(progress)) * (float)this.direction.getOffsetX();
	}

	public float getRenderOffsetY(float progress) {
		return this.extending
			? (this.getAmountExtended(progress) - 1.0F) * (float)this.direction.getOffsetY()
			: (1.0F - this.getAmountExtended(progress)) * (float)this.direction.getOffsetY();
	}

	public float getRenderOffsetZ(float progress) {
		return this.extending
			? (this.getAmountExtended(progress) - 1.0F) * (float)this.direction.getOffsetZ()
			: (1.0F - this.getAmountExtended(progress)) * (float)this.direction.getOffsetZ();
	}

	private void pushEntities(float progress, float distance) {
		if (this.extending) {
			progress = 1.0F - progress;
		} else {
			progress--;
		}

		Box box = Blocks.PISTON_EXTENSION.getCollisionBox(this.world, this.pos, this.pushedBlock, progress, this.direction);
		if (box != null) {
			List<Entity> list = this.world.getEntitiesIn(null, box);
			if (!list.isEmpty()) {
				this.affectedEntities.addAll(list);

				for (Entity entity : this.affectedEntities) {
					if (this.pushedBlock.getBlock() == Blocks.SLIME_BLOCK && this.extending) {
						switch (this.direction.getAxis()) {
							case X:
								entity.velocityX = (double)this.direction.getOffsetX();
								break;
							case Y:
								entity.velocityY = (double)this.direction.getOffsetY();
								break;
							case Z:
								entity.velocityZ = (double)this.direction.getOffsetZ();
						}
					} else {
						entity.move(
							(double)(distance * (float)this.direction.getOffsetX()),
							(double)(distance * (float)this.direction.getOffsetY()),
							(double)(distance * (float)this.direction.getOffsetZ())
						);
					}
				}

				this.affectedEntities.clear();
			}
		}
	}

	public void finish() {
		if (this.lastProgress < 1.0F && this.world != null) {
			this.lastProgress = this.progress = 1.0F;
			this.world.removeBlockEntity(this.pos);
			this.markRemoved();
			if (this.world.getBlockState(this.pos).getBlock() == Blocks.PISTON_EXTENSION) {
				this.world.setBlockState(this.pos, this.pushedBlock, 3);
				this.world.neighbourUpdate(this.pos, this.pushedBlock.getBlock());
			}
		}
	}

	@Override
	public void tick() {
		this.lastProgress = this.progress;
		if (this.lastProgress >= 1.0F) {
			this.pushEntities(1.0F, 0.25F);
			this.world.removeBlockEntity(this.pos);
			this.markRemoved();
			if (this.world.getBlockState(this.pos).getBlock() == Blocks.PISTON_EXTENSION) {
				this.world.setBlockState(this.pos, this.pushedBlock, 3);
				this.world.neighbourUpdate(this.pos, this.pushedBlock.getBlock());
			}
		} else {
			this.progress += 0.5F;
			if (this.progress >= 1.0F) {
				this.progress = 1.0F;
			}

			if (this.extending) {
				this.pushEntities(this.progress, this.progress - this.lastProgress + 0.0625F);
			}
		}
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.pushedBlock = Block.getById(nbt.getInt("blockId")).stateFromData(nbt.getInt("blockData"));
		this.direction = Direction.getById(nbt.getInt("facing"));
		this.lastProgress = this.progress = nbt.getFloat("progress");
		this.extending = nbt.getBoolean("extending");
	}

	@Override
	public void toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putInt("blockId", Block.getIdByBlock(this.pushedBlock.getBlock()));
		nbt.putInt("blockData", this.pushedBlock.getBlock().getData(this.pushedBlock));
		nbt.putInt("facing", this.direction.getId());
		nbt.putFloat("progress", this.lastProgress);
		nbt.putBoolean("extending", this.extending);
	}
}
