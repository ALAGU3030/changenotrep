package com.teamcenter.soa.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class PFFTree {

	private PFFTreeNode root;
	private Map<String, String> pffHeaderMapping = null;

	private List<Integer> resultLevels = null;
	private List<String> resultValues = null;
	private List<String> resultNames = null;
	private List<String> resultPropertyNames = null;
	private List<Integer> resultParentLevel = null;

	public PFFTree(String[] pffAttr) {

		this.root = createTree(pffAttr);
	}

	public PFFTree(PFFTreeNode root) {
		this.root = root;
	}

	public PFFTree() {

	}

	public PFFTreeNode getRootTreeNode() {
		return root;
	}

	public String getFlatTree() {
		StringBuilder builder = new StringBuilder();
		StringBuilder processTree = this.processTree(this.root, builder);
		return processTree.toString();
	}

	public static void printTreeList(PFFResultModel result) {
		List<Map<String, String>> resultMapList = result.getResultMapList();
		for (int i = 0; i < resultMapList.size(); i++) {
			Map<String, String> resultMap = resultMapList.get(i);
			System.out.println("List " + i + ":");
			for (Entry<String, String> entry : resultMap.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue();
				System.out.println("YYY: " + name + "|" + value);

			}

		}
	}

	public List<ResultDset> getTreeResult(QueryConfigAttributeModel configAttrModel) {

		pffHeaderMapping = configAttrModel.getPffHeaderMap();
		resultLevels = new ArrayList<Integer>();
		resultNames = new ArrayList<String>();
		resultValues = new ArrayList<String>();
		resultPropertyNames = new ArrayList<String>();
		resultParentLevel = new ArrayList<Integer>();

		processTree(this.root);

		List<ResultDset> resultDsetList = new ArrayList<ResultDset>();
		ResultDset resultDset = new ResultDset();
		resultDsetList.add(resultDset);
		List<String> valueList = new ArrayList<String>();
		Map<String, List<String>> valueMap = new HashMap<String, List<String>>();

		for (int i = 0; i < resultLevels.size(); i++) {
			
			if (i > 0 && (resultLevels.get(i) < resultLevels.get(i - 1))) {
				int newLevel =resultLevels.get(i);
				resultDset = new ResultDset(resultDset,newLevel);

				valueMap = new HashMap<String, List<String>>();
				resultDsetList.add(resultDset);

				valueList = getValueList(resultNames.get(i), valueMap);
				valueList.add(resultValues.get(i));
				valueMap.put(resultNames.get(i), valueList);

				resultDset.addProperties(valueMap);
				resultDset.addPropLevel(resultNames.get(i), resultLevels.get(i));
				int listSize = valueList.size();
				if (resultDset.getSize() < listSize) {
					resultDset.setSize(listSize);
				}

			} else {
				valueList = getValueList(resultNames.get(i), valueMap);
				valueList.add(resultValues.get(i));
				valueMap.put(resultNames.get(i), valueList);

				resultDset.addProperties(valueMap);
				resultDset.addPropLevel(resultNames.get(i), resultLevels.get(i));
				
				String tcItemID = pffHeaderMapping.get(resultDset.getTcIdProp());
				
				if(tcItemID == null) {
					tcItemID = pffHeaderMapping.get(resultDset.getWersIdProp());
				}
				
				String idName = resultNames.get(i);
				
				if(idName != null && resultNames.get(i).equals(tcItemID)){
					resultDset.setItemId(resultValues.get(i));
				}
				
				int listSize = valueList.size();
				if (resultDset.getSize() < listSize) {
					resultDset.setSize(listSize);
				}

			}
		}

		return resultDsetList;
	}

	private List<String> getValueList(String name, Map<String, List<String>> valueMap) {
		List<String> valueList = new ArrayList<String>();
		List<String> existingValueList = valueMap.get(name);
		if (existingValueList != null) {
			return existingValueList;
		}
		return valueList;

	}

	private void processTree(PFFTreeNode aRoot) {
		if (aRoot.getName().length() > 0) {
			String pff = aRoot.getPropertyName();
			resultLevels.add(aRoot.getLevel());
			resultParentLevel.add(aRoot.getParent().getLevel());
			resultPropertyNames.add(pff);
			resultNames.add(pffHeaderMapping.get(pff));
			resultValues.add(aRoot.getName());

		}

		for (int i = 0; i < aRoot.getChildren().size(); i++) {
			this.processTree(aRoot.getChildren().get(i));
		}

	}

	private StringBuilder processTree(PFFTreeNode aRoot, StringBuilder builder) {
		if (aRoot.getName().length() > 0) {
			builder.append("Level: " + aRoot.getLevel() + " -PLevel: " + aRoot.getParent().getLevel() + " - Property: " + aRoot.getPropertyName()
					+ " - Value: " + aRoot.getName() + "\n");
		}

		for (int i = 0; i < aRoot.getChildren().size(); i++) {
			this.processTree(aRoot.getChildren().get(i), builder);
		}
		return builder;
	}

	public void printTree(PFFTree tree, QueryConfigAttributeModel configAttrModel) {

		this.print(this.root, configAttrModel);
	}

	private void print(PFFTreeNode aRoot, QueryConfigAttributeModel configAttrModel) {
		pffHeaderMapping = configAttrModel.getPffHeaderMap();
		if (aRoot.getName().length() > 0) {
			String pff = aRoot.getPropertyName();
			System.out.println("Level: " + aRoot.getLevel() + " -PLevel: " + aRoot.getParent().getLevel() + "- Property: " + pffHeaderMapping.get(pff)
					+ " - Value: " + aRoot.getName());
			// System.out.println("Level: " + aRoot.getLevel() + "- Property: "
			// + aRoot.getPropertyName() + " - Value: " + aRoot.getName());
		}

		for (int i = 0; i < aRoot.getChildren().size(); i++) {
			this.print(aRoot.getChildren().get(i), configAttrModel);
		}
	}

	public static void traverse(PFFTreeNode root, String path) {
		if (root.getPropertyName() != null && !root.getPropertyName().equals("Root")) {
			String propertyName = root.getPropertyName();
			String name = root.getName();
			path += propertyName + " - ";
			if (name.length() > 0) {
				path += "Value: " + name;
			}
		}

		for (PFFTreeNode child : root.getChildren())
			traverse(child, path);

		// end of current traversal
		if (root.getChildren().isEmpty())
			System.out.print("\n" + path + " ");
	}

	private PFFTreeNode createTree(String[] pffAttr) {
		PFFTreeNode rootNode = new PFFTreeNode(0, "Root", null, null);
		Vector<PFFTreeNode> rootChildren = null;
		for (int i = 0; i < pffAttr.length; i++) {
			String[] crntAttr = pffAttr[i].split("\\.");
			for (int j = 0; j < crntAttr.length; j++) {

				if (j == 0) {
					rootChildren = rootNode.getChildren();
					PFFTreeNode newNode = new PFFTreeNode(j + 1, crntAttr[j], null, null);
					PFFTreeNode parentNodeFound = PFFTreeNode.findNode(rootChildren, newNode);
					if (parentNodeFound == null) {
						rootNode.addChild(newNode);
					}
				} else {
					PFFTreeNode newNode = new PFFTreeNode(j + 1, crntAttr[j], null, null);
					PFFTreeNode parentNode = new PFFTreeNode(j, crntAttr[j - 1], null, null);

					newNode.findLevelNodes(rootNode, j);
					Vector<PFFTreeNode> previousLevelNodes = newNode.getParentNodes();
					PFFTreeNode parentNodeFound = PFFTreeNode.findNode(previousLevelNodes, parentNode);

					parentNode.findLevelNodes(rootNode, j + 1);
					Vector<PFFTreeNode> thisLevelNodes = parentNode.getParentNodes();
					PFFTreeNode newNodeFound = PFFTreeNode.findNode(thisLevelNodes, newNode);

					if (parentNodeFound != null) {
						if (newNodeFound == null || (newNodeFound != null && j == crntAttr.length - 1)) {
							parentNodeFound.addChild(newNode);
						}

					}
				}

			}

		}

		return rootNode;
	}

}