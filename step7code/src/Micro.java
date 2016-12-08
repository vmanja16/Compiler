import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;

public class Micro {

  

  public static void main(String[] args) throws Exception {
  

     ArrayList<IRList> function_list = new ArrayList<IRList>();
    HashMap<String,Function> func_map = new HashMap<String,Function>();

     ArrayList<String> global_vars = new ArrayList<String>();


    ANTLRFileStream file_stream = new ANTLRFileStream(args[0]);

    MicroLexer lexer = new MicroLexer(file_stream); 

    CommonTokenStream tokens = new CommonTokenStream(lexer);
    
    MicroParser parser = new MicroParser(tokens);
    
    ANTLRErrorStrategy es = new MicroErrorStrategy();
    //parser.setErrorHandler(es);

  //  try 
   // {
      ParseTree parse_tree = parser.program();
//      parser.ir_list.print();


//  CREATE GLOBAL LIST
    IRList global_list = new IRList();
    int i = 0;
    IRNode node1 = parser.ir_list.get(i);
    while(!node1.opcode.equals("LABEL")){
      global_list.addLast(node1);
      if(node1.opcode.equals("var")){global_vars.add(node1.result);}
      node1 = parser.ir_list.get(++i);
    }

    // print out global IR
    for(Object node2 : global_list){
      ((IRNode)node2).print();
    }
    global_list.toTiny().print();


    // GET FUNCTION INFO from symbol Tree
    for (SymbolTable table : parser.tree.root.tables){
      func_map.put(table.scope_name, table.function);
    }

    // CREATE FUNCTION LISTS
    IRList temp_list = new IRList();
    for(Object node : parser.ir_list){
      if(((IRNode)node).start){temp_list = new IRList();}
      temp_list.addLast(node);
      if(((IRNode)node).end){function_list.add(temp_list);}
    }


    // PASS FUNCTION LISTS TO GRAPHER!
    Grapher grapher;
    for(IRList list : function_list){
      System.out.println(";START FUNCTION");
      
      grapher = new Grapher(list, global_vars, func_map.get(list.get(0).result));
      grapher.calculateGen();
      grapher.calculateKill();
      grapher.calculateSuccessors();
      grapher.calculatePredecessors(); // pred dependent on succ
      grapher.calculateLiveness();
     // list.print();
      grapher.allocateRegisters();
      System.out.println(";END FUNCTION\n");
    }

/*
    // print Function IR
    for(IRList list : function_list){
      System.out.println(";START FUNCTION");
      list.print();
      System.out.println(";END FUNCTION\n");
    }

*/

      //TinyList tiny_list = (TinyList)parser.ir_list.toTiny();
      //tiny_list.print();
      //System.out.println("sys halt");
  /*    
    }
    catch (Exception e){
      System.out.println(e);
    }
*/
    
  } // end main

} // end class