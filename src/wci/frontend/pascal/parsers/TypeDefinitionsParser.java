package wci.frontend.pascal.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.TYPE;
import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;


public class TypeDefinitionsParser extends DeclarationsParser
{
    public TypeDefinitionsParser(PascalParserTD parent)
    {
        super(parent);
    }

    private static final EnumSet<PascalTokenType> IDENTIFIER_SET =
        DeclarationsParser.VAR_START_SET.clone();

    static {
        IDENTIFIER_SET.add(IDENTIFIER);
    }

    private static final EnumSet<PascalTokenType> EQUALS_SET =
        ConstantDefinitionsParser.CONSTANT_START_SET.clone();

    static {
        EQUALS_SET.add(EQUALS);
        EQUALS_SET.add(SEMICOLON);
    }

    private static final EnumSet<PascalTokenType> FOLLOW_SET =
        EnumSet.of(SEMICOLON);

    private static final EnumSet<PascalTokenType> NEXT_START_SET =
        DeclarationsParser.VAR_START_SET.clone();

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

            SymTabEntry typeId = symTabStack.lookupLocal(name);

            if (typeId == null) {
                typeId = symTabStack.enterLocal(name);
                typeId.appendLineNumber(token.getLineNumber());
            }

            else {
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
                typeId = null;
            }

            token = nextToken();

            token = synchronize(EQUALS_SET);
            if (token.getType() == EQUALS) {
                token = nextToken();
            }
            else {
                errorHandler.flag(token, MISSING_EQUALS, this);
            }

            TypeSpecificationParser typeSpecificationParser =
                new TypeSpecificationParser(this);
            TypeSpec type = typeSpecificationParser.parse(token);

            if (typeId != null) {
                typeId.setDefinition(TYPE);
            }

            if ((type != null) && (typeId != null)) {
                if (type.getIdentifier() == null) {
                    type.setIdentifier(typeId);
                }
                typeId.setTypeSpec(type);
            }
            else {
                token = synchronize(FOLLOW_SET);
            }

            token = currentToken();
            TokenType tokenType = token.getType();

            if (tokenType == SEMICOLON) {
                while (token.getType() == SEMICOLON) {
                    token = nextToken();
                }
            }

            else if (NEXT_START_SET.contains(tokenType)) {
                errorHandler.flag(token, MISSING_SEMICOLON, this);
            }

            token = synchronize(IDENTIFIER_SET);
        }
    }
}
