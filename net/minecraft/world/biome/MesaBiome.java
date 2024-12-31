package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.WoolBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.PerlinNoiseGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.gen.feature.FoliageFeature;

public class MesaBiome extends Biome {
	private BlockState[] layerBlocks;
	private long seed;
	private PerlinNoiseGenerator heightCutoffNoise;
	private PerlinNoiseGenerator heightNoise;
	private PerlinNoiseGenerator layerNoise;
	private boolean field_7249;
	private boolean field_7250;

	public MesaBiome(int i, boolean bl, boolean bl2) {
		super(i);
		this.field_7249 = bl;
		this.field_7250 = bl2;
		this.method_3839();
		this.setTemperatureAndDownfall(2.0F, 0.0F);
		this.passiveEntries.clear();
		this.topBlock = Blocks.SAND.getDefaultState().with(SandBlock.sandType, SandBlock.SandType.RED_SAND);
		this.baseBlock = Blocks.STAINED_TERRACOTTA.getDefaultState();
		this.biomeDecorator.treesPerChunk = -999;
		this.biomeDecorator.deadBushesPerChunk = 20;
		this.biomeDecorator.sugarcanePerChunk = 3;
		this.biomeDecorator.cactusPerChunk = 5;
		this.biomeDecorator.flowersPerChunk = 0;
		this.passiveEntries.clear();
		if (bl2) {
			this.biomeDecorator.treesPerChunk = 5;
		}
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		return this.JUNGLE_TREE_FEATURE;
	}

	@Override
	public int getFoliageColor(BlockPos pos) {
		return 10387789;
	}

	@Override
	public int getGrassColor(BlockPos pos) {
		return 9470285;
	}

	@Override
	public void decorate(World world, Random random, BlockPos pos) {
		super.decorate(world, random, pos);
	}

