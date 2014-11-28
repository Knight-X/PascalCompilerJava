package wci.frontend;

import wci.frontend.pascal.PascalParseTD;
import wci.frontend.pascal.PascalScanner;

public class FrontendFactory
{

  public static Parser createParser(String language, String type, 
			 	Source source)
    throws Exception
  {
    if (language.equalsIgnoreCase("Pascal") &&
	type.equalsIgnoreCase("top-down"))
    {
      Scanner scanner = new PascalScanner(source);
      return new PascalParseTD(scanner);
    }
    else if (!language.equalsIgnoreCase("Pascal")) {
      throw new Exception("Parse factory: Invalid language '" + language + "'");
    }
    else {
      throw new Exception("Parse factory: Invalid type '" + type + "'");
    }
  }
}
