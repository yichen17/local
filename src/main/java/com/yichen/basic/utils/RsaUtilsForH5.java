package com.yichen.basic.utils;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加、解密算法工具类
 */
public class RsaUtilsForH5 {
//    public static final String publicKey  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC42DhN2cJWPheTsEfSUkxbFe6lQiJqKlNy3/rI2aePP8PqFm4ig/nctplSBXVl9H1OHXONorSwW+Un1Nt5Np6a5mVMWdT4dXm51s9+OaFlf77rxjvVptn55WiYwmAZDT4yYdVhe1GvccpTFT4ktkeNys5Okt64DwS8m7k8U15cDwIDAQAB";
//    public static final String privateKey = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBALjYOE3ZwlY+F5OwR9JSTFsV7qVCImoqU3Lf+sjZp48/w+oWbiKD+dy2mVIFdWX0fU4dc42itLBb5SfU23k2nprmZUxZ1Ph1ebnWz345oWV/vuvGO9Wm2fnlaJjCYBkNPjJh1WF7Ua9xylMVPiS2R43Kzk6S3rgPBLybuTxTXlwPAgMBAAECgYEAt6J3LUBtBOji22KBt7zzBnqff6ZYMkiiZrmFmAxDntimdeSavceHb3Iol4e8vfFKm1Q+/DLsd1iULCIXeDB/F4a5Spax30/Phl9XQjOxgSvOqo1qnZjLzePb0CDAow3HOz1WlKVvhOPokn7xlhzzVIgpUjHJtzhb3wjAgNUW8iECQQD+rq04e/MhiJ2imPoAYcbRVXFEw/4yivlwaUHG2uom7qaPJYwXRIDorwnBxjvVQrSWvUp/foGE4rWcWu1a3n+VAkEAuc0LTjeTZ8t3FUkt4fHz97AmTJpKCHVJx+xHIHInRycrTL4TN2ZdT+InZzzhIYdBv1wLKBr1v7yN9GBk34ZUEwJBAIskKbULWvi+J8k2pJdi0/l4Icr8/Trl3IOCrhITdgvRJKReUhxAK+F0B/Oa9wN7TyhvwGzJU7+jEK+cqcKUfsUCQQCgSRQ8uNMQUsV7dnL/nXQjNDIGGYBOhahNsTa29eOOjWrcPIllXIiZNndZp3psKzv78lKpmYBxlR89N78t2btDAkEAocW5KSb2RjYz6US8JPXBzbjePbZBcCpqnS+SJCC+6teiJtHDbjmwGGbm+ySfxsWezzFNL5vjv8JPWeXxWo9bpA==";

    public static final String publicKey  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWlvrbxVNNFsPzB01Z6aJ3rA06hDixkywdtUxfxAVpmJx8+Jd4HK/pgOFJdCUPQtFfkh+rVppQNmWzNvn7YUQ3cSIM3l/j269ii67p8iB+LCebkGWIV2efu57QQLSW63eI03V8AVMmTuSHebw7H3TMrURbU4RDDMnZ3DV93eCOIQIDAQAB";
    public static final String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJaW+tvFU00Ww/MHTVnponesDTqEOLGTLB21TF/EBWmYnHz4l3gcr+mA4Ul0JQ9C0V+SH6tWmlA2ZbM2+fthRDdxIgzeX+Pbr2KLrunyIH4sJ5uQZYhXZ5+7ntBAtJbrd4jTdXwBUyZO5Id5vDsfdMytRFtThEMMydncNX3d4I4hAgMBAAECgYACL/9E4gDrxcDi7uA4xiQOCPnIN9N7AOBLLi8F1RsfBZCAYDwTCpejlLOxFXbUWewgr0a2fnOPZrrjd5D6Dden0NAWiVlBLJ4byw5zWGEpQabOFg3ddMVSVFX+3fDmk4RObnNrH9nScwHd4dibbfmoT5qQRk65WLxRoCDF3aiCYQJBAMmN9PF/b1Y9muEabLXBEP3V/4z/bd9/5WPpK7JUL7IQie+MM10fu35kcK6YOnPgo0c9OUkN8WBaUFxlt1MooasCQQC/RKv51yvzWgFv5IQBE7FD0Jq05BV1Acj0qgZ0n7nve/v/YVSrecpSkQ9XYPhyRy1cplvkAj51IXTxLvhxVxtjAkB+1wotrG/Jiw1b2gBNxUlHJRQkjF59x4P5gzSPjjFR0tyrVsTANwcMPHM5PO2UHOtEGsBhPBgJ9ewaqZxcBfbvAkBTpMBgfgymW1INkK15mxcGRQ+i06veg21SMZipH8C8TkghonrYkmY8PVusJqf/scjQn5/H0oNlzb/KSXQ0fJdpAkB2HJZ4/H8RxCyW2fuY7Jp89sEFSCZbL/qzPWxImC0mnuVPD3E3KfhABCOLvFjC5gkbeNaAit3+EmNoJMjFN4fI";

