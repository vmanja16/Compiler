import org.antlr.v4.runtime.*;
import java.util.*;
import java.lang.*;


class MicroErrorStrategy extends DefaultErrorStrategy{

  public MicroErrorStrategy(){
    super();
  }
  @Override
  public void reportError(Parser recognizer, RecognitionException e){
	throw e;
  }
}