package com.teamcenter.soa.utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Crypt2 {

	public Crypt2() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String password = "MeinPassword";
		PasswordEncryptionService pes = new PasswordEncryptionService();
		byte[] salt = pes.generateSalt();
		byte[] encryptedPassword = pes.getEncryptedPassword(password, salt);
		@SuppressWarnings("unused")
		boolean authenticate = pes.authenticate(password, encryptedPassword, salt);
		System.out.println("ende");

	}

}
