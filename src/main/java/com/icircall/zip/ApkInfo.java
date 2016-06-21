package com.icircall.zip;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import com.icircall.io.EntryInputStream;
import com.icircall.io.EntryOutputStream;

public class ApkInfo {
	final Manifest manifest;
	final List<WZipEntry> entris;

	public ApkInfo(Manifest manifest, List<WZipEntry> extris) {
		super();
		this.manifest = manifest;
		this.entris = extris;
	}

	public ApkInfo(InputStream in) throws IOException {
		this.manifest = new Manifest();
		this.entris = new ArrayList<>();
		readFrom(in);
	}

	public void readFrom(InputStream in) throws IOException {
		EntryInputStream entryIn = new EntryInputStream(in);
		if ("Manifest".equals(entryIn.getNextEntry())) {
			this.manifest.read(entryIn);
		}
		if ("Entris".equals(entryIn.getNextEntry())) {
			DataInputStream dataIn = new DataInputStream(entryIn);
			int size = dataIn.readInt();
			for (int i = 0; i < size; i++) {
				WZipEntry e = new WZipEntry();
				e.readFrom(dataIn);
				this.entris.add(e);
			}
		}
	}

	public void writeTo(OutputStream out) throws IOException {
		EntryOutputStream entryOut = new EntryOutputStream(out);
		entryOut.putNextEntry("Manifest");
		manifest.write(entryOut);
		entryOut.putNextEntry("Entris");
		DataOutputStream dataOut = new DataOutputStream(entryOut);
		dataOut.writeInt(entris.size());
		for (WZipEntry e : entris) {
			e.writeTo(dataOut);
		}
		entryOut.closeEntry();
	}
}
