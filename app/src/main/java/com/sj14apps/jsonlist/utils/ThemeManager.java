package com.sj14apps.jsonlist.utils;

import javafx.scene.Scene;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ThemeManager {
    private final Map<String, String> colors = new HashMap<>();

    public void loadTheme(InputStream xmlStream) {
        if (xmlStream == null) return;
        
        colors.clear();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlStream);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("color");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                if (nList.item(temp) instanceof Element) {
                    Element eElement = (Element) nList.item(temp);
                    String name = eElement.getAttribute("name");
                    String value = eElement.getTextContent();
                    colors.put(name, value);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading theme XML: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public String generateCssVariables() {
        StringBuilder sb = new StringBuilder(".root {\n");
        for (Map.Entry<String, String> entry : colors.entrySet()) {
            // Convert underscore names to kebab-case and use -fx- prefix
            String cssName = entry.getKey().replace("_", "-");
            sb.append("-fx-color-").append(cssName).append(": ").append(entry.getValue()).append(";\n");
        }
        sb.append("}");
        return sb.toString();
    }

    public void applyToScene(Scene scene) {
        if (scene == null) return;
        
        String css = generateCssVariables();
        // Remove existing dynamic theme stylesheets
        scene.getStylesheets().removeIf(s -> s.startsWith("data:text/css"));
        // Add new dynamic stylesheet using data URI (properly encoded)
        scene.getStylesheets().add("data:text/css," + css.replace("#", "%23").replace("\n", "").replace(" ", ""));
    }

    public int getColorAsInt(String name) {
        String hex = colors.get(name);
        if (hex == null) return 0;
        
        try {
            if (hex.startsWith("#")) hex = hex.substring(1);
            // Handle rgba cases if necessary, but primary use is hex for RawJsonView
            if (hex.contains("(")) return 0; 
            return (int) Long.parseLong(hex, 16);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getColor(String name) {
        return colors.getOrDefault(name, "#000000");
    }
}
