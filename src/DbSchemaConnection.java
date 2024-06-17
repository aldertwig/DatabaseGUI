import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DbSchemaConnection
{
    private String dbURL = null;
    private Properties props = null;
    private Connection conn = null;
    private ResultSet result = null;
    private DatabaseMetaData dbMetaData = null;
    private ArrayList<String> relationNames = null;

    public DbSchemaConnection(String dbURL)
    {
        this.dbURL = dbURL;
        props = new Properties();
        props.setProperty("user","postgres");
        props.setProperty("password","lab");
        props.setProperty("ssl","false");
        relationNames = new ArrayList<>();
    }

    public void getMetaData() throws SQLException
    {
        //Get Relation Names
        dbMetaData = conn.getMetaData();
        String[] types = {"TABLE"};
        result = dbMetaData.getTables(conn.getCatalog(), null, "%", types);
        while (result.next()) {
            String tableName = result.getString("TABLE_NAME");
            relationNames.add(tableName);
        }
        result.close();
    }

    public void getRelation(String relationName, DbSchemaFrame dbSchemaGui)
    {
        Statement stat = null;
        try {
            stat = conn.createStatement();
            //Get Relation
            String query = "SELECT * FROM " + relationName;
            result = stat.executeQuery(query);
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columns = new String[columnCount];
            int index = 0;
            for(int i = 1; i <= columnCount; i++) {
                columns[index] = metaData.getColumnLabel(i);
                index++;
            }
            dbSchemaGui.setTupleColumns(columns);
            while(result.next()) {
                index = 0;
                String[] columnRow = new String[columnCount];
                for(int i = 1; i <= columnCount; i++) {
                    columnRow[index] = result.getString(i);
                    index++;
                }
                dbSchemaGui.addTupleToTable(columnRow);
            }
        } catch (Exception e) {
            dbSchemaGui.showError(e.getMessage());
        } finally {
            if (stat != null) {
                try {
                    stat.close();
                } catch (Exception e) {
                    dbSchemaGui.showError(e.getMessage());
                }
            }
        }
    }

    public ArrayList<String> getRelationNames()
    {
        return relationNames;
    }

    public void getConnect() throws SQLException, ClassNotFoundException
    {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection(dbURL, props);
    }

    public void disConnect() throws SQLException
    {
        if (conn != null) {
            conn.close();
        }
    }

    public void updateRelationTuple(String relationName, String[] newValues,
                                    String[] oldValues,
                                    DbSchemaFrame dbSchemaGui)
    {
        try {
            switch(relationName) {
                case "insurance":
                    updateInsurance(newValues, oldValues);
                    break;
                case "car":
                    updateCar(newValues, oldValues);
                    break;
                case "owner":
                    updateOwner(newValues, oldValues);
                    break;
                default:
                    dbSchemaGui.showError("No update query exists for " +
                            "relation: " + relationName);
            }
        } catch (SQLException e) {
            dbSchemaGui.showError(e.getMessage());
        }
    }

    private void updateInsurance(String[] newValues, String[] oldValues) throws
            SQLException
    {
        String query = updateInsuranceQuery();
        PreparedStatement update = conn.prepareStatement(query);
        update.setInt(1, Integer.parseInt(newValues[0]));
        update.setString(2, newValues[1]);
        update.setInt(3, Integer.parseInt(newValues[2]));
        update.setString(4, newValues[3]);
        update.setDate(5, Date.valueOf(newValues[4]));
        update.setInt(6, Integer.parseInt(oldValues[0]));
        update.setString(7, oldValues[1]);
        update.setInt(8, Integer.parseInt(oldValues[2]));
        update.setString(9, oldValues[3]);
        update.setDate(10, Date.valueOf(oldValues[4]));
        update.executeUpdate();
    }

    private String updateInsuranceQuery()
    {
        String query = "UPDATE insurance SET " +
                "insurance_id = ?, license_plate = ?, " +
                "owner_id = ?,  insurance_type = ?, " +
                "expiration_date = ? " +
                "WHERE " +
                "insurance_id = ? AND license_plate = ? AND " +
                "owner_id = ? AND insurance_type = ? AND " +
                "expiration_date = ?";
        return query;
    }

    private void updateCar(String[] newValues, String[] oldValues) throws
            SQLException
    {
        String query = getUpdateCarQuery();
        PreparedStatement update = conn.prepareStatement(query);
        update.setString(1, newValues[0]);
        update.setInt(2, Integer.parseInt(newValues[1]));
        update.setString(3, newValues[2]);
        update.setString(4, newValues[3]);
        update.setString(5, newValues[4]);
        update.setString(6, oldValues[0]);
        update.setInt(7, Integer.parseInt(oldValues[1]));
        update.setString(8, oldValues[2]);
        update.setString(9, oldValues[3]);
        update.setString(10, oldValues[4]);
        update.executeUpdate();
    }

    private String getUpdateCarQuery()
    {
        String query = "UPDATE car SET " +
                "license_plate = ?, owner_id = ?, " +
                "manufacturer = ?, model = ?, colour = ? " +
                "WHERE " +
                "license_plate = ? AND owner_id = ? AND " +
                "manufacturer = ? AND model = ? AND colour = ?";
        return query;
    }

    private void updateOwner(String[] newValues, String[] oldValues) throws
            SQLException
    {
        String query = getUpdateOwnerQuery();
        PreparedStatement update = conn.prepareStatement(query);
        update.setInt(1, Integer.parseInt(newValues[0]));
        update.setString(2, newValues[1]);
        update.setString(3, newValues[2]);
        update.setString(4, newValues[3]);
        update.setString(5, newValues[4]);
        update.setString(6, newValues[5]);
        update.setString(7, newValues[6]);
        update.setInt(8, Integer.parseInt(oldValues[0]));
        update.setString(9, oldValues[1]);
        update.setString(10, oldValues[2]);
        update.setString(11, oldValues[3]);
        update.setString(12, oldValues[4]);
        update.setString(13, oldValues[5]);
        update.setString(14, oldValues[6]);
        update.executeUpdate();
    }

    private String getUpdateOwnerQuery()
    {
        String query = "UPDATE owner SET " +
                "owner_id = ?, last_name = ?, " +
                "first_name = ?, address = ?, " +
                "city = ?, postal_code = ?, " +
                "home_phone = ? " +
                "WHERE " +
                "owner_id = ? AND last_name = ? AND " +
                "first_name = ? AND address = ? AND " +
                "city = ? AND postal_code = ? AND " +
                "home_phone = ?";
        return query;
    }

    public void addRelationTuple(String relationName, String[] rowValues,
                                 DbSchemaFrame dbSchemaGui)
    {
        try {
            switch(relationName) {
                case "insurance":
                    insertInsurance(rowValues);
                    break;
                case "car":
                    insertCar(rowValues);
                    break;
                case "owner":
                    insertOwner(rowValues);
                    break;
                default:
                    dbSchemaGui.showError("No insert query exists for " +
                            "relation: " + relationName);
            }
        } catch (Exception e) {
            dbSchemaGui.showError(e.getMessage());
        }
    }

    private void insertInsurance(String[] rowValues) throws SQLException
    {
        String query = "INSERT INTO insurance " +
                "( insurance_id, license_plate, owner_id, " +
                "insurance_type, expiration_date ) " +
                "VALUES ( ?, ?, ?, ?, ? )";
        PreparedStatement update = conn.prepareStatement(query);
        update.setInt(1, Integer.parseInt(rowValues[0]));
        update.setString(2, rowValues[1]);
        update.setInt(3, Integer.parseInt(rowValues[2]));
        update.setString(4, rowValues[3]);
        update.setDate(5, Date.valueOf(rowValues[4]));
        update.executeUpdate();
        update.close();
    }

    private void insertCar(String[] rowValues) throws SQLException
    {
        String query = "INSERT INTO car " +
                "( license_plate, owner_id, manufacturer, model, colour ) " +
                "VALUES ( ?, ?, ?, ?, ? )";
        PreparedStatement update = conn.prepareStatement(query);
        update.setString(1, rowValues[0]);
        update.setInt(2, Integer.parseInt(rowValues[1]));
        update.setString(3, rowValues[2]);
        update.setString(4, rowValues[3]);
        update.setString(5, rowValues[4]);
        update.executeUpdate();
        update.close();
    }

    private void insertOwner(String[] rowValues) throws SQLException
    {
        String query = "INSERT INTO owner " +
                "( owner_id, last_name, first_name, " +
                "address, city, postal_code, home_phone ) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ? )";
        PreparedStatement update = conn.prepareStatement(query);
        update.setInt(1, Integer.parseInt(rowValues[0]));
        update.setString(2, rowValues[1]);
        update.setString(3, rowValues[2]);
        update.setString(4, rowValues[3]);
        update.setString(5, rowValues[4]);
        update.setString(6, rowValues[5]);
        update.setString(7, rowValues[6]);
        update.executeUpdate();
        update.close();
    }

    public void deleteRelationTuple(String relationName, String[] columns,
                                    String[] rowValues,
                                    DbSchemaFrame dbSchemaGui)
    {
        try {
            Statement stat = conn.createStatement();
            String query = "DELETE FROM " + relationName + " WHERE " +
                    getValueString(columns, rowValues);
            stat.executeUpdate(query);
        } catch (SQLException e) {
            dbSchemaGui.showError(e.getMessage());
        }
    }

    private String getValueString(String[] columns, String[] rowValues)
    {
        String valueString = "";
        for(int i = 0; i < columns.length; i++) {
            valueString += columns[i] + "='" + rowValues[i] + "'";
            if((i + 1) < columns.length) {
                valueString += " AND ";
            }
        }
        return valueString;
    }
}
