package net.minecraft;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

public class class_4372 implements DynamicOps<NbtElement> {
	public static final class_4372 field_21487 = new class_4372();

	protected class_4372() {
	}

	public NbtElement empty() {
		return new NbtEnd();
	}

	public Type<?> getType(NbtElement nbtElement) {
		switch (nbtElement.getType()) {
			case 0:
				return DSL.nilType();
			case 1:
				return DSL.byteType();
			case 2:
				return DSL.shortType();
			case 3:
				return DSL.intType();
			case 4:
				return DSL.longType();
			case 5:
				return DSL.floatType();
			case 6:
				return DSL.doubleType();
			case 7:
				return DSL.list(DSL.byteType());
			case 8:
				return DSL.string();
			case 9:
				return DSL.list(DSL.remainderType());
			case 10:
				return DSL.compoundList(DSL.remainderType(), DSL.remainderType());
			case 11:
				return DSL.list(DSL.intType());
			case 12:
				return DSL.list(DSL.longType());
			default:
				return DSL.remainderType();
		}
	}

	public Optional<Number> getNumberValue(NbtElement nbtElement) {
		return nbtElement instanceof AbstractNbtNumber ? Optional.of(((AbstractNbtNumber)nbtElement).numberValue()) : Optional.empty();
	}

	public NbtElement createNumeric(Number number) {
		return new NbtDouble(number.doubleValue());
	}

	public NbtElement createByte(byte b) {
		return new NbtByte(b);
	}

	public NbtElement createShort(short s) {
		return new NbtShort(s);
	}

	public NbtElement createInt(int i) {
		return new NbtInt(i);
	}

	public NbtElement createLong(long l) {
		return new NbtLong(l);
	}

	public NbtElement createFloat(float f) {
		return new NbtFloat(f);
	}

	public NbtElement createDouble(double d) {
		return new NbtDouble(d);
	}

	public Optional<String> getStringValue(NbtElement nbtElement) {
		return nbtElement instanceof NbtString ? Optional.of(nbtElement.asString()) : Optional.empty();
	}

	public NbtElement createString(String string) {
		return new NbtString(string);
	}

	public NbtElement mergeInto(NbtElement nbtElement, NbtElement nbtElement2) {
		if (nbtElement2 instanceof NbtEnd) {
			return nbtElement;
		} else if (!(nbtElement instanceof NbtCompound)) {
			if (nbtElement instanceof NbtEnd) {
				throw new IllegalArgumentException("mergeInto called with a null input.");
			} else if (nbtElement instanceof AbstractNbtList) {
				AbstractNbtList<NbtElement> abstractNbtList = new NbtList();
				AbstractNbtList<?> abstractNbtList2 = (AbstractNbtList<?>)nbtElement;
				abstractNbtList.addAll(abstractNbtList2);
				abstractNbtList.add(nbtElement2);
				return abstractNbtList;
			} else {
				return nbtElement;
			}
		} else if (!(nbtElement2 instanceof NbtCompound)) {
			return nbtElement;
		} else {
			NbtCompound nbtCompound = new NbtCompound();
			NbtCompound nbtCompound2 = (NbtCompound)nbtElement;

			for (String string : nbtCompound2.getKeys()) {
				nbtCompound.put(string, nbtCompound2.get(string));
			}

			NbtCompound nbtCompound3 = (NbtCompound)nbtElement2;

			for (String string2 : nbtCompound3.getKeys()) {
				nbtCompound.put(string2, nbtCompound3.get(string2));
			}

			return nbtCompound;
		}
	}

	public NbtElement mergeInto(NbtElement nbtElement, NbtElement nbtElement2, NbtElement nbtElement3) {
		NbtCompound nbtCompound;
		if (nbtElement instanceof NbtEnd) {
			nbtCompound = new NbtCompound();
		} else {
			if (!(nbtElement instanceof NbtCompound)) {
				return nbtElement;
			}

			NbtCompound nbtCompound2 = (NbtCompound)nbtElement;
			nbtCompound = new NbtCompound();
			nbtCompound2.getKeys().forEach(string -> nbtCompound.put(string, nbtCompound2.get(string)));
		}

		nbtCompound.put(nbtElement2.asString(), nbtElement3);
		return nbtCompound;
	}

	public NbtElement merge(NbtElement nbtElement, NbtElement nbtElement2) {
		if (nbtElement instanceof NbtEnd) {
			return nbtElement2;
		} else if (nbtElement2 instanceof NbtEnd) {
			return nbtElement;
		} else {
			if (nbtElement instanceof NbtCompound && nbtElement2 instanceof NbtCompound) {
				NbtCompound nbtCompound = (NbtCompound)nbtElement;
				NbtCompound nbtCompound2 = (NbtCompound)nbtElement2;
				NbtCompound nbtCompound3 = new NbtCompound();
				nbtCompound.getKeys().forEach(string -> nbtCompound3.put(string, nbtCompound.get(string)));
				nbtCompound2.getKeys().forEach(string -> nbtCompound3.put(string, nbtCompound2.get(string)));
			}

			if (nbtElement instanceof AbstractNbtList && nbtElement2 instanceof AbstractNbtList) {
				NbtList nbtList = new NbtList();
				nbtList.addAll((AbstractNbtList)nbtElement);
				nbtList.addAll((AbstractNbtList)nbtElement2);
				return nbtList;
			} else {
				throw new IllegalArgumentException("Could not merge " + nbtElement + " and " + nbtElement2);
			}
		}
	}

