package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.chunk.ThreadedAnvilChunkStorage;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorageAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3457 {
	private static final Logger field_16655 = LogManager.getLogger();
	private static final ThreadFactory field_16656 = new ThreadFactoryBuilder().setDaemon(true).build();
	private final String field_16657;
	private final SaveHandler field_16658;
	private final class_4070 field_16659;
	private final Thread field_16660;
	private volatile boolean field_16661 = true;
	private volatile boolean field_16662 = false;
	private volatile float field_16663;
	private volatile int field_16664;
	private volatile int field_16665 = 0;
	private volatile int field_16666 = 0;
	private final Object2FloatMap<DimensionType> field_16667 = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap(Util.method_20233()));
	private volatile Text field_16668 = new TranslatableText("optimizeWorld.stage.counting");

	public class_3457(String string, LevelStorageAccess levelStorageAccess, LevelProperties levelProperties) {
		this.field_16657 = levelProperties.getLevelName();
		this.field_16658 = levelStorageAccess.method_250(string, null);
		this.field_16658.saveWorld(levelProperties);
		this.field_16659 = new class_4070(this.field_16658);
		this.field_16660 = field_16656.newThread(this::method_15531);
		this.field_16660.setUncaughtExceptionHandler(this::method_15523);
		this.field_16660.start();
	}

	private void method_15523(Thread thread, Throwable throwable) {
		field_16655.error("Error upgrading world", throwable);
		this.field_16661 = false;
		this.field_16668 = new TranslatableText("optimizeWorld.stage.failed");
	}

	public void method_15520() {
		this.field_16661 = false;

		try {
			this.field_16660.join();
		} catch (InterruptedException var2) {
		}
	}

	private void method_15531() {
		File file = this.field_16658.getWorldFolder();
		class_3456 lv = new class_3456(file);
		Builder<DimensionType, ThreadedAnvilChunkStorage> builder = ImmutableMap.builder();

		for (DimensionType dimensionType : DimensionType.method_17200()) {
			builder.put(dimensionType, new ThreadedAnvilChunkStorage(dimensionType.method_17197(file), this.field_16658.method_17967()));
		}

		Map<DimensionType, ThreadedAnvilChunkStorage> map = builder.build();
		long l = Util.method_20227();
		this.field_16664 = 0;
		Builder<DimensionType, ListIterator<ChunkPos>> builder2 = ImmutableMap.builder();

		for (DimensionType dimensionType2 : DimensionType.method_17200()) {
			List<ChunkPos> list = lv.method_15514(dimensionType2);
			builder2.put(dimensionType2, list.listIterator());
			this.field_16664 = this.field_16664 + list.size();
		}

		ImmutableMap<DimensionType, ListIterator<ChunkPos>> immutableMap = builder2.build();
		float f = (float)this.field_16664;
		this.field_16668 = new TranslatableText("optimizeWorld.stage.structures");

		for (Entry<DimensionType, ThreadedAnvilChunkStorage> entry : map.entrySet()) {
			((ThreadedAnvilChunkStorage)entry.getValue()).method_17173((DimensionType)entry.getKey(), this.field_16659);
		}

		this.field_16659.method_17975();
		this.field_16668 = new TranslatableText("optimizeWorld.stage.upgrading");
		if (f <= 0.0F) {
			for (DimensionType dimensionType3 : DimensionType.method_17200()) {
				this.field_16667.put(dimensionType3, 1.0F / (float)map.size());
			}
		}

		while (this.field_16661) {
			boolean bl = false;
			float g = 0.0F;

			for (DimensionType dimensionType4 : DimensionType.method_17200()) {
				ListIterator<ChunkPos> listIterator = (ListIterator<ChunkPos>)immutableMap.get(dimensionType4);
				bl |= this.method_15521((ThreadedAnvilChunkStorage)map.get(dimensionType4), listIterator, dimensionType4);
				if (f > 0.0F) {
					float h = (float)listIterator.nextIndex() / f;
					this.field_16667.put(dimensionType4, h);
					g += h;
				}
			}

			this.field_16663 = g;
			if (!bl) {
				this.field_16661 = false;
			}
		}

		this.field_16668 = new TranslatableText("optimizeWorld.stage.finished");
		l = Util.method_20227() - l;
		field_16655.info("World optimizaton finished after {} ms", l);
		map.values().forEach(ThreadedAnvilChunkStorage::save);
		this.field_16659.method_17975();
		this.field_16658.clear();
		this.field_16662 = true;
	}

	private boolean method_15521(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, ListIterator<ChunkPos> listIterator, DimensionType dimensionType) {
		if (listIterator.hasNext()) {
			boolean bl;
			synchronized (threadedAnvilChunkStorage) {
				bl = threadedAnvilChunkStorage.method_17165((ChunkPos)listIterator.next(), dimensionType, this.field_16659);
			}

			if (bl) {
				this.field_16665++;
			} else {
				this.field_16666++;
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean method_15524() {
		return this.field_16662;
	}

	public float method_15522(DimensionType dimensionType) {
		return this.field_16667.getFloat(dimensionType);
	}

	public float method_15525() {
		return this.field_16663;
	}

	public int method_15526() {
		return this.field_16664;
	}

	public int method_15527() {
		return this.field_16665;
	}

	public int method_15528() {
		return this.field_16666;
	}

	public Text method_15529() {
		return this.field_16668;
	}

	public String method_15530() {
		return this.field_16657;
	}
}
