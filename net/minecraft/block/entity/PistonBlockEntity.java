package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class PistonBlockEntity extends BlockEntity implements Tickable {
	private BlockState pushedBlock;
	private Direction direction;
	private boolean extending;
	private boolean source;
	private float progress;
	private float lastProgress;

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
		return (float)this.direction.getOffsetX() * this.method_11703(this.getAmountExtended(progress));
	}

	public float getRenderOffsetY(float progress) {
		return (float)this.direction.getOffsetY() * this.method_11703(this.getAmountExtended(progress));
	}

	public float getRenderOffsetZ(float progress) {
		return (float)this.direction.getOffsetZ() * this.method_11703(this.getAmountExtended(progress));
	}

	private float method_11703(float f) {
		return this.extending ? f - 1.0F : 1.0F - f;
	}

	public Box method_11701(BlockView blockView, BlockPos blockPos) {
		return this.method_11702(blockView, blockPos, this.progress).union(this.method_11702(blockView, blockPos, this.lastProgress));
	}

	public Box method_11702(BlockView blockView, BlockPos blockPos, float f) {
		f = this.method_11703(f);
		return this.pushedBlock
			.getCollisionBox(blockView, blockPos)
			.offset((double)(f * (float)this.direction.getOffsetX()), (double)(f * (float)this.direction.getOffsetY()), (double)(f * (float)this.direction.getOffsetZ()));
	}

	private void method_11704() {
		Box box = this.method_11701(this.world, this.pos).offset(this.pos);
		List<Entity> list = this.world.getEntitiesIn(null, box);
		if (!list.isEmpty()) {
			Direction direction = this.extending ? this.direction : this.direction.getOpposite();

			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
					if (this.pushedBlock.getBlock() == Blocks.SLIME_BLOCK) {
						switch (direction.getAxis()) {
							case X:
								entity.velocityX = (double)direction.getOffsetX();
								break;
							case Y:
								entity.velocityY = (double)direction.getOffsetY();
								break;
							case Z:
								entity.velocityZ = (double)direction.getOffsetZ();
						}
					}

					double d = 0.0;
					double e = 0.0;
					double f = 0.0;
					Box box2 = entity.getBoundingBox();
					switch (direction.getAxis()) {
						case X:
							if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
								d = box.maxX - box2.minX;
							} else {
								d = box2.maxX - box.minX;
							}

							d += 0.01;
							break;
						case Y:
							if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
								e = box.maxY - box2.minY;
							} else {
								e = box2.maxY - box.minY;
							}

							e += 0.01;
							break;
						case Z:
							if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
								f = box.maxZ - box2.minZ;
							} else {
								f = box2.maxZ - box.minZ;
							}

							f += 0.01;
					}

					entity.move(d * (double)direction.getOffsetX(), e * (double)direction.getOffsetY(), f * (double)direction.getOffsetZ());
				}
			}
		}
	}

	public void finish() {
		if (this.lastProgress < 1.0F && this.world != null) {
			this.progress = 1.0F;
			this.lastProgress = this.progress;
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
			this.method_11704();
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

			this.method_11704();
		}
	}

	public static void registerDataFixes(DataFixerUpper dataFixer) {
	}

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.pushedBlock = Block.getById(nbt.getInt("blockId")).stateFromData(nbt.getInt("blockData"));
		this.direction = Direction.getById(nbt.getInt("facing"));
		this.progress = nbt.getFloat("progress");
		this.lastProgress = this.progress;
		this.extending = nbt.getBoolean("extending");
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putInt("blockId", Block.getIdByBlock(this.pushedBlock.getBlock()));
		nbt.putInt("blockData", this.pushedBlock.getBlock().getData(this.pushedBlock));
		nbt.putInt("facing", this.direction.getId());
		nbt.putFloat("progress", this.lastProgress);
		nbt.putBoolean("extending", this.extending);
		return nbt;
	}
}
