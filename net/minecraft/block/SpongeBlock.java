package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SpongeBlock extends Block {
	public static final BooleanProperty WET = BooleanProperty.of("wet");

	protected SpongeBlock() {
		super(Material.SPONGE);
		this.setDefaultState(this.stateManager.getDefaultState().with(WET, false));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate(this.getTranslationKey() + ".dry.name");
	}

	@Override
	public int getMeta(BlockState state) {
		return state.get(WET) ? 1 : 0;
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		this.attemptAbsorbWater(world, pos, state);
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		this.attemptAbsorbWater(world, blockPos, blockState);
		super.method_8641(blockState, world, blockPos, block);
	}

	protected void attemptAbsorbWater(World world, BlockPos pos, BlockState state) {
		if (!(Boolean)state.get(WET) && this.absorbWater(world, pos)) {
			world.setBlockState(pos, state.with(WET, true), 2);
			world.syncGlobalEvent(2001, pos, Block.getIdByBlock(Blocks.WATER));
		}
	}

	private boolean absorbWater(World world, BlockPos pos) {
		Queue<Pair<BlockPos, Integer>> queue = Lists.newLinkedList();
		List<BlockPos> list = Lists.newArrayList();
		queue.add(new Pair<>(pos, 0));
		int i = 0;

		while (!queue.isEmpty()) {
			Pair<BlockPos, Integer> pair = (Pair<BlockPos, Integer>)queue.poll();
			BlockPos blockPos = pair.getLeft();
			int j = pair.getRight();

			for (Direction direction : Direction.values()) {
				BlockPos blockPos2 = blockPos.offset(direction);
				if (world.getBlockState(blockPos2).getMaterial() == Material.WATER) {
					world.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 2);
					list.add(blockPos2);
					i++;
					if (j < 6) {
						queue.add(new Pair<>(blockPos2, j + 1));
					}
				}
			}

			if (i > 64) {
				break;
			}
		}

		for (BlockPos blockPos3 : list) {
			world.updateNeighborsAlways(blockPos3, Blocks.AIR);
		}

		return i > 0;
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		stacks.add(new ItemStack(item, 1, 0));
		stacks.add(new ItemStack(item, 1, 1));
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(WET, (data & 1) == 1);
	}

	@Override
	public int getData(BlockState state) {
		return state.get(WET) ? 1 : 0;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, WET);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.get(WET)) {
			Direction direction = Direction.random(random);
			if (direction != Direction.UP && !world.getBlockState(pos.offset(direction)).method_11739()) {
				double d = (double)pos.getX();
				double e = (double)pos.getY();
				double f = (double)pos.getZ();
				if (direction == Direction.DOWN) {
					e -= 0.05;
					d += random.nextDouble();
					f += random.nextDouble();
				} else {
					e += random.nextDouble() * 0.8;
					if (direction.getAxis() == Direction.Axis.X) {
						f += random.nextDouble();
						if (direction == Direction.EAST) {
							d++;
						} else {
							d += 0.05;
						}
					} else {
						d += random.nextDouble();
						if (direction == Direction.SOUTH) {
							f++;
						} else {
							f += 0.05;
						}
					}
				}

				world.addParticle(ParticleType.WATER_DRIP, d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}
}
