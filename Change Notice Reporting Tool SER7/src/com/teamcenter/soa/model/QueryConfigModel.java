package com.teamcenter.soa.model;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryConfigModel {

	// Configuration Files and Names

	private String queryMode = "";

	public static enum QueryMode {
		PFF;
	}

	// Objects to support multiple queries
	private final static String configPath = "/QueryConfig/List";

	private final static String nameTag = "Name";
	private final static String connectionTag = "Connection";
	private final static String specFileTag = "SpecFile";

	public final static String twoTier = "iiop";
	public final static String fourTier = "http";

	private String name = "EMPTY";
	private String connectionString = "NONE";
	private File specFile = null;
	private String type = "NONE";

	private final String outputFileNameTag = "@FileName";
	private final String dateFormatTag = "@DateInFileNameFormat";
	private final String outputSuffixTag = "@FileNameSuffix";

	private String outputFileName = "";

	private Map<String, String> siteOutputConfig = null;

	// Objects to fill TC Saved Query
	private String queryQueryName = "";
	private List<String> queryAttrNameList;
	private List<String> queryAttrValueList;

	private boolean latestRevOnly=false;

	private String invalidTabPrefix = "Invalid_";
	private String allowedChars = "^.*$";
	private int maxChars = 9999;

	// Result Attribute Storage Object
	private Map<String, QueryConfigAttributeModel> queryConfigAttrModelMap;

	public Map<String, QueryConfigAttributeModel> getQueryConfigAttrModelMap() {
		return queryConfigAttrModelMap;
	}

	public void addToQueryConfigAttrModelMap(String name, QueryConfigAttributeModel queryConfigAttrModelMap) {
		if (this.queryConfigAttrModelMap == null) {
			this.queryConfigAttrModelMap = new HashMap<String, QueryConfigAttributeModel>();
		}

		this.queryConfigAttrModelMap.put(name, queryConfigAttrModelMap);
	}
	
	public Map<String, QueryConfigAttributeModel> getConfigAttr(){
		return queryConfigAttrModelMap;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getSpecFile() {
		return specFile;
	}

	public void setSpecFile(File file) {
		this.specFile = file;
	}

	public static String getSpecFileTag() {
		return specFileTag;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	public Map<String, String> getSiteOutputConfig() {
		return siteOutputConfig;
	}

	public void setConfigOutput(Map<String, String> siteOutputConfig) {
		this.siteOutputConfig = siteOutputConfig;
	}

	public String getQueryQueryName() {
		return queryQueryName;
	}

	public void setQueryQueryName(String queryQueryName) {
		this.queryQueryName = queryQueryName;
	}

	public List<String> getQueryAttrNameList() {
		return queryAttrNameList;
	}

	public void setQueryAttrNameList(List<String> queryAttrNameList) {
		this.queryAttrNameList = queryAttrNameList;
	}

	public List<String> getQueryAttrValueList() {
		return queryAttrValueList;
	}

	public void setQueryAttrValueList(List<String> queryAttrValueList) {
		this.queryAttrValueList = queryAttrValueList;
	}

	public final static String getNameTag() {
		return nameTag;
	}

	public final static String getConfigList() {
		return configPath;
	}

	public static String getConnectionTag() {
		return connectionTag;
	}

	public String getOutputFileNameTag() {
		return outputFileNameTag;
	}

	public String getDateFormatTag() {
		return dateFormatTag;
	}

	public String getOutputSuffixTag() {
		return outputSuffixTag;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getQueryMode() {
		return queryMode;
	}

	public void setQueryMode(String queryMode) {
		this.queryMode = queryMode;
	}
	
	public boolean processLatestRevOnly() {
		return latestRevOnly;
	}

	public void setLatestRevOnly(boolean latestRevOnly) {
		this.latestRevOnly = latestRevOnly;
	}


	public String getAllowedChars() {
		return allowedChars;
	}

	public void setAllowedChars(String allowedChars) {
		this.allowedChars = allowedChars;
	}

	public int getMaxChars() {
		return maxChars;
	}

	public void setMaxChars(int maxChars) {
		this.maxChars = maxChars;
	}

	public String getInvalidTabPrefix() {
		return invalidTabPrefix;
	}

	public void setInvalidTabPrefix(String invalidTabPrefix) {
		this.invalidTabPrefix = invalidTabPrefix;
	}

}
