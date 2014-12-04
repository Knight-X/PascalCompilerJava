package wci.frontend.pascal.tokens;

import wci.frontend.*;
import wci.frontend.pascal.*;


import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;

public class PascalErrorToken extends PascalToken
{
  public PascalErrorToken(Source source, PascalErrorCode errorCode,
			String tokenText)
    throws Exception
  {
    super(source);

    this.text = tokenText;
    this.type = ERROR;
    this.value = errorCode;
  }

  protected void extract()
    throws Exception
  {
  }
}
