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
public class Column implements Serializable, Cloneable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
    private String type;
    private LinkedList<Table> tables;

    public Column(String name) {
        this.name = name;
        tables = new LinkedList<Table>();
    }

    public String getName() {
        return name;
    }
    
    protected synchronized void setName(String name){
        this.name = name;
    }

    public synchronized void addTable(Table tab) {
        tables.add(tab);
    }

    public List<Table> getTables() {
        return tables;
    }

    public synchronized void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Column)) {
            return false;
        }
        Column obj = (Column) o;
        return obj.getName().equals(this.name);
    }
    
    @Override
    public synchronized Column clone(){
        
        Column c = new Column(this.getName());
        c.type = this.getType();
        for(Table t: this.getTables()){
            c.addTable(t);
        }
        return c;
    }

    @Override
    public String toString() {

        return "column: " + name + " type: " + type + "\n";
        
    }
    public String list(){
        String z = this.toString();
        for (Table c : tables) {
            z += "   " + c ;
        }        
        return z;
    }
}