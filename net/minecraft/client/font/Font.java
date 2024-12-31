package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Closeable;
import javax.annotation.Nullable;

public interface Font extends Closeable {
	default void close() {
	}

	@Nullable
	default RenderableGlyph getGlyph(int codePoint) {
		return null;
	}

	IntSet getProvidedGlyphs();
}
