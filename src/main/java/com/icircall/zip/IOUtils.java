package com.icircall.zip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
	public static void writeShort(OutputStream out, short v) throws IOException {
		out.write((v >>> 0) & 0xff);
		out.write((v >>> 8) & 0xff);
	}

	public static void writeInt(OutputStream out, int v) throws IOException {
		out.write((int) ((v >>> 0) & 0xff));
		out.write((int) ((v >>> 8) & 0xff));
		out.write((int) ((v >>> 16) & 0xff));
		out.write((int) ((v >>> 24) & 0xff));
	}

	public static void writeLong(OutputStream out, long v) throws IOException {
		out.write((int) ((v >>> 0) & 0xff));
		out.write((int) ((v >>> 8) & 0xff));
		out.write((int) ((v >>> 16) & 0xff));
		out.write((int) ((v >>> 24) & 0xff));
		out.write((int) ((v >>> 32) & 0xff));
		out.write((int) ((v >>> 40) & 0xff));
		out.write((int) ((v >>> 48) & 0xff));
		out.write((int) ((v >>> 56) & 0xff));
	}

	public static short readShort(InputStream in) throws IOException {
		return (short) (in.read() | (in.read() << 8));
	}

	public static int readInt(InputStream in) throws IOException {
		return in.read() | (in.read() << 8) | (in.read() << 16) | (in.read() << 24);
	}

	public static long readLong(InputStream in) throws IOException {
		long l = in.read() | (in.read() << 8) | (in.read() << 16) | (in.read() << 24);
		long h = in.read() | (in.read() << 8) | (in.read() << 16) | (in.read() << 24);
		return l | (h << 32);
	}
}
