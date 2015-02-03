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
import static wci.intermediate.symtabimpl.RoutineCodeImpl.*;



public class DeclaredRoutineParser extends DeclarationParser
{
    public DeclaredRoutineParser(PascalParserTD parent)
    {
        super(parent);
    }

    private static int dummyCounter = 0;

    public SymTabEntry parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        Definition routineDefn = null;
        String dummyName = null;
        SymTabEntry routineId = null;
        TokenType routineType = token.getType();

        switch ((PascalTokenType) routineType) {
            case PROGRAM: {
                token = nextToken();
                routineDefn = DefinitionImpl.PROGRAM;
                dummyName = "DummyProgramName".toLowerCase();
                break;
            }

            case PROCEDURE: {
                token = nextToken();
                routineDefn = DefinitionImpl.PROCEDURE;
                dummyName = "DummyProcedureName_".toLowerCase() +
                            String.format("%03d", ++dummyCounter);
                break;

            }

            case FUNCTION: {
                token = nextToken();
                routineDefn = DefinitionImpl.FUNCTION;
                dummyName = "DummyFunctionName_".toLowerCase() +
                            String.format("%03d", ++dummyCounter);
                break;
            }

            default: {
                routineDefn = DefinitionImpl.PROGRAM;
                dummyName = "DummyProgramName".toLowerCase();
                break;
            }
        }

        routineId = parseRoutineName(token, dummyName);
        routineId.setDefinition(routineDefn);

        token = currentToken();

        ICode iCode = ICodeFactory.createICode();

        routineId.setAttribute(ROUTINE_ICODE, iCode);
        routineId.setAttribute(ROUTINE_ROUTINES, new ArrayList<SymTabEntry>());

        if (routineId.getAttribute(ROUTINE_CODE) == FORWARD) {
            SymTab symTab = (SymTab) routineId.getAttribute(ROUTINE_SYMTAB);
            symTabStack.push(symTab);
        }else {
            routineId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
        }

        if (routineDefn == DefinitionImpl.PROGRAM) {
            symTabStack.setProgramId(routineId);
        }

        else if (routineId.getAttribute(ROUTINE_CODE) != FORWARD) {
            ArrayList<SymTabEntry> subroutines = (ArrayList<SymTabEntry>)
                                parentId.getAttribute(ROUTINE_ROUTINES);

            subroutines.add(routineId);
        }

        if (routineId.getAttribute(ROUTINE_CODE) == FORWARD) {
            if (token.getType() != SEMICOLON) {
                errorHandler.flag(token, ALREADY_FORWARDED, this);
                parseHeader(token, routineId);
            }
        }
        else {
            parseHeader(token, routineId);
        }

        token = currentToken();
        if (token.getType() == SEMICOLON) {
            do {
                token = nextToken();
            } while (token.getType() == SEMICOLON);
        } else {
            errorHandler.flag(token, MISSING_SEMICOLON, this);
        }

        if ((token.getType() == IDENTIFIER) &&
            (token.getText().equalsIgnoreCase("forward")))
        {
            token = nextToken();
            routineId.setAttribute(ROUTINE_CODE, FORWARD);
        }
        else {
            routineId.setAttribute(ROUTINE_CODE, DECLARED);

            BlockParser blockParser = new BlockParser(this);
            ICodeNode rootNode = blockParser.parse(token, routineId);
            iCode.setRoot(rootNode);
        }

        symTabStack.pop();

        return routineId;
    }


    private SymTabEntry parseRoutineName(Token token, String dummyName)
        throws Exception
    {

        SymTabEntry routineId = null;

        if (token.getType() == IDENTIFIER) {
            String routineName = token.getText().toLowerCase();
            routineId = symTabStack.lookupLocal(routineName);
            
            if (routineId == null) {
                routineId = symTabStack.enterLocal(routineName);

            }
            else if (routineId.getAttribute(ROUTINE_CODE) != FORWARD) {
                routineId = null;
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
            }

            token = nextToken();
        }
        else {
            errorHandler.flag(token, MISSING_IDENTIFIER, this);
        }

        if (routineId == null) {
            routineId = symTabStack.enterLocal(dummyName);
        }

        return routineId;
    }

    private void parseHeader(Token token, SymTabEntry routineId)
        throws Exception
    {

        parseFormalParameters(token, routineId);
        token = currentToken();

        if (routineId.getDefinition() == DefinitionImpl.FUNCTION) {
            VariableDeclarationsParser variableDeclarationsParser = 
                new VariableDeclarationsParser(this);

            variableDeclarationsParser.setDefinition(DefinitionImpl.FUNCTION);
            TypeSpec type = variableDeclarationsParser.parseTypeSpec(token);

            token = currentToken();

            if (type != null) {
                TypeForm form = type.getForm();
                if ((form == TypeFormImpl.ARRAY) ||
                    (form == TypeFormImpl.RECORD))
                {
                    errorHandler.flag(token, INVALID_TYPE, this);
                }
            }
            else {
                type = Predefined.undefinedType;
            }

            routineId.setTypeSpec(type);
            token = currentToken();
        }
    }

    private static final EnumSet<PascalTokenType> PARAMETER_SET = 
        DeclarationsParser.DECLARATION_START_SET.clone();

    static {
        PARAMETER_SET.add(VAR);
        PARAMETER_SET.add(IDENTIFIER);
        PARAMETER_SET.add(RIGHT_PAREN);
    }

    private static final EnumSet<PascalTokenType> LEFT_PAREN_SET = 
        DeclarationsParser.DECLARATION_START_SET.clone();
    static {
        LEFT_PAREN_SET.add(LEFT_PAREN);
        LEFT_PAREN_SET.add(SEMICOLON);
        LEFT_PAREN_SET.add(COLON);
    }


    private static final EnumSet(PascalTokenType> RIGHT_PAREN_SET = 
            LEFT_PAREN_SET.clone();

    static {
        RIGHT_PAREN_SET.remove(LEFT_PAREN);
        RIGHT_PAREN_SET.add(RIGHT_PAREN);
    }
