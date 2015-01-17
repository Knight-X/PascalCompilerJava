package wci.frontend.pascal.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.icodeimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.UNDEFINED;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;
import static wci.intermediate.symtabimpl.DefinitionImpl.VALUE_PARM;
import static wci.intermediate.symtabimpl.DefinitionImpl.VAR_PARM;
import static wci.intermediate.typeimpl.TypeFormImpl.ARRAY;
import static wci.intermediate.typeimpl.TypeFormImpl.RECORD;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;


public class VariableParser extends StatementParser
{
    public VariableParser(PascalParserTD parent)
    {
        super(parent);
    }

    private static final EnumSet<PascalTokenType> SUBSCRIPT_FIELD_START_SET = 
        EnumSet.of(LEFT_BRACKET, DOT);

    public ICodeNode parse(Token token)
        throws Exception
    {

        String name = token.getText().toLowerCase();
        SymTabEntry variableId = symTabStack.lookup(name);

        if (variableId == null) {
            errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
            variableId = symTabStack.enterLocal(name);
            variableId.setDefinition(UNDEFINED);
            variableId.setTypeSpec(Predefined.undefinedType);
        }

        return parse(token, variableId);
    }

    public ICodeNode parse(Token token, SymTabEntry variableId)
        throws Exception
    {
        Definition defnCode = variableId.getDefinition();

        if ((defnCode != VARIABLE) && (defnCode != VALUE_PARM) &&
                (defnCode != VAR_PARM))
        {
            errorHandler.flag(token, INVALID_IDENTIFIER_USAGE, this);
        }

        variableId.appendLineNumber(token.getLineNumber());

        ICodeNode variableNode =
            ICodeFactory.createICodeNode(ICodeNodeTypeImpl.VARIABLE);

        variableNode.setAttribute(ID, variableId);

        token = nextToken();


        TypeSpec variableType = variableId.getTypeSpec();
        while (SUBSCRIPT_FIELD_START_SET.contains(token.getType())) {
            ICodeNode subFldNode = token.getType() == LEFT_BRACKET
                                    ? parseSubscripts(variableType)
                                    : parseField(variableType);

            token = currentToken();

            variableType = subFldNode.getTypeSpec();
            variableNode.addChild(subFldNode);
        }

        variableNode.setTypeSpec(variableType);
        return variableNode;
    }

    private static final EnumSet<PascalTokenType> RIGHT_BRACKET_SET =
        EnumSet.of(RIGHT_BRACKET, EQUALS, SEMICOLON);


    private ICodeNode parseSubscripts(TypeSpec variableType)
        throws Exception
    {
        Token token;

        ExpressionParser expressionParser = new ExpressionParser(this);
        ICodeNode subscriptsNode = ICodeFactory.createICodeNode(SUBSCRIPTS);

        do {
            token = nextToken();

            if (variableType.getForm() == ARRAY) {
                ICodeNode exprNode = expressionParser.parse(token);

                TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                                     : Predefined.undefinedType;


                TypeSpec indexType = 
                    (TypeSpec) variableType.getAttribute(ARRAY_INDEX_TYPE);

                if (!TypeChecker.areAssignmentCompatible(indexType, exprType))                 {
                    errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                }

                subscriptsNode.addChild(exprNode);

                variableType = 
                    (TypeSpec) variableType.getAttribute(ARRAY_ELEMENT_TYPE);
            
            }

            else {
                errorHandler.flag(token, TOO_MANY_SUBSCRIPTS, this);
                expressionParser.parse(token);
            }

            token = currentToken();
        } while (token.getType() == COMMA);


        token = synchronize(RIGHT_BRACKET_SET);

        if (token.getType() == RIGHT_BRACKET) {
            token = nextToken();
        }
        else {
            errorHandler.flag(token, MISSING_RIGHT_BRACKET, this);
        }

        subscriptsNode.setTypeSpec(variableType);
        return subscriptsNode;
    }

    private ICodeNode parseField(TypeSpec variableType)
        throws Exception
    {

        ICodeNode fieldNode = ICodeFactory.createICodeNode(FIELD);

        Token token = nextToken();
        TokenType tokenType = token.getType();

        TypeForm variableForm = variableType.getForm();

        if ((tokenType == IDENTIFIER) && (variableForm == RECORD)) {
            SymTab symTab = (SymTab) variableType.getAttribute(RECORD_SYMTAB);
            String fieldName = token.getText().toLowerCase();

            SymTabEntry fieldId = symTab.lookup(fieldName);

            if (fieldId != null) {
                variableType = fieldId.getTypeSpec();

                fieldId.appendLineNumber(token.getLineNumber());


                fieldNode.setAttribute(ID, fieldId);
            }
            else {
                errorHandler.flag(token, INVALID_FIELD, this);
            }

        }
        else {
            errorHandler.flag(token, INVALID_FIELD, this);
        }

        token = nextToken();

        fieldNode.setTypeSpec(variableType);
        return fieldNode;
    }
}
