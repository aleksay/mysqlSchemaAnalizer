package schemaAnalyzer.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class DomainWriter {

	 public static void createClass(String name, Statement st) {

	        ResultSet columnsSet;
	        String columns = "";
	        File myClass;

	        try {
	            columnsSet = st.executeQuery("SELECT DATA_TYPE,COLUMN_NAME from COLUMNS where TABLE_NAME='" + name + "'");

	            // il type detection sara' rimandato alla classe SchemaSetup
	            while (columnsSet.next()) {
	                if (columnsSet.getString(1).equals("int") || columnsSet.getString(1).equals("char") || columnsSet.getString(1).equals("enum")) {
	                    columns += "private " + TypeMatching.getJavaType(SqlTypes.valueOf("sql_" + columnsSet.getString(1))) + " ";
	                } else {
	                    columns += "private " + TypeMatching.getJavaType(SqlTypes.valueOf(columnsSet.getString(1))) + " ";
	                }

	                columns += columnsSet.getString(2) + ";\n";

	            }

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }


	        try {
	            myClass = new File("./src/main/java/com/mycompany/mysqlschemaanalyzer/Domain/" + name + ".java");
	            if (!myClass.createNewFile()) {
	                System.out.println("cant create file " + name + ".java");
	            }

	            FileWriter out = new FileWriter(myClass);

	            out.append("package com.mycompany.mysqlschemaanalyzer.Domain;\n");
	            out.append("public class " + name + "{\n");
	            out.append(columns);
	            out.append("}");
	            out.close();

	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }

	    }
	
}
