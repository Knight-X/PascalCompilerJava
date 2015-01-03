package wci.intermediate.typeimpl;

import wci.intermediate.TypeKey;
public enum TypeKeyImpl implements TypeKey
{
    ENUMERATION_CONSTANTS,

    SUBRANGE_BASE_TYPE, SUBRANGE_MIN_VALUE, SUBRANGE_MAX_VALUE,

    ARRAY_INDEX_TYPE, ARRAY_ELEMENT_TYPE, ARRAY_ELEMENT_COUNT,

    RECORD_SYMTAB
}
