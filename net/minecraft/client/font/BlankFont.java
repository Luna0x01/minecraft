package net.minecraft.client.font;

import javax.annotation.Nullable;

public class BlankFont implements Font {
	@Nullable
	@Override
	public RenderableGlyph getGlyph(char c) {
		return BlankGlyph.field_2283;
	}
}
