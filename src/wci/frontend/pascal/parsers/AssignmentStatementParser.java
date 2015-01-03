package wci.frontend.pascal.parsers;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

public class AssignmentStatementParser extends StatementParser
{

  public AssignmentStatementParser(PascalParserTD parent)
  {
    super(parent);
  }

  private static final EnumSet<PascalTokenType> COLON_EQUALS_SET =
      ExpressionParser.EXPR_START_SET.clone();

  static {
      COLON_EQUALS_SET.add(COLON_EQUALS);
      COLON_EQUALS_SET.addAll(StatementParser.STMT_FOLLOW_SET);
  }
  public ICodeNode parse(Token token)
    throws Exception
  {
    ICodeNode assignNode = ICodeFactory.createICodeNode(ASSIGN);

    String targetName = token.getText().toLowerCase();
    SymTabEntry targetId = symTabStack.lookup(targetName);

    if (targetId == null) {
      targetId = symTabStack.enterLocal(targetName);
    }

    targetId.appendLineNumber(token.getLineNumber());

    token = nextToken();

    ICodeNode variableNode = ICodeFactory.createICodeNode(VARIABLE);
    variableNode.setAttribute(ID, targetId);

    assignNode.addChild(variableNode);
    
    token = synchronize(COLON_EQUALS_SET);

    if (token.getType() == COLON_EQUALS) {
      token = nextToken();
    } else {
      errorHandler.flag(token, MISSING_COLON_EQUALS, this);
    }

    ExpressionParser expressionParser = new ExpressionParser(this);
    assignNode.addChild(expressionParser.parse(token));

    return assignNode;
  }
}

