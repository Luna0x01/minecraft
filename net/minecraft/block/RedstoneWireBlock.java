package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_4338;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class RedstoneWireBlock extends Block {
	public static final EnumProperty<WireConnection> field_18443 = Properties.NORTH_WIRE_CONNECTION;
	public static final EnumProperty<WireConnection> field_18444 = Properties.EAST_WIRE_CONNECTION;
	public static final EnumProperty<WireConnection> field_18445 = Properties.SOUTH_WIRE_CONNECTION;
	public static final EnumProperty<WireConnection> field_18446 = Properties.WEST_WIRE_CONNECTION;
	public static final IntProperty POWER = Properties.POWER;
	public static final Map<Direction, EnumProperty<WireConnection>> field_18448 = Maps.newEnumMap(
		ImmutableMap.of(Direction.NORTH, field_18443, Direction.EAST, field_18444, Direction.SOUTH, field_18445, Direction.WEST, field_18446)
	);
	protected static final VoxelShape[] field_18449 = new VoxelShape[]{
		Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0),
		Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0),
		Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 16.0),
		Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
		Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 13.0, 1.0, 13.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 13.0, 1.0, 16.0),
		Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
		Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 3.0, 16.0, 1.0, 13.0),
		Block.createCuboidShape(0.0, 0.0, 3.0, 16.0, 1.0, 16.0),
		Block.createCuboidShape(3.0, 0.0, 0.0, 16.0, 1.0, 13.0),
		Block.createCuboidShape(3.0, 0.0, 0.0, 16.0, 1.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 13.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)
	};
	private boolean wiresGivePower = true;
	private final Set<BlockPos> affectedNeighbors = Sets.newHashSet();

	public RedstoneWireBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(field_18443, WireConnection.NONE)
				.withProperty(field_18444, WireConnection.NONE)
				.withProperty(field_18445, WireConnection.NONE)
				.withProperty(field_18446, WireConnection.NONE)
				.withProperty(POWER, Integer.valueOf(0))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18449[method_11625(state)];
	}

	private static int method_11625(BlockState blockState) {
		int i = 0;
		boolean bl = blockState.getProperty(field_18443) != WireConnection.NONE;
		boolean bl2 = blockState.getProperty(field_18444) != WireConnection.NONE;
		boolean bl3 = blockState.getProperty(field_18445) != WireConnection.NONE;
		boolean bl4 = blockState.getProperty(field_18446) != WireConnection.NONE;
		if (bl || bl3 && !bl && !bl2 && !bl4) {
			i |= 1 << Direction.NORTH.getHorizontal();
		}

		if (bl2 || bl4 && !bl && !bl2 && !bl3) {
			i |= 1 << Direction.EAST.getHorizontal();
		}

		if (bl3 || bl && !bl2 && !bl3 && !bl4) {
			i |= 1 << Direction.SOUTH.getHorizontal();
		}

		if (bl4 || bl2 && !bl && !bl3 && !bl4) {
			i |= 1 << Direction.WEST.getHorizontal();
		}

		return i;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockView blockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		return this.getDefaultState()
			.withProperty(field_18446, this.method_8878(blockView, blockPos, Direction.WEST))
			.withProperty(field_18444, this.method_8878(blockView, blockPos, Direction.EAST))
			.withProperty(field_18443, this.method_8878(blockView, blockPos, Direction.NORTH))
			.withProperty(field_18445, this.method_8878(blockView, blockPos, Direction.SOUTH));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.DOWN) {
			return state;
		} else {
			return direction == Direction.UP
				? state.withProperty(field_18446, this.method_8878(world, pos, Direction.WEST))
					.withProperty(field_18444, this.method_8878(world, pos, Direction.EAST))
					.withProperty(field_18443, this.method_8878(world, pos, Direction.NORTH))
					.withProperty(field_18445, this.method_8878(world, pos, Direction.SOUTH))
				: state.withProperty((Property)field_18448.get(direction), this.method_8878(world, pos, direction));
		}
	}

	@Override
	public void method_16584(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				WireConnection wireConnection = blockState.getProperty((Property<WireConnection>)field_18448.get(direction));
				if (wireConnection != WireConnection.NONE && iWorld.getBlockState(pooled.set(blockPos).move(direction)).getBlock() != this) {
					pooled.move(Direction.DOWN);
					BlockState blockState2 = iWorld.getBlockState(pooled);
					if (blockState2.getBlock() != Blocks.OBSERVER) {
						BlockPos blockPos2 = pooled.offset(direction.getOpposite());
						BlockState blockState3 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), iWorld.getBlockState(blockPos2), iWorld, pooled, blockPos2);
						method_16572(blockState2, blockState3, iWorld, pooled, i);
					}

					pooled.set(blockPos).move(direction).move(Direction.UP);
					BlockState blockState4 = iWorld.getBlockState(pooled);
					if (blockState4.getBlock() != Blocks.OBSERVER) {
						BlockPos blockPos3 = pooled.offset(direction.getOpposite());
						BlockState blockState5 = blockState4.getStateForNeighborUpdate(direction.getOpposite(), iWorld.getBlockState(blockPos3), iWorld, pooled, blockPos3);
						method_16572(blockState4, blockState5, iWorld, pooled, i);
					}
				}
			}
		}
	}

	private WireConnection method_8878(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState = blockView.getBlockState(blockPos.offset(direction));
		BlockState blockState2 = blockView.getBlockState(blockPos.up());
		if (!blockState2.method_16907()) {
			boolean bl = blockView.getBlockState(blockPos2).method_16913() || blockView.getBlockState(blockPos2).getBlock() == Blocks.GLOWSTONE;
			if (bl && connectsTo(blockView.getBlockState(blockPos2.up()))) {
				if (blockState.method_16905()) {
					return WireConnection.UP;
				}

				return WireConnection.SIDE;
			}
		}

		return !connectsTo(blockView.getBlockState(blockPos2), direction) && (blockState.method_16907() || !connectsTo(blockView.getBlockState(blockPos2.down())))
			? WireConnection.NONE
			: WireConnection.SIDE;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		return blockState.method_16913() || blockState.getBlock() == Blocks.GLOWSTONE;
	}

	private BlockState update(World world, BlockPos pos, BlockState state) {
		state = this.method_8875(world, pos, state);
		List<BlockPos> list = Lists.newArrayList(this.affectedNeighbors);
		this.affectedNeighbors.clear();

		for (BlockPos blockPos : list) {
			world.updateNeighborsAlways(blockPos, this);
		}

		return state;
	}

	private BlockState method_8875(World world, BlockPos blockPos, BlockState blockState) {
		BlockState blockState2 = blockState;
		int i = (Integer)blockState.getProperty(POWER);
		int j = 0;
		j = this.method_16728(j, blockState);
		this.wiresGivePower = false;
		int k = world.getReceivedRedstonePower(blockPos);
		this.wiresGivePower = true;
		if (k > 0 && k > j - 1) {
			j = k;
		}

		int l = 0;

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			boolean bl = blockPos2.getX() != blockPos.getX() || blockPos2.getZ() != blockPos.getZ();
			BlockState blockState3 = world.getBlockState(blockPos2);
			if (bl) {
				l = this.method_16728(l, blockState3);
			}

			if (blockState3.method_16907() && !world.getBlockState(blockPos.up()).method_16907()) {
				if (bl && blockPos.getY() >= blockPos.getY()) {
					l = this.method_16728(l, world.getBlockState(blockPos2.up()));
				}
			} else if (!blockState3.method_16907() && bl && blockPos.getY() <= blockPos.getY()) {
				l = this.method_16728(l, world.getBlockState(blockPos2.down()));
			}
		}

		if (l > j) {
			j = l - 1;
		} else if (j > 0) {
			j--;
		} else {
			j = 0;
		}

		if (k > j - 1) {
			j = k;
		}

		if (i != j) {
			blockState = blockState.withProperty(POWER, Integer.valueOf(j));
			if (world.getBlockState(blockPos) == blockState2) {
				world.setBlockState(blockPos, blockState, 2);
			}

			this.affectedNeighbors.add(blockPos);

			for (Direction direction2 : Direction.values()) {
				this.affectedNeighbors.add(blockPos.offset(direction2));
			}
		}

		return blockState;
	}

	private void updateNeighbors(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() == this) {
			world.updateNeighborsAlways(pos, this);

			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (oldState.getBlock() != state.getBlock() && !world.isClient) {
			this.update(world, pos, state);

			for (Direction direction : Direction.DirectionType.VERTICAL) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}

			for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
				this.updateNeighbors(world, pos.offset(direction2));
			}

			for (Direction direction3 : Direction.DirectionType.HORIZONTAL) {
				BlockPos blockPos = pos.offset(direction3);
				if (world.getBlockState(blockPos).method_16907()) {
					this.updateNeighbors(world, blockPos.up());
				} else {
					this.updateNeighbors(world, blockPos.down());
				}
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && state.getBlock() != newState.getBlock()) {
			super.onStateReplaced(state, world, pos, newState, moved);
			if (!world.isClient) {
				for (Direction direction : Direction.values()) {
					world.updateNeighborsAlways(pos.offset(direction), this);
				}

				this.update(world, pos, state);

				for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
					this.updateNeighbors(world, pos.offset(direction2));
				}

				for (Direction direction3 : Direction.DirectionType.HORIZONTAL) {
					BlockPos blockPos = pos.offset(direction3);
					if (world.getBlockState(blockPos).method_16907()) {
						this.updateNeighbors(world, blockPos.up());
					} else {
						this.updateNeighbors(world, blockPos.down());
					}
				}
			}
		}
	}

	private int method_16728(int i, BlockState blockState) {
		if (blockState.getBlock() != this) {
			return i;
		} else {
			int j = (Integer)blockState.getProperty(POWER);
			return j > i ? j : i;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.isClient) {
			if (state.canPlaceAt(world, pos)) {
				this.update(world, pos, state);
			} else {
				state.method_16867(world, pos, 0);
				world.method_8553(pos);
			}
		}
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return !this.wiresGivePower ? 0 : state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!this.wiresGivePower) {
			return 0;
		} else {
			int i = (Integer)state.getProperty(POWER);
			if (i == 0) {
				return 0;
			} else if (direction == Direction.UP) {
				return i;
			} else {
				EnumSet<Direction> enumSet = EnumSet.noneOf(Direction.class);

				for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
					if (this.connectsTo(world, pos, direction2)) {
						enumSet.add(direction2);
					}
				}

				if (direction.getAxis().isHorizontal() && enumSet.isEmpty()) {
					return i;
				} else {
					return enumSet.contains(direction) && !enumSet.contains(direction.rotateYCounterclockwise()) && !enumSet.contains(direction.rotateYClockwise()) ? i : 0;
				}
			}
		}
	}

	private boolean connectsTo(BlockView view, BlockPos pos, Direction dir) {
		BlockPos blockPos = pos.offset(dir);
		BlockState blockState = view.getBlockState(blockPos);
		boolean bl = blockState.method_16907();
		boolean bl2 = view.getBlockState(pos.up()).method_16907();
		if (!bl2 && bl && connectsTo(view, blockPos.up())) {
			return true;
		} else if (connectsTo(blockState, dir)) {
			return true;
		} else {
			return blockState.getBlock() == Blocks.REPEATER
					&& blockState.getProperty(AbstractRedstoneGateBlock.POWERED)
					&& blockState.getProperty(AbstractRedstoneGateBlock.FACING) == dir
				? true
				: !bl && connectsTo(view, blockPos.down());
		}
	}

	protected static boolean connectsTo(BlockView view, BlockPos pos) {
		return connectsTo(view.getBlockState(pos));
	}

	protected static boolean connectsTo(BlockState state) {
		return connectsTo(state, null);
	}

	protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
		Block block = state.getBlock();
		if (block == Blocks.REDSTONE_WIRE) {
			return true;
		} else if (state.getBlock() == Blocks.REPEATER) {
			Direction direction = state.getProperty(RepeaterBlock.FACING);
			return direction == dir || direction.getOpposite() == dir;
		} else {
			return Blocks.OBSERVER == state.getBlock() ? dir == state.getProperty(ObserverBlock.FACING) : state.emitsRedstonePower() && dir != null;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return this.wiresGivePower;
	}

	public static int getColorIntensity(int i) {
		float f = (float)i / 15.0F;
		float g = f * 0.6F + 0.4F;
		if (i == 0) {
			g = 0.3F;
		}

		float h = f * f * 0.7F - 0.5F;
		float j = f * f * 0.6F - 0.7F;
		if (h < 0.0F) {
			h = 0.0F;
		}

		if (j < 0.0F) {
			j = 0.0F;
		}

		int k = MathHelper.clamp((int)(g * 255.0F), 0, 255);
		int l = MathHelper.clamp((int)(h * 255.0F), 0, 255);
		int m = MathHelper.clamp((int)(j * 255.0F), 0, 255);
		return 0xFF000000 | k << 16 | l << 8 | m;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		int i = (Integer)state.getProperty(POWER);
		if (i != 0) {
			double d = (double)pos.getX() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			double e = (double)((float)pos.getY() + 0.0625F);
			double f = (double)pos.getZ() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			float g = (float)i / 15.0F;
			float h = g * 0.6F + 0.4F;
			float j = Math.max(0.0F, g * g * 0.7F - 0.5F);
			float k = Math.max(0.0F, g * g * 0.6F - 0.7F);
			world.method_16343(new class_4338(h, j, k, 1.0F), d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.withProperty(field_18443, state.getProperty(field_18445))
					.withProperty(field_18444, state.getProperty(field_18446))
					.withProperty(field_18445, state.getProperty(field_18443))
					.withProperty(field_18446, state.getProperty(field_18444));
			case COUNTERCLOCKWISE_90:
				return state.withProperty(field_18443, state.getProperty(field_18444))
					.withProperty(field_18444, state.getProperty(field_18445))
					.withProperty(field_18445, state.getProperty(field_18446))
					.withProperty(field_18446, state.getProperty(field_18443));
			case CLOCKWISE_90:
				return state.withProperty(field_18443, state.getProperty(field_18446))
					.withProperty(field_18444, state.getProperty(field_18443))
					.withProperty(field_18445, state.getProperty(field_18444))
					.withProperty(field_18446, state.getProperty(field_18445));
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.withProperty(field_18443, state.getProperty(field_18445)).withProperty(field_18445, state.getProperty(field_18443));
			case FRONT_BACK:
				return state.withProperty(field_18444, state.getProperty(field_18446)).withProperty(field_18446, state.getProperty(field_18444));
			default:
				return super.withMirror(state, mirror);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18443, field_18444, field_18445, field_18446, POWER);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
