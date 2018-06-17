import java.sql.*;


public class DataBaseClient {
    private String address;
    private String port = "5432";
    private String user = "hostInfoChecker";
    private String password = "asdQWE123";
    private String dbName = "postgres";

    public DataBaseClient(String newAddress) {
        this.address = newAddress;
    }

    public DataBaseClient(String newAddress, String newUser, String newPassword) {
        this.address = newAddress;
        this.user = newUser;
        this.password = newPassword;
    }

    public DataBaseClient(String newAddress, String newPort, String newUser, String newPassword) {
        this.address = newAddress;
        this.port = newPort;
        this.user = newUser;
        this.password = newPassword;
    }

    public ResultSet connectAndExecute(String query) {
        Connection connection = null;
        ResultSet result = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://" + address + ":" + port + "/" + dbName +
                    "?user=" + user + "&password=" + password + "&ssl=false");
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute(query);
            result = statement.getResultSet();
            statement.close();
            connection.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        } catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void getHosts() {
        String query = "SELECT * FROM public.hosts";
        ResultSet result = connectAndExecute(query);
    }

}
