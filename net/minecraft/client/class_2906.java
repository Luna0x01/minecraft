package net.minecraft.client;

import net.minecraft.client.sound.SoundContainer;
import net.minecraft.util.Identifier;

public class class_2906 implements SoundContainer<class_2906> {
	private final Identifier field_13683;
	private final float field_13684;
	private final float field_13685;
	private final int weight;
	private final class_2906.class_1898 field_13687;
	private final boolean field_13688;

	public class_2906(String string, float f, float g, int i, class_2906.class_1898 arg, boolean bl) {
		this.field_13683 = new Identifier(string);
		this.field_13684 = f;
		this.field_13685 = g;
		this.weight = i;
		this.field_13687 = arg;
		this.field_13688 = bl;
	}

	public Identifier method_12522() {
		return this.field_13683;
	}

	public Identifier method_12523() {
		return new Identifier(this.field_13683.getNamespace(), "sounds/" + this.field_13683.getPath() + ".ogg");
	}

	public float method_12524() {
		return this.field_13684;
	}

	public float method_12525() {
		return this.field_13685;
	}

	@Override
	public int getWeight() {
		return this.weight;
	}

	public class_2906 getSound() {
		return this;
	}

	public class_2906.class_1898 method_12527() {
		return this.field_13687;
	}

	public boolean method_12528() {
		return this.field_13688;
	}

	public static enum class_1898 {
		FILE("file"),
		SOUND_EVENT("event");

		private final String field_8160;

		private class_1898(String string2) {
			this.field_8160 = string2;
		}

		public static class_2906.class_1898 method_7071(String string) {
			for (class_2906.class_1898 lv : values()) {
				if (lv.field_8160.equals(string)) {
					return lv;
				}
			}

			return null;
		}
	}
}
