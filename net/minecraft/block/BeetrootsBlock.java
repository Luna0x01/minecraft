package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BeetrootsBlock extends CropBlock {
	public static final IntProperty BEETROOT_AGE = Properties.AGE_3;
	private static final VoxelShape[] BEETROOT_AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
	};

	public BeetrootsBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public IntProperty getAge() {
		return BEETROOT_AGE;
	}

	@Override
	public int getMaxAge() {
		return 3;
	}

	@Override
	protected Itemable getSeedsItem() {
		return Items.BEETROOT_SEED;
	}

	@Override
	protected Itemable getHarvestItem() {
		return Items.BEETROOT;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(3) != 0) {
			super.scheduledTick(state, world, pos, random);
		}
	}

	@Override
	protected int getGrowthAmount(World world) {
		return super.getGrowthAmount(world) / 3;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(BEETROOT_AGE);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return BEETROOT_AGE_TO_SHAPE[state.getProperty(this.getAge())];
	}
}
