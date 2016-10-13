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


}