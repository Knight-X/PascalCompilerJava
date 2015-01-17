package wci.frontend.pascal.parsers;

import java.util.EnumSet;
import java.util.HashMap;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalTokenType.NOT;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

public class ExpressionParser extends StatementParser
{
    public ExpressionParser(PascalParserTD parent)
    {
      super(parent);
    }

    static final EnumSet<PascalTokenType> EXPR_START_SET =
        EnumSet.of(PLUS, MINUS, IDENTIFIER, INTEGER, REAL, STRING,
                PascalTokenType.NOT, LEFT_PAREN);
    public ICodeNode parse(Token token)
      throws Exception
    {
      return parseExpression(token);
    }

    private static final EnumSet<PascalTokenType> REL_OPS = EnumSet.of(EQUALS, NOT_EQUALS, LESS_THAN, LESS_EQUALS, GREATER_THAN, GREATER_EQUALS);

    private static final HashMap<PascalTokenType, ICodeNodeType> REL_OPS_MAP = new HashMap<PascalTokenType, ICodeNodeType>();

    static{
      REL_OPS_MAP.put(EQUALS, EQ);
      REL_OPS_MAP.put(NOT_EQUALS, NE);
      REL_OPS_MAP.put(LESS_THAN, LT);
      REL_OPS_MAP.put(LESS_EQUALS, LE);
      REL_OPS_MAP.put(GREATER_THAN, GT);
      REL_OPS_MAP.put(GREATER_EQUALS, GE);
    };

    private ICodeNode parseExpression(Token token)
      throws Exception
    {
      ICodeNode rootNode = parseSimpleExpression(token);

      TypeSpec resultType = rootNode != null ? rootNode.getTypeSpec()
                                             : Predefined.undefinedType;

      token = currentToken();
      TokenType tokenType = token.getType();

      if (REL_OPS.contains(tokenType)) {
        ICodeNodeType nodeType = REL_OPS_MAP.get(tokenType);

        ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);

        opNode.addChild(rootNode);
        token = nextToken();

        ICodeNode simExprNode = parseSimpleExpression(token);

        opNode.addChild(simExprNode);

        rootNode = opNode;

        TypeSpec simExprType = simExprNode != null
                                   ? simExprNode.getTypeSpec()
                                   : Predefined.undefinedType;
        if (TypeChecker.areComparisonCompatible(resultType, simExprType)) {
            resultType = Predefined.booleanType;
        }else {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
            resultType = Predefined.undefinedType;
        }

       }

