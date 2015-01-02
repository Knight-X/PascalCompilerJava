package wci.frontend.pascal.parsers;

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


class SimpleTypeParser extends TypeSpecificationParser
{
    protected SimpleTypeParser(PascalParserTD parent)
    {
        super(parent);
    }

    static final EnumSet<PascalTokenType> SIMPLE_TYPE_START_SET = 
        ConstantDefinitionParser.CONSTANT_START_SET.clone();

    static {
        SIMPLE_TYPE_START_SET.add(LEFT_PAREN);
        SIMPLE_TYPE_START_SET.add(COMMA);
        SIMPLE_TYPE_START_SET.add(SEMICOLON);
    }

    public TypeSpec parse(Token token)
        throws Exception
    {
        token = synchronize(SIMPLE_TYPE_START_SET);

        switch ((PascalTokenType) token.getType()) {
            case IDENTIFIER: {
                String name = token.getText().toLowerCase();

                SymTabEntry id = symTabStack.lookup(name);

                if (id != null) {
                    Definition definition = id.getDefinition();

                    if (definition == DefinitionImpl.TYPE) {
                        id.appendLineNumber(token.getLineNumber());

                        token = nextToken();

                        return id.getTypeSpec();
                    }
                    else if ((definition != CONSTANT) && 
                            ((definition != ENUMERATION_CONSTANT)) {
                        errorHandler.flag(token, NOT_TYPE_IDENTIFIER, this);
                        token = nextToken();
                        return null;
                    }
                    else {
                        SubrangeTypeParser subrangeTypeParser =
                        new SubrangeTypeParser(this);

                    return subrangeTypeParser.parse(token);
                    }
                }
                else {
                    errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
                    token = nextToken();
                    return null;
                }
            }
            case LEFT_PAREN: {
                EnumerationTypeParser enumerationTypeParser =
                    new EnumerationTypeParser(this);
                return enumerationTypeParser.parse(token);
            }

            case COMMA: 
            case SEMICOLON: {
                errorHandler.flag(token, INVALID_TYPE, this);
                return null;
            }
            default: {
                SubrangeTypeParser subrangeTypeParser =
                    new SubrangeTypeParser(this);
                return subrangeTypeParser.parse(token);
            }
        }
    }
}
