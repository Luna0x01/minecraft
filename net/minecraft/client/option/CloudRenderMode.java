package net.minecraft.client.option;

public enum CloudRenderMode {
	OFF("options.off"),
	FAST("options.clouds.fast"),
	FANCY("options.clouds.fancy");

	private final String translationKey;

	private CloudRenderMode(String translationKey) {
		this.translationKey = translationKey;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}
}
