class ExpressionNode{
	public String value;
	public ExpressionNode op1;
	public ExpressionNode op2;
	// Constructors
	public ExpressionNode(String value, ExpressionNode op1, ExpressionNode op2){
		this.value = value;
		this.op1 = op1;
		this.op2 = op2;
	}
	public ExpressionNode(String value){
		this.value = value;
		this.op1 = null;
		this.op2 = null;	
	}

	public void print(){
		if (op1 != null){System.out.print("[");op1.print();}
		System.out.print(value+" ");
		if (op2 != null){op2.print();System.out.print("] ");}
	}
}