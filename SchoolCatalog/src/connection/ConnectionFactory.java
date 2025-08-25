package connection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {
        private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
        private static final String DBURL ="jdbc:mysql://localhost:3306/schooldb";
        private static final String USER ="root";
        private static final String PASS ="0000";

        private static ConnectionFactory singleInstance = new ConnectionFactory();

        private Connection createConnection() {
                Connection connection = null;
                try {
                        connection = DriverManager.getConnection(DBURL, USER, PASS);
                } catch (SQLException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                }
                return connection;
        }

        public static Connection getConnection() {
                return singleInstance.createConnection();
        }


}
