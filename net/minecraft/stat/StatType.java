package net.minecraft.stat;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.class_4472;
import net.minecraft.class_4473;
import net.minecraft.util.registry.Registry;

public class StatType<T> implements Iterable<class_4472<T>> {
	private final Registry<T> field_22072;
	private final Map<T, class_4472<T>> field_22073 = new IdentityHashMap();

	public StatType(Registry<T> registry) {
		this.field_22072 = registry;
	}

	public boolean method_21425(T object) {
		return this.field_22073.containsKey(object);
	}

	public class_4472<T> method_21426(T object, class_4473 arg) {
		return (class_4472<T>)this.field_22073.computeIfAbsent(object, objectx -> new class_4472<>(this, (T)objectx, arg));
	}

	public Registry<T> method_21424() {
		return this.field_22072;
	}

	public int method_21428() {
		return this.field_22073.size();
	}

	public Iterator<class_4472<T>> iterator() {
		return this.field_22073.values().iterator();
	}

	public class_4472<T> method_21429(T object) {
		return this.method_21426(object, class_4473.DEFAULT);
	}

	public String method_21430() {
		return "stat_type." + Registry.STATS.getId(this).toString().replace(':', '.');
	}
}
