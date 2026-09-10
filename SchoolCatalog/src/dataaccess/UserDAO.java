package dataaccess;

import connection.ConnectionFactory;
import model.User;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public User login(String username, String plainPassword, String role) {
        String query = "SELECT * FROM users WHERE username = ? AND role = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, role);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    String storedHash = result.getString("password");
                    if (PasswordUtil.verify(plainPassword, storedHash)) {
                        return new User(result.getInt("id"), result.getString("username"),
                                storedHash, result.getString("role"));
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
