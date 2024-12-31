package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ChorusFlowerBlock extends Block {
	public static final IntProperty AGE = Properties.AGE_5;
	private final ChorusPlantBlock plantBlock;

	protected ChorusFlowerBlock(ChorusPlantBlock chorusPlantBlock, Block.Settings settings) {
		super(settings);
		this.plantBlock = chorusPlantBlock;
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(serverWorld, blockPos)) {
			serverWorld.breakBlock(blockPos, true);
		} else {
			BlockPos blockPos2 = blockPos.up();
			if (serverWorld.isAir(blockPos2) && blockPos2.getY() < 256) {
				int i = (Integer)blockState.get(AGE);
				if (i < 5) {
					boolean bl = false;
					boolean bl2 = false;
					BlockState blockState2 = serverWorld.getBlockState(blockPos.down());
					Block block = blockState2.getBlock();
					if (block == Blocks.field_10471) {
						bl = true;
					} else if (block == this.plantBlock) {
						int j = 1;

						for (int k = 0; k < 4; k++) {
							Block block2 = serverWorld.getBlockState(blockPos.down(j + 1)).getBlock();
							if (block2 != this.plantBlock) {
								if (block2 == Blocks.field_10471) {
									bl2 = true;
								}
								break;
							}

							j++;
						}

						if (j < 2 || j <= random.nextInt(bl2 ? 5 : 4)) {
							bl = true;
						}
					} else if (blockState2.isAir()) {
						bl = true;
					}

					if (bl && isSurroundedByAir(serverWorld, blockPos2, null) && serverWorld.isAir(blockPos.up(2))) {
						serverWorld.setBlockState(blockPos, this.plantBlock.withConnectionProperties(serverWorld, blockPos), 2);
						this.grow(serverWorld, blockPos2, i);
					} else if (i < 4) {
						int l = random.nextInt(4);
						if (bl2) {
							l++;
						}

						boolean bl3 = false;

						for (int m = 0; m < l; m++) {
							Direction direction = Direction.Type.field_11062.random(random);
							BlockPos blockPos3 = blockPos.offset(direction);
							if (serverWorld.isAir(blockPos3) && serverWorld.isAir(blockPos3.down()) && isSurroundedByAir(serverWorld, blockPos3, direction.getOpposite())) {
								this.grow(serverWorld, blockPos3, i + 1);
								bl3 = true;
							}
						}

						if (bl3) {
							serverWorld.setBlockState(blockPos, this.plantBlock.withConnectionProperties(serverWorld, blockPos), 2);
						} else {
							this.die(serverWorld, blockPos);
						}
					} else {
						this.die(serverWorld, blockPos);
					}
				}
			}
		}
	}

	private void grow(World world, BlockPos blockPos, int i) {
		world.setBlockState(blockPos, this.getDefaultState().with(AGE, Integer.valueOf(i)), 2);
		world.playLevelEvent(1033, blockPos, 0);
	}

	private void die(World world, BlockPos blockPos) {
		world.setBlockState(blockPos, this.getDefaultState().with(AGE, Integer.valueOf(5)), 2);
		world.playLevelEvent(1034, blockPos, 0);
	}

	private static boolean isSurroundedByAir(WorldView worldView, BlockPos blockPos, @Nullable Direction direction) {
		for (Direction direction2 : Direction.Type.field_11062) {
			if (direction2 != direction && !worldView.isAir(blockPos.offset(direction2))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction != Direction.field_11036 && !blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		BlockState blockState2 = worldView.getBlockState(blockPos.down());
		Block block = blockState2.getBlock();
		if (block != this.plantBlock && block != Blocks.field_10471) {
			if (!blockState2.isAir()) {
				return false;
			} else {
				boolean bl = false;

				for (Direction direction : Direction.Type.field_11062) {
					BlockState blockState3 = worldView.getBlockState(blockPos.offset(direction));
					if (blockState3.getBlock() == this.plantBlock) {
						if (bl) {
							return false;
						}

						bl = true;
					} else if (!blockState3.isAir()) {
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
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	public static void generate(IWorld iWorld, BlockPos blockPos, Random random, int i) {
		iWorld.setBlockState(blockPos, ((ChorusPlantBlock)Blocks.field_10021).withConnectionProperties(iWorld, blockPos), 2);
		generate(iWorld, blockPos, random, blockPos, i, 0);
	}

	private static void generate(IWorld iWorld, BlockPos blockPos, Random random, BlockPos blockPos2, int i, int j) {
		ChorusPlantBlock chorusPlantBlock = (ChorusPlantBlock)Blocks.field_10021;
		int k = random.nextInt(4) + 1;
		if (j == 0) {
			k++;
		}

		for (int l = 0; l < k; l++) {
			BlockPos blockPos3 = blockPos.up(l + 1);
			if (!isSurroundedByAir(iWorld, blockPos3, null)) {
				return;
			}

			iWorld.setBlockState(blockPos3, chorusPlantBlock.withConnectionProperties(iWorld, blockPos3), 2);
			iWorld.setBlockState(blockPos3.down(), chorusPlantBlock.withConnectionProperties(iWorld, blockPos3.down()), 2);
		}

		boolean bl = false;
		if (j < 4) {
			int m = random.nextInt(4);
			if (j == 0) {
				m++;
			}

			for (int n = 0; n < m; n++) {
				Direction direction = Direction.Type.field_11062.random(random);
				BlockPos blockPos4 = blockPos.up(k).offset(direction);
				if (Math.abs(blockPos4.getX() - blockPos2.getX()) < i
					&& Math.abs(blockPos4.getZ() - blockPos2.getZ()) < i
					&& iWorld.isAir(blockPos4)
					&& iWorld.isAir(blockPos4.down())
					&& isSurroundedByAir(iWorld, blockPos4, direction.getOpposite())) {
					bl = true;
					iWorld.setBlockState(blockPos4, chorusPlantBlock.withConnectionProperties(iWorld, blockPos4), 2);
					iWorld.setBlockState(
						blockPos4.offset(direction.getOpposite()), chorusPlantBlock.withConnectionProperties(iWorld, blockPos4.offset(direction.getOpposite())), 2
					);
					generate(iWorld, blockPos4, random, blockPos2, i, j + 1);
				}
			}
		}

		if (!bl) {
			iWorld.setBlockState(blockPos.up(k), Blocks.field_10528.getDefaultState().with(AGE, Integer.valueOf(5)), 2);
		}
	}

	@Override
	public void onProjectileHit(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
		BlockPos blockPos = blockHitResult.getBlockPos();
		world.breakBlock(blockPos, true, entity);
	}
}
