package com.icircall.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.icircall.commons.ByteArrayCache;

public class ApkFile {
	private File dataFile;
	private ApkInfo info;

	public ApkFile(String path) throws FileNotFoundException, IOException {
		dataFile = new File(path + ".data");
		info = new ApkInfo(new FileInputStream(path + ".info"));
	}

	public void writeTo(OutputStream out) throws IOException {
		byte[] buf = ByteArrayCache.get();
		try (InputStream in = new FileInputStream(dataFile)) {
			int len = -1;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
		}
		WZipOutputStream wOut = new WZipOutputStream(out, info.entris);
		wOut.putNextEntry(new WZipEntry("META-INF/MENIFEST.MF"));
		info.manifest.write(wOut);
		wOut.closeEntry();
		wOut.writeCEN();
	}
}
