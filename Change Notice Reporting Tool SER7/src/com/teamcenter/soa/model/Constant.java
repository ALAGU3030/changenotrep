package com.teamcenter.soa.model;

import java.util.Arrays;
import java.util.List;

public class Constant {

	public static final String APPNAME = "Change Notice Reporting Tool ";
	public static final String APPVERSION = "v2.2.5 for SER7";

	public static final String logDir = "ChangeNotice";
	public static final String logfilePrefix = "ReportLog";
	public static final String logfileSuffix = ".log";

	public static final String EMPTY = "EMPTY";

	public static final String DsetSheet = "Dset";
	public static final String ChangeFromPrevRev = "Change from previous revision";
	public static final List<String> SkipChangeFromPrevRev = Arrays.asList("deleted","Deleted", "removed", "Removed", "sys delete - no action req");

	public static final String ItemSheet = "Item";
	
	public static final String OtherOwningSite = "Owning Site";
	public static final String NoticeNumber = "Notice Number";
	public static final String ID = "ID";
	

}
