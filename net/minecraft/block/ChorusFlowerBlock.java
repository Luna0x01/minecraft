package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class ChorusFlowerBlock extends Block {
	public static final IntProperty AGE = Properties.AGE_5;
	private final ChorusPlantBlock plantBlock;

	protected ChorusFlowerBlock(ChorusPlantBlock chorusPlantBlock, Block.Builder builder) {
		super(builder);
		this.plantBlock = chorusPlantBlock;
		this.setDefaultState(this.stateManager.method_16923().withProperty(AGE, Integer.valueOf(0)));
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.method_8535(pos, true);
		} else {
			BlockPos blockPos = pos.up();
			if (world.method_8579(blockPos) && blockPos.getY() < 256) {
				int i = (Integer)state.getProperty(AGE);
				if (i < 5) {
					boolean bl = false;
					boolean bl2 = false;
					BlockState blockState = world.getBlockState(pos.down());
					Block block = blockState.getBlock();
					if (block == Blocks.END_STONE) {
						bl = true;
					} else if (block == this.plantBlock) {
						int j = 1;

						for (int k = 0; k < 4; k++) {
							Block block2 = world.getBlockState(pos.down(j + 1)).getBlock();
							if (block2 != this.plantBlock) {
								if (block2 == Blocks.END_STONE) {
									bl2 = true;
								}
								break;
							}

							j++;
						}

						if (j < 2 || j <= random.nextInt(bl2 ? 5 : 4)) {
							bl = true;
						}
					} else if (blockState.isAir()) {
						bl = true;
					}

					if (bl && isSurroundedByAir(world, blockPos, null) && world.method_8579(pos.up(2))) {
						world.setBlockState(pos, this.plantBlock.withConnectionProperties(world, pos), 2);
						this.grow(world, blockPos, i);
					} else if (i < 4) {
						int l = random.nextInt(4);
						if (bl2) {
							l++;
						}

						boolean bl3 = false;

						for (int m = 0; m < l; m++) {
							Direction direction = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
							BlockPos blockPos2 = pos.offset(direction);
							if (world.method_8579(blockPos2) && world.method_8579(blockPos2.down()) && isSurroundedByAir(world, blockPos2, direction.getOpposite())) {
								this.grow(world, blockPos2, i + 1);
								bl3 = true;
							}
						}

						if (bl3) {
							world.setBlockState(pos, this.plantBlock.withConnectionProperties(world, pos), 2);
						} else {
							this.die(world, pos);
						}
					} else {
						this.die(world, pos);
					}
				}
			}
		}
	}

	private void grow(World world, BlockPos pos, int age) {
		world.setBlockState(pos, this.getDefaultState().withProperty(AGE, Integer.valueOf(age)), 2);
		world.syncGlobalEvent(1033, pos, 0);
	}

	private void die(World world, BlockPos pos) {
		world.setBlockState(pos, this.getDefaultState().withProperty(AGE, Integer.valueOf(5)), 2);
		world.syncGlobalEvent(1034, pos, 0);
	}

	private static boolean isSurroundedByAir(RenderBlockView world, BlockPos pos, @Nullable Direction exceptDirection) {
		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			if (direction != exceptDirection && !world.method_8579(pos.offset(direction))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (direction != Direction.UP && !state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		Block block = blockState.getBlock();
		if (block != this.plantBlock && block != Blocks.END_STONE) {
			if (!blockState.isAir()) {
				return false;
			} else {
				boolean bl = false;

				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					BlockState blockState2 = world.getBlockState(pos.offset(direction));
					if (blockState2.getBlock() == this.plantBlock) {
						if (bl) {
							return false;
						}

						bl = true;
					} else if (!blockState2.isAir()) {
						return false;
					}
				}

				return bl;
			}
		} else {
			return true;
		}
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.method_8651(world, player, pos, state, blockEntity, stack);
		onBlockBreak(world, pos, new ItemStack(this));
	}

	@Override
	protected ItemStack createStackFromBlock(BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(AGE);
	}

	public static void generate(IWorld world, BlockPos pos, Random random, int size) {
		world.setBlockState(pos, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).withConnectionProperties(world, pos), 2);
		generate(world, pos, random, pos, size, 0);
	}

	private static void generate(IWorld world, BlockPos pos, Random random, BlockPos rootPos, int size, int layer) {
		ChorusPlantBlock chorusPlantBlock = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
		int i = random.nextInt(4) + 1;
		if (layer == 0) {
			i++;
		}

		for (int j = 0; j < i; j++) {
			BlockPos blockPos = pos.up(j + 1);
			if (!isSurroundedByAir(world, blockPos, null)) {
				return;
			}

			world.setBlockState(blockPos, chorusPlantBlock.withConnectionProperties(world, blockPos), 2);
			world.setBlockState(blockPos.down(), chorusPlantBlock.withConnectionProperties(world, blockPos.down()), 2);
		}

		boolean bl = false;
		if (layer < 4) {
			int k = random.nextInt(4);
			if (layer == 0) {
				k++;
			}

			for (int l = 0; l < k; l++) {
				Direction direction = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
				BlockPos blockPos2 = pos.up(i).offset(direction);
				if (Math.abs(blockPos2.getX() - rootPos.getX()) < size
					&& Math.abs(blockPos2.getZ() - rootPos.getZ()) < size
					&& world.method_8579(blockPos2)
					&& world.method_8579(blockPos2.down())
					&& isSurroundedByAir(world, blockPos2, direction.getOpposite())) {
					bl = true;
					world.setBlockState(blockPos2, chorusPlantBlock.withConnectionProperties(world, blockPos2), 2);
					world.setBlockState(
						blockPos2.offset(direction.getOpposite()), chorusPlantBlock.withConnectionProperties(world, blockPos2.offset(direction.getOpposite())), 2
					);
					generate(world, blockPos2, random, rootPos, size, layer + 1);
				}
			}
		}

		if (!bl) {
			world.setBlockState(pos.up(i), Blocks.CHORUS_FLOWER.getDefaultState().withProperty(AGE, Integer.valueOf(5)), 2);
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
