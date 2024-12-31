package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class VineBlock extends Block {
	public static final BooleanProperty UP = BooleanProperty.of("up");
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	public static final BooleanProperty[] PROPERTIES = new BooleanProperty[]{UP, NORTH, SOUTH, WEST, EAST};
	protected static final Box field_12827 = new Box(0.0, 0.9375, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12823 = new Box(0.0, 0.0, 0.0, 0.0625, 1.0, 1.0);
	protected static final Box field_12824 = new Box(0.9375, 0.0, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12825 = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 0.0625);
	protected static final Box field_12826 = new Box(0.0, 0.0, 0.9375, 1.0, 1.0, 1.0);

	public VineBlock() {
		super(Material.REPLACEABLE_PLANT);
		this.setDefaultState(this.stateManager.getDefaultState().with(UP, false).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Nullable
	@Override
	public Box method_8640(BlockState state, BlockView view, BlockPos pos) {
		return EMPTY_BOX;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		state = state.getBlockState(view, pos);
		int i = 0;
		Box box = collisionBox;
		if ((Boolean)state.get(UP)) {
			box = field_12827;
			i++;
		}

		if ((Boolean)state.get(NORTH)) {
			box = field_12825;
			i++;
		}

		if ((Boolean)state.get(EAST)) {
			box = field_12824;
			i++;
		}

		if ((Boolean)state.get(SOUTH)) {
			box = field_12826;
			i++;
		}

		if ((Boolean)state.get(WEST)) {
			box = field_12823;
			i++;
		}

		return i == 1 ? box : collisionBox;
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return state.with(UP, view.getBlockState(pos.up()).method_11733());
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean method_8638(BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		switch (direction) {
			case UP:
				return this.method_11642(world.getBlockState(pos.up()));
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
				return this.method_11642(world.getBlockState(pos.offset(direction.getOpposite())));
			default:
				return false;
		}
	}

	private boolean method_11642(BlockState blockState) {
		return blockState.method_11730() && blockState.getMaterial().blocksMovement();
	}

	private boolean recheckGrowth(World world, BlockPos pos, BlockState state) {
		BlockState blockState = state;

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BooleanProperty booleanProperty = getByDirection(direction);
			if ((Boolean)state.get(booleanProperty) && !this.method_11642(world.getBlockState(pos.offset(direction)))) {
				BlockState blockState2 = world.getBlockState(pos.up());
				if (blockState2.getBlock() != this || !(Boolean)blockState2.get(booleanProperty)) {
					state = state.with(booleanProperty, false);
				}
			}
		}

		if (getBlockStateId(state) == 0) {
			return false;
		} else {
			if (blockState != state) {
				world.setBlockState(pos, state, 2);
			}

			return true;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.isClient && !this.recheckGrowth(world, pos, state)) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			if (world.random.nextInt(4) == 0) {
				int i = 4;
				int j = 5;
				boolean bl = false;

				label189:
				for (int k = -4; k <= 4; k++) {
					for (int l = -4; l <= 4; l++) {
						for (int m = -1; m <= 1; m++) {
							if (world.getBlockState(pos.add(k, m, l)).getBlock() == this) {
								if (--j <= 0) {
									bl = true;
									break label189;
								}
							}
						}
					}
				}

				Direction direction = Direction.random(rand);
				BlockPos blockPos = pos.up();
				if (direction == Direction.UP && pos.getY() < 255 && world.isAir(blockPos)) {
					if (!bl) {
						BlockState blockState = state;

						for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
							if (rand.nextBoolean() || !this.method_11642(world.getBlockState(blockPos.offset(direction2)))) {
								blockState = blockState.with(getByDirection(direction2), false);
							}
						}

						if ((Boolean)blockState.get(NORTH) || (Boolean)blockState.get(EAST) || (Boolean)blockState.get(SOUTH) || (Boolean)blockState.get(WEST)) {
							world.setBlockState(blockPos, blockState, 2);
						}
					}
				} else if (!direction.getAxis().isHorizontal() || (Boolean)state.get(getByDirection(direction))) {
					if (pos.getY() > 1) {
						BlockPos blockPos5 = pos.down();
						BlockState blockState3 = world.getBlockState(blockPos5);
						Block block2 = blockState3.getBlock();
						if (block2.material == Material.AIR) {
							BlockState blockState4 = state;

							for (Direction direction5 : Direction.DirectionType.HORIZONTAL) {
								if (rand.nextBoolean()) {
									blockState4 = blockState4.with(getByDirection(direction5), false);
								}
							}

							if ((Boolean)blockState4.get(NORTH) || (Boolean)blockState4.get(EAST) || (Boolean)blockState4.get(SOUTH) || (Boolean)blockState4.get(WEST)) {
								world.setBlockState(blockPos5, blockState4, 2);
							}
						} else if (block2 == this) {
							BlockState blockState5 = blockState3;

							for (Direction direction6 : Direction.DirectionType.HORIZONTAL) {
								BooleanProperty booleanProperty = getByDirection(direction6);
								if (rand.nextBoolean() && (Boolean)state.get(booleanProperty)) {
									blockState5 = blockState5.with(booleanProperty, true);
								}
							}

							if ((Boolean)blockState5.get(NORTH) || (Boolean)blockState5.get(EAST) || (Boolean)blockState5.get(SOUTH) || (Boolean)blockState5.get(WEST)) {
								world.setBlockState(blockPos5, blockState5, 2);
							}
						}
					}
				} else if (!bl) {
					BlockPos blockPos2 = pos.offset(direction);
					BlockState blockState2 = world.getBlockState(blockPos2);
					Block block = blockState2.getBlock();
					if (block.material == Material.AIR) {
						Direction direction3 = direction.rotateYClockwise();
						Direction direction4 = direction.rotateYCounterclockwise();
						boolean bl2 = (Boolean)state.get(getByDirection(direction3));
						boolean bl3 = (Boolean)state.get(getByDirection(direction4));
						BlockPos blockPos3 = blockPos2.offset(direction3);
						BlockPos blockPos4 = blockPos2.offset(direction4);
						if (bl2 && this.method_11642(world.getBlockState(blockPos3))) {
							world.setBlockState(blockPos2, this.getDefaultState().with(getByDirection(direction3), true), 2);
						} else if (bl3 && this.method_11642(world.getBlockState(blockPos4))) {
							world.setBlockState(blockPos2, this.getDefaultState().with(getByDirection(direction4), true), 2);
						} else if (bl2 && world.isAir(blockPos3) && this.method_11642(world.getBlockState(pos.offset(direction3)))) {
							world.setBlockState(blockPos3, this.getDefaultState().with(getByDirection(direction.getOpposite()), true), 2);
						} else if (bl3 && world.isAir(blockPos4) && this.method_11642(world.getBlockState(pos.offset(direction4)))) {
							world.setBlockState(blockPos4, this.getDefaultState().with(getByDirection(direction.getOpposite()), true), 2);
						} else if (this.method_11642(world.getBlockState(blockPos2.up()))) {
							world.setBlockState(blockPos2, this.getDefaultState(), 2);
						}
					} else if (block.material.isOpaque() && blockState2.method_11730()) {
						world.setBlockState(pos, state.with(getByDirection(direction), true), 2);
					}
				}
			}
		}
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		BlockState blockState = this.getDefaultState().with(UP, false).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false);
		return dir.getAxis().isHorizontal() ? blockState.with(getByDirection(dir.getOpposite()), true) : blockState;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.AIR;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		if (!world.isClient && stack.getItem() == Items.SHEARS) {
			player.incrementStat(Stats.mined(this));
			onBlockBreak(world, pos, new ItemStack(Blocks.VINE, 1, 0));
		} else {
			super.method_8651(world, player, pos, state, blockEntity, stack);
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(SOUTH, (data & 1) > 0).with(WEST, (data & 2) > 0).with(NORTH, (data & 4) > 0).with(EAST, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		if ((Boolean)state.get(SOUTH)) {
			i |= 1;
		}

		if ((Boolean)state.get(WEST)) {
			i |= 2;
		}

		if ((Boolean)state.get(NORTH)) {
			i |= 4;
		}

		if ((Boolean)state.get(EAST)) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, UP, NORTH, EAST, SOUTH, WEST);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
			default:
				return super.withMirror(state, mirror);
		}
	}

	public static BooleanProperty getByDirection(Direction dir) {
		switch (dir) {
			case UP:
				return UP;
			case NORTH:
				return NORTH;
			case SOUTH:
				return SOUTH;
			case EAST:
				return EAST;
			case WEST:
				return WEST;
			default:
				throw new IllegalArgumentException(dir + " is an invalid choice");
		}
	}

	public static int getBlockStateId(BlockState state) {
		int i = 0;

		for (BooleanProperty booleanProperty : PROPERTIES) {
			if ((Boolean)state.get(booleanProperty)) {
				i++;
			}
		}

		return i;
	}
}
