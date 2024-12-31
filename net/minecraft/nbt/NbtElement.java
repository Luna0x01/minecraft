package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface NbtElement {
	String[] TYPES = new String[]{"END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]", "LONG[]"};
	Formatting COMPOUND_KEY_FORMATTING = Formatting.AQUA;
	Formatting STRING_FORMATTING = Formatting.GREEN;
	Formatting VALUE_FORMATTING = Formatting.GOLD;
	Formatting TYPE_FORMATTING = Formatting.RED;

	void write(DataOutput output) throws IOException;

	void read(DataInput input, int depth, PositionTracker tracker) throws IOException;

	String toString();

	byte getType();

	static NbtElement createFromType(byte id) {
		switch (id) {
			case 0:
				return new NbtEnd();
			case 1:
				return new NbtByte();
			case 2:
				return new NbtShort();
			case 3:
				return new NbtInt();
			case 4:
				return new NbtLong();
			case 5:
				return new NbtFloat();
			case 6:
				return new NbtDouble();
			case 7:
				return new NbtByteArray();
			case 8:
				return new NbtString();
			case 9:
				return new NbtList();
			case 10:
				return new NbtCompound();
			case 11:
				return new NbtIntArray();
			case 12:
				return new NbtLongArray();
			default:
				return null;
		}
	}

	static String getTypeName(int id) {
		switch (id) {
			case 0:
				return "TAG_End";
			case 1:
				return "TAG_Byte";
			case 2:
				return "TAG_Short";
			case 3:
				return "TAG_Int";
			case 4:
				return "TAG_Long";
			case 5:
				return "TAG_Float";
			case 6:
				return "TAG_Double";
			case 7:
				return "TAG_Byte_Array";
			case 8:
				return "TAG_String";
			case 9:
				return "TAG_List";
			case 10:
				return "TAG_Compound";
			case 11:
				return "TAG_Int_Array";
			case 12:
				return "TAG_Long_Array";
			case 99:
				return "Any Numeric Tag";
			default:
				return "UNKNOWN";
		}
	}

	NbtElement copy();

	default String asString() {
		return this.toString();
	}

	default Text asText() {
		return this.asText("", 0);
	}

	Text asText(String indentChar, int indentCount);
}
