package net.minecraft.world.entity;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

public class SimpleEntityLookup<T extends EntityLike> implements EntityLookup<T> {
	private final EntityIndex<T> index;
	private final SectionedEntityCache<T> cache;

	public SimpleEntityLookup(EntityIndex<T> index, SectionedEntityCache<T> cache) {
		this.index = index;
		this.cache = cache;
	}

	@Nullable
	@Override
	public T get(int id) {
		return this.index.get(id);
	}

	@Nullable
	@Override
	public T get(UUID uuid) {
		return this.index.get(uuid);
	}

	@Override
	public Iterable<T> iterate() {
		return this.index.iterate();
	}

	@Override
	public <U extends T> void forEach(TypeFilter<T, U> filter, Consumer<U> action) {
		this.index.forEach(filter, action);
	}

	@Override
	public void forEachIntersects(Box box, Consumer<T> action) {
		this.cache.forEachIntersects(box, action);
	}

	@Override
	public <U extends T> void forEachIntersects(TypeFilter<T, U> filter, Box box, Consumer<U> action) {
		this.cache.forEachIntersects(filter, box, action);
	}
}
