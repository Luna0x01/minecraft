package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

public interface NbtElement {
	int field_33246 = 64;
	int field_33247 = 96;
	int field_33248 = 32;
	int field_33249 = 224;
	byte NULL_TYPE = 0;
	byte BYTE_TYPE = 1;
	byte SHORT_TYPE = 2;
	byte INT_TYPE = 3;
	byte LONG_TYPE = 4;
	byte FLOAT_TYPE = 5;
	byte DOUBLE_TYPE = 6;
	byte BYTE_ARRAY_TYPE = 7;
	byte STRING_TYPE = 8;
	byte LIST_TYPE = 9;
	byte COMPOUND_TYPE = 10;
	byte INT_ARRAY_TYPE = 11;
	byte LONG_ARRAY_TYPE = 12;
	byte NUMBER_TYPE = 99;
	int field_33264 = 512;

	void write(DataOutput output) throws IOException;

	String toString();

	byte getType();

	NbtType<?> getNbtType();

	NbtElement copy();

	default String asString() {
		return new StringNbtWriter().apply(this);
	}

	void accept(NbtElementVisitor visitor);
}
