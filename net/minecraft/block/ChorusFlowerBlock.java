package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ChorusFlowerBlock extends Block {
	public static final IntProperty field_12625 = IntProperty.of("age", 0, 5);

	protected ChorusFlowerBlock() {
		super(Material.PLANT);
		this.setDefaultState(this.stateManager.getDefaultState().with(field_12625, 0));
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setTickRandomly(true);
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return null;
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!this.method_11588(world, pos)) {
			world.removeBlock(pos, true);
		} else {
			BlockPos blockPos = pos.up();
			if (world.isAir(blockPos) && blockPos.getY() < 256) {
				int i = (Integer)state.get(field_12625);
				if (i < 5 && rand.nextInt(1) == 0) {
					boolean bl = false;
					boolean bl2 = false;
					BlockState blockState = world.getBlockState(pos.down());
					Block block = blockState.getBlock();
					if (block == Blocks.END_STONE) {
						bl = true;
					} else if (block == Blocks.CHORUS_PLANT) {
						int j = 1;

						for (int k = 0; k < 4; k++) {
							Block block2 = world.getBlockState(pos.down(j + 1)).getBlock();
							if (block2 != Blocks.CHORUS_PLANT) {
								if (block2 == Blocks.END_STONE) {
									bl2 = true;
								}
								break;
							}

							j++;
						}

						int l = 4;
						if (bl2) {
							l++;
						}

						if (j < 2 || rand.nextInt(l) >= j) {
							bl = true;
						}
					} else if (blockState.getMaterial() == Material.AIR) {
						bl = true;
					}

					if (bl && method_11585(world, blockPos, null) && world.isAir(pos.up(2))) {
						world.setBlockState(pos, Blocks.CHORUS_PLANT.getDefaultState(), 2);
						this.grow(world, blockPos, i);
					} else if (i < 4) {
						int m = rand.nextInt(4);
						boolean bl3 = false;
						if (bl2) {
							m++;
						}

						for (int n = 0; n < m; n++) {
							Direction direction = Direction.DirectionType.HORIZONTAL.getRandomDirection(rand);
							BlockPos blockPos2 = pos.offset(direction);
							if (world.isAir(blockPos2) && world.isAir(blockPos2.down()) && method_11585(world, blockPos2, direction.getOpposite())) {
								this.grow(world, blockPos2, i + 1);
								bl3 = true;
							}
						}

						if (bl3) {
							world.setBlockState(pos, Blocks.CHORUS_PLANT.getDefaultState(), 2);
						} else {
							this.die(world, pos);
						}
					} else if (i == 4) {
						this.die(world, pos);
					}
				}
			}
		}
	}

	private void grow(World world, BlockPos pos, int age) {
		world.setBlockState(pos, this.getDefaultState().with(field_12625, age), 2);
		world.syncGlobalEvent(1033, pos, 0);
	}

	private void die(World world, BlockPos pos) {
		world.setBlockState(pos, this.getDefaultState().with(field_12625, 5), 2);
		world.syncGlobalEvent(1034, pos, 0);
	}

	private static boolean method_11585(World world, BlockPos blockPos, Direction direction) {
		for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
			if (direction2 != direction && !world.isAir(blockPos.offset(direction2))) {
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
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) && this.method_11588(world, pos);
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (!this.method_11588(world, blockPos)) {
			world.createAndScheduleBlockTick(blockPos, this, 1);
		}
	}

	public boolean method_11588(World world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos.down());
		Block block = blockState.getBlock();
		if (block != Blocks.CHORUS_PLANT && block != Blocks.END_STONE) {
			if (blockState.getMaterial() == Material.AIR) {
				int i = 0;

				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					BlockState blockState2 = world.getBlockState(blockPos.offset(direction));
					Block block2 = blockState2.getBlock();
					if (block2 == Blocks.CHORUS_PLANT) {
						i++;
					} else if (blockState2.getMaterial() != Material.AIR) {
						return false;
					}
				}

				return i == 1;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable ItemStack stack) {
		super.method_8651(world, player, pos, state, blockEntity, stack);
		onBlockBreak(world, pos, new ItemStack(Item.fromBlock(this)));
	}

	@Override
	protected ItemStack createStackFromBlock(BlockState state) {
		return null;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(field_12625, data);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(field_12625);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, field_12625);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		super.onCreation(world, pos, state);
	}

	public static void method_11586(World world, BlockPos blockPos, Random random, int i) {
		world.setBlockState(blockPos, Blocks.CHORUS_PLANT.getDefaultState(), 2);
		method_11587(world, blockPos, random, blockPos, i, 0);
	}

	private static void method_11587(World world, BlockPos blockPos, Random random, BlockPos blockPos2, int i, int j) {
		int k = random.nextInt(4) + 1;
		if (j == 0) {
			k++;
		}

		for (int l = 0; l < k; l++) {
			BlockPos blockPos3 = blockPos.up(l + 1);
			if (!method_11585(world, blockPos3, null)) {
				return;
			}

			world.setBlockState(blockPos3, Blocks.CHORUS_PLANT.getDefaultState(), 2);
		}

		boolean bl = false;
		if (j < 4) {
			int m = random.nextInt(4);
			if (j == 0) {
				m++;
			}

			for (int n = 0; n < m; n++) {
				Direction direction = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
				BlockPos blockPos4 = blockPos.up(k).offset(direction);
				if (Math.abs(blockPos4.getX() - blockPos2.getX()) < i
					&& Math.abs(blockPos4.getZ() - blockPos2.getZ()) < i
					&& world.isAir(blockPos4)
					&& world.isAir(blockPos4.down())
					&& method_11585(world, blockPos4, direction.getOpposite())) {
					bl = true;
					world.setBlockState(blockPos4, Blocks.CHORUS_PLANT.getDefaultState(), 2);
					method_11587(world, blockPos4, random, blockPos2, i, j + 1);
				}
			}
		}

		if (!bl) {
			world.setBlockState(blockPos.up(k), Blocks.CHORUS_FLOWER.getDefaultState().with(field_12625, 5), 2);
		}
	}
}
