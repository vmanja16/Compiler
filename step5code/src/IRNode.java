import java.lang.*;
import java.util.*;
class IRNode extends Object{
	public String opcode;
	public String op1;
	public String op2;
	public String result;
	private HashMap<String, String> ir_tiny_map;

	public IRNode(String opcode, String op1, String op2, String result){
		this.opcode = opcode;
		this.op1 = op1;
		this.op2 = op2;
		this.result = result;
	}
	public void print(){
		String one, two;
		if (op1 == null){one = "";}else{one = op1 + " ";}
		if (op2 == null){two = "";}else{two = op2 + " ";}
		System.out.println(";" + opcode + " " + one + two + result);
	}
	public String tempToReg(String temp_var){
		if (temp_var == null){return null;}
		if (temp_var.startsWith("$")){
			return "r" +  new Integer(Integer.parseInt(temp_var.substring(2)) - 1 ).toString();
		}
		else{return temp_var;}
	}

	public TinyList toTiny(){
			String tiny_op1 = tempToReg(op1);
			String tiny_op2 = tempToReg(op2);
			String tiny_res = tempToReg(result);
			TinyList list = new TinyList();
			TinyNode node1, node2;
		switch(opcode){
			case("ADDI"):
				node1 = new TinyNode("move", tiny_op1, tiny_res);
				node2 = new TinyNode("addi", tiny_op2, tiny_res);
				list.addLast(node1); list.addLast(node2);
				return list;
			case("ADDF"):
				node1 = new TinyNode("move", tiny_op1, tiny_res);
				node2 = new TinyNode("addr", tiny_op2, tiny_res);
				list.addLast(node1); list.addLast(node2);
				return list;
			case("SUBI"):
				node1 = new TinyNode("move", tiny_op1, tiny_res);
				node2 = new TinyNode("subi", tiny_op2, tiny_res);
				list.addLast(node1); list.addLast(node2);
				return list;
			case("SUBF"):
				node1 = new TinyNode("move", tiny_op1, tiny_res);
				node2 = new TinyNode("subr", tiny_op2, tiny_res);
				list.addLast(node1); list.addLast(node2);
				return list;
			case("MULTI"):
				node1 = new TinyNode("move", tiny_op1, tiny_res);
				node2 = new TinyNode("muli", tiny_op2, tiny_res);
				list.addLast(node1); list.addLast(node2);
				return list;
			case("MULTF"):
				node1 = new TinyNode("move", tiny_op1, tiny_res);
				node2 = new TinyNode("mulr", tiny_op2, tiny_res);
				list.addLast(node1); list.addLast(node2);
				return list;
			case("DIVI"):
				node1 = new TinyNode("move", tiny_op1, tiny_res);
				node2 = new TinyNode("divi", tiny_op2, tiny_res);
				list.addLast(node1); list.addLast(node2);
				return list;
			case("DIVF"):
				node1 = new TinyNode("move", tiny_op1, tiny_res);
				node2 = new TinyNode("divr", tiny_op2, tiny_res);
				list.addLast(node1); list.addLast(node2);
				return list;
			case("STOREI"):
				node1 = new TinyNode("move", tiny_op1, tiny_res);
				list.addLast(node1);
				return list;
			case("STOREF"):
				node1 = new TinyNode("move", tiny_op1, tiny_res);
				list.addLast(node1);
				return list;
			case("READI"):
				list.add(new TinyNode("sys readi", result, null));	
				return list;
			case("READF"):
				list.add(new TinyNode("sys readr", result, null));
				return list;
			case("WRITEI"):
				list.addLast(new TinyNode("sys writei", result, null));	
				return list;
			case("WRITEF"):
				list.addLast(new TinyNode("sys writer", result, null));
				return list;
			case("WRITES"):			
				list.addLast(new TinyNode("sys writes", result, null));
				return list;
			case("var"):
				list.addLast(new TinyNode("var", result, null));
				return list;
			default: 
				print();System.out.println("Didn't match!"); return null;
		}
	}

} // endclass
