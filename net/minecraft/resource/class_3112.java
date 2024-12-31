package net.minecraft.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.MetadataSerializer;

public class class_3112 implements ResourcePack {
	private final ResourcePack field_15322;

	public class_3112(ResourcePack resourcePack) {
		this.field_15322 = resourcePack;
	}

	@Override
	public InputStream open(Identifier id) throws IOException {
		return this.field_15322.open(this.method_13887(id));
	}

	private Identifier method_13887(Identifier identifier) {
		String string = identifier.getPath();
		if (!"lang/swg_de.lang".equals(string) && string.startsWith("lang/") && string.endsWith(".lang")) {
			int i = string.indexOf(95);
			if (i != -1) {
				final String string2 = string.substring(0, i + 1) + string.substring(i + 1, string.indexOf(46, i)).toUpperCase() + ".lang";
				return new Identifier(identifier.getNamespace(), "") {
					@Override
					public String getPath() {
						return string2;
					}
				};
			}
		}

		return identifier;
	}

	@Override
	public boolean contains(Identifier id) {
		return this.field_15322.contains(this.method_13887(id));
	}

	@Override
	public Set<String> getNamespaces() {
		return this.field_15322.getNamespaces();
	}

	@Nullable
	@Override
	public <T extends ResourceMetadataProvider> T parseMetadata(MetadataSerializer serializer, String key) throws IOException {
		return this.field_15322.parseMetadata(serializer, key);
	}

	@Override
	public BufferedImage getIcon() throws IOException {
		return this.field_15322.getIcon();
	}

	@Override
	public String getName() {
		return this.field_15322.getName();
	}
}
