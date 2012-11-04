package schemaAnalyzer.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import schemaAnalyzer.domain.Column;
import schemaAnalyzer.domain.Table;

public class SchemaSetup {

	Logger logger;
	List<Column> myCol;
	List<Table> myTab;
	Properties queryLoader;
	Connection dbConnection;
	String mySchema;

	public SchemaSetup(Connection dbConnection, Properties prop, String mySchema) {
		super();
		this.myCol = Collections.synchronizedList(new LinkedList<Column>());
		this.myTab = Collections.synchronizedList(new LinkedList<Table>());
		queryLoader = prop;
		this.dbConnection = dbConnection;
		this.mySchema = mySchema;
	}
	
	public SchemaSetup(Connection dbConnection, Properties prop, String mySchema, Logger logger) {
		super();
		this.myCol = Collections.synchronizedList(new LinkedList<Column>());
		this.myTab = Collections.synchronizedList(new LinkedList<Table>());
		queryLoader = prop;
		this.dbConnection = dbConnection;
		this.mySchema = mySchema;
		this.logger = logger;
	}
	
	
	public List<Table> getSchema(){
		return myTab;
	}

	public Table getTable(String tableName) {

		Table tmpTable = new Table(tableName);
		for (Table tab : myTab) {
			if (tab.equals(tmpTable)) {
				return tab;
			}
		}
		return null;
	}

	public Column getColumn(String columnName) {

		Column tmpColumn = new Column(columnName);
		for (Column col : myCol) {
			if (col.equals(tmpColumn)) {
				return col;
			}
		}
		return null;
	}

