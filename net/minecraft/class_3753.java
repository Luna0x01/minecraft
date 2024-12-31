package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.BigTreeFeature;
import net.minecraft.world.gen.feature.FoliageFeature;

public class class_3753 extends class_3748 {
	@Nullable
	@Override
	protected FoliageFeature<class_3871> method_16850(Random random) {
		return (FoliageFeature<class_3871>)(random.nextInt(10) == 0 ? new BigTreeFeature(true) : new class_3910(true));
	}
}
