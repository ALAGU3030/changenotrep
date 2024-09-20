package com.teamcenter.soa.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class prompts the user for a password and attempts to mask input with "*"
 */

public class PasswordField {

	   /**
	    *@param prompt The prompt to display to the user
	    *@return The password as entered by the user
	    */
	   public static String readPassword (String prompt) {
	      EraserThread et = new EraserThread(prompt);
	      Thread mask = new Thread(et);
	      mask.start();

	      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	      String password = "";

	      try {
	         password = in.readLine();
	      } catch (IOException ioe) {
	        ioe.printStackTrace();
	      }
	      // stop masking
	      et.stopMasking();
	      // return the password entered by the user
	      return password;
	   }


   
   public static String readUserName(String message){
	   System.out.print(message);

	      //  open up standard input
	      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	      String userName = null;

	      //  read the username from the command-line; need to use try/catch with the
	      //  readLine() method
	      try {
	         userName = br.readLine();
	      } catch (IOException ioe) {
	         System.out.println("IO error trying to read your name!");
	         System.exit(1);
	      }
	    return userName;
   }
   
}
