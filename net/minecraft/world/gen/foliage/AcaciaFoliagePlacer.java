package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;

public class AcaciaFoliagePlacer extends FoliagePlacer {
	public AcaciaFoliagePlacer(int i, int j) {
		super(i, j, FoliagePlacerType.field_21302);
	}

	public <T> AcaciaFoliagePlacer(Dynamic<T> dynamic) {
		this(dynamic.get("radius").asInt(0), dynamic.get("radius_random").asInt(0));
	}

	@Override
	public void generate(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BranchedTreeFeatureConfig branchedTreeFeatureConfig,
		int i,
		int j,
		int k,
		BlockPos blockPos,
		Set<BlockPos> set
	) {
		branchedTreeFeatureConfig.foliagePlacer.generate(modifiableTestableWorld, random, branchedTreeFeatureConfig, i, blockPos, 0, k, set);
		branchedTreeFeatureConfig.foliagePlacer.generate(modifiableTestableWorld, random, branchedTreeFeatureConfig, i, blockPos, 1, 1, set);
		BlockPos blockPos2 = blockPos.up();

		for (int l = -1; l <= 1; l++) {
			for (int m = -1; m <= 1; m++) {
				this.method_23450(modifiableTestableWorld, random, blockPos2.add(l, 0, m), branchedTreeFeatureConfig, set);
			}
		}

		for (int n = 2; n <= k - 1; n++) {
			this.method_23450(modifiableTestableWorld, random, blockPos2.east(n), branchedTreeFeatureConfig, set);
			this.method_23450(modifiableTestableWorld, random, blockPos2.west(n), branchedTreeFeatureConfig, set);
			this.method_23450(modifiableTestableWorld, random, blockPos2.south(n), branchedTreeFeatureConfig, set);
			this.method_23450(modifiableTestableWorld, random, blockPos2.north(n), branchedTreeFeatureConfig, set);
		}
	}

	@Override
	public int getRadius(Random random, int i, int j, BranchedTreeFeatureConfig branchedTreeFeatureConfig) {
		return this.radius + random.nextInt(this.randomRadius + 1);
	}

	@Override
	protected boolean method_23451(Random random, int i, int j, int k, int l, int m) {
		return Math.abs(j) == m && Math.abs(l) == m && m > 0;
	}

	@Override
	public int method_23447(int i, int j, int k, int l) {
		return l == 0 ? 0 : 2;
	}
}
