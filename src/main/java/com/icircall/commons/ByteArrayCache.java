package com.icircall.commons;

public class ByteArrayCache extends ThreadLocalCache<byte[]> {
	private static final ByteArrayCache instance = new ByteArrayCache(1024);
	private final int defaultLength;

	private ByteArrayCache(int defaultLength) {
		super();
		this.defaultLength = defaultLength;
	}

	public static byte[] get(int minLength) {
		byte[] ret = instance.getCached();
		if (minLength <= ret.length) {
			return ret;
		}
		return new byte[minLength];
	}

	public static byte[] get() {
		return instance.getCached();
	}

	@Override
	protected byte[] createCache() {
		return new byte[defaultLength];
	}

}
