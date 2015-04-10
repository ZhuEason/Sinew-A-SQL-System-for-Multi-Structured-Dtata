package bytetry;

import java.io.Serializable;

public class Book implements Serializable{
    public int isbn;
    
    public Book() {
	super();
	isbn = 10;
    }
    
    public String toString() {
	return "Book" ;
	
    }
}
