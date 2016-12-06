import java.util.*;
import java.lang.*;

class Function{

	//public HashMap<String, String> variableParamMap;
	public ArrayList<Symbol> parameterList; 
	public ArrayList<Symbol> localList;
	public int reg_count;
	public int parameter_count;
	public int local_count;
	public int return_val;
	public String name;
	public Function(String name){
		this.name = name;
		parameter_count = 0;
		parameterList = new ArrayList<Symbol>();
		localList = new ArrayList<Symbol>();
		reg_count = 0;
	}

	public void addParameter(String name, String type){
		parameter_count++;
		parameterList.add(new Symbol(name, type, ""));
	}

	public void addParameterValues(){
		for (int i = 0; i < parameter_count; i++ ){
			parameterList.get(i).value = "$" + Integer.toString( 5 + parameter_count-i);
		}
		return_val = parameter_count+6;
	}

	public void addLocal(Symbol symbol){
		local_count++;
		symbol.value = "$L" + Integer.toString(local_count);
		localList.add(symbol);
	}

	public Symbol getLocal(String symbol_name){
		for (Symbol symbol: localList){
			if (symbol_name.equals(symbol.name)){
				return symbol;
			}
		}
		return null;
	}

	public String getReturnVal(){
		return "$" + Integer.toString(return_val);
	}

	public Symbol getParameter(String symbol_name){
		for (Symbol symbol: parameterList){
			if (symbol_name.equals(symbol.name)){
				return symbol;
			}
		}
		return null;
	}
	
	public int getNumberOfParameters(){
		return parameter_count;
	}
}