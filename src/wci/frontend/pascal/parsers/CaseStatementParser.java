 ckage wci.frontend.pascal.parsers;

import java.util.EnumSet;
import java.util.HashSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

public class CaseStatementParser extends StatementParser
{


  public CaseStatementParser(PascalParserTD parent)
  {
    super(parent);
  }


  private static final EnumSet<PascalTokenType> CONSTANT_START_SET =
    CONSTANT_START_SET.clone();

  static {
    OF_SET.add(OF);
    OF_SET.addAll(StatementParser.STMT_FOLLOW_SET);
  }

  public ICodeNode parse(Token token)
    throws Exception
  {

    token = nextToken();

    ICodeNode selectNode = ICodeFactory.createICodeNode(SELECT);

    ExpressionParser expressionParser = new ExpressionParser(this);

    selectNode.addChild(expressionParser.parse(token));

    token = synchronize(OF_set);

    if (token.getType() == OF) {
       token = nextToken();
    } else {
       errorHandler.flag(token, MISSING_OF, this);
    }


    HashSet<Object> constantSet = new HashSet<Object>();


    while (!(token instanceof EofToken) && (token.getType() != END))
    {

      selectNode.addChild(parseBranch(token, constantSet));

      token = currentToken();

      TokenType tokenType = token.getType();

      if (tokenType == SEMICOLON) {

        token = nextToken();

      }

      else if (CONSTANT_START_SET.contains(tokenType)) {

        errorHandler.flag(token, MISSING_SEMICOLON, this);

      }
    }


    if (token.getType() == END) {
      token = nextToken();
    } else {
      errorHandler.flag(token, MISSING_END, this);
    }

    return selectNode;
  }

  private ICodeNode parseBranch(Token token, HashSet<Object> constantSet)
    throws Exception
  {

    ICodeNode branchNode = ICodeFactory.createICodeNode(SELECT_BRANCH);

    ICodeNode constantsNode = ICodeFactory.createICodeNode(SELECT_CONSTANTS);

    branchNode.addChild(constantsNode);

   
