package service;

public class Converter {
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
