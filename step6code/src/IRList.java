import java.lang.*;
import java.util.*;

class IRList extends LinkedList{
	public IRList(){}


	public  IRList callMainList(){
		IRList ir_list = new IRList();
		ir_list.add(IRNode.getPushNode());
		ir_list.add(IRNode.getPushNode("$T1"));
		ir_list.add(IRNode.getPushNode("$T2"));
		ir_list.add(IRNode.getPushNode("$T3"));
		ir_list.add(IRNode.getPushNode("$T4"));
		ir_list.add(IRNode.getJSRNode("main"));
		ir_list.add(new IRNode("HALT", null, null, "sys halt"));
		return ir_list;
	}


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
			if (node != null){
				tiny_list.addAll( ( (IRNode)node).toTiny());
			}
		}	
		return tiny_list;
	}


}
