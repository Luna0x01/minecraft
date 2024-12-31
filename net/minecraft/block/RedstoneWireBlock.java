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
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class RedstoneWireBlock extends Block {
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
	public static final IntProperty POWER = Properties.POWER;
	public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.field_11043,
			WIRE_CONNECTION_NORTH,
			Direction.field_11034,
			WIRE_CONNECTION_EAST,
			Direction.field_11035,
			WIRE_CONNECTION_SOUTH,
			Direction.field_11039,
			WIRE_CONNECTION_WEST
		)
	);
	protected static final VoxelShape[] WIRE_CONNECTIONS_TO_SHAPE = new VoxelShape[]{
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

	public RedstoneWireBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(WIRE_CONNECTION_NORTH, WireConnection.field_12687)
				.with(WIRE_CONNECTION_EAST, WireConnection.field_12687)
				.with(WIRE_CONNECTION_SOUTH, WireConnection.field_12687)
				.with(WIRE_CONNECTION_WEST, WireConnection.field_12687)
				.with(POWER, Integer.valueOf(0))
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return WIRE_CONNECTIONS_TO_SHAPE[getWireConnectionMask(blockState)];
	}

	private static int getWireConnectionMask(BlockState blockState) {
		int i = 0;
		boolean bl = blockState.get(WIRE_CONNECTION_NORTH) != WireConnection.field_12687;
		boolean bl2 = blockState.get(WIRE_CONNECTION_EAST) != WireConnection.field_12687;
		boolean bl3 = blockState.get(WIRE_CONNECTION_SOUTH) != WireConnection.field_12687;
		boolean bl4 = blockState.get(WIRE_CONNECTION_WEST) != WireConnection.field_12687;
		if (bl || bl3 && !bl && !bl2 && !bl4) {
			i |= 1 << Direction.field_11043.getHorizontal();
		}

		if (bl2 || bl4 && !bl && !bl2 && !bl3) {
			i |= 1 << Direction.field_11034.getHorizontal();
		}

		if (bl3 || bl && !bl2 && !bl3 && !bl4) {
			i |= 1 << Direction.field_11035.getHorizontal();
		}

		if (bl4 || bl2 && !bl && !bl3 && !bl4) {
			i |= 1 << Direction.field_11039.getHorizontal();
		}

		return i;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		return this.getDefaultState()
			.with(WIRE_CONNECTION_WEST, this.getRenderConnectionType(blockView, blockPos, Direction.field_11039))
			.with(WIRE_CONNECTION_EAST, this.getRenderConnectionType(blockView, blockPos, Direction.field_11034))
			.with(WIRE_CONNECTION_NORTH, this.getRenderConnectionType(blockView, blockPos, Direction.field_11043))
			.with(WIRE_CONNECTION_SOUTH, this.getRenderConnectionType(blockView, blockPos, Direction.field_11035));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction == Direction.field_11033) {
			return blockState;
		} else {
			return direction == Direction.field_11036
				? blockState.with(WIRE_CONNECTION_WEST, this.getRenderConnectionType(iWorld, blockPos, Direction.field_11039))
					.with(WIRE_CONNECTION_EAST, this.getRenderConnectionType(iWorld, blockPos, Direction.field_11034))
					.with(WIRE_CONNECTION_NORTH, this.getRenderConnectionType(iWorld, blockPos, Direction.field_11043))
					.with(WIRE_CONNECTION_SOUTH, this.getRenderConnectionType(iWorld, blockPos, Direction.field_11035))
				: blockState.with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), this.getRenderConnectionType(iWorld, blockPos, direction));
		}
	}

	@Override
	public void method_9517(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : Direction.Type.field_11062) {
				WireConnection wireConnection = blockState.get((Property<WireConnection>)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
				if (wireConnection != WireConnection.field_12687 && iWorld.getBlockState(pooledMutable.method_10114(blockPos).method_10118(direction)).getBlock() != this) {
					pooledMutable.method_10118(Direction.field_11033);
					BlockState blockState2 = iWorld.getBlockState(pooledMutable);
					if (blockState2.getBlock() != Blocks.field_10282) {
						BlockPos blockPos2 = pooledMutable.offset(direction.getOpposite());
						BlockState blockState3 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), iWorld.getBlockState(blockPos2), iWorld, pooledMutable, blockPos2);
						replaceBlock(blockState2, blockState3, iWorld, pooledMutable, i);
					}

					pooledMutable.method_10114(blockPos).method_10118(direction).method_10118(Direction.field_11036);
					BlockState blockState4 = iWorld.getBlockState(pooledMutable);
					if (blockState4.getBlock() != Blocks.field_10282) {
						BlockPos blockPos3 = pooledMutable.offset(direction.getOpposite());
						BlockState blockState5 = blockState4.getStateForNeighborUpdate(direction.getOpposite(), iWorld.getBlockState(blockPos3), iWorld, pooledMutable, blockPos3);
						replaceBlock(blockState4, blockState5, iWorld, pooledMutable, i);
					}
				}
			}
		}
	}

	private WireConnection getRenderConnectionType(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState = blockView.getBlockState(blockPos2);
		BlockPos blockPos3 = blockPos.up();
		BlockState blockState2 = blockView.getBlockState(blockPos3);
		if (!blockState2.isSimpleFullBlock(blockView, blockPos3)) {
			boolean bl = blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.field_11036) || blockState.getBlock() == Blocks.field_10312;
			if (bl && connectsTo(blockView.getBlockState(blockPos2.up()))) {
				if (blockState.method_21743(blockView, blockPos2)) {
					return WireConnection.field_12686;
				}

				return WireConnection.field_12689;
			}
		}

		return !connectsTo(blockState, direction) && (blockState.isSimpleFullBlock(blockView, blockPos2) || !connectsTo(blockView.getBlockState(blockPos2.down())))
			? WireConnection.field_12687
			: WireConnection.field_12689;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		return blockState2.isSideSolidFullSquare(viewableWorld, blockPos2, Direction.field_11036) || blockState2.getBlock() == Blocks.field_10312;
	}

	private BlockState update(World world, BlockPos blockPos, BlockState blockState) {
		blockState = this.updateLogic(world, blockPos, blockState);
		List<BlockPos> list = Lists.newArrayList(this.affectedNeighbors);
		this.affectedNeighbors.clear();

		for (BlockPos blockPos2 : list) {
			world.updateNeighborsAlways(blockPos2, this);
		}

		return blockState;
	}

	private BlockState updateLogic(World world, BlockPos blockPos, BlockState blockState) {
		BlockState blockState2 = blockState;
		int i = (Integer)blockState.get(POWER);
		this.wiresGivePower = false;
		int j = world.getReceivedRedstonePower(blockPos);
		this.wiresGivePower = true;
		int k = 0;
		if (j < 15) {
			for (Direction direction : Direction.Type.field_11062) {
				BlockPos blockPos2 = blockPos.offset(direction);
				BlockState blockState3 = world.getBlockState(blockPos2);
				k = this.increasePower(k, blockState3);
				BlockPos blockPos3 = blockPos.up();
				if (blockState3.isSimpleFullBlock(world, blockPos2) && !world.getBlockState(blockPos3).isSimpleFullBlock(world, blockPos3)) {
					k = this.increasePower(k, world.getBlockState(blockPos2.up()));
				} else if (!blockState3.isSimpleFullBlock(world, blockPos2)) {
					k = this.increasePower(k, world.getBlockState(blockPos2.down()));
				}
			}
		}

		int l = k - 1;
		if (j > l) {
			l = j;
		}

		if (i != l) {
			blockState = blockState.with(POWER, Integer.valueOf(l));
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

	private void updateNeighbors(World world, BlockPos blockPos) {
		if (world.getBlockState(blockPos).getBlock() == this) {
			world.updateNeighborsAlways(blockPos, this);

			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(blockPos.offset(direction), this);
			}
		}
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock() && !world.isClient) {
			this.update(world, blockPos, blockState);

			for (Direction direction : Direction.Type.field_11064) {
				world.updateNeighborsAlways(blockPos.offset(direction), this);
			}

			for (Direction direction2 : Direction.Type.field_11062) {
				this.updateNeighbors(world, blockPos.offset(direction2));
			}

			for (Direction direction3 : Direction.Type.field_11062) {
				BlockPos blockPos2 = blockPos.offset(direction3);
				if (world.getBlockState(blockPos2).isSimpleFullBlock(world, blockPos2)) {
					this.updateNeighbors(world, blockPos2.up());
				} else {
					this.updateNeighbors(world, blockPos2.down());
				}
			}
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
			if (!world.isClient) {
				for (Direction direction : Direction.values()) {
					world.updateNeighborsAlways(blockPos.offset(direction), this);
				}

				this.update(world, blockPos, blockState);

				for (Direction direction2 : Direction.Type.field_11062) {
					this.updateNeighbors(world, blockPos.offset(direction2));
				}

				for (Direction direction3 : Direction.Type.field_11062) {
					BlockPos blockPos2 = blockPos.offset(direction3);
					if (world.getBlockState(blockPos2).isSimpleFullBlock(world, blockPos2)) {
						this.updateNeighbors(world, blockPos2.up());
					} else {
						this.updateNeighbors(world, blockPos2.down());
					}
				}
			}
		}
	}

	private int increasePower(int i, BlockState blockState) {
		if (blockState.getBlock() != this) {
			return i;
		} else {
			int j = (Integer)blockState.get(POWER);
			return j > i ? j : i;
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!world.isClient) {
			if (blockState.canPlaceAt(world, blockPos)) {
				this.update(world, blockPos, blockState);
			} else {
				dropStacks(blockState, world, blockPos);
				world.clearBlockState(blockPos, false);
			}
		}
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return !this.wiresGivePower ? 0 : blockState.getWeakRedstonePower(blockView, blockPos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		if (!this.wiresGivePower) {
			return 0;
		} else {
			int i = (Integer)blockState.get(POWER);
			if (i == 0) {
				return 0;
			} else if (direction == Direction.field_11036) {
				return i;
			} else {
				EnumSet<Direction> enumSet = EnumSet.noneOf(Direction.class);

				for (Direction direction2 : Direction.Type.field_11062) {
					if (this.method_10478(blockView, blockPos, direction2)) {
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

	private boolean method_10478(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState = blockView.getBlockState(blockPos2);
		boolean bl = blockState.isSimpleFullBlock(blockView, blockPos2);
		BlockPos blockPos3 = blockPos.up();
		boolean bl2 = blockView.getBlockState(blockPos3).isSimpleFullBlock(blockView, blockPos3);
		if (!bl2 && bl && connectsTo(blockView, blockPos2.up())) {
			return true;
		} else if (connectsTo(blockState, direction)) {
			return true;
		} else {
			return blockState.getBlock() == Blocks.field_10450
					&& blockState.get(AbstractRedstoneGateBlock.POWERED)
					&& blockState.get(AbstractRedstoneGateBlock.FACING) == direction
				? true
				: !bl && connectsTo(blockView, blockPos2.down());
		}
	}

	protected static boolean connectsTo(BlockView blockView, BlockPos blockPos) {
		return connectsTo(blockView.getBlockState(blockPos));
	}

	protected static boolean connectsTo(BlockState blockState) {
		return connectsTo(blockState, null);
	}

	protected static boolean connectsTo(BlockState blockState, @Nullable Direction direction) {
		Block block = blockState.getBlock();
		if (block == Blocks.field_10091) {
			return true;
		} else if (blockState.getBlock() == Blocks.field_10450) {
			Direction direction2 = blockState.get(RepeaterBlock.FACING);
			return direction2 == direction || direction2.getOpposite() == direction;
		} else {
			return Blocks.field_10282 == blockState.getBlock()
				? direction == blockState.get(ObserverBlock.FACING)
				: blockState.emitsRedstonePower() && direction != null;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return this.wiresGivePower;
	}

	public static int getWireColor(int i) {
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
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		int i = (Integer)blockState.get(POWER);
		if (i != 0) {
			double d = (double)blockPos.getX() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			double e = (double)((float)blockPos.getY() + 0.0625F);
			double f = (double)blockPos.getZ() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			float g = (float)i / 15.0F;
			float h = g * 0.6F + 0.4F;
			float j = Math.max(0.0F, g * g * 0.7F - 0.5F);
			float k = Math.max(0.0F, g * g * 0.6F - 0.7F);
			world.addParticle(new DustParticleEffect(h, j, k, 1.0F), d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case field_11464:
				return blockState.with(WIRE_CONNECTION_NORTH, blockState.get(WIRE_CONNECTION_SOUTH))
					.with(WIRE_CONNECTION_EAST, blockState.get(WIRE_CONNECTION_WEST))
					.with(WIRE_CONNECTION_SOUTH, blockState.get(WIRE_CONNECTION_NORTH))
					.with(WIRE_CONNECTION_WEST, blockState.get(WIRE_CONNECTION_EAST));
			case field_11465:
				return blockState.with(WIRE_CONNECTION_NORTH, blockState.get(WIRE_CONNECTION_EAST))
					.with(WIRE_CONNECTION_EAST, blockState.get(WIRE_CONNECTION_SOUTH))
					.with(WIRE_CONNECTION_SOUTH, blockState.get(WIRE_CONNECTION_WEST))
					.with(WIRE_CONNECTION_WEST, blockState.get(WIRE_CONNECTION_NORTH));
			case field_11463:
				return blockState.with(WIRE_CONNECTION_NORTH, blockState.get(WIRE_CONNECTION_WEST))
					.with(WIRE_CONNECTION_EAST, blockState.get(WIRE_CONNECTION_NORTH))
					.with(WIRE_CONNECTION_SOUTH, blockState.get(WIRE_CONNECTION_EAST))
					.with(WIRE_CONNECTION_WEST, blockState.get(WIRE_CONNECTION_SOUTH));
			default:
				return blockState;
		}
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		switch (blockMirror) {
			case field_11300:
				return blockState.with(WIRE_CONNECTION_NORTH, blockState.get(WIRE_CONNECTION_SOUTH)).with(WIRE_CONNECTION_SOUTH, blockState.get(WIRE_CONNECTION_NORTH));
			case field_11301:
				return blockState.with(WIRE_CONNECTION_EAST, blockState.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_WEST, blockState.get(WIRE_CONNECTION_EAST));
			default:
				return super.mirror(blockState, blockMirror);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWER);
	}
}
