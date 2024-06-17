import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class DbSchemaController
{
    private DbSchemaFrame dbSchemaGui = null;   //The gui.
    private String dbURL = null;                //The database URL.
    private DbSchemaConnection conn = null;     //The db connection.
    private boolean tupleSelected = false;

    /**
     * Creates a controller for DbSchemaMain.
     * @param dbURL    String - the URL to the database.
     */
    public DbSchemaController(String dbURL)
    {
        this.dbURL = dbURL;
        conn = new DbSchemaConnection(this.dbURL);
    }

    /**
     * Sets the gui.
     * @param frame RadioInfoFrame - the frame to be set.
     */
    public void setGui(DbSchemaFrame frame)
    {
        dbSchemaGui = frame;
    }

    public void startDbConnection()
    {
        try {
            conn.getConnect();
            conn.getMetaData();
            setRelationTable();
        } catch (Exception e) {
            dbSchemaGui.showError(e.getMessage());
        }
    }

    public void setRelationTable()
    {
        ArrayList<String> relationList = conn.getRelationNames();
        for(String relationName : relationList) {
            dbSchemaGui.addRelationToTable(relationName);
        }
    }

    /**
     * Action listener for the relation list.
     */
    class RelationListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent le) {
            if (dbSchemaGui.getRelationRowCount() > 0) {
                    /* If channel selected. */
                if (le.getValueIsAdjusting() == false) {
                    tupleSelected = false;
                    dbSchemaGui.resetTupleTable();
                    dbSchemaGui.setAddButton(true);
                    dbSchemaGui.setUpdateButton(false);
                    dbSchemaGui.setDeleteButton(false);
                    String selectedRelation = dbSchemaGui.getRelationName();
                    updateTupleTable(selectedRelation);
                }
            }
        }
    }

    private void updateTupleTable(String relationName)
    {
        conn.getRelation(relationName, dbSchemaGui);
        dbSchemaGui.setSelectedTableColumns();
    }

    /**
     * Action listener for the tuples.
     */
    class TupleListListener implements ListSelectionListener
    {
        @Override
        public void valueChanged(ListSelectionEvent le) {
            if (dbSchemaGui.getTupleRowCount() > 0) {
                    /* If channel selected. */
                if (le.getValueIsAdjusting() == false) {
                    tupleSelected = true;
                    dbSchemaGui.setAddButton(false);
                    dbSchemaGui.setUpdateButton(false);
                    dbSchemaGui.setDeleteButton(true);
                    int row = dbSchemaGui.getSelectedTupleRow();
                    int col = 0;
                    /* Set selected tuple. */
                    dbSchemaGui.setSelectedTable();
                }
            }
        }
    }

    class SelectedTableListener implements TableModelListener
    {
        @Override
        public void tableChanged(TableModelEvent e) {
            if(dbSchemaGui.selectedTableChanged() && hasSelectedTuple()) {
                dbSchemaGui.setUpdateButton(true);
            } else {
                dbSchemaGui.setUpdateButton(false);
            }
        }
    }

    public void addTuple()
    {
        String selectedRelation = dbSchemaGui.getRelationName();
        String[] rowValues = dbSchemaGui.getSelectedTupleValues();
        conn.addRelationTuple(selectedRelation, rowValues, dbSchemaGui);
        updateTupleTable(selectedRelation);
    }

    public void updateTuple()
    {
        String selectedRelation = dbSchemaGui.getRelationName();
        String[] newValues = dbSchemaGui.getSelectedTupleValues();
        String[] oldValues = dbSchemaGui.getSelectedRowValues();
        conn.updateRelationTuple(selectedRelation, newValues, oldValues,
                                 dbSchemaGui);
        updateTupleTable(selectedRelation);
        dbSchemaGui.setDeleteButton(false);
    }

    public void deleteTuple()
    {
        String selectedRelation = dbSchemaGui.getRelationName();
        String[] columns = dbSchemaGui.getSelectedTupleColumns();
        String[] rowValues = dbSchemaGui.getSelectedRowValues();
        conn.deleteRelationTuple(selectedRelation,columns, rowValues,
                                 dbSchemaGui);
        updateTupleTable(selectedRelation);
        dbSchemaGui.setDeleteButton(false);
        dbSchemaGui.setAddButton(true);
    }

    public boolean hasSelectedTuple()
    {
        return tupleSelected;
    }

    /**
     * Action listener for the quit menu item.
     */
    class QuitItemListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            try {
                conn.disConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbSchemaGui.quitOption();
        }
    }
}
