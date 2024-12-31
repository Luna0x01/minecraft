package net.minecraft.world.gen.decorator;

import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.UniformIntDistribution;

public interface Decoratable<R> {
	R decorate(ConfiguredDecorator<?> decorator);

	default R applyChance(int chance) {
		return this.decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(chance)));
	}

	default R repeat(UniformIntDistribution count) {
		return this.decorate(Decorator.COUNT.configure(new CountConfig(count)));
	}

	default R repeat(int count) {
		return this.repeat(UniformIntDistribution.of(count));
	}

	default R repeatRandomly(int maxCount) {
		return this.repeat(UniformIntDistribution.of(0, maxCount));
	}

	default R rangeOf(int max) {
		return this.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(0, 0, max)));
	}

	default R spreadHorizontally() {
		return this.decorate(Decorator.SQUARE.configure(NopeDecoratorConfig.INSTANCE));
	}
}
