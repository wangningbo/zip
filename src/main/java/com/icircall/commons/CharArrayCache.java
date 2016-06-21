package com.icircall.commons;

public class CharArrayCache extends ThreadLocalCache<char[]> {
	private static final CharArrayCache instance = new CharArrayCache(1024);
	private final int defaultLength;

	private CharArrayCache(int defaultLength) {
		super();
		this.defaultLength = defaultLength;
	}

	public static char[] get(int minLength) {
		char[] ret = instance.getCached();
		if (minLength <= ret.length) {
			return ret;
		}
		return new char[minLength];
	}
	
	public static char[] get() {
		return instance.getCached();
	}

	@Override
	protected char[] createCache() {
		return new char[defaultLength];
	}

}
