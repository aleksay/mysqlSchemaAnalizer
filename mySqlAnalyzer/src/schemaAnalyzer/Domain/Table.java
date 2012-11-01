/**
 * 
 */
package schemaAnalyzer.Domain;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ale
 *
 */
public class Table implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
    private LinkedList<Column> columns;
    private LinkedList<Column> keys;

    public Table(String name) {
        this.name = name;
        columns = new LinkedList<Column>();
        keys = new LinkedList<Column>();
    }

    public String getName() {
        return name;
    }

    public LinkedList<Column> getKey() {
		return keys;
	}

	public void addKey(Column key) {
		keys.add(key);
	}

	public void addColumn(Column col) {
        columns.add(col);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Table)) {
            return false;
        }
        Table obj = (Table) o;
        return obj.getName().equals(this.name);
    }

    public String toString() {
        return "Tabella: " + name + " \n ";
    }
    
    public String list(){
        
    	String z;
    	if(!getKey().isEmpty()){
    		z = this.name+";" ;
    		for(Column c:keys){
    			z += c;
    		}
    	}
    	else z = "";
    	
        for (Column c : columns) {
            z += this.name+";" + c ;
        }
        return z;
    }
    
}
