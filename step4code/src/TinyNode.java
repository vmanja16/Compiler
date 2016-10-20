class TinyNode{
	public String opcode;
	public String op1;
	public String op2;
	public TinyNode(String opcode, String op1, String op2){
		this.opcode = opcode;
		this.op1 = op1;
		this.op2 = op2;
		// op2 may be null!
	}

	public void print(){
		String o1, o2;
		if (op1 == null){o1 = "";}else{o1 = op1;}
		if (op2 == null){o2 = "";}else{o2 = op2;}
		System.out.println(opcode + " " + o1 + " " + o2);
	}


}