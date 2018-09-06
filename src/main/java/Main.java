package main.java;

import jdk.nashorn.internal.objects.NativeUint16Array;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

            int oldSize = oldElements.children().size();
            int newSize = newElements.children().size();
            int elementsSize = (newSize < oldSize ? newSize : oldSize);

            while (elementsSize > 0) {
                HashMap<String, String> changeIndexes = new HashMap<>();
                
                while (oldElements.children().size() > 0 && newElements.children().size() > 0) {
                    if (oldElements.hasText() && newElements.hasText()) {
                        if (!oldElements.tagName().equals(newElements.tagName()) &&
                                !oldElements.text().equals(newElements.text())) {
                            System.out.println("Text se neschoduje");
                        }
                    } else {
                        if (!oldElements.tagName().equals(newElements.tagName())) {
                            //if(oldElements.children().size() > newElements.children().size()){
                            changeIndexes.put(oldElements.tagName(), "<font color='orange'>" + oldElements.tagName() + "</font>");
                        }
                    }
                    oldElements = oldElements.child(0);
                    newElements = newElements.child(0);

                    oldSize = oldElements.children().size();
                    newSize = newElements.children().size();
                    elementsSize = (newSize < oldSize ? newSize : oldSize);
                }

                while (oldElements.parent() != null && !oldElements.tagName().equals("body")) {
                    oldElements = oldElements.parent();
                    newElements = newElements.parent();
                }

                Element change = oldElements.child(0).clone();

                for (Element tag : change.getAllElements()) {
                    if (changeIndexes.get(tag.tagName()) != null) {
                        change.append(changeIndexes.get(tag.tagName()));
                        changeIndexes.remove(tag.tagName());
                    }
                }

                difference.append(change.toString());
                oldElements.child(0).remove();
                newElements.child(0).remove();
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
