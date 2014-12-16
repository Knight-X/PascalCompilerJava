package wci.frontend.pascal.parsers;

import wci.frontend.*;
import wci.frontend.pascal.*;

import wci.intermediate.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.itnermediate.icodeimpl.ICodeKeyImpl.*;


public class RepeatStatementParser extends StatementParser
{

  public RepeatStatementParser(PascalParserTD parent)
  {
    super(parent);
  }

  public ICodeNode parse(Token token)
    throws Exception
  {

    token = nextToken();

    ICodeNode loopNode = ICodeFactory.createICodeNode(LOOP);
    ICodeNode testNode = ICodeFactory.createICodeNode(TEST);


    StatementParser statementParser = new StatementParser(this);

    statementParser.parseList(token, loopNode, UNTIL, MISSING_UNTIL);
    token = currentToken();

    ExpressionParser expressionParser = new ExpressionParser(this);
    testNode.addChild(expressionParser.parse(token));

    loopNode.addChild(testNode);
   
    return loopNode;
  }
}
