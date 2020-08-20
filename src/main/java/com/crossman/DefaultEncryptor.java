package com.crossman;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public final class DefaultEncryptor implements Encryptor {

	private final String secretKey, salt;
	private Cipher cipher;

	public DefaultEncryptor(String secretKey, String salt) {
		this.secretKey = secretKey;
		this.salt = salt;
	}

	public void init() {
		try {
			if (secretKey == null) {
				throw new NullPointerException("cannot have a null secret");
			}
			if (salt == null) {
				throw new NullPointerException("cannot have null salt");
			}
			byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

			this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String encrypt(String str) {
		try {
			return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
		} catch (BadPaddingException | IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		}
	}
}
