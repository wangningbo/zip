package com.icircall.io;

import java.io.IOException;
import java.io.OutputStream;

public class BlockOutputStream extends OutputStream {
	private byte[] buf;
	private int pos;
	private OutputStream out;

	public BlockOutputStream(OutputStream out, int maxBlockSize) {
		this.out = out;
		if (maxBlockSize > 0xffff || maxBlockSize <= 2) {
			throw new IllegalArgumentException();
		}
		buf = new byte[maxBlockSize];
		pos = 2;
	}

	public BlockOutputStream(OutputStream out) {
		this(out, 1024);
	}

	@Override
	public void write(int b) throws IOException {
		buf[pos++] = (byte) b;
		flushBuf(false);
	}

	private void flushBuf(boolean force) throws IOException {
		if (pos == buf.length || (force && pos > 2)) {
			buf[0] = (byte) ((pos - 2) >>> 8 & 0xff);
			buf[1] = (byte) ((pos - 2) & 0xff);
			out.write(buf, 0, pos);
			pos = 2;
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		while (len-- > 0) {
			buf[pos++] = b[off++];
			flushBuf(false);
		}
	}

	public void end() throws IOException {
		flushBuf(true);
		buf[0] = 0;
		buf[1] = 0;
		out.write(buf, 0, 2);
	}

	@Override
	public void flush() throws IOException {
		flushBuf(true);
		out.flush();
	}

	public void close() throws IOException {
		end();
		out.close();
	}
}