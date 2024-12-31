package net.minecraft.world.biome;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_3611;
import net.minecraft.class_3616;
import net.minecraft.class_3617;
import net.minecraft.class_3632;
import net.minecraft.class_3633;
import net.minecraft.class_3659;
import net.minecraft.class_3660;
import net.minecraft.class_3679;
import net.minecraft.class_3680;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BiomeSourceType<C extends class_3611, T extends SingletonBiomeSource> {
	public static final BiomeSourceType<class_3617, class_3616> CHECKERBOARD = method_16485("checkerboard", class_3616::new, class_3617::new);
	public static final BiomeSourceType<class_3633, class_3632> FIXED = method_16485("fixed", class_3632::new, class_3633::new);
	public static final BiomeSourceType<class_3660, class_3659> VANILLA_LAYERED = method_16485("vanilla_layered", class_3659::new, class_3660::new);
	public static final BiomeSourceType<class_3680, class_3679> THE_END = method_16485("the_end", class_3679::new, class_3680::new);
	private final Identifier field_17668;
	private final Function<C, T> field_17669;
	private final Supplier<C> field_17670;

	public static void method_16483() {
	}

	public BiomeSourceType(Function<C, T> function, Supplier<C> supplier, Identifier identifier) {
		this.field_17669 = function;
		this.field_17670 = supplier;
		this.field_17668 = identifier;
	}

	public static <C extends class_3611, T extends SingletonBiomeSource> BiomeSourceType<C, T> method_16485(
		String string, Function<C, T> function, Supplier<C> supplier
	) {
		Identifier identifier = new Identifier(string);
		BiomeSourceType<C, T> biomeSourceType = new BiomeSourceType<>(function, supplier, identifier);
		Registry.BIOME_SOURCE_TYPE.add(identifier, biomeSourceType);
		return biomeSourceType;
	}

	public T method_16484(C arg) {
		return (T)this.field_17669.apply(arg);
	}

	public C method_16486() {
		return (C)this.field_17670.get();
	}

	public Identifier method_16487() {
		return this.field_17668;
	}
}
