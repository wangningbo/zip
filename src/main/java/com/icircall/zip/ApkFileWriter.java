package com.icircall.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import com.icircall.commons.Base64;

public class ApkFileWriter extends WZipOutputStream {
	private final static Pattern SF_PATTERN = Pattern.compile("META-INF/[^/]+\\.SF");
	private final static Pattern RSA_PATTERN = Pattern.compile("META-INF/([^/]+\\.(RSA|DSA)|SIG-[^/])");
	private final static int MANIFEST = 1;
	private final static int DATA = 2;
	private final static int SKIP = 3;
	private int stage = -1;
	private ByteArrayOutputStream buf;
	private Manifest manifest = new Manifest();
	private MessageDigest md;
	private final File infoFile;

	public ApkFileWriter(String path) throws FileNotFoundException, NoSuchAlgorithmException {
		super(new FileOutputStream(new File(path + ".data")));
		md = MessageDigest.getInstance("SHA-1");
		infoFile = new File(path + ".info");
	}

	@Override
	public void putNextEntry(WZipEntry e) throws IOException {
		closeEntry();
		String name = e.getName();
		if (name.equals("META-INF/MANIFEST.MF")) {
			buf = new ByteArrayOutputStream();
			stage = MANIFEST;
		} else if (SF_PATTERN.matcher(name).matches() || RSA_PATTERN.matcher(name).matches()) {
			stage = SKIP;
		} else {
			stage = DATA;
			super.putNextEntry(e);
		}
	}

	@Override
	public void closeEntry() throws IOException {
		if (current == null) {
			return;
		}
		String name = current.name;
		super.closeEntry();
		switch (stage) {
		case DATA:
			Attributes attrs = manifest.getAttributes(name);
			if (attrs == null) {
				attrs = new Attributes();
				manifest.getEntries().put(name, attrs);
			}
			attrs.put(new Attributes.Name(md.getAlgorithm() + "-Digest"),
					Base64.getEncoder().encodeToString(md.digest()));
			break;
		case MANIFEST:
			manifest.read(new ByteArrayInputStream(buf.toByteArray()));
			break;
		case SKIP:
			break;
		default:
			throw new IOException();
		}
		stage = -1;
		current = null;
	}

	@Override
	public void writeCEN() throws IOException {
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		switch (stage) {
		case DATA:
			super.write(b, off, len);
			break;
		case MANIFEST:
			buf.write(b, off, len);
			break;
		case SKIP:
			break;
		default:
			throw new IOException();
		}
	}

	@Override
	public void close() throws IOException {
		super.close();
		ApkInfo info = new ApkInfo(manifest, this.entries);
		try (FileOutputStream out = new FileOutputStream(infoFile)) {
			info.writeTo(out);
		}
	}
}