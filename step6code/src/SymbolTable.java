import org.antlr.v4.runtime.*;
import java.util.*;
import java.lang.*;


class SymbolTable{
	private ArrayList<String> declaration_errors = new ArrayList<String>();
    ArrayList<Symbol> symbols = new ArrayList<Symbol>();
    ArrayList<SymbolTable> tables = new ArrayList<SymbolTable>();
    public HashMap<Symbol, Integer> TempRegMap = new HashMap<Symbol, Integer>();
    public int block_number;
    public int enter_label;
    public int exit_label;
	public String scope_name;
	public SymbolTable parent;
	public Function function;
	public SymbolTable(String scope_name, SymbolTable parent, int block_number){
		this.scope_name = scope_name;
		this.parent = parent;
		this.function = null;
		this.block_number = block_number;             
		this.enter_label = 2 * block_number;
		this.exit_label = this.enter_label + 1;
	}
/**
             BUILD FUNCTIONS
*/
	public void add_function(Function function){
		this.function = function;
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
	public Symbol getSymbol(String symbol_name){
		// TODO: WHAT IF SYMBOL IS NOT IN SYMBOLS(null exc)!
		for (Symbol symbol : symbols){
			if (symbol_name.equals(symbol.name)){
				return symbol;
			}
		}
		if (parent == null){return null;}
		else return parent.getSymbol(symbol_name);
	}	
	public Symbol getParameter(String symbol_name){
		return function.getParameter(symbol_name);
	}
	public Symbol getLocal(String symbol_name){
		return function.getLocal(symbol_name);
	}
/**
             REGISTER FUNCTIONS
*/	
   public void addTempReg(String symbol_name, Integer reg){
   		TempRegMap.put(getSymbol(symbol_name), reg);
   }
   public boolean getTempReg(String symbol_name){
   		Symbol symbol = getSymbol(symbol_name);
   		Integer value = TempRegMap.get(symbol);
   		if (value != null){return true;}
   		else {return false;}
   }

} // end class	
