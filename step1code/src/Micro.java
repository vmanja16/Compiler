import org.antlr.v4.runtime.*;
import java.util.*;

public class Micro {

  public static void main(String[] args) throws Exception {
    
    ANTLRFileStream file_stream = new ANTLRFileStream(args[0]);

    MicroLexer lexer = new MicroLexer(file_stream); 

    Vocabulary vocab = lexer.getVocabulary();

    CommonTokenStream tokens = new CommonTokenStream(lexer);
    tokens.fill(); 

    for (Token token : tokens.getTokens()){
      if (token.getType() != MicroLexer.EOF){
        System.out.println("Token Type: " + vocab.getSymbolicName(token.getType()));
        System.out.println("Value: " + token.getText() );   
      }
    }
 

  } // end main

} // end class