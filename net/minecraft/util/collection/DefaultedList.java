package net.minecraft.util.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class DefaultedList<E> extends AbstractList<E> {
	private final List<E> delegate;
	private final E initialElement;

	public static <E> DefaultedList<E> of() {
		return new DefaultedList<>();
	}

	public static <E> DefaultedList<E> ofSize(int size, E defaultValue) {
		Validate.notNull(defaultValue);
		Object[] objects = new Object[size];
		Arrays.fill(objects, defaultValue);
		return new DefaultedList<>(Arrays.asList(objects), defaultValue);
	}

	public static <E> DefaultedList<E> copyOf(E defaultValue, E... values) {
		return new DefaultedList<>(Arrays.asList(values), defaultValue);
	}

	protected DefaultedList() {
		this(new ArrayList(), null);
	}

	protected DefaultedList(List<E> list, @Nullable E object) {
		this.delegate = list;
		this.initialElement = object;
	}

	@Nonnull
	public E get(int i) {
		return (E)this.delegate.get(i);
	}

	public E set(int i, E object) {
		Validate.notNull(object);
		return (E)this.delegate.set(i, object);
	}

	public void add(int i, E object) {
		Validate.notNull(object);
		this.delegate.add(i, object);
	}

	public E remove(int i) {
		return (E)this.delegate.remove(i);
	}

	public int size() {
		return this.delegate.size();
	}

	public void clear() {
		if (this.initialElement == null) {
			super.clear();
		} else {
			for (int i = 0; i < this.size(); i++) {
				this.set(i, this.initialElement);
			}
		}
	}
}
