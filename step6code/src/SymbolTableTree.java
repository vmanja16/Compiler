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
    public int getCurrentEntrance(){
        return current_scope.enter_label;
    }    
    public int getCurrentExit(){
        return current_scope.exit_label;
    }
    public void print(){
    	if(errorCheck()){root.print();}
    }
    public boolean isRoot(){
        return (current_scope==root);
    }
    public boolean isRoot(SymbolTable table){
        return (table==root);
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