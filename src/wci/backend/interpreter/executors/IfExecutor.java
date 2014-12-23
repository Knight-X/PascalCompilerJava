package wci.backend.interpreter.executors;

import java.util.ArrayList;

import wci.intermediate.*;
import wci.backend.interpreter.*;

import static wci.intermediate.ICodeNodeType.*;
import static wci.backend.interpreter.RuntimeErrorCode.*;


public class IfExecutor extends StatementExecutor
{
    public IfExecutor(Executor parent)
    {
        super(parent);
    }

    public Object execute(ICodeNode node)
    {
        ArrayList<ICodeNode> children = node.getChildren();
        ICodeNode exprNode = children.get(0);
        ICodeNode thenStmtNode = children.get(1);
        ICodeNode elseStmtNode = children.size() > 2 ? children.get(2) : null;

        ExpressionExecutor expressionExecutor = new ExpressionExecutor(this);

        StatementExecutor statementExecutor = new StatementExecutor(this);

        boolean b = (Boolean) expressionExecutor.execute(exprNode);

        if (b) {
            statementExecutor.execute(thenStmtNode);
        } else if (elseStmtNode != null) {
            statementExecutor.execute(elseStmtNode);
        }

        ++executionCount;
        return null;
    }
}
