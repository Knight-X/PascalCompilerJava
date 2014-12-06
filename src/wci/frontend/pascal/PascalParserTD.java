package wci.frontend.pascal;
import wci.frontend.*;
import wci.message.*;
import wci.intermediate.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.message.MessageType.PARSER_SUMMARY;

public class PascalParserTD extends Parser
{
  protected static PascalErrorHandler errorHandler = new PascalErrorHandler();

  public PascalParserTD(Scanner scanner)
  {
    super(scanner);
  }

  public void parse() 
    throws Exception
  {
    Token token;
    long startTime = System.currentTimeMillis();

    try{
      while (!((token = nextToken()) instanceof EofToken)) {
        TokenType tokenType = token.getType();

        if (tokenType == IDENTIFIER) {
            String name = token.getText().toLowerCase();

            SymTabEntry entry = symTabStack.lookup(name);

            if (entry == null) {
              entry = symTabStack.enterLocal(name);
            }

            entry.appendLineNumber(token.getLineNumber());
        } else if (tokenType == ERROR){
           errorHandler.flag(token, (PascalErrorCode)token.getValue(), this);
        }

        
      }

     float elapsedTime = (System.currentTimeMillis() - startTime ) / 1000f;

     sendMessage(new Message(PARSER_SUMMARY,
			new Number[] {token.getLineNumber(),
			              getErrorCount(),
                                      elapsedTime}));
      }
     catch (java.io.IOException ex) {
       errorHandler.abortTranslation(IO_ERROR, this);
     }
    }


    public int getErrorCount()
    {
      return errorHandler.getErrorCount();
    }

}
      
    
