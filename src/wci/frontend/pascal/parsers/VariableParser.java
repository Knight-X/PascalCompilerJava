ackage wci.frontend.pascal.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.icodeimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.DefinitionImpl.UNDEFINED;
import static wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;
import static wci.intermediate.symtabimpl.DefinitionImpl.VALUE_PARM;
import static wci.intermediate.symtabimpl.DefinitionImpl.VAR_PARM;
import static wci.intermediate.typeimpl.TypeFormImpl.ARRAY;
import static wci.intermediate.typeimpl.TypeFormImpl.RECORD;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

