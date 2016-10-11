import java.util.*;
class SymbolTableTree{
    SymbolTable root;
	SymbolTable current_scope;
	public SymbolTableTree(){
		this.root = new SymbolTable("GLOBAL", null, 0);
		this.current_scope = this.root;
	}

	public void enterScope(String scope_name, int block_number){
		SymbolTable new_table = new SymbolTable(scope_name, this.current_scope, block_number);
		this.current_scope.add_table(new_table);
		this.current_scope =  new_table;
	}

	public void exitScope(){
		this.current_scope = this.current_scope.parent;
	}


    public void print(){
    	if(errorCheck()){root.print();}
    }


    private boolean errorCheck(){
    	ArrayList<String> errorList = root.getErrors();
    	if (errorList.isEmpty()){
    		return true;
    	}
    	else{
    		System.out.println("DECLARATION ERROR " + errorList.get(0));
    		return false;
    	}
    }

}