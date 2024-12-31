package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DoorBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	public static final BooleanProperty OPEN = BooleanProperty.of("open");
	public static final EnumProperty<DoorBlock.DoorType> HINGE = EnumProperty.of("hinge", DoorBlock.DoorType.class);
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");
	public static final EnumProperty<DoorBlock.HalfType> HALF = EnumProperty.of("half", DoorBlock.HalfType.class);
	protected static final Box field_12647 = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 0.1875);
	protected static final Box field_12648 = new Box(0.0, 0.0, 0.8125, 1.0, 1.0, 1.0);
	protected static final Box field_12645 = new Box(0.8125, 0.0, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12646 = new Box(0.0, 0.0, 0.0, 0.1875, 1.0, 1.0);

	protected DoorBlock(Material material) {
		super(material);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(OPEN, false)
				.with(HINGE, DoorBlock.DoorType.LEFT)
				.with(POWERED, false)
				.with(HALF, DoorBlock.HalfType.LOWER)
		);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		state = state.getBlockState(view, pos);
		Direction direction = state.get(FACING);
		boolean bl = !(Boolean)state.get(OPEN);
		boolean bl2 = state.get(HINGE) == DoorBlock.DoorType.RIGHT;
		switch (direction) {
			case EAST:
			default:
				return bl ? field_12646 : (bl2 ? field_12648 : field_12647);
			case SOUTH:
				return bl ? field_12647 : (bl2 ? field_12646 : field_12645);
			case WEST:
				return bl ? field_12645 : (bl2 ? field_12647 : field_12648);
			case NORTH:
				return bl ? field_12648 : (bl2 ? field_12645 : field_12646);
		}
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate((this.getTranslationKey() + ".name").replaceAll("tile", "item"));
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return isOpen(getTotalData(view, pos));
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	private int method_11604() {
		return this.material == Material.IRON ? 1011 : 1012;
	}

	private int method_11605() {
		return this.material == Material.IRON ? 1005 : 1006;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		if (state.getBlock() == Blocks.IRON_DOOR) {
			return MaterialColor.IRON;
		} else if (state.getBlock() == Blocks.WOODEN_DOOR) {
			return PlanksBlock.WoodType.OAK.getMaterialColor();
		} else if (state.getBlock() == Blocks.SPRUCE_DOOR) {
			return PlanksBlock.WoodType.SPRUCE.getMaterialColor();
		} else if (state.getBlock() == Blocks.BIRCH_DOOR) {
			return PlanksBlock.WoodType.BIRCH.getMaterialColor();
		} else if (state.getBlock() == Blocks.JUNGLE_DOOR) {
			return PlanksBlock.WoodType.JUNGLE.getMaterialColor();
		} else if (state.getBlock() == Blocks.ACACIA_DOOR) {
			return PlanksBlock.WoodType.ACACIA.getMaterialColor();
		} else {
			return state.getBlock() == Blocks.DARK_OAK_DOOR ? PlanksBlock.WoodType.DARK_OAK.getMaterialColor() : super.getMaterialColor(state, view, pos);
		}
	}

	@Override
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction direction, float f, float g, float h) {
		if (this.material == Material.IRON) {
			return false;
		} else {
			BlockPos blockPos = state.get(HALF) == DoorBlock.HalfType.LOWER ? pos : pos.down();
			BlockState blockState = pos.equals(blockPos) ? state : world.getBlockState(blockPos);
			if (blockState.getBlock() != this) {
				return false;
			} else {
				state = blockState.withDefaultValue(OPEN);
				world.setBlockState(blockPos, state, 10);
				world.onRenderRegionUpdate(blockPos, pos);
				world.syncWorldEvent(player, state.get(OPEN) ? this.method_11605() : this.method_11604(), pos, 0);
				return true;
			}
		}
	}

	public void activateDoor(World world, BlockPos pos, boolean isOpen) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == this) {
			BlockPos blockPos = blockState.get(HALF) == DoorBlock.HalfType.LOWER ? pos : pos.down();
			BlockState blockState2 = pos == blockPos ? blockState : world.getBlockState(blockPos);
			if (blockState2.getBlock() == this && (Boolean)blockState2.get(OPEN) != isOpen) {
				world.setBlockState(blockPos, blockState2.with(OPEN, isOpen), 10);
				world.onRenderRegionUpdate(blockPos, pos);
				world.syncWorldEvent(null, isOpen ? this.method_11605() : this.method_11604(), pos, 0);
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (state.get(HALF) == DoorBlock.HalfType.UPPER) {
			BlockPos blockPos = pos.down();
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.getBlock() != this) {
				world.setAir(pos);
			} else if (block != this) {
				blockState.neighbourUpdate(world, blockPos, block, neighborPos);
			}
		} else {
			boolean bl = false;
			BlockPos blockPos2 = pos.up();
			BlockState blockState2 = world.getBlockState(blockPos2);
			if (blockState2.getBlock() != this) {
				world.setAir(pos);
				bl = true;
			}

			if (!world.getBlockState(pos.down()).method_11739()) {
				world.setAir(pos);
				bl = true;
				if (blockState2.getBlock() == this) {
					world.setAir(blockPos2);
				}
			}

			if (bl) {
				if (!world.isClient) {
					this.dropAsItem(world, pos, state, 0);
				}
			} else {
				boolean bl2 = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(blockPos2);
				if (block != this && (bl2 || block.getDefaultState().emitsRedstonePower()) && bl2 != (Boolean)blockState2.get(POWERED)) {
					world.setBlockState(blockPos2, blockState2.with(POWERED, bl2), 2);
					if (bl2 != (Boolean)state.get(OPEN)) {
						world.setBlockState(pos, state.with(OPEN, bl2), 2);
						world.onRenderRegionUpdate(pos, pos);
						world.syncWorldEvent(null, bl2 ? this.method_11605() : this.method_11604(), pos, 0);
					}
				}
			}
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return state.get(HALF) == DoorBlock.HalfType.UPPER ? Items.AIR : this.getItem();
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return pos.getY() >= 255
			? false
			: world.getBlockState(pos.down()).method_11739() && super.canBePlacedAtPos(world, pos) && super.canBePlacedAtPos(world, pos.up());
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	public static int getTotalData(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		int i = blockState.getBlock().getData(blockState);
		boolean bl = isTopHalf(i);
		BlockState blockState2 = view.getBlockState(pos.down());
		int j = blockState2.getBlock().getData(blockState2);
		int k = bl ? j : i;
		BlockState blockState3 = view.getBlockState(pos.up());
		int l = blockState3.getBlock().getData(blockState3);
		int m = bl ? i : l;
		boolean bl2 = (m & 1) != 0;
		boolean bl3 = (m & 2) != 0;
		return removeHalf(k) | (bl ? 8 : 0) | (bl2 ? 16 : 0) | (bl3 ? 32 : 0);
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this.getItem());
	}

	private Item getItem() {
		if (this == Blocks.IRON_DOOR) {
			return Items.IRON_DOOR;
		} else if (this == Blocks.SPRUCE_DOOR) {
			return Items.SPRUCE_DOOR;
		} else if (this == Blocks.BIRCH_DOOR) {
			return Items.BIRCH_DOOR;
		} else if (this == Blocks.JUNGLE_DOOR) {
			return Items.JUNGLE_DOOR;
		} else if (this == Blocks.ACACIA_DOOR) {
			return Items.ACACIA_DOOR;
		} else {
			return this == Blocks.DARK_OAK_DOOR ? Items.DARK_OAK_DOOR : Items.OAK_DOOR;
		}
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		BlockPos blockPos = pos.down();
		BlockPos blockPos2 = pos.up();
		if (player.abilities.creativeMode && state.get(HALF) == DoorBlock.HalfType.UPPER && world.getBlockState(blockPos).getBlock() == this) {
			world.setAir(blockPos);
		}

		if (state.get(HALF) == DoorBlock.HalfType.LOWER && world.getBlockState(blockPos2).getBlock() == this) {
			if (player.abilities.creativeMode) {
				world.setAir(pos);
			}

			world.setAir(blockPos2);
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		if (state.get(HALF) == DoorBlock.HalfType.LOWER) {
			BlockState blockState = view.getBlockState(pos.up());
			if (blockState.getBlock() == this) {
				state = state.with(HINGE, blockState.get(HINGE)).with(POWERED, blockState.get(POWERED));
			}
		} else {
			BlockState blockState2 = view.getBlockState(pos.down());
			if (blockState2.getBlock() == this) {
				state = state.with(FACING, blockState2.get(FACING)).with(OPEN, blockState2.get(OPEN));
			}
		}

		return state;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.get(HALF) != DoorBlock.HalfType.LOWER ? state : state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return mirror == BlockMirror.NONE ? state : state.withRotation(mirror.getRotation(state.get(FACING))).withDefaultValue(HINGE);
	}

	@Override
	public BlockState stateFromData(int data) {
		return (data & 8) > 0
			? this.getDefaultState()
				.with(HALF, DoorBlock.HalfType.UPPER)
				.with(HINGE, (data & 1) > 0 ? DoorBlock.DoorType.RIGHT : DoorBlock.DoorType.LEFT)
				.with(POWERED, (data & 2) > 0)
			: this.getDefaultState()
				.with(HALF, DoorBlock.HalfType.LOWER)
				.with(FACING, Direction.fromHorizontal(data & 3).rotateYCounterclockwise())
				.with(OPEN, (data & 4) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		if (state.get(HALF) == DoorBlock.HalfType.UPPER) {
			i |= 8;
			if (state.get(HINGE) == DoorBlock.DoorType.RIGHT) {
				i |= 1;
			}

			if ((Boolean)state.get(POWERED)) {
				i |= 2;
			}
		} else {
			i |= ((Direction)state.get(FACING)).rotateYClockwise().getHorizontal();
			if ((Boolean)state.get(OPEN)) {
				i |= 4;
			}
		}

		return i;
	}

	protected static int removeHalf(int data) {
		return data & 7;
	}

	public static boolean isOpen(BlockView view, BlockPos pos) {
		return isOpen(getTotalData(view, pos));
	}

	public static Direction getDirection(BlockView view, BlockPos pos) {
		return getDirection(getTotalData(view, pos));
	}

	public static Direction getDirection(int data) {
		return Direction.fromHorizontal(data & 3).rotateYCounterclockwise();
	}

	protected static boolean isOpen(int totalData) {
		return (totalData & 4) != 0;
	}

	protected static boolean isTopHalf(int data) {
		return (data & 8) != 0;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, HALF, FACING, OPEN, HINGE, POWERED);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	public static enum DoorType implements StringIdentifiable {
		LEFT,
		RIGHT;

		public String toString() {
			return this.asString();
		}

		@Override
		public String asString() {
			return this == LEFT ? "left" : "right";
		}
	}

	public static enum HalfType implements StringIdentifiable {
		UPPER,
		LOWER;

		public String toString() {
			return this.asString();
		}

		@Override
		public String asString() {
			return this == UPPER ? "upper" : "lower";
		}
	}
}
