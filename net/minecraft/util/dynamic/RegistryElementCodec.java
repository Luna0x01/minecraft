package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public final class RegistryElementCodec<E> implements Codec<Supplier<E>> {
	private final RegistryKey<? extends Registry<E>> registryRef;
	private final Codec<E> elementCodec;
	private final boolean allowInlineDefinitions;

	public static <E> RegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
		return of(registryRef, elementCodec, true);
	}

	public static <E> Codec<List<Supplier<E>>> method_31194(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
		return Codec.either(of(registryRef, elementCodec, false).listOf(), elementCodec.xmap(object -> () -> object, Supplier::get).listOf())
			.xmap(either -> (List)either.map(list -> list, list -> list), Either::left);
	}

	private static <E> RegistryElementCodec<E> of(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean allowInlineDefinitions) {
		return new RegistryElementCodec<>(registryRef, elementCodec, allowInlineDefinitions);
	}

	private RegistryElementCodec(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean allowInlineDefinitions) {
		this.registryRef = registryRef;
		this.elementCodec = elementCodec;
		this.allowInlineDefinitions = allowInlineDefinitions;
	}

	public <T> DataResult<T> encode(Supplier<E> supplier, DynamicOps<T> dynamicOps, T object) {
		return dynamicOps instanceof RegistryReadingOps
			? ((RegistryReadingOps)dynamicOps).encodeOrId(supplier.get(), object, this.registryRef, this.elementCodec)
			: this.elementCodec.encode(supplier.get(), dynamicOps, object);
	}

	public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> ops, T input) {
		return ops instanceof RegistryOps
			? ((RegistryOps)ops).decodeOrId(input, this.registryRef, this.elementCodec, this.allowInlineDefinitions)
			: this.elementCodec.decode(ops, input).map(pair -> pair.mapFirst(object -> () -> object));
	}

	public String toString() {
		return "RegistryFileCodec[" + this.registryRef + " " + this.elementCodec + "]";
	}
}
