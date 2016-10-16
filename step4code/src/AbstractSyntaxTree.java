import java.util.*;
import java.lang.*;

class AbstractSyntaxTree{
	public ExpressionNode root;
	public LinkedList operator_stack;
	public LinkedList expression_stack;
	public Symbol lhs;	
	private static final Map<String, Integer> precedence_map = Collections.unmodifiableMap(
		new HashMap<String, Integer>() {{
			put("(", 0);
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
				BUILD METHODS
*/			
	public void add_operand(String operand){
		ExpressionNode node = new ExpressionNode(operand);
		expression_stack.push(node);
	}
	public void add_operator(String operation){
		if (!operator_stack.isEmpty()){
		    while(getPrecedence((String)operator_stack.getLast()) >= getPrecedence(operation)){
	            build_node_from_stack();
	            if(operator_stack.isEmpty()){break;}
	        }
        }
        operator_stack.push(operation);
	}
	private void build_node_from_stack(){
		String operator = (String)operator_stack.pop();
        ExpressionNode op2 = (ExpressionNode) expression_stack.pop();
        ExpressionNode op1 = (ExpressionNode) expression_stack.pop();
        ExpressionNode node = new ExpressionNode(operator, op1, op2);
        expression_stack.push(node);
	}
	public void close_expr(){
		/* Called when ')' is seen.
		( expr ')': builds expr nodes, deletes '(' */ 
	    while (operator_stack.getLast() != '('){
	    	build_node_from_stack();
	    	if(operator_stack.isEmpty()){break;}
	    }
        operator_stack.pop(); 
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
		updateRoot();
		if (root != null){root.print();}
	}

}