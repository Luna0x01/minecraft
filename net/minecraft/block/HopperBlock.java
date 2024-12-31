package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
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
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HopperBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", new Predicate<Direction>() {
		public boolean apply(Direction direction) {
			return direction != Direction.UP;
		}
	});
	public static final BooleanProperty ENABLED = BooleanProperty.of("enabled");

	public HopperBlock() {
		super(Material.IRON, MaterialColor.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.DOWN).with(ENABLED, true));
		this.setItemGroup(ItemGroup.REDSTONE);
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void appendCollisionBoxes(World world, BlockPos pos, BlockState state, Box box, List<Box> list, Entity entity) {
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		float f = 0.125F;
		this.setBoundingBox(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		this.setBoundingBox(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		this.setBoundingBox(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
		super.appendCollisionBoxes(world, pos, state, box, list, entity);
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
	public void onCreation(World world, BlockPos pos, BlockState state) {
		this.updateEnabled(world, pos, state);
	}

	@Override
	public boolean onUse(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction direction, float posX, float posY, float posZ) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof HopperBlockEntity) {
				player.openInventory((HopperBlockEntity)blockEntity);
				player.incrementStat(Stats.INTERACTIONS_WITH_HOPPER);
			}

			return true;
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		this.updateEnabled(world, pos, state);
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
	public int getBlockType() {
		return 3;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return true;
	}

	public static Direction getDirection(int data) {
		return Direction.getById(data & 7);
	}

	public static boolean isEnabled(int data) {
		return (data & 8) != 8;
	}

	@Override
	public boolean hasComparatorOutput() {
		return true;
	}

	@Override
	public int getComparatorOutput(World world, BlockPos pos) {
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
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, ENABLED);
	}
}
