package schemaAnalyzer.services;

public final class TypeMatching {
    
    
    public static String getJavaType(SqlTypes sqlType){
        
        switch(sqlType){
            case bigint:
                return "int";
            case varchar:
                return "String";
            case longtext:
                return "String";
            case datetime:
                return "Date";
            case sql_int:
                return "int";
            case decimal:
                return "double";
            case mediumint:
                return "int";
            case longblob:
                return "Object";
            case tinytext:
                return "String";
            case tinyint:
                return "int";
            case smallint:
                return "int";
            case mediumtext:
                return "String";
            case date:
                return "Date";
            case sql_char:
                return "char";
            case timestamp:
                return "Date";
            case set:
                return "Set";
            case sql_enum:
                return "enum";
            case text:
                return "String";
            case blob:
                return "Object";
            case time:
                return "Date";
            default: return null;
        }   
    }
}
