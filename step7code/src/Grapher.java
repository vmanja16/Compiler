import java.util.*;
import java.lang.*; 
class Grapher{
	
	public IRList ir_list;
	public ArrayList<String> globals;

	public Grapher(IRList ir_list, ArrayList<String> globals){
		this.ir_list = ir_list;
		this.globals = globals;
	}

	// should be called on the IRLIST of a function!
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

	void calculateLiveness(){

	}

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
}