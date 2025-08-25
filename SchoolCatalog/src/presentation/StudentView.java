package presentation;

import model.Grade;
import model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentView extends JPanel {
    private JComboBox<Subject> subjectBox;
    private JTable table;
    private DefaultTableModel model;

    public StudentView(){
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        subjectBox = new JComboBox<>();
        topPanel.add(new JLabel("Select Subject"));
        topPanel.add(subjectBox);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Grade"},0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

    }


    public void setSubjects(List<Subject> subjects){
        subjectBox.removeAllItems();
        for(Subject s : subjects){
            subjectBox.addItem(s);
        }
    }

    public Subject getSelectedSubject(){
        return (Subject) subjectBox.getSelectedItem();
    }

    public JComboBox<Subject> getSubjectBox() {
        return subjectBox;
    }

    public void updateGrades(List<Grade> grades){
        model.setRowCount(0);
        for(Grade g : grades){
            model.addRow(new Object[]{g.getValue()});
        }
    }

}
