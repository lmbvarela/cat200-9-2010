/* CAT 200 Group Project
 * Based on Table Layout by Andre_Uhres
 * To store and manage all opened songs
 * Song Table implements SongTableEditor  
*/

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;


public class SongTable extends JTable{

	// Constructor
	public SongTable(){
		super (0, 0);
		setDefaultEditor(SongPanel.class, new SongTableEditor());
		setSurrendersFocusOnKeystroke(true);
	}
	
	// To add a new column
	public void addColumn(String columnName){
		DefaultTableModel model = (DefaultTableModel) getModel();
        model.addColumn(columnName);
	}
	
	// To add single or multiple rows
	public void addRow(final JComponent... components) {
		DefaultTableModel model = (DefaultTableModel) getModel();
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
	
	// Return proper cell editor
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
	
	// SongTableEditor
	public class SongTableEditor implements TableCellEditor{
		
		protected EventListenerList listeners = new EventListenerList();
		protected transient ChangeEvent changeEvent = null;
		protected SongPanel newSong = null;
		@Override
		
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			newSong = (SongPanel) value;
			return newSong;
		}
		@Override
		public Object getCellEditorValue() {
			return newSong;
		}
		@Override
		public void addCellEditorListener(CellEditorListener l) {
			listeners.add(CellEditorListener.class, l);
			
		}
		@Override
		public void cancelCellEditing() {
			fireEditingCanceled();
			
		}
		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}
		@Override
		public void removeCellEditorListener(CellEditorListener l) {
			listeners.remove(CellEditorListener.class, l);
			
		}
		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			if (newSong != null && anEvent instanceof MouseEvent
                    && ((MouseEvent) anEvent).getID() == MouseEvent.MOUSE_PRESSED) {
                Component dispatchComponent =
                        SwingUtilities.getDeepestComponentAt(newSong, 3, 3);
                MouseEvent e = (MouseEvent) anEvent;
                MouseEvent e2 = new MouseEvent(dispatchComponent,
                        MouseEvent.MOUSE_RELEASED, e.getWhen() + 100000,
                        e.getModifiers(), 3, 3, e.getClickCount(), e.isPopupTrigger());
                dispatchComponent.dispatchEvent(e2);
                e2 = new MouseEvent(dispatchComponent, MouseEvent.MOUSE_CLICKED,
                        e.getWhen() + 100001, e.getModifiers(), 3, 3, 1, e.isPopupTrigger());
                dispatchComponent.dispatchEvent(e2);
            }
			return true;
		}
		@Override
		public boolean stopCellEditing() {
			fireEditingStopped();
            return true;
		}
		
		protected void fireEditingCanceled() {
            Object[] listeners = listenerList.getListenerList();
            
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CellEditorListener.class) {
                    if (changeEvent == null) {
                        changeEvent = new ChangeEvent(this);
                    }
                    ((CellEditorListener) listeners[i + 1]).editingCanceled(changeEvent);
                }
            }
		}
		
		protected void fireEditingStopped() {//used in stopCellEditing
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CellEditorListener.class) {
                    // Lazily create the event:
                    if (changeEvent == null) {
                        changeEvent = new ChangeEvent(this);
                    }
                    ((CellEditorListener) listeners[i + 1]).editingStopped(changeEvent);
                }
            }
        }
	}
	
	
}
