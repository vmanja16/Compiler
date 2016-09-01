import org.antlr.v4.runtime.*;

public class Micro {

  public static void main(String[] args) throws Exception {
    
    ANTLRFileStream file_stream = new ANTLRFileStream(args[0]);

    MicroLexer lexer = new MicroLexer(file_stream);

    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    
    tokenStream.fill();

    for ( Object token : tokenStream.getTokens()){
    	System.out.println(token.getType()); // doesn't work
    }

  }

} // end main