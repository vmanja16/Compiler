import java.util.*;
import java.lang.*;

class Function{

	//public HashMap<String, String> variableParamMap;
	public ArrayList<Symbol> parameterList; 
	public ArrayList<Symbol> localList;
	public int reg_count;
	public int parameter_count;
	public Function(){
		parameter_count = 0;
		parameterList = new ArrayList<Symbol>();
		localList = new ArrayList<Symbol>();
		reg_count = 0;
	//	variableParamMap = new HashMap<String,String>();
	}

	/*public void addParameter(String name, String type){
		parameter_count++;
		variableParamMap.put(name, "")
	}*/

	public void addParameter(String name, String type){
		parameter_count++;
		parameterList.add(new Symbol(name, type, ""));
	}

	public void addParameterValues(){
		for (int i = 0; i < parameter_count; i++ ){
			parameterList.get(i).value = "$" + Integer.toString( 5 + parameter_count-i);
		}
	}


	public Symbol getParameter(String symbol_name){
		for (Symbol symbol: parameterList){
			if (symbol_name.equals(symbol.name)){
				return symbol;
			}
		}
		return null;
	}

	/*public IRList pushParameterList(){
		ir_list = new IRList();
		for (int i = 0; i < parameter_count; i++){
			ir_list.add(getPushNode("$"))
		}
	}*/



}