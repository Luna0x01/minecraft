package net.minecraft.world.gen.decorator;

import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public interface Decoratable<R> {
	R decorate(ConfiguredDecorator<?> decorator);

	default R applyChance(int chance) {
		return this.decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(chance)));
	}

	default R repeat(IntProvider count) {
		return this.decorate(Decorator.COUNT.configure(new CountConfig(count)));
	}

	default R repeat(int count) {
		return this.repeat(ConstantIntProvider.create(count));
	}

	default R repeatRandomly(int maxCount) {
		return this.repeat(UniformIntProvider.create(0, maxCount));
	}

	default R uniformRange(YOffset min, YOffset max) {
		return this.range(new RangeDecoratorConfig(UniformHeightProvider.create(min, max)));
	}

	default R triangleRange(YOffset min, YOffset max) {
		return this.range(new RangeDecoratorConfig(TrapezoidHeightProvider.create(min, max)));
	}

	default R range(RangeDecoratorConfig config) {
		return this.decorate(Decorator.RANGE.configure(config));
	}

	default R spreadHorizontally() {
		return this.decorate(Decorator.SQUARE.configure(NopeDecoratorConfig.INSTANCE));
	}
}
