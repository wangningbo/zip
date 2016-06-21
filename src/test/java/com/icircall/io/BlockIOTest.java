package com.icircall.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class BlockIOTest {
	@Test
	public void testIO() throws IOException {
		Random r = new Random();
		byte[] data = new byte[1028];
		r.nextBytes(data);
		ByteArrayOutputStream realOut = new ByteArrayOutputStream();
		try (BlockOutputStream out = new BlockOutputStream(realOut)) {
			out.write(data);
		}
		byte[] readed = new byte[data.length];
		try (BlockInputStream in = new BlockInputStream(new ByteArrayInputStream(realOut.toByteArray()))) {
			Assert.assertEquals(in.read(readed), readed.length);
			Assert.assertEquals(in.read(), -1);
		}
		Assert.assertArrayEquals(data, readed);
	}

	@Test
	public void testEntryIO() throws IOException {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		try (EntryOutputStream out = new EntryOutputStream(buf)) {
			for (int i = 0; i < 1000; i++) {
				out.putNextEntry(Integer.toString(i));
				byte[] data = new byte[i];
				Arrays.fill(data, (byte) i);
				out.write(data);
				out.closeEntry();
			}
		}

		try (EntryInputStream in = new EntryInputStream(new ByteArrayInputStream(buf.toByteArray()))) {
			for (int i = 0; i < 1000; i++) {
				Assert.assertEquals(in.getNextEntry(), Integer.toString(i));
				byte[] data = new byte[i];
				byte[] data1 = new byte[i];
				Arrays.fill(data, (byte) i);
				in.read(data1);
				Assert.assertArrayEquals(data, data1);
				Assert.assertEquals(-1, in.read());
			}
			Assert.assertEquals(in.getNextEntry(), null);
			Assert.assertEquals(-1, in.read());
		}
	}
}