	public Optional<Map<NbtElement, NbtElement>> getMapValues(NbtElement nbtElement) {
		if (nbtElement instanceof NbtCompound) {
			NbtCompound nbtCompound = (NbtCompound)nbtElement;
			return Optional.of(
				nbtCompound.getKeys()
					.stream()
					.map(string -> Pair.of(this.createString(string), nbtCompound.get(string)))
					.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
			);
		} else {
			return Optional.empty();
		}
	}

	public NbtElement createMap(Map<NbtElement, NbtElement> map) {
		NbtCompound nbtCompound = new NbtCompound();

		for (Entry<NbtElement, NbtElement> entry : map.entrySet()) {
			nbtCompound.put(((NbtElement)entry.getKey()).asString(), (NbtElement)entry.getValue());
		}

		return nbtCompound;
	}

	public Optional<Stream<NbtElement>> getStream(NbtElement nbtElement) {
		return nbtElement instanceof AbstractNbtList ? Optional.of(((AbstractNbtList)nbtElement).stream().map(nbtElementx -> nbtElementx)) : Optional.empty();
	}

	public Optional<ByteBuffer> getByteBuffer(NbtElement nbtElement) {
		return nbtElement instanceof NbtByteArray ? Optional.of(ByteBuffer.wrap(((NbtByteArray)nbtElement).getArray())) : super.getByteBuffer(nbtElement);
	}

	public NbtElement createByteList(ByteBuffer byteBuffer) {
		return new NbtByteArray(DataFixUtils.toArray(byteBuffer));
	}

	public Optional<IntStream> getIntStream(NbtElement nbtElement) {
		return nbtElement instanceof NbtIntArray ? Optional.of(Arrays.stream(((NbtIntArray)nbtElement).getIntArray())) : super.getIntStream(nbtElement);
	}

	public NbtElement createIntList(IntStream intStream) {
		return new NbtIntArray(intStream.toArray());
	}

	public Optional<LongStream> getLongStream(NbtElement nbtElement) {
		return nbtElement instanceof NbtLongArray ? Optional.of(Arrays.stream(((NbtLongArray)nbtElement).toArray())) : super.getLongStream(nbtElement);
	}

	public NbtElement createLongList(LongStream longStream) {
		return new NbtLongArray(longStream.toArray());
	}

	public NbtElement createList(Stream<NbtElement> stream) {
		PeekingIterator<NbtElement> peekingIterator = Iterators.peekingIterator(stream.iterator());
		if (!peekingIterator.hasNext()) {
			return new NbtList();
		} else {
			NbtElement nbtElement = (NbtElement)peekingIterator.peek();
			if (nbtElement instanceof NbtByte) {
				ArrayList<Byte> arrayList = Lists.newArrayList(Iterators.transform(peekingIterator, nbtElementx -> ((NbtByte)nbtElementx).byteValue()));
				return new NbtByteArray(arrayList);
			} else if (nbtElement instanceof NbtInt) {
				ArrayList<Integer> arrayList2 = Lists.newArrayList(Iterators.transform(peekingIterator, nbtElementx -> ((NbtInt)nbtElementx).intValue()));
				return new NbtIntArray(arrayList2);
			} else if (nbtElement instanceof NbtLong) {
				ArrayList<Long> arrayList3 = Lists.newArrayList(Iterators.transform(peekingIterator, nbtElementx -> ((NbtLong)nbtElementx).longValue()));
				return new NbtLongArray(arrayList3);
			} else {
				NbtList nbtList = new NbtList();

				while (peekingIterator.hasNext()) {
					NbtElement nbtElement2 = (NbtElement)peekingIterator.next();
					if (!(nbtElement2 instanceof NbtEnd)) {
						nbtList.add(nbtElement2);
					}
				}

				return nbtList;
			}
		}
	}

	public NbtElement remove(NbtElement nbtElement, String string) {
		if (nbtElement instanceof NbtCompound) {
			NbtCompound nbtCompound = (NbtCompound)nbtElement;
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound.getKeys().stream().filter(string2 -> !Objects.equals(string2, string)).forEach(stringx -> nbtCompound2.put(stringx, nbtCompound.get(stringx)));
			return nbtCompound2;
		} else {
			return nbtElement;
		}
	}

	public String toString() {
		return "NBT";
	}
}
