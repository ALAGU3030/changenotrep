package com.teamcenter.soa.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ResultDset {

	public final static int ITEM_ID_LENGTH = 16;
	private final String TC_ID_PROP = "item_id";
	private final String WERS_ID_PROP = "WERS Notice Number";
	private final String TC_REV_PROP = "item_revision_id";

	private String itemId = "";
	private String rev = "";
	private int size = 0;
	private int rootLevel = 0;

	private Map<String, List<String>> properties = new HashMap<String, List<String>>();
	private Map<String, Integer> propLevel = new HashMap<String, Integer>();

	public ResultDset() {

	}
	
	

	public ResultDset(ResultDset existing,int newLevel) {

		Map<String, List<String>> oldProperties = existing.getProperties();
		Map<String, Integer> oldPropLevel = existing.getPropLevel();
		for (Entry<String, List<String>> entry : oldProperties.entrySet()) {
			int pLevel =0;
			List<String> valueList = entry.getValue();
			String key = entry.getKey();
			if(oldPropLevel.containsKey(key)){
				pLevel= oldPropLevel.get(key);
				if(pLevel < newLevel){
					this.properties.put(key, valueList);
					this.propLevel.put(key, pLevel);
				}
			}

		}

		this.itemId = existing.getItemId();
		this.rev = existing.getRev();
	}

	public int getRootLevel() {
		return rootLevel;
	}

	public void setRootLevel(int rootLevel) {
		this.rootLevel = rootLevel;
	}

	public Map<String, Integer> getPropLevel() {
		return propLevel;
	}

	public void setPropLevel(Map<String, Integer> propLevel) {
		this.propLevel = propLevel;
	}

	public void addPropLevel(String name, Integer level) {
		this.propLevel.put(name, level);
	}

	public String getTcIdProp() {
		return TC_ID_PROP;
	}
	
	public String getWersIdProp() {
		return WERS_ID_PROP;
	}

	public String getTcRevProp() {
		return TC_REV_PROP;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Map<String, List<String>> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, List<String>> properties) {
		this.properties = properties;
	}
	
	public void addProperties(Map<String, List<String>> properties) {
		this.properties.putAll(properties);
	}



	@Override
	public String toString() {
		return "ResultDset [TC_ID_PROP=" + TC_ID_PROP + ", WERS_ID_PROP=" + WERS_ID_PROP + ", TC_REV_PROP="
				+ TC_REV_PROP + ", itemId=" + itemId + ", rev=" + rev + ", size=" + size + ", rootLevel=" + rootLevel
				+ ", properties=" + properties + ", propLevel=" + propLevel + "]";
	}


}
