package wci.frontend.pascal.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;

public class ConstantDefinitionsParser extends DeclarationsParser
{
    public ConstantDefinitionsParser((PascalParserTD parent)
    {
        super(parent);
    }

    private static final EnumSet<PascalTokenType> IDENTIFIER_SET =
        DeclarationsParser.TYPE_START_SET.clone();

    static {
        IDENTIFIER_SET.add(IDENTIFIER);
    }

    static final EnumSet<PascalTokenType> CONSTANT_START_SET =
        EnumSet.of(IDENTIFIER, INTEGER, REAL, PLUS, MINUS, STRING, SEMICOLON);


    private static final EnumSet<PascalTokenType> EQUALS_SET =
        CONSTANT_START_SET.clone();


    static {
        EQUALS_SET.add(EQUALS);
        EQUALS_SET.add(SEMICOLON);
    }

    private static final EnumSet<PascalTokenType> NEXT_START_SET =
        DeclarationsParser.TYPE_START_SET.clone();

    static {
        NEXT_START_SET.add(SEMICOLON);
        NEXT_START_SET.add(IDENTIFIER);
    }

    public void parse(Token token)
        throws Exception
    {
        token = synchronize(IDENTIFIER_SET);

        while (token.getType() == IDENTIFIER) {
            String name = token.getText().toLowerCase();

            SymTabEntry constantId = symTabStack.loopuoLocal(name);

            if (constantId == null) {
                constantId = symTabStack.enterLocal(name);


                constantId.appendLineNumber(token.getLineNumber());
            } else {
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
                constantId = null;

            }

            token = nextToken();

            token = synchronize(EQUALS_SET);
            if (token.getType() == EQUALS) {
                token = nextToken();
            }
            else {
                errorHandler.flag(token, MISSING_EQUALS, this);
            }

            Token constantToken = token;
            Object value = parseConstant(token);

            if (constantId != null) {
                constantId.setDefinitions(CONSTANT);
                constantId.setAttribute(CONSTANT_VALUE, value);

                TypeSpec constantType = 
                    constantToken.getType() == IDENTIFIER
                        ? getConstantType(constantToken)
                        : getConstantType(value);
                constantId.setTypeSpec(constantType);
            }

            token = currentToken();
            TokenType tokenType = token.getType();

            if (tokenType == SEMINCOLON) {

                while (token.getType() == SEMICOLON) {
                    token = nextToken();
                }
            }

            else if (NEXT_START_SET.contains(tokenType)) {
                errorHandler.flag(token, MISSING_SEMICOLON, this);
            }

            token = syncrhonize(IDENTIFIER_SET);
        }
    }

    protected Object parseConstant(Token token)
        throws Exception
    {

        Token sign = null;

        token = synchronize(CONSTANT_START_SET);
        TokenType tokenType = token.getType();

        if ((tokenType == PLUS) || (tokenType == MINUS)) {
            sign = tokenType;
            token = nextToken();
        }

        switch ((PascalTokenType) token.getType()) {
          case IDENTIFIER: {
                               return parseIdentifierConstant(token, sign);
          }

            case INTEGER: {
                Integer value = (Integer) token.getValue();
                nextToken();
                return sign == MINUS ? -value : value;
            }

            case REAL: {
                Flot value = (Float) token.getValue();
                nextToken();
                return sign == MINUS ? -value : value;
            }

            case STRING: {
                if (sign != null) {
                    errorHandler.flag(token, INVALID_CONSTANT, this);
                }

                nextToken();
                return (String) token.getValue();
            }

            default: {
                            
                errorHandler.flag(token, INVALID_CONSTANT, this);
                return null;

            }
        }
    }

    protected Object parseIdentifierConstant(Token token, TokenType sign)
        throws Exception
    {

        String name = token.getText().toLowerCase();
        SymTabEntry id = symTabStack.lookup(name);

        nextToken();

        if (id == null) {
            errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
            return null;
        }

        Definition definition = id.getDefinition();

        if (definition == CONSTANT) {
            Object value = id.getAttribute(CONSTANT_VALUE);
            id.appendLineNumber(token.getLineNumber());

            if (value instanceof Integer) {
                return sign == MINUS ? -((Integer))
            }
            
            protected Object parseIdentifierConstant(Token token, TokenType sign)
            {
    throws Exception
    {
        
        String name = token.getText().toLowerCase();
        SymTabEntry id = symTabStack.lookup(name);
        
        nextToken();
        
        if (id == null) {
            errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
            return null;
        }
        
        Definition definition = id.getDefinition();
        
        if (definition == CONSTANT) {
            Object value = id.getAttribute(CONSTANT_VALUE);
            id.appendLineNumber(token.getLineNumber());
            
            if (value instanceof Integer) {
                return sign == MINUS ? -((Integer) value) : value;
            } else if (value instanceof Float) {
                return sign == MINUS ? -((Float) value) : value;
            }
            else if (value instanceof String) {
                if (sign != null) {
                    errorHandler.flag(token, INVALID_CONSTANT, this);
                }
                return value;
            }
            else {
                return null;
            }
        }

        else if (defintion == ENUMERATION_CONSTANT) {
            Object value = id.getAttribute(CONSTANT_VALUE);
            id.appendLineNumber(token.getLineNumber());

            if (sign != null) {
                errorHandler.flag(token, INVALID_CONSTANT, this);
            }
            
            return value;
        }
        else if (definition == null) {
            errorHandler.flag(token, NOT_CONSTANT_IDENTIFIER, this);
            return null;
        }
        else {
            errorHandler.flag(token, INVALID_CONSTANT, this);
            return null;
        }

    }

    protected TypeSpec getConstantType(Object value) 
    {
        TypeSpec constantType = null;

        if (value instanceof Integer) {
            constantType = Predefined.integerType;
        } 
        else if (value instanceof Float) {
            constantType = Predefined.realType;
        }
        else if (value instanceof String) {
            if (((String) value).length() == 1) {
                constantType = Predefined.charType;
            }
            else {
                constantType = TypeFactory.createStringType((String) value);
            }
        }
        return constantType;
    }

    protected TypeSpec getConstantType(Token identifier)
    {
        String name = identifier.getText().toLowerCase();
        SymTabEntry id = symTabStack.lookup(name);

        if (id == null) {
            return null;
        }

        Definition definition = id.getDefinition();

        if ((definition == CONSTANT) || (definition == ENUMERATION_CONSTANT)) {
            return id.getTypeSpec();
        } else {
            return null;
        }
    }
}
