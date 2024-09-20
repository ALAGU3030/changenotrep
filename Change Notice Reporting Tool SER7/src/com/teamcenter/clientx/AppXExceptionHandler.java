//==================================================
//
//  Copyright 2012 Siemens Product Lifecycle Management Software Inc. All Rights Reserved.
//
//==================================================

package com.teamcenter.clientx;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.teamcenter.schemas.soa._2006_03.exceptions.ConnectionException;
import com.teamcenter.schemas.soa._2006_03.exceptions.InternalServerException;
import com.teamcenter.schemas.soa._2006_03.exceptions.ProtocolException;
import com.teamcenter.soa.client.ExceptionHandler;
import com.teamcenter.soa.exceptions.CanceledOperationException;


/**
 * Implementation of the ExceptionHandler. For ConnectionExceptions (server
 * temporarily down .etc) prompts the user to retry the last request. For other
 * exceptions convert to a RunTime exception.
 */
public class AppXExceptionHandler implements ExceptionHandler {

	private static Logger logger = Logger.getLogger(AppXExceptionHandler.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.teamcenter.soa.client.ExceptionHandler#handleException(com.teamcenter.
	 * schemas.soa._2006_03.exceptions.InternalServerException)
	 */
	public void handleException(InternalServerException ise) {

		System.out.println("");
		System.out.println("*****");
		String msg = null;

		logger.setLevel((Level) Level.ALL);

		if (ise instanceof ConnectionException) {
			// ConnectionException are typically due to a network error (server
			// down .etc) and can be recovered from (the last request can be sent again,
			// after the problem is corrected).
			msg = ("The server returned an connection error.\nThis is typically due to a network error (server down .etc)");
			msg = msg + "\nCurrent Error is: " + ise.getMessage() + "\n";
			logger.error(msg);
		} else if (ise instanceof ProtocolException) {
			// ProtocolException are typically due to programming errors
			// (content of HTTP
			// request is incorrect). These are generally can not be
			// recovered from.
			msg = ("\nThe server returned an protocol error.\nThis is most likely the result of a programming error.");
			msg = msg + "\nCurrent Error is: " + ise.getMessage();
			logger.error(msg);
		} else {
			msg = ("\nThe server returned an internal server error.\nThis is most likely the result of a programming error.");
			msg = msg + "\nCurrent Error is: " + ise.getMessage();
			logger.error(msg);

		}

		throw new RuntimeException("Server Error Logged...");

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.teamcenter.soa.client.ExceptionHandler#handleException(com.teamcenter.soa
	 * .exceptions.CanceledOperationException)
	 */
	public void handleException(CanceledOperationException coe) {
		System.out.println("");
		System.out.println("*****");
		System.out.println("Exception caught in com.teamcenter.clientx.AppXExceptionHandler.handleException(CanceledOperationException).");

		// Expecting this from the login tests with bad credentials, and the
		// AnyUserCredentials class not
		// prompting for different credentials
		throw new RuntimeException(coe);
	}

}
