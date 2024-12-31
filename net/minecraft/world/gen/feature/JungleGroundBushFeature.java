package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;

public class JungleGroundBushFeature extends AbstractTreeFeature<TreeFeatureConfig> {
	public JungleGroundBushFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> function) {
		super(function);
	}

	@Override
	public boolean generate(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		Set<BlockPos> set,
		Set<BlockPos> set2,
		BlockBox blockBox,
		TreeFeatureConfig treeFeatureConfig
	) {
		blockPos = modifiableTestableWorld.getTopPosition(Heightmap.Type.field_13203, blockPos).down();
		if (isNaturalDirtOrGrass(modifiableTestableWorld, blockPos)) {
			blockPos = blockPos.up();
			this.setLogBlockState(modifiableTestableWorld, random, blockPos, set, blockBox, treeFeatureConfig);

			for (int i = 0; i <= 2; i++) {
				int j = 2 - i;

				for (int k = -j; k <= j; k++) {
					for (int l = -j; l <= j; l++) {
						if (Math.abs(k) != j || Math.abs(l) != j || random.nextInt(2) != 0) {
							this.setLeavesBlockState(
								modifiableTestableWorld, random, new BlockPos(k + blockPos.getX(), i + blockPos.getY(), l + blockPos.getZ()), set2, blockBox, treeFeatureConfig
							);
						}
					}
				}
			}
		}

		return true;
	}
}
