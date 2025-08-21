package dataaccess;

import connection.ConnectionFactory;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public User login(String username, String password, String role){
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
        try(Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query) ) {
            statement.setString(1,username);
            statement.setString(2,password);
            statement.setString(3,role);
            try(ResultSet result = statement.executeQuery()){
                if(result.next()){
                    return new User(result.getInt("id"),result.getString("username"),result.getString("password"),result.getString("role"));
                }else{
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
