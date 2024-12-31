package net.minecraft.util.collection;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class IdList<T> implements ObjectIdIterable<T> {
	private final IdentityHashMap<T, Integer> idMap;
	private final List<T> list;

	public IdList() {
		this(512);
	}

	public IdList(int i) {
		this.list = Lists.newArrayListWithExpectedSize(i);
		this.idMap = new IdentityHashMap(i);
	}

	public void set(T value, int index) {
		this.idMap.put(value, index);

		while (this.list.size() <= index) {
			this.list.add(null);
		}

		this.list.set(index, value);
	}

	public int getId(T value) {
		Integer integer = (Integer)this.idMap.get(value);
		return integer == null ? -1 : integer;
	}

	@Nullable
	public final T fromId(int index) {
		return (T)(index >= 0 && index < this.list.size() ? this.list.get(index) : null);
	}

	public Iterator<T> iterator() {
		return Iterators.filter(this.list.iterator(), Predicates.notNull());
	}

	public int size() {
		return this.idMap.size();
	}
}
