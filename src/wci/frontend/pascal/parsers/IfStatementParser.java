package wci.frontend.pascal.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;



public class IfStatementParser extends StatementParser
{
  
  public IfStatementParser(PascalParserTD parent)
  {
    super(parent);
  }

  private static final EnumSet<PascalTokenType> THEN_SET = 
    StatementParser.STMT_START_SET.clone();

  static {
    THEN_SET.add(THEN);
    THEN_SET.addAll(StatementParser.STMT_FOLLOW_SET);
  }

  public ICodeNode parse(Token token)
    throws Exception
  {
    token = nextToken();

    ICodeNode ifNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);

    ExpressionParser expressionParser = new ExpressionParser(this);
    ICodeNode exprNode = expressionParser.parse(token);
    ifNode.addChild(exprNode);

    TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                         : Predefined.undefinedType;

    if (!TypeChecker.isBoolean(exprType)) {
        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
    }

    token = synchronize(THEN_SET);

    if (token.getType() == THEN) {
      token = nextToken();
    }
    else {
      errorHandler.flag(token, MISSING_THEN, this);
    }

    StatementParser statementParser = new StatementParser(this);
    ifNode.addChild(statementParser.parse(token));
    token = currentToken();

    if (token.getType() == ELSE) {
      token = nextToken();

      ifNode.addChild(statementParser.parse(token));

    }
    return ifNode;
  }
}

   
