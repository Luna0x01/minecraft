package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.ScheduledTick;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

public class class_3603<T> implements class_3604<T> {
	protected final Predicate<T> field_17512;
	protected final Function<T, Identifier> field_17513;
	protected final Function<Identifier, T> field_17514;
	protected final Set<ScheduledTick<T>> field_17515 = Sets.newHashSet();
	protected final TreeSet<ScheduledTick<T>> field_17516 = new TreeSet();
	private final ServerWorld field_17517;
	private final List<ScheduledTick<T>> field_17518 = Lists.newArrayList();
	private final Consumer<ScheduledTick<T>> field_17519;

	public class_3603(
		ServerWorld serverWorld, Predicate<T> predicate, Function<T, Identifier> function, Function<Identifier, T> function2, Consumer<ScheduledTick<T>> consumer
	) {
		this.field_17512 = predicate;
		this.field_17513 = function;
		this.field_17514 = function2;
		this.field_17517 = serverWorld;
		this.field_17519 = consumer;
	}

	public void method_16409() {
		int i = this.field_17516.size();
		if (i != this.field_17515.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {
			if (i > 65536) {
				i = 65536;
			}

			this.field_17517.profiler.push("cleaning");

			for (int j = 0; j < i; j++) {
				ScheduledTick<T> scheduledTick = (ScheduledTick<T>)this.field_17516.first();
				if (scheduledTick.time > this.field_17517.getLastUpdateTime()) {
					break;
				}

				this.field_17516.remove(scheduledTick);
				this.field_17515.remove(scheduledTick);
				this.field_17518.add(scheduledTick);
			}

			this.field_17517.profiler.pop();
			this.field_17517.profiler.push("ticking");
			Iterator<ScheduledTick<T>> iterator = this.field_17518.iterator();

			while (iterator.hasNext()) {
				ScheduledTick<T> scheduledTick2 = (ScheduledTick<T>)iterator.next();
				iterator.remove();
				int k = 0;
				if (this.field_17517.method_16385(scheduledTick2.pos.add(0, 0, 0), scheduledTick2.pos.add(0, 0, 0))) {
					try {
						this.field_17519.accept(scheduledTick2);
					} catch (Throwable var8) {
						CrashReport crashReport = CrashReport.create(var8, "Exception while ticking");
						CrashReportSection crashReportSection = crashReport.addElement("Block being ticked");
						CrashReportSection.addBlockInfo(crashReportSection, scheduledTick2.pos, null);
						throw new CrashException(crashReport);
					}
				} else {
					this.schedule(scheduledTick2.pos, scheduledTick2.method_16421(), 0);
				}
			}

			this.field_17517.profiler.pop();
			this.field_17518.clear();
		}
	}

	@Override
	public boolean method_16420(BlockPos blockPos, T object) {
		return this.field_17518.contains(new ScheduledTick(blockPos, object));
	}

	public List<ScheduledTick<T>> method_16411(Chunk chunk, boolean bl) {
		ChunkPos chunkPos = chunk.method_3920();
		int i = (chunkPos.x << 4) - 2;
		int j = i + 16 + 2;
		int k = (chunkPos.z << 4) - 2;
		int l = k + 16 + 2;
		return this.method_16413(new BlockBox(i, 0, k, j, 256, l), bl);
	}

	public List<ScheduledTick<T>> method_16413(BlockBox blockBox, boolean bl) {
		List<ScheduledTick<T>> list = null;

		for (int i = 0; i < 2; i++) {
			Iterator<ScheduledTick<T>> iterator;
			if (i == 0) {
				iterator = this.field_17516.iterator();
			} else {
				iterator = this.field_17518.iterator();
			}

			while (iterator.hasNext()) {
				ScheduledTick<T> scheduledTick = (ScheduledTick<T>)iterator.next();
				BlockPos blockPos = scheduledTick.pos;
				if (blockPos.getX() >= blockBox.minX && blockPos.getX() < blockBox.maxX && blockPos.getZ() >= blockBox.minZ && blockPos.getZ() < blockBox.maxZ) {
					if (bl) {
						if (i == 0) {
							this.field_17515.remove(scheduledTick);
						}

						iterator.remove();
					}

					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(scheduledTick);
				}
			}
		}

		return list == null ? Collections.emptyList() : list;
	}

	public void method_16412(BlockBox blockBox, BlockPos blockPos) {
		for (ScheduledTick<T> scheduledTick : this.method_16413(blockBox, false)) {
			if (blockBox.contains(scheduledTick.pos)) {
				BlockPos blockPos2 = scheduledTick.pos.add(blockPos);
				this.method_16415(blockPos2, scheduledTick.method_16421(), (int)(scheduledTick.time - this.field_17517.method_3588().getTime()), scheduledTick.field_17520);
			}
		}
	}

	public NbtList method_16410(Chunk chunk) {
		List<ScheduledTick<T>> list = this.method_16411(chunk, false);
		long l = this.field_17517.getLastUpdateTime();
		NbtList nbtList = new NbtList();

		for (ScheduledTick<T> scheduledTick : list) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putString("i", ((Identifier)this.field_17513.apply(scheduledTick.method_16421())).toString());
			nbtCompound.putInt("x", scheduledTick.pos.getX());
			nbtCompound.putInt("y", scheduledTick.pos.getY());
			nbtCompound.putInt("z", scheduledTick.pos.getZ());
			nbtCompound.putInt("t", (int)(scheduledTick.time - l));
			nbtCompound.putInt("p", scheduledTick.field_17520.method_16422());
			nbtList.add((NbtElement)nbtCompound);
		}

		return nbtList;
	}

	public void method_16414(NbtList nbtList) {
		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			T object = (T)this.field_17514.apply(new Identifier(nbtCompound.getString("i")));
			if (object != null) {
				this.method_16415(
					new BlockPos(nbtCompound.getInt("x"), nbtCompound.getInt("y"), nbtCompound.getInt("z")),
					object,
					nbtCompound.getInt("t"),
					class_3605.method_16423(nbtCompound.getInt("p"))
				);
			}
		}
	}

	@Override
	public boolean method_16417(BlockPos blockPos, T object) {
		return this.field_17515.contains(new ScheduledTick(blockPos, object));
	}

	@Override
	public void method_16419(BlockPos blockPos, T object, int i, class_3605 arg) {
		if (!this.field_17512.test(object)) {
			if (this.field_17517.method_16359(blockPos)) {
				this.method_16416(blockPos, object, i, arg);
			}
		}
	}

	protected void method_16415(BlockPos blockPos, T object, int i, class_3605 arg) {
		if (!this.field_17512.test(object)) {
			this.method_16416(blockPos, object, i, arg);
		}
	}

	private void method_16416(BlockPos blockPos, T object, int i, class_3605 arg) {
		ScheduledTick<T> scheduledTick = new ScheduledTick<>(blockPos, object, (long)i + this.field_17517.getLastUpdateTime(), arg);
		if (!this.field_17515.contains(scheduledTick)) {
			this.field_17515.add(scheduledTick);
			this.field_17516.add(scheduledTick);
		}
	}
}
