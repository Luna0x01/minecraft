package net.minecraft.nbt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NbtCompound extends NbtElement {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern ALLOW_UNQUOTED_PATTERN = Pattern.compile("[A-Za-z0-9._+-]+");
	private final Map<String, NbtElement> data = Maps.newHashMap();

	@Override
	void write(DataOutput output) throws IOException {
		for (String string : this.data.keySet()) {
			NbtElement nbtElement = (NbtElement)this.data.get(string);
			write(string, nbtElement, output);
		}

		output.writeByte(0);
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(384L);
		if (depth > 512) {
			throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
		} else {
			this.data.clear();

			byte b;
			while ((b = readByte(input, tracker)) != 0) {
				String string = readString(input, tracker);
				tracker.add((long)(224 + 16 * string.length()));
				NbtElement nbtElement = readNbt(b, string, input, depth + 1, tracker);
				if (this.data.put(string, nbtElement) != null) {
					tracker.add(288L);
				}
			}
		}
	}

	public Set<String> getKeys() {
		return this.data.keySet();
	}

	@Override
	public byte getType() {
		return 10;
	}

	public int getSize() {
		return this.data.size();
	}

	public void put(String key, NbtElement nbt) {
		this.data.put(key, nbt);
	}

	public void putByte(String key, byte value) {
		this.data.put(key, new NbtByte(value));
	}

	public void putShort(String key, short value) {
		this.data.put(key, new NbtShort(value));
	}

	public void putInt(String key, int value) {
		this.data.put(key, new NbtInt(value));
	}

	public void putLong(String key, long value) {
		this.data.put(key, new NbtLong(value));
	}

	public void putUuid(String key, UUID value) {
		this.putLong(key + "Most", value.getMostSignificantBits());
		this.putLong(key + "Least", value.getLeastSignificantBits());
	}

	@Nullable
	public UUID getUuid(String key) {
		return new UUID(this.getLong(key + "Most"), this.getLong(key + "Least"));
	}

	public boolean containsUuid(String key) {
		return this.contains(key + "Most", 99) && this.contains(key + "Least", 99);
	}

	public void putFloat(String key, float value) {
		this.data.put(key, new NbtFloat(value));
	}

	public void putDouble(String key, double value) {
		this.data.put(key, new NbtDouble(value));
	}

	public void putString(String key, String value) {
		this.data.put(key, new NbtString(value));
	}

	public void putByteArray(String key, byte[] value) {
		this.data.put(key, new NbtByteArray(value));
	}

	public void putIntArray(String key, int[] value) {
		this.data.put(key, new NbtIntArray(value));
	}

	public void putBoolean(String key, boolean bool) {
		this.putByte(key, (byte)(bool ? 1 : 0));
	}

	public NbtElement get(String key) {
		return (NbtElement)this.data.get(key);
	}

	public byte getType(String key) {
		NbtElement nbtElement = (NbtElement)this.data.get(key);
		return nbtElement == null ? 0 : nbtElement.getType();
	}

	public boolean contains(String key) {
		return this.data.containsKey(key);
	}

	public boolean contains(String key, int type) {
		int i = this.getType(key);
		if (i == type) {
			return true;
		} else {
			return type != 99 ? false : i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
		}
	}

	public byte getByte(String key) {
		try {
			if (this.contains(key, 99)) {
				return ((AbstractNbtNumber)this.data.get(key)).byteValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public short getShort(String key) {
		try {
			if (this.contains(key, 99)) {
				return ((AbstractNbtNumber)this.data.get(key)).shortValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public int getInt(String key) {
		try {
			if (this.contains(key, 99)) {
				return ((AbstractNbtNumber)this.data.get(key)).intValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public long getLong(String key) {
		try {
			if (this.contains(key, 99)) {
				return ((AbstractNbtNumber)this.data.get(key)).longValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0L;
	}

	public float getFloat(String key) {
		try {
			if (this.contains(key, 99)) {
				return ((AbstractNbtNumber)this.data.get(key)).floatValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0.0F;
	}

	public double getDouble(String key) {
		try {
			if (this.contains(key, 99)) {
				return ((AbstractNbtNumber)this.data.get(key)).doubleValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0.0;
	}

	public String getString(String key) {
		try {
			if (this.contains(key, 8)) {
				return ((NbtElement)this.data.get(key)).asString();
			}
		} catch (ClassCastException var3) {
		}

		return "";
	}

	public byte[] getByteArray(String key) {
		try {
			if (this.contains(key, 7)) {
				return ((NbtByteArray)this.data.get(key)).getArray();
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.addDetailsToCrashReport(key, 7, var3));
		}

		return new byte[0];
	}

	public int[] getIntArray(String key) {
		try {
			if (this.contains(key, 11)) {
				return ((NbtIntArray)this.data.get(key)).getIntArray();
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.addDetailsToCrashReport(key, 11, var3));
		}

		return new int[0];
	}

	public NbtCompound getCompound(String key) {
		try {
			if (this.contains(key, 10)) {
				return (NbtCompound)this.data.get(key);
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.addDetailsToCrashReport(key, 10, var3));
		}

		return new NbtCompound();
	}

	public NbtList getList(String key, int type) {
		try {
			if (this.getType(key) == 9) {
				NbtList nbtList = (NbtList)this.data.get(key);
				if (!nbtList.isEmpty() && nbtList.getElementType() != type) {
					return new NbtList();
				}

				return nbtList;
			}
		} catch (ClassCastException var4) {
			throw new CrashException(this.addDetailsToCrashReport(key, 9, var4));
		}

		return new NbtList();
	}

	public boolean getBoolean(String key) {
		return this.getByte(key) != 0;
	}

	public void remove(String key) {
		this.data.remove(key);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("{");
		Collection<String> collection = this.data.keySet();
		if (LOGGER.isDebugEnabled()) {
			List<String> list = Lists.newArrayList(this.data.keySet());
			Collections.sort(list);
			collection = list;
		}

		for (String string : collection) {
			if (stringBuilder.length() != 1) {
				stringBuilder.append(',');
			}

			stringBuilder.append(escapeKey(string)).append(':').append(this.data.get(string));
		}

		return stringBuilder.append('}').toString();
	}

	@Override
	public boolean isEmpty() {
		return this.data.isEmpty();
	}

	private CrashReport addDetailsToCrashReport(String key, int id, ClassCastException ex) {
		CrashReport crashReport = CrashReport.create(ex, "Reading NBT data");
		CrashReportSection crashReportSection = crashReport.addElement("Corrupt NBT tag", 1);
		crashReportSection.add("Tag type found", new CrashCallable<String>() {
			public String call() throws Exception {
				return NbtElement.TYPES[((NbtElement)NbtCompound.this.data.get(key)).getType()];
			}
		});
		crashReportSection.add("Tag type expected", new CrashCallable<String>() {
			public String call() throws Exception {
				return NbtElement.TYPES[id];
			}
		});
		crashReportSection.add("Tag name", key);
		return crashReport;
	}

	public NbtCompound copy() {
		NbtCompound nbtCompound = new NbtCompound();

		for (String string : this.data.keySet()) {
			nbtCompound.put(string, ((NbtElement)this.data.get(string)).copy());
		}

		return nbtCompound;
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object) && Objects.equals(this.data.entrySet(), ((NbtCompound)object).data.entrySet());
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ this.data.hashCode();
	}

	private static void write(String key, NbtElement nbt, DataOutput output) throws IOException {
		output.writeByte(nbt.getType());
		if (nbt.getType() != 0) {
			output.writeUTF(key);
			nbt.write(output);
		}
	}

	private static byte readByte(DataInput input, PositionTracker tracker) throws IOException {
		return input.readByte();
	}

	private static String readString(DataInput input, PositionTracker tracker) throws IOException {
		return input.readUTF();
	}

	static NbtElement readNbt(byte type, String name, DataInput input, int depth, PositionTracker tracker) throws IOException {
		NbtElement nbtElement = NbtElement.createFromType(type);

		try {
			nbtElement.read(input, depth, tracker);
			return nbtElement;
		} catch (IOException var9) {
			CrashReport crashReport = CrashReport.create(var9, "Loading NBT data");
			CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
			crashReportSection.add("Tag name", name);
			crashReportSection.add("Tag type", type);
			throw new CrashException(crashReport);
		}
	}

	public void copyFrom(NbtCompound nbt) {
		for (String string : nbt.data.keySet()) {
			NbtElement nbtElement = (NbtElement)nbt.data.get(string);
			if (nbtElement.getType() == 10) {
				if (this.contains(string, 10)) {
					NbtCompound nbtCompound = this.getCompound(string);
					nbtCompound.copyFrom((NbtCompound)nbtElement);
				} else {
					this.put(string, nbtElement.copy());
				}
			} else {
				this.put(string, nbtElement.copy());
			}
		}
	}

	protected static String escapeKey(String string) {
		return ALLOW_UNQUOTED_PATTERN.matcher(string).matches() ? string : NbtString.quote(string);
	}
}
