package userVersion2;

import java.io.Serializable;

public class Data implements Serializable{
	 public String key[];
	 public int aid[];
	 public Object value[];
	 
	 public Data(Format p) {
		 key = p.key;
		 value = p.value;
		 aid = p.aid;
	 }
}
