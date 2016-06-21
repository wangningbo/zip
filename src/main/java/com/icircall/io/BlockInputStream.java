package com.icircall.io;

import java.io.IOException;
import java.io.InputStream;

public class BlockInputStream extends InputStream {
	private final InputStream in;
	private int remaining;

	public BlockInputStream(InputStream in) throws IOException {
		super();
		this.in = in;
	}

	private boolean hasRemaining() throws IOException {
		if (remaining == 0) {
			int h = in.read();
			int l = in.read();
			if (h == -1 || l == -1) {
				remaining = -1;
			}
			remaining = h << 8 | l;
			if (remaining == 0) {
				remaining = -1;
			}
		}
		return remaining > 0;
	}

	@Override
	public int read() throws IOException {
		if (hasRemaining()) {
			remaining--;
			return in.read();
		}
		return -1;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (hasRemaining()) {
			int pos = off;
			while (len > 0 && hasRemaining()) {
				int readed = in.read(b, pos, Math.min(len, remaining));
				if (readed == 0) {
					break;
				}
				len -= readed;
				remaining -= readed;
				pos += readed;
			}
			return pos - off;
		}
		return -1;
	}

	@Override
	public boolean markSupported() {
		return false;
	}
}