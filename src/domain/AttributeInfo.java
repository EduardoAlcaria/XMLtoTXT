package domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class AttributeInfo {
    public String name;
    public String datatype;
    public String length;
    public String enumName;
    public String format;
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

}
