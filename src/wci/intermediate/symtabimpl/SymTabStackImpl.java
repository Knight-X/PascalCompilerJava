package wci.intermediate.symtabimpl;

import java.util.ArrayList;

import wci.intermediate.*;


public class SymTabStackImpl
    extends ArrayList<SymTab>
    implements SymTabStack
{

    private int currentNestingLevel;
    private SymTabEntry programId;

    public SymTabStackImpl()
    {
      this.currentNestingLevel = 0;
      add(SymTabFactory.createSymTab(currentNestingLevel));
    }

    public void setProgramId(SymTabEntry id)
    {
        this.programId = id;
    }

    public SymTabEntry getProgramId()
    {
        return programId;
    }

    public int getCurrentNestingLevel()
    {
      return currentNestingLevel;
    }

    public SymTab getLocalSymTab()
    {
      return get(currentNestingLevel);
    }

    public SymTab push()
    {
        SymTab symTab = SymTabFactory.createSymTab(++currentNestingLevel);
        add(symTab);

        return symTab;
    }

    public SymTab push(SymTab symTab)
    {
        ++currentNestingLevel;
        add(symTab);

        return symTab;
    }

    public SymTabEntry enterLocal(String name)
    {
      return get(currentNestingLevel).enter(name);
    }

    public SymTab pop()
    {
        SymTab symTab = get(currentNestingLevel);
        remove(currentNestingLevel--);

        return symTab;
    }

    public SymTabEntry lookupLocal(String name)
    {
        return get(currentNestingLevel).lookup(name);
    }

    public SymTabEntry lookup(String name)
    {
      return lookupLocal(name);
    }
}
