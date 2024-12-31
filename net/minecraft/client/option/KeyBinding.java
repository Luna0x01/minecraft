package net.minecraft.client.option;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.collection.IntObjectStorage;
import org.lwjgl.input.Keyboard;

public class KeyBinding implements Comparable<KeyBinding> {
	private static final List<KeyBinding> KEYS = Lists.newArrayList();
	private static final IntObjectStorage<KeyBinding> KEY_MAP = new IntObjectStorage<>();
	private static final Set<String> categories = Sets.newHashSet();
	private final String translationKey;
	private final int defaultCode;
	private final String category;
	private int code;
	private boolean pressed;
	private int timesPressed;

	public static void onKeyPressed(int keyCode) {
		if (keyCode != 0) {
			KeyBinding keyBinding = KEY_MAP.get(keyCode);
			if (keyBinding != null) {
				keyBinding.timesPressed++;
			}
		}
	}

	public static void setKeyPressed(int keyCode, boolean pressed) {
		if (keyCode != 0) {
			KeyBinding keyBinding = KEY_MAP.get(keyCode);
			if (keyBinding != null) {
				keyBinding.pressed = pressed;
			}
		}
	}

	public static void method_12137() {
		for (KeyBinding keyBinding : KEYS) {
			try {
				setKeyPressed(keyBinding.code, Keyboard.isKeyDown(keyBinding.code));
			} catch (IndexOutOfBoundsException var3) {
			}
		}
	}

	public static void releaseAllKeys() {
		for (KeyBinding keyBinding : KEYS) {
			keyBinding.reset();
		}
	}

	public static void updateKeysByCode() {
		KEY_MAP.clear();

		for (KeyBinding keyBinding : KEYS) {
			KEY_MAP.set(keyBinding.code, keyBinding);
		}
	}

	public static Set<String> getCategories() {
		return categories;
	}

	public KeyBinding(String string, int i, String string2) {
		this.translationKey = string;
		this.code = i;
		this.defaultCode = i;
		this.category = string2;
		KEYS.add(this);
		KEY_MAP.set(i, this);
		categories.add(string2);
	}

	public boolean isPressed() {
		return this.pressed;
	}

	public String getCategory() {
		return this.category;
	}

	public boolean wasPressed() {
		if (this.timesPressed == 0) {
			return false;
		} else {
			this.timesPressed--;
			return true;
		}
	}

	private void reset() {
		this.timesPressed = 0;
		this.pressed = false;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public int getDefaultCode() {
		return this.defaultCode;
	}

	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int compareTo(KeyBinding keyBinding) {
		int i = I18n.translate(this.category).compareTo(I18n.translate(keyBinding.category));
		if (i == 0) {
			i = I18n.translate(this.translationKey).compareTo(I18n.translate(keyBinding.translationKey));
		}

		return i;
	}
}
