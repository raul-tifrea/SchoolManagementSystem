package presentation;

import model.Absence;
import model.Grade;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TeacherView extends JPanel {
   private JTable gradestable;
   private DefaultTableModel gradesmodel;
   private JTable absencestable;
   private DefaultTableModel absencesmodel;
   private JComboBox<Student> studentcombo;
   private JComboBox<Student> absencestudcombo;
   private JTextField gradeField;
   private JButton addGradeButton;
   private JButton addAbsenceButton;
   private JButton deleteAbsenceButton;

   private Map<String, List<Integer>> gradeIdMap = new HashMap<>();

   public TeacherView(){
       setLayout(new BorderLayout());
       JTabbedPane tabs = new JTabbedPane();

       JPanel gradesPanel = new JPanel(new BorderLayout());
       gradesmodel = new DefaultTableModel(new Object[]{"Student Name","Grade", "Average"},0){
           @Override
           public boolean isCellEditable(int row, int column) {
               return false;
           }
       };
       gradestable = new JTable(gradesmodel);
       gradesPanel.add(new JScrollPane(gradestable), BorderLayout.CENTER);

       JPanel formPanel = new JPanel(new FlowLayout());
       studentcombo = new JComboBox<>();
       gradeField = new JTextField(8);
       addGradeButton = new JButton("Add Grade");


       formPanel.add(new JLabel("Student"));
       formPanel.add(studentcombo);
       formPanel.add(new JLabel("Grade"));
       formPanel.add(gradeField);
       formPanel.add(addGradeButton);

       gradesPanel.add(formPanel, BorderLayout.SOUTH);

       JPanel absencesPanel = new JPanel(new BorderLayout());
       absencesmodel = new DefaultTableModel(new Object[]{"Student Name", "Absence"},0){
           @Override
           public boolean isCellEditable(int row, int column) {
               return false;
           }
       };
       absencestable = new JTable(absencesmodel);
       JPanel absenceFormPanel = new JPanel(new FlowLayout());
       absencestudcombo = new JComboBox<>();
       absenceFormPanel.add(new JLabel("Student"));
       absenceFormPanel.add(absencestudcombo);
       absencesPanel.add(absenceFormPanel, BorderLayout.NORTH);

       JPanel absencesFormPanel = new JPanel(new FlowLayout());
       addAbsenceButton = new JButton("Add Absence");
       deleteAbsenceButton = new JButton("Delete Absence");
       absencesFormPanel.add(addAbsenceButton);
       absencesFormPanel.add(deleteAbsenceButton);
       absencesPanel.add(new JScrollPane(absencestable), BorderLayout.CENTER);
       absencesPanel.add(absencesFormPanel, BorderLayout.SOUTH);

       tabs.addTab("Grades", gradesPanel);
       tabs.addTab("Absences", absencesPanel);
       add(tabs, BorderLayout.CENTER);

   }

   public void setStudentCombo(List<Student> students){
       studentcombo.removeAllItems();
       for(Student s : students){
           studentcombo.addItem(s);
       }
   }


   public Student getSelectedStudent(){
       return (Student) studentcombo.getSelectedItem();
   }


   public double getGrade(){
       return Double.parseDouble(gradeField.getText());
   }

   public void updateGradesTable(List<Grade> grades){
       gradesmodel.setRowCount(0);
       gradeIdMap.clear();
       Map<String, StringBuilder> map = new LinkedHashMap<>();
       Map<String, List<Double>> averageMap = new HashMap<>();
       for(Grade g : grades) {
           map.computeIfAbsent(g.getStudentName(), k -> new StringBuilder()).append(g.getValue()).append(", ");
           gradeIdMap.computeIfAbsent(g.getStudentName(), k -> new ArrayList<>()).add(g.getId());
           averageMap.computeIfAbsent(g.getStudentName(), k -> new ArrayList<>()).add(g.getValue());
       }
       for(Map.Entry<String, StringBuilder> e : map.entrySet()){
           String allGrade = e.getValue().toString().replaceAll(", $", "");
           List<Double> average = averageMap.get(e.getKey());
           double averageGrade = average.stream().mapToDouble(Double::doubleValue).average().orElse(0);
           gradesmodel.addRow(new Object[]{e.getKey(), allGrade, String.format("%.2f", averageGrade)});
       }
   }

   public Map<String, List<Integer>> getGradeIdMap(){
       return gradeIdMap;
   }


   public JButton getAddGradeButton(){
       return addGradeButton;
   }


    public void clearForm(){
       studentcombo.setSelectedIndex(0);
       gradeField.setText("");
    }


    public JTable getGradesTable() {
        return gradestable;
    }

    public JButton getAddAbsenceButton() {
       return addAbsenceButton;
    }

    public JButton getDeleteAbsenceButton() {
       return deleteAbsenceButton;
    }
    public JTable getAbsencesTable() {
        return absencestable;
    }

    public int getSelectedAbsenceId(List<Absence> absences){
       int row = absencestable.getSelectedRow();
       if(row == -1){
           return -1;
       }
    return absences.get(row).getId();
    }

    public void updateAbsencesTable(List<Absence> absences){
       absencesmodel.setRowCount(0);
       for(Absence a : absences){
           absencesmodel.addRow(new Object[]{a.getStudentname(), a.getDate().toString()});
       }
    }

    public JComboBox<Student> getAbsenceStudentCombo() {
        return absencestudcombo;
    }

    public Student getSelectedAbsenceStudent(){
       return (Student) absencestudcombo.getSelectedItem();
    }

    public void setAbsenceStudentCombo(List<Student> students){
       absencestudcombo.removeAllItems();
       for(Student s : students){
           absencestudcombo.addItem(s);
       }
    }


}
