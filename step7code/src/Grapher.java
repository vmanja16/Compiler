import java.util.*;
import java.lang.*; 
class Grapher{

	public LinkedHashMap<String,String> regs = new LinkedHashMap<String,String>();
	public LinkedHashMap<String,String> lvars = new LinkedHashMap<String,String>();
	public IRList ir_list;
	public ArrayList<String> globals;
	public Function function;
	public TinyList tiny_list;
	public int link_count;
/**
				CONSTRUCTOR
*/
	public Grapher(IRList ir_list, ArrayList<String> globals, Function function){
		this.ir_list = ir_list;
		this.globals = globals;
		this.function = function;
		for(int i = 0; i < ir_list.size(); i++){
			ir_list.get(i).fid = i+1;
		}

		regs.put("r3", "");
		regs.put("r2", "");
		regs.put("r1", "");
		regs.put("r0", "");
		
		for (int i=1; i <= function.local_count; i++){
			lvars.put("$-"+i, "$L"+i);
		}
		link_count = function.local_count;

	}

	public boolean full(){
		return(
			regs.get("r3").equals("") &&
			regs.get("r2").equals("") &&
			regs.get("r1").equals("") &&
			regs.get("r0").equals("")
		);
	}

	public String getSpillLocation(String temp_to_spill){
		// look for lowest empty link # ($-#) 
		for(String key : lvars.keySet()){
			if(lvars.get(key).equals(temp_to_spill)){return key;}
		}
		// move temp to $-# and update lvars map 
		link_count++;
		String lc = "$-"+link_count; 
		lvars.put(lc, temp_to_spill);
		return lc;
	}

	public String getFreeReg(){
		if (regs.get("r3").equals("")){return "r3";}
		if (regs.get("r2").equals("")){return "r2";}
		if (regs.get("r1").equals("")){return "r1";}
		if (regs.get("r0").equals("")){return "r0";}
		return null;
	}

/**
				Calculates in_set and out_set: Does multiple passes to reach CONVERGENCE (a guess at this point) 
*/
	public void calculateLiveness(){
		HashSet<String> temp_set = new HashSet<String>();
		IRNode curr_node;
		for (int j = 0; j< 10; j++){

			for(int i=ir_list.size()-1; i >= 0; i--){
				temp_set.clear();
				curr_node = ir_list.get(i);

				// LiveOut = LiveIn(successors) --> up to 2 successors 
				for(IRNode suc: curr_node.successors){curr_node.out_set.addAll(suc.in_set);}
				// LiveIn  = Gen(s) + (LiveOut(s) - Kill(s))
				curr_node.in_set.addAll(curr_node.gen_set); // + Gen(s)
				temp_set.addAll(curr_node.out_set);         
				for(String s : curr_node.kill_set){temp_set.remove(s);}
				curr_node.in_set.addAll(temp_set);       // + LiveOut(s) - Kill(s)

			}
		}
	}

/** 
Creates Successors based on instruction 
*/
	void calculateSuccessors(){
		for(int i=0; i< ir_list.size()-1; i++){
			IRNode node = ir_list.get(i);
			if(node.is_branch()){node.successors.add(ir_list.findTargetNode(node.result));} // add branch target
			if(!node.is_jump()){node.successors.add(ir_list.get(i+1));}
		}
	}
	// should be called on the IRLIST of a function!
/** 
Creates Predecessors based on successors 
*/
	void calculatePredecessors(){
		for(int i=0; i < ir_list.size(); i++){
			IRNode node = ir_list.get(i);
			for (IRNode suc : node.successors){
				suc.predecessors.add(node);
			}			
		}
	}

/** 
Creates KILL Sets based on opcodes
*/
	void calculateKill(){
		for(int i=0; i< ir_list.size(); i++){
			IRNode node = (IRNode)ir_list.get(i);

			switch(node.opcode){
				case("ADDI"):node.addKill(node.result); break;
				case("ADDF"):node.addKill(node.result); break;
				case("SUBI"):node.addKill(node.result); break;
				case("SUBF"):node.addKill(node.result); break;
				case("MULTI"):node.addKill(node.result); break;
				case("MULTF"):node.addKill(node.result); break;
				case("DIVI"):node.addKill(node.result); break;
				case("DIVF"):node.addKill(node.result); break;
				case("STOREI"):node.addKill(node.result); break;
				case("STOREF"):node.addKill(node.result); break;
				case("READI"):node.addKill(node.result); break;
				case("READF"):node.addKill(node.result); break;
				case("POP"): node.addKill(node.op1); break;
				// FUNCTION Calls
				default: break;
			}
		}
	}
/** 
CreatesGEN Sets based on opcodes
*/
	void calculateGen(){
		for(int i=0; i< ir_list.size(); i++){
			IRNode node = (IRNode)ir_list.get(i);

			switch(node.opcode){
				case("ADDI"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("ADDF"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("SUBI"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("SUBF"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("MULTI"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("MULTF"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("DIVI"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("DIVF"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("STOREI"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("STOREF"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("WRITEI"):node.addGen(node.result); break;
				case("WRITEF"):node.addGen(node.result); break;
				case("LEF"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("LEI"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("GEI"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("GEF"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("NEI"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("NEF"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("EQI"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("EQF"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("LTI"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("LTF"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("GTI"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("GTF"):node.addGen(node.op1); node.addGen(node.op2); break;
				case("PUSH"):node.addGen(node.op1);
				// TODO:
				case("JSR"): for(String glb : globals){node.addGen(glb);}


				
				//case("var"): node.addGen(node.result);
				// FUNCTION Calls
				default: break;
			}
		}
	}

public String tempToReg(String temp_var){
		if (temp_var == null){return null;}
		if (temp_var.startsWith("$T")){
						
		}
		if(temp_var.startsWith("$L")){
			return temp_var.replace('L','-'); 
		}
		else{return temp_var;}
	}



/*
	public TinyList IRTiny(IRNode node){
		String tiny_op1 = tempToReg(op1);
		String tiny_op2 = tempToReg(op2);
		String tiny_res = tempToReg(result);
		TinyList list = new TinyList();
		TinyNode node1, node2;
		switch(node.opcode){
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
				list.addLast(new TinyNode("pop", "r0", null));
				list.addLast(new TinyNode("pop", "r1", null));
				list.addLast(new TinyNode("pop", "r2", null));
				list.addLast(new TinyNode("pop", "r3", null));
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
*/






}