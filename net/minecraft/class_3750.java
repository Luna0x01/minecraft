package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.BirchTreeFeature;
import net.minecraft.world.gen.feature.FoliageFeature;

public class class_3750 extends class_3748 {
	@Nullable
	@Override
	protected FoliageFeature<class_3871> method_16850(Random random) {
		return new BirchTreeFeature(true, false);
	}
}
