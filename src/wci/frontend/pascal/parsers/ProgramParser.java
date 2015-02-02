package wci.frontend.pascal.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;


public class ProgramParser extends DeclarationParser
{
    public ProgramParser(PascalParserTD parent)
    {
        super(parent);
    }

    static final EnumSet<PascalTokenType> PROGRAM_START_SET =
        EnumSet.of(PROGRAM, SEMICOLON);
    static {
        PROGRAM_START_SET.addAll(DeclarationParser.DECLARATION_START_SET);
    }

    public SymTabEntry parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        token = synchronize(PROGRAM_START_SET);

        DeclarationRoutineParser routineParser = new DeclaredRoutineParser(this);
        routineParser.parse(token, parentId);

        token = currentToken();

        if (token.getType() != DOT) {
            errorHandler.flag(token, MISSING_PERIOD, this);
        }
        return null;
    }
}
