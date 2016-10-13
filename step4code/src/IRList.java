import java.lang.*;
import java.util.*;

class IRList extends LinkedList{
	public IRList(){}


	public void print(){
		System.out.println(";IR code");
		Object[] ir_array= toArray();
		for(Object node : ir_array){
			((IRNode)node).print();
		}
	}

	public LinkedList toTiny(){
		tiny_list = new LinkedList();
		Object[] ir_array= toArray();
		for(Object node : ir_array){
			tiny_list.addLast(((IRNode)node).toTiny());
		}	
	}


}
