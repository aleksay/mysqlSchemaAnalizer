package schemaAnalyzer.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	File logfile;
	FileWriter out;

	public Logger() {
		logfile = new File("./schemaAnalyzer.out");
		try {
			
			
			if(logfile.exists() && logfile.canWrite()){
				out = new FileWriter(logfile);
				out.append("--------------------------------------------------------------");
			}else{
				if (!logfile.createNewFile()) {
					throw new IllegalStateException("impossibile creare il file di log");
				}else{
					out = new FileWriter(logfile);			
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}

	public void append(Object message){
		try {
			out.append(message.toString());
			System.out.println(message.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void close(){
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}


