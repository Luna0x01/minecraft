package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MyceliumBlock extends Block {
	public static final BooleanProperty SNOWY = BooleanProperty.of("snowy");

	protected MyceliumBlock() {
		super(Material.GRASS, MaterialColor.PURPLE);
		this.setDefaultState(this.stateManager.getDefaultState().with(SNOWY, false));
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		Block block = view.getBlockState(pos.up()).getBlock();
		return state.with(SNOWY, block == Blocks.SNOW || block == Blocks.SNOW_LAYER);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			if (world.getLightLevelWithNeighbours(pos.up()) < 4 && world.getBlockState(pos.up()).getBlock().getOpacity() > 2) {
				world.setBlockState(pos, Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.DIRT));
			} else {
				if (world.getLightLevelWithNeighbours(pos.up()) >= 9) {
					for (int i = 0; i < 4; i++) {
						BlockPos blockPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
						BlockState blockState = world.getBlockState(blockPos);
						Block block = world.getBlockState(blockPos.up()).getBlock();
						if (blockState.getBlock() == Blocks.DIRT
							&& blockState.get(DirtBlock.VARIANT) == DirtBlock.DirtType.DIRT
							&& world.getLightLevelWithNeighbours(blockPos.up()) >= 4
							&& block.getOpacity() <= 2) {
							world.setBlockState(blockPos, this.getDefaultState());
						}
					}
				}
			}
		}
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		super.randomDisplayTick(world, pos, state, rand);
		if (rand.nextInt(10) == 0) {
			world.addParticle(
				ParticleType.TOWN_AURA,
				(double)((float)pos.getX() + rand.nextFloat()),
				(double)((float)pos.getY() + 1.1F),
				(double)((float)pos.getZ() + rand.nextFloat()),
				0.0,
				0.0,
				0.0
			);
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Blocks.DIRT.getDropItem(Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.DIRT), random, id);
	}

	@Override
	public int getData(BlockState state) {
		return 0;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, SNOWY);
	}
}
