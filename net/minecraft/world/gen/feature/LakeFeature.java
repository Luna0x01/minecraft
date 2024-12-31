package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class LakeFeature extends Feature<SingleStateFeatureConfig> {
	private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();

	public LakeFeature(Codec<SingleStateFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<SingleStateFeatureConfig> context) {
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();
		SingleStateFeatureConfig singleStateFeatureConfig = context.getConfig();

		while (blockPos.getY() > structureWorldAccess.getBottomY() + 5 && structureWorldAccess.isAir(blockPos)) {
			blockPos = blockPos.down();
		}

		if (blockPos.getY() <= structureWorldAccess.getBottomY() + 4) {
			return false;
		} else {
			blockPos = blockPos.down(4);
			if (structureWorldAccess.getStructures(ChunkSectionPos.from(blockPos), StructureFeature.VILLAGE).findAny().isPresent()) {
				return false;
			} else {
				boolean[] bls = new boolean[2048];
				int i = random.nextInt(4) + 4;

				for (int j = 0; j < i; j++) {
					double d = random.nextDouble() * 6.0 + 3.0;
					double e = random.nextDouble() * 4.0 + 2.0;
					double f = random.nextDouble() * 6.0 + 3.0;
					double g = random.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
					double h = random.nextDouble() * (8.0 - e - 4.0) + 2.0 + e / 2.0;
					double k = random.nextDouble() * (16.0 - f - 2.0) + 1.0 + f / 2.0;

					for (int l = 1; l < 15; l++) {
						for (int m = 1; m < 15; m++) {
							for (int n = 1; n < 7; n++) {
								double o = ((double)l - g) / (d / 2.0);
								double p = ((double)n - h) / (e / 2.0);
								double q = ((double)m - k) / (f / 2.0);
								double r = o * o + p * p + q * q;
								if (r < 1.0) {
									bls[(l * 16 + m) * 8 + n] = true;
								}
							}
						}
					}
				}

				for (int s = 0; s < 16; s++) {
					for (int t = 0; t < 16; t++) {
						for (int u = 0; u < 8; u++) {
							boolean bl = !bls[(s * 16 + t) * 8 + u]
								&& (
									s < 15 && bls[((s + 1) * 16 + t) * 8 + u]
										|| s > 0 && bls[((s - 1) * 16 + t) * 8 + u]
										|| t < 15 && bls[(s * 16 + t + 1) * 8 + u]
										|| t > 0 && bls[(s * 16 + (t - 1)) * 8 + u]
										|| u < 7 && bls[(s * 16 + t) * 8 + u + 1]
										|| u > 0 && bls[(s * 16 + t) * 8 + (u - 1)]
								);
							if (bl) {
								Material material = structureWorldAccess.getBlockState(blockPos.add(s, u, t)).getMaterial();
								if (u >= 4 && material.isLiquid()) {
									return false;
								}

								if (u < 4 && !material.isSolid() && structureWorldAccess.getBlockState(blockPos.add(s, u, t)) != singleStateFeatureConfig.state) {
									return false;
								}
							}
						}
					}
				}

				for (int v = 0; v < 16; v++) {
					for (int w = 0; w < 16; w++) {
						for (int x = 0; x < 8; x++) {
							if (bls[(v * 16 + w) * 8 + x]) {
								BlockPos blockPos2 = blockPos.add(v, x, w);
								boolean bl2 = x >= 4;
								structureWorldAccess.setBlockState(blockPos2, bl2 ? CAVE_AIR : singleStateFeatureConfig.state, 2);
								if (bl2) {
									structureWorldAccess.getBlockTickScheduler().schedule(blockPos2, CAVE_AIR.getBlock(), 0);
									this.markBlocksAboveForPostProcessing(structureWorldAccess, blockPos2);
								}
							}
						}
					}
				}

				for (int y = 0; y < 16; y++) {
					for (int z = 0; z < 16; z++) {
						for (int aa = 4; aa < 8; aa++) {
							if (bls[(y * 16 + z) * 8 + aa]) {
								BlockPos blockPos3 = blockPos.add(y, aa - 1, z);
								if (isSoil(structureWorldAccess.getBlockState(blockPos3)) && structureWorldAccess.getLightLevel(LightType.SKY, blockPos.add(y, aa, z)) > 0) {
									Biome biome = structureWorldAccess.getBiome(blockPos3);
									if (biome.getGenerationSettings().getSurfaceConfig().getTopMaterial().isOf(Blocks.MYCELIUM)) {
										structureWorldAccess.setBlockState(blockPos3, Blocks.MYCELIUM.getDefaultState(), 2);
									} else {
										structureWorldAccess.setBlockState(blockPos3, Blocks.GRASS_BLOCK.getDefaultState(), 2);
									}
								}
							}
						}
					}
				}

				if (singleStateFeatureConfig.state.getMaterial() == Material.LAVA) {
					BlockSource blockSource = context.getGenerator().getBlockSource();

					for (int ab = 0; ab < 16; ab++) {
						for (int ac = 0; ac < 16; ac++) {
							for (int ad = 0; ad < 8; ad++) {
								boolean bl3 = !bls[(ab * 16 + ac) * 8 + ad]
									&& (
										ab < 15 && bls[((ab + 1) * 16 + ac) * 8 + ad]
											|| ab > 0 && bls[((ab - 1) * 16 + ac) * 8 + ad]
											|| ac < 15 && bls[(ab * 16 + ac + 1) * 8 + ad]
											|| ac > 0 && bls[(ab * 16 + (ac - 1)) * 8 + ad]
											|| ad < 7 && bls[(ab * 16 + ac) * 8 + ad + 1]
											|| ad > 0 && bls[(ab * 16 + ac) * 8 + (ad - 1)]
									);
								if (bl3 && (ad < 4 || random.nextInt(2) != 0)) {
									BlockState blockState = structureWorldAccess.getBlockState(blockPos.add(ab, ad, ac));
									if (blockState.getMaterial().isSolid() && !blockState.isIn(BlockTags.LAVA_POOL_STONE_REPLACEABLES)) {
										BlockPos blockPos4 = blockPos.add(ab, ad, ac);
										structureWorldAccess.setBlockState(blockPos4, blockSource.get(blockPos4), 2);
										this.markBlocksAboveForPostProcessing(structureWorldAccess, blockPos4);
									}
								}
							}
						}
					}
				}

				if (singleStateFeatureConfig.state.getMaterial() == Material.WATER) {
					for (int ae = 0; ae < 16; ae++) {
						for (int af = 0; af < 16; af++) {
							int ag = 4;
							BlockPos blockPos5 = blockPos.add(ae, 4, af);
							if (structureWorldAccess.getBiome(blockPos5).canSetIce(structureWorldAccess, blockPos5, false)) {
								structureWorldAccess.setBlockState(blockPos5, Blocks.ICE.getDefaultState(), 2);
							}
						}
					}
				}

				return true;
			}
		}
	}
}
