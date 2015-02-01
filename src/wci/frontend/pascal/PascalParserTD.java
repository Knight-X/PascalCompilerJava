package wci.frontend.pascal;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.parsers.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;

import wci.message.*;


import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.message.MessageType.PARSER_SUMMARY;

public class PascalParserTD extends Parser
{
  protected static PascalErrorHandler errorHandler = new PascalErrorHandler();

  private SymTabEntry routineId;

  public PascalParserTD(Scanner scanner)
  {
    super(scanner);
  }

  public PascalParserTD(PascalParserTD parent)
  {
    super(parent.getScanner());
  }
   public PascalErrorHandler getErrorHandler()
    {
       return errorHandler;
    }

   public SymTabEntry getRountineId()
   {
       return routineId;
   }

  public void parse() 
    throws Exception
  {
    
    long startTime = System.currentTimeMillis();


    Predefined.initialize(symTabStack);
    
 
    try{
        Token token = nextToken();
        
        ProgramParser programParser = new ProgramParser(this);

        programParser.parse(token, null);
        token = currentToken();


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

    public Token synchronize(EnumSet syncSet)
      throws Exception
    {

      Token token = currentToken();

      if (!syncSet.contains(token.getType())) {
          
          errorHandler.flag(token, UNEXPECTED_TOKEN, this);

          do {
              token = nextToken();
          } while (!(token instanceof EofToken) &&
                   !syncSet.contains(token.getType()));
      }

      return token;
    }


}
      
    
