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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Boxes;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class PistonBlockEntity extends BlockEntity implements Tickable {
	private BlockState pushedBlock;
	private Direction facing;
	private boolean extending;
	private boolean source;
	private static final ThreadLocal<Direction> field_12205 = ThreadLocal.withInitial(() -> null);
	private float progress;
	private float lastProgress;
	private long savedWorldTime;

	public PistonBlockEntity() {
		super(BlockEntityType.field_11897);
	}

	public PistonBlockEntity(BlockState blockState, Direction direction, boolean bl, boolean bl2) {
		this();
		this.pushedBlock = blockState;
		this.facing = direction;
		this.extending = bl;
		this.source = bl2;
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
	}

	public boolean isExtending() {
		return this.extending;
	}

	public Direction getFacing() {
		return this.facing;
	}

	public boolean isSource() {
		return this.source;
	}

	public float getProgress(float f) {
		if (f > 1.0F) {
			f = 1.0F;
		}

		return MathHelper.lerp(f, this.lastProgress, this.progress);
	}

	public float getRenderOffsetX(float f) {
		return (float)this.facing.getOffsetX() * this.getAmountExtended(this.getProgress(f));
	}

	public float getRenderOffsetY(float f) {
		return (float)this.facing.getOffsetY() * this.getAmountExtended(this.getProgress(f));
	}

	public float getRenderOffsetZ(float f) {
		return (float)this.facing.getOffsetZ() * this.getAmountExtended(this.getProgress(f));
	}

	private float getAmountExtended(float f) {
		return this.extending ? f - 1.0F : 1.0F - f;
	}

	private BlockState getHeadBlockState() {
		return !this.isExtending() && this.isSource() && this.pushedBlock.getBlock() instanceof PistonBlock
			? Blocks.field_10379
				.getDefaultState()
				.with(PistonHeadBlock.TYPE, this.pushedBlock.getBlock() == Blocks.field_10615 ? PistonType.field_12634 : PistonType.field_12637)
				.with(PistonHeadBlock.FACING, this.pushedBlock.get(PistonBlock.FACING))
			: this.pushedBlock;
	}

	private void pushEntities(float f) {
		Direction direction = this.getMovementDirection();
		double d = (double)(f - this.progress);
		VoxelShape voxelShape = this.getHeadBlockState().getCollisionShape(this.world, this.getPos());
		if (!voxelShape.isEmpty()) {
			List<Box> list = voxelShape.getBoundingBoxes();
			Box box = this.offsetHeadBox(this.getApproximateHeadBox(list));
			List<Entity> list2 = this.world.getEntities(null, Boxes.stretch(box, direction, d).union(box));
			if (!list2.isEmpty()) {
				boolean bl = this.pushedBlock.getBlock() == Blocks.field_10030;

				for (Entity entity : list2) {
					if (entity.getPistonBehavior() != PistonBehavior.field_15975) {
						if (bl) {
							Vec3d vec3d = entity.getVelocity();
							double e = vec3d.x;
							double g = vec3d.y;
							double h = vec3d.z;
							switch (direction.getAxis()) {
								case field_11048:
									e = (double)direction.getOffsetX();
									break;
								case field_11052:
									g = (double)direction.getOffsetY();
									break;
								case field_11051:
									h = (double)direction.getOffsetZ();
							}

							entity.setVelocity(e, g, h);
						}

						double i = 0.0;

						for (Box box2 : list) {
							Box box3 = Boxes.stretch(this.offsetHeadBox(box2), direction, d);
							Box box4 = entity.getBoundingBox();
							if (box3.intersects(box4)) {
								i = Math.max(i, getIntersectionSize(box3, direction, box4));
								if (i >= d) {
									break;
								}
							}
						}

						if (!(i <= 0.0)) {
							i = Math.min(i, d) + 0.01;
							method_23672(direction, entity, i, direction);
							if (!this.extending && this.source) {
								this.push(entity, direction, d);
							}
						}
					}
				}
			}
		}
	}

	private static void method_23672(Direction direction, Entity entity, double d, Direction direction2) {
		field_12205.set(direction);
		entity.move(MovementType.field_6310, new Vec3d(d * (double)direction2.getOffsetX(), d * (double)direction2.getOffsetY(), d * (double)direction2.getOffsetZ()));
		field_12205.set(null);
	}

	private void method_23674(float f) {
		if (this.method_23364()) {
			Direction direction = this.getMovementDirection();
			if (direction.getAxis().isHorizontal()) {
				double d = this.pushedBlock.getCollisionShape(this.world, this.pos).getMaximum(Direction.Axis.field_11052);
				Box box = this.offsetHeadBox(new Box(0.0, d, 0.0, 1.0, 1.5000000999999998, 1.0));
				double e = (double)(f - this.progress);

				for (Entity entity : this.world.getEntities((Entity)null, box, entityx -> method_23671(box, entityx))) {
					method_23672(direction, entity, e, direction);
				}
			}
		}
	}

	private static boolean method_23671(Box box, Entity entity) {
		return entity.getPistonBehavior() == PistonBehavior.field_15974
			&& entity.onGround
			&& entity.getX() >= box.x1
			&& entity.getX() <= box.x2
			&& entity.getZ() >= box.z1
			&& entity.getZ() <= box.z2;
	}

	private boolean method_23364() {
		return this.pushedBlock.getBlock() == Blocks.field_21211;
	}

	public Direction getMovementDirection() {
		return this.extending ? this.facing : this.facing.getOpposite();
	}

	private Box getApproximateHeadBox(List<Box> list) {
		double d = 0.0;
		double e = 0.0;
		double f = 0.0;
		double g = 1.0;
		double h = 1.0;
		double i = 1.0;

		for (Box box : list) {
			d = Math.min(box.x1, d);
			e = Math.min(box.y1, e);
			f = Math.min(box.z1, f);
			g = Math.max(box.x2, g);
			h = Math.max(box.y2, h);
			i = Math.max(box.z2, i);
		}

		return new Box(d, e, f, g, h, i);
	}

	private static double getIntersectionSize(Box box, Direction direction, Box box2) {
		switch (direction) {
			case field_11034:
				return box.x2 - box2.x1;
			case field_11039:
				return box2.x2 - box.x1;
			case field_11036:
			default:
				return box.y2 - box2.y1;
			case field_11033:
				return box2.y2 - box.y1;
			case field_11035:
				return box.z2 - box2.z1;
			case field_11043:
				return box2.z2 - box.z1;
		}
	}

	private Box offsetHeadBox(Box box) {
		double d = (double)this.getAmountExtended(this.progress);
		return box.offset(
			(double)this.pos.getX() + d * (double)this.facing.getOffsetX(),
			(double)this.pos.getY() + d * (double)this.facing.getOffsetY(),
			(double)this.pos.getZ() + d * (double)this.facing.getOffsetZ()
		);
	}

	private void push(Entity entity, Direction direction, double d) {
		Box box = entity.getBoundingBox();
		Box box2 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos);
		if (box.intersects(box2)) {
			Direction direction2 = direction.getOpposite();
			double e = getIntersectionSize(box2, direction2, box) + 0.01;
			double f = getIntersectionSize(box2, direction2, box.intersection(box2)) + 0.01;
			if (Math.abs(e - f) < 0.01) {
				e = Math.min(e, d) + 0.01;
				method_23672(direction, entity, e, direction2);
			}
		}
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
			if (this.world.getBlockState(this.pos).getBlock() == Blocks.field_10008) {
				BlockState blockState;
				if (this.source) {
					blockState = Blocks.field_10124.getDefaultState();
				} else {
					blockState = Block.getRenderingState(this.pushedBlock, this.world, this.pos);
				}

				this.world.setBlockState(this.pos, blockState, 3);
				this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
			}
		}
	}

	@Override
	public void tick() {
		this.savedWorldTime = this.world.getTime();
		this.lastProgress = this.progress;
		if (this.lastProgress >= 1.0F) {
			this.world.removeBlockEntity(this.pos);
			this.markRemoved();
			if (this.pushedBlock != null && this.world.getBlockState(this.pos).getBlock() == Blocks.field_10008) {
				BlockState blockState = Block.getRenderingState(this.pushedBlock, this.world, this.pos);
				if (blockState.isAir()) {
					this.world.setBlockState(this.pos, this.pushedBlock, 84);
					Block.replaceBlock(this.pushedBlock, blockState, this.world, this.pos, 3);
				} else {
					if (blockState.contains(Properties.WATERLOGGED) && (Boolean)blockState.get(Properties.WATERLOGGED)) {
						blockState = blockState.with(Properties.WATERLOGGED, Boolean.valueOf(false));
					}

					this.world.setBlockState(this.pos, blockState, 67);
					this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
				}
			}
		} else {
			float f = this.progress + 0.5F;
			this.pushEntities(f);
			this.method_23674(f);
			this.progress = f;
			if (this.progress >= 1.0F) {
				this.progress = 1.0F;
			}
		}
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.pushedBlock = NbtHelper.toBlockState(compoundTag.getCompound("blockState"));
		this.facing = Direction.byId(compoundTag.getInt("facing"));
		this.progress = compoundTag.getFloat("progress");
		this.lastProgress = this.progress;
		this.extending = compoundTag.getBoolean("extending");
		this.source = compoundTag.getBoolean("source");
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.put("blockState", NbtHelper.fromBlockState(this.pushedBlock));
		compoundTag.putInt("facing", this.facing.getId());
		compoundTag.putFloat("progress", this.lastProgress);
		compoundTag.putBoolean("extending", this.extending);
		compoundTag.putBoolean("source", this.source);
		return compoundTag;
	}

	public VoxelShape getCollisionShape(BlockView blockView, BlockPos blockPos) {
		VoxelShape voxelShape;
		if (!this.extending && this.source) {
			voxelShape = this.pushedBlock.with(PistonBlock.EXTENDED, Boolean.valueOf(true)).getCollisionShape(blockView, blockPos);
		} else {
			voxelShape = VoxelShapes.empty();
		}

		Direction direction = (Direction)field_12205.get();
		if ((double)this.progress < 1.0 && direction == this.getMovementDirection()) {
			return voxelShape;
		} else {
			BlockState blockState;
			if (this.isSource()) {
				blockState = Blocks.field_10379
					.getDefaultState()
					.with(PistonHeadBlock.FACING, this.facing)
					.with(PistonHeadBlock.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 4.0F));
			} else {
				blockState = this.pushedBlock;
			}

			float f = this.getAmountExtended(this.progress);
			double d = (double)((float)this.facing.getOffsetX() * f);
			double e = (double)((float)this.facing.getOffsetY() * f);
			double g = (double)((float)this.facing.getOffsetZ() * f);
			return VoxelShapes.union(voxelShape, blockState.getCollisionShape(blockView, blockPos).offset(d, e, g));
		}
	}

	public long getSavedWorldTime() {
		return this.savedWorldTime;
	}
}
