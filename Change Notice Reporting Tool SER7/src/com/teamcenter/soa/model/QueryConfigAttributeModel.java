package com.teamcenter.soa.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class QueryConfigAttributeModel {

	private Vector<String> header;
	private Set<String> propertySet;

	private Map<String, String> headerPffMap;
	private Map<String, String> pffHeaderMap;
	private Map<String,String> headerTypeMap;

	public Vector<String> getHeader() {
		if (header == null) {
			header = new Vector<String>();
		}
		return header;
	}

	public void setHeader(Vector<String> header) {
		this.header = header;
	}

	public void addHeaderCell(int pos, String name) {
		if (this.header == null) {
			this.header = new Vector<String>();
		}
		if (header.size() < pos) {
			header.setSize(pos);
		}
		this.header.add(pos, name);
	}

	public Map<String, String> getPffHeaderMap() {
		if (pffHeaderMap == null) {
			pffHeaderMap = new HashMap<String, String>();
		}
		return pffHeaderMap;
	}

	public void addHeaderToPff(String pff, String name) {
		if (this.pffHeaderMap == null) {
			this.pffHeaderMap = new HashMap<String, String>();
		}
		this.pffHeaderMap.put(pff, name);
	}

	public void setPffHeaderMap(Map<String, String> pffHeaderMap) {
		this.pffHeaderMap = pffHeaderMap;
	}
	
	public Map<String, String> getHeaderPffMap() {
		if (headerPffMap == null) {
			headerPffMap = new HashMap<String, String>();
		}
		return headerPffMap;
	}

	public void addPffToHeader(String name, String pff) {
		if (this.headerPffMap == null) {
			this.headerPffMap = new HashMap<String, String>();
		}
		this.headerPffMap.put(name, pff);
	}

	public void setHeaderPffMap(Map<String, String> headerPropMap) {
		this.headerPffMap = headerPropMap;
	}

	public Set<String> getPropertySet() {
		return propertySet;
	}

	public void addProperty(String property) {
		if (this.propertySet == null) {
			this.propertySet = new HashSet<String>();
		}
		this.propertySet.add(property);
	}

	public Map<String,String> getHeaderType() {
		return headerTypeMap;
	}

	public void addToHeaderType(String name, String isMember) {

		if (this.headerTypeMap == null) {
			this.headerTypeMap = new HashMap<String, String>();
		}
		this.headerTypeMap.put(name, isMember);
	}


}
