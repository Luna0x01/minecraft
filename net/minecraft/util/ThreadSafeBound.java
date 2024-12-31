package net.minecraft.util;

import java.lang.reflect.Array;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeBound<T> {
	private final T[] data;
	private final Class<? extends T> clazz;
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private int lastReadIndex;
	private int lastWrittenIndex;

	public ThreadSafeBound(Class<? extends T> class_, int i) {
		this.clazz = class_;
		this.data = (T[])((Object[])Array.newInstance(class_, i));
	}

	public T add(T item) {
		this.readWriteLock.writeLock().lock();
		this.data[this.lastWrittenIndex] = item;
		this.lastWrittenIndex = (this.lastWrittenIndex + 1) % this.size();
		if (this.lastReadIndex < this.size()) {
			this.lastReadIndex++;
		}

		this.readWriteLock.writeLock().unlock();
		return item;
	}

	public int size() {
		this.readWriteLock.readLock().lock();
		int i = this.data.length;
		this.readWriteLock.readLock().unlock();
		return i;
	}

	public T[] copyAndGetData() {
		T[] objects = (T[])((Object[])Array.newInstance(this.clazz, this.lastReadIndex));
		this.readWriteLock.readLock().lock();

		for (int i = 0; i < this.lastReadIndex; i++) {
			int j = (this.lastWrittenIndex - this.lastReadIndex + i) % this.size();
			if (j < 0) {
				j += this.size();
			}

			objects[i] = this.data[j];
		}

		this.readWriteLock.readLock().unlock();
		return objects;
	}
}
