package wci.frontend.pascal.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;



public class ForStatementParser extends StatementParser
{

  public ForStatementParser(PascalParserTD parent)
  {
    super(parent);
  }

  private static final EnumSet<PascalTokenType> TO_DOWNTO_SET = 
    ExpressionParser.EXPR_START_SET.clone();

  static {
    TO_DOWNTO_SET.add(TO);
    TO_DOWNTO_SET.add(DOWNTO);
    TO_DOWNTO_SET.addAll(StatementParser.STMT_FOLLOW_SET);
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
    Token targetToken = token;

    ICodeNode compoundNode = ICodeFactory.createICodeNode(COMPOUND);
    ICodeNode loopNode = ICodeFactory.createICodeNode(LOOP);
    ICodeNode testNode = ICodeFactory.createICodeNode(TEST);

    AssignmentStatementParser assignmentParser = 
      new AssignmentStatementParser(this);

    ICodeNode initAssignNode = assignmentParser.parse(token);

    TypeSpec controlType = initAssignNode != null 
                            ? initAssignNode.getTypeSpec()
                            : Predefined.undefinedType;
    setLineNumber(initAssignNode, targetToken);
    
    if (!TypeChecker.isInteger(controlType) && 
         (controlType.getForm() != ENUMERATION)) 
    {
        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
    }
    compoundNode.addChild(initAssignNode);

    compoundNode.addChild(loopNode);

    token = synchronize(TO_DOWNTO_SET);

    TokenType direction = token.getType();

    if ((direction == TO) || (direction == DOWNTO)) {
      token = nextToken();
    } else {
      direction = TO;
      errorHandler.flag(token, MISSING_TO_DOWNTO, this);
    }

    ICodeNode relOpNode = ICodeFactory.createICodeNode(direction == TO ? GT : LT);

    relOpNode.setTypeSpec(Predefined.booleanType);

    ICodeNode controlVarNode = initAssignNode.getChildren().get(0);

    relOpNode.addChild(controlVarNode.copy());

    ExpressionParser expressionParser = new ExpressionParser(this);

    ICodeNOde exprNode = expressionParser.parse(token);
    relOpNode.addChild(exprNode);

    TypsSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                         : Predefined.undefinedType;

    if (!TypeChecker.areAssignmentCompatible(controlType, exprType)) {
        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
    }

    testNode.addChild(relOpNode);
    loopNode.addChild(testNode);

    token = synchronize(DO_SET);
    if (token.getType() == DO) {
      token = nextToken();
    } else {
      errorHandler.flag(token, MISSING_DO, this);
    }

    StatementParser statementParser = new StatementParser(this);
    loopNode.addChild(statementParser.parse(token));


    ICodeNode nextAssignNode = ICodeFactory.createICodeNode(ASSIGN);

    nextAssignNode.setTypeSpec(controlType);
    nextAssignNode.addChild(controlVarNode.copy());

    ICodeNode arithOpNode = ICodeFactory.createICodeNode(direction == TO ? ADD : SUBTRACT);
    
    arithOpNode.setTypeSpec(Predefined.integerType);
    arithOpNode.addChild(controlVarNode.copy());

    ICodeNode oneNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);

    oneNode.setAttribute(VALUE, 1);
    oneNode.setTypeSpec(Predefined.integerType);
     arithOpNode.addChild(oneNode);


    nextAssignNode.addChild(arithOpNode);

    loopNode.addChild(nextAssignNode);

    setLineNumber(nextAssignNode, targetToken);

    return compoundNode;
  }
}
