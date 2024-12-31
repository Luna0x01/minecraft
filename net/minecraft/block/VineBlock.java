package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class VineBlock extends Block {
	public static final BooleanProperty field_18563 = ConnectingBlock.UP;
	public static final BooleanProperty field_18564 = ConnectingBlock.NORTH;
	public static final BooleanProperty field_18565 = ConnectingBlock.EAST;
	public static final BooleanProperty field_18566 = ConnectingBlock.SOUTH;
	public static final BooleanProperty field_18567 = ConnectingBlock.WEST;
	public static final Map<Direction, BooleanProperty> field_18568 = (Map<Direction, BooleanProperty>)ConnectingBlock.FACING_TO_PROPERTY
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != Direction.DOWN)
		.collect(Util.method_20218());
	protected static final VoxelShape field_18569 = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18570 = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	protected static final VoxelShape field_18571 = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18572 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	protected static final VoxelShape field_18573 = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

	public VineBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(field_18563, Boolean.valueOf(false))
				.withProperty(field_18564, Boolean.valueOf(false))
				.withProperty(field_18565, Boolean.valueOf(false))
				.withProperty(field_18566, Boolean.valueOf(false))
				.withProperty(field_18567, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		VoxelShape voxelShape = VoxelShapes.empty();
		if ((Boolean)state.getProperty(field_18563)) {
			voxelShape = VoxelShapes.union(voxelShape, field_18569);
		}

		if ((Boolean)state.getProperty(field_18564)) {
			voxelShape = VoxelShapes.union(voxelShape, field_18572);
		}

		if ((Boolean)state.getProperty(field_18565)) {
			voxelShape = VoxelShapes.union(voxelShape, field_18571);
		}

		if ((Boolean)state.getProperty(field_18566)) {
			voxelShape = VoxelShapes.union(voxelShape, field_18573);
		}

		if ((Boolean)state.getProperty(field_18567)) {
			voxelShape = VoxelShapes.union(voxelShape, field_18570);
		}

		return voxelShape;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		return this.method_16764(this.method_16765(state, world, pos));
	}

	private boolean method_16764(BlockState blockState) {
		return this.method_16766(blockState) > 0;
	}

	private int method_16766(BlockState blockState) {
		int i = 0;

		for (BooleanProperty booleanProperty : field_18568.values()) {
			if ((Boolean)blockState.getProperty(booleanProperty)) {
				i++;
			}
		}

		return i;
	}

	private boolean method_16759(BlockView blockView, BlockPos blockPos, Direction direction) {
		if (direction == Direction.DOWN) {
			return false;
		} else {
			BlockPos blockPos2 = blockPos.offset(direction);
			if (this.method_16763(blockView, blockPos2, direction)) {
				return true;
			} else if (direction.getAxis() == Direction.Axis.Y) {
				return false;
			} else {
				BooleanProperty booleanProperty = (BooleanProperty)field_18568.get(direction);
				BlockState blockState = blockView.getBlockState(blockPos.up());
				return blockState.getBlock() == this && (Boolean)blockState.getProperty(booleanProperty);
			}
		}
	}

	private boolean method_16763(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState = blockView.getBlockState(blockPos);
		return blockState.getRenderLayer(blockView, blockPos, direction.getOpposite()) == BlockRenderLayer.SOLID && !method_14351(blockState.getBlock());
	}

	protected static boolean method_14351(Block block) {
		return block instanceof ShulkerBoxBlock
			|| block instanceof StainedGlassBlock
			|| block == Blocks.BEACON
			|| block == Blocks.CAULDRON
			|| block == Blocks.GLASS
			|| block == Blocks.PISTON
			|| block == Blocks.STICKY_PISTON
			|| block == Blocks.PISTON_HEAD
			|| block.isIn(BlockTags.WOODEN_TRAPDOORS);
	}

	private BlockState method_16765(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		if ((Boolean)blockState.getProperty(field_18563)) {
			blockState = blockState.withProperty(field_18563, Boolean.valueOf(this.method_16763(blockView, blockPos2, Direction.DOWN)));
		}

		BlockState blockState2 = null;

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BooleanProperty booleanProperty = method_16761(direction);
			if ((Boolean)blockState.getProperty(booleanProperty)) {
				boolean bl = this.method_16759(blockView, blockPos, direction);
				if (!bl) {
					if (blockState2 == null) {
						blockState2 = blockView.getBlockState(blockPos2);
					}

					bl = blockState2.getBlock() == this && (Boolean)blockState2.getProperty(booleanProperty);
				}

				blockState = blockState.withProperty(booleanProperty, Boolean.valueOf(bl));
			}
		}

		return blockState;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.DOWN) {
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			BlockState blockState = this.method_16765(state, world, pos);
			return !this.method_16764(blockState) ? Blocks.AIR.getDefaultState() : blockState;
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient) {
			BlockState blockState = this.method_16765(state, world, pos);
			if (blockState != state) {
				if (this.method_16764(blockState)) {
					world.setBlockState(pos, blockState, 2);
				} else {
					state.method_16867(world, pos, 0);
					world.method_8553(pos);
				}
			} else if (world.random.nextInt(4) == 0) {
				Direction direction = Direction.random(random);
				BlockPos blockPos = pos.up();
				if (direction.getAxis().isHorizontal() && !(Boolean)state.getProperty(method_16761(direction))) {
					if (this.method_16758(world, pos)) {
						BlockPos blockPos2 = pos.offset(direction);
						BlockState blockState2 = world.getBlockState(blockPos2);
						if (blockState2.isAir()) {
							Direction direction2 = direction.rotateYClockwise();
							Direction direction3 = direction.rotateYCounterclockwise();
							boolean bl = (Boolean)state.getProperty(method_16761(direction2));
							boolean bl2 = (Boolean)state.getProperty(method_16761(direction3));
							BlockPos blockPos3 = blockPos2.offset(direction2);
							BlockPos blockPos4 = blockPos2.offset(direction3);
							if (bl && this.method_16763(world, blockPos3, direction2)) {
								world.setBlockState(blockPos2, this.getDefaultState().withProperty(method_16761(direction2), Boolean.valueOf(true)), 2);
							} else if (bl2 && this.method_16763(world, blockPos4, direction3)) {
								world.setBlockState(blockPos2, this.getDefaultState().withProperty(method_16761(direction3), Boolean.valueOf(true)), 2);
							} else {
								Direction direction4 = direction.getOpposite();
								if (bl && world.method_8579(blockPos3) && this.method_16763(world, pos.offset(direction2), direction4)) {
									world.setBlockState(blockPos3, this.getDefaultState().withProperty(method_16761(direction4), Boolean.valueOf(true)), 2);
								} else if (bl2 && world.method_8579(blockPos4) && this.method_16763(world, pos.offset(direction3), direction4)) {
									world.setBlockState(blockPos4, this.getDefaultState().withProperty(method_16761(direction4), Boolean.valueOf(true)), 2);
								} else if ((double)world.random.nextFloat() < 0.05 && this.method_16763(world, blockPos2.up(), Direction.UP)) {
									world.setBlockState(blockPos2, this.getDefaultState().withProperty(field_18563, Boolean.valueOf(true)), 2);
								}
							}
						} else if (this.method_16763(world, blockPos2, direction)) {
							world.setBlockState(pos, state.withProperty(method_16761(direction), Boolean.valueOf(true)), 2);
						}
					}
				} else {
					if (direction == Direction.UP && pos.getY() < 255) {
						if (this.method_16759(world, pos, direction)) {
							world.setBlockState(pos, state.withProperty(field_18563, Boolean.valueOf(true)), 2);
							return;
						}

						if (world.method_8579(blockPos)) {
							if (!this.method_16758(world, pos)) {
								return;
							}

							BlockState blockState3 = state;

							for (Direction direction5 : Direction.DirectionType.HORIZONTAL) {
								if (random.nextBoolean() || !this.method_16763(world, blockPos.offset(direction5), Direction.UP)) {
									blockState3 = blockState3.withProperty(method_16761(direction5), Boolean.valueOf(false));
								}
							}

							if (this.method_16767(blockState3)) {
								world.setBlockState(blockPos, blockState3, 2);
							}

							return;
						}
					}

					if (pos.getY() > 0) {
						BlockPos blockPos5 = pos.down();
						BlockState blockState4 = world.getBlockState(blockPos5);
						if (blockState4.isAir() || blockState4.getBlock() == this) {
							BlockState blockState5 = blockState4.isAir() ? this.getDefaultState() : blockState4;
							BlockState blockState6 = this.method_16760(state, blockState5, random);
							if (blockState5 != blockState6 && this.method_16767(blockState6)) {
								world.setBlockState(blockPos5, blockState6, 2);
							}
						}
					}
				}
			}
		}
	}

	private BlockState method_16760(BlockState blockState, BlockState blockState2, Random random) {
		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			if (random.nextBoolean()) {
				BooleanProperty booleanProperty = method_16761(direction);
				if ((Boolean)blockState.getProperty(booleanProperty)) {
					blockState2 = blockState2.withProperty(booleanProperty, Boolean.valueOf(true));
				}
			}
		}

		return blockState2;
	}

	private boolean method_16767(BlockState blockState) {
		return (Boolean)blockState.getProperty(field_18564)
			|| (Boolean)blockState.getProperty(field_18565)
			|| (Boolean)blockState.getProperty(field_18566)
			|| (Boolean)blockState.getProperty(field_18567);
	}

	private boolean method_16758(BlockView blockView, BlockPos blockPos) {
		int i = 4;
		Iterable<BlockPos.Mutable> iterable = BlockPos.Mutable.mutableIterate(
			blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4, blockPos.getX() + 4, blockPos.getY() + 1, blockPos.getZ() + 4
		);
		int j = 5;

		for (BlockPos blockPos2 : iterable) {
			if (blockView.getBlockState(blockPos2).getBlock() == this) {
				if (--j <= 0) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos());
		return blockState.getBlock() == this ? this.method_16766(blockState) < field_18568.size() : super.canReplace(state, itemPlacementContext);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		boolean bl = blockState.getBlock() == this;
		BlockState blockState2 = bl ? blockState : this.getDefaultState();

		for (Direction direction : context.method_16021()) {
			if (direction != Direction.DOWN) {
				BooleanProperty booleanProperty = method_16761(direction);
				boolean bl2 = bl && (Boolean)blockState.getProperty(booleanProperty);
				if (!bl2 && this.method_16759(context.getWorld(), context.getBlockPos(), direction)) {
					return blockState2.withProperty(booleanProperty, Boolean.valueOf(true));
				}
			}
		}

		return bl ? blockState2 : null;
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		if (!world.isClient && stack.getItem() == Items.SHEARS) {
			player.method_15932(Stats.MINED.method_21429(this));
			player.addExhaustion(0.005F);
			onBlockBreak(world, pos, new ItemStack(Blocks.VINE));
		} else {
			super.method_8651(world, player, pos, state, blockEntity, stack);
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18563, field_18564, field_18565, field_18566, field_18567);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.withProperty(field_18564, state.getProperty(field_18566))
					.withProperty(field_18565, state.getProperty(field_18567))
					.withProperty(field_18566, state.getProperty(field_18564))
					.withProperty(field_18567, state.getProperty(field_18565));
			case COUNTERCLOCKWISE_90:
				return state.withProperty(field_18564, state.getProperty(field_18565))
					.withProperty(field_18565, state.getProperty(field_18566))
					.withProperty(field_18566, state.getProperty(field_18567))
					.withProperty(field_18567, state.getProperty(field_18564));
			case CLOCKWISE_90:
				return state.withProperty(field_18564, state.getProperty(field_18567))
					.withProperty(field_18565, state.getProperty(field_18564))
					.withProperty(field_18566, state.getProperty(field_18565))
					.withProperty(field_18567, state.getProperty(field_18566));
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.withProperty(field_18564, state.getProperty(field_18566)).withProperty(field_18566, state.getProperty(field_18564));
			case FRONT_BACK:
				return state.withProperty(field_18565, state.getProperty(field_18567)).withProperty(field_18567, state.getProperty(field_18565));
			default:
				return super.withMirror(state, mirror);
		}
	}

	public static BooleanProperty method_16761(Direction direction) {
		return (BooleanProperty)field_18568.get(direction);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
