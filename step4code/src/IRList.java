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

	public TinyList toTiny(){
		TinyList tiny_list = new TinyList();
		Object[] ir_array= toArray();
		for(Object node : ir_array){
			tiny_list.addAll( ( (IRNode)node).toTiny());
		}	
		return tiny_list;
	}


}