	public void loadSchema() {
		try {

			Column tmpCol;
			Table tmpTab;

			PreparedStatement colTabSt = dbConnection.prepareStatement(queryLoader.getProperty("tabCol"));
			
			colTabSt.setString(1, mySchema);
			colTabSt.setString(2, mySchema);

			if(logger != null)
				logger.append("inizio recupero info\n");
			ResultSet colTab = colTabSt.executeQuery();
			if(logger != null)
				logger.append("fine recupero info\n");

			while (colTab.next()) {
				tmpCol = new Column(colTab.getString(1));
				tmpTab = new Table(colTab.getString(2));

				setDataType(tmpCol,tmpTab);				
				
				if(myTab.contains(tmpTab)){
					tmpTab = myTab.get(myTab.indexOf(tmpTab));
				}else{
					myTab.add(tmpTab);
				}
				if (isPKey(tmpCol, tmpTab)) {
					/*if (!tmpTab.getKey().isEmpty()) {
						logger.append("la tabella "+tmpTab.getName()+ " ha piu di una chiave:\n");
						logger.append(" vecchia: "+tmpTab.getKey()+" nuova: "+tmpCol+"\n");
						
					}*/
					if(logger != null)
						logger.append(tmpCol.getName() + " chiave di "+tmpTab.getName());
					tmpTab.addKey(tmpCol);
					//NON DA ERRORE MA COMPORTAMENTO INATTESO
					tmpCol.setTableKeyOf(tmpTab);
					
				}else if(!tmpTab.getColumns().contains(tmpCol)){
					
					tmpTab.getColumns().add(tmpCol);
					
				}else{
					if(logger != null)
						logger.append("relazione tabella colonna ripetuta\n");
				}
			}
			printReport();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printReport(){
		
		if(logger == null) return;
			
		logger.append("------------report--------------------\n");
		
		for (Table tab : myTab) {
			logger.append(tab.list());
		}
	}
	
	public boolean isPKey(Column col, Table tab) {

		try {
			PreparedStatement isKey = dbConnection.prepareStatement(queryLoader
					.getProperty("keys"));
			isKey.setString(1, col.getName());
			isKey.setString(2, tab.getName());
			isKey.setString(3, tab.getName());
			isKey.setString(4, mySchema);
			isKey.setString(5, col.getName());
			
			ResultSet keys = isKey.executeQuery();

			int occurrence = 0;
			while (keys.next())
				occurrence++;
			
			if(occurrence > 1){
				if(logger != null)
					logger.append("isKey: trovate "+occurrence+" match per "+ col.getName()+" su table: "+tab.getName());
			}
			return occurrence > 0;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void setDataType(Column tmpCol, Table tmpTab) {

		PreparedStatement dataType;
		try {
			dataType = dbConnection.prepareStatement(queryLoader.getProperty("datatype"));

			dataType.setString(1, tmpCol.getName());
			dataType.setString(2, mySchema);
			dataType.setString(3, tmpTab.getName());

			ResultSet result = dataType.executeQuery();
			if (!result.first()) {
				throw new IllegalStateException("la colonna "
						+ tmpCol.getName() + "non ha  tipo");
			}

			String sqlType = result.getString(1);
			if (sqlType.equals("int") || sqlType.equals("char")	|| sqlType.equals("enum")) {
				tmpCol.setType(TypeMatching.getJavaType(SqlTypes.valueOf("sql_"	+ sqlType)));
			} else {
				tmpCol.setType(TypeMatching.getJavaType(SqlTypes.valueOf(sqlType)));

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*public void createSchema() {

		try {

			Statement st = dbConnection.createStatement();
			Statement st2 = dbConnection.createStatement();

			
			 * ResultSet info = st.executeQuery("select " + "TABLE_CATALOG, " +
			 * "TABLE_SCHEMA," + "TABLE_NAME," + "COLUMN_NAME," +
			 * "ORDINAL_POSITION," + "COLUMN_DEFAULT," + "IS_NULLABLE," +
			 * "DATA_TYPE," + "CHARACTER_MAXIMUM_LENGTH," +
			 * "CHARACTER_OCTET_LENGTH," + "NUMERIC_PRECISION," +
			 * "NUMERIC_SCALE," + "CHARACTER_SET_NAME," + "COLLATION_NAME," +
			 * "COLUMN_TYPE," + "COLUMN_KEY," + "EXTRA," + "PRIVILEGES," +
			 * "COLUMN_COMMENT from columns where TABLE_SCHEMA='information_schema'"
			 * );
			 * 
			 * while(info.next()){ String z = ""; for(int i=1; i<=
			 * info.getMetaData().getColumnCount();i++){ z+=
			 * info.getString(i)+", "; } // System.out.println(z);
			 * 
			 * }
			 

			*//**
			 * lancio di createClass(String name,Statement st)
			 * 
			 * ResultSet tables = st.executeQuery("select TABLE_NAME from TABLES
			 * where TABLE_SCHEMA='information_schema'");
			 * 
			 * ArrayList<String> tableNames = new ArrayList<String>();
			 * 
			 * while(tables.next()){ tableNames.add(tables.getString(1)); }
			 * 
			 * for(String table: tableNames){ createClass(table,st); }
			 *//*
			
			 * seleziona le tabelle divise per colonne
			 * 
			 * select c1.column_name, concat(', '),c1.table_name from columns c1
			 * where c1.table_schema='information_schema' and c1.column_name in
			 * (select distinct(c2.column_name) from columns c2 where
			 * c2.table_schema='information_schema') order by c1.column_name;
			 * 
			 * 
			 * oggetto table oggetto column tutti questi oggetti vengono
			 * correlati fra loro in due mappe m1 ed m2. m1 e' la mappa del
			 * risultato della query sopra che mette in relazione le tabelle fra
			 * loro sulla base di una colonna in comune. m2 invece mantiene una
			 * relazione tra colonne acuulunate dal fatto di appartenere alla
			 * stessa tabella.
			 

			ResultSet columnTables = st
					.executeQuery("select c1.column_name, c1.table_name from columns c1 where c1.table_schema='"
							+ mySchema
							+ "' and c1.column_name in (select distinct(c2.column_name) from columns c2 where c2.table_schema='"
							+ mySchema + "') order by c1.column_name");

			Column tmpCol;
			Table tmpTab;

			while (columnTables.next()) {

				tmpCol = new Column(columnTables.getString(1));
				tmpTab = new Table(columnTables.getString(2));

				// System.out.println("myCol.contains(tmpCol): " +
				// myCol.contains(tmpCol));
				if (!myCol.contains(tmpCol)) {

					ResultSet result = st2
							.executeQuery("SELECT DATA_TYPE from COLUMNS where COLUMN_NAME='"
									+ tmpCol.getName() + "'");
					if (!result.first()) {
						throw new IllegalStateException("la colonna "
								+ tmpCol.getName() + "non ha  tipo");
					}

					String sqlType = result.getString(1);
					if (sqlType.equals("int") || sqlType.equals("char")
							|| sqlType.equals("enum")) {
						tmpCol.setType(TypeMatching.getJavaType(SqlTypes
								.valueOf("sql_" + sqlType)));
					} else {
						tmpCol.setType(TypeMatching.getJavaType(SqlTypes
								.valueOf(sqlType)));

					}

					synchronized (myCol) {
						myCol.add(tmpCol);
					}

				} else {
					tmpCol = myCol.get(myCol.indexOf(tmpCol));
				}

				if (!myTab.contains(tmpTab)) {
					synchronized (myTab) {
						myTab.add(tmpTab);
					}
				} else {
					tmpTab = myTab.get(myTab.indexOf(tmpTab));
				}

				if (!tmpTab.getColumns().contains(tmpCol)) {

					tmpTab.addColumn(tmpCol);
				}
				if (!tmpCol.getTables().contains(tmpTab)) {
					tmpCol.addTable(tmpTab);
				}

			}
			// createKeysOnColumnName();
			keyExtractor(dbConnection);

			logger.append("------------report--------------------");
			for (Table tab : myTab) {
				logger.append(tab.list());
			}

			dbConnection.close();
			logger.close();
		} catch (SQLException ex) {
			logger.close();
			ex.printStackTrace();
		}
	}

	private void createKeysOnColumnName() {

		
		 * La colonna a_aname(di tipo string) e' condivisa fra la tabella a e la
		 * tabella b la colonna a_name crea un oggetto tabella nuovo ( c ) con
		 * due elementi colonna: un sequence e se stesso. in tutte le tabelle
		 * che originariamente referenziavano la colonna a_name verra rimossa
		 * questa relazione e verra' creata una nuova relazione con la colonna
		 * id della tabella c
		 * 
		 * 
		 * 
		 * aggiornando il type delle colonne referenziate dalle tabelle al tipo
		 * con lo stesso nome dellatabella c
		 

		Column col;
		Column[] myColFreeze = new Column[myCol.size()];
		synchronized (myCol) {
			myCol.toArray(myColFreeze);
		}
		for (int i = 0; i < myColFreeze.length; i++) {

			col = myColFreeze[i];

			if (col.getTables().size() > 1) {
				// System.out.println(col);
				Column origData = col.clone();

				Table keyTab = new Table(col.getName());
				Column id = new Column("id");
				id.addTable(keyTab);
				id.setType("int");
				keyTab.addColumn(id);
				keyTab.addColumn(origData);

				synchronized (myTab) {
					myTab.add(keyTab);
					synchronized (myCol) {
						myCol.add(id);
						myCol.add(origData);
					}
				}

				col.setType(col.getName());
			}
		}
	}*/

/*	public void keyExtractor(Connection dbConnection) {

		
		 * Query di estrazione dedicata esclusivamente alle foreign key... manca
		 * la selezione dello schema o della tabella dipende come la si vuole
		 * fare
		 * 
		  select t.CONSTRAINT_NAME, t.TABLE_SCHEMA, t.TABLE_NAME,
		  k.COLUMN_NAME, k.REFERENCED_TABLE_NAME, k.REFERENCED_COLUMN_NAME,
		  t.CONSTRAINT_TYPE, r.UPDATE_RULE, r.DELETE_RULE from key_column_usage
		  k, referential_constraints r, table_constraints t where
		  t.CONSTRAINT_NAME = r.CONSTRAINT_NAME and t.CONSTRAINT_NAME =
		  k.CONSTRAINT_NAME;
		 

		
		 * questa invece per le primary key, manca la selezione per table_name
		 * forse si potrebbe fare un po piu articolata ma questa dovrebbe gia
		 * fornire le info necessarie.
		 * 
		 * select TABLE_SCHEMA,TABLE_NAME,COLUMN_NAME,CONSTRAINT_NAME from
		 * key_column_usage where CONSTRAINT_NAME = 'PRIMARY' and
		 * COLUMN_NAME='';
		 

		Column[] myColFreeze = new Column[myCol.size()];
		myCol.toArray(myColFreeze);
		Table[] myTabFreeze;

		myCol.toArray(myColFreeze);

		for (Column col : myColFreeze) {

	//		myTabFreeze = new Table[col.getTables().size()];
	//		col.getTables().toArray(myTabFreeze);

			for (Table tab : myTabFreeze) {
				try {
					Statement st = dbConnection.createStatement();

					ResultSet reso = st
							.executeQuery("select TABLE_SCHEMA,TABLE_NAME,COLUMN_NAME,CONSTRAINT_NAME "
									+ "from key_column_usage "
									+ "where CONSTRAINT_NAME = 'PRIMARY' and COLUMN_NAME='"
									+ col.getName()
									+ "' and TABLE_NAME='"
									+ tab.getName() + "'");
					// logger.append("found primary key on "+col.getName()+" for the table "+tab.getName());

					while (reso.next()) {

						Column tmpCol = getColumn(reso.getString(3));
						Table tmpTab = getTable(reso.getString(2));

						logger.append("found primary key: \n" + tmpCol
								+ " \n on table " + tmpTab.getName() + "\n");
						tmpCol.setTableKeyOf(tmpTab);
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}*/
}
