package presentation;

import model.Grade;
import model.Student;
import model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TeacherView extends JPanel {
   private JTable table;
   private DefaultTableModel model;
   private JComboBox<Student> studentcombo;
   private JComboBox<String> subjectcombo;
   private JTextField gradeField;
   private JButton addGradeButton;

   private Map<String, List<Integer>> gradeIdMap = new HashMap<>();

   public TeacherView(){
       setLayout(new BorderLayout());
       model = new DefaultTableModel(new Object[]{"Student Name","Grade"},0){
           @Override
           public boolean isCellEditable(int row, int column) {
               return false;
           }
       };
       table = new JTable(model);
       add(new JScrollPane(table), BorderLayout.CENTER);

       JPanel formPanel = new JPanel(new FlowLayout());
       studentcombo = new JComboBox<>();
       gradeField = new JTextField(8);
       addGradeButton = new JButton("Add Grade");


       formPanel.add(new JLabel("Student"));
       formPanel.add(studentcombo);
       formPanel.add(new JLabel("Grade"));
       formPanel.add(gradeField);
       formPanel.add(addGradeButton);

       add(formPanel, BorderLayout.SOUTH);
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

   public String getSelectedStudentName(){
      int row = table.getSelectedRow();
      if(row == -1){
          return null;
      }
      return (String) table.getValueAt(row, 0);
   }

   public double getGrade(){
       return Double.parseDouble(gradeField.getText());
   }


   public void updateTable(List<Grade> grades){
       model.setRowCount(0);
       gradeIdMap.clear();
       Map<String, StringBuilder> map = new LinkedHashMap<>();
       for(Grade g : grades) {
           map.computeIfAbsent(g.getStudentName(), k -> new StringBuilder()).append(g.getValue()).append(", ");
           gradeIdMap.computeIfAbsent(g.getStudentName(), k -> new ArrayList<>()).add(g.getId());
       }
       for(Map.Entry<String, StringBuilder> e : map.entrySet()){
           String allGrade = e.getValue().toString().replaceAll(", $", "");
           model.addRow(new Object[]{e.getKey(), allGrade});
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


    public JTable getTable() {
        return table;
    }



}
