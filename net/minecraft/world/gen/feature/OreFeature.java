package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkSectionCache;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class OreFeature extends Feature<OreFeatureConfig> {
	public OreFeature(Codec<OreFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<OreFeatureConfig> context) {
		Random random = context.getRandom();
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		OreFeatureConfig oreFeatureConfig = context.getConfig();
		float f = random.nextFloat() * (float) Math.PI;
		float g = (float)oreFeatureConfig.size / 8.0F;
		int i = MathHelper.ceil(((float)oreFeatureConfig.size / 16.0F * 2.0F + 1.0F) / 2.0F);
		double d = (double)blockPos.getX() + Math.sin((double)f) * (double)g;
		double e = (double)blockPos.getX() - Math.sin((double)f) * (double)g;
		double h = (double)blockPos.getZ() + Math.cos((double)f) * (double)g;
		double j = (double)blockPos.getZ() - Math.cos((double)f) * (double)g;
		int k = 2;
		double l = (double)(blockPos.getY() + random.nextInt(3) - 2);
		double m = (double)(blockPos.getY() + random.nextInt(3) - 2);
		int n = blockPos.getX() - MathHelper.ceil(g) - i;
		int o = blockPos.getY() - 2 - i;
		int p = blockPos.getZ() - MathHelper.ceil(g) - i;
		int q = 2 * (MathHelper.ceil(g) + i);
		int r = 2 * (2 + i);

		for (int s = n; s <= n + q; s++) {
			for (int t = p; t <= p + q; t++) {
				if (o <= structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) {
					return this.generateVeinPart(structureWorldAccess, random, oreFeatureConfig, d, e, h, j, l, m, n, o, p, q, r);
				}
			}
		}

		return false;
	}

	protected boolean generateVeinPart(
		StructureWorldAccess structureWorldAccess,
		Random random,
		OreFeatureConfig config,
		double startX,
		double endX,
		double startZ,
		double endZ,
		double startY,
		double endY,
		int x,
		int y,
		int z,
		int horizontalSize,
		int verticalSize
	) {
		int i = 0;
		BitSet bitSet = new BitSet(horizontalSize * verticalSize * horizontalSize);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int j = config.size;
		double[] ds = new double[j * 4];

		for (int k = 0; k < j; k++) {
			float f = (float)k / (float)j;
			double d = MathHelper.lerp((double)f, startX, endX);
			double e = MathHelper.lerp((double)f, startY, endY);
			double g = MathHelper.lerp((double)f, startZ, endZ);
			double h = random.nextDouble() * (double)j / 16.0;
			double l = ((double)(MathHelper.sin((float) Math.PI * f) + 1.0F) * h + 1.0) / 2.0;
			ds[k * 4 + 0] = d;
			ds[k * 4 + 1] = e;
			ds[k * 4 + 2] = g;
			ds[k * 4 + 3] = l;
		}

		for (int m = 0; m < j - 1; m++) {
			if (!(ds[m * 4 + 3] <= 0.0)) {
				for (int n = m + 1; n < j; n++) {
					if (!(ds[n * 4 + 3] <= 0.0)) {
						double o = ds[m * 4 + 0] - ds[n * 4 + 0];
						double p = ds[m * 4 + 1] - ds[n * 4 + 1];
						double q = ds[m * 4 + 2] - ds[n * 4 + 2];
						double r = ds[m * 4 + 3] - ds[n * 4 + 3];
						if (r * r > o * o + p * p + q * q) {
							if (r > 0.0) {
								ds[n * 4 + 3] = -1.0;
							} else {
								ds[m * 4 + 3] = -1.0;
							}
						}
					}
				}
			}
		}

		try (ChunkSectionCache chunkSectionCache = new ChunkSectionCache(structureWorldAccess)) {
			for (int s = 0; s < j; s++) {
				double t = ds[s * 4 + 3];
				if (!(t < 0.0)) {
					double u = ds[s * 4 + 0];
					double v = ds[s * 4 + 1];
					double w = ds[s * 4 + 2];
					int aa = Math.max(MathHelper.floor(u - t), x);
					int ab = Math.max(MathHelper.floor(v - t), y);
					int ac = Math.max(MathHelper.floor(w - t), z);
					int ad = Math.max(MathHelper.floor(u + t), aa);
					int ae = Math.max(MathHelper.floor(v + t), ab);
					int af = Math.max(MathHelper.floor(w + t), ac);

					for (int ag = aa; ag <= ad; ag++) {
						double ah = ((double)ag + 0.5 - u) / t;
						if (ah * ah < 1.0) {
							for (int ai = ab; ai <= ae; ai++) {
								double aj = ((double)ai + 0.5 - v) / t;
								if (ah * ah + aj * aj < 1.0) {
									for (int ak = ac; ak <= af; ak++) {
										double al = ((double)ak + 0.5 - w) / t;
										if (ah * ah + aj * aj + al * al < 1.0 && !structureWorldAccess.isOutOfHeightLimit(ai)) {
											int am = ag - x + (ai - y) * horizontalSize + (ak - z) * horizontalSize * verticalSize;
											if (!bitSet.get(am)) {
												bitSet.set(am);
												mutable.set(ag, ai, ak);
												if (structureWorldAccess.method_37368(mutable)) {
													ChunkSection chunkSection = chunkSectionCache.getSection(mutable);
													if (chunkSection != WorldChunk.EMPTY_SECTION) {
														int an = ChunkSectionPos.getLocalCoord(ag);
														int ao = ChunkSectionPos.getLocalCoord(ai);
														int ap = ChunkSectionPos.getLocalCoord(ak);
														BlockState blockState = chunkSection.getBlockState(an, ao, ap);

														for (OreFeatureConfig.Target target : config.targets) {
															if (shouldPlace(blockState, chunkSectionCache::getBlockState, random, config, target, mutable)) {
																chunkSection.setBlockState(an, ao, ap, target.state, false);
																i++;
																break;
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return i > 0;
	}

	public static boolean shouldPlace(
		BlockState state, Function<BlockPos, BlockState> posToState, Random random, OreFeatureConfig config, OreFeatureConfig.Target target, BlockPos.Mutable pos
	) {
		if (!target.target.test(state, random)) {
			return false;
		} else {
			return shouldNotDiscard(random, config.discardOnAirChance) ? true : !isExposedToAir(posToState, pos);
		}
	}

	protected static boolean shouldNotDiscard(Random random, float chance) {
		if (chance <= 0.0F) {
			return true;
		} else {
			return chance >= 1.0F ? false : random.nextFloat() >= chance;
		}
	}
}
