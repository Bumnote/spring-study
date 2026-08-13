package com.laboratory.url.common.codec;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.laboratory.url.common.config.AesProperties;
import com.laboratory.url.common.exception.ErrorCode;
import com.laboratory.url.common.exception.PayloadException;

@Component
public class AesCipher {

	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final int IV_LENGTH = 16;

	private final SecretKeySpec secretKey;
	private final SecureRandom secureRandom = new SecureRandom();

	public AesCipher(AesProperties properties) {
		byte[] keyBytes = properties.key().getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
			throw new IllegalStateException("url.aes.key는 16/24/32바이트여야 합니다. 현재: " + keyBytes.length + "바이트");
		}
		this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
	}

	public String encryptToBase64(String plainText) {
		try {
			byte[] iv = new byte[IV_LENGTH];
			secureRandom.nextBytes(iv);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
			byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			byte[] combined = new byte[iv.length + cipherText.length];
			System.arraycopy(iv, 0, combined, 0, iv.length);
			System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

			return Base64Codec.encode(combined);
		} catch (Exception e) {
			throw new PayloadException(ErrorCode.PAYLOAD_ENCRYPT_FAILED, e);
		}
	}

	public String decryptFromBase64(String base64) {
		return decrypt(Base64Codec.decode(base64));
	}

	public String decrypt(byte[] ivAndCipherText) {
		if (ivAndCipherText.length <= IV_LENGTH) {
			throw new PayloadException(ErrorCode.PAYLOAD_DECRYPT_FAILED,
				"IV(16바이트)와 암호문을 담기에 길이가 부족합니다: " + ivAndCipherText.length + "바이트");
		}
		try {
			byte[] iv = Arrays.copyOfRange(ivAndCipherText, 0, IV_LENGTH);
			byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, IV_LENGTH, ivAndCipherText.length);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

			return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
		} catch (PayloadException e) {
			throw e;
		} catch (Exception e) {
			throw new PayloadException(ErrorCode.PAYLOAD_DECRYPT_FAILED, e);
		}
	}
}
