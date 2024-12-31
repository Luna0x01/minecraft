package net.minecraft.client.font;

import java.io.Closeable;
import javax.annotation.Nullable;

public interface Font extends Closeable {
	default void close() {
	}

	@Nullable
	default RenderableGlyph getGlyph(char c) {
		return null;
	}
}
