package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.FoliageFeature;

public class class_3751 extends class_3747 {
	@Nullable
	@Override
	protected FoliageFeature<class_3871> method_16850(Random random) {
		return null;
	}

	@Nullable
	@Override
	protected FoliageFeature<class_3871> method_16848(Random random) {
		return new DarkOakTreeFeature(true);
	}
}
