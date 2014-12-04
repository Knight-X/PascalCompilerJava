package wci.frontend.pascal;

import wci.frontend.*;
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
    skipWhiteSpace();
    Token token;
    char currentChar = currentChar();

    if (currentChar == EOF) {
	token = new EofToken(source);
    } else if (Character.isLetter(currentChar)) {
        token = new PascalWordToken(source);
      }
    else if  (Character.isDigit(currentChar)){
      token = new PascalNumberToken(source);
    } else  if (currentChar == '\'') {
        token = new PascalStringToken(source);
    } else if (PascalTokenType.SPECIAL_SYMBOLS.containsKey(Character.toString(currentChar))) {
      token = new PascalSpecialSymbolToken(source);
    }
    else
      {
      token = new PascalErrorToken(source, INVALID_CHARACTER, Character.toString(currentChar));
      nextChar();
    }

    return token;
  }

  private void skipWhiteSpace()
      throws Exception
      {
        char currentChar = currentChar();

        while (Character.isWhitespace(currentChar) || (currentChar == '{')) {
          if (currentChar == '{'){
            do {
              currentChar = nextChar();
            } while ((currentChar != '}') && (currentChar != EOF));

            if (currentChar == '}') {
              currentChar = nextChar();
            }
          }
          else {
            currentChar = nextChar();
          }
        }
      }


}
