package net.minecraft.entity.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;
import org.apache.commons.lang3.ObjectUtils;

public class DataTracker {
	private final Entity entity;
	private boolean empty = true;
	private static final Map<Class<?>, Integer> trackedEntities = Maps.newHashMap();
	private final Map<Integer, DataTracker.DataEntry> entries = Maps.newHashMap();
	private boolean dirty;
	private ReadWriteLock lock = new ReentrantReadWriteLock();

	public DataTracker(Entity entity) {
		this.entity = entity;
	}

	public <T> void track(int id, T object) {
		Integer integer = (Integer)trackedEntities.get(object.getClass());
		if (integer == null) {
			throw new IllegalArgumentException("Unknown data type: " + object.getClass());
		} else if (id > 31) {
			throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
		} else if (this.entries.containsKey(id)) {
			throw new IllegalArgumentException("Duplicate id value for " + id + "!");
		} else {
			DataTracker.DataEntry dataEntry = new DataTracker.DataEntry(integer, id, object);
			this.lock.writeLock().lock();
			this.entries.put(id, dataEntry);
			this.lock.writeLock().unlock();
			this.empty = false;
		}
	}

	public void addEntry(int i, int j) {
		DataTracker.DataEntry dataEntry = new DataTracker.DataEntry(j, i, null);
		this.lock.writeLock().lock();
		this.entries.put(i, dataEntry);
		this.lock.writeLock().unlock();
		this.empty = false;
	}

	public byte getByte(int id) {
		return (Byte)this.get(id).getValue();
	}

	public short getShort(int id) {
		return (Short)this.get(id).getValue();
	}

	public int getInt(int id) {
		return (Integer)this.get(id).getValue();
	}

	public float getFloat(int id) {
		return (Float)this.get(id).getValue();
	}

	public String getString(int id) {
		return (String)this.get(id).getValue();
	}

	public ItemStack getStack(int id) {
		return (ItemStack)this.get(id).getValue();
	}

	private DataTracker.DataEntry get(int id) {
		this.lock.readLock().lock();

		DataTracker.DataEntry dataEntry;
		try {
			dataEntry = (DataTracker.DataEntry)this.entries.get(id);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Getting synched entity data");
			CrashReportSection crashReportSection = crashReport.addElement("Synched entity data");
			crashReportSection.add("Data ID", id);
			throw new CrashException(crashReport);
		}

		this.lock.readLock().unlock();
		return dataEntry;
	}

	public EulerAngle method_10992(int i) {
		return (EulerAngle)this.get(i).getValue();
	}

	public <T> void setProperty(int id, T value) {
		DataTracker.DataEntry dataEntry = this.get(id);
		if (ObjectUtils.notEqual(value, dataEntry.getValue())) {
			dataEntry.setValue(value);
			this.entity.method_8364(id);
			dataEntry.setModified(true);
			this.dirty = true;
		}
	}

