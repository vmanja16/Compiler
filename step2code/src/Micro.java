import org.antlr.v4.runtime.*;
import java.util.*;

public class Micro {

  public static void main(String[] args) throws Exception {
    
    ANTLRFileStream file_stream = new ANTLRFileStream(args[0]);

    MicroLexer lexer = new MicroLexer(file_stream);
   
    //String [] token_names = ((Lexer)lexer).getTokenNames();
 
    Token token = lexer.nextToken();

    Vocabulary vocab = lexer.getVocabulary();

    while (token.getType() != MicroLexer.EOF){
      System.out.println("Token Type: " + vocab.getSymbolicName(token.getType())); //+ token_names[token.getType()]);
      System.out.println("Value: " + token.getText() );   
      token = lexer.nextToken();
      }
 

  } // end main

} // end class