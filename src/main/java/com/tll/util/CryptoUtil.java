package com.tll.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * CryptoUtil - Utility class for cryptologic-related methods.
 * @author jpk
 */
@SuppressWarnings("restriction")
public abstract class CryptoUtil {

	private static final String CIPHER_TRANSFORMATION = "DES";

	private static final String ENCODING = "UTF8";

	private static final byte[] encKey = new byte[] {
		69,
		47,
		-28,
		28,
		-16,
		-53,
		-81,
		39 };

	private static Key key;

	private static Key getKey() throws GeneralSecurityException {
		if(key == null) {
			final KeySpec keySpec = new DESKeySpec(encKey);
			final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CIPHER_TRANSFORMATION);
			key = keyFactory.generateSecret(keySpec);
		}
		return key;
	}

	/**
	 * Encrypts an Object that is serializable.
	 * @param s The serializable to encrypt
	 * @return byte array
	 * @throws GeneralSecurityException
	 */
	public static byte[] encryptSerializable(Serializable s) throws GeneralSecurityException {
		if(s == null) return null;

		byte[] data = null;

		// serialize
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			final ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(s);
			oos.close();
			data = baos.toByteArray();
		}
		catch(final IOException ioe) {
			throw new GeneralSecurityException("Error attempting to serialize object: " + ioe.getMessage());
		}

		// encrypt
		final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, getKey());
		return cipher.doFinal(data);

	}

	/**
	 * Decrypts an Object that is {@link Serializable}.
	 * @param edata The encrypted data
	 * @return decrypted serializable instance
	 * @throws GeneralSecurityException
	 * @throws ClassNotFoundException
	 */
	public static Serializable decryptSerializable(byte[] edata) throws GeneralSecurityException, ClassNotFoundException {
		if(edata == null) return null;

		byte[] data = null;

		// decrypt
		final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, getKey());
		data = cipher.doFinal(edata);

		// deserialize
		try {
			final ByteArrayInputStream bais = new ByteArrayInputStream(data);
			final ObjectInputStream ois = new ObjectInputStream(bais);
			final Object obj = ois.readObject();
			if(!(obj instanceof Serializable)) {
				throw new IllegalArgumentException("Encrypted data not Serializable");
			}
			return (Serializable) obj;
		}
		catch(final IOException ioe) {
			throw new GeneralSecurityException("Error attempting to de-serialize object: " + ioe.getMessage());
		}
	}

	/**
	 * Encrypts a String.
	 * @param str The String to encrypt
	 * @return encrypted string
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	public static String encrypt(String str) {
		try {
			final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, getKey());
			return new BASE64Encoder().encode(cipher.doFinal(str.getBytes(ENCODING)));
		}
		catch(final GeneralSecurityException gse) {
			throw new IllegalArgumentException("Encryption failed due to an unexpected security error: " + gse.getMessage(),
					gse);
		}
		catch(final UnsupportedEncodingException uee) {
			throw new IllegalStateException("Encryption failed due to an unsupported encoding: " + ENCODING, uee);
		}
	}

	/**
	 * Decrypts a String.
	 * @param str The String to decrypt
	 * @return decrypted string
	 * @throws IllegalArgumentException When either an I/O or a security related
	 *         error occurrs.
	 * @throws IllegalStateException When there is an encoding related error.
	 */
	public static String decrypt(String str) {
		try {
			final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, getKey());
			// Decode base64 to get bytes
			final byte[] dec = new BASE64Decoder().decodeBuffer(str);

			// Decrypt
			final byte[] utf8 = cipher.doFinal(dec);

			// Decode using utf-8
			return new String(utf8, ENCODING);
		}
		catch(final GeneralSecurityException gse) {
			throw new IllegalArgumentException("Decryption failed due to an unexpected security error: " + gse.getMessage(),
					gse);
		}
		catch(final UnsupportedEncodingException uee) {
			throw new IllegalStateException("Decryption failed due to an unsupported encoding: " + ENCODING, uee);
		}
		catch(final IOException ioe) {
			throw new IllegalArgumentException("Decryption failed: " + ioe.getMessage(), ioe);
		}
	}

	/**
	 * Digests a string with the specified digest type. This method converts the
	 * digest result to a String.
	 * @param str string to digest
	 * @param digestType the java string representation of the digest type
	 * @return the digested string
	 * @throws IllegalArgumentException When an digest related error occurrs.
	 */
	public static String digest(String str, String digestType) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(digestType);
		}
		catch(final NoSuchAlgorithmException nsae) {
			throw new IllegalArgumentException(StringUtil.replaceVariables("Could not get digest with algorithm: %1",
					digestType), nsae);
		}
		final byte[] digest = md.digest(str.getBytes());
		final StringBuffer hexString = new StringBuffer();
		for(final byte element : digest) {
			String plainText = Integer.toHexString(0xFF & element);

			if(plainText.length() < 2) {
				plainText = "0" + plainText;
			}

			hexString.append(plainText);
		}
		return hexString.toString();
	}
}
