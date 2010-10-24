/* CAT 200 Group Project
 * Based on Table Layout by Andre_Uhres
 * To store and manage all opened songs
 * Song Table implements SongTableEditor  
*/

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class SongTable extends JTable{

	/**
	 * Constructor
	 */
	
	public SongTable(){
		super (0, 0);  //Create SongTable with 0 row and 0 column
		addColumn("Files Opened");
		setDefaultRenderer(SongPanel.class, new SongTableRenderer());
		setDefaultEditor(SongPanel.class, new SongTableEditor());
		setRowHeight(150);  //SongPanel height is 150
	}
	
	
	/**
	 *  Override function getCellRenderer of JTable
	 *  
	 *  Make sure cell renderer is not null
	 */
	
	 public TableCellRenderer getCellRenderer(final int row, final int column) {
		 TableColumn tableColumn = getColumnModel().getColumn(column);
	     TableCellRenderer renderer = tableColumn.getCellRenderer();
	        if (renderer == null) {
	            Class c = getColumnClass(column);
	            if (c.equals(Object.class)) {
	                Object o = getValueAt(row, column);
	                if (o != null) {
	                    c = getValueAt(row, column).getClass();
	                }
	            }
	            renderer = getDefaultRenderer(c);
	        }
	        return renderer;
	    }
	 
	 
	/** Override function getCellEditor of JTable
	 *  
	 *  Make sure cell editor is not null
	 */
	 
	 public TableCellEditor getCellEditor(final int row, final int column) {
		  
		 TableColumn tableColumn = getColumnModel().getColumn(column);
	        TableCellEditor editor = tableColumn.getCellEditor();
	        if (editor == null) {
	            Class c = getColumnClass(column);
	            if (c.equals(Object.class)) {
	                Object o = getValueAt(row, column);
	                if (o != null) {
	                    c = getValueAt(row, column).getClass();
	                }
	            }
	            editor = getDefaultEditor(c);
	        }
	        return editor;
	        
	 }
	
	 
	/**
	 *  Add a new column into SongTable
	 * 
	 *  By default, SongTable only has one column
	 */
	
	public void addColumn(String columnNames) {
        DefaultTableModel model = (DefaultTableModel) getModel();
            model.addColumn(columnNames);
    }
	
	
	/**
	 *  Add a new row or multiple rows into SongTable 
	 */
	
	public void addRow(final JComponent... components) {
        DefaultTableModel model = (DefaultTableModel) getModel();
        while (model.getColumnCount() < components.length) {
            model.addColumn("");
        }
        model.addRow(components);
        int h = 0;
        for (JComponent comp : components) {
            int h2 = comp.getPreferredSize().height;
            if (h < h2) {
                h = h2;
            }
        }
        setRowHeight(getRowCount() - 1, h);
        
    }
	
	
	/**
	 * Delete a row from SongTable 
	 */
	public void deleteRow(int rowIndex){
		DefaultTableModel model = (DefaultTableModel) getModel();
		SongPanel song = (SongPanel) model.getValueAt(rowIndex, 0);
		removeEditor();      //stop cell editor
		song.player.stop();  //stop the song
		model.removeRow(rowIndex);
	}
	
	/**
	 * Update the interface of every song panels contained in the Song Table
	 */
	public void update(){
		DefaultTableModel model = (DefaultTableModel) getModel();
		for(int i = 0; i < getRowCount(); i++){
			if(getEditingRow() != i)
				model.fireTableCellUpdated(i, 0);
		}
	}
	
	
	/**
	 * SongTableRenderer that is used to draw SongPanel
	 */
	class SongTableRenderer extends DefaultTableCellRenderer{
		
		public SongPanel getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean isFocused, int row, int column){
			if(value == null)
				return null;
			
			SongPanel song = (SongPanel) value;
			
			return song;
		}
	}
	
	
	/**
	 * SongTableEditor to enable the interaction between user and each SongPanel
	 */
	class SongTableEditor extends AbstractCellEditor implements TableCellEditor{

		SongPanel song;
		@Override
		public Object getCellEditorValue() {
			return song;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			//System.out.println("Come into editor");
			if(value == null)
				return null;
			
			song = (SongPanel) value;
			return song;
		}	
	}

}