package iva.client.swing.tables;

/**
 * This abstract class provides default implementations for most of the 
 * methods in the {@code TableModel} interface for a fixed column table. 
 * It takes care of the management of listeners and provides some conveniences 
 * for generating {@code TableModelEvents} and dispatching them to listeners.
 * Subclasses only need to implement methods for managing table row data.
 * 
 * @author Aron
 * @see javax.swing.table.AbstractTableModel
 */
public abstract class AbstractTableModel extends javax.swing.table.AbstractTableModel {
	private static final long serialVersionUID = -6585043900816484802L;
	
	protected final String[] columnNames;
	
	public AbstractTableModel(String[] columnNames) {
		this.columnNames = columnNames;
	}

	@Override
	public abstract int getRowCount();

	/**
     * Removes the row at <code>row</code> from the model.  Notification
     * of the row being removed should be sent to all listeners.
     * @param row the row index of the row to be removed
     */
    public abstract void removeRow(int row);

    /**
     * Removes all the rows from the model. Notification
     * of the rows being removed should be sent to all listeners.
     */
    public abstract void removeAllRows();

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (getRowCount() > 0) {
			return getValueAt(0, columnIndex).getClass();
		} else {
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public abstract Object getValueAt(int rowIndex, int columnIndex);

	@Override
	public abstract void setValueAt(Object aValue, int rowIndex, int columnIndex);

	public String[] getColumnNames() {
		return columnNames;
	}

}
