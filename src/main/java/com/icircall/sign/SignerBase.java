package com.icircall.sign;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class SignerBase {
	protected X509Certificate[] certChain;
	protected PrivateKey privateKey;
	protected String digestAlgorithm;
	protected String signatureAlgorithm;
	protected String subjectName;

	public SignerBase(KeyStore store, char[] keyPass)
			throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
		String alias = null;
		Enumeration<String> aliasEnu = store.aliases();
		if (aliasEnu.hasMoreElements()) {
			alias = aliasEnu.nextElement();
			if (aliasEnu.hasMoreElements()) {
				throw new IllegalArgumentException("keystore中含有多个alias，需指定alias");
			}
		} else {
			throw new IllegalArgumentException("keystore中没有alias");
		}
		init(store, alias, keyPass, "SHA1");
	}

	public SignerBase(KeyStore store, String alias, char[] keyPass)
			throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
		init(store, alias, keyPass, "SHA1");
	}

	public SignerBase(String storePath, char[] storePass, String alias, char[] keyPass, String storeType)
			throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException,
			IOException {
		init(storePath, storePass, alias, keyPass, storeType, "SHA1");
	}

	private void init(KeyStore store, String alias, char[] keyPass, String digestAlgorithm)
			throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
		Certificate[] certs = store.getCertificateChain(alias);
		certChain = new X509Certificate[certs.length];
		for (int i = 0; i < certs.length; i++) {
			certChain[i] = (X509Certificate) certs[i];
		}
		privateKey = (PrivateKey) store.getKey(alias, keyPass);
		subjectName = getSubjectName(certChain[0]);
		signatureAlgorithm = digestAlgorithm + "with" + privateKey.getAlgorithm();
		this.digestAlgorithm = digestAlgorithm;
	}

	private void init(String storePath, char[] storePass, String alias, char[] keyPass, String storeType,
			String digestAlgorithm) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException,
					CertificateException, IOException {
		KeyStore store = KeyStore.getInstance(storeType);
		InputStream in = this.getClass().getResourceAsStream(storePath);
		try {
			store.load(in, storePass);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		init(store, alias, keyPass, digestAlgorithm);
	}

	public static String getSubjectName(final X509Certificate cert) {
		final String fullSubjectDn = cert.getSubjectX500Principal().getName();
		try {
			LdapName fullSubjectLn = new LdapName(fullSubjectDn);
			for (final Rdn rdn : fullSubjectLn.getRdns()) {
				if ("CN".equalsIgnoreCase(rdn.getType())) {
					return rdn.getValue().toString();
				}
			}
		} catch (InvalidNameException e) {
			return null;
		}
		return null;
	}
}
