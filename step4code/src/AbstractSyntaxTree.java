import java.util.*;
import java.lang.*;

class AbstractSyntaxTree{
	public ExpressionNode root;
	public LinkedList operator_stack;
	public LinkedList expression_stack;
	public Symbol lhs;	
	private static final Map<String, Integer> precedence_map = Collections.unmodifiableMap(
		new HashMap<String, Integer>() {{
			put("open", 0);
			put("+", 1);
			put("-", 1);
			put("*", 2);
			put("/", 2);
		}});

	public AbstractSyntaxTree(Symbol lhs){
		this.lhs = lhs;
		operator_stack = new LinkedList();
		expression_stack = new LinkedList();
		root = null;
	}

/** 
				BUILD METHODSs
*/			
	public void add_operand(String operand){
		//System.out.println(operand);
		ExpressionNode node = new ExpressionNode(operand);
		expression_stack.push(node);
	}
	public void add_operator(String operation){
		//System.out.println(operation);
		if (!operator_stack.isEmpty()){
		    while(getPrecedence(operation) <= getPrecedence((String)operator_stack.getFirst()) ){
	            build_node_from_stack();
	            if(operator_stack.isEmpty()){break;}
	        }
        }
        operator_stack.push(operation);
	}
	public void print_expression_stack(){
		System.out.println("\nCurrent Stack");
		for (int i=0; i < expression_stack.size(); i++){
			System.out.print( ((ExpressionNode)expression_stack.get(i)).value + " ");
		}
	}
	private void build_node_from_stack(){
		//print_expression_stack();
		String operator = (String)operator_stack.pop();
        ExpressionNode op2 = (ExpressionNode) expression_stack.pop();
        ExpressionNode op1 = (ExpressionNode) expression_stack.pop();
        ExpressionNode node = new ExpressionNode(operator, op1, op2);
        expression_stack.push(node);
	}
	public void open_expr(){
		//System.out.println("open");
		operator_stack.push("open");
	}
	public void close_expr(){
		/* Called when ')' is seen.
		( expr ')': builds expr nodes, deletes '(' */ 
	    while (!operator_stack.getFirst().equals("open")){
	    	build_node_from_stack();
	    	if(operator_stack.isEmpty()){break;}
	    }
        operator_stack.pop(); 
	}
	public void end(){
		while(!operator_stack.isEmpty()){
			build_node_from_stack();
		}
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
				TRAVERSAL METHODS
*/
	public void print(){
		//print_expression_stack();
		updateRoot();
		if (root != null){root.print(); System.out.println();}
	}

}