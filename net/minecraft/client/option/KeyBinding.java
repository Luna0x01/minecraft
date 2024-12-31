package net.minecraft.client.option;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.class_4107;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Util;

public class KeyBinding implements Comparable<KeyBinding> {
	private static final Map<String, KeyBinding> field_15866 = Maps.newHashMap();
	private static final Map<class_4107.class_4108, KeyBinding> field_19923 = Maps.newHashMap();
	private static final Set<String> categories = Sets.newHashSet();
	private static final Map<String, Integer> field_15867 = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put("key.categories.movement", 1);
		hashMap.put("key.categories.gameplay", 2);
		hashMap.put("key.categories.inventory", 3);
		hashMap.put("key.categories.creative", 4);
		hashMap.put("key.categories.multiplayer", 5);
		hashMap.put("key.categories.ui", 6);
		hashMap.put("key.categories.misc", 7);
	});
	private final String translationKey;
	private final class_4107.class_4108 field_19924;
	private final String category;
	private class_4107.class_4108 field_19925;
	private boolean pressed;
	private int timesPressed;

	public static void method_18167(class_4107.class_4108 arg) {
		KeyBinding keyBinding = (KeyBinding)field_19923.get(arg);
		if (keyBinding != null) {
			keyBinding.timesPressed++;
		}
	}

	public static void method_18168(class_4107.class_4108 arg, boolean bl) {
		KeyBinding keyBinding = (KeyBinding)field_19923.get(arg);
		if (keyBinding != null) {
			keyBinding.pressed = bl;
		}
	}

	public static void method_12137() {
		for (KeyBinding keyBinding : field_15866.values()) {
			if (keyBinding.field_19925.method_18158() == class_4107.class_4109.KEYSYM && keyBinding.field_19925.method_18159() != -1) {
				keyBinding.pressed = class_4107.method_18154(keyBinding.field_19925.method_18159());
			}
		}
	}

	public static void releaseAllKeys() {
		for (KeyBinding keyBinding : field_15866.values()) {
			keyBinding.reset();
		}
	}

	public static void updateKeysByCode() {
		field_19923.clear();

		for (KeyBinding keyBinding : field_15866.values()) {
			field_19923.put(keyBinding.field_19925, keyBinding);
		}
	}

	public KeyBinding(String string, int i, String string2) {
		this(string, class_4107.class_4109.KEYSYM, i, string2);
	}

	public KeyBinding(String string, class_4107.class_4109 arg, int i, String string2) {
		this.translationKey = string;
		this.field_19925 = arg.method_18162(i);
		this.field_19924 = this.field_19925;
		this.category = string2;
		field_15866.put(string, this);
		field_19923.put(this.field_19925, this);
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

	public class_4107.class_4108 method_18172() {
		return this.field_19924;
	}

	public void method_18170(class_4107.class_4108 arg) {
		this.field_19925 = arg;
	}

	public int compareTo(KeyBinding keyBinding) {
		return this.category.equals(keyBinding.category)
			? I18n.translate(this.translationKey).compareTo(I18n.translate(keyBinding.translationKey))
			: ((Integer)field_15867.get(this.category)).compareTo((Integer)field_15867.get(keyBinding.category));
	}

	public static Supplier<String> method_14453(String string) {
		KeyBinding keyBinding = (KeyBinding)field_15866.get(string);
		return keyBinding == null ? () -> string : keyBinding::method_18174;
	}

	public boolean method_18171(KeyBinding keyBinding) {
		return this.field_19925.equals(keyBinding.field_19925);
	}

	public boolean method_18173() {
		return this.field_19925.equals(class_4107.field_19910);
	}

	public boolean method_18166(int i, int j) {
		return i == -1
			? this.field_19925.method_18158() == class_4107.class_4109.SCANCODE && this.field_19925.method_18159() == j
			: this.field_19925.method_18158() == class_4107.class_4109.KEYSYM && this.field_19925.method_18159() == i;
	}

	public boolean method_18165(int i) {
		return this.field_19925.method_18158() == class_4107.class_4109.MOUSE && this.field_19925.method_18159() == i;
	}

	public String method_18174() {
		return this.field_19925.method_18157();
	}

	public boolean method_18175() {
		return this.field_19925.equals(this.field_19924);
	}

	public String method_18176() {
		return this.field_19925.method_18160();
	}
}
