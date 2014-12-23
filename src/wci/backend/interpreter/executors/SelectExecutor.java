package wci.backend.interpreter.executors;


import java.util.ArrayList;
import java.util.HashMap;

import wci.intermediate.*;
import wci.backend.interpreter.*;

import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.backend.interpreter.RuntimeErrorCode.*;


public class SelectExecutor extends StatementExecutor
{

    public SelectExecutor(Executor parent)
    {
        super(parent);
    }

    private static HashMap<ICodeNode, HashMap<Object, ICodeNode>> jumpCache =
        new HashMap<ICodeNode, HashMap<Object, ICodeNode>>();

    public Object execute(ICodeNode node)
    {
        HashMap<Object, ICodeNode> jumpTable = jumpCache.get(node);

        if (jumpTable == null) {
            jumpTable = createJumpTable(node);
            jumpCache.put(node, jumpTable);

        }

        ArrayList<ICodeNode> selectChildren = node.getChildren();
        ICodeNode exprNode = selectChildren.get(0);

        ExpressionExecutr expressionExecutor = new ExpressionExecutor(this);
        Object selectValue = new ExpressionExecutor(this);
        Object selectValue = expressionExecutor.execute(exprNode);

        ICodeNode statementNode = jumpTable.get(selectValue);
        if (statementNode != null) {
            StatementExecutor statementExecutor = new StatementExecutor(this);
            statementExecutor.execute(statementNode);
        }

        ++executionCount;
        return null;
    }

    private HashMap<Object, ICodeNode> createJumpTable(ICodeNode node)
    {
        HashMap<Object, ICodeNode> jumpTable = new HashMap<Object, ICodeNode>();

        ArraList<ICodeNode> selectChildren = node.getChildren();

        for (int i = 1; i < selectChildren.size(); ++i) {
            ICodeNode branchNode = selectChildren.get(i);
            ICodeNode constantsNode = branchNode.getChildren().get(0);
            ICodeNode statemnetNode = branchNode.getChildren().get(1);

            ArrayList<ICodeNode> constantsList = constantsNode.getChildren();

            for (ICodeNode constantNode : constantsList) {

                Object value = constantNode.getAttribute(VALUE);
                jumpTable.put(value, statementNode);
            }
        }
        return jumpTable;
    }
}
