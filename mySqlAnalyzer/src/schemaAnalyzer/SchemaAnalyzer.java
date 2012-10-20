/**
 * 
 */
package schemaAnalyzer;

import schemaAnalyzer.services.*;

/**
 * @author ale
 *
 */
public class SchemaAnalyzer {

	public static void main(String[] args){
		
		new SchemaSetup().createSchema();
	}
	
}
