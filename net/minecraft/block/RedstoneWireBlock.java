package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
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
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
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
		Block block = view.getBlockState(pos.offset(dir)).getBlock();
		if (!connectsTo(view.getBlockState(blockPos), dir) && (block.isNormalBlock() || !connectsTo(view.getBlockState(blockPos.down())))) {
			Block block2 = view.getBlockState(pos.up()).getBlock();
			return !block2.isNormalBlock() && block.isNormalBlock() && connectsTo(view.getBlockState(blockPos.up()))
				? RedstoneWireBlock.RedstoneWireType.UP
				: RedstoneWireBlock.RedstoneWireType.NONE;
		} else {
			return RedstoneWireBlock.RedstoneWireType.SIDE;
		}
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		return null;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getBlockColor(BlockView view, BlockPos pos, int id) {
		BlockState blockState = view.getBlockState(pos);
		return blockState.getBlock() != this ? super.getBlockColor(view, pos, id) : this.getColorIntensity((Integer)blockState.get(POWER));
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return World.isOpaque(world, pos.down()) || world.getBlockState(pos.down()).getBlock() == Blocks.GLOWSTONE;
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

			if (world.getBlockState(blockPos).getBlock().isFullCube() && !world.getBlockState(pos1.up()).getBlock().isFullCube()) {
				if (bl && pos1.getY() >= pos2.getY()) {
					l = this.getPower(world, blockPos.up(), l);
				}
			} else if (!world.getBlockState(blockPos).getBlock().isFullCube() && bl && pos1.getY() <= pos2.getY()) {
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
				if (world.getBlockState(blockPos).getBlock().isFullCube()) {
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
				if (world.getBlockState(blockPos).getBlock().isFullCube()) {
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
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!world.isClient) {
			if (this.canBePlacedAtPos(world, pos)) {
				this.update(world, pos, state);
			} else {
				this.dropAsItem(world, pos, state, 0);
				world.setAir(pos);
			}
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.REDSTONE;
	}

	@Override
	public int getStrongRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return !this.wiresGivePower ? 0 : this.getWeakRedstonePower(view, pos, state, facing);
	}

	@Override
	public int getWeakRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		if (!this.wiresGivePower) {
			return 0;
		} else {
			int i = (Integer)state.get(POWER);
			if (i == 0) {
				return 0;
			} else if (facing == Direction.UP) {
				return i;
			} else {
				EnumSet<Direction> enumSet = EnumSet.noneOf(Direction.class);

				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					if (this.connectsTo(view, pos, direction)) {
						enumSet.add(direction);
					}
				}

				if (facing.getAxis().isHorizontal() && enumSet.isEmpty()) {
					return i;
				} else {
					return enumSet.contains(facing) && !enumSet.contains(facing.rotateYCounterclockwise()) && !enumSet.contains(facing.rotateYClockwise()) ? i : 0;
				}
			}
		}
	}

	private boolean connectsTo(BlockView view, BlockPos pos, Direction dir) {
		BlockPos blockPos = pos.offset(dir);
		BlockState blockState = view.getBlockState(blockPos);
		Block block = blockState.getBlock();
		boolean bl = block.isFullCube();
		boolean bl2 = view.getBlockState(pos.up()).getBlock().isFullCube();
		if (!bl2 && bl && connectsTo(view, blockPos.up())) {
			return true;
		} else if (connectsTo(blockState, dir)) {
			return true;
		} else {
			return block == Blocks.POWERED_REPEATER && blockState.get(AbstractRedstoneGateBlock.FACING) == dir ? true : !bl && connectsTo(view, blockPos.down());
		}
	}

	protected static boolean connectsTo(BlockView view, BlockPos pos) {
		return connectsTo(view.getBlockState(pos));
	}

	protected static boolean connectsTo(BlockState state) {
		return connectsTo(state, null);
	}

	protected static boolean connectsTo(BlockState state, Direction dir) {
		Block block = state.getBlock();
		if (block == Blocks.REDSTONE_WIRE) {
			return true;
		} else if (Blocks.UNPOWERED_REPEATER.isComparator(block)) {
			Direction direction = state.get(RepeaterBlock.FACING);
			return direction == dir || direction.getOpposite() == dir;
		} else {
			return block.emitsRedstonePower() && dir != null;
		}
	}

	@Override
	public boolean emitsRedstonePower() {
		return this.wiresGivePower;
	}

	private int getColorIntensity(int power) {
		float f = (float)power / 15.0F;
		float g = f * 0.6F + 0.4F;
		if (power == 0) {
			g = 0.3F;
		}

		float h = f * f * 0.7F - 0.5F;
		float i = f * f * 0.6F - 0.7F;
		if (h < 0.0F) {
			h = 0.0F;
		}

		if (i < 0.0F) {
			i = 0.0F;
		}

		int j = MathHelper.clamp((int)(g * 255.0F), 0, 255);
		int k = MathHelper.clamp((int)(h * 255.0F), 0, 255);
		int l = MathHelper.clamp((int)(i * 255.0F), 0, 255);
		return 0xFF000000 | j << 16 | k << 8 | l;
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		int i = (Integer)state.get(POWER);
		if (i != 0) {
			double d = (double)pos.getX() + 0.5 + ((double)rand.nextFloat() - 0.5) * 0.2;
			double e = (double)((float)pos.getY() + 0.0625F);
			double f = (double)pos.getZ() + 0.5 + ((double)rand.nextFloat() - 0.5) * 0.2;
			float g = (float)i / 15.0F;
			float h = g * 0.6F + 0.4F;
			float j = Math.max(0.0F, g * g * 0.7F - 0.5F);
			float k = Math.max(0.0F, g * g * 0.6F - 0.7F);
			world.addParticle(ParticleType.REDSTONE, d, e, f, (double)h, (double)j, (double)k);
		}
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Items.REDSTONE;
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
