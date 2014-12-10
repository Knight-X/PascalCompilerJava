package wci.frontend.pascal.parsers;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

public class StatementParser extends PascalParserTD
{
  public StatementParser(PascalParserTD parent)
  {
    super(parent);
  }

  public ICodeNode parse(Token token)
  {
    ICodeNode statementNode = null;

    switch ((PascalTokenType) token.getType()) {

      case BEGIN: {
        CompoundStatementParser compoundParser = new CompoundStatementParser(this);
        statementNode = compoundParser.parse(token);
        break;
      }

      case IDENTIFIER: {
       AssignmentStatementParser assignmentParser = new AssignmentStatementParser(this);
       statementNode = assignmentParser.parse(token);
       break;
      }

      default:
        statementNode = ICodeFactory.createICodeNode(NO_OP);
        break;
      }
   

    setLineNumber(statementNode, token);

   return statementNode;
  }

  protected void setLineNumber(ICodeNode node, Token token)
  {
    if (node != null) {
      node.setAttribute(LINE, token.getLineNumber9);
    }
  }

  protected void parseList(Token token, ICodeNode parentNode, 
                           PascalTokenType terminator, 
                           PascalErrorCode errorCode)
    throws Exception 
  {
    while (!(token instanceof EofToken) && (token.getType() != terminator)) {
      ICodeNode statementNode = parse(token);
      parentNode.addChild(statementNode);
    
      token = currentToken();
      TokentType tokentype = token.getType();

      if (tokenType == SEMICOLON) {
        token = nextToken();
      }

      else if (tokenType == IDENTIFIER) {
        errorHandler.flag(token, MISSING_SEMICOLON, this);
      }

      else if (tokenType != terminator) {
        errorHanlder.flag(token, UNEXPECTED_TOKEN, this);
        token = nextToken();
      }

   }

   if (token.getType() == terminator) {
       token = nextToken();
   } else {
      errorHanlder.flag(token, errorCode, this);
   }
 }
}
