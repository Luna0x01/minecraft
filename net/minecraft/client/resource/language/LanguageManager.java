package net.minecraft.client.resource.language;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.class_4454;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageManager implements ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	protected static final TranslationStorage translationStorage = new TranslationStorage();
	private String currentLanguageCode;
	private final Map<String, LanguageDefinition> languageDefs = Maps.newHashMap();

	public LanguageManager(String string) {
		this.currentLanguageCode = string;
		I18n.setTranslationStorage(translationStorage);
	}

	public void reloadResourceLanguages(List<class_4454> resourcePacks) {
		this.languageDefs.clear();

		for (class_4454 lv : resourcePacks) {
			try {
				LanguageResourceMetadata languageResourceMetadata = lv.method_21329(LanguageResourceMetadata.field_21049);
				if (languageResourceMetadata != null) {
					for (LanguageDefinition languageDefinition : languageResourceMetadata.getLanguageDefinitions()) {
						if (!this.languageDefs.containsKey(languageDefinition.getCode())) {
							this.languageDefs.put(languageDefinition.getCode(), languageDefinition);
						}
					}
				}
			} catch (IOException | RuntimeException var7) {
				LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", lv.method_5899(), var7);
			}
		}
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		List<String> list = Lists.newArrayList(new String[]{"en_us"});
		if (!"en_us".equals(this.currentLanguageCode)) {
			list.add(this.currentLanguageCode);
		}

		translationStorage.method_19557(resourceManager, list);
		Language.load(translationStorage.translations);
	}

	public boolean isRightToLeft() {
		return this.getLanguage() != null && this.getLanguage().isRightToLeft();
	}

	public void setLanguage(LanguageDefinition language) {
		this.currentLanguageCode = language.getCode();
	}

	public LanguageDefinition getLanguage() {
		String string = this.languageDefs.containsKey(this.currentLanguageCode) ? this.currentLanguageCode : "en_us";
		return (LanguageDefinition)this.languageDefs.get(string);
	}

	public SortedSet<LanguageDefinition> getAllLanguages() {
		return Sets.newTreeSet(this.languageDefs.values());
	}

	public LanguageDefinition method_14698(String string) {
		return (LanguageDefinition)this.languageDefs.get(string);
	}
}