	@Override
	public void method_6420(World world, Random random, ChunkBlockStateStorage chunkStorage, int i, int j, double d) {
		if (this.layerBlocks == null || this.seed != world.getSeed()) {
			this.initLayerBlocks(world.getSeed());
		}

		if (this.heightCutoffNoise == null || this.heightNoise == null || this.seed != world.getSeed()) {
			Random random2 = new Random(this.seed);
			this.heightCutoffNoise = new PerlinNoiseGenerator(random2, 4);
			this.heightNoise = new PerlinNoiseGenerator(random2, 1);
		}

		this.seed = world.getSeed();
		double e = 0.0;
		if (this.field_7249) {
			int k = (i & -16) + (j & 15);
			int l = (j & -16) + (i & 15);
			double f = Math.min(Math.abs(d), this.heightCutoffNoise.noise((double)k * 0.25, (double)l * 0.25));
			if (f > 0.0) {
				double g = 0.001953125;
				double h = Math.abs(this.heightNoise.noise((double)k * g, (double)l * g));
				e = f * f * 2.5;
				double m = Math.ceil(h * 50.0) + 14.0;
				if (e > m) {
					e = m;
				}

				e += 64.0;
			}
		}

		int n = i & 15;
		int o = j & 15;
		int p = world.getSeaLevel();
		BlockState blockState = Blocks.STAINED_TERRACOTTA.getDefaultState();
		BlockState blockState2 = this.baseBlock;
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int r = -1;
		boolean bl2 = false;

		for (int s = 255; s >= 0; s--) {
			if (chunkStorage.get(o, s, n).getBlock().getMaterial() == Material.AIR && s < (int)e) {
				chunkStorage.set(o, s, n, Blocks.STONE.getDefaultState());
			}

			if (s <= random.nextInt(5)) {
				chunkStorage.set(o, s, n, Blocks.BEDROCK.getDefaultState());
			} else {
				BlockState blockState3 = chunkStorage.get(o, s, n);
				if (blockState3.getBlock().getMaterial() == Material.AIR) {
					r = -1;
				} else if (blockState3.getBlock() == Blocks.STONE) {
					if (r == -1) {
						bl2 = false;
						if (q <= 0) {
							blockState = null;
							blockState2 = Blocks.STONE.getDefaultState();
						} else if (s >= p - 4 && s <= p + 1) {
							blockState = Blocks.STAINED_TERRACOTTA.getDefaultState();
							blockState2 = this.baseBlock;
						}

						if (s < p && (blockState == null || blockState.getBlock().getMaterial() == Material.AIR)) {
							blockState = Blocks.WATER.getDefaultState();
						}

						r = q + Math.max(0, s - p);
						if (s >= p - 1) {
							if (!this.field_7250 || s <= 86 + q * 2) {
								if (s > p + 3 + q) {
									BlockState blockState4;
									if (s < 64 || s > 127) {
										blockState4 = Blocks.STAINED_TERRACOTTA.getDefaultState().with(WoolBlock.COLOR, DyeColor.ORANGE);
									} else if (bl) {
										blockState4 = Blocks.TERRACOTTA.getDefaultState();
									} else {
										blockState4 = this.calculateLayerBlockState(i, s, j);
									}

									chunkStorage.set(o, s, n, blockState4);
								} else {
									chunkStorage.set(o, s, n, this.topBlock);
									bl2 = true;
								}
							} else if (bl) {
								chunkStorage.set(o, s, n, Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.COARSE_DIRT));
							} else {
								chunkStorage.set(o, s, n, Blocks.GRASS.getDefaultState());
							}
						} else {
							chunkStorage.set(o, s, n, blockState2);
							if (blockState2.getBlock() == Blocks.STAINED_TERRACOTTA) {
								chunkStorage.set(o, s, n, blockState2.getBlock().getDefaultState().with(WoolBlock.COLOR, DyeColor.ORANGE));
							}
						}
					} else if (r > 0) {
						r--;
						if (bl2) {
							chunkStorage.set(o, s, n, Blocks.STAINED_TERRACOTTA.getDefaultState().with(WoolBlock.COLOR, DyeColor.ORANGE));
						} else {
							BlockState blockState7 = this.calculateLayerBlockState(i, s, j);
							chunkStorage.set(o, s, n, blockState7);
						}
					}
				}
			}
		}
	}

	private void initLayerBlocks(long seed) {
		this.layerBlocks = new BlockState[64];
		Arrays.fill(this.layerBlocks, Blocks.TERRACOTTA.getDefaultState());
		Random random = new Random(seed);
		this.layerNoise = new PerlinNoiseGenerator(random, 1);

		for (int i = 0; i < 64; i++) {
			i += random.nextInt(5) + 1;
			if (i < 64) {
				this.layerBlocks[i] = Blocks.STAINED_TERRACOTTA.getDefaultState().with(WoolBlock.COLOR, DyeColor.ORANGE);
			}
		}

		int j = random.nextInt(4) + 2;

		for (int k = 0; k < j; k++) {
			int l = random.nextInt(3) + 1;
			int m = random.nextInt(64);

			for (int n = 0; m + n < 64 && n < l; n++) {
				this.layerBlocks[m + n] = Blocks.STAINED_TERRACOTTA.getDefaultState().with(WoolBlock.COLOR, DyeColor.YELLOW);
			}
		}

		int o = random.nextInt(4) + 2;

		for (int p = 0; p < o; p++) {
			int q = random.nextInt(3) + 2;
			int r = random.nextInt(64);

			for (int s = 0; r + s < 64 && s < q; s++) {
				this.layerBlocks[r + s] = Blocks.STAINED_TERRACOTTA.getDefaultState().with(WoolBlock.COLOR, DyeColor.BROWN);
			}
		}

		int t = random.nextInt(4) + 2;

		for (int u = 0; u < t; u++) {
			int v = random.nextInt(3) + 1;
			int w = random.nextInt(64);

			for (int x = 0; w + x < 64 && x < v; x++) {
				this.layerBlocks[w + x] = Blocks.STAINED_TERRACOTTA.getDefaultState().with(WoolBlock.COLOR, DyeColor.RED);
			}
		}

		int y = random.nextInt(3) + 3;
		int z = 0;

		for (int aa = 0; aa < y; aa++) {
			int ab = 1;
			z += random.nextInt(16) + 4;

			for (int ac = 0; z + ac < 64 && ac < ab; ac++) {
				this.layerBlocks[z + ac] = Blocks.STAINED_TERRACOTTA.getDefaultState().with(WoolBlock.COLOR, DyeColor.WHITE);
				if (z + ac > 1 && random.nextBoolean()) {
					this.layerBlocks[z + ac - 1] = Blocks.STAINED_TERRACOTTA.getDefaultState().with(WoolBlock.COLOR, DyeColor.SILVER);
				}

				if (z + ac < 63 && random.nextBoolean()) {
					this.layerBlocks[z + ac + 1] = Blocks.STAINED_TERRACOTTA.getDefaultState().with(WoolBlock.COLOR, DyeColor.SILVER);
				}
			}
		}
	}

	private BlockState calculateLayerBlockState(int x, int y, int z) {
		int i = (int)Math.round(this.layerNoise.noise((double)x * 1.0 / 512.0, (double)x * 1.0 / 512.0) * 2.0);
		return this.layerBlocks[(y + i + 64) % 64];
	}

	@Override
	protected Biome getMutatedVariant(int id) {
		boolean bl = this.id == Biome.MESA.id;
		MesaBiome mesaBiome = new MesaBiome(id, bl, this.field_7250);
		if (!bl) {
			mesaBiome.setHeight(HILLS_HEIGHT);
			mesaBiome.setName(this.name + " M");
		} else {
			mesaBiome.setName(this.name + " (Bryce)");
		}

		mesaBiome.seedModifier(this.field_4661, true);
		return mesaBiome;
	}
}
