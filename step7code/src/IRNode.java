import java.lang.*;
import java.util.*;
class IRNode extends Object{
	public String opcode;
	public String op1;
	public String op2;
	public String result;
	public ArrayList<IRNode> successors, predecessors;
	public HashSet<String> gen_set, kill_set, in_set, out_set;
	public boolean start, end;
	public int fid;

	public IRNode(String opcode, String op1, String op2, String result){
		this.opcode = opcode;
		this.op1 = op1;
		this.op2 = op2;
		this.result = result;
		this.start = false;
		this.end = false;
		successors = new ArrayList<IRNode>();
		predecessors = new ArrayList<IRNode>();
		gen_set = new HashSet<String>();
		kill_set = new HashSet<String>();
		in_set = new HashSet<String>();
		out_set = new HashSet<String>();
		fid = 0;
	}
	public static IRNode getReturnNode(){
		return new IRNode("RET", null, null, null);
	}
	public static IRNode getJSRNode(String label){
		return new IRNode("JSR", null, null, label);
	}
	public static IRNode getJumpNode(int label_number){
		return new IRNode("JUMP", null, null, "label"+label_number);
	}
	public static IRNode getLabelNode(int label_number){
		return new IRNode("LABEL", null, null, "label"+label_number);
	}
	public static IRNode getLabelNode(String label){
		return new IRNode("LABEL", null, null, label);
	}
	public static IRNode getPushNode(){
		return new IRNode("PUSH", null, null, null);
	}
	public static IRNode getPushNode(String reg){
		return new IRNode("PUSH", reg, null, null);
	}
	public static IRNode getPopNode(){
		return new IRNode("POP", null, null, null);
	}
	public static IRNode getPopNode(String reg){
		return new IRNode("POP", null, null, reg);
	}
	public static String getTempPrefix(){
		return "$T";
	}
	public void addGen(String gen){if (gen!=null)if(!gen.substring(0,1).matches("[0-9]")) gen_set.add(gen);}
	public void addKill(String kill){if (kill!=null)if(!kill.substring(0,1).matches("[0-9]"))kill_set.add(kill);}
	public void print(){
		String one, two, res;
		if (op1 == null){one = "";}else{one = op1 + " ";}
		if (op2 == null){two = "";}else{two = op2 + " ";}
		if (result == null){res = "";}else{res = result;}
		//System.out.println(";" + opcode + " " + one + two + res);
		System.out.print(";" + opcode + " " + one + two + res + "\t");
		System.out.print("LiveOut::{ "); for(String o : out_set){System.out.print(o + ", "); }
		System.out.println("}");
		/*
		System.out.print(";Gen:{ "); for(String gen : gen_set){System.out.print(gen + " ");}
		System.out.print("}");
		System.out.print("Kill:{ "); for(String kill : kill_set){System.out.print(kill + " ");}
		System.out.print("}");
		System.out.print("Pred:{ "); for(IRNode pred : predecessors){System.out.print(pred.opcode + pred.result + ", ");}
		System.out.print("}");
		System.out.print("Succ:{ "); for(IRNode succ : successors){System.out.print(succ.opcode + succ.result + ", ");}
		System.out.print("}");
		System.out.println();
		*/
		if (opcode.equals("RET")){System.out.println();}

	}

/**
					TINY CONVERSION
*/	
	public String tempToReg(String temp_var){
		if (temp_var == null){return null;}
		if (temp_var.startsWith("$T")){
			return "r" +  new Integer(Integer.parseInt(temp_var.substring(2)) - 1 ).toString();
		}
		if(temp_var.startsWith("$L")){
			return temp_var.replace('L','-'); 
		}
		else{return temp_var;}
	}

	public boolean is_branch(){
		switch(opcode){
			case("LEF"):return true;
			case("LEI"):return true;
			case("GEI"):return true;
			case("GEF"):return true;
			case("NEI"):return true;
			case("NEF"):return true;
			case("EQI"):return true;
			case("EQF"):return true;
			case("LTI"):return true;
			case("LTF"):return true;
			case("GTI"):return true;
			case("GTF"):return true;
			case("JUMP"):return true;
			default:return false;

		}
	}

	public boolean is_jump(){
		return opcode.equals("JUMP");
	}


