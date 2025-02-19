package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.CaveSurface;
import net.minecraft.world.gen.feature.util.DripstoneHelper;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class LargeDripstoneFeature extends Feature<LargeDripstoneFeatureConfig> {
	public LargeDripstoneFeature(Codec<LargeDripstoneFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<LargeDripstoneFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		LargeDripstoneFeatureConfig largeDripstoneFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		if (!DripstoneHelper.canGenerate(structureWorldAccess, blockPos)) {
			return false;
		} else {
			Optional<CaveSurface> optional = CaveSurface.create(
				structureWorldAccess, blockPos, largeDripstoneFeatureConfig.floorToCeilingSearchRange, DripstoneHelper::canGenerate, DripstoneHelper::canReplaceOrLava
			);
			if (optional.isPresent() && optional.get() instanceof CaveSurface.Bounded) {
				CaveSurface.Bounded bounded = (CaveSurface.Bounded)optional.get();
				if (bounded.getHeight() < 4) {
					return false;
				} else {
					int i = (int)((float)bounded.getHeight() * largeDripstoneFeatureConfig.maxColumnRadiusToCaveHeightRatio);
					int j = MathHelper.clamp(i, largeDripstoneFeatureConfig.columnRadius.getMin(), largeDripstoneFeatureConfig.columnRadius.getMax());
					int k = MathHelper.nextBetween(random, largeDripstoneFeatureConfig.columnRadius.getMin(), j);
					LargeDripstoneFeature.DripstoneGenerator dripstoneGenerator = createGenerator(
						blockPos.withY(bounded.getCeiling() - 1), false, random, k, largeDripstoneFeatureConfig.stalactiteBluntness, largeDripstoneFeatureConfig.heightScale
					);
					LargeDripstoneFeature.DripstoneGenerator dripstoneGenerator2 = createGenerator(
						blockPos.withY(bounded.getFloor() + 1), true, random, k, largeDripstoneFeatureConfig.stalagmiteBluntness, largeDripstoneFeatureConfig.heightScale
					);
					LargeDripstoneFeature.WindModifier windModifier;
					if (dripstoneGenerator.generateWind(largeDripstoneFeatureConfig) && dripstoneGenerator2.generateWind(largeDripstoneFeatureConfig)) {
						windModifier = new LargeDripstoneFeature.WindModifier(blockPos.getY(), random, largeDripstoneFeatureConfig.windSpeed);
					} else {
						windModifier = LargeDripstoneFeature.WindModifier.create();
					}

					boolean bl = dripstoneGenerator.canGenerate(structureWorldAccess, windModifier);
					boolean bl2 = dripstoneGenerator2.canGenerate(structureWorldAccess, windModifier);
					if (bl) {
						dripstoneGenerator.generate(structureWorldAccess, random, windModifier);
					}

					if (bl2) {
						dripstoneGenerator2.generate(structureWorldAccess, random, windModifier);
					}

					return true;
				}
			} else {
				return false;
			}
		}
	}

	private static LargeDripstoneFeature.DripstoneGenerator createGenerator(
		BlockPos pos, boolean isStalagmite, Random random, int scale, FloatProvider bluntness, FloatProvider heightScale
	) {
		return new LargeDripstoneFeature.DripstoneGenerator(pos, isStalagmite, scale, (double)bluntness.get(random), (double)heightScale.get(random));
	}

	private void method_35360(
		StructureWorldAccess structureWorldAccess, BlockPos blockPos, CaveSurface.Bounded bounded, LargeDripstoneFeature.WindModifier windModifier
	) {
		structureWorldAccess.setBlockState(windModifier.modify(blockPos.withY(bounded.getCeiling() - 1)), Blocks.DIAMOND_BLOCK.getDefaultState(), 2);
		structureWorldAccess.setBlockState(windModifier.modify(blockPos.withY(bounded.getFloor() + 1)), Blocks.GOLD_BLOCK.getDefaultState(), 2);

		for (BlockPos.Mutable mutable = blockPos.withY(bounded.getFloor() + 2).mutableCopy(); mutable.getY() < bounded.getCeiling() - 1; mutable.move(Direction.UP)) {
			BlockPos blockPos2 = windModifier.modify(mutable);
			if (DripstoneHelper.canGenerate(structureWorldAccess, blockPos2) || structureWorldAccess.getBlockState(blockPos2).isOf(Blocks.DRIPSTONE_BLOCK)) {
				structureWorldAccess.setBlockState(blockPos2, Blocks.CREEPER_HEAD.getDefaultState(), 2);
			}
		}
	}

	static final class DripstoneGenerator {
		private BlockPos pos;
		private final boolean isStalagmite;
		private int scale;
		private final double bluntness;
		private final double heightScale;

		DripstoneGenerator(BlockPos blockPos, boolean bl, int i, double d, double e) {
			this.pos = blockPos;
			this.isStalagmite = bl;
			this.scale = i;
			this.bluntness = d;
			this.heightScale = e;
		}

		private int getBaseScale() {
			return this.scale(0.0F);
		}

		private int method_35361() {
			return this.isStalagmite ? this.pos.getY() : this.pos.getY() - this.getBaseScale();
		}

		private int method_35362() {
			return !this.isStalagmite ? this.pos.getY() : this.pos.getY() + this.getBaseScale();
		}

		boolean canGenerate(StructureWorldAccess world, LargeDripstoneFeature.WindModifier wind) {
			while (this.scale > 1) {
				BlockPos.Mutable mutable = this.pos.mutableCopy();
				int i = Math.min(10, this.getBaseScale());

				for (int j = 0; j < i; j++) {
					if (world.getBlockState(mutable).isOf(Blocks.LAVA)) {
						return false;
					}

					if (DripstoneHelper.canGenerateBase(world, wind.modify(mutable), this.scale)) {
						this.pos = mutable;
						return true;
					}

					mutable.move(this.isStalagmite ? Direction.DOWN : Direction.UP);
				}

				this.scale /= 2;
			}

			return false;
		}

		private int scale(float height) {
			return (int)DripstoneHelper.scaleHeightFromRadius((double)height, (double)this.scale, this.heightScale, this.bluntness);
		}

		void generate(StructureWorldAccess world, Random random, LargeDripstoneFeature.WindModifier wind) {
			for (int i = -this.scale; i <= this.scale; i++) {
				for (int j = -this.scale; j <= this.scale; j++) {
					float f = MathHelper.sqrt((float)(i * i + j * j));
					if (!(f > (float)this.scale)) {
						int k = this.scale(f);
						if (k > 0) {
							if ((double)random.nextFloat() < 0.2) {
								k = (int)((float)k * MathHelper.nextBetween(random, 0.8F, 1.0F));
							}

							BlockPos.Mutable mutable = this.pos.add(i, 0, j).mutableCopy();
							boolean bl = false;

							for (int l = 0; l < k; l++) {
								BlockPos blockPos = wind.modify(mutable);
								if (DripstoneHelper.canGenerateOrLava(world, blockPos)) {
									bl = true;
									Block block = Blocks.DRIPSTONE_BLOCK;
									world.setBlockState(blockPos, block.getDefaultState(), 2);
								} else if (bl && world.getBlockState(blockPos).isIn(BlockTags.BASE_STONE_OVERWORLD)) {
									break;
								}

								mutable.move(this.isStalagmite ? Direction.UP : Direction.DOWN);
							}
						}
					}
				}
			}
		}

		boolean generateWind(LargeDripstoneFeatureConfig config) {
			return this.scale >= config.minRadiusForWind && this.bluntness >= (double)config.minBluntnessForWind;
		}
	}

	static final class WindModifier {
		private final int y;
		@Nullable
		private final Vec3d wind;

		WindModifier(int y, Random random, FloatProvider wind) {
			this.y = y;
			float f = wind.get(random);
			float g = MathHelper.nextBetween(random, 0.0F, (float) Math.PI);
			this.wind = new Vec3d((double)(MathHelper.cos(g) * f), 0.0, (double)(MathHelper.sin(g) * f));
		}

		private WindModifier() {
			this.y = 0;
			this.wind = null;
		}

		static LargeDripstoneFeature.WindModifier create() {
			return new LargeDripstoneFeature.WindModifier();
		}

		BlockPos modify(BlockPos pos) {
			if (this.wind == null) {
				return pos;
			} else {
				int i = this.y - pos.getY();
				Vec3d vec3d = this.wind.multiply((double)i);
				return pos.add(vec3d.x, 0.0, vec3d.z);
			}
		}
	}
}
