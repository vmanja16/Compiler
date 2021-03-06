import org.antlr.v4.runtime.*;
import java.util.*;


class SymbolTable{
	private ArrayList<String> declaration_errors = new ArrayList<String>();
    ArrayList<Symbol> symbols = new ArrayList<Symbol>();
    ArrayList<SymbolTable> tables = new ArrayList<SymbolTable>();
    int block_number;
	public String scope_name;
	public SymbolTable parent;
	public SymbolTable(String scope_name, SymbolTable parent, int block_number){
		this.scope_name = scope_name;
		this.parent = parent;
		this.block_number = block_number;
	}

	public void add_symbol(Symbol symbol){
			declarationErrorCheck(symbol);
			symbols.add(symbol);
	}
	public void add_table(SymbolTable table){
		this.tables.add(table);
	}

	private void declarationErrorCheck (Symbol symbol) {
		for(Symbol s : symbols){
			if (symbol.name.equals(s.name)){
				declaration_errors.add(symbol.name);
			}
		}
	}

	public void print(){
		if (this.block_number == 0){System.out.println("Symbol table " + this.scope_name);}
		else {System.out.println("Symbol table " + this.scope_name + " " + this.block_number);}
		for (Symbol symbol : this.symbols){
			symbol.print();
		}
		for (SymbolTable table : this.tables){
			System.out.println("");
			table.print();
		}
	}

	public ArrayList<String> getErrors(){
		ArrayList<String> errorList = new ArrayList<String>();
		if (!declaration_errors.isEmpty()){
			errorList.addAll(declaration_errors);
		}
		
		for (SymbolTable table : tables){
			ArrayList<String> arr = table.getErrors();
			if (!arr.isEmpty()){
				errorList.addAll(arr);
			}
		}
		return errorList;
	}

} // end class	
