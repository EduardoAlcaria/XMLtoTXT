package service;

import domain.AssociationInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Associations {

    protected static List<AssociationInfo> getAssociations(Document doc) {
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
                assoc.behavior = Extract.elementText(assocElement, "DELETE_BEHAVIOUR");
                assoc.DbImplementation = Extract.codeGenProperties(assocElement);


                // attributes
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

                // ===== USE / LABEL LIST (SEPARATE) =====
                List<String> useLines = new ArrayList<>();

                NodeList useDefs = assocElement.getElementsByTagName("USE_DEFINITION");

                for (int k = 0; k < useDefs.getLength(); k++) {
                    Node useDefNode = useDefs.item(k);

                    if (useDefNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element useDefElement = (Element) useDefNode;
                        NodeList children = useDefElement.getChildNodes();

                        String columnName = null;
                        String columnAlias = null;
                        String labelName = null;

                        for (int m = 0; m < children.getLength(); m++) {
                            Node child = children.item(m);

                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                Element el = (Element) child;

                                switch (el.getTagName()) {
                                    case "COLUMN_NAME" -> columnName = el.getTextContent();
                                    case "COLUMN_ALIAS" -> columnAlias = el.getTextContent();
                                    case "NAME_OF_LABEL" -> labelName = el.getTextContent();
                                }
                            }
                        }

                        if (columnName != null && columnAlias != null) {
                            useLines.add("use " + columnName + " as " + columnAlias);
                        } else if (columnName != null) {
                            useLines.add("use " + columnName);
                        }

                        if (labelName != null) {
                            useLines.add("labelText \"" + labelName + "\";");
                        }
                    }
                }

                // store separately
                assoc.useLines = useLines;

                assocList.add(assoc);
            }
        }

        return assocList;
    }

    protected static void writeAssoc(PrintWriter writer,
                                     List<AssociationInfo> associations) throws IOException {

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

            boolean hasUse = assoc.useLines != null && !assoc.useLines.isEmpty();
            boolean hasBehavior = assoc.behavior != null && !assoc.behavior.isEmpty();
            boolean hasDbImplementation = assoc.DbImplementation != null && !assoc.DbImplementation.isEmpty();

            // ---- header ----
            writer.print("   " + typeRef + "    " + name + "    " + toEntity + "(" + attrs + ")");

            if (hasBehavior) {
                writer.print("/" + assoc.behavior);
            }

            if (hasDbImplementation) {
                writer.print("{\n");
                for (String s : assoc.DbImplementation.keySet()) {
                    writer.print("    " + s + "    " + "\"" +assoc.DbImplementation.get(s) + "\";" + "\n");
                }
                writer.println("\n}");
            }


            // ---- body ----
            if (hasUse) {
                writer.println("{");
                for (String line : assoc.useLines) {
                    writer.println("      " + line + ";");
                }
                writer.println("   }");
            }

            writer.println();
        }

        writer.println("}");

        Path path = Paths.get("output/CurrencyRevalDetail.entity");

        List<String> stringStream = Files.readAllLines(path)
                .stream()
                .filter(s -> (s.contains("reference") || s.startsWith("parent")) && !s.substring(s.length() - 1).equalsIgnoreCase("{"))
                .map(s -> s.concat(";"))
                .toList();

        stringStream.forEach(System.out::println);

    }

}
