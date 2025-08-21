package presentation;

import dataaccess.AdminDAO;
import model.User;
import presentation.AdminView;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class AdminController {
    private final AdminView view;
    private final AdminDAO dao;

    public AdminController(AdminView view) throws SQLException {
        this.view = view;
        this.dao = new AdminDAO();

        initListeners();
        refreshTable();
    }

    private void initListeners() {
        view.getAddButton().addActionListener(e -> addUser());
        view.getDeleteButton().addActionListener(e -> {
            try {
                deleteUser();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void addUser() {
        String username = view.getUsername();
        String password = view.getPassword();
        String role = view.getRole();
        String name = view.getName();
        String email = view.getEmail();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please fill all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String emailregex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if (!email.matches(emailregex)) {
            JOptionPane.showMessageDialog(view, "Invalid email address.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        try {
            User user = new User(username, password, role);
            int userId = dao.insertUser(user);

            if ("student".equals(role)) {
                dao.insertStudent(userId, name, email);
            } else if ("teacher".equals(role)) {
                dao.insertTeacher(userId, name);
            }

            refreshTable();
            view.clearForm();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error adding user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() throws SQLException {
        int userId = view.getSelectedUserId();
        if (userId == -1) {
            JOptionPane.showMessageDialog(view, "Please select a user.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (dao.deleteUser(userId)) {
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(view, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() throws SQLException {
        List<User> users = dao.getUsers();
        view.updateTable(users);
    }
}
