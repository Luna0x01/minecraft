package net.minecraft.nbt;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.nbt.visitor.NbtElementVisitor;

public class NbtList extends AbstractNbtList<NbtElement> {
	private static final int field_33199 = 296;
	public static final NbtType<NbtList> TYPE = new NbtType<NbtList>() {
		public NbtList read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(296L);
			if (i > 512) {
				throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
			} else {
				byte b = dataInput.readByte();
				int j = dataInput.readInt();
				if (b == 0 && j > 0) {
					throw new RuntimeException("Missing type on ListTag");
				} else {
					nbtTagSizeTracker.add(32L * (long)j);
					NbtType<?> nbtType = NbtTypes.byId(b);
					List<NbtElement> list = Lists.newArrayListWithCapacity(j);

					for (int k = 0; k < j; k++) {
						list.add(nbtType.read(dataInput, i + 1, nbtTagSizeTracker));
					}

					return new NbtList(list, b);
				}
			}
		}

		@Override
		public String getCrashReportName() {
			return "LIST";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_List";
		}
	};
	private final List<NbtElement> value;
	private byte type;

	NbtList(List<NbtElement> list, byte type) {
		this.value = list;
		this.type = type;
	}

	public NbtList() {
		this(Lists.newArrayList(), (byte)0);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		if (this.value.isEmpty()) {
			this.type = 0;
		} else {
			this.type = ((NbtElement)this.value.get(0)).getType();
		}

		output.writeByte(this.type);
		output.writeInt(this.value.size());

		for (NbtElement nbtElement : this.value) {
			nbtElement.write(output);
		}
	}

	@Override
	public byte getType() {
		return 9;
	}

	@Override
	public NbtType<NbtList> getNbtType() {
		return TYPE;
	}

	@Override
	public String toString() {
		return this.asString();
	}

	private void forgetTypeIfEmpty() {
		if (this.value.isEmpty()) {
			this.type = 0;
		}
	}

	@Override
	public NbtElement remove(int i) {
		NbtElement nbtElement = (NbtElement)this.value.remove(i);
		this.forgetTypeIfEmpty();
		return nbtElement;
	}

	public boolean isEmpty() {
		return this.value.isEmpty();
	}

	public NbtCompound getCompound(int index) {
		if (index >= 0 && index < this.value.size()) {
			NbtElement nbtElement = (NbtElement)this.value.get(index);
			if (nbtElement.getType() == 10) {
				return (NbtCompound)nbtElement;
			}
		}

		return new NbtCompound();
	}

	public NbtList getList(int index) {
		if (index >= 0 && index < this.value.size()) {
			NbtElement nbtElement = (NbtElement)this.value.get(index);
			if (nbtElement.getType() == 9) {
				return (NbtList)nbtElement;
			}
		}

		return new NbtList();
	}

	public short getShort(int index) {
		if (index >= 0 && index < this.value.size()) {
			NbtElement nbtElement = (NbtElement)this.value.get(index);
			if (nbtElement.getType() == 2) {
				return ((NbtShort)nbtElement).shortValue();
			}
		}

		return 0;
	}

	public int getInt(int index) {
		if (index >= 0 && index < this.value.size()) {
			NbtElement nbtElement = (NbtElement)this.value.get(index);
			if (nbtElement.getType() == 3) {
				return ((NbtInt)nbtElement).intValue();
			}
		}

		return 0;
	}

	public int[] getIntArray(int index) {
		if (index >= 0 && index < this.value.size()) {
			NbtElement nbtElement = (NbtElement)this.value.get(index);
			if (nbtElement.getType() == 11) {
				return ((NbtIntArray)nbtElement).getIntArray();
			}
		}

		return new int[0];
	}

	public long[] getLongArray(int index) {
		if (index >= 0 && index < this.value.size()) {
			NbtElement nbtElement = (NbtElement)this.value.get(index);
			if (nbtElement.getType() == 11) {
				return ((NbtLongArray)nbtElement).getLongArray();
			}
		}

		return new long[0];
	}

	public double getDouble(int index) {
		if (index >= 0 && index < this.value.size()) {
			NbtElement nbtElement = (NbtElement)this.value.get(index);
			if (nbtElement.getType() == 6) {
				return ((NbtDouble)nbtElement).doubleValue();
			}
		}

		return 0.0;
	}

	public float getFloat(int index) {
		if (index >= 0 && index < this.value.size()) {
			NbtElement nbtElement = (NbtElement)this.value.get(index);
			if (nbtElement.getType() == 5) {
				return ((NbtFloat)nbtElement).floatValue();
			}
		}

		return 0.0F;
	}

	public String getString(int index) {
		if (index >= 0 && index < this.value.size()) {
			NbtElement nbtElement = (NbtElement)this.value.get(index);
			return nbtElement.getType() == 8 ? nbtElement.asString() : nbtElement.toString();
		} else {
			return "";
		}
	}

	public int size() {
		return this.value.size();
	}

	public NbtElement get(int i) {
		return (NbtElement)this.value.get(i);
	}

	@Override
	public NbtElement set(int i, NbtElement nbtElement) {
		NbtElement nbtElement2 = this.get(i);
		if (!this.setElement(i, nbtElement)) {
			throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", nbtElement.getType(), this.type));
		} else {
			return nbtElement2;
		}
	}

	@Override
	public void add(int i, NbtElement nbtElement) {
		if (!this.addElement(i, nbtElement)) {
			throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", nbtElement.getType(), this.type));
		}
	}

	@Override
	public boolean setElement(int index, NbtElement element) {
		if (this.canAdd(element)) {
			this.value.set(index, element);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addElement(int index, NbtElement element) {
		if (this.canAdd(element)) {
			this.value.add(index, element);
			return true;
		} else {
			return false;
		}
	}

	private boolean canAdd(NbtElement element) {
		if (element.getType() == 0) {
			return false;
		} else if (this.type == 0) {
			this.type = element.getType();
			return true;
		} else {
			return this.type == element.getType();
		}
	}

	public NbtList copy() {
		Iterable<NbtElement> iterable = (Iterable<NbtElement>)(NbtTypes.byId(this.type).isImmutable()
			? this.value
			: Iterables.transform(this.value, NbtElement::copy));
		List<NbtElement> list = Lists.newArrayList(iterable);
		return new NbtList(list, this.type);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtList && Objects.equals(this.value, ((NbtList)o).value);
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public void accept(NbtElementVisitor visitor) {
		visitor.visitList(this);
	}

	@Override
	public byte getHeldType() {
		return this.type;
	}

	public void clear() {
		this.value.clear();
		this.type = 0;
	}
}
