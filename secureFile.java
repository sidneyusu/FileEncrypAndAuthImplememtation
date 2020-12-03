/**
 * 
 * @author Sidney Shane Dizon
 * UCID: 10149277
 * CPSC 418 Assignment 1 Problem 6
 * secureFile.java
 *
 */


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class secureFile {
	private static KeyGenerator keyGen = null;
	private static KeyGenerator hmacKey = null;
	private static SecretKey secKey = null;
	private static byte[] raw = null;
	private static SecretKeySpec secKeySpec = null;
	private static Cipher secCipher = null;
	
	public static void main(String[] args) {
		FileInputStream messageFile = null;
		FileOutputStream cipherFile = null;
		byte[] hmacHash = null;
		
		try {
			//open files
			messageFile = new FileInputStream(args[0]);
			cipherFile = new FileOutputStream(args[1]);
			//read file into byte array
			byte[] msg = new byte[messageFile.available()];
			messageFile.read(msg);
			
			//Parse the seed for PRNG
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(args[2].getBytes());

			//Generate HMACKey
			hmacKey = KeyGenerator.getInstance("HMACSHA1");
			hmacKey.init(128, random);
			SecretKey secretKey = hmacKey.generateKey();

			Mac mac = Mac.getInstance("HMACSHA1");
			mac.init(secretKey);
			//Generate 128 AES Key
			keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128, random);
			secKey = keyGen.generateKey();
			raw = secKey.getEncoded();
			secKeySpec = new SecretKeySpec(raw, "AES");
			secCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			//AES Using CBC - Generate IV
			byte[] iv = new byte[128/8];
			random.nextBytes(iv);

			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			//HMAC SHA-1 CBC Hash
			hmacHash = mac.doFinal(msg);

			
			//Concatenate the digest with the message
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			b.write(msg);
			b.write(hmacHash);
			byte[] msgNDigest = b.toByteArray();

			
			/* Encrypt the File with AES */
			secCipher.init(Cipher.ENCRYPT_MODE, secKeySpec, ivSpec);
			byte[] encrypted = secCipher.doFinal(msgNDigest);
			cipherFile.write(encrypted);			
			cipherFile.close();
			System.out.println("Result was written to: " + args[1]);
	
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
