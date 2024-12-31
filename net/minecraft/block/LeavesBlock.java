package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class LeavesBlock extends Block {
	public static final BooleanProperty DECAYABLE = BooleanProperty.of("decayable");
	public static final BooleanProperty CHECK_DECAY = BooleanProperty.of("check_decay");
	protected boolean fancyGraphicsStatus;
	int[] neighborBlockDecayInfo;

	public LeavesBlock() {
		super(Material.FOLIAGE);
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setStrength(0.2F);
		this.setOpacity(1);
		this.setBlockSoundGroup(BlockSoundGroup.field_12761);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		int i = 1;
		int j = 2;
		int k = pos.getX();
		int l = pos.getY();
		int m = pos.getZ();
		if (world.isRegionLoaded(new BlockPos(k - 2, l - 2, m - 2), new BlockPos(k + 2, l + 2, m + 2))) {
			for (int n = -1; n <= 1; n++) {
				for (int o = -1; o <= 1; o++) {
					for (int p = -1; p <= 1; p++) {
						BlockPos blockPos = pos.add(n, o, p);
						BlockState blockState = world.getBlockState(blockPos);
						if (blockState.getMaterial() == Material.FOLIAGE && !(Boolean)blockState.get(CHECK_DECAY)) {
							world.setBlockState(blockPos, blockState.with(CHECK_DECAY, true), 4);
						}
					}
				}
			}
		}
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient) {
			if ((Boolean)state.get(CHECK_DECAY) && (Boolean)state.get(DECAYABLE)) {
				int i = 4;
				int j = 5;
				int k = pos.getX();
				int l = pos.getY();
				int m = pos.getZ();
				int n = 32;
				int o = 1024;
				int p = 16;
				if (this.neighborBlockDecayInfo == null) {
					this.neighborBlockDecayInfo = new int[32768];
				}

				if (world.isRegionLoaded(new BlockPos(k - 5, l - 5, m - 5), new BlockPos(k + 5, l + 5, m + 5))) {
					BlockPos.Mutable mutable = new BlockPos.Mutable();

					for (int q = -4; q <= 4; q++) {
						for (int r = -4; r <= 4; r++) {
							for (int s = -4; s <= 4; s++) {
								BlockState blockState = world.getBlockState(mutable.setPosition(k + q, l + r, m + s));
								Block block = blockState.getBlock();
								if (block != Blocks.LOG && block != Blocks.LOG2) {
									if (blockState.getMaterial() == Material.FOLIAGE) {
										this.neighborBlockDecayInfo[(q + 16) * 1024 + (r + 16) * 32 + s + 16] = -2;
									} else {
										this.neighborBlockDecayInfo[(q + 16) * 1024 + (r + 16) * 32 + s + 16] = -1;
									}
								} else {
									this.neighborBlockDecayInfo[(q + 16) * 1024 + (r + 16) * 32 + s + 16] = 0;
								}
							}
						}
					}

					for (int t = 1; t <= 4; t++) {
						for (int u = -4; u <= 4; u++) {
							for (int v = -4; v <= 4; v++) {
								for (int w = -4; w <= 4; w++) {
									if (this.neighborBlockDecayInfo[(u + 16) * 1024 + (v + 16) * 32 + w + 16] == t - 1) {
										if (this.neighborBlockDecayInfo[(u + 16 - 1) * 1024 + (v + 16) * 32 + w + 16] == -2) {
											this.neighborBlockDecayInfo[(u + 16 - 1) * 1024 + (v + 16) * 32 + w + 16] = t;
										}

										if (this.neighborBlockDecayInfo[(u + 16 + 1) * 1024 + (v + 16) * 32 + w + 16] == -2) {
											this.neighborBlockDecayInfo[(u + 16 + 1) * 1024 + (v + 16) * 32 + w + 16] = t;
										}

										if (this.neighborBlockDecayInfo[(u + 16) * 1024 + (v + 16 - 1) * 32 + w + 16] == -2) {
											this.neighborBlockDecayInfo[(u + 16) * 1024 + (v + 16 - 1) * 32 + w + 16] = t;
										}

										if (this.neighborBlockDecayInfo[(u + 16) * 1024 + (v + 16 + 1) * 32 + w + 16] == -2) {
											this.neighborBlockDecayInfo[(u + 16) * 1024 + (v + 16 + 1) * 32 + w + 16] = t;
										}

										if (this.neighborBlockDecayInfo[(u + 16) * 1024 + (v + 16) * 32 + (w + 16 - 1)] == -2) {
											this.neighborBlockDecayInfo[(u + 16) * 1024 + (v + 16) * 32 + (w + 16 - 1)] = t;
										}

										if (this.neighborBlockDecayInfo[(u + 16) * 1024 + (v + 16) * 32 + w + 16 + 1] == -2) {
											this.neighborBlockDecayInfo[(u + 16) * 1024 + (v + 16) * 32 + w + 16 + 1] = t;
										}
									}
								}
							}
						}
					}
				}

				int x = this.neighborBlockDecayInfo[16912];
				if (x >= 0) {
					world.setBlockState(pos, state.with(CHECK_DECAY, false), 4);
				} else {
					this.breakBlock(world, pos);
				}
			}
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.hasRain(pos.up()) && !world.getBlockState(pos.down()).method_11739() && random.nextInt(15) == 1) {
			double d = (double)((float)pos.getX() + random.nextFloat());
			double e = (double)pos.getY() - 0.05;
			double f = (double)((float)pos.getZ() + random.nextFloat());
			world.addParticle(ParticleType.WATER_DRIP, d, e, f, 0.0, 0.0, 0.0);
		}
	}

	private void breakBlock(World world, BlockPos pos) {
		this.dropAsItem(world, pos, world.getBlockState(pos), 0);
		world.setAir(pos);
	}

	@Override
	public int getDropCount(Random rand) {
		return rand.nextInt(20) == 0 ? 1 : 0;
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.SAPLING);
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		if (!world.isClient) {
			int i = this.getSaplingDropChance(state);
			if (id > 0) {
				i -= 2 << id;
				if (i < 10) {
					i = 10;
				}
			}

			if (world.random.nextInt(i) == 0) {
				Item item = this.getDropItem(state, world.random, id);
				onBlockBreak(world, pos, new ItemStack(item, 1, this.getMeta(state)));
			}

			i = 200;
			if (id > 0) {
				i -= 10 << id;
				if (i < 40) {
					i = 40;
				}
			}

			this.dropApple(world, pos, state, i);
		}
	}

	protected void dropApple(World world, BlockPos pos, BlockState state, int dropChance) {
	}

	protected int getSaplingDropChance(BlockState state) {
		return 20;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return !this.fancyGraphicsStatus;
	}

	public void setGraphics(boolean fancyGraphics) {
		this.fancyGraphicsStatus = fancyGraphics;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return this.fancyGraphicsStatus ? RenderLayer.CUTOUT_MIPPED : RenderLayer.SOLID;
	}

	@Override
	public boolean method_13703(BlockState state) {
		return false;
	}

	public abstract PlanksBlock.WoodType getWoodType(int state);

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		return !this.fancyGraphicsStatus && view.getBlockState(pos.offset(direction)).getBlock() == this ? false : super.method_8654(state, view, pos, direction);
	}
}