    /**
     * 加密算法AES
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 算法名称/加密模式/数据填充方式
     * 默认：RSA/ECB/PKCS1Padding
     */
    private static final String ALGORITHMS = "RSA/ECB/PKCS1Padding";

    /**
     * Map获取公钥的key
     */
    private static final String PUBLIC_KEY = "publicKey";

    /**
     * Map获取私钥的key
     */
    private static final String PRIVATE_KEY = "privateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * RSA 位数 如果采用2048 上面最大加密和最大解密则须填写:  245 256
     */
    private static final int INITIALIZE_LENGTH = 1024;

    /**
     * 后端RSA的密钥对(公钥和私钥)Map，由静态代码块赋值
     */
    private static Map<String, Object> genKeyPair = new HashMap<>();

    static {
        try {
            genKeyPair.putAll(genKeyPair());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成密钥对(公钥和私钥)
     */
    private static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(INITIALIZE_LENGTH);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        //公钥
        keyMap.put(PUBLIC_KEY, publicKey);
        //私钥
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        //base64格式的key字符串转Key对象
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);

        //设置加密、填充方式
        /*
            如需使用更多加密、填充方式，引入
            <dependency>
                <groupId>org.bouncycastle</groupId>
                <artifactId>bcprov-jdk16</artifactId>
                <version>1.46</version>
            </dependency>
            并改成
            Cipher cipher = Cipher.getInstance(ALGORITHMS ,new BouncyCastleProvider());
         */
        Cipher cipher = Cipher.getInstance(ALGORITHMS);
        cipher.init(Cipher.DECRYPT_MODE, privateK);

        //分段进行解密操作
        return encryptAndDecryptOfSubsection(encryptedData, cipher, MAX_DECRYPT_BLOCK);
    }

    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        //base64格式的key字符串转Key对象
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);

        Cipher cipher = Cipher.getInstance(ALGORITHMS);
        cipher.init(Cipher.ENCRYPT_MODE, publicK);

        //分段进行加密操作
        return encryptAndDecryptOfSubsection(data, cipher, MAX_ENCRYPT_BLOCK);
    }

    /**
     * 获取私钥
     */
    public static String getPrivateKey() {
        Key key = (Key) genKeyPair.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 获取公钥
     */
    public static String getPublicKey() {
        Key key = (Key) genKeyPair.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 分段进行加密、解密操作
     */
    private static byte[] encryptAndDecryptOfSubsection(byte[] data, Cipher cipher, int encryptBlock) throws Exception {
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > encryptBlock) {
                cache = cipher.doFinal(data, offSet, encryptBlock);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * encryptBlock;
        }
        byte[] toByteArray = out.toByteArray();
        out.close();
        return toByteArray;
    }
//    public static void main(String[] args) {
//        //字符串
//        String str = "adsfa";
//        try {
//            System.out.println("私钥：" + RsaUtilsForH5.getPrivateKey());
//            System.out.println("公钥：" + RsaUtilsForH5.getPublicKey());
//            LoginSMSRequestEntity loginSMSRequestEntity = new LoginSMSRequestEntity();
//            loginSMSRequestEntity.setChannel("asdfasfdasfda");
//            loginSMSRequestEntity.setMobile("18811111111");
//
//            //公钥加密
//            byte[] ciphertext = RsaUtilsForH5.encryptByPublicKey(loginSMSRequestEntity.toString().getBytes(), RsaUtilsForH5.publicKey);
//
//            String s = Base64.encodeBase64String(ciphertext);
//            //1、前端传过来公钥、加密后的数据 Base64.encodeBase64String(ciphertext);
//
//            //私钥解密
//            byte[] plaintext = RsaUtilsForH5.decryptByPrivateKey(Base64.decodeBase64(s), RsaUtilsForH5.privateKey);
//
//            System.out.println("公钥加密前：" + str);
//            System.out.println("公钥加密后：" + Base64.encodeBase64String(ciphertext));
//            System.out.println("私钥解密后：" + FastJsonUtils.fromJson(new String(plaintext),LoginSMSRequestEntity.class));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}