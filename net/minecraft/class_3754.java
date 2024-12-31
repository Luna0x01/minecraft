package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.GiantSpruceTreeFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;

public class class_3754 extends class_3747 {
	@Nullable
	@Override
	protected FoliageFeature<class_3871> method_16850(Random random) {
		return new SpruceTreeFeature(true);
	}

	@Nullable
	@Override
	protected FoliageFeature<class_3871> method_16848(Random random) {
		return new GiantSpruceTreeFeature(false, random.nextBoolean());
	}
}
