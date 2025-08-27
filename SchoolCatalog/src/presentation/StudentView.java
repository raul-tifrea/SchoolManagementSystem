package presentation;

import model.Absence;
import model.Grade;
import model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
        tabs.setBackground(new Color(151, 21, 251));
        JPanel gradesPanel = new JPanel(new BorderLayout());
        gradesPanel.setBackground(new Color(151, 21, 251));
        JPanel lowerPanel = new JPanel(new FlowLayout());
        lowerPanel.setBackground(new Color(151, 21, 251));
        lowerPanel.setBorder(BorderFactory.createEmptyBorder());
        subjectBox = new JComboBox<>();
        subjectBox.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));
        subjectBox.setPreferredSize(new Dimension(100, 20));
        subjectBox.setFont(new Font("", Font.PLAIN, 14));
        JLabel subjectLabel = new JLabel("Select Subject");
        subjectLabel.setFont(new Font("", Font.BOLD, 14));
        subjectLabel.setForeground(Color.WHITE);
        lowerPanel.add(subjectLabel);
        lowerPanel.add(subjectBox);
        gradesPanel.add(lowerPanel, BorderLayout.SOUTH);

        gradesmodel = new DefaultTableModel(new Object[]{"Grade","Average"},0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        gradestable = new JTable(gradesmodel);
        gradestable.setBackground(new Color(255, 255, 255));
        gradestable.setShowGrid(false);
        gradestable.setFont(new Font("", Font.PLAIN, 14));
        gradestable.setRowHeight(30);

        JTableHeader tableHeader = gradestable.getTableHeader();
        tableHeader.setFont(new Font("", Font.BOLD, 16));
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableHeader.setDefaultRenderer(headerRenderer);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        gradestable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        gradestable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        JScrollPane scrollPane = new JScrollPane(gradestable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        gradesPanel.add(scrollPane, BorderLayout.CENTER);



        JPanel absencesPanel = new JPanel(new BorderLayout());
        absencesPanel.setBackground(new Color(151, 21, 251));
        absencesmodel = new DefaultTableModel(new Object[]{"Subject", "Absence"},0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        absencestable = new JTable(absencesmodel);
        absencestable.setBackground(new Color(255, 255, 255));
        absencestable.setBorder(BorderFactory.createEmptyBorder());
        absencestable.setFont(new Font("", Font.PLAIN, 14));
        absencestable.setShowGrid(false);
        absencestable.setRowHeight(30);

        tableHeader = absencestable.getTableHeader();
        tableHeader.setFont(new Font("", Font.BOLD, 16));

        headerRenderer = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tableHeader.setDefaultRenderer(headerRenderer);

        centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        absencestable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        absencestable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JPanel formPanel = new JPanel(new FlowLayout());

        formPanel.setBackground(new Color(151, 21, 251));

        absencesubjectBox = new JComboBox<>();
        absencesubjectBox.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.BLACK));
        absencesubjectBox.setPreferredSize(new Dimension(100, 20));
        absencesubjectBox.setFont(new Font("", Font.PLAIN, 14));

        JLabel absenceLabel = new JLabel("Select Absence Subject");
        absenceLabel.setFont(new Font("", Font.BOLD, 14));
        absenceLabel.setForeground(Color.WHITE);
        formPanel.add(absenceLabel);

        formPanel.add(absencesubjectBox);

        absencesPanel.add(new JScrollPane(absencestable), BorderLayout.CENTER);
        absencesPanel.add(formPanel, BorderLayout.SOUTH);
        absencesPanel.setBackground(new Color(151, 21, 251));


        tabs.addTab("Grades", gradesPanel);
        tabs.addTab("Absences", absencesPanel);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        tabs.setBorder(BorderFactory.createEmptyBorder());
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
