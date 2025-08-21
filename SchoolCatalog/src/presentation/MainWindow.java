package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import dataaccess.UserDAO;
import model.User;
import presentation.AdminView;
import presentation.StudentView;
import presentation.TeacherView;
import presentation.AdminController;

public class MainWindow extends JFrame {

    private JTabbedPane MainPane;
    private AdminView AdminView;
    private StudentView studentView;
    private TeacherView teacherView;

    private JPanel loginPanel;
    private JComboBox<String> roleBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public MainWindow(){
        setTitle("School Catalog");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("School Catalog", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loginPanel = createNewLoginPanel();
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);

        MainPane = new JTabbedPane();
        add(mainPanel);


    }

    private JPanel createNewLoginPanel(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Role"), c);

        c.gridx = 1;
        roleBox = new JComboBox<>(new String[]{"Admin","Teacher","Student"});
        roleBox.setPreferredSize(new Dimension(200, 30));
        panel.add(roleBox, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("Username"), c);
        c.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, c);

        c.gridx = 0;
        c.gridy = 2;
        panel.add(new JLabel("Password"), c);
        c.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, c);

        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        panel.add(loginButton, c);

        loginButton.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e){
                try {
                    login();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    login();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        return panel;
    }

    private void login() throws SQLException {
        String role = (String) roleBox.getSelectedItem();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        try {
            User user = userDAO.login(username, password, role);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Login successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                roleview(role.toLowerCase());
            }
        } catch (HeadlessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void roleview(String role) throws SQLException {
        getContentPane().removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutPanel.add(logoutButton);
        mainPanel.add(logoutPanel, BorderLayout.NORTH);

        MainPane = new JTabbedPane();
        if(role.equals("admin")){
            if(AdminView == null){
                AdminView = new AdminView();
                new AdminController(AdminView);
            }
            MainPane.addTab("Admin", AdminView);
        } else if(role.equals("teacher")){
            if(teacherView == null){
                teacherView = new TeacherView();

            }
            MainPane.addTab("Teacher", teacherView);
        } else if(role.equals("student")){
            if(studentView == null){
                studentView = new StudentView();
            }
            MainPane.addTab("Student", studentView);
        }

        mainPanel.add(MainPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        revalidate();
        repaint();


    }

    private void logout(){
        getContentPane().removeAll();
        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("School Catalog", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loginPanel = createNewLoginPanel();
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        add(mainPanel);

        AdminView = null;
        studentView = null;
        teacherView = null;

        revalidate();
        repaint();

    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
    }



}
