package com.icircall.commons;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

public abstract class ThreadLocalCache<T> {
	private ThreadLocal<CacheHolder> threadLocal = new ThreadLocal() {
		@Override
		protected CacheHolder initialValue() {
			return new CacheHolder();
		}
	};

	public T getCached() {
		return threadLocal.get().get();
	}

	protected void set(T cache) {
		threadLocal.get().set(cache);
	}

	private class CacheHolder {
		SoftReference<T> ref;

		public T get() {
			T ret;
			if (ref == null || (ret = ref.get()) == null) {
				ret = createCache();
				ref = new SoftReference(ret);
			}
			return ret;
		}

		public void set(T value) {
			ref = new SoftReference(value);
		}

	}

	protected abstract T createCache();
}
