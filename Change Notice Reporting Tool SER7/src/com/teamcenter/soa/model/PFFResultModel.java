package com.teamcenter.soa.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PFFResultModel extends ResultModel{

	private HashMap<String, String> resultMap = null;
	private List<Map<String, String>> resultMapList = null;
	
	public PFFResultModel(QueryConfigAttributeModel configAttrModel) {
		resultMap = new HashMap<String, String>();
		for (String header : configAttrModel.getHeaderPffMap().keySet())resultMap.put(header, "");
		
		
	}

	public HashMap<String, String> getResultMap() {
		return resultMap;
	}

	public void setResultMap(HashMap<String, String> resultMap) {
		this.resultMap = resultMap;
	}

	public List<Map<String, String>> getResultMapList() {
		return resultMapList;
	}

	public void setResultMapList(List<Map<String, String>> resultMapList) {
		this.resultMapList = resultMapList;
	}



}
