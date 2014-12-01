package wci.frontend.pascal.tokens;

import wci.frontend.*;
import wci.frontend.pascal.*;

import static wci.frontend.pascal.PascalTokenType.*;


public class PascalWordToken extends PascalToken
{
  public PascalWordToken(Source source)
    throws Exception
  {
    super(source);
  }

  protected void extract()
    throws Exception
  {
    StringBuilder textBuffer = new StringBuilder();
  
    char currentChar = currentChar();

    while (Character.isLetterOrDigit(currentChar)) {
      textBuffer.append(currentChar);
      currentChar = nextChar();
    }

    text = textBuffer.toString();


    type = (RESERVED_WORDS.contains(text.toLowerCase())) ? PascalTokenType.valueOf(text.toUpperCase()) : IDENTIFIER;
  }

}
    
