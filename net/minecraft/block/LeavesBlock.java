package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class LeavesBlock extends BaseLeavesBlock {
	public static final BooleanProperty DECAYABLE = BooleanProperty.of("decayable");
	public static final BooleanProperty CHECK_DECAY = BooleanProperty.of("check_decay");
	int[] neighborBlockDecayInfo;
	protected int fancyIndex;
	protected boolean fancyGraphicsStatus;

	public LeavesBlock() {
		super(Material.FOLIAGE, false);
		this.setTickRandomly(true);
		this.setItemGroup(ItemGroup.DECORATIONS);
		this.setStrength(0.2F);
		this.setOpacity(1);
		this.setSound(GRASS);
	}

	@Override
	public int getColor() {
		return FoliageColors.getColor(0.5, 1.0);
	}

	@Override
	public int getColor(BlockState state) {
		return FoliageColors.getDefaultColor();
	}

	@Override
	public int getBlockColor(BlockView view, BlockPos pos, int id) {
		return BiomeColors.getFoliageColor(view, pos);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		int i = 1;
		int j = i + 1;
		int k = pos.getX();
		int l = pos.getY();
		int m = pos.getZ();
		if (world.isRegionLoaded(new BlockPos(k - j, l - j, m - j), new BlockPos(k + j, l + j, m + j))) {
			for (int n = -i; n <= i; n++) {
				for (int o = -i; o <= i; o++) {
					for (int p = -i; p <= i; p++) {
						BlockPos blockPos = pos.add(n, o, p);
						BlockState blockState = world.getBlockState(blockPos);
						if (blockState.getBlock().getMaterial() == Material.FOLIAGE && !(Boolean)blockState.get(CHECK_DECAY)) {
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
				int j = i + 1;
				int k = pos.getX();
				int l = pos.getY();
				int m = pos.getZ();
				int n = 32;
				int o = n * n;
				int p = n / 2;
				if (this.neighborBlockDecayInfo == null) {
					this.neighborBlockDecayInfo = new int[n * n * n];
				}

				if (world.isRegionLoaded(new BlockPos(k - j, l - j, m - j), new BlockPos(k + j, l + j, m + j))) {
					BlockPos.Mutable mutable = new BlockPos.Mutable();

					for (int q = -i; q <= i; q++) {
						for (int r = -i; r <= i; r++) {
							for (int s = -i; s <= i; s++) {
								Block block = world.getBlockState(mutable.setPosition(k + q, l + r, m + s)).getBlock();
								if (block != Blocks.LOG && block != Blocks.LOG2) {
									if (block.getMaterial() == Material.FOLIAGE) {
										this.neighborBlockDecayInfo[(q + p) * o + (r + p) * n + s + p] = -2;
									} else {
										this.neighborBlockDecayInfo[(q + p) * o + (r + p) * n + s + p] = -1;
									}
								} else {
									this.neighborBlockDecayInfo[(q + p) * o + (r + p) * n + s + p] = 0;
								}
							}
						}
					}

					for (int t = 1; t <= 4; t++) {
						for (int u = -i; u <= i; u++) {
							for (int v = -i; v <= i; v++) {
								for (int w = -i; w <= i; w++) {
									if (this.neighborBlockDecayInfo[(u + p) * o + (v + p) * n + w + p] == t - 1) {
										if (this.neighborBlockDecayInfo[(u + p - 1) * o + (v + p) * n + w + p] == -2) {
											this.neighborBlockDecayInfo[(u + p - 1) * o + (v + p) * n + w + p] = t;
										}

										if (this.neighborBlockDecayInfo[(u + p + 1) * o + (v + p) * n + w + p] == -2) {
											this.neighborBlockDecayInfo[(u + p + 1) * o + (v + p) * n + w + p] = t;
										}

										if (this.neighborBlockDecayInfo[(u + p) * o + (v + p - 1) * n + w + p] == -2) {
											this.neighborBlockDecayInfo[(u + p) * o + (v + p - 1) * n + w + p] = t;
										}

										if (this.neighborBlockDecayInfo[(u + p) * o + (v + p + 1) * n + w + p] == -2) {
											this.neighborBlockDecayInfo[(u + p) * o + (v + p + 1) * n + w + p] = t;
										}

										if (this.neighborBlockDecayInfo[(u + p) * o + (v + p) * n + (w + p - 1)] == -2) {
											this.neighborBlockDecayInfo[(u + p) * o + (v + p) * n + (w + p - 1)] = t;
										}

										if (this.neighborBlockDecayInfo[(u + p) * o + (v + p) * n + w + p + 1] == -2) {
											this.neighborBlockDecayInfo[(u + p) * o + (v + p) * n + w + p + 1] = t;
										}
									}
								}
							}
						}
					}
				}

				int x = this.neighborBlockDecayInfo[p * o + p * n + p];
				if (x >= 0) {
					world.setBlockState(pos, state.with(CHECK_DECAY, false), 4);
				} else {
					this.breakBlock(world, pos);
				}
			}
		}
	}

	@Override
	public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (world.hasRain(pos.up()) && !World.isOpaque(world, pos.down()) && rand.nextInt(15) == 1) {
			double d = (double)((float)pos.getX() + rand.nextFloat());
			double e = (double)pos.getY() - 0.05;
			double f = (double)((float)pos.getZ() + rand.nextFloat());
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
	public boolean hasTransparency() {
		return !this.fancyGraphics;
	}

	public void setGraphics(boolean fancyGraphics) {
		this.fancyGraphicsStatus = fancyGraphics;
		this.fancyGraphics = fancyGraphics;
		this.fancyIndex = fancyGraphics ? 0 : 1;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return this.fancyGraphicsStatus ? RenderLayer.CUTOUT_MIPPED : RenderLayer.SOLID;
	}

	@Override
	public boolean isLeafBlock() {
		return false;
	}

	public abstract PlanksBlock.WoodType getWoodType(int state);
}
