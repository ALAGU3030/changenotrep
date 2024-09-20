package com.teamcenter.soa.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Crypt {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// der zu verschl. Text
		String password = "Das ist mein Password";

		// Verschluesseln
		Base64.Encoder encoder = Base64.getEncoder();
		String passwordEncrypted = encoder.encodeToString(password.getBytes(StandardCharsets.UTF_8));

		// Ergebnis
		System.out.println(passwordEncrypted);

		// // Entschluesseln
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] decodedByteArray = decoder.decode(passwordEncrypted);
		System.out.println(new String(decodedByteArray));


	}

}