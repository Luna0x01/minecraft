package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NbtList extends AbstractNbtList<NbtElement> {
	private static final Logger LOGGER = LogManager.getLogger();
	private List<NbtElement> value = Lists.newArrayList();
	private byte type = 0;

	@Override
	public void write(DataOutput output) throws IOException {
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
	public void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
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

			stringBuilder.append(this.value.get(i));
		}

		return stringBuilder.append(']').toString();
	}

	public boolean add(NbtElement nbtElement) {
		if (nbtElement.getType() == 0) {
			LOGGER.warn("Invalid TagEnd added to ListTag");
			return false;
		} else {
			if (this.type == 0) {
				this.type = nbtElement.getType();
			} else if (this.type != nbtElement.getType()) {
				LOGGER.warn("Adding mismatching tag types to tag list");
				return false;
			}

			this.value.add(nbtElement);
			return true;
		}
	}

	@Override
	public NbtElement set(int i, NbtElement nbtElement) {
		if (nbtElement.getType() == 0) {
			LOGGER.warn("Invalid TagEnd added to ListTag");
			return (NbtElement)this.value.get(i);
		} else if (i >= 0 && i < this.value.size()) {
			if (this.type == 0) {
				this.type = nbtElement.getType();
			} else if (this.type != nbtElement.getType()) {
				LOGGER.warn("Adding mismatching tag types to tag list");
				return (NbtElement)this.value.get(i);
			}

			return (NbtElement)this.value.set(i, nbtElement);
		} else {
			LOGGER.warn("index out of bounds to set tag in tag list");
			return null;
		}
	}

	public NbtElement remove(int i) {
		return (NbtElement)this.value.remove(i);
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

	@Override
	public NbtElement get(int i) {
		return (NbtElement)(i >= 0 && i < this.value.size() ? (NbtElement)this.value.get(i) : new NbtEnd());
	}

	@Override
	public int size() {
		return this.value.size();
	}

	@Override
	public NbtElement getElement(int index) {
		return (NbtElement)this.value.get(index);
	}

	@Override
	public void setElement(int index, NbtElement nbt) {
		this.value.set(index, nbt);
	}

	@Override
	public void remove(int index) {
		this.value.remove(index);
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

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtList && Objects.equals(this.value, ((NbtList)o).value);
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public Text asText(String indentChar, int indentCount) {
		if (this.isEmpty()) {
			return new LiteralText("[]");
		} else {
			Text text = new LiteralText("[");
			if (!indentChar.isEmpty()) {
				text.append("\n");
			}

			for (int i = 0; i < this.value.size(); i++) {
				Text text2 = new LiteralText(Strings.repeat(indentChar, indentCount + 1));
				text2.append(((NbtElement)this.value.get(i)).asText(indentChar, indentCount + 1));
				if (i != this.value.size() - 1) {
					text2.append(String.valueOf(',')).append(indentChar.isEmpty() ? " " : "\n");
				}

				text.append(text2);
			}

			if (!indentChar.isEmpty()) {
				text.append("\n").append(Strings.repeat(indentChar, indentCount));
			}

			text.append("]");
			return text;
		}
	}

	public int getElementType() {
		return this.type;
	}
}
