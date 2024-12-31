package net.minecraft.client.search;

import java.util.List;

public interface Searchable<T> {
	List<T> findAll(String string);
}
