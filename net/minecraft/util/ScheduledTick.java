package net.minecraft.util;

import net.minecraft.class_3605;
import net.minecraft.util.math.BlockPos;

public class ScheduledTick<T> implements Comparable<ScheduledTick<T>> {
	private static long entries;
	private final T field_17521;
	public final BlockPos pos;
	public final long time;
	public final class_3605 field_17520;
	private final long id;

	public ScheduledTick(BlockPos blockPos, T object) {
		this(blockPos, object, 0L, class_3605.NORMAL);
	}

	public ScheduledTick(BlockPos blockPos, T object, long l, class_3605 arg) {
		this.id = entries++;
		this.pos = blockPos.toImmutable();
		this.field_17521 = object;
		this.time = l;
		this.field_17520 = arg;
	}

	public boolean equals(Object object) {
		if (!(object instanceof ScheduledTick)) {
			return false;
		} else {
			ScheduledTick scheduledTick = (ScheduledTick)object;
			return this.pos.equals(scheduledTick.pos) && this.field_17521 == scheduledTick.field_17521;
		}
	}

	public int hashCode() {
		return this.pos.hashCode();
	}

	public int compareTo(ScheduledTick scheduledTick) {
		if (this.time < scheduledTick.time) {
			return -1;
		} else if (this.time > scheduledTick.time) {
			return 1;
		} else if (this.field_17520.ordinal() < scheduledTick.field_17520.ordinal()) {
			return -1;
		} else if (this.field_17520.ordinal() > scheduledTick.field_17520.ordinal()) {
			return 1;
		} else if (this.id < scheduledTick.id) {
			return -1;
		} else {
			return this.id > scheduledTick.id ? 1 : 0;
		}
	}

	public String toString() {
		return this.field_17521 + ": " + this.pos + ", " + this.time + ", " + this.field_17520 + ", " + this.id;
	}

	public T method_16421() {
		return this.field_17521;
	}
}
