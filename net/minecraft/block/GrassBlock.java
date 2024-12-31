package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GrassBlock extends Block implements Growable {
	public static final BooleanProperty SNOWY = BooleanProperty.of("snowy");

	protected GrassBlock() {
		super(Material.GRASS);
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
	public int getColor() {
		return GrassColors.getColor(0.5, 1.0);
	}

	@Override
	public int getColor(BlockState state) {
		return this.getColor();
	}

	@Override
	public int getBlockColor(BlockView view, BlockPos pos, int id) {
		return BiomeColors.getGrassColor(view, pos);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			if (world.getLightLevelWithNeighbours(pos.up()) < 4 && world.getBlockState(pos.up()).getBlock().getOpacity() > 2) {
				world.setBlockState(pos, Blocks.DIRT.getDefaultState());
			} else {
				if (world.getLightLevelWithNeighbours(pos.up()) >= 9) {
					for (int i = 0; i < 4; i++) {
						BlockPos blockPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
						Block block = world.getBlockState(blockPos.up()).getBlock();
						BlockState blockState = world.getBlockState(blockPos);
						if (blockState.getBlock() == Blocks.DIRT
							&& blockState.get(DirtBlock.VARIANT) == DirtBlock.DirtType.DIRT
							&& world.getLightLevelWithNeighbours(blockPos.up()) >= 4
							&& block.getOpacity() <= 2) {
							world.setBlockState(blockPos, Blocks.GRASS.getDefaultState());
						}
					}
				}
			}
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Blocks.DIRT.getDropItem(Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.DIRT), random, id);
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
		return true;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.up();

		label38:
		for (int i = 0; i < 128; i++) {
			BlockPos blockPos2 = blockPos;

			for (int j = 0; j < i / 16; j++) {
				blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
				if (world.getBlockState(blockPos2.down()).getBlock() != Blocks.GRASS || world.getBlockState(blockPos2).getBlock().isFullCube()) {
					continue label38;
				}
			}

			if (world.getBlockState(blockPos2).getBlock().material == Material.AIR) {
				if (random.nextInt(8) == 0) {
					FlowerBlock.FlowerType flowerType = world.getBiome(blockPos2).pickFlower(random, blockPos2);
					FlowerBlock flowerBlock = flowerType.getColor().getBlock();
					BlockState blockState = flowerBlock.getDefaultState().with(flowerBlock.getFlowerProperties(), flowerType);
					if (flowerBlock.canPlantAt(world, blockPos2, blockState)) {
						world.setBlockState(blockPos2, blockState, 3);
					}
				} else {
					BlockState blockState2 = Blocks.TALLGRASS.getDefaultState().with(TallPlantBlock.TYPE, TallPlantBlock.GrassType.GRASS);
					if (Blocks.TALLGRASS.canPlantAt(world, blockPos2, blockState2)) {
						world.setBlockState(blockPos2, blockState2, 3);
					}
				}
			}
		}
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT_MIPPED;
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
