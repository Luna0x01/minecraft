package net.minecraft.client.option;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.collection.IntObjectStorage;
import org.lwjgl.input.Keyboard;

public class KeyBinding implements Comparable<KeyBinding> {
	private static final Map<String, KeyBinding> field_15866 = Maps.newHashMap();
	private static final IntObjectStorage<KeyBinding> KEY_MAP = new IntObjectStorage<>();
	private static final Set<String> categories = Sets.newHashSet();
	private static final Map<String, Integer> field_15867 = Maps.newHashMap();
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
		for (KeyBinding keyBinding : field_15866.values()) {
			try {
				setKeyPressed(keyBinding.code, keyBinding.code < 256 && Keyboard.isKeyDown(keyBinding.code));
			} catch (IndexOutOfBoundsException var3) {
			}
		}
	}

	public static void releaseAllKeys() {
		for (KeyBinding keyBinding : field_15866.values()) {
			keyBinding.reset();
		}
	}

	public static void updateKeysByCode() {
		KEY_MAP.clear();

		for (KeyBinding keyBinding : field_15866.values()) {
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
		field_15866.put(string, this);
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
		return this.category.equals(keyBinding.category)
			? I18n.translate(this.translationKey).compareTo(I18n.translate(keyBinding.translationKey))
			: ((Integer)field_15867.get(this.category)).compareTo((Integer)field_15867.get(keyBinding.category));
	}

	public static Supplier<String> method_14453(String string) {
		KeyBinding keyBinding = (KeyBinding)field_15866.get(string);
		return keyBinding == null ? () -> string : () -> GameOptions.getFormattedNameForKeyCode(keyBinding.getCode());
	}

	static {
		field_15867.put("key.categories.movement", 1);
		field_15867.put("key.categories.gameplay", 2);
		field_15867.put("key.categories.inventory", 3);
		field_15867.put("key.categories.creative", 4);
		field_15867.put("key.categories.multiplayer", 5);
		field_15867.put("key.categories.ui", 6);
		field_15867.put("key.categories.misc", 7);
	}
}
