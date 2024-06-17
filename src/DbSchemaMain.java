import javax.swing.*;

public class DbSchemaMain
{
    private static String title = "DB Interface";
    private static String dbURL = "jdbc:postgresql://localhost:5432/lab2";

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DbSchemaController c = new DbSchemaController(dbURL);
                DbSchemaFrame gui = new DbSchemaFrame(title, c);
                c.setGui(gui);
                gui.showFrame(true);
                c.startDbConnection();
            }
        });
    }
}

