package com.teamcenter.soa.query;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;


import org.apache.log4j.Logger;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.teamcenter.clientx.Session;
import com.teamcenter.fms.clientcache.proxy.IFileCacheProxyCB;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core._2007_06.DataManagement.ExpandGRMRelationsData;
import com.teamcenter.services.strong.core._2007_06.DataManagement.ExpandGRMRelationsOutput;
import com.teamcenter.services.strong.core._2007_06.DataManagement.ExpandGRMRelationsPref;
import com.teamcenter.services.strong.core._2007_06.DataManagement.ExpandGRMRelationsResponse;
import com.teamcenter.services.strong.core._2007_06.DataManagement.RelationAndTypesFilter2;
import com.teamcenter.services.strong.query.SavedQueryService;
import com.teamcenter.services.strong.query._2006_03.SavedQuery.GetSavedQueriesResponse;
import com.teamcenter.services.strong.query._2007_09.SavedQuery.QueryResults;
import com.teamcenter.services.strong.query._2007_09.SavedQuery.SavedQueriesResponse;
import com.teamcenter.services.strong.query._2008_06.SavedQuery.QueryInput;
import com.teamcenter.soa.client.FileManagementUtility;
import com.teamcenter.soa.client.GetFileResponse;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.Property;
import com.teamcenter.soa.client.model.PropertyDescription;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.ImanFile;
import com.teamcenter.soa.client.model.strong.ImanQuery;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;
import com.teamcenter.soa.internal.client.model.PropertyImpl;
import com.teamcenter.soa.model.Constant;
import com.teamcenter.soa.model.PFFTree;
import com.teamcenter.soa.model.PFFTreeNode;
import com.teamcenter.soa.model.QueryConfigAttributeModel;
import com.teamcenter.soa.model.QueryConfigModel;
import com.teamcenter.soa.model.ResultDset;
import com.teamcenter.soa.model.Statistics;
import com.teamcenter.soa.utils.ClassUtil;
import com.teamcenter.soa.utils.PFFComparator;
import com.teamcenter.soa.utils.Stopwatch;

public class Query {

	private Logger logger = Logger.getLogger(Query.class);

	private Statistics stats = null;
	private Set<String> itemProcessed = new HashSet<String>();

	public Query(Statistics stats) {
		this.stats = stats;
	}

