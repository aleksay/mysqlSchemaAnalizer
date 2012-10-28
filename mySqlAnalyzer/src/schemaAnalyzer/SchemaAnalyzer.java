/**
 * 
 */
package schemaAnalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import schemaAnalyzer.services.*;

/**
 * @author ale
 * 
 */
public class SchemaAnalyzer {

	private static String dbAddress;
	private static String dbPort;
	private static String dbUser;
	private static String dbPwd;
	private static String dbSchema;
	private static String dbmsType;
	//private static String dbparameter;
	//private static String jdbcLink;

	public static void main(String[] args) throws SQLException {
 
		Connection dbConnection = createConnection();
		SchemaSetup mySetup = new SchemaSetup(dbConnection,getQuery(),dbSchema);
		//mySetup.createSchema();
		
		mySetup.loadSchema();
		dbConnection.close();
	}
	
	private static String getJdbcLink(){
		
		loadConnection();
		return "jdbc:mysql://"+dbAddress+":"+dbPort+"/information_schema";
	}	

	private static void loadConnection() {
		try {
			File fXmlFile = new File("./configureMyApp.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("connection");

			Node nNode = nList.item(0);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				dbAddress = getTagValue("address", eElement);
				dbPort = getTagValue("port", eElement);
				dbPwd = getTagValue("password", eElement);
				dbUser = getTagValue("user", eElement);
				dbSchema = getTagValue("schema", eElement);
				dbmsType = getTagValue("dbms", eElement);
			//	dbparameter = getTagValue("parameters",eElement);
			//	jdbcLink = getTagValue("jdbcLink",eElement);

			}
		} catch (SAXException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		}
	}

	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
				.getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}
	
	public static Connection createConnection(){
		Connection dbConnection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			dbConnection = DriverManager.getConnection(getJdbcLink(),dbUser, dbPwd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dbConnection;		
	}
	
	public static Properties getQuery(){
		
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("./dbms/"+dbmsType+".qry"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return prop;
	}
	
	
}
