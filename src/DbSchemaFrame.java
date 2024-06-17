import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Vector;

/**
 * Class that represents the view. Uses tables to show relations and tuples.
 * @author Johan Ahlqvist
 * 2020-02-16
 */
public class DbSchemaFrame
{
    private JFrame dbSchemaFrame;               //The view.
    private DefaultTableModel relationTable;    //Table for relations.
    private JTable relationJTable;              //JTable for relation table.
    private DefaultTableModel tupleTable;       //Table for tuples.
    private JTable tupleJTable;                 //JTable for tuples table.
    private JMenuItem quitItem;                 //Menu item for quiting.
    private DefaultTableModel selectedTupleTable;   //Table for selected tuple.
    private JTable selectedTupleJTable;       //JTable for selected tuple table.
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;

    /**
     * Constructor for the frame. Takes frame name and the controller as
     * parameters.
     * @param name          String - title for frame.
     * @param controller    RadioInfoController - the controller.
     */
    public DbSchemaFrame(String name, DbSchemaController controller)
    {
        dbSchemaFrame = new JFrame(name);
        dbSchemaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dbSchemaFrame.setMinimumSize(new Dimension(800, 400));
        dbSchemaFrame.setPreferredSize(new Dimension(800, 400));
        dbSchemaFrame.setLayout(new BorderLayout());
        /* Build menus. */
        dbSchemaFrame.setJMenuBar(createMenus(controller));
        /* Build panels. */
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BorderLayout());
        JPanel relationsPanel = buildRelationsPanel(controller);
        JPanel tuplePanel = buildTuplePanel(controller);
        upperPanel.add(relationsPanel, BorderLayout.WEST);
        upperPanel.add(tuplePanel, BorderLayout.CENTER);

        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BorderLayout());
        JPanel selectedPanel = buildSelectedPanel(controller);
        JPanel optionPanel = buildOptionPanel(controller);
        lowerPanel.add(selectedPanel, BorderLayout.CENTER);
        lowerPanel.add(optionPanel, BorderLayout.SOUTH);
        /* Add panels to frame. */
        dbSchemaFrame.add(upperPanel, BorderLayout.CENTER);
        dbSchemaFrame.add(lowerPanel, BorderLayout.SOUTH);
        /* Pack frame. */
        dbSchemaFrame.pack();
    }

    /**
     * Creates a menu bar with menus and menu items.
     * @param c DbSchemaController - the controller.
     * @return  JMenuBar - the menu bar.
     */
    public JMenuBar createMenus(DbSchemaController c)
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu programMenu = new JMenu("Program");

        quitItem = new JMenuItem("Exit");
        DbSchemaController.QuitItemListener quitListener = c.new
                QuitItemListener();
        quitItem.addActionListener(quitListener);

        programMenu.add(quitItem);
        menuBar.add(programMenu);

        return menuBar;
    }

    /**
     * Quits the program.
     */
    public void quitOption()
    {
        dbSchemaFrame.dispatchEvent(new WindowEvent(dbSchemaFrame,
                                    WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Builds the panel for viewing relations.
     * @param c DbSchemaController - the controller.
     * @return  JPanel - the panel.
     */
    public JPanel buildRelationsPanel(final DbSchemaController c)
    {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Relations"));
        panel.setLayout(new BorderLayout());
	    /* Build the table. */
        JTable table = buildRelationsTable(c);

        int HEIGHT = 250;
        int WIDTH = 200;
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        return panel;
    }

    /**
     * Builds the panel for viewing relation tuples.
     * @param c DbSchemaController - the controller.
     * @return  JPanel - the panel.
     */
    public JPanel buildTuplePanel(final DbSchemaController c)
    {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Tuples"));
        panel.setLayout(new BorderLayout());
	    /* Build the table. */
        JTable table = buildTupleTable(c);

        int HEIGHT = 250;
        int WIDTH = 600;
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        scrollPane.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        panel.add(scrollPane);

        return panel;
    }

    /**
     * Builds the panel for selected relation tuple.
     * @param c DbSchemaController - the controller.
     * @return  JPanel - the panel.
     */
    public JPanel buildSelectedPanel(final DbSchemaController c)
    {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Tuple"));
        panel.setLayout(new BorderLayout());
	    /* Build the table. */
        JTable table = buildSelectedTable(c);

        int HEIGHT = 50;
        int WIDTH = 800;
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        scrollPane.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        panel.add(scrollPane);

        return panel;
    }

    /**
     * Builds the panel for options.
     * @param c DbSchemaController - the controller.
     * @return  JPanel - the panel.
     */
    public JPanel buildOptionPanel(final DbSchemaController c)
    {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Options"));
        panel.setLayout(new FlowLayout());

        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                c.addTuple();
            }
        });
        addButton.setEnabled(false);

        updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                c.updateTuple();
            }
        });
        updateButton.setEnabled(false);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                c.deleteTuple();
            }
        });
        deleteButton.setEnabled(false);

        int HEIGHT = 50;
        int WIDTH = 800;
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);

        return panel;
    }

    /**
     * Builds the table that show relations.
     * @param c DbSchemaController - the controller.
     * @return  JTable - the table.
     */
    public JTable buildRelationsTable(final DbSchemaController c)
    {
        relationTable = new DefaultTableModel();
        relationTable.addColumn("Name");

        relationJTable = new JTable(relationTable) {
            @Override
            public boolean isCellEditable ( int row, int column )
            {
                return false;
            }
        };
        relationJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        /* Add a list selection listener. */
        relationJTable.getSelectionModel().addListSelectionListener(c.
                                           new RelationListListener());

        return relationJTable;
    }

    /**
     * Builds the table that show relation tuples.
     * @param c DbSchemaController - the controller.
     * @return  JTable - the table.
     */
    public JTable buildTupleTable(final DbSchemaController c)
    {
        tupleTable = new DefaultTableModel();

        tupleJTable = new JTable(tupleTable) {
            @Override
            public boolean isCellEditable ( int row, int column )
            {
                return false;
            }
        };
        tupleJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        /* Add a list selection listener. */
        tupleJTable.getSelectionModel().addListSelectionListener(c.
                                        new TupleListListener());

        return tupleJTable;
    }

    /**
     * Builds the table that show selected relation tuple.
     * @param c DbSchemaController - the controller.
     * @return  JTable - the table.
     */
    public JTable buildSelectedTable(final DbSchemaController c)
    {
        selectedTupleTable = new DefaultTableModel();

        selectedTupleJTable = new JTable(selectedTupleTable) {
            @Override
            public boolean isCellEditable ( int row, int column )
            {
                return true;
            }
        };
        selectedTupleJTable.getModel().addTableModelListener(c. new
                SelectedTableListener());

        return selectedTupleJTable;
    }
    /**
     * Gets the amount of rows in relation table.
     * @return  int - the amount of rows.
     */
    public int getRelationRowCount()
    {
        return relationTable.getRowCount();
    }

    /**
     * Gets the amount of rows in tuple table.
     * @return  int - the amount of rows.
     */
    public int getTupleRowCount()
    {
        return tupleTable.getRowCount();
    }

    /**
     * Sets the frame to be shown or not.
     * @param show  boolean - true if to show, else false.
     */
    public void showFrame(boolean show)
    {
        dbSchemaFrame.setVisible(show);
    }

    /**
     * Resets the tuple table.
     */
    public void resetTupleTable()
    {
        tupleTable.setColumnCount(0);
        tupleTable.setRowCount(0);
        tupleTable.fireTableDataChanged();
    }

    /**
     * Gets the selected row in the relation table.
     * @return  int - the selected row.
     */
    public int getSelectedRelationRow()
    {
        return relationJTable.getSelectedRow();
    }

    /**
     * Gets the selected row in the tuple table.
     * @return  int - the selected row.
     */
    public int getSelectedTupleRow()
    {
        return tupleJTable.getSelectedRow();
    }

    /**
     * Adds an relation to the relation table.
     * @param name  String - the relation's name.
     */
    public void addRelationToTable(String name)
    {
        relationTable.addRow(new Object[]{ name });
    }

    /**
     * Adds an episode to the episode table.
     * @param row     String[] - the tuple row.
     */
    public void addTupleToTable(String[] row)
    {
        Vector vectorRow = new Vector();
        for(int i = 0; i < row.length; i++) {
            vectorRow.add(row[i]);
        }
        tupleTable.addRow(vectorRow);
    }

    public void setTupleColumns(String[] columns)
    {
        resetTupleTable();
        for(int i = 0; i < columns.length; i++) {
            tupleTable.addColumn(columns[i]);
        }
    }

    public void setSelectedTableColumns()
    {
        selectedTupleTable.setColumnCount(0);
        selectedTupleTable.setRowCount(1);
        int count = tupleTable.getColumnCount();
        for(int i = 0; i < count; i++) {
            selectedTupleTable.addColumn(tupleTable.getColumnName(i));
        }
    }

    public void setSelectedTable()
    {
        int row = getSelectedTupleRow();
        int count = tupleTable.getColumnCount();
        for(int i = 0; i < count; i++) {
            selectedTupleTable.setValueAt(tupleTable.getValueAt(row, i), 0, i);
        }
    }

    /**
     * Gets the relation's name from relation table.
     * @return  String - the name of relation.
     */
    public String getRelationName()
    {
        int row = getSelectedRelationRow();
        int col = 0;
        return relationJTable.getValueAt(row, col).toString();
    }

    public void showError(String message)
    {
        JOptionPane.showMessageDialog(dbSchemaFrame, message,
                                      "Database error" ,
                                      JOptionPane.ERROR_MESSAGE);
    }

    public void setAddButton(boolean set)
    {
        addButton.setEnabled(set);
    }

    public void setUpdateButton(boolean set)
    {
        updateButton.setEnabled(set);
    }

    public void setDeleteButton(boolean set)
    {
        deleteButton.setEnabled(set);
    }

    public String[] getSelectedTupleColumns()
    {
        int count = tupleTable.getColumnCount();
        String[] columns = new String[count];
        for(int i = 0; i < count; i++) {
            columns[i] = tupleTable.getColumnName(i);
        }
        return columns;
    }

    public String[] getSelectedRowValues()
    {
        int count = tupleTable.getColumnCount();
        int row = getSelectedTupleRow();
        String[] rowValues = new String[count];
        for(int i = 0; i < count; i++) {
            rowValues[i] = String.valueOf(tupleTable.getValueAt(row, i));
        }
        return rowValues;
    }

    public String[] getSelectedTupleValues()
    {
        int count = tupleTable.getColumnCount();
        int row = 0;
        String[] rowValues = new String[count];
        for(int i = 0; i < count; i++) {
            rowValues[i] = String.valueOf(selectedTupleTable.getValueAt(row, i));
        }
        return rowValues;
    }

    public boolean selectedTableChanged()
    {
        int row = getSelectedTupleRow();
        Vector tupleVector = tupleTable.getDataVector();
        Vector selectedVector = selectedTupleTable.getDataVector();
        if(row >= 0) {
            if(!selectedVector.get(0).equals(tupleVector.get(row))) {
                return true;
            }
        }
        return false;
    }
}
