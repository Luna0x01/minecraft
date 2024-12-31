package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public interface TickScheduler<T> {
	boolean isScheduled(BlockPos blockPos, T object);

	default void schedule(BlockPos blockPos, T object, int i) {
		this.schedule(blockPos, object, i, TickPriority.field_9314);
	}

	void schedule(BlockPos blockPos, T object, int i, TickPriority tickPriority);

	boolean isTicking(BlockPos blockPos, T object);

	void scheduleAll(Stream<ScheduledTick<T>> stream);
}
