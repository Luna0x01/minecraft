package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface StringIdentifiable {
	String asString();

	static <E extends Enum<E> & StringIdentifiable> Codec<E> createCodec(Supplier<E[]> enumValues, Function<? super String, ? extends E> fromString) {
		E[] enums = (E[])enumValues.get();
		return createCodec(Enum::ordinal, ordinal -> enums[ordinal], fromString);
	}

	static <E extends StringIdentifiable> Codec<E> createCodec(
		ToIntFunction<E> compressedEncoder, IntFunction<E> compressedDecoder, Function<? super String, ? extends E> decoder
	) {
		return new Codec<E>() {
			public <T> DataResult<T> encode(E stringIdentifiable, DynamicOps<T> dynamicOps, T object) {
				return dynamicOps.compressMaps()
					? dynamicOps.mergeToPrimitive(object, dynamicOps.createInt(compressedEncoder.applyAsInt(stringIdentifiable)))
					: dynamicOps.mergeToPrimitive(object, dynamicOps.createString(stringIdentifiable.asString()));
			}

			public <T> DataResult<com.mojang.datafixers.util.Pair<E, T>> decode(DynamicOps<T> dynamicOps, T object) {
				return dynamicOps.compressMaps()
					? dynamicOps.getNumberValue(object)
						.flatMap(
							number -> (DataResult)Optional.ofNullable(compressedDecoder.apply(number.intValue()))
									.map(DataResult::success)
									.orElseGet(() -> DataResult.error("Unknown element id: " + number))
						)
						.map(stringIdentifiable -> com.mojang.datafixers.util.Pair.of(stringIdentifiable, dynamicOps.empty()))
					: dynamicOps.getStringValue(object)
						.flatMap(
							string -> (DataResult)Optional.ofNullable(decoder.apply(string))
									.map(DataResult::success)
									.orElseGet(() -> DataResult.error("Unknown element name: " + string))
						)
						.map(stringIdentifiable -> com.mojang.datafixers.util.Pair.of(stringIdentifiable, dynamicOps.empty()));
			}

			public String toString() {
				return "StringRepresentable[" + compressedEncoder + "]";
			}
		};
	}

	static Keyable method_28142(StringIdentifiable[] stringIdentifiables) {
		return new Keyable() {
			public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
				return dynamicOps.compressMaps()
					? IntStream.range(0, stringIdentifiables.length).mapToObj(dynamicOps::createInt)
					: Arrays.stream(stringIdentifiables).map(StringIdentifiable::asString).map(dynamicOps::createString);
			}
		};
	}
}
