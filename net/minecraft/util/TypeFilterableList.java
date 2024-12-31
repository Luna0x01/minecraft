package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TypeFilterableList<T> extends AbstractSet<T> {
	private static final Set<Class<?>> field_11829 = Sets.newHashSet();
	private final Map<Class<?>, List<T>> elementsByType = Maps.newHashMap();
	private final Set<Class<?>> field_11831 = Sets.newIdentityHashSet();
	private final Class<T> elementType;
	private final List<T> allElements = Lists.newArrayList();

	public TypeFilterableList(Class<T> class_) {
		this.elementType = class_;
		this.field_11831.add(class_);
		this.elementsByType.put(class_, this.allElements);

		for (Class<?> class2 : field_11829) {
			this.method_10802(class2);
		}
	}

	protected void method_10802(Class<?> class_) {
		field_11829.add(class_);

		for (T object : this.allElements) {
			if (class_.isAssignableFrom(object.getClass())) {
				this.method_10803(object, class_);
			}
		}

		this.field_11831.add(class_);
	}

	protected Class<?> method_10805(Class<?> class_) {
		if (this.elementType.isAssignableFrom(class_)) {
			if (!this.field_11831.contains(class_)) {
				this.method_10802(class_);
			}

			return class_;
		} else {
			throw new IllegalArgumentException("Don't know how to search for " + class_);
		}
	}

	public boolean add(T object) {
		for (Class<?> class_ : this.field_11831) {
			if (class_.isAssignableFrom(object.getClass())) {
				this.method_10803(object, class_);
			}
		}

		return true;
	}

	private void method_10803(T object, Class<?> class_) {
		List<T> list = (List<T>)this.elementsByType.get(class_);
		if (list == null) {
			this.elementsByType.put(class_, Lists.newArrayList(new Object[]{object}));
		} else {
			list.add(object);
		}
	}

	public boolean remove(Object object) {
		T object2 = (T)object;
		boolean bl = false;

		for (Class<?> class_ : this.field_11831) {
			if (class_.isAssignableFrom(object2.getClass())) {
				List<T> list = (List<T>)this.elementsByType.get(class_);
				if (list != null && list.remove(object2)) {
					bl = true;
				}
			}
		}

		return bl;
	}

	public boolean contains(Object object) {
		return Iterators.contains(this.method_10806(object.getClass()).iterator(), object);
	}

	public <S> Iterable<S> method_10806(Class<S> class_) {
		return new Iterable<S>() {
			public Iterator<S> iterator() {
				List<T> list = (List<T>)TypeFilterableList.this.elementsByType.get(TypeFilterableList.this.method_10805(class_));
				if (list == null) {
					return Iterators.emptyIterator();
				} else {
					Iterator<T> iterator = list.iterator();
					return Iterators.filter(iterator, class_);
				}
			}
		};
	}

	public Iterator<T> iterator() {
		return this.allElements.isEmpty() ? Iterators.emptyIterator() : Iterators.unmodifiableIterator(this.allElements.iterator());
	}

	public int size() {
		return this.allElements.size();
	}
}
