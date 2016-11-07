import java.util.*;
import java.lang.*;

class AbstractSyntaxTree{
	public ExpressionNode root;
	public LinkedList operator_stack;
	public LinkedList expression_stack;
	public IRList ir_list;
	public Symbol lhs;
	private Integer temp_count;
	public String type;
	public SymbolTable table;
	public String input;

	private static final Map<String, Integer> precedence_map = Collections.unmodifiableMap(
		new HashMap<String, Integer>() {{
			put("open", 0);
			put("+", 1);
			put("-", 1);
			put("*", 2);
			put("/", 2);
		}});

	public AbstractSyntaxTree(Symbol lhs, int reg_number, SymbolTable table){
		this.lhs = lhs;
		operator_stack = new LinkedList();
		expression_stack = new LinkedList();
		ir_list = new IRList();
		root = null;
		temp_count = new Integer(reg_number);
		if (lhs.type.equals("FLOAT")){type = "F";}else{type = "I";}
		this.table = table;
	}
		public AbstractSyntaxTree(String input, int reg_number, SymbolTable table){
		this.lhs = null;
		operator_stack = new LinkedList();
		expression_stack = new LinkedList();
		ir_list = new IRList();
		root = null;
		temp_count = new Integer(reg_number);
		//if (lhs.type.equals("FLOAT")){type = "F";}else{type = "I";}
		determineType(input);
		this.table = table;
	}

	public void String determineType(){
		// check if symbol involved
		if input.contains('.'){type = "F"; return}
		for (String str : input.split(" ")){
			if (table.getSymbol(str) != null){
				type = table.getSymbol(str).type[0]; return;
			}
		type = "I";
		}
	}

/** 
				BUILD METHODS
*/			
	public void add_operand(String operand){
		ExpressionNode node = new ExpressionNode(operand);
		expression_stack.push(node);

        //print_expression_stack();
        //print_operation_stack();
	}
	public void add_operator(String operation){
		if (!operator_stack.isEmpty()){
		    while(getPrecedence(operation) <= getPrecedence((String)operator_stack.getFirst()) ){
	            build_node_from_stack();
	            if(operator_stack.isEmpty()){break;}
	        }
        }
        operator_stack.push(operation);

        //print_expression_stack();
        //print_operation_stack();
	}
	private void build_node_from_stack(){
		String operator = (String)operator_stack.pop();
        ExpressionNode op2 = (ExpressionNode) expression_stack.pop();
        ExpressionNode op1 = (ExpressionNode) expression_stack.pop();
        ExpressionNode node = new ExpressionNode(operator, op1, op2);
        expression_stack.push(node);
	}
	public void open_expr(){
		operator_stack.push("open");

        //print_expression_stack();
        //print_operation_stack();
	}
	public void close_expr(){
		// Called when ')' is seen.
		//( expr ')': builds expr nodes, deletes '('
	    while (!operator_stack.getFirst().equals("open")){
	    	build_node_from_stack();
	    	if(operator_stack.isEmpty()){break;}
	    }
        operator_stack.pop();
        //print_expression_stack();
        //print_operation_stack();

	}
	public void end(){
		while(!operator_stack.isEmpty()){
			build_node_from_stack();
			//print_expression_stack();
			//print_operation_stack();
			//updateRoot();
		}
		updateRoot();
		post_order(root);
		if (lhs == null){return;}
		ir_list.addLast(new IRNode("STORE"+type, root.value, null, lhs.name));
		table.addTempReg(lhs.name, getTempCount());

	}
	private void updateRoot(){
		if (!expression_stack.isEmpty()){
			root = (ExpressionNode) expression_stack.pop();
		}
	}
	private int getPrecedence(String operator){
		return precedence_map.get(operator).intValue();
	}
	/** 
				PRINT METHODS
*/

	public void print_expression(){
		updateRoot();
		if (root != null){root.print(); System.out.println("\n\n\n"); System.out.println("\n\n\n");}
	}
	public void print_expression_stack(){
		System.out.println("\nCurrent Stack");
		for (int i=0; i < expression_stack.size(); i++){
			System.out.print( ((ExpressionNode)expression_stack.get(i)).value + " ");
		}
	}
	public void print_operation_stack(){
		System.out.println("\nOP Stack");
		for (int i=0; i < operator_stack.size(); i++){
			System.out.print( (String)operator_stack.get(i) + " ");
		}
		System.out.println("\n");
	}
	public void print_ir_list(){
		ir_list.print();
	}
/** 
				TRAVERSAL METHODS
*/
	public int getTempCount(){return temp_count.intValue();}
	private String getLatestTemp(){return ("$T" + temp_count.toString());}
	private String getNewTemp(){
		temp_count+=1;
		return getLatestTemp();
	}
	
	private IRNode createIRNode(ExpressionNode node){
	    if (node.value.equals("+")){return new IRNode("ADD"+type, node.op1.value, node.op2.value, getNewTemp());} // add
		else if (node.value.equals("-")){return new IRNode("SUB"+type, node.op1.value, node.op2.value, getNewTemp());} // sub
		else if (node.value.equals("*")){return new IRNode("MULT"+type, node.op1.value, node.op2.value, getNewTemp());} // mul
		else if (node.value.equals("/")){return new IRNode("DIV"+type, node.op1.value, node.op2.value, getNewTemp());} // div	
		else {
				if (table.getSymbol(node.value) != null){ // is a symbol
					if (table.getTempReg(node.value)){
						return null;
					}
					else{
						getNewTemp();
						table.addTempReg(node.value, getTempCount());
						return new IRNode("STORE"+type, node.value, null, getLatestTemp());
					} 
				}
				else{ // is a literal
					return new IRNode("STORE"+type, node.value, null, getNewTemp()); // store temp
				}
		}
	}
	private void Merge(ExpressionNode node, String replacment_value){
		node.op1 = null;
		node.op2 = null;
		node.value = replacment_value;
	}
	private void post_order(ExpressionNode node){
		if (node.op1 != null){
			post_order(node.op1);
		}
		if (node.op2 != null){
			post_order(node.op2);
		}
		
		IRNode newIR = createIRNode(node);
		if (newIR != null){
			ir_list.addLast(newIR);
			Merge(node, getLatestTemp());
		}
	}



}