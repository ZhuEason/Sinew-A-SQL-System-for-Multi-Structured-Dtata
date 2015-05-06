package userVersion2;
	

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import userLayer.JasonParser;

public class Loader {
	
	public Loader(String path) throws IOException, SQLException {
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		
		String s = "";
		while ((s = br.readLine()) != null) {
//		    System.out.println(s);
		    Format p = new Format(s);
		    Data data = new Data(p);
		    Serialization.writeObject(data);
		}
	}
	
	public static void main(String[] args) throws IOException, SQLException{
		Loader l = new Loader(args[0]);
	}
	
}
