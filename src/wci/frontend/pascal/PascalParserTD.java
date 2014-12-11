package wci.frontend.pascal;
import wci.frontend.*;
import wci.frontend.pascal.parsers.*;
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

  public PascalParserTD(PascalParserTD parent)
  {
    super(parent.getScanner());
  }

  public void parse() 
    throws Exception
  {
    
    long startTime = System.currentTimeMillis();
    iCode = ICodeFactory.createICode();

    try{
        Token token = nextToken();
        ICodeNode rootNode = null;

        if (token.getType() == BEGIN) {
            StatementParser statementParser = new StatementParser(this); 
            rootNode = statementParser.parse(token);
            token = currentToken();
        } else {
            errorHandler.flag(token, UNEXPECTED_TOKEN, this);
        }

            if (token.getType() != DOT) {
              errorHandler.flag(token, MISSING_PERIOD, this);
        }
          token = currentToken();
          
       if (rootNode != null){
           iCode.setRoot(rootNode);
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
      
    
