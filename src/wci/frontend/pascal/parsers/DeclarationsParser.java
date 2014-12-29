package wci.frontend.pascal.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;


public class DeclarationsParser extends PascalParserTD
{

    public DeclarationsParser(PascalParserTD parent)
    {
        super(parent);
    }

    static final EnumSet<PascalTokenType> DECLARATION_START_SET =
        EnumSet.of(CONST, TYPE, VAR, PROCEDURE, FUNCTION, BEGIN);

    static final EnumSet<PascalTokenType> TYPE_START_SET = 
        DECLARATION_START_SET.clone();

    static {
        TYPE_START_SET.remove(CONST);
    }

    static final EnumSet<PascalTokenType> VAR_START_SET =
        TYPE_START_SET.clone();

    static {
        VAR_START_SET.remove(TYPE);
    }

    static final EnumSet<PascalTokenType> ROUTINE_START_SET =
        VAR_START_SET.clone();
    static {
        ROUTINE_START_SET.remove(VAR);
    }

    public void parse(Token token)
        throws Exception
    {
        token = synchronize(DECLARATION_START_SET);

        if (token.getType() == CONST) {
            token = nextToken();

            ConstantDefinitionsParser constantDefinitionsParser =
                new ConstantDefinitionsParser(this);
            constantDefinitionsParser.parse(token);
        }

        token = synchronize(TYPE_START_SET);

        if (token.getType() == TYPE) {
            token = nextToken();

            TypeDefinitionsParser typeDefinitionsParser =
                new TypeDefinitionsParser(this);

            typeDefinitionsParser.parse(token);
        }

        token = synchronize(VAR_START_SET);

        if (token.getType() == VAR) {
            token = nextToken();

            VariableDeclarationsParser variableDeclarationsParser =
                new VariableDeclarationsParser(this);

            variableDeclarationsParser.setDefinition(VARIABLE);

            variableDelarationsParser.parse(token);
        }

        token = synchronize(ROUTINE_START_SET);
    }
}
