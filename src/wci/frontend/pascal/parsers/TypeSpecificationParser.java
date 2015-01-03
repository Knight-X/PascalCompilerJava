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
import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;


class TypeSpecificationParser extends PascalParserTD
{

    protected TypeSpecificationParser(PascalParserTD parent)
    {
        super(parent);
    }

    static final EnumSet<PascalTokenType> TYPE_START_SET = 
        SimpleTypeParser.SIMPLE_TYPE_START_SET.clone();
    static {
        TYPE_START_SET.add(PascalTokenType.ARRAY);
        TYPE_START_SET.add(PascalTokenType.RECORD);
        TYPE_START_SET.add(SEMICOLON);
    }

    public TypeSpec parse(Token token)
        throws Exception
    {
        token = synchronize(TYPE_START_SET);

        switch ((PascalTokenType) token.getType()) {
            case ARRAY: {
                    ArrayTypeParser arrayTypeParser = new ArrayTypeParser(this);
                    return arrayTypeParser.parse(token);
            }

            case RECORD: {
                             RecordTypeParser recordTypeParser = new RecordTypeParser(this);
                             return recordTypeParser.parse(token);
            }

            default: {
                         SimpleTypeParser simpleTypeParser = new SimpleTypeParser(this);
                         return simpleTypeParser.parse(token);
            }
        }

    }
}
