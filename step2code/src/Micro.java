import org.antlr.v4.runtime.*;
//import org.antlr.v4.tree;
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

    try 
    {
      parser.program();
      System.out.println("Accepted");
    }
    catch (Exception e){
      System.out.println("Not Accepted");
    }


    
  } // end main

} // end class