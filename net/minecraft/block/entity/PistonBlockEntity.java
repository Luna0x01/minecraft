package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;

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
	private long field_18681;

	public PistonBlockEntity() {
		super(BlockEntityType.PISTON);
	}

	public PistonBlockEntity(BlockState blockState, Direction direction, boolean bl, boolean bl2) {
		this();
		this.pushedBlock = blockState;
		this.direction = direction;
		this.extending = bl;
		this.source = bl2;
	}

	@Override
	public NbtCompound getUpdatePacketContent() {
		return this.toNbt(new NbtCompound());
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

	private BlockState method_13759() {
		return !this.isExtending() && this.isSource()
			? Blocks.PISTON_HEAD
				.getDefaultState()
				.withProperty(PistonHeadBlock.field_18667, this.pushedBlock.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT)
				.withProperty(PistonHeadBlock.FACING, this.pushedBlock.getProperty(PistonBlock.FACING))
			: this.pushedBlock;
	}

	private void method_13758(float f) {
		Direction direction = this.method_16854();
		double d = (double)(f - this.progress);
		VoxelShape voxelShape = this.method_13759().getCollisionShape(this.world, this.getPos());
		if (!voxelShape.isEmpty()) {
			List<Box> list = voxelShape.getBoundingBoxes();
			Box box = this.method_13750(this.method_13753(list));
			List<Entity> list2 = this.world.getEntities(null, this.method_13751(box, direction, d).union(box));
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

	public Direction method_16854() {
		return this.extending ? this.direction : this.direction.getOpposite();
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
		Box box2 = VoxelShapes.matchesAnywhere().getBoundingBox().offset(this.pos);
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

	public BlockState getPushedBlock() {
		return this.pushedBlock;
	}

	public void finish() {
		if (this.lastProgress < 1.0F && this.world != null) {
			this.progress = 1.0F;
			this.lastProgress = this.progress;
			this.world.removeBlockEntity(this.pos);
			this.markRemoved();
			if (this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_PISTON) {
				BlockState blockState;
				if (this.source) {
					blockState = Blocks.AIR.getDefaultState();
				} else {
					blockState = Block.method_16583(this.pushedBlock, this.world, this.pos);
				}

				this.world.setBlockState(this.pos, blockState, 3);
				this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
			}
		}
	}

	@Override
	public void tick() {
		this.field_18681 = this.world.getLastUpdateTime();
		this.lastProgress = this.progress;
		if (this.lastProgress >= 1.0F) {
			this.world.removeBlockEntity(this.pos);
			this.markRemoved();
			if (this.pushedBlock != null && this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_PISTON) {
				BlockState blockState = Block.method_16583(this.pushedBlock, this.world, this.pos);
				if (blockState.isAir()) {
					this.world.setBlockState(this.pos, this.pushedBlock, 84);
					Block.method_16572(this.pushedBlock, blockState, this.world, this.pos, 3);
				} else {
					if (blockState.method_16933(Properties.WATERLOGGED) && (Boolean)blockState.getProperty(Properties.WATERLOGGED)) {
						blockState = blockState.withProperty(Properties.WATERLOGGED, Boolean.valueOf(false));
					}

					this.world.setBlockState(this.pos, blockState, 67);
					this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
				}
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

	@Override
	public void fromNbt(NbtCompound nbt) {
		super.fromNbt(nbt);
		this.pushedBlock = NbtHelper.toBlockState(nbt.getCompound("blockState"));
		this.direction = Direction.getById(nbt.getInt("facing"));
		this.progress = nbt.getFloat("progress");
		this.lastProgress = this.progress;
		this.extending = nbt.getBoolean("extending");
		this.source = nbt.getBoolean("source");
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		super.toNbt(nbt);
		nbt.put("blockState", NbtHelper.method_20139(this.pushedBlock));
		nbt.putInt("facing", this.direction.getId());
		nbt.putFloat("progress", this.lastProgress);
		nbt.putBoolean("extending", this.extending);
		nbt.putBoolean("source", this.source);
		return nbt;
	}

	public VoxelShape method_16853(BlockView blockView, BlockPos blockPos) {
		VoxelShape voxelShape;
		if (!this.extending && this.source) {
			voxelShape = this.pushedBlock.withProperty(PistonBlock.field_18654, Boolean.valueOf(true)).getCollisionShape(blockView, blockPos);
		} else {
			voxelShape = VoxelShapes.empty();
		}

		Direction direction = (Direction)field_15180.get();
		if ((double)this.progress < 1.0 && direction == this.method_16854()) {
			return voxelShape;
		} else {
			BlockState blockState;
			if (this.isSource()) {
				blockState = Blocks.PISTON_HEAD
					.getDefaultState()
					.withProperty(PistonHeadBlock.FACING, this.direction)
					.withProperty(PistonHeadBlock.field_18668, Boolean.valueOf(this.extending != 1.0F - this.progress < 4.0F));
			} else {
				blockState = this.pushedBlock;
			}

			float f = this.method_11703(this.progress);
			double d = (double)((float)this.direction.getOffsetX() * f);
			double e = (double)((float)this.direction.getOffsetY() * f);
			double g = (double)((float)this.direction.getOffsetZ() * f);
			return VoxelShapes.union(voxelShape, blockState.getCollisionShape(blockView, blockPos).offset(d, e, g));
		}
	}

	public long method_16855() {
		return this.field_18681;
	}
}
