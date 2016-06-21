package com.icircall.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;

import com.icircall.commons.ByteArrayCache;

public class WZipOuputStreamTest {

	@Test
	public void testApk() throws FileNotFoundException, NoSuchAlgorithmException, IOException {
		byte[] buf = ByteArrayCache.get();
		try (ApkFileWriter w = new ApkFileWriter("/home/wang/a")) {
			try (ZipInputStream in = new ZipInputStream(
					new FileInputStream("/home/wang/Downloads/naochuanyue-1.5.4-19084669705698846.apk"))) {
				ZipEntry e = null;
				int len = -1;
				while ((e = in.getNextEntry()) != null) {
					w.putNextEntry(new WZipEntry(e));
					while ((len = in.read(buf)) != -1) {
						w.write(buf, 0, len);
					}
				}
			}
		}
		ApkFile apk = new ApkFile("/home/wang/a");
		try(OutputStream out = new FileOutputStream("/home/wang/a.apk")){
			apk.writeTo(out);
		}
	}

	public void testWrite() throws FileNotFoundException, IOException {
		File in = new File("/home/wang/Downloads/app-naochuanyue-debug-1.5.3_build_4.apk");
		File out = new File("/home/wang/Downloads/w.zip");
		try (WZipOutputStream w = new WZipOutputStream(new FileOutputStream(out))) {
			try (ZipInputStream r = new ZipInputStream(new FileInputStream(in))) {
				byte[] buf = new byte[1024];
				ZipEntry e = null;
				while ((e = r.getNextEntry()) != null) {
					w.putNextEntry(new WZipEntry(e));
					int readed = -1;
					while ((readed = r.read(buf)) != -1) {
						w.write(buf, 0, readed);
					}
				}
			}
		}
	}

	private static class NoCEN extends WZipOutputStream {

		public NoCEN(OutputStream out) {
			super(out);
		}

		@Override
		public void writeCEN() {
		}
	}

	@Test
	public void testAppend() throws IOException {
		File in = new File("/home/wang/Downloads/app-naochuanyue-debug-1.5.3_build_4.apk");
		File out = new File("/home/wang/Downloads/w.zip");
		try (OutputStream o = new FileOutputStream(out)) {
			WZipOutputStream w = new NoCEN(o);
			try (ZipInputStream r = new ZipInputStream(new FileInputStream(in))) {
				byte[] buf = new byte[1024];
				ZipEntry e = null;
				while ((e = r.getNextEntry()) != null) {
					w.putNextEntry(new WZipEntry(e));
					int readed = -1;
					while ((readed = r.read(buf)) != -1) {
						w.write(buf, 0, readed);
					}
				}
			}
			w.finish();

			w = new WZipOutputStream(o, w.getEntries());
			w.putNextEntry(new WZipEntry("a"));
			w.write("abc".getBytes());
			w.close();
		}
	}
}
