package net.minecraft.util.profiler;

import java.util.function.Supplier;

public interface Profiler {
	void startTick();

	void endTick();

	void push(String string);

	void push(Supplier<String> supplier);

	void pop();

	void swap(String string);

	void swap(Supplier<String> supplier);

	void visit(String string);

	void visit(Supplier<String> supplier);
}
