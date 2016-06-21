package com.icircall.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class EntryOutputStream extends OutputStream {
	private Set<String> names = new HashSet<>();
	private final DataOutputStream out;
	private BlockOutputStream current;

	public EntryOutputStream(OutputStream out) {
		super();
		this.out = new DataOutputStream(out);
	}

	public void putNextEntry(String name) throws IOException {
		if (!names.add(name)) {
			throw new IOException("entry already exists:" + name);
		}
		closeEntry();
		out.writeUTF(name);
		current = new BlockOutputStream(out);
	}

	public void closeEntry() throws IOException {
		if (current == null) {
			return;
		}
		current.end();
		current = null;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (current == null) {
			throw new IOException("no entry");
		}
		current.write(b, off, len);
	}

	@Override
	public void write(int b) throws IOException {
		if (current == null) {
			throw new IOException("no entry");
		}
		current.write(b);
	}

	@Override
	public void flush() throws IOException {
		if (current != null) {
			current.flush();
		}
	}

	@Override
	public void close() throws IOException {
		closeEntry();
		out.close();
	}

}