	public void markDirty(int id) {
		this.get(id).modified = true;
		this.dirty = true;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public static void writeData(List<DataTracker.DataEntry> entries, PacketByteBuf data) throws IOException {
		if (entries != null) {
			for (DataTracker.DataEntry dataEntry : entries) {
				writeEntryToPacket(data, dataEntry);
			}
		}

		data.writeByte(127);
	}

	public List<DataTracker.DataEntry> getChangedEntries() {
		List<DataTracker.DataEntry> list = null;
		if (this.dirty) {
			this.lock.readLock().lock();

			for (DataTracker.DataEntry dataEntry : this.entries.values()) {
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

		for (DataTracker.DataEntry dataEntry : this.entries.values()) {
			writeEntryToPacket(packet, dataEntry);
		}

		this.lock.readLock().unlock();
		packet.writeByte(127);
	}

	public List<DataTracker.DataEntry> getEntries() {
		List<DataTracker.DataEntry> list = null;
		this.lock.readLock().lock();

		for (DataTracker.DataEntry dataEntry : this.entries.values()) {
			if (list == null) {
				list = Lists.newArrayList();
			}

			list.add(dataEntry);
		}

		this.lock.readLock().unlock();
		return list;
	}

	private static void writeEntryToPacket(PacketByteBuf packet, DataTracker.DataEntry entry) throws IOException {
		int i = (entry.getValueType() << 5 | entry.method_2707() & 31) & 0xFF;
		packet.writeByte(i);
		switch (entry.getValueType()) {
			case 0:
				packet.writeByte((Byte)entry.getValue());
				break;
			case 1:
				packet.writeShort((Short)entry.getValue());
				break;
			case 2:
				packet.writeInt((Integer)entry.getValue());
				break;
			case 3:
				packet.writeFloat((Float)entry.getValue());
				break;
			case 4:
				packet.writeString((String)entry.getValue());
				break;
			case 5:
				ItemStack itemStack = (ItemStack)entry.getValue();
				packet.writeItemStack(itemStack);
				break;
			case 6:
				BlockPos blockPos = (BlockPos)entry.getValue();
				packet.writeInt(blockPos.getX());
				packet.writeInt(blockPos.getY());
				packet.writeInt(blockPos.getZ());
				break;
			case 7:
				EulerAngle eulerAngle = (EulerAngle)entry.getValue();
				packet.writeFloat(eulerAngle.getPitch());
				packet.writeFloat(eulerAngle.getYaw());
				packet.writeFloat(eulerAngle.getRoll());
		}
	}

	public static List<DataTracker.DataEntry> deserializePacket(PacketByteBuf packet) throws IOException {
		List<DataTracker.DataEntry> list = null;

		for (int i = packet.readByte(); i != 127; i = packet.readByte()) {
			if (list == null) {
				list = Lists.newArrayList();
			}

			int j = (i & 224) >> 5;
			int k = i & 31;
			DataTracker.DataEntry dataEntry = null;
			switch (j) {
				case 0:
					dataEntry = new DataTracker.DataEntry(j, k, packet.readByte());
					break;
				case 1:
					dataEntry = new DataTracker.DataEntry(j, k, packet.readShort());
					break;
				case 2:
					dataEntry = new DataTracker.DataEntry(j, k, packet.readInt());
					break;
				case 3:
					dataEntry = new DataTracker.DataEntry(j, k, packet.readFloat());
					break;
				case 4:
					dataEntry = new DataTracker.DataEntry(j, k, packet.readString(32767));
					break;
				case 5:
					dataEntry = new DataTracker.DataEntry(j, k, packet.readItemStack());
					break;
				case 6:
					int l = packet.readInt();
					int m = packet.readInt();
					int n = packet.readInt();
					dataEntry = new DataTracker.DataEntry(j, k, new BlockPos(l, m, n));
					break;
				case 7:
					float f = packet.readFloat();
					float g = packet.readFloat();
					float h = packet.readFloat();
					dataEntry = new DataTracker.DataEntry(j, k, new EulerAngle(f, g, h));
			}

			list.add(dataEntry);
		}

		return list;
	}

	public void writeUpdatedEntries(List<DataTracker.DataEntry> list) {
		this.lock.writeLock().lock();

		for (DataTracker.DataEntry dataEntry : list) {
			DataTracker.DataEntry dataEntry2 = (DataTracker.DataEntry)this.entries.get(dataEntry.method_2707());
			if (dataEntry2 != null) {
				dataEntry2.setValue(dataEntry.getValue());
				this.entity.method_8364(dataEntry.method_2707());
			}
		}

		this.lock.writeLock().unlock();
		this.dirty = true;
	}

	public boolean isEmpty() {
		return this.empty;
	}

	public void clearDirty() {
		this.dirty = false;
	}

	static {
		trackedEntities.put(Byte.class, 0);
		trackedEntities.put(Short.class, 1);
		trackedEntities.put(Integer.class, 2);
		trackedEntities.put(Float.class, 3);
		trackedEntities.put(String.class, 4);
		trackedEntities.put(ItemStack.class, 5);
		trackedEntities.put(BlockPos.class, 6);
		trackedEntities.put(EulerAngle.class, 7);
	}

	public static class DataEntry {
		private final int valueType;
		private final int field_3424;
		private Object value;
		private boolean modified;

		public DataEntry(int i, int j, Object object) {
			this.field_3424 = j;
			this.value = object;
			this.valueType = i;
			this.modified = true;
		}

		public int method_2707() {
			return this.field_3424;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public Object getValue() {
			return this.value;
		}

		public int getValueType() {
			return this.valueType;
		}

		public boolean isModified() {
			return this.modified;
		}

		public void setModified(boolean modified) {
			this.modified = modified;
		}
	}
}
