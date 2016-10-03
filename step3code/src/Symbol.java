import org.antlr.v4.runtime.*;
import java.util.*;
class Symbol{

	String name;
	String type;
	String value;
	public Symbol (String name, String type, String value){
		this.name = name;
		this.type = type;
		this.value = value;

}
	public void print (){
		if (this.type == "STRING"){
			System.out.println("name " + this.name + " type " + this.type +
		                   " value " + this.value);
		}
		else {
			System.out.println("name " + this.name + " type " + this.type); 
		}
	}





} // end class