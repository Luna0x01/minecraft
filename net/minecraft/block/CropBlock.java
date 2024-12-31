package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CropBlock extends PlantBlock implements Growable {
	public static final IntProperty AGE = IntProperty.of("age", 0, 7);
	private static final Box[] field_12638 = new Box[]{
		new Box(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.25, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.375, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.625, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.75, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 0.875, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
	};

	protected CropBlock() {
		this.setDefaultState(this.stateManager.getDefaultState().with(this.getAge(), 0));
		this.setTickRandomly(true);
		this.setItemGroup(null);
		this.setStrength(0.0F);
		this.setBlockSoundGroup(BlockSoundGroup.field_12761);
		this.disableStats();
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return field_12638[state.get(this.getAge())];
	}

	@Override
	protected boolean method_11579(BlockState blockState) {
		return blockState.getBlock() == Blocks.FARMLAND;
	}

	protected IntProperty getAge() {
		return AGE;
	}

	public int getMaxAge() {
		return 7;
	}

	protected int getAge(BlockState state) {
		return (Integer)state.get(this.getAge());
	}

	public BlockState withAge(int age) {
		return this.getDefaultState().with(this.getAge(), age);
	}

	public boolean isMature(BlockState state) {
		return (Integer)state.get(this.getAge()) >= this.getMaxAge();
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		super.onScheduledTick(world, pos, state, rand);
		if (world.getLightLevelWithNeighbours(pos.up()) >= 9) {
			int i = this.getAge(state);
			if (i < this.getMaxAge()) {
				float f = getAvailableMoisture(this, world, pos);
				if (rand.nextInt((int)(25.0F / f) + 1) == 0) {
					world.setBlockState(pos, this.withAge(i + 1), 2);
				}
			}
		}
	}

	public void applyGrowth(World world, BlockPos pos, BlockState state) {
		int i = this.getAge(state) + this.getGrowthAmount(world);
		int j = this.getMaxAge();
		if (i > j) {
			i = j;
		}

		world.setBlockState(pos, this.withAge(i), 2);
	}

	protected int getGrowthAmount(World world) {
		return MathHelper.nextInt(world.random, 2, 5);
	}

	protected static float getAvailableMoisture(Block block, World world, BlockPos pos) {
		float f = 1.0F;
		BlockPos blockPos = pos.down();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				float g = 0.0F;
				BlockState blockState = world.getBlockState(blockPos.add(i, 0, j));
				if (blockState.getBlock() == Blocks.FARMLAND) {
					g = 1.0F;
					if ((Integer)blockState.get(FarmlandBlock.MOISTURE) > 0) {
						g = 3.0F;
					}
				}

				if (i != 0 || j != 0) {
					g /= 4.0F;
				}

				f += g;
			}
		}

		BlockPos blockPos2 = pos.north();
		BlockPos blockPos3 = pos.south();
		BlockPos blockPos4 = pos.west();
		BlockPos blockPos5 = pos.east();
		boolean bl = block == world.getBlockState(blockPos4).getBlock() || block == world.getBlockState(blockPos5).getBlock();
		boolean bl2 = block == world.getBlockState(blockPos2).getBlock() || block == world.getBlockState(blockPos3).getBlock();
		if (bl && bl2) {
			f /= 2.0F;
		} else {
			boolean bl3 = block == world.getBlockState(blockPos4.north()).getBlock()
				|| block == world.getBlockState(blockPos5.north()).getBlock()
				|| block == world.getBlockState(blockPos5.south()).getBlock()
				|| block == world.getBlockState(blockPos4.south()).getBlock();
			if (bl3) {
				f /= 2.0F;
			}
		}

		return f;
	}

	@Override
	public boolean canPlantAt(World world, BlockPos pos, BlockState state) {
		return (world.getLightLevel(pos) >= 8 || world.hasDirectSunlight(pos)) && this.method_11579(world.getBlockState(pos.down()));
	}

	protected Item getSeedItem() {
		return Items.WHEAT_SEEDS;
	}

	protected Item getHarvestItem() {
		return Items.WHEAT;
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		super.randomDropAsItem(world, pos, state, chance, 0);
		if (!world.isClient) {
			int i = this.getAge(state);
			if (i >= this.getMaxAge()) {
				int j = 3 + id;

				for (int k = 0; k < j; k++) {
					if (world.random.nextInt(2 * this.getMaxAge()) <= i) {
						onBlockBreak(world, pos, new ItemStack(this.getSeedItem()));
					}
				}
			}
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return this.isMature(state) ? this.getHarvestItem() : this.getSeedItem();
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this.getSeedItem());
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
		return !this.isMature(state);
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		this.applyGrowth(world, pos, state);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.withAge(data);
	}

	@Override
	public int getData(BlockState state) {
		return this.getAge(state);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, AGE);
	}
}
