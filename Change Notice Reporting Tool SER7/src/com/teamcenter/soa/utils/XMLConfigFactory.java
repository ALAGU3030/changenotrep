package com.teamcenter.soa.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.teamcenter.soa.model.QueryConfigAttributeModel;
import com.teamcenter.soa.model.QueryConfigModel;

public class XMLConfigFactory {

	private Document xmlDocument = null;
	private File queryConfigFile = null;
	private Map<String, QueryConfigModel> queryModelNameMap;
	private Map<String, String> queryModelNameTypeMap;

	public XMLConfigFactory(String configFile, String mode)
			throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		this.queryConfigFile = new File(configFile);
		parseConfig();
		parseSpec(mode);
	}

	public Map<String, QueryConfigModel> getQueryModelMap() {
		return queryModelNameMap;
	}

	public Map<String, String> getQueryModelNameTypeMap() {
		return queryModelNameTypeMap;
	}

	private void parseConfig()
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

		queryModelNameMap = new LinkedHashMap<String, QueryConfigModel>();
		queryModelNameTypeMap = new LinkedHashMap<String, String>();

		String xmlFileUriString = queryConfigFile.toURI().toString();

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		xmlDocument = builder.parse(xmlFileUriString);

		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xPath.compile(QueryConfigModel.getConfigList()).evaluate(xmlDocument,
				XPathConstants.NODESET);
		
		
		for (int i = 0; i < nodeList.getLength(); ++i) {
			QueryConfigModel queryConfigModel = new QueryConfigModel();
			Node node = nodeList.item(i);
			NamedNodeMap propAttr = node.getAttributes();

			try {
				String crrntName = propAttr.getNamedItem(QueryConfigModel.getNameTag()).getNodeValue();
				queryConfigModel.setName(crrntName);

				String crrntConnectionString = propAttr.getNamedItem(QueryConfigModel.getConnectionTag())
						.getNodeValue();
				if (crrntConnectionString.startsWith(QueryConfigModel.twoTier)) {
					queryConfigModel.setType(QueryConfigModel.twoTier);
				} else if (crrntConnectionString.startsWith(QueryConfigModel.fourTier)) {
					queryConfigModel.setType(QueryConfigModel.fourTier);
				}
				queryConfigModel.setConnectionString(crrntConnectionString);

				String crrntSpecFileName = propAttr.getNamedItem(QueryConfigModel.getSpecFileTag()).getNodeValue();
				queryConfigModel.setSpecFile(new File(crrntSpecFileName));

			} catch (NullPointerException ne) {
				System.out.println("Error in config: (missing Name|Connection|SpecFileName)");
				System.exit(0);
			}

			queryModelNameMap.put(queryConfigModel.getName(), queryConfigModel);
			queryModelNameTypeMap.put(queryConfigModel.getName(), queryConfigModel.getType());

		}

	}

	private void parseSpec(String mode)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

		for (Entry<String, QueryConfigModel> entry : queryModelNameMap.entrySet()) {
			QueryConfigModel model = entry.getValue();

			String xmlFileUriString = model.getSpecFile().toURI().toString();
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			xmlDocument = builder.parse(xmlFileUriString);

			setQueryMode(model, mode);
			setQueryName(model, mode);
			setQueryAttributes(model, mode);
			setValidResults(model, mode);
			setQueryResultConfig(model, mode);
			setOutput(model, mode);

		}
	}

	private void setQueryMode(QueryConfigModel model, String mode) {
		model.setQueryMode(mode);
	}

	private void setQueryName(QueryConfigModel model, String mode) throws XPathExpressionException {
		String first = "/QueryConfig/Query[@QueryMode='";
		String end = "']/QueryParams/@QueryName";
		String queryNameXPath = first + mode + end;
		XPathExpression expr = XPathFactory.newInstance().newXPath().compile(queryNameXPath);
		Node node = (Node) expr.evaluate(xmlDocument, XPathConstants.NODE);
		model.setQueryQueryName(node.getNodeValue());
	}

	private void setQueryAttributes(QueryConfigModel model, String mode) throws XPathExpressionException {

		List<String> queryAttrNameList = new ArrayList<String>();
		List<String> queryAttrValueList = new ArrayList<String>();

		String first = "/QueryConfig/Query[@QueryMode='";
		String end = "']/QueryParams/QueryAttributes";

		String queryAttrXPath = first + mode + end;
		XPathExpression expr = XPathFactory.newInstance().newXPath().compile(queryAttrXPath);
		NodeList nl = (NodeList) expr.evaluate(xmlDocument, XPathConstants.NODESET);
		for (int i = 0; i < nl.getLength(); i++) {
			Element el = (Element) nl.item(i);
			NamedNodeMap attr = el.getAttributes();
			Node nameNode = attr.getNamedItem("Name");
			queryAttrNameList.add(nameNode.getNodeValue());
			Node valueNode = attr.getNamedItem("Value");
			queryAttrValueList.add(valueNode.getNodeValue());
		}

		model.setQueryAttrNameList(queryAttrNameList);
		model.setQueryAttrValueList(queryAttrValueList);

	}

	private void setValidResults(QueryConfigModel model, String mode) throws XPathExpressionException {

		String first = "/QueryConfig/Query[@QueryMode='";
		String end = "']/QueryParams/ResultFilter";

		String queryAttrXPath = first + mode + end;
		XPathExpression expr = XPathFactory.newInstance().newXPath().compile(queryAttrXPath);
		NodeList nl = (NodeList) expr.evaluate(xmlDocument, XPathConstants.NODESET);
		for (int i = 0; i < nl.getLength(); i++) {
			Element el = (Element) nl.item(i);
			NamedNodeMap attr = el.getAttributes();

			Node nameNode = attr.getNamedItem("Name");
			Node valueNode = attr.getNamedItem("Value");

			String name = nameNode.getNodeValue();
			String value = valueNode.getNodeValue();

			switch (name) {
			case "LatestRevisionOnly":
				if (value.equalsIgnoreCase("YES")) {
					model.setLatestRevOnly(true);
				} else {
					model.setLatestRevOnly(false);
				}
				break;
			case "MaxChars":
				model.setMaxChars(Integer.parseInt(value));
				break;
			case "AllowedChars":
				model.setAllowedChars(value);
				break;
			case "InvalidTabPrefix":
				model.setInvalidTabPrefix(value);
			default:
				break;
			}
		}

	}

	private void setQueryResultConfig(QueryConfigModel model, String mode) throws XPathExpressionException {

		String resultNameXPathStart = "/QueryConfig/Query[@QueryMode='";
		String resultNameXPathEnd = "']/QueryParams/Results";
		String resultNameXPath = resultNameXPathStart + mode + resultNameXPathEnd;

		XPathExpression resultNameExp = XPathFactory.newInstance().newXPath().compile(resultNameXPath);
		NodeList resultNameNList = (NodeList) resultNameExp.evaluate(xmlDocument, XPathConstants.NODESET);

		for (int i = 0; i < resultNameNList.getLength(); i++) {

			Element el = (Element) resultNameNList.item(i);

			NamedNodeMap attr = el.getAttributes();
			Node nameNode = attr.getNamedItem("Name");
			String resultName = nameNode.getNodeValue();

			NodeList childNodes = el.getChildNodes();
			QueryConfigAttributeModel qcAttrModel = new QueryConfigAttributeModel();

			for (int j = 0; j < childNodes.getLength(); j++) {

				Node child = childNodes.item(j);
				if (child.getNodeType() == Node.ELEMENT_NODE) {

					Element propElement = (Element) child;
					NamedNodeMap propAttr = propElement.getAttributes();
					int order = Integer.parseInt(propAttr.getNamedItem("Order").getNodeValue());
					String name = propAttr.getNamedItem("Name").getNodeValue();
					
					Node headerNode = propAttr.getNamedItem("CSVHeader");
					if(headerNode != null) {
						String isMember = headerNode.getNodeValue();
						qcAttrModel.addToHeaderType(name, isMember);
					}
					
					String crrntMode = propAttr.getNamedItem(mode).getNodeValue();
					qcAttrModel.addHeaderCell(order, name);
					qcAttrModel.addProperty(crrntMode);
					qcAttrModel.addPffToHeader(name, crrntMode);
					qcAttrModel.addHeaderToPff(crrntMode, name);

				}
			}

			model.addToQueryConfigAttrModelMap(resultName, qcAttrModel);

		}

	}

	private void setOutput(QueryConfigModel model, String mode) throws XPathExpressionException {

		String outputXpathPrefix = "/QueryConfig/Output/";
		String outputFileNameXPath = outputXpathPrefix + model.getOutputFileNameTag();
		XPathExpression fileNameExpr = XPathFactory.newInstance().newXPath().compile(outputFileNameXPath);
		Node fileNameNode = (Node) fileNameExpr.evaluate(xmlDocument, XPathConstants.NODE);

		String dateFormatXPath = outputXpathPrefix + model.getDateFormatTag();
		XPathExpression dateFormatExpr = XPathFactory.newInstance().newXPath().compile(dateFormatXPath);
		Node dateFormatNode = (Node) dateFormatExpr.evaluate(xmlDocument, XPathConstants.NODE);

		String fileNameSuffixXPath = outputXpathPrefix + model.getOutputSuffixTag();
		XPathExpression fileNameSuffixExpr = XPathFactory.newInstance().newXPath().compile(fileNameSuffixXPath);
		Node fileNameSuffixNode = (Node) fileNameSuffixExpr.evaluate(xmlDocument, XPathConstants.NODE);

		model.setOutputFileName(buildFileName(fileNameNode.getNodeValue(), dateFormatNode.getNodeValue(),
				fileNameSuffixNode.getNodeValue()));

	}

	private String buildFileName(String fileName, String dateFormat, String suffix) {
		String fullFileName = fileName;
		String dateString = "";
		if (!dateFormat.isEmpty()) {
			Date date = new Date();
			try {
				SimpleDateFormat format = new SimpleDateFormat(dateFormat);
				dateString = format.format(date);
			} catch (IllegalArgumentException ie) {
				String message = ie.getMessage();
				System.out.println("Incorrect Date Format in Config (" + message + ")");
				System.out.println(
						"correct values see: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html");
				System.exit(0);
			}

			fullFileName = fullFileName + dateString;
		}

		fullFileName = fullFileName + suffix;

		return fullFileName;

	}

}
