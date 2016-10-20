import java.lang.*;
import java.util.*;


class TinyList extends LinkedList{
	public TinyList(){}


	public void print(){
		System.out.println(";tiny code");
		Object[] tiny_array= toArray();
		for(Object node : tiny_array){
			((TinyNode)node).print();
		}
	}
	
}