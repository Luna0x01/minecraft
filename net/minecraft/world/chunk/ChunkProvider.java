package net.minecraft.world.chunk;

import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.class_3781;
import net.minecraft.server.world.ChunkGenerator;

public interface ChunkProvider extends AutoCloseable {
	@Nullable
	Chunk method_17044(int i, int j, boolean bl, boolean bl2);

	@Nullable
	default class_3781 method_17043(int i, int j, boolean bl) {
		Chunk chunk = this.method_17044(i, j, true, false);
		if (chunk == null && bl) {
			throw new UnsupportedOperationException("Could not create an empty chunk");
		} else {
			return chunk;
		}
	}

	boolean method_17045(BooleanSupplier booleanSupplier);

	String getChunkProviderName();

	ChunkGenerator<?> method_17046();

	default void close() {
	}
}
