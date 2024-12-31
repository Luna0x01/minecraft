package net.minecraft.util.profiler;

import java.util.function.Supplier;

public interface ReadableProfiler extends Profiler {
	@Override
	void push(String string);

	@Override
	void push(Supplier<String> supplier);

	@Override
	void pop();

	@Override
	void swap(String string);

	@Override
	void swap(Supplier<String> supplier);

	ProfileResult getResults();
}
