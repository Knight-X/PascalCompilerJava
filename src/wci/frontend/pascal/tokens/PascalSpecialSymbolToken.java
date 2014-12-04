package wci.frontend.pascal.tokens;

import wci.frontend.*;
import wci.frontend.pascal.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;


public class PascalSpecialSymbolToken extends PascalToken
{
  public PascalSpecialSymbolToken(Source source)
    throws Exception
  {
     super(source);
  }

  protected void extract()
    throws Exception
  {
    char currentChar = currentChar();

    text = Character.toString(currentChar);
    type = null;

    switch(currentChar) {
      
      case '+': case '-': case '*': case '/': case ',':
      case ';': case '\'': case '=': case '(': case ')':
      case '[': case ']': case '{': case '}': case '^': {

        nextChar();
        break;
      }

      case ':': {
        currentChar = nextChar();

        if (currentChar == '=') {
          text += currentChar;
          nextChar();
        }
      break;
     }

     case '<': {
        currentChar = nextChar();

        if (currentChar == '=') {
          text += currentChar;
          nextChar();
        }
       else if (currentChar == '>') {
          text += currentChar;
          nextChar();
       }

       break;
     }

     case '>':{
       currentChar = nextChar();

       if (currentChar == '=') {
         text += currentChar;
         nextChar();
       }
      break;
     }


     case '.': {
       currentChar = nextChar();
       
       if (currentChar == '.') {
         text += currentChar;
         nextChar();
       }

      break;
    }

    default: {
      nextChar();
      type = ERROR;
      value = INVALID_CHARACTER;
    }

  }

  if (type == null) {
     type = SPECIAL_SYMBOLS.get(text);
   }

  }
}
  
