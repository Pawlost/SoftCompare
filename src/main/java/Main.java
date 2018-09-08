package main.java;

import jdk.nashorn.internal.objects.NativeUint16Array;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        HashMap<Integer, Document> oldH = new HashMap<>();
        HashMap<Integer, Document> newH = new HashMap<>();

        try {
            newH.put(1, Jsoup.parse(FileLoader.load("./src/resources/chapter2.html")));
            oldH.put(1, Jsoup.parse(FileLoader.load("./src/resources/chapter1.html")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            softCompare(oldH, newH,"./resources");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //argorithm itself, frontend of resources
    public static void softCompare(HashMap<Integer, Document> oldHTMLChapters, HashMap<Integer, Document> newHTMLChapters,
                                   String tempPath) throws IOException {

        System.out.println("Starting soft compare");
        Document difference = Jsoup.parse("");
        int chaptersSize = (oldHTMLChapters.size() <= newHTMLChapters.size() ? newHTMLChapters.size() : oldHTMLChapters.size());

        for (int i = 1; i <= chaptersSize; i++) {
            Document oldDiff = oldHTMLChapters.get(i);
            Document newDiff = newHTMLChapters.get(i);

            Element oldElements = oldDiff.body().getAllElements().first();
            Element newElements = newDiff.body().getAllElements().first();

            while (oldElements.children().size() > 0) {
                while (oldElements.children().size() > 0 && newElements.children().size() > 0) {
                    oldElements = oldElements.child(0);
                    newElements = newElements.child(0);
                }

                while (oldElements.children().size() > 0) {
                    oldElements = oldElements.child(0);
                    oldElements.parent().append("<font color='orange'>[" + oldElements.tagName() + "]</font>");
                }

                if (!oldElements.ownText().equals(newElements.ownText())) {
                    Element help = oldElements.clone();
                    oldElements = oldElements.parent();
                    oldElements.child(0).remove();
                    oldElements.append("<" + help.tagName() + "><font color='red'>[" + help.ownText() + "]</font>" +
                            "</" + help.tagName() + ">");
                    if(newElements.hasText()){
                        oldElements.append("<" + newElements.tagName() + "><font color='green'>[" + newElements.ownText() +
                                "]</font>" + "</" + newElements.tagName() + ">");
                    }
                }

                while (oldElements.parent() != null && !oldElements.tagName().equals("body")) {
                    oldElements = oldElements.parent();
                }

                while (newElements.parent() != null && !newElements.tagName().equals("body")) {
                    newElements = newElements.parent();
                }

                difference.append(oldElements.child(0).toString());
                if(oldElements.children().size() > 0) {
                    oldElements.child(0).remove();
                }
                if(newElements.children().size() > 0) {
                    newElements.child(0).remove();
                }
            }
        }

        createFile(tempPath + "/difference.html", difference.html());
        System.out.println("Soft compare done");
    }

    //creates colored changes
    private static Document editDiffText(Document diffChapter, String type) {
        switch (type) {
            case "removed":
                for (Element edited : diffChapter.select("body")) {
                    String html = edited.html();
                    edited.remove();
                    diffChapter.append("<font color='red'><del>" + html + "</del></font>");
                }
                break;

            case "created":
                for (Element edited : diffChapter.select("body")) {
                    String html = edited.html();
                    edited.remove();
                    diffChapter.append("<font color='green'>" + html + "</font>");
                }
                break;

            case "tag":
                for (Element edited : diffChapter.select("body")) {
                    String html = edited.html();
                    edited.remove();
                    diffChapter.append("<font color='orange'>" + html + "</font>");
                }
                break;
        }
        return diffChapter.clone();
    }

    public static void createFile(String filePath, String text) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, text + "\n", "UTF-8");
    }
}
