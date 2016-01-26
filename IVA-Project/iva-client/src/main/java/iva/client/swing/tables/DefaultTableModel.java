package iva.client.swing.tables;

import java.util.Vector;

public class DefaultTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 7634458989700661306L;
	
	protected final Vector<Object[]> data;
	
	public DefaultTableModel(String[] columnNames) {
		super(columnNames);
		data = new Vector<>();
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	/**
     * Replaces a row at <code>row</code> in the model.  The new row
     * will contain <code>null</code> values unless <code>rowData</code>
     * is specified.  Notification of the row being added will be generated.
     * @param row the row index of the row to be inserted
     * @param rowData optional data of the row being added
     */
    public void setRow(int row, Object[] rowData) {
		data.set(row, fitRowData(rowData));
		fireTableRowsUpdated(row, row);
	}
	
	/**
     * Inserts a row at <code>row</code> in the model.  The new row
     * will contain <code>null</code> values unless <code>rowData</code>
     * is specified.  Notification of the row being added will be generated.
     * @param row the row index of the row to be inserted
     * @param rowData optional data of the row being added
     */
    public void insertRow(int row, Object[] rowData) {
		data.insertElementAt(fitRowData(rowData), row);
		fireTableRowsInserted(row, row);
	}

	/**
     * Adds a row to the end of the model.  The new row will contain
     * <code>null</code> values unless <code>rowData</code> is specified.
     * Notification of the row being added will be generated.
     * @param rowData optional data of the row being added
     */
    public void addRow(Object[] rowData) {
		insertRow(getColumnCount(), rowData);
	}

    /**
     * Truncates row data larger than the column count.
     * @param rowData data of the row being added
     * @return fitted row data
     */
    protected Object[] fitRowData(Object[] rowData) {
    	Object[] fittedRowData = new Object[getColumnCount()];
		for(int i=0; i < fittedRowData.length; i++) {
			if(i < rowData.length) {
				fittedRowData[i] = rowData[i];
			}
		}
		return fittedRowData;
    }

	@Override
	public void removeRow(int row) {
		data.removeElementAt(row);
		fireTableRowsDeleted(row, row);
	}

	@Override
	public void removeAllRows() {
		int lastRow = getRowCount() - 1;
		if (lastRow >= 0) {
			data.clear();
			fireTableRowsDeleted(0, lastRow);
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object[] rowData = data.get(rowIndex);
        return rowData[columnIndex];
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Object[] rowData = data.get(rowIndex);
		rowData[columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}

}
