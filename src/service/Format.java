package service;

import domain.AttributeInfo;
import domain.Flags;
import domain.Types;

public class Format {
    protected static String formatDatatype(AttributeInfo attr) {
        String length = attr.length != null ? attr.length : "";
        String precision = attr.precision != null ? attr.precision : "";

        if (attr.datatype == null) {
            return "";
        }

        String dataTypeRet = switch (attr.datatype) {
            case "NUMBER" -> attr.precision != null ? String.format("%s(%s)",Types.NUMBER.getType(), precision) : Types.NUMBER.getType();
            case "TEXT" ->  attr.length != null ? String.format("%s(%s)",Types.TEXT.getType(), length) : Types.TEXT.getType();
            case "BOOLEAN" -> attr.booleanValue == null ? Types.BOOLEAN.getType() : Types.BOOLEAN.getType() + attr.booleanValue;
            case "DATE" -> Types.DATE.getType();
            case "DATETIME" -> Types.DATETIME.getType();
            case "ENUMERATION" -> attr.enumSubset == null ? String.format("%s(%s)", Types.ENUMERATION.getType(), attr.enumName) : String.format("%s(%s.%s)", Types.ENUMERATION.getType(), attr.enumName, attr.enumSubset);
            default -> attr.datatype;
        };

        if (attr.format != null) {
            dataTypeRet = dataTypeRet + "/" + attr.format;
        }
        return dataTypeRet;
    }

    protected static String formatFlags(AttributeInfo attr) {
        StringBuilder flags = new StringBuilder();

        if (attr.isParentKey) {
            flags.append(Flags.PARENTKEY.getValue());
        }

        if (attr.isPrimaryKey && !attr.isParentKey) {
            flags.append(Flags.PRIMARYKEY.getValue());
        }

        if (!attr.isPrimaryKey && !attr.isParentKey) {
            flags.append(Flags.ATTRIBUTE.getValue());
        }

        if (attr.isMandatory) {
            flags.append(Flags.MANDATORY.getValue());
        } else {
            flags.append(Flags.NONE.getValue());
        }

        if (!attr.isServerGenerated) {
            flags.append(Flags.INSERTABLE.getValue());
        } else {
            flags.append(Flags.NONE.getValue());
        }

        if (attr.isUpdateAllowed) {
            flags.append(Flags.UPDATEALLOWED.getValue());
        } else {
            flags.append(Flags.NONE.getValue());
        }

        if (attr.isDefaultLov) {
            flags.append(Flags.DEFAULTLOV.getValue());
        } else {
            flags.append(Flags.NONE.getValue());
        }

        if (attr.isDerived) {
            flags.append(Flags.DERIVED.getValue());
        }

        if (attr.codeGenProperties.isEmpty()) {
            flags.append(";");
        }

        return flags.toString();
    }

    protected static String formatPaddingRight(String str, int length) {
        return String.format("%-" + length + "s", str);
    }

    protected static String tagNameToPropertyName(String tagName) {

        StringBuilder result = new StringBuilder();
        String[] parts = tagName.split("_");

        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(part.charAt(0))
                        .append(part.substring(1).toLowerCase());
            }
        }

        return result.toString();
    }
}
