package presentation;

import model.Absence;
import model.Grade;
import model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentView extends JPanel {
    private JComboBox<Subject> subjectBox;
    private JTable gradestable;
    private DefaultTableModel gradesmodel;
    private JTable absencestable;
    private DefaultTableModel absencesmodel;
    private JComboBox<Subject> absencesubjectBox;

    public StudentView(){
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        JPanel gradesPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout());
        subjectBox = new JComboBox<>();
        topPanel.add(new JLabel("Select Subject"));
        topPanel.add(subjectBox);
        gradesPanel.add(topPanel, BorderLayout.SOUTH);

        gradesmodel = new DefaultTableModel(new Object[]{"Grade","Average"},0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gradestable = new JTable(gradesmodel);
        gradesPanel.add(new JScrollPane(gradestable), BorderLayout.CENTER);


        JPanel absencesPanel = new JPanel(new BorderLayout());
        absencesmodel = new DefaultTableModel(new Object[]{"Subject", "Absence"},0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        absencestable = new JTable(absencesmodel);
        JPanel formPanel = new JPanel(new FlowLayout());
        absencesubjectBox = new JComboBox<>();
        formPanel.add(new JLabel("Select Subject"));
        formPanel.add(absencesubjectBox);
        absencesPanel.add(new JScrollPane(absencestable), BorderLayout.CENTER);
        absencesPanel.add(formPanel, BorderLayout.SOUTH);


        tabs.addTab("Grades", gradesPanel);
        tabs.addTab("Absences", absencesPanel);
        add(tabs, BorderLayout.CENTER);

    }


    public void setSubjectsGrade(List<Subject> subjects){
        subjectBox.removeAllItems();
        for(Subject s : subjects){
            subjectBox.addItem(s);
        }
    }

    public void setAbsenceSubjects(List<Subject> subjects){
        absencesubjectBox.removeAllItems();
        for(Subject s : subjects){
            absencesubjectBox.addItem(s);
        }
    }

    public Subject getSelectedSubject(){
        return (Subject) subjectBox.getSelectedItem();
    }

    public JComboBox<Subject> getSubjectBox() {
        return subjectBox;
    }

    public Subject getSelectedAbsenceSubject(){
        return (Subject) absencesubjectBox.getSelectedItem();
    }

    public JComboBox<Subject> getAbsenceSubjectBox() {
        return absencesubjectBox;
    }

    public void updateGrades(List<Grade> grades){
        gradesmodel.setRowCount(0);
        double sum = 0;
        for(Grade g : grades){
            sum += g.getValue();
            gradesmodel.addRow(new Object[]{g.getValue()});
        }

        if(!grades.isEmpty()){
            double average = sum / grades.size();
            gradesmodel.setValueAt(String.format("%.2f", average), grades.size()/2, 1);
        }
    }

    public void updateAbsences(List<Absence> absences){
        absencesmodel.setRowCount(0);
        for(Absence a : absences){
            absencesmodel.addRow(new Object[]{a.getSubjectname(), a.getDate().toString()});
        }
    }

}
