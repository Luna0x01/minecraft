package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;

public enum class_3786 implements class_3455<ChunkPos, class_3786> {
	EMPTY("empty", null, -1, false, class_3786.class_3787.PROTOCHUNK),
	BASE("base", new class_4443(), 0, false, class_3786.class_3787.PROTOCHUNK),
	CARVED("carved", new class_4444(), 0, false, class_3786.class_3787.PROTOCHUNK),
	LIQUID_CARVED("liquid_carved", new class_4450(), 1, false, class_3786.class_3787.PROTOCHUNK),
	DECORATED("decorated", new class_4446(), 1, true, class_3786.class_3787.PROTOCHUNK) {
		@Override
		public void method_15513(ChunkPos chunkPos, BiConsumer<ChunkPos, class_3786> biConsumer) {
			int i = chunkPos.x;
			int j = chunkPos.z;
			class_3786 lv = this.method_15512();
			int k = 8;

			for (int l = i - 8; l <= i + 8; l++) {
				if (l < i - 1 || l > i + 1) {
					for (int m = j - 8; m <= j + 8; m++) {
						if (m < j - 1 || m > j + 1) {
							ChunkPos chunkPos2 = new ChunkPos(l, m);
							biConsumer.accept(chunkPos2, EMPTY);
						}
					}
				}
			}

			for (int n = i - 1; n <= i + 1; n++) {
				for (int o = j - 1; o <= j + 1; o++) {
					ChunkPos chunkPos3 = new ChunkPos(n, o);
					biConsumer.accept(chunkPos3, lv);
				}
			}
		}
	},
	LIGHTED("lighted", new class_4449(), 1, true, class_3786.class_3787.PROTOCHUNK),
	MOBS_SPAWNED("mobs_spawned", new class_4451(), 0, true, class_3786.class_3787.PROTOCHUNK),
	FINALIZED("finalized", new class_4448(), 0, true, class_3786.class_3787.PROTOCHUNK),
	FULLCHUNK("fullchunk", new class_4447(), 0, true, class_3786.class_3787.LEVELCHUNK),
	POSTPROCESSED("postprocessed", new class_4447(), 0, true, class_3786.class_3787.LEVELCHUNK);

	private static final Map<String, class_3786> field_18866 = Util.make(Maps.newHashMap(), hashMap -> {
		for (class_3786 lv : values()) {
			hashMap.put(lv.method_17052(), lv);
		}
	});
	private final String field_18867;
	@Nullable
	private final class_4445 field_18868;
	private final int field_18869;
	private final class_3786.class_3787 field_18870;
	private final boolean field_18871;

	private class_3786(String string2, class_4445 arg, int j, @Nullable boolean bl, class_3786.class_3787 arg2) {
		this.field_18867 = string2;
		this.field_18868 = arg;
		this.field_18869 = j;
		this.field_18870 = arg2;
		this.field_18871 = bl;
	}

	public String method_17052() {
		return this.field_18867;
	}

	public ChunkBlockStateStorage method_17048(World world, ChunkGenerator<?> chunkGenerator, Map<ChunkPos, ChunkBlockStateStorage> map, int i, int j) {
		return this.field_18868.method_21301(this, world, chunkGenerator, map, i, j);
	}

	public void method_15513(ChunkPos chunkPos, BiConsumer<ChunkPos, class_3786> biConsumer) {
		int i = chunkPos.x;
		int j = chunkPos.z;
		class_3786 lv = this.method_15512();

		for (int k = i - this.field_18869; k <= i + this.field_18869; k++) {
			for (int l = j - this.field_18869; l <= j + this.field_18869; l++) {
				biConsumer.accept(new ChunkPos(k, l), lv);
			}
		}
	}

	public int method_17053() {
		return this.field_18869;
	}

	public class_3786.class_3787 method_17054() {
		return this.field_18870;
	}

	@Nullable
	public static class_3786 method_17050(String string) {
		return (class_3786)field_18866.get(string);
	}

	@Nullable
	public class_3786 method_15512() {
		return this.ordinal() == 0 ? null : values()[this.ordinal() - 1];
	}

	public boolean method_17056() {
		return this.field_18871;
	}

	public boolean method_17049(class_3786 arg) {
		return this.ordinal() >= arg.ordinal();
	}

	public static enum class_3787 {
		PROTOCHUNK,
		LEVELCHUNK;
	}
}
