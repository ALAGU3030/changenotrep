package com.teamcenter.soa.model;

import java.util.HashMap;
import java.util.Map;

public class Statistics {
	private int numOfAllRevs = 0;
	private int numOfLatestRevs = 0;
	private double loginTime = 0.0;
	private double queryTime = 0.0;
	private double filterTime = 0.0;
	private double writeTime = 0.0;
	private double copyTime = 0.0;
	private String connection = "";

	private Map<String, Double> attrTime = new HashMap<String, Double>();

	public Statistics() {

	}

	public double getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(double writeTime) {
		this.writeTime = writeTime;
	}

	public double getCopyTime() {
		return copyTime;
	}

	public void setCopyTime(double copyTime) {
		this.copyTime = copyTime;
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public double getFilterTime() {
		return filterTime;
	}

	public void setFilterTime(double filterTime) {
		this.filterTime = filterTime;
	}

	public int getNumOfAllRevs() {
		return numOfAllRevs;
	}

	public void setNumOfAllRevs(int numOfAllRevs) {
		this.numOfAllRevs = numOfAllRevs;
	}

	public int getNumOfLatestRevs() {
		return numOfLatestRevs;
	}

	public void setNumOfLatestRevs(int numOfLatestRevs) {
		this.numOfLatestRevs = numOfLatestRevs;
	}

	public double getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(double loginTime) {
		this.loginTime = loginTime;
	}

	public double getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(double queryTime) {
		this.queryTime = queryTime;
	}

	public Map<String, Double> getAttrTime() {
		return attrTime;
	}

	public void setAttrTime(String name, Double time) {
		this.attrTime.put(name, time);
	}

}
