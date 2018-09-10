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
        ArrayList<ArrayList<Element>> oldEArray = new ArrayList<>();
        ArrayList<ArrayList<Element>> newEArray = new ArrayList<>();

        Document difference = Jsoup.parse("");
        int chaptersSize = (oldHTMLChapters.size() <= newHTMLChapters.size() ? newHTMLChapters.size() : oldHTMLChapters.size());

        for (int i = 1; i <= chaptersSize; i++) {
            Document oldDiff = oldHTMLChapters.get(i);
            Document newDiff = newHTMLChapters.get(i);

            ArrayList<Element> oldChildren = new ArrayList<>();
            ArrayList<Element> newChildren = new ArrayList<>();

            oldChildren.add(oldDiff.body().clone());
            oldEArray.add((ArrayList<Element>) oldChildren.clone());

            int childrenSize = oldEArray.get(oldEArray.size()-1).size();
            System.out.println(childrenSize);

            while (oldEArray.size() > 1) {
                while (childrenSize > 0) {
                    oldChildren = new ArrayList<>();
                    Element element = oldEArray.get(oldEArray.size() - 1).get(childrenSize - 1);
                    for (int index = 0; index < element.children().size() - 1; index++) {
                        oldChildren.add(element.child(index).clone());
                        element.child(index).remove();
                        System.out.println(index);
                    }
                    oldEArray.add((ArrayList<Element>) oldChildren.clone());
                    childrenSize = oldEArray.get(oldEArray.size() - 1).size();
                }

                oldChildren = oldEArray.get(oldEArray.size() - 1);
                if (oldChildren.size() > 0) {
                    Element clone = oldChildren.get(oldChildren.size() - 1).clone();
                    oldChildren.remove(oldChildren.size() - 1);
                    if (oldEArray.size() > 1) {
                        oldChildren = oldEArray.get(oldEArray.size() - 2);
                    } else {
                        oldChildren = oldEArray.get(0);
                    }
                    if (oldChildren.size() > 1) {
                        oldChildren.get(oldChildren.size() - 1).append(clone.toString());
                    } else {
                        System.out.println(oldEArray.size());
                        oldChildren.get(0).append(clone.toString());
                    }
                    oldChildren = oldEArray.get(oldEArray.size() - 1);
                    if (oldChildren.size() <= 0) {
                        oldEArray.remove(oldEArray.size() - 1);
                    }
                } else {
                    oldEArray.remove(oldEArray.size() - 1);
                }
            }
        }

        createFile(tempPath + "/difference.html", oldEArray.get(0).get(0).html());
        System.out.println("Soft compare done");
    }

    public static void createFile(String filePath, String text) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, text + "\n", "UTF-8");
    }
}
