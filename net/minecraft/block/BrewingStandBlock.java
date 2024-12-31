package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4342;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BrewingStandBlock extends BlockWithEntity {
	public static final BooleanProperty[] HAS_BOTTLES = new BooleanProperty[]{Properties.HAS_BOTTLE_0, Properties.HAS_BOTTLE_1, Properties.HAS_BOTTLE_2};
	protected static final VoxelShape SHAPE = VoxelShapes.union(
		Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 14.0, 9.0)
	);

	public BrewingStandBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(HAS_BOTTLES[0], Boolean.valueOf(false))
				.withProperty(HAS_BOTTLES[1], Boolean.valueOf(false))
				.withProperty(HAS_BOTTLES[2], Boolean.valueOf(false))
		);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new BrewingStandBlockEntity();
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				player.openInventory((BrewingStandBlockEntity)blockEntity);
				player.method_15928(Stats.INTERACT_WITH_BREWINGSTAND);
			}

			return true;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				((BrewingStandBlockEntity)blockEntity).method_16791(itemStack.getName());
			}
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		double d = (double)((float)pos.getX() + 0.4F + random.nextFloat() * 0.2F);
		double e = (double)((float)pos.getY() + 0.7F + random.nextFloat() * 0.3F);
		double f = (double)((float)pos.getZ() + 0.4F + random.nextFloat() * 0.2F);
		world.method_16343(class_4342.field_21363, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				ItemScatterer.spawn(world, pos, (BrewingStandBlockEntity)blockEntity);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
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
		return RenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(HAS_BOTTLES[0], HAS_BOTTLES[1], HAS_BOTTLES[2]);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
