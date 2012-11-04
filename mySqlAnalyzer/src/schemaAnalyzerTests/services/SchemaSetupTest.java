package schemaAnalyzerTests.services;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import schemaAnalyzer.SchemaAnalyzer;
import schemaAnalyzer.domain.Column;
import schemaAnalyzer.domain.Table;
import schemaAnalyzer.services.SchemaSetup;

public class SchemaSetupTest {

	private SchemaSetup schema;
	
	@Before
	public final void createSchemaSetup() {
	//	fail("Not yet implemented"); // TODO
		SchemaAnalyzer.loadConnection();
		
		schema = new SchemaSetup(SchemaAnalyzer.createConnection(), SchemaAnalyzer.getQuery(), SchemaAnalyzer.getDbSchema());
		Assert.assertNotNull(schema);
		schema.loadSchema();
		Assert.assertNotNull(schema.getSchema());
		
	}

	/*@Test
	public final void testGetSchema() {
		fail("Not yet implemented"); // TODO
	}*/

	@Test
	public final void testLoadSchema() {
	//	fail("Not yet implemented"); // TODO
		
		
		/*
		 * id chiave di whine_events
		 whine_events;id;int;
		 whine_events;body;String;
		 whine_events;mailifnobugs;int;
		 whine_events;owner_userid;int; //this will be a foreign_key
		 whine_events;subject;String;
		 * */
		
		Table testTable = new Table("whine_events");
		Column id = new Column("id");
		id.setTableKeyOf(testTable);
		id.setType("int");
		testTable.addKey(id);
		
		Column body = new Column("body");
		body.setType("String");
		testTable.addColumn(body);
		
		Column mailifnobugs = new Column("mailifnobugs");
		body.setType("int");
		testTable.addColumn(mailifnobugs);
		
		Column owner_userid = new Column("owner_userid");
		body.setType("int");
		testTable.addColumn(owner_userid);
		
		Column subject = new Column("subject");
		body.setType("String");
		testTable.addColumn(subject);
		
		
		
		
		
		Table sampledTable = schema.getTable("whine_events");
		
		Assert.assertEquals("comparision between tables failed", testTable, sampledTable);
		Assert.assertEquals("comparision between key failed", testTable.getKey(), sampledTable.getKey());
		Assert.assertEquals("comparision between columns failed", testTable.getColumns(), sampledTable.getColumns());
		
	}

	@Test
	public final void testIsPKey() {
		/*
		 *  namedqueries_link_in_footer;namedquery_id;int;
		 *  namedqueries_link_in_footer;user_id;int;
		 *
		 */
		
		Table testTable = new Table("namedqueries_link_in_footer");
		
		Column namedquery_id = new Column("namedquery_id");
		namedquery_id.setTableKeyOf(testTable);
		namedquery_id.setType("int");
		testTable.addKey(namedquery_id);
		
		Column user_id = new Column("user_id");
		namedquery_id.setTableKeyOf(testTable);
		namedquery_id.setType("int");
		testTable.addKey(user_id);
		
		Table sampleTable = schema.getTable("namedqueries_link_in_footer");
		
		Assert.assertEquals(testTable, sampleTable);
		Assert.assertTrue(sampleTable.getColumns().isEmpty());
		Assert.assertEquals(testTable.getKey(),sampleTable.getKey());
		Assert.assertTrue(sampleTable.getKey().contains(user_id));
		Assert.assertTrue(sampleTable.getKey().contains(namedquery_id));
		
		
		
		
		
	}

/*	@Test
	public final void testSetDataType() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testKeyExtractor() {
		fail("Not yet implemented"); // TODO
	}
*/
}
