package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StemBlock extends PlantBlock implements Growable {
	public static final IntProperty AGE = IntProperty.of("age", 0, 7);
	public static final DirectionProperty FACING = DirectionProperty.of("facing", new Predicate<Direction>() {
		public boolean apply(Direction direction) {
			return direction != Direction.DOWN;
		}
	});
	private final Block mainBlock;

	protected StemBlock(Block block) {
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0).with(FACING, Direction.UP));
		this.mainBlock = block;
		this.setTickRandomly(true);
		float f = 0.125F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
		this.setItemGroup(null);
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		state = state.with(FACING, Direction.UP);

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			if (view.getBlockState(pos.offset(direction)).getBlock() == this.mainBlock) {
				state = state.with(FACING, direction);
				break;
			}
		}

		return state;
	}

	@Override
	protected boolean canPlantOnTop(Block block) {
		return block == Blocks.FARMLAND;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		super.onScheduledTick(world, pos, state, rand);
		if (world.getLightLevelWithNeighbours(pos.up()) >= 9) {
			float f = CropBlock.getAvailableMoisture(this, world, pos);
			if (rand.nextInt((int)(25.0F / f) + 1) == 0) {
				int i = (Integer)state.get(AGE);
				if (i < 7) {
					state = state.with(AGE, i + 1);
					world.setBlockState(pos, state, 2);
				} else {
					for (Direction direction : Direction.DirectionType.HORIZONTAL) {
						if (world.getBlockState(pos.offset(direction)).getBlock() == this.mainBlock) {
							return;
						}
					}

					pos = pos.offset(Direction.DirectionType.HORIZONTAL.getRandomDirection(rand));
					Block block = world.getBlockState(pos.down()).getBlock();
					if (world.getBlockState(pos).getBlock().material == Material.AIR && (block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.GRASS)) {
						world.setBlockState(pos, this.mainBlock.getDefaultState());
					}
				}
			}
		}
	}

	public void grow(World world, BlockPos pos, BlockState state) {
		int i = (Integer)state.get(AGE) + MathHelper.nextInt(world.random, 2, 5);
		world.setBlockState(pos, state.with(AGE, Math.min(7, i)), 2);
	}

	@Override
	public int getColor(BlockState state) {
		if (state.getBlock() != this) {
			return super.getColor(state);
		} else {
			int i = (Integer)state.get(AGE);
			int j = i * 32;
			int k = 255 - i * 8;
			int l = i * 4;
			return j << 16 | k << 8 | l;
		}
	}

	@Override
	public int getBlockColor(BlockView view, BlockPos pos, int id) {
		return this.getColor(view.getBlockState(pos));
	}

	@Override
	public void setBlockItemBounds() {
		float f = 0.125F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		this.boundingBoxMaxY = (double)((float)((Integer)view.getBlockState(pos).get(AGE) * 2 + 2) / 16.0F);
		float f = 0.125F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (float)this.boundingBoxMaxY, 0.5F + f);
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		super.randomDropAsItem(world, pos, state, chance, id);
		if (!world.isClient) {
			Item item = this.getSeeds();
			if (item != null) {
				int i = (Integer)state.get(AGE);

				for (int j = 0; j < 3; j++) {
					if (world.random.nextInt(15) <= i) {
						onBlockBreak(world, pos, new ItemStack(item));
					}
				}
			}
		}
	}

	protected Item getSeeds() {
		if (this.mainBlock == Blocks.PUMPKIN) {
			return Items.PUMPKIN_SEEDS;
		} else {
			return this.mainBlock == Blocks.MELON_BLOCK ? Items.MELON_SEEDS : null;
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return null;
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		Item item = this.getSeeds();
		return item != null ? item : null;
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
		return (Integer)state.get(AGE) != 7;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		this.grow(world, pos, state);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(AGE, data);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(AGE);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, AGE, FACING);
	}
}
