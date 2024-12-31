package net.minecraft.client;

import java.io.File;
import net.minecraft.client.resource.AssetsIndex;
import net.minecraft.util.Identifier;

public class class_2902 extends AssetsIndex {
	private final File field_13655;

	public class_2902(File file) {
		this.field_13655 = file;
	}

	@Override
	public File method_12496(Identifier identifier) {
		return new File(this.field_13655, identifier.toString().replace(':', '/'));
	}

	@Override
	public File method_12495() {
		return new File(this.field_13655, "pack.mcmeta");
	}
}
