//==================================================
//
//  Copyright 2016 Siemens Product Lifecycle Management Software Inc. All Rights Reserved.
//
//==================================================

package com.teamcenter.soa.main;

import java.awt.EventQueue;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.xml.sax.SAXException;

import com.teamcenter.clientx.AppXCredentialManager;
import com.teamcenter.clientx.Session;
import com.teamcenter.schemas.soa._2006_03.exceptions.ConnectionException;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.User;
import com.teamcenter.soa.exceptions.CanceledOperationException;
import com.teamcenter.soa.exceptions.NotLoadedException;
import com.teamcenter.soa.model.Constant;
import com.teamcenter.soa.model.Excel;
import com.teamcenter.soa.model.QueryConfigModel;
import com.teamcenter.soa.model.ResultDset;
import com.teamcenter.soa.model.Statistics;
import com.teamcenter.soa.query.Policy;
import com.teamcenter.soa.query.Query;
import com.teamcenter.soa.utils.Stopwatch;
import com.teamcenter.soa.utils.XMLConfigFactory;

/**
 * This sample SOA client application demonstrates usage of SOA to perform any
 * TC Saved Query
 * 
 */
public class ChangeNoticeReportMain {

	private static Logger logger = Logger.getLogger(ChangeNoticeReportMain.class);
	private static AppXCredentialManager credentialManager;
	private static Statistics stats = new Statistics();


