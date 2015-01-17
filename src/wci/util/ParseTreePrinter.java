package wci.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.PrintStream;

import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;


public class ParseTreePrinter
{
  private static final int INDENT_WIDTH = 4;
  private static final int LINE_WIDTH = 80;

  private PrintStream ps;
  private int length;
  private String indent;
  private String indentation;
  private StringBuilder line;

  public ParseTreePrinter(PrintStream ps)
  {
    this.ps = ps;
    this.length = 0;
    this.indentation = "";

    this.line = new StringBuilder();
    this.indent = "";

    for (int i = 0; i < INDENT_WIDTH; ++i) {
      this.indent += " ";
    }
  
  }

  public void print(ICode iCode)
  {
    ps.println("\n====== INTERMEDIATE CODE ======\n");

    printNode((ICodeNodeImpl) iCode.getRoot());  
    printLine();
  }

  private void printNode(ICodeNodeImpl node)
  {
    append(indentation);
    append("<" + node.toString());

    printAttributes(node);
    printTypeSpec(node);

    ArrayList<ICodeNode> childNodes = node.getChildren();

    if ((childNodes != null) && (childNodes.size() > 0)) {
      append(">");
      printLine();
 
      printChildNodes(childNodes);
      append(indentation);
      append("</" + node.toString() + ">");

    }

    else {
      append(" ");
      append("/>");
    }

    printLine();
  }


  private void printAttributes(ICodeNodeImpl node)
  {
    String saveIndentation = indentation;

    indentation += indent;

    Set<Map.Entry<ICodeKey, Object>> attributes = node.entrySet();

    Iterator<Map.Entry<ICodeKey, Object>> it = attributes.iterator();

    while (it.hasNext()) {
      Map.Entry<ICodeKey, Object> attribute = it.next();
      printAttribute(attribute.getKey().toString(), attribute.getValue());
    }

    indentation = saveIndentation;
  }

  private void printAttribute(String keyString, Object value)
  {
    boolean isSymTabEntry = value instanceof SymTabEntry;

    String valueString = isSymTabEntry ? ((SymTabEntry) value).getName() : value.toString();

    String text = keyString.toLowerCase() + "=\"" + valueString + "\"";
    append(" ");
    append(text);
    

    if (isSymTabEntry) {
      int level = ((SymTabEntry) value).getSymTab().getNestingLevel();
      printAttribute("LEVEL", level);
    }
  }

  private void printChildNodes(ArrayList<ICodeNode> childNodes)
  {
     String saveIndentation = indentation;
     indentation += indent;
     
     for (ICodeNode child : childNodes) {
       printNode((ICodeNodeImpl) child);
     }

     indentation = saveIndentation;
  }


  private void printTypeSpec(ICodeNodeImpl node)
  {
      TypeSpec typeSpec = node.getTypeSpec();

      if (typeSpec != null) {
          String saveMargin = indentation;

          indentation += indent;

          String typeName;

          SymTabEntry typeId = typeSpec.getIdentifier();

          if (typeId != null) {
              typeName = typeId.getName();
          }

          else {
              int code = typeSpec.hashCode() + typeSpec.getForm().hashCode();

              typeName = "$anon_" + Integer.toHexString(code);
          }

          printAttribute("TYPE_ID", typeName);
          indentation = saveMargin;
      }
  }

  private void append(String text)
  {  
    int textLength = text.length();
    boolean lineBreak = false;

    if (length + textLength > LINE_WIDTH) {
     printLine();
     line.append(indentation);
     length = indentation.length();
     lineBreak = true;
    }

    if (!(lineBreak && text.equals(" "))) {
      line.append(text);
      length += textLength;
    }

  }

  private void printLine()
  {
    if (length > 0) {
      ps.println(line);
      line.setLength(0); 
      length = 0;
    }
  }



}
