package wci.frontend.pascal;
import wci.frontend.*;
import wci.message.Message;
import wci.frontend.Parser;

import static wci.message.MessageType.PARSER_SUMMARY;

public class PascalParserTD extends Parser
{
  public PascalParserTD(Scanner scanner)
  {
    super(scanner);
  }

  public void parse() 
    throws Exception
  {
    Token token;
    long startTime = System.currentTimeMillis();

    while (!((token = nextToken()) instanceof EofToken)) {}

     float elapsedTime = (System.currentTimeMillis() - startTime ) / 1000f;

     sendMessage(new Message(PARSER_SUMMARY,
			new Number[] {token.getLineNumber(),
			              getErrorCount(),
                                      elapsedTime}));
    }


    public int getErrorCount()
    {
      return 0;
    }

}
      
    
