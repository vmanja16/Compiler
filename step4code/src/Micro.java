import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;

public class Micro {

  public static void main(String[] args) throws Exception {
    
    ANTLRFileStream file_stream = new ANTLRFileStream(args[0]);

    MicroLexer lexer = new MicroLexer(file_stream); 

    CommonTokenStream tokens = new CommonTokenStream(lexer);
    
    MicroParser parser = new MicroParser(tokens);
    
    ANTLRErrorStrategy es = new MicroErrorStrategy();
    parser.setErrorHandler(es);

  //  try 
   // {
      ParseTree parse_tree = parser.program();
      SymbolTableTree symbol_table_tree = parser.tree;

      //parser.ir_list.print();

      ParseTreeWalker parse_tree_waklker = new ParseTreeWalker();
      MicroRuleListener listener = new MicroRuleListener(symbol_table_tree);
      
      parse_tree_waklker.walk(listener, parse_tree);
  /*    
    }
    catch (Exception e){
      System.out.println(e);
    }
*/
    
  } // end main

} // end class