package wci.intermediate.typeimpl;

import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;


public class TypeChecker
{

    public static boolean isInteger(TypeSpec type)
    {
        return (type != null) && (type.baseType() == Predefined.integerType);
    }

    public static boolean areBothInteger(TypeSpec type1, TypeSpec type2)
    {
        return isInteger(type1) && isInteger(type2);
    }


