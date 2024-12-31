package net.minecraft.client.resource.metadata;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.resource.ResourceMetadataProvider;

public class TextureResourceMetadata implements ResourceMetadataProvider {
	private final boolean field_6674;
	private final boolean field_6675;
	private final List<Integer> field_8134;

	public TextureResourceMetadata(boolean bl, boolean bl2, List<Integer> list) {
		this.field_6674 = bl;
		this.field_6675 = bl2;
		this.field_8134 = list;
	}

	public boolean method_5980() {
		return this.field_6674;
	}

	public boolean method_5981() {
		return this.field_6675;
	}

	public List<Integer> method_7049() {
		return Collections.unmodifiableList(this.field_8134);
	}
}
