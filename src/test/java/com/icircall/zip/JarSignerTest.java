package com.icircall.zip;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.junit.Test;

import com.icircall.sign.JarSigner;

public class JarSignerTest {
	@Test
	public void test() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, InvalidKeyException, SignatureException {
		KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
		try (InputStream in = new FileInputStream("/home/wang/test.jks")) {
			store.load(in, "123456".toCharArray());
		}
		JarSigner signer = new JarSigner(store, "123456".toCharArray());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] signed = signer.sign("adfafadfasd1231adfafdfadfadf".getBytes());
		System.out.println(signed.length);
		signer.writePKCS7(out, "efadsfadfadfadf".getBytes());
		System.out.println(out.size());
		
	}
}
