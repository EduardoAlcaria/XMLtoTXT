package service;

import domain.AccessModifiers;
import domain.AttributeInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedHashMap;
import java.util.Map;

public class Extract {
    protected static Map<String, String> codeGenProperties(Element attrElement) {
        Map<String, String> properties = new LinkedHashMap<>();

        NodeList codeGenNodes = attrElement.getElementsByTagName("CODE_GENERATION_PROPERTIES");

        for (int i = 0; i < codeGenNodes.getLength(); i++) {
            Node codeGenNode = codeGenNodes.item(i);
            if (codeGenNode.getNodeType() == Node.ELEMENT_NODE) {
                Element codeGenElement = (Element) codeGenNode;
                NodeList children = codeGenElement.getChildNodes();

                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element) child;
                        String tagName = childElement.getTagName();
                        String value = childElement.getTextContent();


                        if (!tagName.equals("CODE_GENERATION_PROPERTIES") &&
                                value != null && !value.trim().isEmpty()) {

                            String propertyName = Converter.tagNameToPropertyName(tagName);
                            properties.put(propertyName, value.trim());
                        }
                    }
                }
            }
        }

        return properties;
    }

    protected static String textContent(Document doc, String tagName) {
        Element root = doc.getDocumentElement();
        NodeList children = root.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE &&
                    node.getNodeName().equals(tagName)) {
                return node.getTextContent();
            }
        }
        return "";
    }

    protected static String elementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    protected static String visibility(AttributeInfo attr) {
        if (attr.isPrimaryKey && !attr.isParentKey ) {
            return AccessModifiers.KEY.getAccessModifier();
        } else if (attr.isPublic) {
            return AccessModifiers.PUBLIC.getAccessModifier();
        } else if (attr.isParentKey) {
            return AccessModifiers.PARENT_KEY.getAccessModifier() ;
        }
        return AccessModifiers.PRIVATE.getAccessModifier();
    }
}
