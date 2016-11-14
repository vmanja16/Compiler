import java.lang.*;
import java.util.*;

class IRList extends LinkedList{
	public IRList(){}



	public IRNode getFirst(){
		return (IRNode) super.getFirst();
	}

	public IRNode getLast(){
		return (IRNode) super.getLast();
	}

	public IRNode remove(int index){
		return (IRNode) super.remove(index);
	}

	public void replace(int index, String new_result){
		IRNode node = remove(index);
		node.result = new_result;
		add(index, node);
	}

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
