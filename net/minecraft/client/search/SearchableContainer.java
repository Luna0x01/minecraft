package net.minecraft.client.search;

public interface SearchableContainer<T> extends Searchable<T> {
	void add(T object);

	void clear();

	void reload();
}
