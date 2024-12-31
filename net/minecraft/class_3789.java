package net.minecraft;

import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.chunk.ThreadedAnvilChunkStorage;

public class class_3789<T> implements class_3604<T> {
	protected final Predicate<T> field_18930;
	protected final Function<T, Identifier> field_18931;
	protected final Function<Identifier, T> field_18932;
	private final ChunkPos field_18933;
	private final ShortList[] field_18934 = new ShortList[16];

	public class_3789(Predicate<T> predicate, Function<T, Identifier> function, Function<Identifier, T> function2, ChunkPos chunkPos) {
		this.field_18930 = predicate;
		this.field_18931 = function;
		this.field_18932 = function2;
		this.field_18933 = chunkPos;
	}

	public NbtList method_17148() {
		return ThreadedAnvilChunkStorage.method_17180(this.field_18934);
	}

	public void method_17150(NbtList nbtList) {
		for (int i = 0; i < nbtList.size(); i++) {
			NbtList nbtList2 = nbtList.getList(i);

			for (int j = 0; j < nbtList2.size(); j++) {
				ChunkBlockStateStorage.method_17119(this.field_18934, i).add(nbtList2.getShort(j));
			}
		}
	}

	public void method_17149(class_3604<T> arg, Function<BlockPos, T> function) {
		for (int i = 0; i < this.field_18934.length; i++) {
			if (this.field_18934[i] != null) {
				ShortListIterator var4 = this.field_18934[i].iterator();

				while (var4.hasNext()) {
					Short short_ = (Short)var4.next();
					BlockPos blockPos = ChunkBlockStateStorage.method_17116(short_, i, this.field_18933);
					arg.schedule(blockPos, (T)function.apply(blockPos), 0);
				}

				this.field_18934[i].clear();
			}
		}
	}

	@Override
	public boolean method_16417(BlockPos blockPos, T object) {
		return false;
	}

	@Override
	public void method_16419(BlockPos blockPos, T object, int i, class_3605 arg) {
		ChunkBlockStateStorage.method_17119(this.field_18934, blockPos.getY() >> 4).add(ChunkBlockStateStorage.method_17135(blockPos));
	}

	@Override
	public boolean method_16420(BlockPos blockPos, T object) {
		return false;
	}
}