	public TinyList toTiny(){
		String tiny_op1 = tempToReg(op1);
		String tiny_op2 = tempToReg(op2);
		String tiny_res = tempToReg(result);
		TinyList list = new TinyList();
		TinyNode node1, node2;
		switch(opcode){
			case("ADDI"):
				list.addLast(new TinyNode("move", tiny_op1, tiny_res));
				list.addLast(new TinyNode("addi", tiny_op2, tiny_res));
				return list;
			case("ADDF"):
				list.addLast(new TinyNode("move", tiny_op1, tiny_res));
				list.addLast(new TinyNode("addr", tiny_op2, tiny_res));
				return list;
			case("SUBI"):
				list.addLast(new TinyNode("move", tiny_op1, tiny_res));
				list.addLast(new TinyNode("subi", tiny_op2, tiny_res));
				return list;
			case("SUBF"):
				list.addLast(new TinyNode("move", tiny_op1, tiny_res));
				list.addLast(new TinyNode("subr", tiny_op2, tiny_res));
				return list;
			case("MULTI"):
				list.addLast(new TinyNode("move", tiny_op1, tiny_res));
				list.addLast(new TinyNode("muli", tiny_op2, tiny_res));
				return list;
			case("MULTF"):
				list.addLast(new TinyNode("move", tiny_op1, tiny_res));
				list.addLast(new TinyNode("mulr", tiny_op2, tiny_res));
				return list;
			case("DIVI"):
				list.addLast(new TinyNode("move", tiny_op1, tiny_res));
				list.addLast(new TinyNode("divi", tiny_op2, tiny_res));
				return list;
			case("DIVF"):
				list.addLast(new TinyNode("move", tiny_op1, tiny_res));
				list.addLast(new TinyNode("divr", tiny_op2, tiny_res));
				return list;
			case("STOREI"):
				list.addLast(new TinyNode("move", tiny_op1, tiny_res));
				return list;
			case("STOREF"):
				list.addLast(new TinyNode("move", tiny_op1, tiny_res));
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
			case("STR"):
				list.addLast(new TinyNode("str", tiny_op1, result));
				return list;
			case("LABEL"):
				list.addLast(new TinyNode("label", result, null));
				return list;
			case("JUMP"):
				list.addLast(new TinyNode("jmp", result, null));
				return list;
			// CONDITIONALS
			case("EQI"):
				list.addLast(new TinyNode("cmpi", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jeq", result, null));
				return list;
			case("NEI"):
				list.addLast(new TinyNode("cmpi", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jne", result, null));
				return list;
			case("LEI"):
				list.addLast(new TinyNode("cmpi", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jle", result, null));
				return list;
			case("LTI"):
				list.addLast(new TinyNode("cmpi", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jlt", result, null));
				return list;
			case("GEI"):
				list.addLast(new TinyNode("cmpi", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jge", result, null));
				return list;
			case("GTI"):
				list.addLast(new TinyNode("cmpi", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jgt", result, null));
				return list;
			case("EQF"):
				list.addLast(new TinyNode("cmpr", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jeq", result, null));
				return list;
			case("NEF"):
				list.addLast(new TinyNode("cmpr", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jne", result, null));
				return list;
			case("LEF"):
				list.addLast(new TinyNode("cmpr", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jle", result, null));
				return list;
			case("LTF"):
				list.addLast(new TinyNode("cmpr", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jlt", result, null));
				return list;
			case("GEF"):
				list.addLast(new TinyNode("cmpr", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jge", result, null));
				return list;
			case("GTF"):
				list.addLast(new TinyNode("cmpr", tiny_op1, tiny_op2));
				list.addLast(new TinyNode("jgt", result, null));
				return list;
			// FUNCTION Calls
			case("LINK"):
				list.addLast(new TinyNode("link", result, null));	
				return list;
			case("PUSH"):
				list.addLast(new TinyNode("push", tiny_op1, null));
				return list;
			case("POP"):
				list.addLast(new TinyNode("pop", tiny_op1, null));
				return list;
			case("RET"):
				list.addLast(new TinyNode("unlnk", null, null));
				list.addLast(new TinyNode("ret", null, null));
				return list;
			case("JSR"):
				list.addLast(new TinyNode("push", "r0", null));
				list.addLast(new TinyNode("push", "r1", null));
				list.addLast(new TinyNode("push", "r2", null));
				list.addLast(new TinyNode("push", "r3", null));
				list.addLast(new TinyNode("jsr", result, null));
				list.addLast(new TinyNode("pop", "r3", null));
				list.addLast(new TinyNode("pop", "r2", null));
				list.addLast(new TinyNode("pop", "r1", null));
				list.addLast(new TinyNode("pop", "r0", null));
				return list;
			case("HALT"):
				list.addLast(new TinyNode(result, null, null));
				return list;
			default: 
				return null;
		}
	}

} // endclass
