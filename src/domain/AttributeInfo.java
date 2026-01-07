package domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class AttributeInfo {
    public String name;
    public String datatype;
    public String length;
    public String enumName;
    public String format;
    public String precision;
    public String useDefs;

    public Map<String, String> codeGenProperties = new LinkedHashMap<>();


    public boolean isPrimaryKey;
    public boolean isParentKey;
    public boolean isPublic;
    public boolean isMandatory;
    public boolean isServerGenerated;
    public boolean isUpdateAllowed;
    public boolean isUpdateAllowedIfNull;
    public boolean isDefaultLov;
    public boolean isQueryable;
    public boolean isDerived;


    public String booleanValue;
    public String enumSubset;

    @Override
    public String toString() {
        return "AttributeInfo{" +
                "name='" + name + '\'' +
                ", datatype='" + datatype + '\'' +
                ", length='" + length + '\'' +
                ", enumName='" + enumName + '\'' +
                ", format='" + format + '\'' +
                ", precision='" + precision + '\'' +
                ", useDefs='" + useDefs + '\'' +
                ", codeGenProperties=" + codeGenProperties +
                ", isPrimaryKey=" + isPrimaryKey +
                ", isParentKey=" + isParentKey +
                ", isPublic=" + isPublic +
                ", isMandatory=" + isMandatory +
                ", isServerGenerated=" + isServerGenerated +
                ", isUpdateAllowed=" + isUpdateAllowed +
                ", isUpdateAllowedIfNull=" + isUpdateAllowedIfNull +
                ", isDefaultLov=" + isDefaultLov +
                ", isQueryable=" + isQueryable +
                ", isDerived=" + isDerived +
                ", booleanValue='" + booleanValue + '\'' +
                ", enumSubset='" + enumSubset + '\'' +
                '}';
    }
}
