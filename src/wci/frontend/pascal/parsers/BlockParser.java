package wci.frontend.pascal.parsers;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;



public class BlockParser extends PascalParserTD
{

    public BlockParser(PascalParserTD parent)
    {
        super(parent);
    }

    public ICodeNode parse(Token token, SymTabEntry routineId)
        throws Exception
    {
        DeclarationsParser declarationParser = new DeclarationsParser(this);
        StatementParser statementParser = new StatementParser(this);

        declarationParser.parse(token);

        token = synchronize(StatementParser.STMT_START_SET);
        TokenType tokenType = token.getType();
        ICodeNode rootNode = null;

        if (tokenType == BEGIN) {
            rootNode = statementParser.parse(token);
        }

        else {
            errorHandler.flag(token, MISSING_BEGIN, this);

            if (StatementParser.STMT_START_SET.contains(tokenType)) {
                rootNode = ICodeFactory.createICodeNode(COMPOUND);
                statementParser.parseList(token, rootNode, END, MISSING_END);
            }
        }

        return rootNode;
    }
}
