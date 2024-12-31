package net.minecraft.util.profiler;

import java.util.function.Supplier;

public class DummyProfiler implements ReadableProfiler {
	public static final DummyProfiler INSTANCE = new DummyProfiler();

	private DummyProfiler() {
	}

	@Override
	public void startTick() {
	}

	@Override
	public void endTick() {
	}

	@Override
	public void push(String string) {
	}

	@Override
	public void push(Supplier<String> supplier) {
	}

	@Override
	public void pop() {
	}

	@Override
	public void swap(String string) {
	}

	@Override
	public void swap(Supplier<String> supplier) {
	}

	@Override
	public void visit(String string) {
	}

	@Override
	public void visit(Supplier<String> supplier) {
	}

	@Override
	public ProfileResult getResult() {
		return EmptyProfileResult.INSTANCE;
	}
}
