package net.minecraft.world.gen.chunk;

import java.util.function.Supplier;
import net.minecraft.class_3784;
import net.minecraft.class_3798;
import net.minecraft.class_3799;
import net.minecraft.class_3807;
import net.minecraft.class_3808;
import net.minecraft.class_3809;
import net.minecraft.class_3811;
import net.minecraft.class_3917;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.chunk.DebugChunkGenerator;
import net.minecraft.world.chunk.EndChunkGenerator;
import net.minecraft.world.chunk.FlatChunkGenerator;
import net.minecraft.world.chunk.SurfaceChunkGenerator;

public class ChunkGeneratorType<C extends class_3798, T extends ChunkGenerator<C>> implements class_3784<C, T> {
	public static final ChunkGeneratorType<class_3809, SurfaceChunkGenerator> SURFACE = method_17039("surface", SurfaceChunkGenerator::new, class_3809::new, true);
	public static final ChunkGeneratorType<class_3807, class_3808> CAVES = method_17039("caves", class_3808::new, class_3807::new, true);
	public static final ChunkGeneratorType<class_3811, EndChunkGenerator> FLOATING_ISLANDS = method_17039(
		"floating_islands", EndChunkGenerator::new, class_3811::new, true
	);
	public static final ChunkGeneratorType<class_3799, DebugChunkGenerator> DEBUG = method_17039("debug", DebugChunkGenerator::new, class_3799::new, false);
	public static final ChunkGeneratorType<class_3917, FlatChunkGenerator> FLAT = method_17039("flat", FlatChunkGenerator::new, class_3917::new, false);
	private final Identifier field_18852;
	private final class_3784<C, T> field_18853;
	private final boolean field_18854;
	private final Supplier<C> field_18855;

	public static void method_17038() {
	}

	public ChunkGeneratorType(class_3784<C, T> arg, boolean bl, Supplier<C> supplier, Identifier identifier) {
		this.field_18853 = arg;
		this.field_18854 = bl;
		this.field_18855 = supplier;
		this.field_18852 = identifier;
	}

	public static <C extends class_3798, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> method_17039(
		String string, class_3784<C, T> arg, Supplier<C> supplier, boolean bl
	) {
		Identifier identifier = new Identifier(string);
		ChunkGeneratorType<C, T> chunkGeneratorType = new ChunkGeneratorType<>(arg, bl, supplier, identifier);
		Registry.CHUNK_GENERATOR_TYPE.add(identifier, chunkGeneratorType);
		return chunkGeneratorType;
	}

	@Override
	public T create(World world, SingletonBiomeSource singletonBiomeSource, C arg) {
		return this.field_18853.create(world, singletonBiomeSource, arg);
	}

	public C method_17040() {
		return (C)this.field_18855.get();
	}

	public boolean method_17041() {
		return this.field_18854;
	}

	public Identifier method_17042() {
		return this.field_18852;
	}
}
