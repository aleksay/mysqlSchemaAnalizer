package schemaAnalyzer.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import schemaAnalyzer.Domain.Column;
import schemaAnalyzer.Domain.Table;

public class SchemaSetup {

	static final Logger logger = new Logger();
	List<Column> myCol = Collections.synchronizedList(new LinkedList<Column>()); 
	List<Table>  myTab = Collections.synchronizedList(new LinkedList<Table>()); 

	public void createSchema(Connection dbConnection, String mySchema) {

		try {

			Statement st = dbConnection.createStatement();
			Statement st2 = dbConnection.createStatement();

			/*
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
			 */

			/**
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
			 */
			/*
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
			 */

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
					if (sqlType.equals("int") || sqlType.equals("char") || sqlType.equals("enum")) {
						tmpCol.setType(TypeMatching.getJavaType(SqlTypes.valueOf("sql_" + sqlType)));
					} else {
						tmpCol.setType(TypeMatching.getJavaType(SqlTypes.valueOf(sqlType)));
						
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
			createKeysOnColumnName();
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

		/*
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
		 */

		Column col;
		Column[] myColFreeze = new Column[myCol.size()];
		synchronized(myCol){
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
	}
	
	public void keyExtractor(){
		
		/*
		 * Query di estrazione dedicata esclusivamente alle foreign key...
		 * manca la selezione dello schema o della tabella dipende come la si vuole fare
		 
		 select
			t.CONSTRAINT_NAME, 
			t.TABLE_SCHEMA, 
			t.TABLE_NAME, 
			k.COLUMN_NAME,	
			k.REFERENCED_TABLE_NAME,
			k.REFERENCED_COLUMN_NAME,
			t.CONSTRAINT_TYPE,
			r.UPDATE_RULE,
			r.DELETE_RULE
		from 
			key_column_usage k,
			referential_constraints r,
			table_constraints t
		where
			t.CONSTRAINT_NAME = r.CONSTRAINT_NAME and
			t.CONSTRAINT_NAME = k.CONSTRAINT_NAME;
		 */
		
		/*
		 * questa invece per le primary key, manca la selezione per table_name
		 * forse si potrebbe fare un po piu articolata ma questa dovrebbe gia fornire le info necessarie. 
		 * 
		 select
 			TABLE_SCHEMA,TABLE_NAME,COLUMN_NAME,CONSTRAINT_NAME
		from 
			key_column_usage
		where
			CONSTRAINT_NAME = 'PRIMARY';
		 */
		
		
	}
	
	
	
	
}
