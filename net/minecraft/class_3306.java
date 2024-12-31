package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;

public class class_3306 implements ResourceReloadListener {
	public static final class_3306.class_3307<ItemStack> field_16177 = new class_3306.class_3307<>();
	public static final class_3306.class_3307<class_3286> field_16178 = new class_3306.class_3307<>();
	private final Map<class_3306.class_3307<?>, class_3304<?>> field_16179 = Maps.newHashMap();

	@Override
	public void reload(ResourceManager resourceManager) {
		for (class_3304<?> lv : this.field_16179.values()) {
			lv.method_14700();
		}
	}

	public <T> void method_14706(class_3306.class_3307<T> arg, class_3304<T> arg2) {
		this.field_16179.put(arg, arg2);
	}

	public <T> class_3308<T> method_14705(class_3306.class_3307<T> arg) {
		return (class_3308<T>)this.field_16179.get(arg);
	}

	public static class class_3307<T> {
	}
}
