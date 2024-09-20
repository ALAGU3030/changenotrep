package com.teamcenter.soa.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.teamcenter.clientx.Session;
import com.teamcenter.services.strong.core.SessionService;
import com.teamcenter.soa.common.ObjectPropertyPolicy;
import com.teamcenter.soa.model.QueryConfigAttributeModel;
import com.teamcenter.soa.model.QueryConfigModel;

public class Policy {
	ObjectPropertyPolicy policy = null;

	public Policy() {
		policy = new ObjectPropertyPolicy();
	}

	public void addPolicy(String objType, String[] properties) {
		policy.addType(objType, properties);
		SessionService.getService(Session.getConnection()).setObjectPropertyPolicy(policy);
	}

	public String[] getItemRevisionPolicyAttr(QueryConfigModel queryConfigModel) {
		Map<String, QueryConfigAttributeModel> configAttr = queryConfigModel.getConfigAttr();
		QueryConfigAttributeModel queryConfigAttributeModel = configAttr.get("Item");
		
		// Do NOT (!) touch original queryConfigAttributeModel - make copy of propertySet:
		Set<String> propertySet = queryConfigAttributeModel.getPropertySet();
		Set<String> propertySetCopy = new HashSet<String>(propertySet);
		
		Set<String> removalSet = new HashSet<String>();
		
		
		propertySetCopy.forEach((String name) -> {
			if (name.contains(".")) {
				removalSet.add(name);
			}
		});
		
		propertySetCopy.add("item_id");
		propertySetCopy.add("item_revision_id");

		propertySetCopy.removeAll(removalSet);

		String[] properties = propertySetCopy.toArray(new String[0]);
		return properties;

	}

}
