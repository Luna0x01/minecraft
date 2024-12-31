package net.minecraft.client.resource.language;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Language;
import net.minecraft.util.MetadataSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageManager implements ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MetadataSerializer serializer;
	private String currentLanguageCode;
	protected static final TranslationStorage translationStorage = new TranslationStorage();
	private Map<String, LanguageDefinition> languageDefs = Maps.newHashMap();

	public LanguageManager(MetadataSerializer metadataSerializer, String string) {
		this.serializer = metadataSerializer;
		this.currentLanguageCode = string;
		I18n.setTranslationStorage(translationStorage);
	}

	public void reloadResourceLanguages(List<ResourcePack> resourcePacks) {
		this.languageDefs.clear();

		for (ResourcePack resourcePack : resourcePacks) {
			try {
				LanguageResourceMetadata languageResourceMetadata = resourcePack.parseMetadata(this.serializer, "language");
				if (languageResourceMetadata != null) {
					for (LanguageDefinition languageDefinition : languageResourceMetadata.getLanguageDefinitions()) {
						if (!this.languageDefs.containsKey(languageDefinition.getCode())) {
							this.languageDefs.put(languageDefinition.getCode(), languageDefinition);
						}
					}
				}
			} catch (RuntimeException var7) {
				LOGGER.warn("Unable to parse metadata section of resourcepack: " + resourcePack.getName(), var7);
			} catch (IOException var8) {
				LOGGER.warn("Unable to parse metadata section of resourcepack: " + resourcePack.getName(), var8);
			}
		}
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		List<String> list = Lists.newArrayList(new String[]{"en_US"});
		if (!"en_US".equals(this.currentLanguageCode)) {
			list.add(this.currentLanguageCode);
		}

		translationStorage.load(resourceManager, list);
		Language.load(translationStorage.translations);
	}

	public boolean forcesUnicodeFont() {
		return translationStorage.isRightToLeft();
	}

	public boolean isRightToLeft() {
		return this.getLanguage() != null && this.getLanguage().isRightToLeft();
	}

	public void setLanguage(LanguageDefinition language) {
		this.currentLanguageCode = language.getCode();
	}

	public LanguageDefinition getLanguage() {
		return this.languageDefs.containsKey(this.currentLanguageCode)
			? (LanguageDefinition)this.languageDefs.get(this.currentLanguageCode)
			: (LanguageDefinition)this.languageDefs.get("en_US");
	}

	public SortedSet<LanguageDefinition> getAllLanguages() {
		return Sets.newTreeSet(this.languageDefs.values());
	}
}
