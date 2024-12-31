package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.datafixer.DataFixerUpper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonBlockEntity extends BlockEntity implements Tickable {
	private BlockState pushedBlock;
	private Direction direction;
	private boolean extending;
	private boolean source;
	private static final ThreadLocal<Direction> field_15180 = new ThreadLocal<Direction>() {
		protected Direction initialValue() {
			return null;
		}
	};
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
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
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
		BlockState blockState = this.method_13759();
		return blockState.getCollisionBox(blockView, blockPos)
			.offset((double)(f * (float)this.direction.getOffsetX()), (double)(f * (float)this.direction.getOffsetY()), (double)(f * (float)this.direction.getOffsetZ()));
	}

	private BlockState method_13759() {
		return !this.isExtending() && this.isSource()
			? Blocks.PISTON_HEAD
				.getDefaultState()
				.with(
					PistonHeadBlock.TYPE, this.pushedBlock.getBlock() == Blocks.STICKY_PISTON ? PistonHeadBlock.PistonHeadType.STICKY : PistonHeadBlock.PistonHeadType.DEFAULT
				)
				.with(PistonHeadBlock.FACING, this.pushedBlock.get(PistonBlock.FACING))
			: this.pushedBlock;
	}

	private void method_13758(float f) {
		Direction direction = this.extending ? this.direction : this.direction.getOpposite();
		double d = (double)(f - this.progress);
		List<Box> list = Lists.newArrayList();
		this.method_13759().appendCollisionBoxes(this.world, BlockPos.ORIGIN, new Box(BlockPos.ORIGIN), list, null, true);
		if (!list.isEmpty()) {
			Box box = this.method_13750(this.method_13753(list));
			List<Entity> list2 = this.world.getEntitiesIn(null, this.method_13751(box, direction, d).union(box));
			if (!list2.isEmpty()) {
				boolean bl = this.pushedBlock.getBlock() == Blocks.SLIME_BLOCK;

				for (int i = 0; i < list2.size(); i++) {
					Entity entity = (Entity)list2.get(i);
					if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
						if (bl) {
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

						double e = 0.0;

						for (int j = 0; j < list.size(); j++) {
							Box box2 = this.method_13751(this.method_13750((Box)list.get(j)), direction, d);
							Box box3 = entity.getBoundingBox();
							if (box2.intersects(box3)) {
								e = Math.max(e, this.method_13752(box2, direction, box3));
								if (e >= d) {
									break;
								}
							}
						}

						if (!(e <= 0.0)) {
							e = Math.min(e, d) + 0.01;
							field_15180.set(direction);
							entity.move(MovementType.PISTON, e * (double)direction.getOffsetX(), e * (double)direction.getOffsetY(), e * (double)direction.getOffsetZ());
							field_15180.set(null);
							if (!this.extending && this.source) {
								this.method_13754(entity, direction, d);
							}
						}
					}
				}
			}
		}
	}

	private Box method_13753(List<Box> list) {
		double d = 0.0;
		double e = 0.0;
		double f = 0.0;
		double g = 1.0;
		double h = 1.0;
		double i = 1.0;

		for (Box box : list) {
			d = Math.min(box.minX, d);
			e = Math.min(box.minY, e);
			f = Math.min(box.minZ, f);
			g = Math.max(box.maxX, g);
			h = Math.max(box.maxY, h);
			i = Math.max(box.maxZ, i);
		}

		return new Box(d, e, f, g, h, i);
	}

	private double method_13752(Box box, Direction direction, Box box2) {
		switch (direction.getAxis()) {
			case X:
				return method_13755(box, direction, box2);
			case Y:
			default:
				return method_13756(box, direction, box2);
			case Z:
				return method_13757(box, direction, box2);
		}
	}

	private Box method_13750(Box box) {
		double d = (double)this.method_11703(this.progress);
		return box.offset(
			(double)this.pos.getX() + d * (double)this.direction.getOffsetX(),
			(double)this.pos.getY() + d * (double)this.direction.getOffsetY(),
			(double)this.pos.getZ() + d * (double)this.direction.getOffsetZ()
		);
	}

	private Box method_13751(Box box, Direction direction, double d) {
		double e = d * (double)direction.getAxisDirection().offset();
		double f = Math.min(e, 0.0);
		double g = Math.max(e, 0.0);
		switch (direction) {
			case WEST:
				return new Box(box.minX + f, box.minY, box.minZ, box.minX + g, box.maxY, box.maxZ);
			case EAST:
				return new Box(box.maxX + f, box.minY, box.minZ, box.maxX + g, box.maxY, box.maxZ);
			case DOWN:
				return new Box(box.minX, box.minY + f, box.minZ, box.maxX, box.minY + g, box.maxZ);
			case UP:
			default:
				return new Box(box.minX, box.maxY + f, box.minZ, box.maxX, box.maxY + g, box.maxZ);
			case NORTH:
				return new Box(box.minX, box.minY, box.minZ + f, box.maxX, box.maxY, box.minZ + g);
			case SOUTH:
				return new Box(box.minX, box.minY, box.maxZ + f, box.maxX, box.maxY, box.maxZ + g);
		}
	}

	private void method_13754(Entity entity, Direction direction, double d) {
		Box box = entity.getBoundingBox();
		Box box2 = Block.collisionBox.offset(this.pos);
		if (box.intersects(box2)) {
			Direction direction2 = direction.getOpposite();
			double e = this.method_13752(box2, direction2, box) + 0.01;
			double f = this.method_13752(box2, direction2, box.intersection(box2)) + 0.01;
			if (Math.abs(e - f) < 0.01) {
				e = Math.min(e, d) + 0.01;
				field_15180.set(direction);
				entity.move(MovementType.PISTON, e * (double)direction2.getOffsetX(), e * (double)direction2.getOffsetY(), e * (double)direction2.getOffsetZ());
				field_15180.set(null);
			}
		}
	}

	private static double method_13755(Box box, Direction direction, Box box2) {
		return direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? box.maxX - box2.minX : box2.maxX - box.minX;
	}

	private static double method_13756(Box box, Direction direction, Box box2) {
		return direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? box.maxY - box2.minY : box2.maxY - box.minY;
	}

	private static double method_13757(Box box, Direction direction, Box box2) {
		return direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? box.maxZ - box2.minZ : box2.maxZ - box.minZ;
	}

	public void finish() {
		if (this.lastProgress < 1.0F && this.world != null) {
			this.progress = 1.0F;
			this.lastProgress = this.progress;
			this.world.removeBlockEntity(this.pos);
			this.markRemoved();
			if (this.world.getBlockState(this.pos).getBlock() == Blocks.PISTON_EXTENSION) {
				this.world.setBlockState(this.pos, this.pushedBlock, 3);
				this.world.updateNeighbor(this.pos, this.pushedBlock.getBlock(), this.pos);
			}
		}
	}

	@Override
	public void tick() {
		this.lastProgress = this.progress;
		if (this.lastProgress >= 1.0F) {
			this.world.removeBlockEntity(this.pos);
			this.markRemoved();
			if (this.world.getBlockState(this.pos).getBlock() == Blocks.PISTON_EXTENSION) {
				this.world.setBlockState(this.pos, this.pushedBlock, 3);
				this.world.updateNeighbor(this.pos, this.pushedBlock.getBlock(), this.pos);
			}
		} else {
			float f = this.progress + 0.5F;
			this.method_13758(f);
			this.progress = f;
			if (this.progress >= 1.0F) {
				this.progress = 1.0F;
			}
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
		this.source = nbt.getBoolean("source");
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.putInt("blockId", Block.getIdByBlock(this.pushedBlock.getBlock()));
		nbt.putInt("blockData", this.pushedBlock.getBlock().getData(this.pushedBlock));
		nbt.putInt("facing", this.direction.getId());
		nbt.putFloat("progress", this.lastProgress);
		nbt.putBoolean("extending", this.extending);
		nbt.putBoolean("source", this.source);
		return nbt;
	}

	public void method_13749(World world, BlockPos blockPos, Box box, List<Box> list, @Nullable Entity entity) {
		if (!this.extending && this.source) {
			this.pushedBlock.with(PistonBlock.EXTENDED, true).appendCollisionBoxes(world, blockPos, box, list, entity, false);
		}

		Direction direction = (Direction)field_15180.get();
		if (!((double)this.progress < 1.0) || direction != (this.extending ? this.direction : this.direction.getOpposite())) {
			int i = list.size();
			BlockState blockState;
			if (this.isSource()) {
				blockState = Blocks.PISTON_HEAD
					.getDefaultState()
					.with(PistonHeadBlock.FACING, this.direction)
					.with(PistonHeadBlock.SHORT, this.extending != 1.0F - this.progress < 0.25F);
			} else {
				blockState = this.pushedBlock;
			}

			float f = this.method_11703(this.progress);
			double d = (double)((float)this.direction.getOffsetX() * f);
			double e = (double)((float)this.direction.getOffsetY() * f);
			double g = (double)((float)this.direction.getOffsetZ() * f);
			blockState.appendCollisionBoxes(world, blockPos, box.offset(-d, -e, -g), list, entity, true);

			for (int j = i; j < list.size(); j++) {
				list.set(j, ((Box)list.get(j)).offset(d, e, g));
			}
		}
	}
}
