package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HopperBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", new Predicate<Direction>() {
		public boolean apply(@Nullable Direction direction) {
			return direction != Direction.UP;
		}
	});
	public static final BooleanProperty ENABLED = BooleanProperty.of("enabled");
	protected static final Box field_12685 = new Box(0.0, 0.0, 0.0, 1.0, 0.625, 1.0);
	protected static final Box field_12686 = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 0.125);
	protected static final Box field_12687 = new Box(0.0, 0.0, 0.875, 1.0, 1.0, 1.0);
	protected static final Box field_12688 = new Box(0.875, 0.0, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12689 = new Box(0.0, 0.0, 0.0, 0.125, 1.0, 1.0);

	public HopperBlock() {
		super(Material.IRON, MaterialColor.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.DOWN).with(ENABLED, true));
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return collisionBox;
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity) {
		appendCollisionBoxes(pos, entityBox, boxes, field_12685);
		appendCollisionBoxes(pos, entityBox, boxes, field_12689);
		appendCollisionBoxes(pos, entityBox, boxes, field_12688);
		appendCollisionBoxes(pos, entityBox, boxes, field_12686);
		appendCollisionBoxes(pos, entityBox, boxes, field_12687);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		Direction direction = dir.getOpposite();
		if (direction == Direction.UP) {
			direction = Direction.DOWN;
		}

		return this.getDefaultState().with(FACING, direction).with(ENABLED, true);
	}

	@Override
	public BlockEntity createBlockEntity(World world, int id) {
		return new HopperBlockEntity();
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof HopperBlockEntity) {
				((HopperBlockEntity)blockEntity).setCustomName(itemStack.getCustomName());
			}
		}
	}

	@Override
	public boolean method_11568(BlockState state) {
		return true;
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		this.updateEnabled(world, pos, state);
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
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof HopperBlockEntity) {
				playerEntity.openInventory((HopperBlockEntity)blockEntity);
				playerEntity.incrementStat(Stats.INTERACTIONS_WITH_HOPPER);
			}

			return true;
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		this.updateEnabled(world, blockPos, blockState);
	}

	private void updateEnabled(World world, BlockPos pos, BlockState state) {
		boolean bl = !world.isReceivingRedstonePower(pos);
		if (bl != (Boolean)state.get(ENABLED)) {
			world.setBlockState(pos, state.with(ENABLED, bl), 4);
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof HopperBlockEntity) {
			ItemScatterer.spawn(world, pos, (HopperBlockEntity)blockEntity);
			world.updateHorizontalAdjacent(pos, this);
		}

		super.onBreaking(world, pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		return true;
	}

	public static Direction getDirection(int data) {
		return Direction.getById(data & 7);
	}

	public static boolean isEnabled(int data) {
		return (data & 8) != 8;
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(FACING, getDirection(data)).with(ENABLED, isEnabled(data));
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getId();
		if (!(Boolean)state.get(ENABLED)) {
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
		return new StateManager(this, FACING, ENABLED);
	}
}
