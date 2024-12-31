package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class CactusBlock extends Block {
	public static final IntProperty AGE = Properties.AGE_15;
	protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
	protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	protected CactusBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(AGE, Integer.valueOf(0)));
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.method_8535(pos, true);
		} else {
			BlockPos blockPos = pos.up();
			if (world.method_8579(blockPos)) {
				int i = 1;

				while (world.getBlockState(pos.down(i)).getBlock() == this) {
					i++;
				}

				if (i < 3) {
					int j = (Integer)state.getProperty(AGE);
					if (j == 15) {
						world.setBlockState(blockPos, this.getDefaultState());
						BlockState blockState = state.withProperty(AGE, Integer.valueOf(0));
						world.setBlockState(pos, blockState, 4);
						blockState.neighborUpdate(world, blockPos, this, pos);
					} else {
						world.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(j + 1)), 4);
					}
				}
			}
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos) {
		return COLLISION_SHAPE;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return true;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (!state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockState blockState = world.getBlockState(pos.offset(direction));
			Material material = blockState.getMaterial();
			if (material.isSolid() || world.getFluidState(pos.offset(direction)).matches(FluidTags.LAVA)) {
				return false;
			}
		}

		Block block = world.getBlockState(pos.down()).getBlock();
		return (block == Blocks.CACTUS || block == Blocks.SAND || block == Blocks.RED_SAND) && !world.getBlockState(pos.up()).getMaterial().isFluid();
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		entity.damage(DamageSource.CACTUS, 1.0F);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(AGE);
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
