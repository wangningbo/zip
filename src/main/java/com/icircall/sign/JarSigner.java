package com.icircall.sign;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import com.icircall.commons.ByteArrayCache;

import sun.security.pkcs.ContentInfo;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;

@SuppressWarnings("restriction")
public class JarSigner extends SignerBase {

	public JarSigner(KeyStore store, String alias, char[] keyPass) throws KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException {
		super(store, alias, keyPass);
	}

	public JarSigner(KeyStore store, char[] keyPass) throws KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException {
		super(store, keyPass);
	}

	public JarSigner(String storePath, char[] storePass, String alias, char[] keyPass, String storeType)
			throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException,
			IOException {
		super(storePath, storePass, alias, keyPass, storeType);
	}

	/**
	 * 生成签名数据
	 * 
	 * @param data
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public byte[] sign(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sig = createSignature();
		sig.update(data);
		return sig.sign();
	}

	public byte[] sign(InputStream input) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException,
			IOException {
		Signature sig = Signature.getInstance(signatureAlgorithm);
		byte[] buff = ByteArrayCache.get();
		sig.initSign(privateKey);
		int i = -1;
		while ((i = input.read(buff)) != -1) {
			sig.update(buff, 0, i);
		}
		return sig.sign();
	}

	public Signature createSignature() throws NoSuchAlgorithmException, InvalidKeyException {
		Signature sig = Signature.getInstance(signatureAlgorithm);
		sig.initSign(privateKey);
		return sig;
	}

	/**
	 * 以PKCS7的格式输出签名数据
	 * 
	 * @param out
	 * @param signature
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public void writePKCS7(OutputStream out, byte[] signature) throws NoSuchAlgorithmException, IOException {
		AlgorithmId keyAlg = AlgorithmId.get(privateKey.getAlgorithm());
		AlgorithmId digestAlg = AlgorithmId.get(digestAlgorithm);
		X509Certificate cert = certChain[0];
		X500Principal issuer = certChain[0].getIssuerX500Principal();
		SignerInfo signerInfo = new SignerInfo((X500Name.asX500Name(issuer)), cert.getSerialNumber(), digestAlg,
				keyAlg, signature);
		PKCS7 pkcs = new PKCS7(new AlgorithmId[] { digestAlg }, new ContentInfo(ContentInfo.DATA_OID, null), certChain,
				new SignerInfo[] { signerInfo });
		pkcs.encodeSignedData(out);
	}

	/**
	 * 生成签名数据，并已PKCS7的格式输出
	 * 
	 * @param out
	 * @param content
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws IOException
	 */
	public void write(OutputStream out, byte[] content) throws InvalidKeyException, NoSuchAlgorithmException,
			SignatureException, IOException {
		writePKCS7(out, sign(content));
	}

	public String getKeyAlgorithm() {
		return privateKey.getAlgorithm();
	}
}
