package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.util.CaveSurface;
import net.minecraft.world.gen.feature.util.DripstoneHelper;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DripstoneClusterFeature extends Feature<DripstoneClusterFeatureConfig> {
	public DripstoneClusterFeature(Codec<DripstoneClusterFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DripstoneClusterFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		DripstoneClusterFeatureConfig dripstoneClusterFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		if (!DripstoneHelper.canGenerate(structureWorldAccess, blockPos)) {
			return false;
		} else {
			int i = dripstoneClusterFeatureConfig.height.get(random);
			float f = dripstoneClusterFeatureConfig.wetness.get(random);
			float g = dripstoneClusterFeatureConfig.density.get(random);
			int j = dripstoneClusterFeatureConfig.radius.get(random);
			int k = dripstoneClusterFeatureConfig.radius.get(random);

			for (int l = -j; l <= j; l++) {
				for (int m = -k; m <= k; m++) {
					double d = this.dripstoneChance(j, k, l, m, dripstoneClusterFeatureConfig);
					BlockPos blockPos2 = blockPos.add(l, 0, m);
					this.generate(structureWorldAccess, random, blockPos2, l, m, f, d, i, g, dripstoneClusterFeatureConfig);
				}
			}

			return true;
		}
	}

	private void generate(
		StructureWorldAccess world,
		Random random,
		BlockPos pos,
		int localX,
		int localZ,
		float wetness,
		double dripstoneChance,
		int height,
		float density,
		DripstoneClusterFeatureConfig config
	) {
		Optional<CaveSurface> optional = CaveSurface.create(
			world, pos, config.floorToCeilingSearchRange, DripstoneHelper::canGenerate, DripstoneHelper::canReplaceOrLava
		);
		if (optional.isPresent()) {
			OptionalInt optionalInt = ((CaveSurface)optional.get()).getCeilingHeight();
			OptionalInt optionalInt2 = ((CaveSurface)optional.get()).getFloorHeight();
			if (optionalInt.isPresent() || optionalInt2.isPresent()) {
				boolean bl = random.nextFloat() < wetness;
				CaveSurface caveSurface;
				if (bl && optionalInt2.isPresent() && this.canWaterSpawn(world, pos.withY(optionalInt2.getAsInt()))) {
					int i = optionalInt2.getAsInt();
					caveSurface = ((CaveSurface)optional.get()).withFloor(OptionalInt.of(i - 1));
					world.setBlockState(pos.withY(i), Blocks.WATER.getDefaultState(), 2);
				} else {
					caveSurface = (CaveSurface)optional.get();
				}

				OptionalInt optionalInt3 = caveSurface.getFloorHeight();
				boolean bl2 = random.nextDouble() < dripstoneChance;
				int m;
				if (optionalInt.isPresent() && bl2 && !this.isLava(world, pos.withY(optionalInt.getAsInt()))) {
					int j = config.dripstoneBlockLayerThickness.get(random);
					this.placeDripstoneBlocks(world, pos.withY(optionalInt.getAsInt()), j, Direction.UP);
					int k;
					if (optionalInt3.isPresent()) {
						k = Math.min(height, optionalInt.getAsInt() - optionalInt3.getAsInt());
					} else {
						k = height;
					}

					m = this.getHeight(random, localX, localZ, density, k, config);
				} else {
					m = 0;
				}

				boolean bl3 = random.nextDouble() < dripstoneChance;
				int p;
				if (optionalInt3.isPresent() && bl3 && !this.isLava(world, pos.withY(optionalInt3.getAsInt()))) {
					int o = config.dripstoneBlockLayerThickness.get(random);
					this.placeDripstoneBlocks(world, pos.withY(optionalInt3.getAsInt()), o, Direction.DOWN);
					p = Math.max(0, m + MathHelper.nextBetween(random, -config.maxStalagmiteStalactiteHeightDiff, config.maxStalagmiteStalactiteHeightDiff));
				} else {
					p = 0;
				}

				int y;
				int x;
				if (optionalInt.isPresent() && optionalInt3.isPresent() && optionalInt.getAsInt() - m <= optionalInt3.getAsInt() + p) {
					int r = optionalInt3.getAsInt();
					int s = optionalInt.getAsInt();
					int t = Math.max(s - m, r + 1);
					int u = Math.min(r + p, s - 1);
					int v = MathHelper.nextBetween(random, t, u + 1);
					int w = v - 1;
					x = s - v;
					y = w - r;
				} else {
					x = m;
					y = p;
				}

				boolean bl4 = random.nextBoolean() && x > 0 && y > 0 && caveSurface.getOptionalHeight().isPresent() && x + y == caveSurface.getOptionalHeight().getAsInt();
				if (optionalInt.isPresent()) {
					DripstoneHelper.generatePointedDripstone(world, pos.withY(optionalInt.getAsInt() - 1), Direction.DOWN, x, bl4);
				}

				if (optionalInt3.isPresent()) {
					DripstoneHelper.generatePointedDripstone(world, pos.withY(optionalInt3.getAsInt() + 1), Direction.UP, y, bl4);
				}
			}
		}
	}

	private boolean isLava(WorldView world, BlockPos pos) {
		return world.getBlockState(pos).isOf(Blocks.LAVA);
	}

	private int getHeight(Random random, int localX, int localZ, float density, int height, DripstoneClusterFeatureConfig config) {
		if (random.nextFloat() > density) {
			return 0;
		} else {
			int i = Math.abs(localX) + Math.abs(localZ);
			float f = (float)MathHelper.clampedLerpFromProgress((double)i, 0.0, (double)config.maxDistanceFromCenterAffectingHeightBias, (double)height / 2.0, 0.0);
			return (int)clampedGaussian(random, 0.0F, (float)height, f, (float)config.heightDeviation);
		}
	}

	private boolean canWaterSpawn(StructureWorldAccess world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (!blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.DRIPSTONE_BLOCK) && !blockState.isOf(Blocks.POINTED_DRIPSTONE)) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				if (!this.isStoneOrWater(world, pos.offset(direction))) {
					return false;
				}
			}

			return this.isStoneOrWater(world, pos.down());
		} else {
			return false;
		}
	}

	private boolean isStoneOrWater(WorldAccess world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return blockState.isIn(BlockTags.BASE_STONE_OVERWORLD) || blockState.getFluidState().isIn(FluidTags.WATER);
	}

	private void placeDripstoneBlocks(StructureWorldAccess world, BlockPos pos, int height, Direction direction) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int i = 0; i < height; i++) {
			if (!DripstoneHelper.generateDripstoneBlock(world, mutable)) {
				return;
			}

			mutable.move(direction);
		}
	}

	private double dripstoneChance(int radiusX, int radiusZ, int localX, int localZ, DripstoneClusterFeatureConfig config) {
		int i = radiusX - Math.abs(localX);
		int j = radiusZ - Math.abs(localZ);
		int k = Math.min(i, j);
		return MathHelper.clampedLerpFromProgress(
			(double)k, 0.0, (double)config.maxDistanceFromCenterAffectingChanceOfDripstoneColumn, (double)config.chanceOfDripstoneColumnAtMaxDistanceFromCenter, 1.0
		);
	}

	private static float clampedGaussian(Random random, float min, float max, float mean, float deviation) {
		return ClampedNormalFloatProvider.get(random, mean, deviation, min, max);
	}
}