      if (rootNode != null) {
          rootNode.setTypeSpec(resultType);
      }
      return rootNode;
     }

     private static final EnumSet<PascalTokenType> ADD_OPS = EnumSet.of(PLUS, MINUS, PascalTokenType.OR);

     private static final HashMap<PascalTokenType, ICodeNodeTypeImpl> ADD_OPS_OPS_MAP =
       new HashMap<PascalTokenType, ICodeNodeTypeImpl>();

     static {
       ADD_OPS_OPS_MAP.put(PLUS, ADD);
       ADD_OPS_OPS_MAP.put(MINUS, SUBTRACT);
       ADD_OPS_OPS_MAP.put(PascalTokenType.OR, ICodeNodeTypeImpl.OR);
     };

     private ICodeNode parseSimpleExpression(Token token)
       throws Exception
     {
       Token signToken = null;
       TokenType signType = null;

       TokenType tokenType = token.getType();

       if ((tokenType == PLUS) || (tokenType == MINUS)) {
         signType = tokenType;
         signToken = token;
         token = nextToken();
       }

       ICodeNode rootNode = parseTerm(token);
       TypeSpec resultType = rootNode != null ? rootNode.getTypeSpec()
                                              : Predefined.undefinedType;

       if ((signType != null) && (!TypeChecker.isIntegerOrReal(resultType))) {
           errorHandler.flag(signToken, INCOMPATIBLE_TYPES, this);
       }

       if (signType == MINUS) {

         ICodeNode negateNode = ICodeFactory.createICodeNode(NEGATE);
         negateNode.addChild(rootNode);
         negateNode.setTypeSpec(rootNode.getTypeSpec());
         rootNode = negateNode;
       }

       token = currentToken();
       tokenType = token.getType();

       while (ADD_OPS.contains(tokenType)) {
        TokenType operator = tokenType;

        ICodeNodeType nodeType = ADD_OPS_OPS_MAP.get(tokenType);
        ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
        opNode.addChild(rootNode);

        token = nextToken();

        ICodeNode termNode = parseTerm(token);

        opNode.addChild(termNode);
        TypeSpec termType = termNode != null ? termNode.getTypeSpec()
                                             : Predefined.undefinedType;

        rootNode = opNode;

        switch ((PascalTokenType) operator) {
            case PLUS:
            case MINUS: {
                    if (TypeChecker.areBothInteger(resultType, termType)) {
                        resultType = Predefined.integerType;
                    }
                    else if (TypeChecker.isAtLeastOneReal(resultType,
                                                          termType)) {
                        resultType = Predefined.realType;
                    }
                    else {
                        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                    }

                    break;
              }

            case OR: {
                    if (TypeChecker.areBothBoolean(resultType, termType)) {
                        resultType = Predefined.booleanType;
                    }
                    else {
                        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                    }

                    break;
            }
        }


        rootNode.setTypeSpec(resultType);
        token = currentToken();

        tokenType = token.getType();

        }

      return rootNode;
   }


   private static final EnumSet<PascalTokenType> MULT_OPS = EnumSet.of(STAR, SLASH, DIV, PascalTokenType.MOD, PascalTokenType.AND);

   private static final HashMap<PascalTokenType, ICodeNodeType> MULT_OPS_OPS_MAP = new HashMap<PascalTokenType, ICodeNodeType>();

   static {
     MULT_OPS_OPS_MAP.put(STAR, MULTIPLY);
     MULT_OPS_OPS_MAP.put(SLASH, FLOAT_DIVIDE);
     MULT_OPS_OPS_MAP.put(DIV, INTEGER_DIVIDE);
     MULT_OPS_OPS_MAP.put(PascalTokenType.MOD, ICodeNodeTypeImpl.MOD);
     MULT_OPS_OPS_MAP.put(PascalTokenType.AND, ICodeNodeTypeImpl.AND);
   };

   private ICodeNode parseTerm(Token token)
     throws Exception
   {
     ICodeNode rootNode = parseFactor(token);

     TypeSpec resultType = rootNode != null ? rootNode.getTypeSpec()
                                            : Predefined.undefinedType;

     token = currentToken();

     TokenType tokenType = token.getType();

     while (MULT_OPS.contains(tokenType)) {

       TokenType operator = tokenType;

       ICodeNodeType nodeType = MULT_OPS_OPS_MAP.get(tokenType);

       ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
       opNode.addChild(rootNode);

       token = nextToken();

       ICodeNode factorNode = parseFactor(token);

       opNode.addChild(factorNode);

       TypeSpec factorType = factorNode != null ? factorNode.getTypeSpec()
                                                : Predefined.undefinedType;


       rootNode = opNode;

       switch ((PascalTokenType) operator) {

           case STAR: {
                if (TypeChecker.areBothInteger(resultType, factorType)) {
                    resultType = Predefined.integerType;
                }

                else if (TypeChecker.isAtLeastOneReal(resultType,
                                                      factorType)) {
                    resultType = Predefined.realType;
                }
                else {
                    errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                }

                break;
           }

           case SLASH: {

                if (TypeChecker.areBothInteger(resultType, factorType) ||
                    TypeChecker.isAtLeastOneReal(resultType, factorType))
                {
                    resultType = Predefined.realType;
                }
                else {
                    errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                }

                break;
           }

           case DIV:
           case MOD: {
                if (TypeChecker.areBothInteger(resultType, factorType)) {
                    resultType = Predefined.integerType;
                }
                else {
                    errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                }

                break;
           }

           case AND: {

                if (TypeChecker.areBothBoolean(resultType, factorType)) {

                    resultType = Predefined.booleanType;
                }

                else {
                    errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                }
                break;
           }

       }

       rootNode.setTypeSpec(resultType);
       
       token = currentToken();
       tokenType = token.getType();
     }

    return rootNode;

  }


  private ICodeNode parseFactor(Token token)
    throws Exception
  {

    TokenType tokenType = token.getType();

    ICodeNode rootNode = null;

    switch ((PascalTokenType) tokenType) {
      case IDENTIFIER: {

            return parseIdentifier(token);
      }

       case INTEGER: {
         rootNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);

         rootNode.setAttribute(VALUE, token.getValue());
         token = nextToken();

         rootNode.setTypeSpec(Predefined.integerType);
         break;
       }

       case REAL: {
         rootNode = ICodeFactory.createICodeNode(REAL_CONSTANT);
         rootNode.setAttribute(VALUE, token.getValue());
         token = nextToken();

         rootNode.setTypeSpec(Predefined.realType);
         break;
       }
       
       case STRING: {
         String value = (String) token.getValue();
         
         rootNode = ICodeFactory.createICodeNode(STRING_CONSTANT);
         rootNode.setAttribute(VALUE, value);

         TypeSpec resultType = value.length() == 1
                                    ? Predefined.charType
                                    : TypeFactory.createStringType(value);



         token = nextToken();
         rootNode.setTypeSpec(resultType);
         break;
       }

       case NOT: {
         token = nextToken();
    
         rootNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);

         ICodeNode factorNode = parseFactor(token);
         rootNode.addChild(factorNode);
         break;
       }

       case LEFT_PAREN: {
         token = nextToken();

         rootNode = parseExpression(token);

         token = currentToken();

         if (token.getType() == RIGHT_PAREN) {
            token = nextToken();
         } else {
            errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
         }

         break;
       }

       default: {
         errorHandler.flag(token, UNEXPECTED_TOKEN, this);
         break;
       }

     }
    return rootNode;
   }


  private ICodeNode parseIdentifier(Token token)
      throws Exception
  {
      ICodeNode rootNode = null;

      String name = token.getText().toLowerCase();
      SymTabEntry id = symTabStack.lookup(name);

      if (id == null) {

          errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
          id = symTabStack.enterLocal(name);
          id.setDefinition(UNDEFINED);
          id.setTypeSpec(Predefined.undefinedType);

      }

      Definition defnCode = id.getDefinition();


      switch ((DefinitionImpl) defnCode) {

          case CONSTANT: {
                Object value = id.getAttribute(CONSTANT_VALUE);
                TypeSpec type = id.getTypeSpec();

                if (value instanceof Integer) {
                    rootNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
                    rootNode.setAttribute(VALUE, value);
                }
                else if (value instanceof Float) {
                    rootNode = ICodeFactory.createICodeNode(REAL_CONSTANT);
                    rootNode.setAttribute(VALUE, value);
                }
                else if (value instanceof String) {
                    rootNode = ICodeFactory.createICodeNode(STRING_CONSTANT);
                    rootNode.setAttribute(VALUE, value);
                }

                id.appendLineNumber(token.getLineNumber());

                token = nextToken();

                if (rootNode != null) {
                    rootNode.setTypeSpec(type);
                }
                break;
          }
          case ENUMERATION_CONSTANT: {
                Object value = id.getAttribute(CONSTANT_VALUE);
                TypeSpec type = id.getTypeSpec();

                rootNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);

                rootNode.setAttribute(VALUE, value);

                id.appendLineNumber(token.getLineNumber());

                token = nextToken();

                rootNode.setTypeSpec(type);

                break;
          }

          default: {
                VariableParser variableParser = new VariableParser(this);

                rootNode = variableParser.parse(token, id);
                break;
          }
      }
      return rootNode;
  }

}

       
        
  
     
     
    