	/**
	 * Performs a TC Saved Query and returns the result based on the input Parameter
	 * 
	 * @param QueryAttributeModel queryAttributeModel
	 * @return result
	 */
	public ModelObject[] doQuery(QueryConfigModel configModel) {
		ModelObject[] foundObjs = null;
		
		Session.refreshServer();

		// Query Parameter:
		String queryName = configModel.getQueryQueryName();
		String[] attr = configModel.getQueryAttrNameList().toArray(new String[0]);
		String[] val = configModel.getQueryAttrValueList().toArray(new String[0]);
		
		System.out.println("queryName :"+queryName);
		System.out.println("attr :"+Arrays.toString(attr));
		System.out.println("val :"+Arrays.toString(val));


		ImanQuery query = null;
		// Get the service stub.
		SavedQueryService queryService = SavedQueryService.getService(Session.getConnection());
		DataManagementService dmService = DataManagementService.getService(Session.getConnection());
		
		
		try {

			// *****************************
			// Execute the service operation
			// *****************************
			GetSavedQueriesResponse savedQueries = queryService.getSavedQueries();
			
			
			if (savedQueries.queries.length == 0) {
				logger.error("There are no saved queries in the system.");
			}

	
			// Find one called 'Item Revision...'
			for (int i = 0; i < savedQueries.queries.length; i++) {
//				System.out.println("==="+savedQueries.queries[i].name);
				if (savedQueries.queries[i].name.equals(queryName)) {
					query = savedQueries.queries[i].query;
					break;
				}
			}
			

		} catch (ServiceException e) {
			logger.error(e);
		}

		if (query == null) {
			logger.error("There is not an " + queryName + " query.");
		}

		try {
			// Search for all Items
			QueryInput savedQueryInput[] = new QueryInput[1];
			savedQueryInput[0] = new QueryInput();
			savedQueryInput[0].query = query;
			savedQueryInput[0].limitList = new ModelObject[0];
			savedQueryInput[0].entries = attr;
			savedQueryInput[0].values = val;

			// *****************************
			// Execute the service operation
			// *****************************
			SavedQueriesResponse savedQueryResult = queryService.executeSavedQueries(savedQueryInput);
			//System.out.println("savedQueryResult : "+savedQueryResult.arrayOfResults[0]);
			
			QueryResults found = savedQueryResult.arrayOfResults[0];

			ServiceData sdata = dmService.loadObjects(found.objectUIDS);
			foundObjs = new ModelObject[sdata.sizeOfPlainObjects()];

			for (int i = 0; i < sdata.sizeOfPlainObjects(); i++) {
				foundObjs[i] = sdata.getPlainObject(i);
			}
			
			
//			int numOfAllRevs=foundObjs.length;
//			for (int ii = 0; ii < numOfAllRevs; ii++) {
//				ModelObject obj = foundObjs[ii];
//
//				String itemID = getPropertyValue(dmService, obj, "item_id");
//				String revId = getPropertyValue(dmService, obj, "item_revision_id");  //object_name
//				String objname=getPropertyValue(dmService, obj, "c3p_DPA5Complete");
//				System.out.println(itemID+" "+revId+" "+objname);
//			}

			
			return foundObjs;
			
			

		} catch (Exception e) {
			logger.error(e);

		}
		
		return foundObjs;

	}

	private ModelObject[] getLatestRevisions(DataManagementService dmService, ModelObject[] foundObjs) {

		Map<String, String> revMap = new HashMap<String, String>();
		Map<String, ModelObject> objMap = new HashMap<String, ModelObject>();

		// dmService.getProperties(foundObjs, new String[] { "item_id",
		// "item_revision_id" });
		int numOfAllRevs = foundObjs.length;

		for (int i = 0; i < numOfAllRevs; i++) {
			ModelObject obj = foundObjs[i];

			String itemID = getPropertyValue(dmService, obj, "item_id");
			String revId = getPropertyValue(dmService, obj, "item_revision_id");  
			//System.out.println(itemID+" "+revId);
			String existingRev = revMap.get(itemID);

			if (existingRev != null) {
				int crrntRevNum = 0;
				int existingRevNum = 0;

				try {
					crrntRevNum = Integer.parseInt(revId);
					existingRevNum = Integer.parseInt(existingRev);
				} catch (NumberFormatException ex) {
					crrntRevNum = findPosition(revId.charAt(0));
					existingRevNum = findPosition(existingRev.charAt(0));
				}
            //  System.out.println("existingRev"+itemID+" "+ crrntRevNum+" "+existingRevNum);
				if (crrntRevNum >= existingRevNum) {
					revMap.put(itemID, revId);
					objMap.put(itemID, obj);
				}
			} else {
				revMap.put(itemID, revId);
				objMap.put(itemID, obj);
			}

		}
     //System.out.println(revMap);
		List<ModelObject> objList = new ArrayList<ModelObject>(objMap.values());
		ModelObject[] resultArray = new ModelObject[objList.size()];
		resultArray = objList.toArray(resultArray);

		return resultArray;
	}

	public static int findPosition(char inputLetter) {
		// converting input letter in to uniform case.
		char inputLetterToLowerCase = Character.toLowerCase(inputLetter);
		// COnverting chat in to its ascii value
		int asciiValueOfinputChar = (int) inputLetterToLowerCase;
		// ASCII value of lower case letters starts from 97
		int position = asciiValueOfinputChar - 96;
		return position;
	}

