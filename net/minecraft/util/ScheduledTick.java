package net.minecraft.util;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class ScheduledTick implements Comparable<ScheduledTick> {
	private static long entries;
	private final Block block;
	public final BlockPos pos;
	public long time;
	public int priority;
	private final long id;

	public ScheduledTick(BlockPos blockPos, Block block) {
		this.id = entries++;
		this.pos = blockPos.toImmutable();
		this.block = block;
	}

	public boolean equals(Object object) {
		if (!(object instanceof ScheduledTick)) {
			return false;
		} else {
			ScheduledTick scheduledTick = (ScheduledTick)object;
			return this.pos.equals(scheduledTick.pos) && Block.areBlocksEqual(this.block, scheduledTick.block);
		}
	}

	public int hashCode() {
		return this.pos.hashCode();
	}

	public ScheduledTick setTime(long time) {
		this.time = time;
		return this;
	}

	public void setPriority(int position) {
		this.priority = position;
	}

	public int compareTo(ScheduledTick scheduledTick) {
		if (this.time < scheduledTick.time) {
			return -1;
		} else if (this.time > scheduledTick.time) {
			return 1;
		} else if (this.priority != scheduledTick.priority) {
			return this.priority - scheduledTick.priority;
		} else if (this.id < scheduledTick.id) {
			return -1;
		} else {
			return this.id > scheduledTick.id ? 1 : 0;
		}
	}

	public String toString() {
		return Block.getIdByBlock(this.block) + ": " + this.pos + ", " + this.time + ", " + this.priority + ", " + this.id;
	}

	public Block getBlock() {
		return this.block;
	}
}
