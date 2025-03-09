package com.abdessalem.finetudeingenieurworkflow.Entites;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
public class HtmlCleanerUtil {
    public static String cleanHtml(String htmlContent) {
        if (htmlContent == null) {
            return "";
        }
        // Convertir le HTML en texte brut
        Document document = Jsoup.parse(htmlContent);
        return document.text();
    }
}
