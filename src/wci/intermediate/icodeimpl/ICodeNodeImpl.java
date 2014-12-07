package wci.intermediate.icodeimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import wci.intermediate.*;


public class ICodeNodeImpl
  extends HashMap<ICodeKey, Object>
  implements ICodeNode
{

  private ICodeNodeType type;
  private ICodeNode parent;
  private ArrayList<ICodeNode> children;

  public ICodeNodeImpl(ICodeNodeType type)
  {
    this.type = type;
    this.parent = null;
    this.children = new ArrayList<ICodeNode>();
  }

  public ICodeNodeType getType()
  {
    return type;
  }

  public ICodeNode getParent()
  {
    return parent;
  }

  public ICodeNode addChild(ICodeNode node)
  {
    if (node != null) {
      children.add(node);
      ((ICodeNodeImpl) node).parent = this;
    }
    return node;
  }

  public ArrayList<ICodeNode> getChildren()
  {
    return children;
  }

  public void setAttribute(ICodeKey key, Object value)
  {
    put(key, value); 

  }


  public Object getAttribute(ICodeKey key)
  {
    return get(key);
  }

  public ICodeNode copy() 
  {
    ICodeNodeImpl copy = (ICodeNodeImpl) ICodeFactory.creteICodeNode(type);

    Set<Map.Entry<ICodeKey, Object> attributes = entrySet();
    Iterator<Map.Entry<ICodeKey, Object>> it = attributes.iterator();

    while (it.hasNext()) {
      Map.Entry<ICodeKey, Object> attribute = it.next();

      copy.put(attribute.getKey(), attribute.getValue());

     }
    return copy;
  }

  public String toString()
  {
    return type.toString();
  }
}

  
