package wci.frontend.pascal;

import wci.frontend.*;
import wci.frontend.Scanner;
import wci.frontend.Token;

import wci.frontend.pascal.tokens.*;

import static wci.frontend.Source.EOF;
import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;

public class PascalScanner extends Scanner
{
  public PascalScanner(Source source)
  {
    super(source);
  }

  protected Token extractToken()
    throws Exception
  {
    Token token;
    char currentChar = currentChar();

    if (currentChar == EOF) {
	token = new EofToken(source);
    } else if (Character.isLetter(currentChar)) {
        token = new PascalWordToken(source);
        
    }

    return token;
  }

}
