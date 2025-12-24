package service;

import domain.AssociationInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import java.io.PrintWriter;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

public class Associations {

    protected static List<AssociationInfo> getAssociations(Document doc){
        NodeList associations = doc.getElementsByTagName("ASSOCIATION");
        List<AssociationInfo> assocList = new ArrayList<>();


        for (int i = 0; i < associations.getLength(); i++) {
            Node assocNode = associations.item(i);
            if (assocNode.getNodeType() == Node.ELEMENT_NODE) {
                Element assocElement = (Element) assocNode;
                AssociationInfo assoc = new AssociationInfo();

                assoc.name = Extract.elementText(assocElement, "NAME");
                assoc.toEntity = Extract.elementText(assocElement, "TO_ENTITY");
                assoc.isParent = "1".equals(Extract.elementText(assocElement, "IS_PARENT"));
                assoc.isViewReference = "1".equals(Extract.elementText(assocElement, "IS_VIEW_REFERENCE"));


                NodeList assocAttrs = assocElement.getElementsByTagName("ASSOCIATION_ATTRIBUTE");
                List<String> assocAttrNames = new ArrayList<>();

                for (int j = 0; j < assocAttrs.getLength(); j++) {
                    Node assocAttrNode = assocAttrs.item(j);
                    if (assocAttrNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element assocAttrElement = (Element) assocAttrNode;
                        String attrName = Extract.elementText(assocAttrElement, "NAME");
                        if (attrName != null) {
                            assocAttrNames.add(attrName);
                        }
                    }
                }

                assoc.attributes = assocAttrNames;
                assoc.properties = Extract.extractAssociationProperties(assocElement);
                assocList.add(assoc);
            }
        }

        return assocList;
    }
    protected static void writeAssoc(PrintWriter writer, List<AssociationInfo> associations) {

        writer.println("associations {");

        int maxAssocNameLen = 0;
        int maxToEntityLen = 0;

        for (AssociationInfo assoc : associations) {
            maxAssocNameLen = Math.max(maxAssocNameLen, assoc.name.length());
            maxToEntityLen = Math.max(maxToEntityLen, assoc.toEntity.length());
        }

        for (AssociationInfo assoc : associations) {
            String name = Format.formatPaddingRight(assoc.name, maxAssocNameLen);
            String toEntity = Format.formatPaddingRight(assoc.toEntity, maxToEntityLen).strip();
            String attrs = String.join(",", assoc.attributes);

            String typeRef = assoc.isParent ? "parent" : "reference";


            if (!assoc.properties.isEmpty()) {
                for (Map.Entry<String, String> entry : assoc.properties.entrySet()) {
                    writer.print("   " + typeRef + "    " + name + "    " + toEntity + "(" + attrs + ")/" + entry.getValue() + ";");
                }
            } else {
                writer.print("   " + typeRef + "    " + name + "    " + toEntity + "(" + attrs + ");");
            }
            writer.println();
        }

        writer.println("}");
    }
}
