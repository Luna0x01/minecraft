package net.minecraft.nbt;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class NbtCompound implements NbtElement {
	public static final Codec<NbtCompound> CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
		NbtElement nbtElement = (NbtElement)dynamic.convert(NbtOps.INSTANCE).getValue();
		return nbtElement instanceof NbtCompound ? DataResult.success((NbtCompound)nbtElement) : DataResult.error("Not a compound tag: " + nbtElement);
	}, nbt -> new Dynamic(NbtOps.INSTANCE, nbt));
	private static final int field_33190 = 384;
	private static final int field_33191 = 256;
	public static final NbtType<NbtCompound> TYPE = new NbtType<NbtCompound>() {
		public NbtCompound read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(384L);
			if (i > 512) {
				throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
			} else {
				Map<String, NbtElement> map = Maps.newHashMap();

				byte b;
				while ((b = NbtCompound.readByte(dataInput, nbtTagSizeTracker)) != 0) {
					String string = NbtCompound.readString(dataInput, nbtTagSizeTracker);
					nbtTagSizeTracker.add((long)(224 + 16 * string.length()));
					NbtElement nbtElement = NbtCompound.read(NbtTypes.byId(b), string, dataInput, i + 1, nbtTagSizeTracker);
					if (map.put(string, nbtElement) != null) {
						nbtTagSizeTracker.add(288L);
					}
				}

				return new NbtCompound(map);
			}
		}

		@Override
		public String getCrashReportName() {
			return "COMPOUND";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_Compound";
		}
	};
	private final Map<String, NbtElement> entries;

	protected NbtCompound(Map<String, NbtElement> entries) {
		this.entries = entries;
	}

	public NbtCompound() {
		this(Maps.newHashMap());
	}

	@Override
	public void write(DataOutput output) throws IOException {
		for (String string : this.entries.keySet()) {
			NbtElement nbtElement = (NbtElement)this.entries.get(string);
			write(string, nbtElement, output);
		}

		output.writeByte(0);
	}

	public Set<String> getKeys() {
		return this.entries.keySet();
	}

	@Override
	public byte getType() {
		return 10;
	}

	@Override
	public NbtType<NbtCompound> getNbtType() {
		return TYPE;
	}

	public int getSize() {
		return this.entries.size();
	}

	@Nullable
	public NbtElement put(String key, NbtElement element) {
		return (NbtElement)this.entries.put(key, element);
	}

	public void putByte(String key, byte value) {
		this.entries.put(key, NbtByte.of(value));
	}

	public void putShort(String key, short value) {
		this.entries.put(key, NbtShort.of(value));
	}

	public void putInt(String key, int value) {
		this.entries.put(key, NbtInt.of(value));
	}

	public void putLong(String key, long value) {
		this.entries.put(key, NbtLong.of(value));
	}

	public void putUuid(String key, UUID value) {
		this.entries.put(key, NbtHelper.fromUuid(value));
	}

	public UUID getUuid(String key) {
		return NbtHelper.toUuid(this.get(key));
	}

	public boolean containsUuid(String key) {
		NbtElement nbtElement = this.get(key);
		return nbtElement != null && nbtElement.getNbtType() == NbtIntArray.TYPE && ((NbtIntArray)nbtElement).getIntArray().length == 4;
	}

	public void putFloat(String key, float value) {
		this.entries.put(key, NbtFloat.of(value));
	}

	public void putDouble(String key, double value) {
		this.entries.put(key, NbtDouble.of(value));
	}

	public void putString(String key, String value) {
		this.entries.put(key, NbtString.of(value));
	}

	public void putByteArray(String key, byte[] value) {
		this.entries.put(key, new NbtByteArray(value));
	}

	public void putByteArray(String key, List<Byte> value) {
		this.entries.put(key, new NbtByteArray(value));
	}

	public void putIntArray(String key, int[] value) {
		this.entries.put(key, new NbtIntArray(value));
	}

	public void putIntArray(String key, List<Integer> value) {
		this.entries.put(key, new NbtIntArray(value));
	}

	public void putLongArray(String key, long[] value) {
		this.entries.put(key, new NbtLongArray(value));
	}

	public void putLongArray(String key, List<Long> value) {
		this.entries.put(key, new NbtLongArray(value));
	}

	public void putBoolean(String key, boolean value) {
		this.entries.put(key, NbtByte.of(value));
	}

	@Nullable
	public NbtElement get(String key) {
		return (NbtElement)this.entries.get(key);
	}

	public byte getType(String key) {
		NbtElement nbtElement = (NbtElement)this.entries.get(key);
		return nbtElement == null ? 0 : nbtElement.getType();
	}

	public boolean contains(String key) {
		return this.entries.containsKey(key);
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
				return ((AbstractNbtNumber)this.entries.get(key)).byteValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public short getShort(String key) {
		try {
			if (this.contains(key, 99)) {
				return ((AbstractNbtNumber)this.entries.get(key)).shortValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public int getInt(String key) {
		try {
			if (this.contains(key, 99)) {
				return ((AbstractNbtNumber)this.entries.get(key)).intValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0;
	}

	public long getLong(String key) {
		try {
			if (this.contains(key, 99)) {
				return ((AbstractNbtNumber)this.entries.get(key)).longValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0L;
	}

	public float getFloat(String key) {
		try {
			if (this.contains(key, 99)) {
				return ((AbstractNbtNumber)this.entries.get(key)).floatValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0.0F;
	}

	public double getDouble(String key) {
		try {
			if (this.contains(key, 99)) {
				return ((AbstractNbtNumber)this.entries.get(key)).doubleValue();
			}
		} catch (ClassCastException var3) {
		}

		return 0.0;
	}

	public String getString(String key) {
		try {
			if (this.contains(key, 8)) {
				return ((NbtElement)this.entries.get(key)).asString();
			}
		} catch (ClassCastException var3) {
		}

		return "";
	}

	public byte[] getByteArray(String key) {
		try {
			if (this.contains(key, 7)) {
				return ((NbtByteArray)this.entries.get(key)).getByteArray();
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(key, NbtByteArray.TYPE, var3));
		}

		return new byte[0];
	}

	public int[] getIntArray(String key) {
		try {
			if (this.contains(key, 11)) {
				return ((NbtIntArray)this.entries.get(key)).getIntArray();
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(key, NbtIntArray.TYPE, var3));
		}

		return new int[0];
	}

	public long[] getLongArray(String key) {
		try {
			if (this.contains(key, 12)) {
				return ((NbtLongArray)this.entries.get(key)).getLongArray();
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(key, NbtLongArray.TYPE, var3));
		}

		return new long[0];
	}

	public NbtCompound getCompound(String key) {
		try {
			if (this.contains(key, 10)) {
				return (NbtCompound)this.entries.get(key);
			}
		} catch (ClassCastException var3) {
			throw new CrashException(this.createCrashReport(key, TYPE, var3));
		}

		return new NbtCompound();
	}

	public NbtList getList(String key, int type) {
		try {
			if (this.getType(key) == 9) {
				NbtList nbtList = (NbtList)this.entries.get(key);
				if (!nbtList.isEmpty() && nbtList.getHeldType() != type) {
					return new NbtList();
				}

				return nbtList;
			}
		} catch (ClassCastException var4) {
			throw new CrashException(this.createCrashReport(key, NbtList.TYPE, var4));
		}

		return new NbtList();
	}

	public boolean getBoolean(String key) {
		return this.getByte(key) != 0;
	}

	public void remove(String key) {
		this.entries.remove(key);
	}

	@Override
	public String toString() {
		return this.asString();
	}

	public boolean isEmpty() {
		return this.entries.isEmpty();
	}

	private CrashReport createCrashReport(String key, NbtType<?> reader, ClassCastException classCastException) {
		CrashReport crashReport = CrashReport.create(classCastException, "Reading NBT data");
		CrashReportSection crashReportSection = crashReport.addElement("Corrupt NBT tag", 1);
		crashReportSection.add("Tag type found", (CrashCallable<String>)(() -> ((NbtElement)this.entries.get(key)).getNbtType().getCrashReportName()));
		crashReportSection.add("Tag type expected", reader::getCrashReportName);
		crashReportSection.add("Tag name", key);
		return crashReport;
	}

	public NbtCompound copy() {
		Map<String, NbtElement> map = Maps.newHashMap(Maps.transformValues(this.entries, NbtElement::copy));
		return new NbtCompound(map);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtCompound && Objects.equals(this.entries, ((NbtCompound)o).entries);
	}

	public int hashCode() {
		return this.entries.hashCode();
	}

	private static void write(String key, NbtElement element, DataOutput output) throws IOException {
		output.writeByte(element.getType());
		if (element.getType() != 0) {
			output.writeUTF(key);
			element.write(output);
		}
	}

	static byte readByte(DataInput input, NbtTagSizeTracker tracker) throws IOException {
		return input.readByte();
	}

	static String readString(DataInput input, NbtTagSizeTracker tracker) throws IOException {
		return input.readUTF();
	}

	static NbtElement read(NbtType<?> reader, String key, DataInput input, int depth, NbtTagSizeTracker tracker) {
		try {
			return reader.read(input, depth, tracker);
		} catch (IOException var8) {
			CrashReport crashReport = CrashReport.create(var8, "Loading NBT data");
			CrashReportSection crashReportSection = crashReport.addElement("NBT Tag");
			crashReportSection.add("Tag name", key);
			crashReportSection.add("Tag type", reader.getCrashReportName());
			throw new CrashException(crashReport);
		}
	}

	public NbtCompound copyFrom(NbtCompound source) {
		for (String string : source.entries.keySet()) {
			NbtElement nbtElement = (NbtElement)source.entries.get(string);
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

		return this;
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitCompound(this);
	}

	protected Map<String, NbtElement> toMap() {
		return Collections.unmodifiableMap(this.entries);
	}
}
