import org.antlr.v4.runtime.*;
import java.util.*;
import java.lang.*;


class MicroErrorStrategy extends DefaultErrorStrategy{

  public MicroErrorStrategy(){}
  
  @Override
  public void reportError(Parser recognizer, RecognitionException e){
  	System.out.println("Not Accepted" + e);
  }
  
}