	/**
	 * @param args wrong parameter count will print out a Usage statement
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		System.out.println(Constant.APPNAME + Constant.APPVERSION);
		// System.out.println("Path:"+System.getenv().get("Path"));

		if (args.length != 3) {
			if (args.length != 5) {
				System.out.println(">>> Number of Arguments wrong:[" + args.length + "]");
				System.out.println(
						">>> usage 1(interactive credentials): java -jar soaQuery.jar [Names_CommaSeparated | All] [Query Mode] [ConfigDir] ");
				System.out.println(
						">>> usage 2(manual credentials): java -jar soaQuery.jar [UserName] [Password]  [Names_CommaSeparated | All] [Query Mode] [ConfigDir]");
				System.exit(0);
			}
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					new ChangeNoticeReportMain(args);
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
					System.exit(1);
				}
			}
		});

	}

	public ChangeNoticeReportMain(String[] args) throws ConnectionException {
		Excel excel = null;
		String userName = "";
		String password = "";
		String configNames = "";
		String queryMode = "";
		String configFile = "";

		// Provide Credentials for Login to TC
		String[] credentials = null;
		credentialManager = new AppXCredentialManager();

		if (args.length == 3) {
			configNames = args[0];
			queryMode = args[1];
			configFile = args[2];
			try {
				credentials = credentialManager.promptForCredentials();
			} catch (CanceledOperationException e1) {
				e1.printStackTrace();
			}
		}

		if (args.length == 5) {
			userName = args[0];
			password = args[1];
			configNames = args[2];
			queryMode = args[3];
			configFile = args[4];
			credentialManager.setUserPassword(userName, password, "SoaAppX");
			credentials = credentialManager.getCredentials();
			
		}

		String startMsg = "Change Notice Report started";
		String programMsg = "Program to be processed: " + configNames;
		String queryMsg = "Query Mode: " + queryMode;
		String configMsg = "Config file: " + configFile + "\n";

		logger.info(startMsg);
		logger.info(programMsg);
		logger.info(queryMsg);
		logger.info(configMsg);

		System.out.printf("\n" + startMsg + "\n");
		System.out.printf(programMsg + "\n");
		System.out.printf(queryMsg + "\n");
		System.out.printf(configMsg + "\n");

		List<String> configList = new ArrayList<String>(Arrays.asList(configNames.split(",")));

		XMLConfigFactory xmlConfFactory = null;
		Map<String, QueryConfigModel> queryModelNameMap = null;
		try {
			xmlConfFactory = new XMLConfigFactory(configFile, queryMode);
			queryModelNameMap = xmlConfFactory.getQueryModelMap();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		String connectionString = "";
		String prevConnectionString = "";
		boolean sameConnection = true;
		Session session = null;

		for (Entry<String, QueryConfigModel> entry : queryModelNameMap.entrySet()) {
			String crrntConfig = entry.getKey();
			
			if (!configNames.equals("ALL")) {
				if (!configList.contains(crrntConfig)) {
					continue;
				}
			}
			QueryConfigModel queryConfigModel = entry.getValue();
			connectionString = queryConfigModel.getConnectionString();

			if (!prevConnectionString.equals(connectionString)) {
				sameConnection = false;
				prevConnectionString = connectionString;
				if (session != null) {
					session.logout();
					session = null;
				}

			} else {
				sameConnection = true;
			}

			if (!sameConnection) {
				Stopwatch loginWatch = new Stopwatch();
				session = new Session(connectionString, credentialManager);

				String protocol = Session.getConnection().getProtocol();
	
				switch (protocol) {
				case "HTTP":
					stats.setConnection("4-Tier");
					break;
				case "IIOP":
					stats.setConnection("2-Tier");
					break;

				default:
					break;
				}

				User user = session.login(credentials);
				
			
				if (user == null) {
					String skipMsg = ("Skipped due to bad connection: " + queryConfigModel.getName() + "\n");
					logger.error(skipMsg);
					AnsiConsole.systemInstall();
					System.out.println(Ansi.ansi().fgBrightRed().a("\n\n" + skipMsg).reset());
					AnsiConsole.systemUninstall();
					session = null;
					continue;
				}

				try {
					double loginTime = loginWatch.elapsedTime();

					String loginMsg = "Logged in as " + user.get_user_name() + " - Duration: " + loginTime
							+ " seconds\n";
					logger.info(loginMsg);
					System.out.println("\n" + loginMsg);

					stats.setLoginTime(loginTime);
				} catch (NotLoadedException e) {
					logger.error(e);
					e.printStackTrace();
				}
			}

			String procMsg = "Processing: " + queryConfigModel.getName();
			logger.info(procMsg);

			AnsiConsole.systemInstall();
			System.out.println(Ansi.ansi().fgBrightYellow().a(procMsg).reset());
			AnsiConsole.systemUninstall();

			String queryObj = "ItemRevision";
			String queryName = queryConfigModel.getQueryQueryName();
			queryName = queryName.replaceAll("\\s+","");
			
			if(queryName.contains(queryObj)) {
				Policy policy = new Policy();
				String[] properties = policy.getItemRevisionPolicyAttr(queryConfigModel);
				policy.addPolicy(queryObj, properties);
			}

			Query query = new Query(stats);

			Stopwatch queryWatch = new Stopwatch();

			// Perform query to the plain Objects in the database

			ModelObject[] foundObjs = query.doQuery(queryConfigModel);
			//session.printObjects(foundObjs);
			//System.out.println(foundObjs.getClass().getPropertyDisplayableValues());

			double queryTime = queryWatch.elapsedTime();
			System.out.println("Queried Object Revisions -  Duration: " + queryTime + " seconds");
			stats.setQueryTime(queryTime);

			String mode = queryConfigModel.getQueryMode();
			
			switch (QueryConfigModel.QueryMode.valueOf(mode)) {
			case PFF:
				// Get Results as defined in properties to be returned
				Map<String, List<ResultDset>> pffSheetResult = query.getResults(foundObjs, queryConfigModel);
				//System.out.println(pffSheetResult);
				try {
					// Write result into Excel
					String outputFileName = queryConfigModel.getOutputFileName();
					excel = new Excel(outputFileName, pffSheetResult, queryConfigModel, stats);
					excel.writeExcel();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				break;
			}
		}

		if(excel != null) {
			double allTime = excel.getTotalTime();
			System.out.println("Time Summary: " + allTime + " seconds\n");
			session.logout();
		}


	}
	

}
