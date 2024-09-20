package com.teamcenter.soa.model;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import com.teamcenter.soa.utils.Stopwatch;
import com.teamcenter.soa.utils.StringFormatUtil;

public class Excel {
	private Logger logger = Logger.getLogger(Excel.class);
	private Statistics stats;
	private XSSFCellStyle textStyle = null;
	private String fileName;
	private Map<String, List<ResultDset>> pffSheetResult = null;;
	private QueryConfigModel queryConfigModel = null;;
	private List<String> remoteNoticeNumOnItemTab = new ArrayList<String>();

	public Excel(String fileName, Map<String, List<ResultDset>> pffSheetResult, QueryConfigModel queryConfigModel,
			Statistics stats) {
		this.fileName = fileName;
		this.pffSheetResult = pffSheetResult;
		this.queryConfigModel = queryConfigModel;
		this.stats = stats;
	}

	public void writeExcel() throws IOException {
		Stopwatch writeWatch = new Stopwatch();
		FileOutputStream out = null;
		File tempFile = File.createTempFile("cnReport", ".xlsx");
		tempFile.deleteOnExit();

		XSSFWorkbook workbook = new XSSFWorkbook();
		textStyle = workbook.createCellStyle();

		Map<String, QueryConfigAttributeModel> qcAttrModel = queryConfigModel.getQueryConfigAttrModelMap();

		for (Entry<String, QueryConfigAttributeModel> entry : qcAttrModel.entrySet()) {
			String sheetName = entry.getKey();
			
			QueryConfigAttributeModel qcAttr = entry.getValue();
			List<ResultDset> sheetResult = pffSheetResult.get(sheetName);
			
			createSheet(workbook, sheetName, qcAttr, queryConfigModel, sheetResult);
		}

		arrageSheetOrder(workbook, queryConfigModel);

		createStatSheet(workbook, queryConfigModel);

		try {
			out = new FileOutputStream(tempFile);
			workbook.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
		}

		double writeTime = writeWatch.elapsedTime();
		stats.setWriteTime(writeTime);
		String msg = ("Written Result - Duration: " + writeTime);
		System.out.println(msg);
		logger.info(msg);

		Stopwatch copyWatch = new Stopwatch();
		Path source = Paths.get(tempFile.getAbsolutePath());
		Path target = Paths.get(fileName);

		AnsiConsole.systemInstall();
		if (copyFile(source, target)) {
			double copyTime = copyWatch.elapsedTime();
			stats.setCopyTime(copyTime);
			msg = ("Copied Result - Duration: " + copyTime + " seconds");
			System.out.println(msg);
			logger.info(msg);

			msg = (fileName + " written and copied successfully on disk.\n");
			System.out.println(Ansi.ansi().fgBrightGreen().a(msg).reset());
			logger.info(msg);

		} else {
			msg = ("Sorry, the Result is lost!");
			System.out.println(Ansi.ansi().fgBrightYellow().a(msg).reset());
			logger.info(msg);
		}
		AnsiConsole.systemUninstall();

	}

	/**
	 * Copy source file to target location. If {@code prompt} is true then prompt
	 * user to overwrite target if it exists. The {@code preserve} parameter
	 * determines if file attributes should be copied/preserved.
	 */
	private boolean copyFile(Path source, Path target) {

		boolean success = false;
		CopyOption[] options = new CopyOption[] { COPY_ATTRIBUTES, REPLACE_EXISTING };

		boolean readableSource = Files.isReadable(source);

		if (!readableSource) {
			System.out.println("Unable to read temporary result File: " + source.toString());
			return success;
		}

		try {
			Files.copy(source, target, options);
			success = true;
		} catch (Exception e1) {
			success = false;
			AnsiConsole.systemInstall();
			System.out.println(Ansi.ansi().fgBrightYellow().a("Unable to write Result!: " + e1.getMessage()).reset());
			AnsiConsole.systemUninstall();
			Console console = System.console();
			String input = console.readLine("Do you want to retry (y/n)?");
			if (input.equalsIgnoreCase("y")) {
				success = copyFile(source, target);
			} else {
				return success;
			}
		}

		return success;

	}

