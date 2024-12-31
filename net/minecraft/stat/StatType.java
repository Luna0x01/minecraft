package net.minecraft.stat;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.registry.Registry;

public class StatType<T> implements Iterable<Stat<T>> {
	private final Registry<T> registry;
	private final Map<T, Stat<T>> stats = new IdentityHashMap();

	public StatType(Registry<T> registry) {
		this.registry = registry;
	}

	public boolean hasStat(T object) {
		return this.stats.containsKey(object);
	}

	public Stat<T> getOrCreateStat(T object, StatFormatter statFormatter) {
		return (Stat<T>)this.stats.computeIfAbsent(object, objectx -> new Stat<>(this, (T)objectx, statFormatter));
	}

	public Registry<T> getRegistry() {
		return this.registry;
	}

	public Iterator<Stat<T>> iterator() {
		return this.stats.values().iterator();
	}

	public Stat<T> getOrCreateStat(T object) {
		return this.getOrCreateStat(object, StatFormatter.DEFAULT);
	}

	public String getTranslationKey() {
		return "stat_type." + Registry.field_11152.getId(this).toString().replace(':', '.');
	}
}
