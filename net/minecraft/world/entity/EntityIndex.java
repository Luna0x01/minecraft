package net.minecraft.world.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.TypeFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityIndex<T extends EntityLike> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Int2ObjectMap<T> idToEntity = new Int2ObjectLinkedOpenHashMap();
	private final Map<UUID, T> uuidToEntity = Maps.newHashMap();

	public <U extends T> void forEach(TypeFilter<T, U> filter, Consumer<U> action) {
		ObjectIterator var3 = this.idToEntity.values().iterator();

		while (var3.hasNext()) {
			T entityLike = (T)var3.next();
			U entityLike2 = (U)filter.downcast(entityLike);
			if (entityLike2 != null) {
				action.accept(entityLike2);
			}
		}
	}

	public Iterable<T> iterate() {
		return Iterables.unmodifiableIterable(this.idToEntity.values());
	}

	public void add(T entity) {
		UUID uUID = entity.getUuid();
		if (this.uuidToEntity.containsKey(uUID)) {
			LOGGER.warn("Duplicate entity UUID {}: {}", uUID, entity);
		} else {
			this.uuidToEntity.put(uUID, entity);
			this.idToEntity.put(entity.getId(), entity);
		}
	}

	public void remove(T entity) {
		this.uuidToEntity.remove(entity.getUuid());
		this.idToEntity.remove(entity.getId());
	}

	@Nullable
	public T get(int id) {
		return (T)this.idToEntity.get(id);
	}

	@Nullable
	public T get(UUID uuid) {
		return (T)this.uuidToEntity.get(uuid);
	}

	public int size() {
		return this.uuidToEntity.size();
	}
}
