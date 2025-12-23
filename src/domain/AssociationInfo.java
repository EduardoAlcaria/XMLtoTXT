package domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AssociationInfo {

    public String name;
    public String toEntity;
    public boolean isParent;
    public boolean isViewReference;
    public List<String> attributes;
    public Map<String, String> properties;

}
