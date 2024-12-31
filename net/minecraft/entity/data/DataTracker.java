package net.minecraft.entity.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataTracker {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Class<? extends Entity>, Integer> field_13832 = Maps.newHashMap();
	private final Entity entity;
	private final Map<Integer, DataTracker.DataEntry<?>> field_13833 = Maps.newHashMap();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean empty = true;
	private boolean dirty;

	public DataTracker(Entity entity) {
		this.entity = entity;
	}

	public static <T> TrackedData<T> registerData(Class<? extends Entity> class_, TrackedDataHandler<T> trackedDataHandler) {
		if (LOGGER.isDebugEnabled()) {
			try {
				Class<?> class2 = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
				if (!class2.equals(class_)) {
					LOGGER.debug("defineId called for: {} from {}", new Object[]{class_, class2, new RuntimeException()});
				}
			} catch (ClassNotFoundException var5) {
			}
		}

		int i;
		if (field_13832.containsKey(class_)) {
			i = (Integer)field_13832.get(class_) + 1;
		} else {
			int j = 0;
			Class<?> class3 = class_;

			while (class3 != Entity.class) {
				class3 = class3.getSuperclass();
				if (field_13832.containsKey(class3)) {
					j = (Integer)field_13832.get(class3) + 1;
					break;
				}
			}

			i = j;
		}

		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
		} else {
			field_13832.put(class_, i);
			return trackedDataHandler.create(i);
		}
	}

	public <T> void startTracking(TrackedData<T> trackedData, T object) {
		int i = trackedData.method_12713();
		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
		} else if (this.field_13833.containsKey(i)) {
			throw new IllegalArgumentException("Duplicate id value for " + i + "!");
		} else if (TrackedDataHandlerRegistry.method_12720(trackedData.method_12714()) < 0) {
			throw new IllegalArgumentException("Unregistered serializer " + trackedData.method_12714() + " for " + i + "!");
		} else {
			this.method_12757(trackedData, object);
		}
	}

	private <T> void method_12757(TrackedData<T> trackedData, T object) {
		DataTracker.DataEntry<T> dataEntry = new DataTracker.DataEntry<>(trackedData, object);
		this.lock.writeLock().lock();
		this.field_13833.put(trackedData.method_12713(), dataEntry);
		this.empty = false;
		this.lock.writeLock().unlock();
	}

	private <T> DataTracker.DataEntry<T> method_12756(TrackedData<T> trackedData) {
		this.lock.readLock().lock();

		DataTracker.DataEntry<T> dataEntry;
		try {
			dataEntry = (DataTracker.DataEntry<T>)this.field_13833.get(trackedData.method_12713());
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Getting synched entity data");
			CrashReportSection crashReportSection = crashReport.addElement("Synched entity data");
			crashReportSection.add("Data ID", trackedData);
			throw new CrashException(crashReport);
		}

		this.lock.readLock().unlock();
		return dataEntry;
	}

	public <T> T get(TrackedData<T> trackedData) {
		return this.method_12756(trackedData).getValue();
	}

	public <T> void set(TrackedData<T> trackedData, T object) {
		DataTracker.DataEntry<T> dataEntry = this.method_12756(trackedData);
		if (ObjectUtils.notEqual(object, dataEntry.getValue())) {
			dataEntry.setValue(object);
			this.entity.onTrackedDataSet(trackedData);
			dataEntry.setModified(true);
			this.dirty = true;
		}
	}

	public <T> void method_12754(TrackedData<T> trackedData) {
		this.method_12756(trackedData).modified = true;
		this.dirty = true;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public static void method_12749(List<DataTracker.DataEntry<?>> list, PacketByteBuf packetByteBuf) throws IOException {
		if (list != null) {
			int i = 0;

			for (int j = list.size(); i < j; i++) {
				DataTracker.DataEntry<?> dataEntry = (DataTracker.DataEntry<?>)list.get(i);
				method_12747(packetByteBuf, dataEntry);
			}
		}

		packetByteBuf.writeByte(255);
	}

	@Nullable
	public List<DataTracker.DataEntry<?>> getChangedEntries() {
		List<DataTracker.DataEntry<?>> list = null;
		if (this.dirty) {
			this.lock.readLock().lock();

			for (DataTracker.DataEntry<?> dataEntry : this.field_13833.values()) {
				if (dataEntry.isModified()) {
					dataEntry.setModified(false);
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(dataEntry);
				}
			}

			this.lock.readLock().unlock();
		}

		this.dirty = false;
		return list;
	}

	public void write(PacketByteBuf packet) throws IOException {
		this.lock.readLock().lock();

		for (DataTracker.DataEntry<?> dataEntry : this.field_13833.values()) {
			method_12747(packet, dataEntry);
		}

		this.lock.readLock().unlock();
		packet.writeByte(255);
	}

	@Nullable
	public List<DataTracker.DataEntry<?>> getEntries() {
		List<DataTracker.DataEntry<?>> list = null;
		this.lock.readLock().lock();

		for (DataTracker.DataEntry<?> dataEntry : this.field_13833.values()) {
			if (list == null) {
				list = Lists.newArrayList();
			}

			list.add(dataEntry);
		}

		this.lock.readLock().unlock();
		return list;
	}

	private static <T> void method_12747(PacketByteBuf packetByteBuf, DataTracker.DataEntry<T> dataEntry) throws IOException {
		TrackedData<T> trackedData = dataEntry.method_12758();
		int i = TrackedDataHandlerRegistry.method_12720(trackedData.method_12714());
		if (i < 0) {
			throw new EncoderException("Unknown serializer type " + trackedData.method_12714());
		} else {
			packetByteBuf.writeByte(trackedData.method_12713());
			packetByteBuf.writeVarInt(i);
			trackedData.method_12714().write(packetByteBuf, dataEntry.getValue());
		}
	}

	@Nullable
	public static List<DataTracker.DataEntry<?>> method_12753(PacketByteBuf packetByteBuf) throws IOException {
		List<DataTracker.DataEntry<?>> list = null;

		int i;
		while ((i = packetByteBuf.readUnsignedByte()) != 255) {
			if (list == null) {
				list = Lists.newArrayList();
			}

			int j = packetByteBuf.readVarInt();
			TrackedDataHandler<?> trackedDataHandler = TrackedDataHandlerRegistry.method_12718(j);
			if (trackedDataHandler == null) {
				throw new DecoderException("Unknown serializer type " + j);
			}

			list.add(new DataTracker.DataEntry<>(trackedDataHandler.create(i), trackedDataHandler.read(packetByteBuf)));
		}

		return list;
	}

	public void writeUpdatedEntries(List<DataTracker.DataEntry<?>> list) {
		this.lock.writeLock().lock();

		for (DataTracker.DataEntry<?> dataEntry : list) {
			DataTracker.DataEntry<?> dataEntry2 = (DataTracker.DataEntry<?>)this.field_13833.get(dataEntry.method_12758().method_12713());
			if (dataEntry2 != null) {
				this.method_12752(dataEntry2, dataEntry);
				this.entity.onTrackedDataSet(dataEntry.method_12758());
			}
		}

		this.lock.writeLock().unlock();
		this.dirty = true;
	}

	protected <T> void method_12752(DataTracker.DataEntry<T> dataEntry, DataTracker.DataEntry<?> dataEntry2) {
		dataEntry.setValue((T)dataEntry2.getValue());
	}

	public boolean isEmpty() {
		return this.empty;
	}

	public void clearDirty() {
		this.dirty = false;
	}

	public static class DataEntry<T> {
		private final TrackedData<T> field_13834;
		private T value;
		private boolean modified;

		public DataEntry(TrackedData<T> trackedData, T object) {
			this.field_13834 = trackedData;
			this.value = object;
			this.modified = true;
		}

		public TrackedData<T> method_12758() {
			return this.field_13834;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public T getValue() {
			return this.value;
		}

		public boolean isModified() {
			return this.modified;
		}

		public void setModified(boolean modified) {
			this.modified = modified;
		}
	}
}
