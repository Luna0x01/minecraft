package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.chunk.ChunkStorage;
import net.minecraft.world.level.storage.WorldSaveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4452 extends class_3452<ChunkPos, class_3786, ChunkBlockStateStorage> {
	private static final Logger field_21867 = LogManager.getLogger();
	private final World field_21868;
	private final ChunkGenerator<?> field_21869;
	private final ChunkStorage field_21870;
	private final ThreadExecutor field_21871;
	private final Long2ObjectMap<class_3452<ChunkPos, class_3786, ChunkBlockStateStorage>.a> field_21872 = new class_3594<class_3452<ChunkPos, class_3786, ChunkBlockStateStorage>.a>(
		8192, 5000
	) {
		protected boolean method_16293(class_3452<ChunkPos, class_3786, ChunkBlockStateStorage>.a arg) {
			ChunkBlockStateStorage chunkBlockStateStorage = (ChunkBlockStateStorage)arg.method_15499();
			return !chunkBlockStateStorage.method_17120() && !chunkBlockStateStorage.method_17133();
		}
	};

	public class_4452(int i, World world, ChunkGenerator<?> chunkGenerator, ChunkStorage chunkStorage, ThreadExecutor threadExecutor) {
		super("WorldGen", i, class_3786.FINALIZED, () -> new EnumMap(class_3786.class), () -> new EnumMap(class_3786.class));
		this.field_21868 = world;
		this.field_21869 = chunkGenerator;
		this.field_21870 = chunkStorage;
		this.field_21871 = threadExecutor;
	}

	@Nullable
	protected class_3452<ChunkPos, class_3786, ChunkBlockStateStorage>.a method_15486(ChunkPos chunkPos, boolean bl) {
		synchronized (this.field_21870) {
			return bl ? (class_3452.class_3453)this.field_21872.computeIfAbsent(chunkPos.method_16281(), l -> {
				ChunkBlockStateStorage chunkBlockStateStorage;
				try {
					chunkBlockStateStorage = this.field_21870.method_17187(this.field_21868, chunkPos.x, chunkPos.z, arg -> {
					});
				} catch (CrashException var6) {
					throw var6;
				} catch (Exception var7) {
					field_21867.error("Couldn't load protochunk", var7);
					chunkBlockStateStorage = null;
				}

				if (chunkBlockStateStorage != null) {
					chunkBlockStateStorage.method_9143(this.field_21868.getLastUpdateTime());
					return new class_3452.class_3453(this, chunkPos, chunkBlockStateStorage, chunkBlockStateStorage.method_17009());
				} else {
					return new class_3452.class_3453(this, chunkPos, new ChunkBlockStateStorage(chunkPos, class_3790.field_18935), class_3786.EMPTY);
				}
			}) : (class_3452.class_3453)this.field_21872.get(chunkPos.method_16281());
		}
	}

	protected ChunkBlockStateStorage method_15484(ChunkPos chunkPos, class_3786 arg, Map<ChunkPos, ChunkBlockStateStorage> map) {
		return arg.method_17048(this.field_21868, this.field_21869, map, chunkPos.x, chunkPos.z);
	}

	protected class_3452<ChunkPos, class_3786, ChunkBlockStateStorage>.a method_15483(
		ChunkPos chunkPos, class_3452<ChunkPos, class_3786, ChunkBlockStateStorage>.a arg
	) {
		((ChunkBlockStateStorage)arg.method_15499()).method_17109(1);
		return arg;
	}

	protected void method_15494(ChunkPos chunkPos, class_3452<ChunkPos, class_3786, ChunkBlockStateStorage>.a arg) {
		((ChunkBlockStateStorage)arg.method_15499()).method_17109(-1);
	}

	public void method_21308(BooleanSupplier booleanSupplier) {
		synchronized (this.field_21870) {
			ObjectIterator var3 = this.field_21872.values().iterator();

			while (var3.hasNext()) {
				class_3452<ChunkPos, class_3786, ChunkBlockStateStorage>.class_3453 lv = (class_3452.class_3453)var3.next();
				ChunkBlockStateStorage chunkBlockStateStorage = (ChunkBlockStateStorage)lv.method_15499();
				if (chunkBlockStateStorage.method_17133() && chunkBlockStateStorage.method_17009().method_17054() == class_3786.class_3787.PROTOCHUNK) {
					try {
						chunkBlockStateStorage.method_9143(this.field_21868.getLastUpdateTime());
						this.field_21870.method_17185(this.field_21868, chunkBlockStateStorage);
						chunkBlockStateStorage.method_17117(false);
					} catch (IOException var8) {
						field_21867.error("Couldn't save chunk", var8);
					} catch (WorldSaveException var9) {
						field_21867.error("Couldn't save chunk; already in use by another instance of Minecraft?", var9);
					}
				}

				if (!booleanSupplier.getAsBoolean()) {
					return;
				}
			}
		}
	}
}