	public Map<String, List<ResultDset>> getResults(ModelObject[] foundObjs, QueryConfigModel queryConfigModel) {
		Map<String, List<ResultDset>> sheetResult = null;
		DataManagementService dmService = DataManagementService.getService(Session.getConnection());

		// Total Number of Object Revisions
		int numOfAllRevs = foundObjs.length;
		stats.setNumOfAllRevs(numOfAllRevs);

		// Filter latest Object Revisions

		if (queryConfigModel.processLatestRevOnly()) {
			Stopwatch filterWatch = new Stopwatch();
			foundObjs = getLatestRevisions(dmService, foundObjs);
			double filterTime = filterWatch.elapsedTime();
			stats.setFilterTime(filterTime);

			// Number of Latest Object Revisions
			int numOfLatestRevs = foundObjs.length;
			stats.setNumOfLatestRevs(numOfLatestRevs);

			// Output of Numbers and Filter Time
			String filterMsg = ("Filtered for latest Object Revisions" + " - Duration: " + filterTime + " seconds");
			System.out.println(filterMsg);
			logger.info(filterMsg);

			String revFoundMsg = ("Number of all Object Revisions found: " + numOfAllRevs);
			System.out.println(revFoundMsg);
			logger.info(revFoundMsg);

			String lastRevsMsg = ("Number of latest Object Revisions to use: " + numOfLatestRevs + "\n");
			System.out.println(lastRevsMsg);
			logger.info(lastRevsMsg);
		} else {
			String procMsg = ("Processing all Object Revisions...");
			System.out.println(procMsg);
			logger.info(procMsg);

			String allRevMsg = ("Number of all Object Revisions found: " + numOfAllRevs);
			System.out.println(allRevMsg);
			logger.info(allRevMsg);
		}

		try {

			//printResult(dmService,foundObjs);
			sheetResult = getSheetResult(dmService, foundObjs, queryConfigModel);
		} catch (IllegalArgumentException e) {
			logger.error("Error getting Result:" + e.getMessage());
			e.printStackTrace();
		} catch (NotLoadedException e) {
			logger.error("Error getting Result:" + e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			logger.error("Error getting Result:" + e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			logger.error("Error getting Result:" + e.getMessage());
			e.printStackTrace();
		}

		return sheetResult;
	}

	@SuppressWarnings("unused")
	private void printResult(DataManagementService dmService, ModelObject[] foundObjs) {
		for (int i = 0; i < foundObjs.length; i++) {
			ModelObject modelObject = foundObjs[i];
			String id = getPropertyValue(dmService, modelObject, "item_id");
			String name = getPropertyValue(dmService, modelObject, "object_name");
			String rev = getPropertyValue(dmService, modelObject, "item_revision_id");
			System.out.println(id + ";" + name + ";" + rev);

		}
	}

	/**
	 * Get result as String 1. Load attributes for query result objects 2. Sort
	 * results 3. Format and return the result string
	 * 
	 * @param foundObjs
	 * @param topBopLine
	 * @param attrToLoad
	 * @return result
	 * @throws NotLoadedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unused")
	private Map<String, List<ResultDset>> getSheetResult(DataManagementService dmService, ModelObject[] foundObjs,
			QueryConfigModel queryConfigModel)
			throws NotLoadedException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Map<String, List<ResultDset>> sheetResult = new HashMap<String, List<ResultDset>>();
		String mode = queryConfigModel.getQueryMode();
		Map<String, QueryConfigAttributeModel> queryConfigAttrModelMap = queryConfigModel.getQueryConfigAttrModelMap();

		String[] headerMemberPFFArray = null;
		String[] headerNonMemberPFFArray = null;

		switch (QueryConfigModel.QueryMode.valueOf(mode)) {
		case PFF:
			// Results for each Property Set (e.g. Workflow and Item)
			Set<Entry<String, QueryConfigAttributeModel>> entrySet = queryConfigAttrModelMap.entrySet();

			
			int count = 1;

			for (Entry<String, QueryConfigAttributeModel> entry : entrySet) {
				Stopwatch attrWatch = new Stopwatch();
				String sheetName = entry.getKey();

				QueryConfigAttributeModel configAttrModel = entry.getValue();
				headerMemberPFFArray = getHeaderPFFArray(configAttrModel, "Yes");
				headerNonMemberPFFArray = getHeaderPFFArray(configAttrModel, "No");

				List<ResultDset> resultList = new ArrayList<ResultDset>();
				PFFTree attrTree = createAttrTree(configAttrModel);
				Vector<PFFTreeNode> results = getResults(dmService, foundObjs, attrTree, headerMemberPFFArray,
						headerNonMemberPFFArray, sheetName);
				// results for each item found
				for (int i = 0; i < results.size(); i++) {
					PFFTree tree = new PFFTree(results.get(i));
					// tree.printTree(tree, configAttrModel);
					List<ResultDset> treeResult = tree.getTreeResult(configAttrModel);
					resultList.addAll(tree.getTreeResult(configAttrModel));
				}

				//System.out.println(resultList);
				sheetResult.put(sheetName, resultList);
                 
				//System.out.println(sheetResult.toString());
				++count;

				double attrTime = attrWatch.elapsedTime();
				stats.setAttrTime(sheetName, attrTime);

				String attrMsg = "Got " + sheetName + " Attributes - Duration: " + attrTime + " seconds";
				System.out.println("\n" + attrMsg);
				logger.info(attrMsg);

			}

			String finishMsg = ("TC Actions finished - Start Excel output...");
			System.out.println("\r" + finishMsg);
			logger.info(finishMsg);

			break;

		}

		return sheetResult;

	}

	private String[] getHeaderPFFArray(QueryConfigAttributeModel configAttrModel, String isMember) {
		Vector<String> header = configAttrModel.getHeader();

		Map<String, String> isMemberMap = configAttrModel.getHeaderType();
		if (isMemberMap == null) {
			return null;
		}
		List<String> headerList = new ArrayList<String>();

		Map<String, String> pffHeaderMap = configAttrModel.getHeaderPffMap();
		for (int i = 0; i < header.size(); i++) {
			String name = header.get(i);
			String value = pffHeaderMap.get(name);
			if (isMemberMap.get(name).equalsIgnoreCase(isMember)) {
				headerList.add(value);
			}

		}

		return headerList.toArray(new String[headerList.size()]);

	}

	private Vector<PFFTreeNode> getResults(DataManagementService dmService, ModelObject[] foundObjs, PFFTree attrTree,
			String[] headerMemberPFFArray, String[] headerNonMemberPFFArray, String sheetName)
			throws IllegalArgumentException, IllegalAccessException, NotLoadedException {
		Vector<PFFTreeNode> results = new Vector<PFFTreeNode>();
		for (int i = 0; i < foundObjs.length; i++) {
			PFFTreeNode rootAttrNode = attrTree.getRootTreeNode();
			PFFTreeNode resultRootNode = new PFFTreeNode(0, "", rootAttrNode.getName(), foundObjs[i]);
			String treePath = "";
			if (sheetName.equalsIgnoreCase("Dset")) {
				try {
					processItemDset(dmService, resultRootNode, rootAttrNode, headerMemberPFFArray,
							headerNonMemberPFFArray);
					results.add(resultRootNode);
				} catch (IOException e) {
					logger.error(e);
					e.printStackTrace();
				}
			} else {
				getResultTree(dmService, resultRootNode, rootAttrNode, treePath);
				results.add(resultRootNode);
			}
		}
		return results;
	}

	private void getResultTree(DataManagementService dmService, PFFTreeNode resultNode, PFFTreeNode attrNode,
			String localTreePath) throws IllegalArgumentException, IllegalAccessException, NotLoadedException {

		PFFTreeNode resultChildNode = null;
		String treePath = "";
		for (PFFTreeNode crrntAttrNode : attrNode.getChildren()) {
			ModelObject foundObj = resultNode.getObject();
			treePath = getTreePath(localTreePath, crrntAttrNode);
			if (!crrntAttrNode.hasChildren()) {
				//System.out.println("crrnAttr :"+crrntAttrNode.getName());
				String propertyValue = getPropertyValue(dmService, foundObj, crrntAttrNode.getName());
				//System.out.println(crrntAttrNode.getName()+"     ===    "+propertyValue);
				PFFTreeNode resultPropertyNode = new PFFTreeNode(crrntAttrNode.getLevel(), propertyValue, treePath,
						null);
				resultNode.addChild(resultPropertyNode);
			} else {
				ModelObject[] relatedFound = getRelated(dmService, foundObj, crrntAttrNode);
				for (int i = 0; i < relatedFound.length; i++) {
					if (relatedFound[i] != null) {
						resultChildNode = new PFFTreeNode(crrntAttrNode.getLevel(), "", treePath, relatedFound[i]);
						resultNode.addChild(resultChildNode);
						getResultTree(dmService, resultChildNode, crrntAttrNode, treePath);

					}
				}
			}

		}

	}

	private String getTreePath(String localTreePath, PFFTreeNode crrntAttrNode) {
		if (localTreePath.isEmpty()) {
			return crrntAttrNode.getName();
		} else {
			return localTreePath + "." + crrntAttrNode.getName();
		}

	}

	private ModelObject[] getRelated(DataManagementService dmService, ModelObject foundObj, PFFTreeNode crrntAttrNode)
			throws IllegalArgumentException, IllegalAccessException {
		Object related = null;
		ModelObject[] relatedFound = new ModelObject[1];

		Method methodToGetRelated = ClassUtil.getAccessibleMethods(foundObj.getClass(), crrntAttrNode.getName());
		try {
			related = methodToGetRelated.invoke(foundObj, (Object[]) null);
		} catch (InvocationTargetException e) {
			dmService.getProperties(new ModelObject[] { foundObj }, new String[] { crrntAttrNode.getName() });
			try {
				related = methodToGetRelated.invoke(foundObj, (Object[]) null);
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
				logger.error(e);
			}
		}

		if (related instanceof ModelObject[]) {
			relatedFound = (ModelObject[]) related;
		} else if (related instanceof ModelObject) {
			relatedFound = new ModelObject[] { (ModelObject) related };
		}

		return relatedFound;
	}

	private PFFTree createAttrTree(QueryConfigAttributeModel configAttrModel) {
		List<String> propertyList = new ArrayList<String>(configAttrModel.getPropertySet());
		Collections.sort(propertyList, new PFFComparator());
		PFFTree attrTree = new PFFTree(propertyList.toArray(new String[0]));
		return attrTree;
	}

	public String buildResultLineString(List<String> list) {
		StringBuilder builder = new StringBuilder("|");
		for (int i = 0; i < list.size(); i++) {
			builder.append(list.get(i));
			builder.append("|");
		}
		return builder.toString();
	}

	private String getPropertyValue(DataManagementService dmService, ModelObject obj, String propertyKey) {

		String propertyResult = "NA";
		Property propertyObject = null;

		try {
			propertyObject = obj.getPropertyObject(propertyKey);
		} catch (NotLoadedException e) {
			try {
				dmService.getProperties(new ModelObject[] { obj }, new String[] { propertyKey });
				try {
					propertyObject = obj.getPropertyObject(propertyKey);
				} catch (NotLoadedException e1) {
					return propertyResult;
				}
			} catch (NullPointerException np) {
				return propertyResult;
			}

		} catch (IllegalArgumentException ia) {
			return propertyResult;
		}

		PropertyDescription propertyDescription = propertyObject.getPropertyDescription();
		if (propertyDescription == null) {
			String displayableValue = propertyObject.getDisplayableValue();
			if (displayableValue == null) {
				return propertyResult;
			}

			return displayableValue.replaceAll("[\n\r]", "");
		} else {
			
			if (propertyDescription.isArray()) {
				if (propertyObject instanceof PropertyImpl) {
					PropertyImpl arrayProp = (PropertyImpl) propertyObject;
					arrayProp.getDisplayableValues().toString();
					String arryProp = arrayProp.getDisplayableValues().toString();
					//System.out.print(arryProp);
					propertyResult = arryProp.replaceAll("[\n\r]", "");
				} else {
					String arrayErrorMsg = ("Property Class is ARRAY, but not PropertyImpl!!! Ask Frank to fix...");
					System.out.println(arrayErrorMsg);
					logger.error(arrayErrorMsg);
					return propertyResult;
				}

			} else {
				String displayableValue = propertyObject.getDisplayableValue();
				if (displayableValue == null) {
					return propertyResult;
				}
				propertyResult = displayableValue.replaceAll("[\n\r]", "");
			}
		}

		return propertyResult;

	}

	private void processItemDset(DataManagementService dmService, PFFTreeNode resultRootNode, PFFTreeNode rootAttrNode,
			String[] headerMemberPFFArray, String[] headerNonMemberPFFArray) throws IOException {
		ModelObject foundObj = resultRootNode.getObject();
		if (foundObj instanceof ItemRevision) {
			ItemRevision rev = (ItemRevision) foundObj;
			dmService.getProperties(new ModelObject[] { rev }, new String[] { "items_tag", "item_id" });
			try {
				Item item = rev.get_items_tag();
				String itemId = rev.get_item_id();
				if (itemProcessed.contains(itemId)) {
					return;
				} else {
					itemProcessed.add(itemId);
					File csvFile = getFileOfItemDset(dmService, item);
					if (csvFile == null) {
						System.out.println("WARNING: Missing MISC Dataset or File on Item: " + itemId);
						logger.warn("WARNING: Missing MISC Dataset or File on Item: " + itemId);
						return;
					}
					processCSVFile(csvFile, resultRootNode, rootAttrNode, headerMemberPFFArray,
							headerNonMemberPFFArray);
				}

			} catch (NotLoadedException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}

	}

	private File getFileOfItemDset(DataManagementService dmService, Item item) throws IOException, NotLoadedException {

		Dataset miscDset = null;
		TreeMap<String, Dataset> miscDsets = new TreeMap<String, Dataset>();

		RelationAndTypesFilter2 filter = new RelationAndTypesFilter2();
		filter.relationName = "IMAN_reference";
		filter.objectTypeNames = new String[] { "MISC" };

		RelationAndTypesFilter2[] filterArray = new RelationAndTypesFilter2[1];
		filterArray[0] = filter;

		ExpandGRMRelationsPref pref = new ExpandGRMRelationsPref();
		pref.info = filterArray;
		pref.expItemRev = false;

		ExpandGRMRelationsResponse response = dmService.expandGRMRelationsForPrimary(new ModelObject[] { item }, pref);

		ExpandGRMRelationsOutput[] output = response.output;
		ExpandGRMRelationsData[] otherSideObjData = output[0].otherSideObjData;
		for (int i = 0; i < otherSideObjData.length; i++) {
			ModelObject[] otherSideObjects = otherSideObjData[i].otherSideObjects;
			for (int j = 0; j < otherSideObjects.length; j++) {
				if (otherSideObjects[j] != null && otherSideObjects[j] instanceof Dataset) {
					miscDset = (Dataset) otherSideObjects[j];
					dmService.getProperties(new ModelObject[] { miscDset }, new String[] { "object_name" });
					String dSetName = miscDset.get_object_name();
					miscDsets.put(dSetName, miscDset);

				}

			}
		}

		if (miscDsets.isEmpty()) {
			return null;
		}

		String lastEntry = miscDsets.lastKey();
		return getMiscDsetFile(dmService, miscDsets.get(lastEntry));

	}

	private File getMiscDsetFile(DataManagementService dmService, Dataset dset) throws IOException, NotLoadedException {
		File miscFile = null;
		FileManagementUtility fMSFileManagement = new FileManagementUtility(Session.getConnection());
		dmService.getProperties(new ModelObject[] { dset }, new String[] { "ref_list" });
		ModelObject[] refs = dset.get_ref_list();

		if (refs.length == 1) {
			ImanFile[] imanFiles = new ImanFile[] { (ImanFile) refs[0] };
			ImanFile imanFile = imanFiles[0];
			dmService.getProperties(new ModelObject[] { imanFile }, new String[] { "file_name", "original_file_name" });
			String original_file_name = imanFile.get_original_file_name();
			String location = System.getProperty("java.io.tmpdir") + File.separator + original_file_name;

			GetFileResponse getFileResponse = fMSFileManagement.getFileToLocation(imanFiles[0], location,
					(IFileCacheProxyCB) null, (Object) null);
			miscFile = getFileResponse.getFile(0);
		} else {
			String message = "Error getting Misc File";
			System.out.println(message);
			logger.error(message);
		}

		return miscFile;

	}

	private void processCSVFile(File csvFile, PFFTreeNode resultRootNode, PFFTreeNode rootAttrNode,
			String[] headerMemberPFFArray, String[] headerNonMemberPFFArray) throws IOException {

		Reader reader = Files.newBufferedReader(csvFile.toPath());
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
		Map<String, Integer> headerMap = new HashMap<String, Integer>();

		int headerRow = 9999;

		List<CSVRecord> records = csvParser.getRecords();
		for (int i = 0; i < records.size(); i++) {
			CSVRecord csvRecord = records.get(i);
			int size = csvRecord.size();

			if (size < 2)
				continue;

			String name = csvRecord.get(0);
			String value = csvRecord.get(1);

			if (name.isEmpty()) {
				name = "EMPTY";
			}
			if (value.isEmpty()) {
				value = "EMPTY";
			}

			if (name.equals("No of Rows")) {
				headerRow = i + 1;
			}

			if (Arrays.stream(headerNonMemberPFFArray).anyMatch(name::equals)) {
				addCSVResultNode(resultRootNode, rootAttrNode, name, value);
				continue;
			}

			// if (Arrays.stream(headerMemberPFFArray).anyMatch(name::equals)) { // Table
			// Header found! << BAD!
			if (i == headerRow) { // Real Table Header found!
				for (int j = 0; j < csvRecord.size(); j++) {
					String headerCol = csvRecord.get(j);
					boolean isConfigured = Arrays.stream(headerMemberPFFArray).anyMatch(headerCol::equals);
					if (isConfigured) {
						headerMap.put(headerCol, j);
					}
				}
				continue;
			}

			if (!headerMap.isEmpty()) {
				for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
					String csvKey = entry.getKey();
					String csvValue = csvRecord.get(entry.getValue());
					if (csvValue.isEmpty()) {
						csvValue = Constant.EMPTY;
					}
					addCSVResultNode(resultRootNode, rootAttrNode, csvKey, csvValue);

				}
			}

		}
		csvParser.close();

	}

	private void addCSVResultNode(PFFTreeNode resultRootNode, PFFTreeNode rootAttrNode, String name, String value) {
		PFFTreeNode crrntAttrNode = rootAttrNode.getChildNodeByName(name);
		if (crrntAttrNode != null) {
			PFFTreeNode resultPropertyNode = new PFFTreeNode(crrntAttrNode.getLevel(), value, crrntAttrNode.getName(),
					null);
			resultRootNode.addChild(resultPropertyNode);
		}
	}

}
