package service;

import com.sun.nio.sctp.Association;
import domain.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConvertXMLToText {
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void convert(Path xmlFile, Path outputDir) throws Exception {

        System.out.println(ANSI_CYAN + "Converting entity " + xmlFile.getFileName() + ANSI_RESET);


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
                    if (Extract.elementText(attrElement, "ENUMERATION_SUBSET") != null) {
                        attr.enumSubset = Extract.elementText(attrElement, "ENUMERATION_SUBSET");
                    }
                }

                boolean hasBooleanValue = (Extract.elementText(attrElement, "BOOLEAN_TRUE_VALUE") != null);

                if (hasBooleanValue) {
                    attr.booleanValue = String.format("(\"%s\", \"%s\")", Extract.elementText(attrElement, "BOOLEAN_TRUE_VALUE"),
                            Extract.elementText(attrElement, "BOOLEAN_FALSE_VALUE"));
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

        List<AssociationInfo> assocList = Associations.getAssociations(doc);
        List<StateInfo> stateList = StateMachine.extractStates(doc);


        List<String> readAllLines = Files.readAllLines(xmlFile);



        Map<String, String> codeGenProperties = readAllLines.stream()
                .map(String::trim)
                .takeWhile(s -> !s.equals("</CODE_GENERATION_PROPERTIES>"))
                .filter(s -> !s.contains("<CODE_GENERATION_PROPERTIES>"))
                .filter(s -> !s.contains("<?"))
                .filter(s -> !s.contains("<ENTITY xmlns:xsi="))
                .collect(Collectors.toMap(
                        key -> key.substring(1, key.indexOf('>')),
                        value -> value.substring(value.indexOf('>') + 1, value.indexOf("</")))

                );


        generateOutputFile(
                Path.of(outputDir.toString(), entityName + ".entity"),
                entityName,
                component,
                layer,
                attrList,
                assocList,
                stateList,
                codeGenProperties
        );



    }

    private static void generateOutputFile(Path outputFile,
                                           String entityName,
                                           String component,
                                           String layer,
                                           List<AttributeInfo> attributes,
                                           List<AssociationInfo> associations,
                                           List<StateInfo> states,
                                           Map<String, String> codeGenProperties) throws IOException {

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile.toFile()))){
            writer.println("entityname " + entityName + ";");
            writer.println("component  " + component + ";");
            if (!layer.isEmpty()) {
                writer.println("layer      " + layer + ";");
            }
            writer.println();
            writer.println();

            if (!codeGenProperties.isEmpty()) {
                System.out.println(codeGenProperties);
                writer.println("codegenproperties {");
                    codeGenProperties.forEach((key, value) -> {
                        String formatedKey = Format.tagNameToPropertyName(key);
                        String formatedValue = "        \"" + value + "\"";
                        writer.println("        " + formatedKey + "          " + formatedValue + ";");
                    });

                writer.println("}");
            }


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
                Associations.writeAssoc(writer, associations);
            }

            if (!associations.isEmpty()) {
                StateMachine.writeStates(writer, states);
            }




        }catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }



}
