package net.minecraft.block;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class StairsBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);
	public static final EnumProperty<StairsBlock.Half> HALF = EnumProperty.of("half", StairsBlock.Half.class);
	public static final EnumProperty<StairsBlock.Shape> SHAPE = EnumProperty.of("shape", StairsBlock.Shape.class);
	private static final int[][] ignoredCornersByDirection = new int[][]{{4, 5}, {5, 7}, {6, 7}, {4, 6}, {0, 1}, {1, 3}, {2, 3}, {0, 2}};
	private final Block block;
	private final BlockState state;
	private boolean isRayTraced;
	private int rayTracePass;

	protected StairsBlock(BlockState blockState) {
		super(blockState.getBlock().material);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HALF, StairsBlock.Half.BOTTOM).with(SHAPE, StairsBlock.Shape.STRAIGHT)
		);
		this.block = blockState.getBlock();
		this.state = blockState;
		this.setStrength(this.block.hardness);
		this.setResistance(this.block.blastResistance / 3.0F);
		this.setSound(this.block.sound);
		this.setOpacity(255);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		if (this.isRayTraced) {
			this.setBoundingBox(
				0.5F * (float)(this.rayTracePass % 2),
				0.5F * (float)(this.rayTracePass / 4 % 2),
				0.5F * (float)(this.rayTracePass / 2 % 2),
				0.5F + 0.5F * (float)(this.rayTracePass % 2),
				0.5F + 0.5F * (float)(this.rayTracePass / 4 % 2),
				0.5F + 0.5F * (float)(this.rayTracePass / 2 % 2)
			);
		} else {
			this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public void setBaseBoundingBox(BlockView view, BlockPos pos) {
		if (view.getBlockState(pos).get(HALF) == StairsBlock.Half.TOP) {
			this.setBoundingBox(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}
	}

	public static boolean isStairs(Block block) {
		return block instanceof StairsBlock;
	}

	public static boolean isSameStairs(BlockView view, BlockPos pos, BlockState state) {
		BlockState blockState = view.getBlockState(pos);
		Block block = blockState.getBlock();
		return isStairs(block) && blockState.get(HALF) == state.get(HALF) && blockState.get(FACING) == state.get(FACING);
	}

	public int getOuterConnectDirection(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		Direction direction = blockState.get(FACING);
		StairsBlock.Half half = blockState.get(HALF);
		boolean bl = half == StairsBlock.Half.TOP;
		if (direction == Direction.EAST) {
			BlockState blockState2 = view.getBlockState(pos.east());
			Block block = blockState2.getBlock();
			if (isStairs(block) && half == blockState2.get(HALF)) {
				Direction direction2 = blockState2.get(FACING);
				if (direction2 == Direction.NORTH && !isSameStairs(view, pos.south(), blockState)) {
					return bl ? 1 : 2;
				}

				if (direction2 == Direction.SOUTH && !isSameStairs(view, pos.north(), blockState)) {
					return bl ? 2 : 1;
				}
			}
		} else if (direction == Direction.WEST) {
			BlockState blockState3 = view.getBlockState(pos.west());
			Block block2 = blockState3.getBlock();
			if (isStairs(block2) && half == blockState3.get(HALF)) {
				Direction direction3 = blockState3.get(FACING);
				if (direction3 == Direction.NORTH && !isSameStairs(view, pos.south(), blockState)) {
					return bl ? 2 : 1;
				}

				if (direction3 == Direction.SOUTH && !isSameStairs(view, pos.north(), blockState)) {
					return bl ? 1 : 2;
				}
			}
		} else if (direction == Direction.SOUTH) {
			BlockState blockState4 = view.getBlockState(pos.south());
			Block block3 = blockState4.getBlock();
			if (isStairs(block3) && half == blockState4.get(HALF)) {
				Direction direction4 = blockState4.get(FACING);
				if (direction4 == Direction.WEST && !isSameStairs(view, pos.east(), blockState)) {
					return bl ? 2 : 1;
				}

				if (direction4 == Direction.EAST && !isSameStairs(view, pos.west(), blockState)) {
					return bl ? 1 : 2;
				}
			}
		} else if (direction == Direction.NORTH) {
			BlockState blockState5 = view.getBlockState(pos.north());
			Block block4 = blockState5.getBlock();
			if (isStairs(block4) && half == blockState5.get(HALF)) {
				Direction direction5 = blockState5.get(FACING);
				if (direction5 == Direction.WEST && !isSameStairs(view, pos.east(), blockState)) {
					return bl ? 1 : 2;
				}

				if (direction5 == Direction.EAST && !isSameStairs(view, pos.west(), blockState)) {
					return bl ? 2 : 1;
				}
			}
		}

		return 0;
	}

	public int getInnerConnectDirection(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		Direction direction = blockState.get(FACING);
		StairsBlock.Half half = blockState.get(HALF);
		boolean bl = half == StairsBlock.Half.TOP;
		if (direction == Direction.EAST) {
			BlockState blockState2 = view.getBlockState(pos.west());
			Block block = blockState2.getBlock();
			if (isStairs(block) && half == blockState2.get(HALF)) {
				Direction direction2 = blockState2.get(FACING);
				if (direction2 == Direction.NORTH && !isSameStairs(view, pos.north(), blockState)) {
					return bl ? 1 : 2;
				}

				if (direction2 == Direction.SOUTH && !isSameStairs(view, pos.south(), blockState)) {
					return bl ? 2 : 1;
				}
			}
		} else if (direction == Direction.WEST) {
			BlockState blockState3 = view.getBlockState(pos.east());
			Block block2 = blockState3.getBlock();
			if (isStairs(block2) && half == blockState3.get(HALF)) {
				Direction direction3 = blockState3.get(FACING);
				if (direction3 == Direction.NORTH && !isSameStairs(view, pos.north(), blockState)) {
					return bl ? 2 : 1;
				}

				if (direction3 == Direction.SOUTH && !isSameStairs(view, pos.south(), blockState)) {
					return bl ? 1 : 2;
				}
			}
		} else if (direction == Direction.SOUTH) {
			BlockState blockState4 = view.getBlockState(pos.north());
			Block block3 = blockState4.getBlock();
			if (isStairs(block3) && half == blockState4.get(HALF)) {
				Direction direction4 = blockState4.get(FACING);
				if (direction4 == Direction.WEST && !isSameStairs(view, pos.west(), blockState)) {
					return bl ? 2 : 1;
				}

				if (direction4 == Direction.EAST && !isSameStairs(view, pos.east(), blockState)) {
					return bl ? 1 : 2;
				}
			}
		} else if (direction == Direction.NORTH) {
			BlockState blockState5 = view.getBlockState(pos.south());
			Block block4 = blockState5.getBlock();
			if (isStairs(block4) && half == blockState5.get(HALF)) {
				Direction direction5 = blockState5.get(FACING);
				if (direction5 == Direction.WEST && !isSameStairs(view, pos.west(), blockState)) {
					return bl ? 1 : 2;
				}

				if (direction5 == Direction.EAST && !isSameStairs(view, pos.east(), blockState)) {
					return bl ? 2 : 1;
				}
			}
		}

		return 0;
	}

	public boolean canConnectInner(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		Direction direction = blockState.get(FACING);
		StairsBlock.Half half = blockState.get(HALF);
		boolean bl = half == StairsBlock.Half.TOP;
		float f = 0.5F;
		float g = 1.0F;
		if (bl) {
			f = 0.0F;
			g = 0.5F;
		}

		float h = 0.0F;
		float i = 1.0F;
		float j = 0.0F;
		float k = 0.5F;
		boolean bl2 = true;
		if (direction == Direction.EAST) {
			h = 0.5F;
			k = 1.0F;
			BlockState blockState2 = view.getBlockState(pos.east());
			Block block = blockState2.getBlock();
			if (isStairs(block) && half == blockState2.get(HALF)) {
				Direction direction2 = blockState2.get(FACING);
				if (direction2 == Direction.NORTH && !isSameStairs(view, pos.south(), blockState)) {
					k = 0.5F;
					bl2 = false;
				} else if (direction2 == Direction.SOUTH && !isSameStairs(view, pos.north(), blockState)) {
					j = 0.5F;
					bl2 = false;
				}
			}
		} else if (direction == Direction.WEST) {
			i = 0.5F;
			k = 1.0F;
			BlockState blockState3 = view.getBlockState(pos.west());
			Block block2 = blockState3.getBlock();
			if (isStairs(block2) && half == blockState3.get(HALF)) {
				Direction direction3 = blockState3.get(FACING);
				if (direction3 == Direction.NORTH && !isSameStairs(view, pos.south(), blockState)) {
					k = 0.5F;
					bl2 = false;
				} else if (direction3 == Direction.SOUTH && !isSameStairs(view, pos.north(), blockState)) {
					j = 0.5F;
					bl2 = false;
				}
			}
		} else if (direction == Direction.SOUTH) {
			j = 0.5F;
			k = 1.0F;
			BlockState blockState4 = view.getBlockState(pos.south());
			Block block3 = blockState4.getBlock();
			if (isStairs(block3) && half == blockState4.get(HALF)) {
				Direction direction4 = blockState4.get(FACING);
				if (direction4 == Direction.WEST && !isSameStairs(view, pos.east(), blockState)) {
					i = 0.5F;
					bl2 = false;
				} else if (direction4 == Direction.EAST && !isSameStairs(view, pos.west(), blockState)) {
					h = 0.5F;
					bl2 = false;
				}
			}
		} else if (direction == Direction.NORTH) {
			BlockState blockState5 = view.getBlockState(pos.north());
			Block block4 = blockState5.getBlock();
			if (isStairs(block4) && half == blockState5.get(HALF)) {
				Direction direction5 = blockState5.get(FACING);
				if (direction5 == Direction.WEST && !isSameStairs(view, pos.east(), blockState)) {
					i = 0.5F;
					bl2 = false;
				} else if (direction5 == Direction.EAST && !isSameStairs(view, pos.west(), blockState)) {
					h = 0.5F;
					bl2 = false;
				}
			}
		}

		this.setBoundingBox(h, f, j, i, g, k);
		return bl2;
	}

	public boolean isConnectedInner(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		Direction direction = blockState.get(FACING);
		StairsBlock.Half half = blockState.get(HALF);
		boolean bl = half == StairsBlock.Half.TOP;
		float f = 0.5F;
		float g = 1.0F;
		if (bl) {
			f = 0.0F;
			g = 0.5F;
		}

		float h = 0.0F;
		float i = 0.5F;
		float j = 0.5F;
		float k = 1.0F;
		boolean bl2 = false;
		if (direction == Direction.EAST) {
			BlockState blockState2 = view.getBlockState(pos.west());
			Block block = blockState2.getBlock();
			if (isStairs(block) && half == blockState2.get(HALF)) {
				Direction direction2 = blockState2.get(FACING);
				if (direction2 == Direction.NORTH && !isSameStairs(view, pos.north(), blockState)) {
					j = 0.0F;
					k = 0.5F;
					bl2 = true;
				} else if (direction2 == Direction.SOUTH && !isSameStairs(view, pos.south(), blockState)) {
					j = 0.5F;
					k = 1.0F;
					bl2 = true;
				}
			}
		} else if (direction == Direction.WEST) {
			BlockState blockState3 = view.getBlockState(pos.east());
			Block block2 = blockState3.getBlock();
			if (isStairs(block2) && half == blockState3.get(HALF)) {
				h = 0.5F;
				i = 1.0F;
				Direction direction3 = blockState3.get(FACING);
				if (direction3 == Direction.NORTH && !isSameStairs(view, pos.north(), blockState)) {
					j = 0.0F;
					k = 0.5F;
					bl2 = true;
				} else if (direction3 == Direction.SOUTH && !isSameStairs(view, pos.south(), blockState)) {
					j = 0.5F;
					k = 1.0F;
					bl2 = true;
				}
			}
		} else if (direction == Direction.SOUTH) {
			BlockState blockState4 = view.getBlockState(pos.north());
			Block block3 = blockState4.getBlock();
			if (isStairs(block3) && half == blockState4.get(HALF)) {
				j = 0.0F;
				k = 0.5F;
				Direction direction4 = blockState4.get(FACING);
				if (direction4 == Direction.WEST && !isSameStairs(view, pos.west(), blockState)) {
					bl2 = true;
				} else if (direction4 == Direction.EAST && !isSameStairs(view, pos.east(), blockState)) {
					h = 0.5F;
					i = 1.0F;
					bl2 = true;
				}
			}
		} else if (direction == Direction.NORTH) {
			BlockState blockState5 = view.getBlockState(pos.south());
			Block block4 = blockState5.getBlock();
			if (isStairs(block4) && half == blockState5.get(HALF)) {
				Direction direction5 = blockState5.get(FACING);
				if (direction5 == Direction.WEST && !isSameStairs(view, pos.west(), blockState)) {
					bl2 = true;
				} else if (direction5 == Direction.EAST && !isSameStairs(view, pos.east(), blockState)) {
					h = 0.5F;
					i = 1.0F;
					bl2 = true;
				}
			}
		}

		if (bl2) {
			this.setBoundingBox(h, f, j, i, g, k);
		}

		return bl2;
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
		this.setBaseBoundingBox(world, pos);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		boolean bl = this.canConnectInner(world, pos);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		if (bl && this.isConnectedInner(world, pos)) {
			super.appendCollisionBoxes(world, pos, state, box, list, entity);
		}

		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		this.block.randomDisplayTick(world, pos, state, rand);
	}

	@Override
	public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
		this.block.onBlockBreakStart(world, pos, player);
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state) {
		this.block.onBreakByPlayer(world, pos, state);
	}

	@Override
	public int getBrightness(BlockView blockView, BlockPos pos) {
		return this.block.getBrightness(blockView, pos);
	}

	@Override
	public float getBlastResistance(Entity entity) {
		return this.block.getBlastResistance(entity);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return this.block.getRenderLayerType();
	}

	@Override
	public int getTickRate(World world) {
		return this.block.getTickRate(world);
	}

	@Override
	public Box getSelectionBox(World world, BlockPos pos) {
		return this.block.getSelectionBox(world, pos);
	}

	@Override
	public Vec3d onEntityCollision(World world, BlockPos pos, Entity entity, Vec3d velocity) {
		return this.block.onEntityCollision(world, pos, entity, velocity);
	}

	@Override
	public boolean hasCollision() {
		return this.block.hasCollision();
	}

	@Override
	public boolean canCollide(BlockState state, boolean bl) {
		return this.block.canCollide(state, bl);
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return this.block.canBePlacedAtPos(world, pos);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		this.neighborUpdate(world, pos, this.state, Blocks.AIR);
		this.block.onCreation(world, pos, this.state);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		this.block.onBreaking(world, pos, this.state);
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		this.block.onSteppedOn(world, pos, entity);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		this.block.onScheduledTick(world, pos, state, rand);
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		return this.block.onUse(world, pos, this.state, player, Direction.DOWN, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		this.block.onDestroyedByExplosion(world, pos, explosion);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return this.block.getMaterialColor(this.state);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		BlockState blockState = super.getStateFromData(world, pos, dir, x, y, z, id, entity);
		blockState = blockState.with(FACING, entity.getHorizontalDirection()).with(SHAPE, StairsBlock.Shape.STRAIGHT);
		return dir != Direction.DOWN && (dir == Direction.UP || !((double)y > 0.5))
			? blockState.with(HALF, StairsBlock.Half.BOTTOM)
			: blockState.with(HALF, StairsBlock.Half.TOP);
	}

	@Override
	public BlockHitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
		BlockHitResult[] blockHitResults = new BlockHitResult[8];
		BlockState blockState = world.getBlockState(pos);
		int i = ((Direction)blockState.get(FACING)).getHorizontal();
		boolean bl = blockState.get(HALF) == StairsBlock.Half.TOP;
		int[] is = ignoredCornersByDirection[i + (bl ? 4 : 0)];
		this.isRayTraced = true;

		for (int j = 0; j < 8; j++) {
			this.rayTracePass = j;
			if (Arrays.binarySearch(is, j) < 0) {
				blockHitResults[j] = super.rayTrace(world, pos, start, end);
			}
		}

		for (int m : is) {
			blockHitResults[m] = null;
		}

		BlockHitResult blockHitResult = null;
		double d = 0.0;

		for (BlockHitResult blockHitResult2 : blockHitResults) {
			if (blockHitResult2 != null) {
				double e = blockHitResult2.pos.squaredDistanceTo(end);
				if (e > d) {
					blockHitResult = blockHitResult2;
					d = e;
				}
			}
		}

		return blockHitResult;
	}

	@Override
	public BlockState stateFromData(int data) {
		BlockState blockState = this.getDefaultState().with(HALF, (data & 4) > 0 ? StairsBlock.Half.TOP : StairsBlock.Half.BOTTOM);
		return blockState.with(FACING, Direction.getById(5 - (data & 3)));
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		if (state.get(HALF) == StairsBlock.Half.TOP) {
			i |= 4;
		}

		return i | 5 - ((Direction)state.get(FACING)).getId();
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		if (this.canConnectInner(view, pos)) {
			switch (this.getInnerConnectDirection(view, pos)) {
				case 0:
					state = state.with(SHAPE, StairsBlock.Shape.STRAIGHT);
					break;
				case 1:
					state = state.with(SHAPE, StairsBlock.Shape.INNER_RIGHT);
					break;
				case 2:
					state = state.with(SHAPE, StairsBlock.Shape.INNER_LEFT);
			}
		} else {
			switch (this.getOuterConnectDirection(view, pos)) {
				case 0:
					state = state.with(SHAPE, StairsBlock.Shape.STRAIGHT);
					break;
				case 1:
					state = state.with(SHAPE, StairsBlock.Shape.OUTER_RIGHT);
					break;
				case 2:
					state = state.with(SHAPE, StairsBlock.Shape.OUTER_LEFT);
			}
		}

		return state;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, HALF, SHAPE);
	}

	public static enum Half implements StringIdentifiable {
		TOP("top"),
		BOTTOM("bottom");

		private final String name;

		private Half(String string2) {
			this.name = string2;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}

	public static enum Shape implements StringIdentifiable {
		STRAIGHT("straight"),
		INNER_LEFT("inner_left"),
		INNER_RIGHT("inner_right"),
		OUTER_LEFT("outer_left"),
		OUTER_RIGHT("outer_right");

		private final String name;

		private Shape(String string2) {
			this.name = string2;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
