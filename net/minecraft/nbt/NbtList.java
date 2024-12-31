package net.minecraft.nbt;

import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NbtList extends NbtElement {
	private static final Logger LOGGER = LogManager.getLogger();
	private List<NbtElement> value = Lists.newArrayList();
	private byte type = 0;

	@Override
	void write(DataOutput output) throws IOException {
		if (this.value.isEmpty()) {
			this.type = 0;
		} else {
			this.type = ((NbtElement)this.value.get(0)).getType();
		}

		output.writeByte(this.type);
		output.writeInt(this.value.size());

		for (int i = 0; i < this.value.size(); i++) {
			((NbtElement)this.value.get(i)).write(output);
		}
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(296L);
		if (depth > 512) {
			throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
		} else {
			this.type = input.readByte();
			int i = input.readInt();
			if (this.type == 0 && i > 0) {
				throw new RuntimeException("Missing type on ListTag");
			} else {
				tracker.add(32L * (long)i);
				this.value = Lists.newArrayListWithCapacity(i);

				for (int j = 0; j < i; j++) {
					NbtElement nbtElement = NbtElement.createFromType(this.type);
					nbtElement.read(input, depth + 1, tracker);
					this.value.add(nbtElement);
				}
			}
		}
	}

	@Override
	public byte getType() {
		return 9;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[");

		for (int i = 0; i < this.value.size(); i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(i).append(':').append(this.value.get(i));
		}

		return stringBuilder.append(']').toString();
	}

	public void add(NbtElement nbt) {
		if (nbt.getType() == 0) {
			LOGGER.warn("Invalid TagEnd added to ListTag");
		} else {
			if (this.type == 0) {
				this.type = nbt.getType();
			} else if (this.type != nbt.getType()) {
				LOGGER.warn("Adding mismatching tag types to tag list");
				return;
			}

			this.value.add(nbt);
		}
	}

	public void set(int id, NbtElement nbt) {
		if (nbt.getType() == 0) {
			LOGGER.warn("Invalid TagEnd added to ListTag");
		} else if (id >= 0 && id < this.value.size()) {
			if (this.type == 0) {
				this.type = nbt.getType();
			} else if (this.type != nbt.getType()) {
				LOGGER.warn("Adding mismatching tag types to tag list");
				return;
			}

			this.value.set(id, nbt);
		} else {
			LOGGER.warn("index out of bounds to set tag in tag list");
		}
	}

	public NbtElement remove(int id) {
		return (NbtElement)this.value.remove(id);
	}

	@Override
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

	public NbtElement get(int index) {
		return (NbtElement)(index >= 0 && index < this.value.size() ? (NbtElement)this.value.get(index) : new NbtEnd());
	}

	public int size() {
		return this.value.size();
	}

	public NbtList copy() {
		NbtList nbtList = new NbtList();
		nbtList.type = this.type;

		for (NbtElement nbtElement : this.value) {
			NbtElement nbtElement2 = nbtElement.copy();
			nbtList.value.add(nbtElement2);
		}

		return nbtList;
	}

	@Override
	public boolean equals(Object object) {
		if (super.equals(object)) {
			NbtList nbtList = (NbtList)object;
			if (this.type == nbtList.type) {
				return this.value.equals(nbtList.value);
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ this.value.hashCode();
	}

	public int getElementType() {
		return this.type;
	}
}
