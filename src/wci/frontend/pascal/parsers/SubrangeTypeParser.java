package wci.frontend.pascal.parsers;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.*;
import wci.intermediate.typeimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.*;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;


public class SubrangeTypeParser extends TypeSpecificationParser
{
    protected SubrangeTypeParser(PascalParserTD parent)
    {
        super(parent);
    }

    public TypeSpec parse(Token token)
        throws Exception
    {
        TypeSpec subrangeType = TypeFactory.createType(SUBRANGE);

        Object minValue = null;
        Object maxValue = null;

        Token constantToken = token;
        ConstantDefinitionsParser constantParser =
            new ConstantDefinitionsParser(this);
        minValue = constantParser.parseConstant(token);

        TypeSpec minType = constantToken.getType() == IDENTIFIER
                            ? constantParser.getConstantType(constantToken)
                            : constantParser.getConstantType(minValue);


        minValue = checkValueType(constantToken, minValue, minType);

        token = currentToken();

        Boolean sawDotDot = false;

        if (token.getType() == DOT_DOT) {
            token = nextToken();
            sawDotDot = true;
        }

        TokenType tokenType = token.getType();

        if (ConstantDefinitionsParser.CONSTANT_START_SET.contains(tokenType)) {
            if (!sawDotDot) {
                errorHandler.flag(token, MISSING_DOT_DOT, this);
            }

            token = synchronize(ConstantDefinitionsParser.CONSTANT_START_SET);

            constantToken  = token;
            maxValue = constantParser.parseConstant(token);


            TypeSpec maxType = constantToken.getType() == IDENTIFIER
                                ? constantParser.getConstantType(constantToken)
                                : constantParser.getConstantType(maxValue);

            maxValue = checkValueType(constantToken, maxValue, maxType);

            if ((minType == null) || (maxType == null)) {
                errorHandler.flag(constantToken, INCOMPATIBLE_TYPES, this);
            }

            else if (minType != maxType) {
                errorHandler.flag(constantToken, INVALID_SUBRANGE_TYPE, this);
            }

            else if ((minValue != null) && (maxValue != null) && 
                    ((Integer) minValue >= (Integer) maxValue)) {
                errorHandler.flag(constantToken, MIN_GT_MAX, this);
            }
        }
        else {
            errorHandler.flag(constantToken, INVALID_SUBRANGE_TYPE, this);
        }

        subrangeType.setAttribute(SUBRANGE_BASE_TYPE, minType);
        subrangeType.setAttribute(SUBRANGE_MIN_VALUE, minValue);
        subrangeType.setAttribute(SUBRANGE_MAX_VALUE, maxValue);

        return subrangeType;
    }

    private Object checkValueType(Token token, Object value, TypeSpec type)
    {
        if (type == null) {
            return value;
        }
        if (type == Predefined.integerType) {
            return value;
        }
        else if (type == Predefined.charType) {
            char ch = ((String) value).charAt(0);
            return Character.getNumericValue(ch);
        }
        else if (type.getForm() == ENUMERATION) {
            return value;
        }
        else {
            errorHandler.flag(token, INVALID_SUBRANGE_TYPE, this);
            return value;
        }
    }
}

