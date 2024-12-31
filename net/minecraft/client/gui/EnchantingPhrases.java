package net.minecraft.client.gui;

import java.util.Random;

public class EnchantingPhrases {
	private static final EnchantingPhrases INSTANCE = new EnchantingPhrases();
	private Random random = new Random();
	private String[] phrases = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale "
		.split(" ");

	private EnchantingPhrases() {
	}

	public static EnchantingPhrases getInstance() {
		return INSTANCE;
	}

	public String getRandomString() {
		int i = this.random.nextInt(2) + 3;
		String string = "";

		for (int j = 0; j < i; j++) {
			if (j > 0) {
				string = string + " ";
			}

			string = string + this.phrases[this.random.nextInt(this.phrases.length)];
		}

		return string;
	}

	public void setSeed(long seed) {
		this.random.setSeed(seed);
	}
}
