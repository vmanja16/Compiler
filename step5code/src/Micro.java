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
      parser.ir_list.print();
      TinyList tiny_list = (TinyList)parser.ir_list.toTiny();
      tiny_list.print();
      System.out.println("sys halt");
  /*    
    }
    catch (Exception e){
      System.out.println(e);
    }
*/
    
  } // end main

} // end class