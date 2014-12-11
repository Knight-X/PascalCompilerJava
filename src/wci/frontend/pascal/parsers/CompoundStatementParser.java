package wci.frontend.pascal.parsers;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;

public class CompoundStatementParser extends StatementParser
{
  
  public CompoundStatementParser(PascalParserTD parent)
  {
    super(parent);
  }

  public ICodeNode parse(Token token)
    throws Exception
  {
    token = nextToken();

    ICodeNode compoundNode = ICodeFactory.createICodeNode(COMPOUND);

    StatementParser statementParser = new StatementParser(this);

    statementParser.parseList(token, compoundNode, END, MISSING_END);

    return compoundNode;
  }
}
