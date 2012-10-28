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
    private Column key;

    public Table(String name) {
        this.name = name;
        columns = new LinkedList<Column>();
    }

    public String getName() {
        return name;
    }

    public Column getKey() {
		return key;
	}

	public void setKey(Column key) {
		this.key = key;
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
    	if(getKey() != null){
    		z = this.name+";" + getKey();    		
    	}
    	else z = "";
    	
        for (Column c : columns) {
            z += this.name+";" + c ;
        }
        return z;
    }
    
}
