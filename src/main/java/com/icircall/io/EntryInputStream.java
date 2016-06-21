package com.icircall.io;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class EntryInputStream extends InputStream {
	private final DataInputStream in;
	private BlockInputStream current;

	public EntryInputStream(InputStream in) {
		super();
		this.in = (DataInputStream) (in instanceof DataInputStream ? in : new DataInputStream(in));
	}

	public String getNextEntry() throws IOException {
		String name = null;
		try {
			name = in.readUTF();
		} catch (EOFException e) {
			return null;
		}
		current = new BlockInputStream(in);
		return name;
	}

	@Override
	public int read() throws IOException {
		if (current == null) {
			return -1;
		}
		int i = current.read();
		if (i == -1) {
			current = null;
		}
		return i;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (current == null) {
			return -1;
		}
		int i = current.read(b, off, len);
		if (i == -1) {
			current = null;
		}
		return i;
	}

}
