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

    VariableParser variableParser = new VariableParser(this);
    ICodeNode targetNode = variableParser.parse(token);
    TypeSpec targetType = targetNode != null ? targetNode.getTypeSpec()
                                             : Predefined.undefinedType;

    assignNode.addChild(targetNode);

    token = synchronize(COLON_EQUALS_SET);

    if (token.getType() == COLON_EQUALS) {
      token = nextToken();
    }
    else {
        errorHandler.flag(token, MISSING_COLON_EQUALS, this);
    }

    ExpressionParser expressionParser = new ExpressionParser(this);

    ICodeNode exprNode = expressionParser.parse(token);
    assignNode.addChild(exprNode);


    TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                         : Predefined.undefinedType;

    if (!TypeChecker.areAssignmentCompatible(targetType, exprType)) {
        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
    }

    assignNode.setTypeSpec(targetType);
    return assignNode;

  }
}

