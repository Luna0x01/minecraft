package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.Identifier;

public class class_2828 {
	private static final Map<Identifier, class_2829.class_2830<?>> field_13251 = Maps.newHashMap();
	private static final Map<Class<? extends class_2829>, class_2829.class_2830<?>> field_13252 = Maps.newHashMap();

	public static <T extends class_2829> void method_12099(class_2829.class_2830<? extends T> arg) {
		Identifier identifier = arg.method_12103();
		Class<T> class_ = (Class<T>)arg.method_12106();
		if (field_13251.containsKey(identifier)) {
			throw new IllegalArgumentException("Can't re-register entity property name " + identifier);
		} else if (field_13252.containsKey(class_)) {
			throw new IllegalArgumentException("Can't re-register entity property class " + class_.getName());
		} else {
			field_13251.put(identifier, arg);
			field_13252.put(class_, arg);
		}
	}

	public static class_2829.class_2830<?> method_12101(Identifier identifier) {
		class_2829.class_2830<?> lv = (class_2829.class_2830<?>)field_13251.get(identifier);
		if (lv == null) {
			throw new IllegalArgumentException("Unknown loot entity property '" + identifier + "'");
		} else {
			return lv;
		}
	}

	public static <T extends class_2829> class_2829.class_2830<T> method_12100(T arg) {
		class_2829.class_2830<?> lv = (class_2829.class_2830<?>)field_13252.get(arg.getClass());
		if (lv == null) {
			throw new IllegalArgumentException("Unknown loot entity property " + arg);
		} else {
			return (class_2829.class_2830<T>)lv;
		}
	}

	static {
		method_12099(new class_2831.class_2832());
	}
}
