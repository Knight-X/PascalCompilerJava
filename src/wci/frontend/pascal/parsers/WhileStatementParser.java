package wci.frontend.pascal.parsers;

import java.util.EnumSet;


import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;


public class WhileStatementParser extends StatementParser
{

  public WhileStatementParser(PascalParserTD parent)
  {
    super(parent);
  }


  private static final EnumSet<PascalTokenType> DO_SET =
    StatementParser.STMT_START_SET.clone();

  static {
    DO_SET.add(DO);
    DO_SET.addAll(StatementParser.STMT_FOLLOW_SET);
  }

  public ICodeNode parse(Token token)
    throws Exception
  {
    token = nextToken();

    ICodeNode loopNode = ICodeFactory.createICodeNode(LOOP);
    ICodeNode breakNode = ICodeFactory.createICodeNode(TEST);
    ICodeNode notNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);

    loopNode.addChild(breakNode);
    breakNode.addChild(notNode);

    ExpressionParser expressionParser = new ExpressionParser(this);

    ICodeNode exprNode = expressionParser.parse(token);
    notNode.addChild(exprNode);

    TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                         : Predefined.undefinedType;

    if (!TypeChecker.isBoolean(exprType)) {
        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
    }

    token = synchronize(DO_SET);

    if (token.getType() == DO) {
      token = nextToken();
    } else {
      errorHandler.flag(token, MISSING_DO, this);
    }

    StatementParser statementParser = new StatementParser(this);
    loopNode.addChild(statementParser.parse(token));

    return loopNode;
  }
}