	private void createStatSheet(XSSFWorkbook workbook, QueryConfigModel queryConfigModel) {

		XSSFSheet sheet = workbook.createSheet("Statistics");
		int rowCount = 0;

		XSSFRow headerRow = sheet.createRow(rowCount++);
		XSSFCell headerCell = headerRow.createCell(0);
		headerCell.setCellValue("CN Notice Information Report Statistics");

		// TC Connection Type (2-Tier or 4-Tier)
		XSSFRow connectionRow = sheet.createRow(rowCount++);
		XSSFCell connectionTitleCell = connectionRow.createCell(0);
		connectionTitleCell.setCellValue("TC Connection Type");
		XSSFCell connectionValueCell = connectionRow.createCell(1);

		if (stats == null) {
			System.out.println("stats is null");
		}

		connectionValueCell.setCellValue(stats.getConnection());

		// TC Login Time
		double loginTime = stats.getLoginTime();
		XSSFRow loginTimeRow = sheet.createRow(rowCount++);
		XSSFCell loginTimeTitleCell = loginTimeRow.createCell(0);
		loginTimeTitleCell.setCellValue("Time to login to TC [Seconds]");
		XSSFCell loginTimeValueCell = loginTimeRow.createCell(1);
		loginTimeValueCell.setCellValue(loginTime);

		// TC Query Time for all Object Revisions
		double queryTime = stats.getQueryTime();
		XSSFRow queryTimeRow = sheet.createRow(rowCount++);
		XSSFCell queryTimeTitleCell = queryTimeRow.createCell(0);
		queryTimeTitleCell.setCellValue("Time used for Query [Seconds]");
		XSSFCell queryTimeValueCell = queryTimeRow.createCell(1);
		queryTimeValueCell.setCellValue(queryTime);

		// Filter Time for Latest Object Revisions (if configured)
		if (queryConfigModel.processLatestRevOnly()) {
			double filterTime = stats.getFilterTime();
			XSSFRow filterTimeRow = sheet.createRow(rowCount++);
			XSSFCell filterTimeTitleCell = filterTimeRow.createCell(0);
			filterTimeTitleCell.setCellValue("Time Filter only latest Revisions [Seconds]");
			XSSFCell filterTimeValueCell = filterTimeRow.createCell(1);
			filterTimeValueCell.setCellValue(filterTime);
		}

		// Time to get Attributes
		Map<String, Double> attrTimeMap = stats.getAttrTime();

		for (Entry<String, Double> entry : attrTimeMap.entrySet()) {
			String type = entry.getKey();
			Double value = entry.getValue();
			XSSFRow attrRow = sheet.createRow(rowCount++);
			XSSFCell attrTitleCell = attrRow.createCell(0);
			attrTitleCell.setCellValue("Time to get " + type + " Attribute values [Seconds]");
			XSSFCell attrValueCell = attrRow.createCell(1);
			attrValueCell.setCellValue(value);
		}

		// Excel Write Time
		XSSFRow writeTimeRow = sheet.createRow(rowCount++);
		XSSFCell writeTimeTitleCell = writeTimeRow.createCell(0);
		writeTimeTitleCell.setCellValue("Time to write Excel TEMP File [Seconds]");
		XSSFCell writeTimeValueCell = writeTimeRow.createCell(1);
		double writeTime = stats.getWriteTime();
		writeTimeValueCell.setCellValue(writeTime);

		// Excel Copy Time
		XSSFRow copyTimeRow = sheet.createRow(rowCount++);
		XSSFCell copyTimeTitleCell = copyTimeRow.createCell(0);
		copyTimeTitleCell.setCellValue("Time to copy Excel TEMP File to final Excel File [Seconds]");
		XSSFCell copyTimeValueCell = copyTimeRow.createCell(1);
		double copyTime = stats.getCopyTime();
		copyTimeValueCell.setCellValue(copyTime);

		// Overall Runtime
		XSSFRow allTimeRow = sheet.createRow(rowCount++);
		XSSFCell allTimeTitleCell = allTimeRow.createCell(0);
		allTimeTitleCell.setCellValue("Summary Time used [Seconds]");
		XSSFCell allTimeValueCell = allTimeRow.createCell(1);
		double allTime = getTotalTime();
		allTimeValueCell.setCellValue(allTime);

		// Number of all Object Revisions found
		XSSFRow numOfAllRevRow = sheet.createRow(rowCount++);
		XSSFCell numOfAllRevTitleCell = numOfAllRevRow.createCell(0);
		numOfAllRevTitleCell.setCellValue("Number of all Revisions found [Quantity]");
		XSSFCell numOfAllRevValueCell = numOfAllRevRow.createCell(1);
		numOfAllRevValueCell.setCellValue(stats.getNumOfAllRevs());

		// Number of all LATEST Object Revisions found (if configured)
		if (queryConfigModel.processLatestRevOnly()) {
			XSSFRow numOfLatestRevRow = sheet.createRow(rowCount++);
			XSSFCell numOfLatestRevTitleCell = numOfLatestRevRow.createCell(0);
			numOfLatestRevTitleCell.setCellValue("Number of latest Revisions used [Quantity]");
			XSSFCell numOfLatestRevValueCell = numOfLatestRevRow.createCell(1);
			numOfLatestRevValueCell.setCellValue(stats.getNumOfLatestRevs());
		}

		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);

	}

	public double getTotalTime() {
		double totalTime = 0;

		totalTime += stats.getLoginTime();
		totalTime += stats.getQueryTime();
		totalTime += stats.getFilterTime();

		Map<String, Double> attrTime = stats.getAttrTime();
		for (Entry<String, Double> entry : attrTime.entrySet()) {
			Double value = entry.getValue();
			totalTime += value;

		}

		totalTime += stats.getWriteTime();
		totalTime += stats.getCopyTime();

		return totalTime;
	}

	private void createSheet(XSSFWorkbook workbook, String sheetName, QueryConfigAttributeModel qcAttr,
			QueryConfigModel queryConfigModel, List<ResultDset> sheetResult) {
		final String badSheetName = queryConfigModel.getInvalidTabPrefix() + sheetName;

		// Create the sheet with header frozen
		XSSFSheet sheet = workbook.createSheet(sheetName);
		sheet.createFreezePane(0, 1);

		// Header Row and Values:
		createHeader(sheet, qcAttr);

		// Sheet Data:
		for (int i = 0; i < sheetResult.size(); i++) {
			ResultDset queryResultModel = (ResultDset) sheetResult.get(i);

			if (putInvalidObjectsOnTakeOut(workbook, badSheetName, queryResultModel, qcAttr, queryConfigModel)) {
				continue;
			}

			if (sheetName.equals(Constant.ItemSheet)) {
				collectRemoteNoticeNumbers(sheet, queryResultModel, qcAttr);

			}

			if (!remoteNoticeNumOnItemTab.contains(queryResultModel.getItemId())) {
				fillSheet(queryResultModel, sheet, qcAttr);
			}

		}

	}

	private void fillSheet(ResultDset queryResultModel, XSSFSheet sheet, QueryConfigAttributeModel qcAttr) {
		int size = queryResultModel.getSize();
		//System.out.println("queryResult "+size);
		fillData(queryResultModel, size);
		//System.out.println(queryResultModel.getProperties());
		for (int j = 0; j < size; j++) {
			
//			if (!dsetValuesOk(sheet, queryResultModel, qcAttr, j)) {
//				continue;
//			}
//			
			int newRowNum = sheet.getLastRowNum() + 1;
			sheet.createRow(newRowNum);
			fillRow(sheet, queryResultModel, qcAttr, j);
			
		}
	}

	private void collectRemoteNoticeNumbers(XSSFSheet sheet, ResultDset queryResultModel,
			QueryConfigAttributeModel qcAttr) {

		String id = queryResultModel.getItemId();
		Vector<String> header = qcAttr.getHeader();
		Map<String, List<String>> properties = queryResultModel.getProperties();

		for (int i = 0; i < header.size(); i++) {
			String currentHeaderValue = header.get(i);
			if (currentHeaderValue.equals(Constant.OtherOwningSite)) {
				List<String> propList = properties.get(header.get(i));
				if (propList != null) {
					if (propList.size() > 0) {
						String value = propList.get(0);
						if (value != null) {
							if (value.length() > 0) {
								remoteNoticeNumOnItemTab.add(id);
							}
						}
					}
				}
			}

		}
	
	}

	private boolean putInvalidObjectsOnTakeOut(XSSFWorkbook workbook, String badSheetName, ResultDset queryResultModel,
			QueryConfigAttributeModel qcAttr, QueryConfigModel queryConfigModel) {
		String itemId = queryResultModel.getItemId();

		int maxChars = queryConfigModel.getMaxChars();
		String allowedChars = queryConfigModel.getAllowedChars();

		Pattern pattern = Pattern.compile(allowedChars);
		Matcher matcher = pattern.matcher(itemId);
		boolean itemIdOK = matcher.matches();

		if (!itemIdOK || itemId.length() != maxChars) {
			XSSFSheet badSheet = workbook.getSheet(badSheetName);
			if (badSheet == null) {
				badSheet = workbook.createSheet(badSheetName);
				badSheet.createFreezePane(0, 1);
				createHeader(badSheet, qcAttr);
			}
			fillSheet(queryResultModel, badSheet, qcAttr);
			return true;
		}

		return false;
	}

	private boolean dsetValuesOk(XSSFSheet sheet, ResultDset queryResultModel, QueryConfigAttributeModel qcAttr,int j) {
		Vector<String> header = qcAttr.getHeader();
		Map<String, List<String>> properties = queryResultModel.getProperties();
		//System.out.println(properties.get);

		for (int i = 0; i < header.size(); i++) {

			String currentHeaderValue = header.get(i);
			List<String> propList = properties.get(header.get(i));
			//System.out.println();
			String value = "";

			if (currentHeaderValue.equals(Constant.ChangeFromPrevRev)) {
				if (propList != null) {
					if (propList.size() > 0) {
						for (Iterator<String> iterator = propList.iterator(); iterator.hasNext();) {
							value = (String) iterator.next();
							//System.out.println(value);
							if (Constant.SkipChangeFromPrevRev.contains(value)) {
								return false;
							}
						}
					}
				}
			}
		}

		return true;
	}

	public static boolean containsIgnoreCase(List<String> list, String findMe) {
		for (String s : list) {
			if (findMe.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	private void fillRow(XSSFSheet sheet, ResultDset queryResultModel, QueryConfigAttributeModel qcAttr, int j) {

		Vector<String> header = qcAttr.getHeader();
		Vector<String> row = new Vector<String>();
		Map<String, List<String>> properties = queryResultModel.getProperties();

		XSSFDataFormat dataFormat = sheet.getWorkbook().createDataFormat();
		XSSFRow tableRow = sheet.getRow(sheet.getLastRowNum());
		

		for (int i = 0; i < header.size(); i++) {
			String headerVal = header.get(i);
			List<String> propList = properties.get(header.get(i));
			String value = "";

			if (propList != null) {
				if (propList.size() >= j + 1) {
					value = propList.get(j);
				}
			}

			//System.out.println(value);
			if (headerVal.startsWith("SHOW_ON")) {
				if (!value.isEmpty()) {
					row.remove(row.size() - 1);
					row.add(value);
					
				}
			} else {
				if (value.equals("deleted") || value.equals("sys delete - no action req")
						|| value.equals("removed") ||value.equals("sys remove - no action req")) {
					System.out.println("Found: " + value);
					
				}
				row.add(value);
				
			}

		}
             // System.out.println("row"+row.size());
		//System.out.println("Vector"+row);
		if(row.stream().anyMatch(val->val.equalsIgnoreCase("deleted") || val.equalsIgnoreCase("removed") || val.equalsIgnoreCase("Removed") || val.equalsIgnoreCase("Deleted") ||val.equalsIgnoreCase("sys delete - no action req") ||val.equalsIgnoreCase("sys remove - no action req")))
		{
			row.removeAll(row);
			XSSFRow rowToRemove = sheet.getRow(sheet.getLastRowNum());
			sheet.removeRow(rowToRemove);
		}else {
			
		for (int k = 0; k < row.size(); k++) {
		
				XSSFCell cell = tableRow.createCell(k);
			String content = row.get(k);

			if (content == Constant.EMPTY) {
				content = "";
			}
			cell.setCellValue(content);
         
			textStyle.setDataFormat(dataFormat.getFormat(StringFormatUtil.TEXT_FORMAT));
			cell.setCellStyle(textStyle);
		
		}
		}
		//System.out.println("vector loop finshed");
	}


	private void fillData(ResultDset queryResultModel, int size) {
		Map<String, List<String>> properties = queryResultModel.getProperties();
		for (Entry<String, List<String>> entry : properties.entrySet()) {
			List<String> valueList = entry.getValue();
			String key = entry.getKey();
			if (key != null) {
				for (int i = 0; i < size; i++) {
					int vsize = valueList.size();
					if (vsize < i + 1) {
						valueList.add(valueList.get(i - 1));
					}
				}
			}

		}

	}

	private void createHeader(XSSFSheet sheet, QueryConfigAttributeModel qcAttr) {
		Vector<String> header = qcAttr.getHeader();
		// Header Row and Values:
		XSSFCell cell = null;
		XSSFRow headerRow = sheet.createRow(0);
		int headerCols = 0;
		for (int i = 0; i < header.size(); i++) {
			String headerCell = header.get(i);
			if (headerCell.startsWith("SHOW_ON")) {
				continue;
			}
			cell = headerRow.createCell(headerCols);
			cell.setCellValue(headerCell);
			headerCols++;
		}

	}

	private void arrageSheetOrder(XSSFWorkbook workbook, QueryConfigModel queryConfigModel) {
		int numberOfSheets = workbook.getNumberOfSheets();
		for (int i = 0; i < numberOfSheets; i++) {
			XSSFSheet crrntSheet = workbook.getSheetAt(i);
			String sheetName = crrntSheet.getSheetName();
			if (sheetName.startsWith(queryConfigModel.getInvalidTabPrefix())) {
				workbook.setSheetOrder(sheetName, numberOfSheets - 1);
			}

		}

	}

}
