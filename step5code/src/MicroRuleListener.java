
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

class MicroRuleListener extends MicroBaseListener{
	public SymbolTableTree tree;

	public MicroRuleListener(SymbolTableTree tree){
		this.tree = tree;
	}

	@Override public void enterRead_stmt(MicroParser.Read_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitRead_stmt(MicroParser.Read_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterWrite_stmt(MicroParser.Write_stmtContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitWrite_stmt(MicroParser.Write_stmtContext ctx) { 
		//write_stmt: 'WRITE' '(' id_list ')' ';'
		//String[] idList = $id_list.text.split(",");
	    //String opcode = null;
	    //for (String id : idList){
		 //   Symbol symbol = tree.current_scope.getSymbol(id);
		  //  if (symbol.type.equals("INT")){opcode = "WRITEI";}
		   // else if (symbol.type.equals("FLOAT")){opcode = "WRITEF";}
		    //IRNode ir_node = new IRNode(opcode, null, null, id);
		    //ir_list.addLast(ir_node); 
	   // }
	}






}