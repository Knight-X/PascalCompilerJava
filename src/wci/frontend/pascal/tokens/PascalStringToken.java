package wci.frontend.pascal.tokens;

import wci.frontend.*;
import wci.frontend.pascal.*;

import static wci.frontend.Source.EOL;
import static wci.frontend.Source.EOF;
import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;


public class PascalStringToken extends PascalToken
{
  public PascalStringToken(Source source)
    throws Exception
  {
    super(source);
  }

  protected void extract()
  { 

    StringBuilder textBuffer = new StringBuilder();
    StringBuilder valueBuffer = new StringBuilder();

    char currentChar = nextChar();
    textBuffer.append('\'');

    do {
      if (Character.isWhitespace(currentChar)) {
        currentChar = ' ';
       }
     
      if ((currentChar != '\'') && (currentChar != EOF)) {
        textBuffer.append(currentChar);
        valueBuffer.append(currentChar);
        currentChar = nextChar();
      }

      if (currentChar == '\'') {
        while ((currentChar == '\'') && (peekChar() == '\'')) {
          textBuffer.append("''");
          valueBuffer.append(currentChar);
          currentChar = nextChar();
          currentChar = nextChar();
        }
      }
    } while ((currenChar != '\'') && (currentChar != EOF));

    if (currentChar == '\'') {
      nextChar();
      textBuffer.append('\'');

      type = STRING;
      value = valueBuffer.toString();
    } else {
       type = ERROR;
       value = UNEXPECTED_EOF;
    }

    text = textBuffer.toString();
  }
}
