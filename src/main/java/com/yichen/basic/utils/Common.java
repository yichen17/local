package com.yichen.basic.utils;

public class Common {
	public static int type;

	// hex String to byte[]
	public static byte[] hex2byte(String hex) {
		int len = hex.length() / 2;
		if (len % 2 != 0) {
			throw new IllegalArgumentException();
		}
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			result[i] = Integer.valueOf(hex.substring(2 * i, 2 * i + 2), 16).byteValue();
		}
		return result;
	}

	// byte[] to hex String
	public static String byte2hex(byte[] byteArray) {
		String hs = "";
		String tmp = "";
		for (int n = 0; n < byteArray.length; n++) {
			tmp = (Integer.toHexString(byteArray[n] & 0XFF));
			if (tmp.length() == 1) {
				hs = hs + "0" + tmp;
			} else {
				hs = hs + tmp;
			}
			if (n < byteArray.length - 1) {
				hs = hs + "";
			}
		}

		return hs.toUpperCase();
	}

	// 加密结果处理
	public static String cryptoResult(byte[] result) {
		try {
			switch (type) {
			case 0:// BASE64
				return new String(Base64.encode(result), "utf-8");
			case 1:// HEX
				return byte2hex(result);
			default:
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// 解密结果处理
	public static byte[] cryptoCipher(String cipher) {
		try {
			switch (type) {
			case 0:// BASE64
				return Base64.decode(cipher.getBytes("utf-8"));
			case 1:// HEX
				return hex2byte(cipher);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}