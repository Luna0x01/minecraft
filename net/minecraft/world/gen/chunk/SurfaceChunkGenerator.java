package net.minecraft.world.gen.chunk;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class SurfaceChunkGenerator<T extends ChunkGeneratorConfig> extends ChunkGenerator<T> {
	private static final float[] field_16649 = Util.make(new float[13824], fs -> {
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				for (int k = 0; k < 24; k++) {
					fs[i * 24 * 24 + j * 24 + k] = (float)method_16571(j - 12, k - 12, i - 12);
				}
			}
		}
	});
	private static final BlockState AIR = Blocks.field_10124.getDefaultState();
	private final int verticalNoiseResolution;
	private final int horizontalNoiseResolution;
	private final int noiseSizeX;
	private final int noiseSizeY;
	private final int noiseSizeZ;
	protected final ChunkRandom random;
	private final OctavePerlinNoiseSampler field_16574;
	private final OctavePerlinNoiseSampler field_16581;
	private final OctavePerlinNoiseSampler field_16575;
	private final NoiseSampler surfaceDepthNoise;
	protected final BlockState defaultBlock;
	protected final BlockState defaultFluid;

	public SurfaceChunkGenerator(IWorld iWorld, BiomeSource biomeSource, int i, int j, int k, T chunkGeneratorConfig, boolean bl) {
		super(iWorld, biomeSource, chunkGeneratorConfig);
		this.verticalNoiseResolution = j;
		this.horizontalNoiseResolution = i;
		this.defaultBlock = chunkGeneratorConfig.getDefaultBlock();
		this.defaultFluid = chunkGeneratorConfig.getDefaultFluid();
		this.noiseSizeX = 16 / this.horizontalNoiseResolution;
		this.noiseSizeY = k / this.verticalNoiseResolution;
		this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
		this.random = new ChunkRandom(this.seed);
		this.field_16574 = new OctavePerlinNoiseSampler(this.random, 15, 0);
		this.field_16581 = new OctavePerlinNoiseSampler(this.random, 15, 0);
		this.field_16575 = new OctavePerlinNoiseSampler(this.random, 7, 0);
		this.surfaceDepthNoise = (NoiseSampler)(bl ? new OctaveSimplexNoiseSampler(this.random, 3, 0) : new OctavePerlinNoiseSampler(this.random, 3, 0));
	}

	private double sampleNoise(int i, int j, int k, double d, double e, double f, double g) {
		double h = 0.0;
		double l = 0.0;
		double m = 0.0;
		double n = 1.0;

		for (int o = 0; o < 16; o++) {
			double p = OctavePerlinNoiseSampler.maintainPrecision((double)i * d * n);
			double q = OctavePerlinNoiseSampler.maintainPrecision((double)j * e * n);
			double r = OctavePerlinNoiseSampler.maintainPrecision((double)k * d * n);
			double s = e * n;
			PerlinNoiseSampler perlinNoiseSampler = this.field_16574.getOctave(o);
			if (perlinNoiseSampler != null) {
				h += perlinNoiseSampler.sample(p, q, r, s, (double)j * s) / n;
			}

			PerlinNoiseSampler perlinNoiseSampler2 = this.field_16581.getOctave(o);
			if (perlinNoiseSampler2 != null) {
				l += perlinNoiseSampler2.sample(p, q, r, s, (double)j * s) / n;
			}

			if (o < 8) {
				PerlinNoiseSampler perlinNoiseSampler3 = this.field_16575.getOctave(o);
				if (perlinNoiseSampler3 != null) {
					m += perlinNoiseSampler3.sample(
							OctavePerlinNoiseSampler.maintainPrecision((double)i * f * n),
							OctavePerlinNoiseSampler.maintainPrecision((double)j * g * n),
							OctavePerlinNoiseSampler.maintainPrecision((double)k * f * n),
							g * n,
							(double)j * g * n
						)
						/ n;
				}
			}

			n /= 2.0;
		}

		return MathHelper.clampedLerp(h / 512.0, l / 512.0, (m / 10.0 + 1.0) / 2.0);
	}

	protected double[] sampleNoiseColumn(int i, int j) {
		double[] ds = new double[this.noiseSizeY + 1];
		this.sampleNoiseColumn(ds, i, j);
		return ds;
	}

	protected void sampleNoiseColumn(double[] ds, int i, int j, double d, double e, double f, double g, int k, int l) {
		double[] es = this.computeNoiseRange(i, j);
		double h = es[0];
		double m = es[1];
		double n = this.method_16409();
		double o = this.method_16410();

		for (int p = 0; p < this.getNoiseSizeY(); p++) {
			double q = this.sampleNoise(i, p, j, d, e, f, g);
			q -= this.computeNoiseFalloff(h, m, p);
			if ((double)p > n) {
				q = MathHelper.clampedLerp(q, (double)l, ((double)p - n) / (double)k);
			} else if ((double)p < o) {
				q = MathHelper.clampedLerp(q, -30.0, (o - (double)p) / (o - 1.0));
			}

			ds[p] = q;
		}
	}

	protected abstract double[] computeNoiseRange(int i, int j);

	protected abstract double computeNoiseFalloff(double d, double e, int i);

	protected double method_16409() {
		return (double)(this.getNoiseSizeY() - 4);
	}

	protected double method_16410() {
		return 0.0;
	}

	@Override
	public int getHeightOnGround(int i, int j, Heightmap.Type type) {
		int k = Math.floorDiv(i, this.horizontalNoiseResolution);
		int l = Math.floorDiv(j, this.horizontalNoiseResolution);
		int m = Math.floorMod(i, this.horizontalNoiseResolution);
		int n = Math.floorMod(j, this.horizontalNoiseResolution);
		double d = (double)m / (double)this.horizontalNoiseResolution;
		double e = (double)n / (double)this.horizontalNoiseResolution;
		double[][] ds = new double[][]{
			this.sampleNoiseColumn(k, l), this.sampleNoiseColumn(k, l + 1), this.sampleNoiseColumn(k + 1, l), this.sampleNoiseColumn(k + 1, l + 1)
		};
		int o = this.getSeaLevel();

		for (int p = this.noiseSizeY - 1; p >= 0; p--) {
			double f = ds[0][p];
			double g = ds[1][p];
			double h = ds[2][p];
			double q = ds[3][p];
			double r = ds[0][p + 1];
			double s = ds[1][p + 1];
			double t = ds[2][p + 1];
			double u = ds[3][p + 1];

			for (int v = this.verticalNoiseResolution - 1; v >= 0; v--) {
				double w = (double)v / (double)this.verticalNoiseResolution;
				double x = MathHelper.lerp3(w, d, e, f, r, h, t, g, s, q, u);
				int y = p * this.verticalNoiseResolution + v;
				if (x > 0.0 || y < o) {
					BlockState blockState;
					if (x > 0.0) {
						blockState = this.defaultBlock;
					} else {
						blockState = this.defaultFluid;
					}

					if (type.getBlockPredicate().test(blockState)) {
						return y + 1;
					}
				}
			}
		}

		return 0;
	}

	protected abstract void sampleNoiseColumn(double[] ds, int i, int j);

	public int getNoiseSizeY() {
		return this.noiseSizeY + 1;
	}

	@Override
	public void buildSurface(ChunkRegion chunkRegion, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setSeed(i, j);
		ChunkPos chunkPos2 = chunk.getPos();
		int k = chunkPos2.getStartX();
		int l = chunkPos2.getStartZ();
		double d = 0.0625;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = 0; m < 16; m++) {
			for (int n = 0; n < 16; n++) {
				int o = k + m;
				int p = l + n;
				int q = chunk.sampleHeightmap(Heightmap.Type.field_13194, m, n) + 1;
				double e = this.surfaceDepthNoise.sample((double)o * 0.0625, (double)p * 0.0625, 0.0625, (double)m * 0.0625) * 15.0;
				chunkRegion.getBiome(mutable.set(k + m, q, l + n))
					.buildSurface(
						chunkRandom, chunk, o, p, q, e, this.getConfig().getDefaultBlock(), this.getConfig().getDefaultFluid(), this.getSeaLevel(), this.world.getSeed()
					);
			}
		}

		this.buildBedrock(chunk, chunkRandom);
	}

	protected void buildBedrock(Chunk chunk, Random random) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = chunk.getPos().getStartX();
		int j = chunk.getPos().getStartZ();
		T chunkGeneratorConfig = this.getConfig();
		int k = chunkGeneratorConfig.getMinY();
		int l = chunkGeneratorConfig.getMaxY();

		for (BlockPos blockPos : BlockPos.iterate(i, 0, j, i + 15, 0, j + 15)) {
			if (l > 0) {
				for (int m = l; m >= l - 4; m--) {
					if (m >= l - random.nextInt(5)) {
						chunk.setBlockState(mutable.set(blockPos.getX(), m, blockPos.getZ()), Blocks.field_9987.getDefaultState(), false);
					}
				}
			}

			if (k < 256) {
				for (int n = k + 4; n >= k; n--) {
					if (n <= k + random.nextInt(5)) {
						chunk.setBlockState(mutable.set(blockPos.getX(), n, blockPos.getZ()), Blocks.field_9987.getDefaultState(), false);
					}
				}
			}
		}
	}

	@Override
	public void populateNoise(IWorld iWorld, Chunk chunk) {
		int i = this.getSeaLevel();
		ObjectList<PoolStructurePiece> objectList = new ObjectArrayList(10);
		ObjectList<JigsawJunction> objectList2 = new ObjectArrayList(32);
		ChunkPos chunkPos = chunk.getPos();
		int j = chunkPos.x;
		int k = chunkPos.z;
		int l = j << 4;
		int m = k << 4;

		for (StructureFeature<?> structureFeature : Feature.JIGSAW_STRUCTURES) {
			String string = structureFeature.getName();
			LongIterator longIterator = chunk.getStructureReferences(string).iterator();

			while (longIterator.hasNext()) {
				long n = longIterator.nextLong();
				ChunkPos chunkPos2 = new ChunkPos(n);
				Chunk chunk2 = iWorld.getChunk(chunkPos2.x, chunkPos2.z);
				StructureStart structureStart = chunk2.getStructureStart(string);
				if (structureStart != null && structureStart.hasChildren()) {
					for (StructurePiece structurePiece : structureStart.getChildren()) {
						if (structurePiece.method_16654(chunkPos, 12) && structurePiece instanceof PoolStructurePiece) {
							PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
							StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
							if (projection == StructurePool.Projection.field_16687) {
								objectList.add(poolStructurePiece);
							}

							for (JigsawJunction jigsawJunction : poolStructurePiece.getJunctions()) {
								int o = jigsawJunction.getSourceX();
								int p = jigsawJunction.getSourceZ();
								if (o > l - 12 && p > m - 12 && o < l + 15 + 12 && p < m + 15 + 12) {
									objectList2.add(jigsawJunction);
								}
							}
						}
					}
				}
			}
		}

		double[][][] ds = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];

		for (int q = 0; q < this.noiseSizeZ + 1; q++) {
			ds[0][q] = new double[this.noiseSizeY + 1];
			this.sampleNoiseColumn(ds[0][q], j * this.noiseSizeX, k * this.noiseSizeZ + q);
			ds[1][q] = new double[this.noiseSizeY + 1];
		}

		ProtoChunk protoChunk = (ProtoChunk)chunk;
		Heightmap heightmap = protoChunk.getHeightmap(Heightmap.Type.field_13195);
		Heightmap heightmap2 = protoChunk.getHeightmap(Heightmap.Type.field_13194);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		ObjectListIterator<PoolStructurePiece> objectListIterator = objectList.iterator();
		ObjectListIterator<JigsawJunction> objectListIterator2 = objectList2.iterator();

		for (int r = 0; r < this.noiseSizeX; r++) {
			for (int s = 0; s < this.noiseSizeZ + 1; s++) {
				this.sampleNoiseColumn(ds[1][s], j * this.noiseSizeX + r + 1, k * this.noiseSizeZ + s);
			}

			for (int t = 0; t < this.noiseSizeZ; t++) {
				ChunkSection chunkSection = protoChunk.getSection(15);
				chunkSection.lock();

				for (int u = this.noiseSizeY - 1; u >= 0; u--) {
					double d = ds[0][t][u];
					double e = ds[0][t + 1][u];
					double f = ds[1][t][u];
					double g = ds[1][t + 1][u];
					double h = ds[0][t][u + 1];
					double v = ds[0][t + 1][u + 1];
					double w = ds[1][t][u + 1];
					double x = ds[1][t + 1][u + 1];

					for (int y = this.verticalNoiseResolution - 1; y >= 0; y--) {
						int z = u * this.verticalNoiseResolution + y;
						int aa = z & 15;
						int ab = z >> 4;
						if (chunkSection.getYOffset() >> 4 != ab) {
							chunkSection.unlock();
							chunkSection = protoChunk.getSection(ab);
							chunkSection.lock();
						}

						double ac = (double)y / (double)this.verticalNoiseResolution;
						double ad = MathHelper.lerp(ac, d, h);
						double ae = MathHelper.lerp(ac, f, w);
						double af = MathHelper.lerp(ac, e, v);
						double ag = MathHelper.lerp(ac, g, x);

						for (int ah = 0; ah < this.horizontalNoiseResolution; ah++) {
							int ai = l + r * this.horizontalNoiseResolution + ah;
							int aj = ai & 15;
							double ak = (double)ah / (double)this.horizontalNoiseResolution;
							double al = MathHelper.lerp(ak, ad, ae);
							double am = MathHelper.lerp(ak, af, ag);

							for (int an = 0; an < this.horizontalNoiseResolution; an++) {
								int ao = m + t * this.horizontalNoiseResolution + an;
								int ap = ao & 15;
								double aq = (double)an / (double)this.horizontalNoiseResolution;
								double ar = MathHelper.lerp(aq, al, am);
								double as = MathHelper.clamp(ar / 200.0, -1.0, 1.0);
								as = as / 2.0 - as * as * as / 24.0;

								while (objectListIterator.hasNext()) {
									PoolStructurePiece poolStructurePiece2 = (PoolStructurePiece)objectListIterator.next();
									BlockBox blockBox = poolStructurePiece2.getBoundingBox();
									int at = Math.max(0, Math.max(blockBox.minX - ai, ai - blockBox.maxX));
									int au = z - (blockBox.minY + poolStructurePiece2.getGroundLevelDelta());
									int av = Math.max(0, Math.max(blockBox.minZ - ao, ao - blockBox.maxZ));
									as += method_16572(at, au, av) * 0.8;
								}

								objectListIterator.back(objectList.size());

								while (objectListIterator2.hasNext()) {
									JigsawJunction jigsawJunction2 = (JigsawJunction)objectListIterator2.next();
									int aw = ai - jigsawJunction2.getSourceX();
									int ax = z - jigsawJunction2.getSourceGroundY();
									int ay = ao - jigsawJunction2.getSourceZ();
									as += method_16572(aw, ax, ay) * 0.4;
								}

								objectListIterator2.back(objectList2.size());
								BlockState blockState;
								if (as > 0.0) {
									blockState = this.defaultBlock;
								} else if (z < i) {
									blockState = this.defaultFluid;
								} else {
									blockState = AIR;
								}

								if (blockState != AIR) {
									if (blockState.getLuminance() != 0) {
										mutable.set(ai, z, ao);
										protoChunk.addLightSource(mutable);
									}

									chunkSection.setBlockState(aj, aa, ap, blockState, false);
									heightmap.trackUpdate(aj, z, ap, blockState);
									heightmap2.trackUpdate(aj, z, ap, blockState);
								}
							}
						}
					}
				}

				chunkSection.unlock();
			}

			double[][] es = ds[0];
			ds[0] = ds[1];
			ds[1] = es;
		}
	}

	private static double method_16572(int i, int j, int k) {
		int l = i + 12;
		int m = j + 12;
		int n = k + 12;
		if (l < 0 || l >= 24) {
			return 0.0;
		} else if (m < 0 || m >= 24) {
			return 0.0;
		} else {
			return n >= 0 && n < 24 ? (double)field_16649[n * 24 * 24 + l * 24 + m] : 0.0;
		}
	}

	private static double method_16571(int i, int j, int k) {
		double d = (double)(i * i + k * k);
		double e = (double)j + 0.5;
		double f = e * e;
		double g = Math.pow(Math.E, -(f / 16.0 + d / 16.0));
		double h = -e * MathHelper.fastInverseSqrt(f / 2.0 + d / 2.0) / 2.0;
		return h * g;
	}
}
