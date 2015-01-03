package wci.frontend.pascal.parsers;

import java.util.EnumSet;
import java.util.ArrayList;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.RECORD;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;


class RecordTypeParser extends TypeSpecificationParser
{

    protected RecordTypeParser(PascalParserTD parent)
    {
        super(parent);
    }


    private static final EnumSet<PascalTokenType> END_SET =
        DeclarationsParser.VAR_START_SET.clone();

    static {
        END_SET.add(END);
        END_SET.add(SEMICOLON);
    }

    public TypeSpec parse(Token token)
        throws Exception
    {
        TypeSpec recordType = TypeFactory.createType(RECORD);

        token = nextToken();

        recordType.setAttribute(RECORD_SYMTAB, symTabStack.push());

        VariableDeclarationsParser variableDeclarationsParser =
            new VariableDeclarationsParser(this);

        variableDeclarationsParser.setDefinition(FIELD);
        variableDeclarationsParser.parse(token);

        symTabStack.pop();

        token = synchronize(END_SET);

        if (token.getType() == END) {
            token = nextToken();
        } else {
            errorHandler.flag(token, MISSING_END, this);
        }

        return recordType;
    }
}
