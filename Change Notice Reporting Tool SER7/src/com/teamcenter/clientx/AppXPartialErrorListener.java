//==================================================
//
//  Copyright 2012 Siemens Product Lifecycle Management Software Inc. All Rights Reserved.
//
//==================================================

package com.teamcenter.clientx;

import org.apache.log4j.Logger;

import com.teamcenter.soa.client.model.ErrorStack;
import com.teamcenter.soa.client.model.ErrorValue;
import com.teamcenter.soa.client.model.PartialErrorListener;

/**
 * Implementation of the PartialErrorListener. Print out any partial errors
 * returned.
 *
 */
public class AppXPartialErrorListener implements PartialErrorListener {
	private static Logger logger = Logger.getLogger(AppXPartialErrorListener.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.teamcenter.soa.client.model.PartialErrorListener#handlePartialError(com.
	 * teamcenter.soa.client.model.ErrorStack[])
	 */
	public void handlePartialError(ErrorStack[] stacks) {
		if (stacks.length == 0)
			return;

		System.out.println("");
		System.out.println("*****");

		String msg = ("Partial Errors caught in com.teamcenter.clientx.AppXPartialErrorListener.");
		System.out.println(msg);
		logger.error(msg);

		for (int i = 0; i < stacks.length; i++) {
			ErrorValue[] errors = stacks[i].getErrorValues();
			msg = msg + ("\nPartial Error for ");
			System.out.print(msg);
			logger.error(msg);
			// The different service implementation may optionally associate
			// an ModelObject, client ID, or nothing, with each partial error
			if (stacks[i].hasAssociatedObject()) {
				msg = "object " + stacks[i].getAssociatedObject().getUid();
				System.out.println(msg);
				logger.error(msg);
			} else if (stacks[i].hasClientId()) {
				msg = ("client id " + stacks[i].getClientId());
				System.out.println(msg);
				logger.error(msg);
			} else if (stacks[i].hasClientIndex()) {
				msg = ("client index " + stacks[i].getClientIndex());
				System.out.println(msg);
				logger.error(msg);
			}

			// Each Partial Error will have one or more contributing error messages
			for (int j = 0; j < errors.length; j++) {
				msg = ("    Code: " + errors[j].getCode() + "\tSeverity: " + errors[j].getLevel() + "\t" + errors[j].getMessage());
				System.out.println(msg);
				logger.error(msg);				
			}
		}

	}

}
