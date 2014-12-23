package wci.backend.interpreter.executors;

import java.util.ArrayList;

import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;
import wci.backend.interpreter.*;

import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.backend.interpreter.RuntimeErrorCode.*;

public class LoopExecutor extends StatementExecutor
{

    public LoopExecutor(Executor parent)
    {
        super(parent);
    }

    public Object execute(ICodeNode node)
    {
        boolean exitLoop = false;
        ICodeNode exprNode = null;

        ArrayList<ICodeNode> loopChildren = node.getChildren();
        ExpressionExecutor expressionExecutor = new ExpressionExecutor(this);
        StatementExecutor statementExecutor = new StatementExecutor(this);

        while (!exitLoop) {
            ++executionCount;

            for (ICodeNode child : loopChildren) {
                ICodeNodeTypeImpl childType = 
                    (ICodeNodeTypeImpl) child.getType();

                if (childType == TEST) {
                    if (exprNode == null) {
                        exprNode = child.getChildren().get(0);
                    }
                    exitLoop = (Boolean) expressionExecutor.execute(exprNode);
                }

                else {
                    statementExecutor.execute(child);
                }

                if (exitLoop) {
                    break;
                }
            }
        }
        return null;
    }
}
