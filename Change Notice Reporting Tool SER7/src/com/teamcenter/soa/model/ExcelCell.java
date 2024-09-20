package com.teamcenter.soa.model;


/**
 * Model for Excel Cells
 * 
 * @author Frank Nauroth
 */

public class ExcelCell {
	private final int MAX_STRING_PRINT_LEN = 26;

	private String columnName = "";
	private int rowNumber = 0;
	private String cellID = "";

	private String content = "";
	private String printableContent;
	private boolean lastCellInRow = false;

	/**
	 * Checks, if this is the last Cell in Row
	 * 
	 * @return true, if last cell and false otherwise
	 */
	public boolean isLastCellInRow() {
		return lastCellInRow;
	}

	/**
	 * Set this Cell as the last Cell in Row
	 * 
	 * @param lastCellInRow
	 */
	public void setLastCellInRow(boolean lastCellInRow) {
		this.lastCellInRow = lastCellInRow;
	}

	/**
	 * Returns the Column Name of the Cell
	 * 
	 * @return coloumnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Set the Cell's Column Name
	 * 
	 * @param columnName
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * Returns the Row Number of the Cell
	 * 
	 * @return rowNumber
	 */
	public int getRowNumber() {
		return rowNumber;
	}

	/**
	 * Set the Cell's Row Number
	 * 
	 * @param rowNumber
	 */
	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	/**
	 * Returns the Cell ID (Column Name concatenated with Row Number) of the
	 * Cell
	 * 
	 * @return cellID
	 */
	public String getCellID() {
		return cellID;
	}

	/**
	 * Set the Cell ID (Column Name concatenated with Row Number)
	 * 
	 * @param cellID
	 */
	public void setCellID(String cellID) {
		this.cellID = cellID;
	}

	/**
	 * Returns the Cell Content as String representation
	 * 
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Set the Cell Number Content as String representation
	 * 		using the content as the full double content and dataFormatString used to define the printable content
	 * 
	 * @param  content
	 * @param dataFormatString

	 */
	public void setContent(double content, String wysiwygContent) {

		this.content = Double.toString(content);
		String fixedContent = FixString.setWidth(wysiwygContent, MAX_STRING_PRINT_LEN);
		this.printableContent = fixedContent;
	}

	/**
	 * Set the Cell String Content as String representation
	 * 		using the content as the full string content
	 * 
	 * @param content
	 */
	public void setContent(String content,String wysiwygContent) {
		this.content = content;
		String fixedContent = FixString.setWidth(wysiwygContent, MAX_STRING_PRINT_LEN);
		this.printableContent = fixedContent;
	}
	
	/**
	 * Set the Cell boolean Content as String representation
	 * 		using the content as the full string content
	 * @param content
	 */
	public void setContent(boolean content,String wysiwygContent) {
		this.content = Boolean.toString(content);
		String fixedContent = FixString.setWidth(wysiwygContent, MAX_STRING_PRINT_LEN);
		this.printableContent = fixedContent;
	}
	
	/**
	 * Set the Cell Error Content as String representation
	 * 		using the content as the full string content
	 * 
	 * @param content
	 */
	public void setContent(byte content,String wysiwygContent) {
		this.content = Byte.toString(content);
		String fixedContent = FixString.setWidth(wysiwygContent, MAX_STRING_PRINT_LEN);
		this.printableContent = fixedContent;
	}
	
	

	/**
	 * Returns the Cell printable Content as String representation
	 * 
	 * @return String printableContent
	 */
	public String getPrintableContent() {
		return printableContent;
	}

	/**
	 * Constructor (not really needed so far)
	 */
	public ExcelCell() {

	}

}
