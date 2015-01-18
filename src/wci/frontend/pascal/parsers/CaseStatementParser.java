package wci.frontend.pascal.parsers;

import java.util.EnumSet;
import java.util.HashSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.ENUMERATION;

public class CaseStatementParser extends StatementParser
{


  public CaseStatementParser(PascalParserTD parent)
  {
    super(parent);
  }


  private static final EnumSet<PascalTokenType> CONSTANT_START_SET =
      EnumSet.of(IDENTIFIER, INTEGER, PLUS, MINUS, STRING);

  private static final EnumSet<PascalTokenType> OF_SET =
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
    ICodeNode exprNode = expressionParser.parse(token);
    selectNode.addChild(exprNode);

    TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                         : Predefined.undefinedType;

    if (!TypeChecker.isInteger(exprType) &&
        !TypeChecker.isChar(exprType) && 
        (exprType.getForm() != ENUMERATION))
    {
        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
    }



    token = synchronize(OF_SET);

    if (token.getType() == OF) {
       token = nextToken();
    } else {
       errorHandler.flag(token, MISSING_OF, this);
    }


    HashSet<Object> constantSet = new HashSet<Object>();


    while (!(token instanceof EofToken) && (token.getType() != END))
    {

      selectNode.addChild(parseBranch(token, exprType, constantSet));

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

  private ICodeNode parseBranch(Token token, TypeSpec expressionType, HashSet<Object> constantSet)
    throws Exception
  {

    ICodeNode branchNode = ICodeFactory.createICodeNode(SELECT_BRANCH);

    ICodeNode constantsNode = ICodeFactory.createICodeNode(SELECT_CONSTANTS);

    branchNode.addChild(constantsNode);

    parseConstantList(token, expressionType, constantsNode, constantSet);

    token = currentToken();

    if (token.getType() == COLON) {

      token = nextToken();
    } else {
      errorHandler.flag(token, MISSING_COLON, this);
    }

    StatementParser statementParser = new StatementParser(this);

    branchNode.addChild(statementParser.parse(token));

    return branchNode;
  }

  private static final EnumSet<PascalTokenType> COMMA_SET = 
    CONSTANT_START_SET.clone();

  static {
    COMMA_SET.add(COMMA);
    COMMA_SET.add(COLON);
    COMMA_SET.addAll(StatementParser.STMT_START_SET);
    COMMA_SET.addAll(StatementParser.STMT_FOLLOW_SET);
  }


  private void parseConstantList(Token token, TypeSpec expressionType, 
                                ICodeNode constantsNode,
                                HashSet<Object> constantSet)
    throws Exception
  {

    while (CONSTANT_START_SET.contains(token.getType())) {

      constantsNode.addChild(parseConstant(token, expressionType,
                                            constantSet));

      token = synchronize(COMMA_SET);

      if (token.getType() == COMMA) {

        token = nextToken();

      }

      else if (CONSTANT_START_SET.contains(token.getType())) {

        errorHandler.flag(token, MISSING_COMMA, this);

      }
     }
   }


  private ICodeNode parseConstant(Token token, TypeSpec expressionType,
                                HashSet<Object> constantSet)
    throws Exception
  {

    TokenType sign = null;

    ICodeNode constantNode = null;
    TypeSpec constantType = null;

    token = synchronize(CONSTANT_START_SET);

    TokenType tokenType = token.getType();

    if ((tokenType == PLUS) || (tokenType == MINUS)) {

      sign = tokenType; 
      token = nextToken();

    }

    switch ((PascalTokenType) token.getType()) {

      case IDENTIFIER: {

        constantNode = parseIdentifierConstant(token, sign);
        if (constantNode != null) {
            constantType = constantNode.getTypeSpec();
        }
        break;
      }

      case INTEGER: {
        constantNode = parseIntegerConstant(token.getText(), sign);
        constantType = Predefined.integerType;
        break;
      }

      case STRING: {
        constantNode = parseCharacterConstant(token, (String) token.getValue(), sign);
        constantType = Predefined.charType;
        break;
      }

      default: {

        errorHandler.flag(token, INVALID_CONSTANT, this);

         break;
      }

    }

    if (constantNode != null) {

      Object value = constantNode.getAttribute(VALUE);

      if (constantSet.contains(value)) {
         errorHandler.flag(token, CASE_CONSTANT_REUSED, this);
      }

      else {
        constantSet.add(value);
      }
    }

    if (!TypeChecker.areComparisonCompatible(expressionType,
                                            constantType)) {
        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
    }

    token = nextToken();
    constantNode.setTypeSpec(constantType);
    return constantNode;
  }

  private ICodeNode parseIdentifierConstant(Token token, TokenType sign)
    throws Exception
  {
    ICodeNode constantNode = null;
    TypeSpec constantType = null;

    String name = token.getText().toLowerCase();

    SymTabEntry id = symTabStack.lookup(name);

    if (id == null) {
        id = symTabStack.enterLocal(name);
        id.setDefinition(UNDEFINED);
        id.setTypeSpec(Predefined.undefinedType);
        errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);

        return null;
    }

    Definition defnCode = id.getDefinition();

    if ((defnCode == CONSTANT) || (defnCode == ENUMERATION_CONSTANT)) {
        Object constantValue = id.getAttribute(CONSTANT_VALUE);

        constantType = id.getTypeSpec();

        if ((sign != null) && !TypeChecker.isInteger(constantType)) {
            errorHandler.flag(token, INVALID_CONSTANT, this);
        }

        constantNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
        constantNode.setAttribute(VALUE, constantValue);
    }

    id.appendLineNumber(token.getLineNumber());
    if (constantNode != null) {
        constantNode.setTypeSpec(constantType);
    }

    return constantNode;
  }

  private ICodeNode parseIntegerConstant(String value, TokenType sign)
  {
    ICodeNode constantNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);

    int intValue = Integer.parseInt(value);

    if (sign == MINUS) {
      intValue = -intValue;
    }

    constantNode.setAttribute(VALUE, intValue);

    return constantNode;
  }

  private ICodeNode parseCharacterConstant(Token token, String value, TokenType sign)
  {
    ICodeNode constantNode = null;

    if (sign != null) {
      errorHandler.flag(token, INVALID_CONSTANT, this);
    }
    else {
      if (value.length() == 1) {

        constantNode = ICodeFactory.createICodeNode(STRING_CONSTANT);
        constantNode.setAttribute(VALUE, value);

      } else {
        errorHandler.flag(token, INVALID_CONSTANT, this);
      }
   }

   return constantNode;
  }
}
    

        
   
