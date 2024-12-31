package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TrapdoorBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	public static final BooleanProperty OPEN = BooleanProperty.of("open");
	public static final EnumProperty<TrapdoorBlock.TrapdoorType> HALF = EnumProperty.of("half", TrapdoorBlock.TrapdoorType.class);
	protected static final Box field_12811 = new Box(0.0, 0.0, 0.0, 0.1875, 1.0, 1.0);
	protected static final Box field_12812 = new Box(0.8125, 0.0, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12813 = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 0.1875);
	protected static final Box field_12814 = new Box(0.0, 0.0, 0.8125, 1.0, 1.0, 1.0);
	protected static final Box field_12809 = new Box(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0);
	protected static final Box field_12810 = new Box(0.0, 0.8125, 0.0, 1.0, 1.0, 1.0);

	protected TrapdoorBlock(Material material) {
		super(material);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false).with(HALF, TrapdoorBlock.TrapdoorType.BOTTOM));
		float f = 0.5F;
		float g = 1.0F;
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		Box box;
		if ((Boolean)state.get(OPEN)) {
			switch ((Direction)state.get(FACING)) {
				case NORTH:
				default:
					box = field_12814;
					break;
				case SOUTH:
					box = field_12813;
					break;
				case WEST:
					box = field_12812;
					break;
				case EAST:
					box = field_12811;
			}
		} else if (state.get(HALF) == TrapdoorBlock.TrapdoorType.TOP) {
			box = field_12810;
		} else {
			box = field_12809;
		}

		return box;
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
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return !(Boolean)view.getBlockState(pos).get(OPEN);
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		if (this.material == Material.IRON) {
			return true;
		} else {
			blockState = blockState.withDefaultValue(OPEN);
			world.setBlockState(blockPos, blockState, 2);
			this.method_11640(playerEntity, world, blockPos, (Boolean)blockState.get(OPEN));
			return true;
		}
	}

	protected void method_11640(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, boolean bl) {
		if (bl) {
			int i = this.material == Material.IRON ? 1037 : 1007;
			world.syncWorldEvent(playerEntity, i, blockPos, 0);
		} else {
			int j = this.material == Material.IRON ? 1036 : 1013;
			world.syncWorldEvent(playerEntity, j, blockPos, 0);
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (!world.isClient) {
			boolean bl = world.isReceivingRedstonePower(blockPos);
			if (bl || block.getDefaultState().emitsRedstonePower()) {
				boolean bl2 = (Boolean)blockState.get(OPEN);
				if (bl2 != bl) {
					world.setBlockState(blockPos, blockState.with(OPEN, bl), 2);
					this.method_11640(null, world, blockPos, bl);
				}
			}
		}
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		BlockState blockState = this.getDefaultState();
		if (dir.getAxis().isHorizontal()) {
			blockState = blockState.with(FACING, dir).with(OPEN, false);
			blockState = blockState.with(HALF, y > 0.5F ? TrapdoorBlock.TrapdoorType.TOP : TrapdoorBlock.TrapdoorType.BOTTOM);
		} else {
			blockState = blockState.with(FACING, entity.getHorizontalDirection().getOpposite()).with(OPEN, false);
			blockState = blockState.with(HALF, dir == Direction.UP ? TrapdoorBlock.TrapdoorType.BOTTOM : TrapdoorBlock.TrapdoorType.TOP);
		}

		return blockState;
	}

	@Override
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		return true;
	}

	protected static Direction getDirection(int data) {
		switch (data & 3) {
			case 0:
				return Direction.NORTH;
			case 1:
				return Direction.SOUTH;
			case 2:
				return Direction.WEST;
			case 3:
			default:
				return Direction.EAST;
		}
	}

	protected static int getDirectionData(Direction dir) {
		switch (dir) {
			case NORTH:
				return 0;
			case SOUTH:
				return 1;
			case WEST:
				return 2;
			case EAST:
			default:
				return 3;
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState()
			.with(FACING, getDirection(data))
			.with(OPEN, (data & 4) != 0)
			.with(HALF, (data & 8) == 0 ? TrapdoorBlock.TrapdoorType.BOTTOM : TrapdoorBlock.TrapdoorType.TOP);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= getDirectionData(state.get(FACING));
		if ((Boolean)state.get(OPEN)) {
			i |= 4;
		}

		if (state.get(HALF) == TrapdoorBlock.TrapdoorType.TOP) {
			i |= 8;
		}

		return i;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, OPEN, HALF);
	}

	public static enum TrapdoorType implements StringIdentifiable {
		TOP("top"),
		BOTTOM("bottom");

		private final String name;

		private TrapdoorType(String string2) {
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
