package net.minecraft.client.option;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.text.Text;

public class LogarithmicOption extends DoubleOption {
	public LogarithmicOption(
		String string,
		double d,
		double e,
		float f,
		Function<GameOptions, Double> function,
		BiConsumer<GameOptions, Double> biConsumer,
		BiFunction<GameOptions, DoubleOption, Text> biFunction
	) {
		super(string, d, e, f, function, biConsumer, biFunction);
	}

	@Override
	public double getRatio(double value) {
		return Math.log(value / this.min) / Math.log(this.max / this.min);
	}

	@Override
	public double getValue(double ratio) {
		return this.min * Math.pow(Math.E, Math.log(this.max / this.min) * ratio);
	}
}
