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

	public TinyNode toTiny(){
		switch(opcode){
			case("WRITEI"):
				return new TinyNode("sys writei", result, null);	
			case("WRITEF"):
				return new TinyNode("sys writer", result, null);
			default: return null;
		}
	}

} // endclass
