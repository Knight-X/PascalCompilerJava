package wci.intermediate.symtabimpl;

import wci.intermediate.Definition;

public enum DefinitionImpl implements Definition
{
    CONSTANT, ENUMERATION_CONSTANT("enmeration constant"),
    TYPE, VARIABLE, FIELD("record field"),
    VALUE_PARM("value parameter"),
    VAR_PARM("VAR parameter"),
    PROGRAM,
    PROCEDURE,
    FUNCTION,
    UNDEFINED;

    private String text;

    DefinitionImpl(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return text;
    }
}
