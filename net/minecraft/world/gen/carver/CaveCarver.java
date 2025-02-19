package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.AquiferSampler;

public class CaveCarver extends Carver<CaveCarverConfig> {
	public CaveCarver(Codec<CaveCarverConfig> codec) {
		super(codec);
	}

	public boolean shouldCarve(CaveCarverConfig caveCarverConfig, Random random) {
		return random.nextFloat() <= caveCarverConfig.probability;
	}

	public boolean carve(
		CarverContext carverContext,
		CaveCarverConfig caveCarverConfig,
		Chunk chunk,
		Function<BlockPos, Biome> function,
		Random random,
		AquiferSampler aquiferSampler,
		ChunkPos chunkPos,
		BitSet bitSet
	) {
		int i = ChunkSectionPos.getBlockCoord(this.getBranchFactor() * 2 - 1);
		int j = random.nextInt(random.nextInt(random.nextInt(this.getMaxCaveCount()) + 1) + 1);

		for (int k = 0; k < j; k++) {
			double d = (double)chunkPos.getOffsetX(random.nextInt(16));
			double e = (double)caveCarverConfig.y.get(random, carverContext);
			double f = (double)chunkPos.getOffsetZ(random.nextInt(16));
			double g = (double)caveCarverConfig.horizontalRadiusMultiplier.get(random);
			double h = (double)caveCarverConfig.verticalRadiusMultiplier.get(random);
			double l = (double)caveCarverConfig.floorLevel.get(random);
			Carver.SkipPredicate skipPredicate = (context, scaledRelativeX, scaledRelativeY, scaledRelativeZ, y) -> isPositionExcluded(
					scaledRelativeX, scaledRelativeY, scaledRelativeZ, l
				);
			int m = 1;
			if (random.nextInt(4) == 0) {
				double n = (double)caveCarverConfig.yScale.get(random);
				float o = 1.0F + random.nextFloat() * 6.0F;
				this.carveCave(carverContext, caveCarverConfig, chunk, function, random.nextLong(), aquiferSampler, d, e, f, o, n, bitSet, skipPredicate);
				m += random.nextInt(4);
			}

			for (int p = 0; p < m; p++) {
				float q = random.nextFloat() * (float) (Math.PI * 2);
				float r = (random.nextFloat() - 0.5F) / 4.0F;
				float s = this.getTunnelSystemWidth(random);
				int t = i - random.nextInt(i / 4);
				int u = 0;
				this.carveTunnels(
					carverContext,
					caveCarverConfig,
					chunk,
					function,
					random.nextLong(),
					aquiferSampler,
					d,
					e,
					f,
					g,
					h,
					s,
					q,
					r,
					0,
					t,
					this.getTunnelSystemHeightWidthRatio(),
					bitSet,
					skipPredicate
				);
			}
		}

		return true;
	}

	protected int getMaxCaveCount() {
		return 15;
	}

	protected float getTunnelSystemWidth(Random random) {
		float f = random.nextFloat() * 2.0F + random.nextFloat();
		if (random.nextInt(10) == 0) {
			f *= random.nextFloat() * random.nextFloat() * 3.0F + 1.0F;
		}

		return f;
	}

	protected double getTunnelSystemHeightWidthRatio() {
		return 1.0;
	}

	protected void carveCave(
		CarverContext context,
		CaveCarverConfig config,
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		long seed,
		AquiferSampler aquiferSampler,
		double x,
		double y,
		double z,
		float yaw,
		double yawPitchRatio,
		BitSet carvingMask,
		Carver.SkipPredicate skipPredicate
	) {
		double d = 1.5 + (double)(MathHelper.sin((float) (Math.PI / 2)) * yaw);
		double e = d * yawPitchRatio;
		this.carveRegion(context, config, chunk, posToBiome, seed, aquiferSampler, x + 1.0, y, z, d, e, carvingMask, skipPredicate);
	}

	protected void carveTunnels(
		CarverContext context,
		CaveCarverConfig config,
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		long seed,
		AquiferSampler aquiferSampler,
		double x,
		double y,
		double z,
		double horizontalScale,
		double verticalScale,
		float width,
		float yaw,
		float pitch,
		int branchStartIndex,
		int branchCount,
		double yawPitchRatio,
		BitSet carvingMask,
		Carver.SkipPredicate skipPredicate
	) {
		Random random = new Random(seed);
		int i = random.nextInt(branchCount / 2) + branchCount / 4;
		boolean bl = random.nextInt(6) == 0;
		float f = 0.0F;
		float g = 0.0F;

		for (int j = branchStartIndex; j < branchCount; j++) {
			double d = 1.5 + (double)(MathHelper.sin((float) Math.PI * (float)j / (float)branchCount) * width);
			double e = d * yawPitchRatio;
			float h = MathHelper.cos(pitch);
			x += (double)(MathHelper.cos(yaw) * h);
			y += (double)MathHelper.sin(pitch);
			z += (double)(MathHelper.sin(yaw) * h);
			pitch *= bl ? 0.92F : 0.7F;
			pitch += g * 0.1F;
			yaw += f * 0.1F;
			g *= 0.9F;
			f *= 0.75F;
			g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (j == i && width > 1.0F) {
				this.carveTunnels(
					context,
					config,
					chunk,
					posToBiome,
					random.nextLong(),
					aquiferSampler,
					x,
					y,
					z,
					horizontalScale,
					verticalScale,
					random.nextFloat() * 0.5F + 0.5F,
					yaw - (float) (Math.PI / 2),
					pitch / 3.0F,
					j,
					branchCount,
					1.0,
					carvingMask,
					skipPredicate
				);
				this.carveTunnels(
					context,
					config,
					chunk,
					posToBiome,
					random.nextLong(),
					aquiferSampler,
					x,
					y,
					z,
					horizontalScale,
					verticalScale,
					random.nextFloat() * 0.5F + 0.5F,
					yaw + (float) (Math.PI / 2),
					pitch / 3.0F,
					j,
					branchCount,
					1.0,
					carvingMask,
					skipPredicate
				);
				return;
			}

			if (random.nextInt(4) != 0) {
				if (!canCarveBranch(chunk.getPos(), x, z, j, branchCount, width)) {
					return;
				}

				this.carveRegion(context, config, chunk, posToBiome, seed, aquiferSampler, x, y, z, d * horizontalScale, e * verticalScale, carvingMask, skipPredicate);
			}
		}
	}

	private static boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, double floorY) {
		return scaledRelativeY <= floorY ? true : scaledRelativeX * scaledRelativeX + scaledRelativeY * scaledRelativeY + scaledRelativeZ * scaledRelativeZ >= 1.0;
	}
}
