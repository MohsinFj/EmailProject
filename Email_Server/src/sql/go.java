/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Mohsin
 */
public class go {

    private static Connection con = null;

    public static void getConnection() {
        try {

            String url = "jdbc:mysql://localhost:3306/email?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            con = DriverManager.getConnection(url, "root", "");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insert(String query) {

        try {
            //افتح اتصال مع قاعدة الييانات
            getConnection();

            Statement stm = con.createStatement();

            stm.executeUpdate(query);
            stm.close();
            con.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<String> checkEmailAndPassword(String query) {
        ArrayList<String> data = new ArrayList<>();
        try {

            getConnection();

            Statement stm = con.createStatement();

            ResultSet rs = stm.executeQuery(query);
            if (rs.next()) {
                data.add(rs.getString("name"));
                data.add(rs.getString("department"));
            }

            rs.close();
            stm.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return data;
    }
    
    public static ArrayList<String> getFileName(String query) {
        ArrayList<String> filesName = new ArrayList<>();
        try {

            getConnection();

            Statement stm = con.createStatement();

            ResultSet rs = stm.executeQuery(query);
            
            while (rs.next()) {
                String fileName = rs.getString("file_name");
                filesName.add(fileName);
            }

            rs.close();
            stm.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return filesName;
    }
    public static ArrayList<String> getClientName(String query) {
        ArrayList<String> clientsName = new ArrayList<>();
        try {

            getConnection();

            Statement stm = con.createStatement();

            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                String name = rs.getString("name");
                clientsName.add(name);
            }

            rs.close();
            stm.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return clientsName;
    }

    public static int getLastID(String query) {

        try {

            getConnection();

            Statement stm = con.createStatement();

            ResultSet rs = stm.executeQuery(query);

            int last_id = 1;
            //تمثل الصفوف
            while (rs.next()) {
                last_id = rs.getInt("id");
                System.out.println("last_id: " + last_id);
            }

            rs.close();
            stm.close();
            con.close();

            return last_id;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return 0;
    }

    public static ArrayList<String> getDepartment(String query) {
        
        ArrayList<String> departmentsName = new ArrayList<>();
        try {
            getConnection();

            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(query);

            while (rs.next()) {
                String department = rs.getString("department_name");
                departmentsName.add(department);
            }

            stm.close();
            rs.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return departmentsName;
    }

    public static void showTableData(JTable table, String query) {
        try {

            DefaultTableModel model = (DefaultTableModel) table.getModel();

            model.setRowCount(0);

            getConnection();

            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();

            int c = rsmd.getColumnCount(); // يجلب عدد الاعمدة الموجودة في قاعدة البيانات

            Object row[];
            while (rs.next()) {
                row = new Object[c];
                for (int i = 0; i < c; i++) {

                    row[i] = rs.getString(i + 1);
                }
                model.addRow(row);
            }

            rs.close();
            stm.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static void fillList(JList list, String query) {
        try {
            getConnection();

            Statement stm = con.createStatement();

            ResultSet rs = stm.executeQuery(query);

            DefaultListModel model = new DefaultListModel();

            while (rs.next()) {
                model.addElement(rs.getString("name"));
            }

            list.setModel(model);

            rs.close();
            stm.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static void fillComboBox(JComboBox cmb, String query) {
        try {
            getConnection();

            Statement stm = con.createStatement();

            ResultSet rs = stm.executeQuery(query);

            while (rs.next()) {
                cmb.addItem(rs.getString("name"));
            }

            rs.close();
            stm.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}
