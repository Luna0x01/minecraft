package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RedstoneWireBlock extends Block {
	public static final EnumProperty<RedstoneWireBlock.RedstoneWireType> NORTH = EnumProperty.of("north", RedstoneWireBlock.RedstoneWireType.class);
	public static final EnumProperty<RedstoneWireBlock.RedstoneWireType> EAST = EnumProperty.of("east", RedstoneWireBlock.RedstoneWireType.class);
	public static final EnumProperty<RedstoneWireBlock.RedstoneWireType> SOUTH = EnumProperty.of("south", RedstoneWireBlock.RedstoneWireType.class);
	public static final EnumProperty<RedstoneWireBlock.RedstoneWireType> WEST = EnumProperty.of("west", RedstoneWireBlock.RedstoneWireType.class);
	public static final IntProperty POWER = IntProperty.of("power", 0, 15);
	protected static final Box[] field_12731 = new Box[]{
		new Box(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 0.8125),
		new Box(0.1875, 0.0, 0.1875, 0.8125, 0.0625, 1.0),
		new Box(0.0, 0.0, 0.1875, 0.8125, 0.0625, 0.8125),
		new Box(0.0, 0.0, 0.1875, 0.8125, 0.0625, 1.0),
		new Box(0.1875, 0.0, 0.0, 0.8125, 0.0625, 0.8125),
		new Box(0.1875, 0.0, 0.0, 0.8125, 0.0625, 1.0),
		new Box(0.0, 0.0, 0.0, 0.8125, 0.0625, 0.8125),
		new Box(0.0, 0.0, 0.0, 0.8125, 0.0625, 1.0),
		new Box(0.1875, 0.0, 0.1875, 1.0, 0.0625, 0.8125),
		new Box(0.1875, 0.0, 0.1875, 1.0, 0.0625, 1.0),
		new Box(0.0, 0.0, 0.1875, 1.0, 0.0625, 0.8125),
		new Box(0.0, 0.0, 0.1875, 1.0, 0.0625, 1.0),
		new Box(0.1875, 0.0, 0.0, 1.0, 0.0625, 0.8125),
		new Box(0.1875, 0.0, 0.0, 1.0, 0.0625, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.0625, 0.8125),
		new Box(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0)
	};
	private boolean wiresGivePower = true;
	private final Set<BlockPos> affectedNeighbors = Sets.newHashSet();

	public RedstoneWireBlock() {
		super(Material.DECORATION);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(NORTH, RedstoneWireBlock.RedstoneWireType.NONE)
				.with(EAST, RedstoneWireBlock.RedstoneWireType.NONE)
				.with(SOUTH, RedstoneWireBlock.RedstoneWireType.NONE)
				.with(WEST, RedstoneWireBlock.RedstoneWireType.NONE)
				.with(POWER, 0)
		);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12731[method_11625(state.getBlockState(view, pos))];
	}

	private static int method_11625(BlockState blockState) {
		int i = 0;
		boolean bl = blockState.get(NORTH) != RedstoneWireBlock.RedstoneWireType.NONE;
		boolean bl2 = blockState.get(EAST) != RedstoneWireBlock.RedstoneWireType.NONE;
		boolean bl3 = blockState.get(SOUTH) != RedstoneWireBlock.RedstoneWireType.NONE;
		boolean bl4 = blockState.get(WEST) != RedstoneWireBlock.RedstoneWireType.NONE;
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
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		state = state.with(WEST, this.getWireType(view, pos, Direction.WEST));
		state = state.with(EAST, this.getWireType(view, pos, Direction.EAST));
		state = state.with(NORTH, this.getWireType(view, pos, Direction.NORTH));
		return state.with(SOUTH, this.getWireType(view, pos, Direction.SOUTH));
	}

	private RedstoneWireBlock.RedstoneWireType getWireType(BlockView view, BlockPos pos, Direction dir) {
		BlockPos blockPos = pos.offset(dir);
		BlockState blockState = view.getBlockState(pos.offset(dir));
		if (!connectsTo(view.getBlockState(blockPos), dir) && (blockState.method_11734() || !connectsTo(view.getBlockState(blockPos.down())))) {
			BlockState blockState2 = view.getBlockState(pos.up());
			if (!blockState2.method_11734()) {
				boolean bl = view.getBlockState(blockPos).method_11739() || view.getBlockState(blockPos).getBlock() == Blocks.GLOWSTONE;
				if (bl && connectsTo(view.getBlockState(blockPos.up()))) {
					if (blockState.method_11733()) {
						return RedstoneWireBlock.RedstoneWireType.UP;
					}

					return RedstoneWireBlock.RedstoneWireType.SIDE;
				}
			}

			return RedstoneWireBlock.RedstoneWireType.NONE;
		} else {
			return RedstoneWireBlock.RedstoneWireType.SIDE;
		}
	}

	@Nullable
	@Override
	public Box getCollisionBox(BlockState state, World world, BlockPos pos) {
		return EMPTY_BOX;
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
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).method_11739() || world.getBlockState(pos.down()).getBlock() == Blocks.GLOWSTONE;
	}

	private BlockState update(World world, BlockPos pos, BlockState state) {
		state = this.update(world, pos, pos, state);
		List<BlockPos> list = Lists.newArrayList(this.affectedNeighbors);
		this.affectedNeighbors.clear();

		for (BlockPos blockPos : list) {
			world.updateNeighborsAlways(blockPos, this);
		}

		return state;
	}

	private BlockState update(World world, BlockPos pos1, BlockPos pos2, BlockState state) {
		BlockState blockState = state;
		int i = (Integer)state.get(POWER);
		int j = 0;
		j = this.getPower(world, pos2, j);
		this.wiresGivePower = false;
		int k = world.getReceivedRedstonePower(pos1);
		this.wiresGivePower = true;
		if (k > 0 && k > j - 1) {
			j = k;
		}

		int l = 0;

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos = pos1.offset(direction);
			boolean bl = blockPos.getX() != pos2.getX() || blockPos.getZ() != pos2.getZ();
			if (bl) {
				l = this.getPower(world, blockPos, l);
			}

			if (world.getBlockState(blockPos).method_11734() && !world.getBlockState(pos1.up()).method_11734()) {
				if (bl && pos1.getY() >= pos2.getY()) {
					l = this.getPower(world, blockPos.up(), l);
				}
			} else if (!world.getBlockState(blockPos).method_11734() && bl && pos1.getY() <= pos2.getY()) {
				l = this.getPower(world, blockPos.down(), l);
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
			state = state.with(POWER, j);
			if (world.getBlockState(pos1) == blockState) {
				world.setBlockState(pos1, state, 2);
			}

			this.affectedNeighbors.add(pos1);

			for (Direction direction2 : Direction.values()) {
				this.affectedNeighbors.add(pos1.offset(direction2));
			}
		}

		return state;
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
	public void onCreation(World world, BlockPos pos, BlockState state) {
		if (!world.isClient) {
			this.update(world, pos, state);

			for (Direction direction : Direction.DirectionType.VERTICAL) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}

			for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
				this.updateNeighbors(world, pos.offset(direction2));
			}

			for (Direction direction3 : Direction.DirectionType.HORIZONTAL) {
				BlockPos blockPos = pos.offset(direction3);
				if (world.getBlockState(blockPos).method_11734()) {
					this.updateNeighbors(world, blockPos.up());
				} else {
					this.updateNeighbors(world, blockPos.down());
				}
			}
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		super.onBreaking(world, pos, state);
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
				if (world.getBlockState(blockPos).method_11734()) {
					this.updateNeighbors(world, blockPos.up());
				} else {
					this.updateNeighbors(world, blockPos.down());
				}
			}
		}
	}

	private int getPower(World world, BlockPos pos, int power) {
		if (world.getBlockState(pos).getBlock() != this) {
			return power;
		} else {
			int i = (Integer)world.getBlockState(pos).get(POWER);
			return i > power ? i : power;
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (!world.isClient) {
			if (this.canBePlacedAtPos(world, blockPos)) {
				this.update(world, blockPos, blockState);
			} else {
				this.dropAsItem(world, blockPos, blockState, 0);
				world.setAir(blockPos);
			}
		}
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.REDSTONE;
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
			int i = (Integer)state.get(POWER);
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
		boolean bl = blockState.method_11734();
		boolean bl2 = view.getBlockState(pos.up()).method_11734();
		if (!bl2 && bl && connectsTo(view, blockPos.up())) {
			return true;
		} else if (connectsTo(blockState, dir)) {
			return true;
		} else {
			return blockState.getBlock() == Blocks.POWERED_REPEATER && blockState.get(AbstractRedstoneGateBlock.DIRECTION) == dir
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
		} else if (Blocks.UNPOWERED_REPEATER.method_11603(state)) {
			Direction direction = state.get(RepeaterBlock.DIRECTION);
			return direction == dir || direction.getOpposite() == dir;
		} else {
			return state.emitsRedstonePower() && dir != null;
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
		int i = (Integer)state.get(POWER);
		if (i != 0) {
			double d = (double)pos.getX() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			double e = (double)((float)pos.getY() + 0.0625F);
			double f = (double)pos.getZ() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			float g = (float)i / 15.0F;
			float h = g * 0.6F + 0.4F;
			float j = Math.max(0.0F, g * g * 0.7F - 0.5F);
			float k = Math.max(0.0F, g * g * 0.6F - 0.7F);
			world.addParticle(ParticleType.REDSTONE, d, e, f, (double)h, (double)j, (double)k);
		}
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.REDSTONE);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(POWER, data);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(POWER);
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

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, NORTH, EAST, SOUTH, WEST, POWER);
	}

	static enum RedstoneWireType implements StringIdentifiable {
		UP("up"),
		SIDE("side"),
		NONE("none");

		private final String name;

		private RedstoneWireType(String string2) {
			this.name = string2;
		}

		public String toString() {
			return this.asString();
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
