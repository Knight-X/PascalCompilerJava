package wci.frontend.pascal.parsers;

import java.util.EnumSet;
import java.util.ArrayList;

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


public class EnumerationTypeParser extends TypeSpecificationParser
{
    protected EnumerationTypeParser(PascalParserTD parent)
    {
        super(parent);
    }

    private static final EnumSet<PascalTokenType> ENUM_CONSTANT_START_SET =
        EnumSet.of(IDENTIFIER, COMMA);

    private static final EnumSet<PascalTokenType> ENUM_DEFINITION_FOLLOW_SET =
        EnumSet.of(RIGHT_PAREN, SEMICOLON);

    static {
        ENUM_DEFINITION_FOLLOW_SET.addAll(DeclarationsParser.VAR_START_SET);
    }

    public TypeSpec parse(Token token)
         throws Exception
     {
         TypeSpec enumerationType = TypeFactory.createType(ENUMERATION);

         int value = -1;

         ArrayList<SymTabEntry> constants = new ArrayList<SymTabEntry>();

         token = nextToken();

         do {
             token = synchronize(ENUM_CONSTANT_START_SET);

             parseEnumerationIdentifier(token, ++value, enumerationType,
                     constants);

             token = currentToken();
             TokenType tokenType = token.getType();

             if (tokenType == COMMA) {
                 token = nextToken();

                 if (ENUM_DEFINITION_FLOOW_SET.contains(token.getType())) {
                     errorHandler.flag(token, MISSING_IDENTIFIER, this);
                 }
             }
             else if (ENUM_CONSTANT_START_SET.contains(tokenType)) {
                 errorHandler.flag(token, MISSING_COMMA, this);
             }
         }while (!NUM_DEFINITION_FOLLOW_SET.contains(token.getType()));

         if (token.getType() == RIGHT_PAREN) {
             token = nextToken();
         }
         else {
             errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
         }

         enumerationType.setAttribute(ENUMERATION_CONSTANTS, constants);
         return enumerationType;
     }

    private void parseEnumerationIdentifier(Token token, int value,
                                            TypeSpec enumerationType,
                                            ArrayList<SymTabEntry> constants)
        throws Exception
    {
        TokenType tokenType = token.getType();

        if (tokenType == IDENTIFIER) {
            String name = token.getText().toLowerCase();

            SymTabEntry constantId = symTabStack.lookupLocal(name);

            if (constantId != null) {
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
            }
            else {
                constantId = symTabStack.enterLocal(name);
                constantId.setDefinition(ENUMERATION_CONSTANT);
                constantId.setTypeSpec(enmerationType);
                constantId.setAttribute(CONSTANT_VALUE, value);
                constantId.appendLineNumber(token.getLineNumber());
                constants.add(constantId);
            }

            token = nextToken();
        }
        else {
            errorHandler.flag(token, MISSING_IDENTIFIER, this);
        }
    }
}
