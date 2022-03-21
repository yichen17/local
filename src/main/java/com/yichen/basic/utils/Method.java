package com.yichen.basic.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
	@version 1.0 2017-08-08
	@author renxd
*/

public class Method {
	// --- AES ---

	/**
	 * 标准 AES CBC 加密
	 *
	 * @param plaintext 明文，类型为 byte 数组
	 * @param key       密钥，类型为十六进制的 byte 数组
	 * @param iv        初始向量，16个字节的 byte 数组，比如： 0xaa, 0xbb, 0xcc, 0xdd, 0xee, 0xff,
	 *                  0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88, 0x99
	 * @throws Exception
	 * @return 密文，类型为 byte 数组
	 */
	public static byte[] encryptcbc_aes(byte[] plaintext, byte[] key, byte[] iv) throws Exception {
		SecretKeySpec aeskey = new SecretKeySpec(key, 0, key.length, "AES");
		IvParameterSpec aesiv = new IvParameterSpec(iv);
		Cipher aescipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aescipher.init(Cipher.ENCRYPT_MODE, aeskey, aesiv);
		return aescipher.doFinal(plaintext);
	}

	/**
	 * 标准 AES CBC 解密
	 *
	 * @param cipher 密文，类型为 byte 数组
	 * @param key    密钥，类型为十六进制的 byte 数组
	 * @param iv     初始向量，16个字节的 byte 数组，比如： 0xaa, 0xbb, 0xcc, 0xdd, 0xee, 0xff,
	 *               0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88, 0x99
	 * @throws Exception
	 * @return 明文，类型为 byte 数组
	 */
	public static byte[] decryptcbc_aes(byte[] cipher, byte[] key, byte[] iv) throws Exception {
		SecretKeySpec aeskey = new SecretKeySpec(key, 0, key.length, "AES");
		IvParameterSpec aseiv = new IvParameterSpec(iv);
		Cipher aescipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aescipher.init(Cipher.DECRYPT_MODE, aeskey, aseiv);
		return aescipher.doFinal(cipher);
	}

	/**
	 * 标准 AES ECB 加密
	 *
	 * @param plaintext 明文，类型为 byte 数组
	 * @param key       密钥，类型为十六进制的 byte 数组
	 * @throws Exception
	 * @return 密文，类型为 byte 数组
	 */
	public static byte[] encryptecb_aes(byte[] plaintext, byte[] key) throws Exception {
		SecretKeySpec aeskey = new SecretKeySpec(key, 0, key.length, "AES");
		Cipher aescipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		aescipher.init(Cipher.ENCRYPT_MODE, aeskey);
		return aescipher.doFinal(plaintext);
	}

	/**
	 * 标准 AES ECB 解密
	 *
	 * @param cipher 密文，类型为 byte 数组
	 * @param key    密钥，类型为十六进制的 byte 数组
	 * @throws Exception
	 * @return 明文，类型为 byte 数组
	 */
	public static byte[] decryptecb_aes(byte[] cipher, byte[] key) throws Exception {
		SecretKeySpec aeskey = new SecretKeySpec(key, 0, key.length, "AES");
		Cipher aescipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		aescipher.init(Cipher.DECRYPT_MODE, aeskey);
		return aescipher.doFinal(cipher);
	}
}