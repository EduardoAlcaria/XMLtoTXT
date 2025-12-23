package service;

import domain.AssociationInfo;
import domain.AttributeInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvertXMLToText {
    public static String convert(Path xmlFile, Path outputDir) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile.toFile());
        doc.getDocumentElement().normalize();

        String entityName = Extract.textContent(doc, "NAME");
        String component = Extract.textContent(doc, "COMPONENT");
        String layer = Extract.textContent(doc, "LAYER");


        NodeList attributes = doc.getElementsByTagName("ATTRIBUTE");
        List<AttributeInfo> attrList = new ArrayList<>();

        for (int i = 0; i < attributes.getLength(); i++) {
            Node attrNode = attributes.item(i);
            if (attrNode.getNodeType() == Node.ELEMENT_NODE) {
                Element attrElement = (Element) attrNode;
                AttributeInfo attr = new AttributeInfo();

                attr.name = Extract.elementText(attrElement, "NAME");
                attr.datatype = Extract.elementText(attrElement, "DATATYPE");


                if (attr.datatype != null && attr.datatype.equalsIgnoreCase("ENUMERATION")) {
                    attr.enumName = Extract.elementText(attrElement, "ENUMERATION_NAME");
                }

                attr.format = Extract.elementText(attrElement, "FORMAT");
                attr.length = Extract.elementText(attrElement, "LENGTH");
                attr.isPrimaryKey = "1".equals(Extract.elementText(attrElement, "IS_PRIMARY_KEY"));
                attr.isParentKey = "1".equals(Extract.elementText(attrElement, "IS_PARENT_KEY"));
                attr.isPublic = "1".equals(Extract.elementText(attrElement, "IS_PUBLIC"));
                attr.isMandatory = "1".equals(Extract.elementText(attrElement, "IS_MANDATORY"));
                attr.isServerGenerated = "1".equals(Extract.elementText(attrElement, "IS_SERVER_GENERATED"));
                attr.isUpdateAllowed = "1".equals(Extract.elementText(attrElement, "IS_UPDATE_ALLOWED"));
                attr.isUpdateAllowedIfNull = "1".equals(Extract.elementText(attrElement, "IS_UPDATE_ALLOWED_IF_NULL"));
                attr.isDefaultLov = "1".equals(Extract.elementText(attrElement, "IS_DEFAULT_LOV"));
                attr.isQueryable = "1".equals(Extract.elementText(attrElement, "IS_QUERYABLE"));
                attr.isDerived = "1".equals(Extract.elementText(attrElement, "IS_DERIVED"));




                attr.codeGenProperties = Extract.codeGenProperties(attrElement);

                attrList.add(attr);
            }
        }

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
                assocList.add(assoc);
            }
        }

        generateOutputFile(
                Path.of(outputDir.toString(), entityName + ".entity"),
                entityName,
                component,
                layer,
                attrList,
                assocList
        );

        return entityName;
    }
    private static void generateOutputFile(Path outputFile,
                                           String entityName,
                                           String component,
                                           String layer,
                                           List<AttributeInfo> attributes,
                                           List<AssociationInfo> associations) throws IOException {

        PrintWriter writer = new PrintWriter(new FileWriter(outputFile.toFile()));

        writer.println("entityname " + entityName + ";");
        writer.println("component  " + component + ";");
        if (!layer.isEmpty()) {
            writer.println("layer      " + layer + ";");
        }
        writer.println();
        writer.println();

        writer.println("attributes {");

        int maxNameLen = 0;
        int maxDatatypeLen = 0;
        int maxFlagsLen = 0;

        for (AttributeInfo attr : attributes) {

            maxNameLen = Math.max(maxNameLen, attr.name.length());
            String datatypeStr = Format.formatDatatype(attr);
            maxDatatypeLen = Math.max(maxDatatypeLen, datatypeStr.length());
            String flagsStr = Format.formatFlags(attr);
            maxFlagsLen = Math.max(maxFlagsLen, flagsStr.length());
        }

        for (AttributeInfo attr : attributes) {
            String visibility = Extract.visibility(attr);
            String name = Format.formatPaddingRight(attr.name, maxNameLen);
            String datatype = Format.formatPaddingRight(Format.formatDatatype(attr), maxDatatypeLen);
            String flags = Format.formatPaddingRight(Format.formatFlags(attr), maxFlagsLen);


            writer.print("   " + visibility + " " + name + " " + datatype + " " + flags);


            if (!attr.codeGenProperties.isEmpty()) {
                writer.println("{");
                for (Map.Entry<String, String> entry : attr.codeGenProperties.entrySet()) {
                    writer.println("      " + entry.getKey() + " \"" + entry.getValue() + "\";");
                }
                writer.print("   }");
            }

            writer.println();
        }

        writer.println("}");
        writer.println();

        if (!associations.isEmpty()) {
            writer.println("associations {");

            int maxAssocNameLen = 0;
            int maxToEntityLen = 0;

            for (AssociationInfo assoc : associations) {
                maxAssocNameLen = Math.max(maxAssocNameLen, assoc.name.length());
                maxToEntityLen = Math.max(maxToEntityLen, assoc.toEntity.length());
            }

            for (AssociationInfo assoc : associations) {
                String name = Format.formatPaddingRight(assoc.name, maxAssocNameLen);
                String toEntity = Format.formatPaddingRight(assoc.toEntity, maxToEntityLen);
                String attrs = String.join(", ", assoc.attributes);

                String typeRef = assoc.isParent ? "parent" : "reference";

                writer.println("   " + typeRef + "    " + name + "    " + toEntity + "(" + attrs + ");");
            }

            writer.println("}");
        }

        writer.close();
    }
}
