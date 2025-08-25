package presentation;

import model.Subject;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminView extends JPanel{

    private JTable table;
    private DefaultTableModel model;

    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField nameField;
    private JTextField emailField;
    private JButton addButton;
    private JButton deleteButton;
    private JComboBox<String> roleBox;
    private JButton setSubjectTeacher;

    public AdminView(){
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new Object[]{"ID","Username","Role","Subject"},0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout());
        usernameField = new JTextField(8);
        passwordField = new JTextField(8);
        nameField = new JTextField(8);
        emailField = new JTextField(8);
        roleBox = new JComboBox<>(new String[]{"student","teacher"});
        addButton = new JButton("Add User");
        deleteButton = new JButton("Delete User");
        setSubjectTeacher = new JButton("Set Subject Teacher");

        formPanel.add(new JLabel("Username"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Role"));
        formPanel.add(roleBox);
        formPanel.add(addButton);
        formPanel.add(deleteButton);
        formPanel.add(setSubjectTeacher);

        add(formPanel, BorderLayout.SOUTH);

    }

    public JButton getSetSubjectTeacherButton(){
        return setSubjectTeacher;
    }

    public int getSelectedUserId(){
        int row = table.getSelectedRow();
        if(row == -1){
            return -1;
        }
        return (int) table.getValueAt(row, 0);
    }

    public void updateTable(List<User> users) {
        model.setRowCount(0);
        for (User u : users) {
            String subject = (u.getSubject() != null) ? u.getSubject() : "-";
            model.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole(), subject});
        }
    }

    public void clearForm(){
        usernameField.setText("");
        passwordField.setText("");
        nameField.setText("");
        emailField.setText("");
        roleBox.setSelectedIndex(0);
    }
    public JButton getAddButton() {
        return addButton;
    }
    public JButton getDeleteButton() {
        return deleteButton;
    }

    public String getUsername() {
        return usernameField.getText();
    }
    public String getPassword() {
        return passwordField.getText();
    }

    public String getName() {
        return nameField.getText();
    }
    public String getEmail() {
        return emailField.getText();
    }
    public String getRole() {
        return (String) roleBox.getSelectedItem();
    }

}
