package com.teamcenter.soa.model;

import java.util.Vector;
import com.teamcenter.soa.client.model.ModelObject;

public class PFFTreeNode {

	private PFFTreeNode parent = null;
	private Vector<PFFTreeNode> children = new Vector<PFFTreeNode>();
	private Vector<PFFTreeNode> parentNodes = new Vector<PFFTreeNode>();
	private ModelObject data;
	private String name;
	private String propertyName;
	private int level;

	public PFFTreeNode(int level, String name, String propertyName, ModelObject aData) {
		this.level = level;
		this.name = name;
		this.propertyName = propertyName;
		this.data = aData;
	}

	public PFFTreeNode(ModelObject aData) {
		this.data = aData;
	}

	public ModelObject getObject() {
		return data;
	}

	public Vector<PFFTreeNode> getParentNodes() {
		return this.parentNodes;
	}

	public void addChildren(PFFTreeNode... aChildren) {
		for (PFFTreeNode child : aChildren) {
			child.setParent(this);
			this.children.add(child);
		}

	}

	public void addChild(PFFTreeNode aChildren) {
		aChildren.setParent(this);
		this.children.add(aChildren);
	}

	public PFFTreeNode getParent() {
		return parent;
	}

	public void setParent(PFFTreeNode parent) {
		this.parent = parent;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Object getData() {

		return this.data;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public boolean hasChildren() {
		if (children == null || children.isEmpty()) {
			return false;
		}
		return true;
	}

	public PFFTreeNode getChildNodeByName(String name) {
		for (int i = 0; i < children.size(); i++) {
			PFFTreeNode pffTreeNode = children.get(i);
			String childName = pffTreeNode.getName();
			if (childName.equals(name)) {
				return pffTreeNode;
			}
		}
		return null;
	}

	public static PFFTreeNode findNode(Vector<PFFTreeNode> levelNodes, PFFTreeNode nodeToFind) {
		if (levelNodes == null || levelNodes.isEmpty()) {
			return null;
		}
		for (int i = 0; i < levelNodes.size(); i++) {
			PFFTreeNode treeNode = levelNodes.get(i);
			if (treeNode.getName().equals(nodeToFind.getName())) {
				return treeNode;
			}
		}
		return null;

	}

	public static void addNode(PFFTreeNode rootNode, PFFTreeNode node) {
		boolean noteExists = false;
		// first node to add
		if (!rootNode.hasChildren()) {
			rootNode.addChild(node);
			return;
		}

		Vector<PFFTreeNode> children = rootNode.getChildren();

		for (int i = 0; i < children.size(); i++) {
			PFFTreeNode childNode = children.get(i);
			if (childNode.getLevel() == node.getLevel()) {
				if (childNode.getName().equals(node.getName())) {
					noteExists = true;
				}
			} else if (childNode.getLevel() > node.getLevel()) {
				addNode(childNode, node);
			}
		}

		if (!noteExists) {
			rootNode.addChild(node);
		}

	}

	public void findLevelNodes(PFFTreeNode node, int level) {
		Vector<PFFTreeNode> nodeChildren = new Vector<PFFTreeNode>();

		if (node.hasChildren()) {
			nodeChildren = node.getChildren();
			for (int i = 0; i < nodeChildren.size(); i++) {
				PFFTreeNode childNode = nodeChildren.get(i);
				if (childNode.getLevel() == level) {
					parentNodes = nodeChildren;
					return;
				}
				findLevelNodes(childNode, level);
			}

		} else {
			if (node.getLevel() == level) {
				nodeChildren.add(node);
				parentNodes = nodeChildren;
			}
		}

	}

	public Vector<PFFTreeNode> getChildren() {

		return this.children;
	}


}