package com.mahasbr.util;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;

public class CMSEncryptionDecryption {

	private static String ENCODING_CHARSET = "UTF-8";

	public static String passwordBasedEncAES(String pText, String password) throws Exception{
		String result="";
		int TAG_LENGTH_BIT = 128;
	    int IV_LENGTH_BYTE = 16;
	    int SALT_LENGTH_BYTE = 16;
	    int iterationCount=65536;
	    int keyLength=256;
	    String ENCRYPT_ALGO="AES/GCM/NoPadding";
        byte[] salt=null;

        byte[] iv =null;
        iv= getRandomNonce(IV_LENGTH_BYTE);
        salt = getRandomNonce(SALT_LENGTH_BYTE);

        // secret key from password
        SecretKey aesKeyFromPassword;
		try {
			aesKeyFromPassword = getAESKeyFromPassword(password, salt,iterationCount,keyLength);
			Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
			// ASE-GCM needs GCMParameterSpec
	        cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

	        byte[] cipherText = cipher.doFinal(pText.getBytes());

	        // prefix IV and Salt to cipher text
	        byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length)
	                .put(iv)
	                .put(cipherText)
	                .put(salt)
	                .array();
	        result=Base64.getEncoder().encodeToString(cipherTextWithIvSalt);
		} catch (InvalidKeySpecException e) {
			System.out.println(e.getMessage());
			throw new Exception("Incorrect or wrong key for encryption");
		}catch (BadPaddingException badPad) {
			System.out.println(badPad.getMessage());
			throw new Exception("Incorrect or wrong key for encryption");
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
        return result;
	}

	public static String passwordBasedDecryAES(String cText, String password) throws Exception{
		String result="";
		int TAG_LENGTH_BIT = 128;
		int IV_LENGTH_BYTE = 16;
		int SALT_LENGTH_BYTE = 16;
		int iterationCount=65536;
		int keyLength=256;
		String ENCRYPT_ALGO="AES/GCM/NoPadding";
		byte[] salt=null;

		byte[] iv =null;

		// secret key from password
		SecretKey aesKeyFromPassword;
		try {
			byte[] decode = Base64.getDecoder().decode(cText.getBytes(ENCODING_CHARSET));
			ByteBuffer bb = ByteBuffer.wrap(decode);

			iv = new byte[IV_LENGTH_BYTE];
			bb.get(iv);
			byte[] cipherText = new byte[bb.remaining()-SALT_LENGTH_BYTE];
			bb.get(cipherText);
			salt = new byte[SALT_LENGTH_BYTE];
			bb.get(salt);
			aesKeyFromPassword = getAESKeyFromPassword(password, salt,iterationCount,keyLength);
			Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
			// ASE-GCM needs GCMParameterSpec
			cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

			byte[] plainText = cipher.doFinal(cipherText);

			result=new String(plainText, ENCODING_CHARSET);
		} catch (InvalidKeySpecException e) {
			System.out.println(e.getMessage());
			throw new Exception("Incorrect or wrong key for encryption");

		}catch (BadPaddingException badPad) {
			System.out.println(badPad.getMessage());
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	public static SecretKey getAESKeyFromPassword(String password, byte[] salt,
			int iterationCount,int keyLengthInByte)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");//"PBKDF2WithHmacSHA256"

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLengthInByte);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;

    }

	public static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

}
