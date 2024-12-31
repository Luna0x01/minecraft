package net.minecraft.client.resource.metadata;

import java.util.Collection;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.client.resource.language.LanguageDefinition;

public class LanguageResourceMetadata implements ResourceMetadataProvider {
	private final Collection<LanguageDefinition> definitions;

	public LanguageResourceMetadata(Collection<LanguageDefinition> collection) {
		this.definitions = collection;
	}

	public Collection<LanguageDefinition> getLanguageDefinitions() {
		return this.definitions;
	}
}
