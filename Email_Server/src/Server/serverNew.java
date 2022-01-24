/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import sql.go;

/**
 *
 * @author Mohsin
 */
public class serverNew {

    public serverNew() {

        try {
            ServerSocket server = new ServerSocket(9999);
            System.out.println("Server is running");
            while (true) {
                Socket client = server.accept();
                
                new Thread(new Runnable() {
                    Socket client;
                    public Runnable init(Socket client) {
                        this.client = client;
                        return this;
                    }

                    @Override
                    public void run() {
                        try {
                            InputStream is = client.getInputStream();
                            OutputStream out = client.getOutputStream();

                            ObjectInputStream objectReadSource = new ObjectInputStream(is);
                            ObjectOutputStream objectWriteSource = new ObjectOutputStream(out);

                            Object data[] = (Object[]) objectReadSource.readObject();
                            switch (data[0].toString()) {
                                // Case 1
                                case "checkEmailAndPassword": {
                                    String email = data[1].toString();
                                    String password = data[2].toString();
                                    String query = "SELECT name,department FROM clients WHERE email = '" + email + "' AND password='" + password + "'";

                                    ArrayList<String> clientData = go.checkEmailAndPassword(query);
                                    objectWriteSource.writeObject(clientData);
                                    objectWriteSource.flush();
                                    break;
                                }
                                // Case 2
                                case "getDepartmentName": {
                                    String query = "SELECT department_name FROM departments";
                                    ArrayList<String> info = go.getDepartment(query);
                                    
                                    objectWriteSource.writeObject(info);
                                    objectWriteSource.flush();
                                    break;
                                }
                                // Case 3
                                case "addNewClient": {
                                    Object name = data[1];
                                    Object email = data[2];
                                    Object password = data[3];
                                    Object department = data[4];

                                    String query = "INSERT INTO clients(name, email, password, department) VALUES ('" + name + "','" + email + "','" + password + "','" + department + "')";
                                    go.insert(query);
                                    objectWriteSource.writeUTF("تمت الاضافة بنجاح");
                                    objectWriteSource.flush();
                                    break;
                                }
                                case "getClientName": {
                                    Object department = data[1];
                                    Object clientName = data[2];
                                    String query = "SELECT name FROM clients WHERE department='" + department + "' AND NOT name='"+clientName+"'";
                                    ArrayList<String> info = go.getClientName(query);
                                    
                                    objectWriteSource.writeObject(info);
                                    objectWriteSource.flush();
                                    
                                    break;
                                }
                                case "sendFile": {
                                    Object fileName = data[1];
                                    Object fromClientName = data[2];
                                    Object fromClientDepartment = data[3];
                                    HashMap<Object,Object> toClient = (HashMap<Object,Object>)data[4]; //اسم المستقبل والقسم
                                    
                                    //جلب الوقت الحالي
                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                    SimpleDateFormat sdf3 = new SimpleDateFormat("HH_mm_ss");
                                    String format = sdf3.format(timestamp);
                                    
                                    FileOutputStream fis = new FileOutputStream(System.getProperty("user.dir") + "\\src\\FilesDownloaded\\" + format + fileName);
                                    fis.write((byte[]) data[5]);
                                    fis.close();
                                    
                                    Object toClientName , toClientDepartment; //اسم المستقبل والقسم
                                    int size = toClient.size();
                                    for (int i = 0; i < size; i++) {
                                        //key = fileName
                                        //value = fileByte
                                        toClientName = toClient.keySet().toArray()[i];
                                        toClientDepartment = toClient.get(toClientName);
                                        
                                        String query = "INSERT INTO filesend(file_name, from_name, from_department, to_name, to_department, state) VALUES ('" + format + fileName + "','" + fromClientName + "','" + fromClientDepartment + "','" + toClientName + "','" + toClientDepartment + "','" + 0 + "')";
                                        go.insert(query);
                                    }

                                    objectWriteSource.writeUTF("تم ارسال الملف بنجاح");
                                    objectWriteSource.flush();
                                    
                                    break;
                                }
                                case "getFile": {
                                    Object clientName = data[1];
                                    String query = "SELECT file_name FROM filesend WHERE to_name='" + clientName + "' AND state = 0";
                                    //هنا جلب اسم الملف مع الوقت مدمج
                                    ArrayList<String> filesName = go.getFileName(query);
                                    int size = filesName.size();
                                    
                                    if (size > 0) {
                                        objectWriteSource.writeObject(filesName);
                                        FileInputStream fin = null;
                                        for (int i = 0; i < size; i++) {
                                            File file = new File(System.getProperty("user.dir") + "\\src\\FilesDownloaded\\" + filesName.get(i));
                                            fin = new FileInputStream(file);
                                            byte[] byteFile;
                                            byteFile = new byte[fin.available()];
                                            fin.read(byteFile);
                                            
                                            objectWriteSource.writeObject(byteFile);
                                        }
                                        fin.close();
                                        objectWriteSource.flush();
                                    }
                                    break;
                                }
                                case "changeFileState": {
                                    Object fileName = data[1];

                                    String query = "UPDATE filesend SET state='1' WHERE file_name ='" + fileName + "' ";
                                    go.insert(query);
                                    break;
                                }
                               
                            }

                            objectReadSource.close();
                            objectWriteSource.close();
                            is.close();
                            out.close();
                            client.close();
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }.init(client)).start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
