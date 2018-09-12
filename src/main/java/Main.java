package main.java;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
            softCompare(oldH, newH, "./resources");
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
            newChildren.add(newDiff.body().clone());

            oldEArray.add((ArrayList<Element>) oldChildren.clone());
            newEArray.add((ArrayList<Element>) newChildren.clone());

            Element oldElement = oldEArray.get(0).get(0).child(0);
            Element newElement = newEArray.get(0).get(0).child(0);

            while (oldEArray.size() > 0) {

                while (oldElement.children().size() > 0) {
                    oldChildren = new ArrayList<>();
                    newChildren = new ArrayList<>();

                    if (oldElement.children().size() > 1) {
                        for (int index = 0; index < oldElement.children().size(); index++) {
                            oldChildren.add(oldElement.child(index));
                        }
                        oldEArray.add(oldChildren);
                        oldChildren = new ArrayList<>();
                        oldElement = oldElement.child(0);
                    }

                    if (newElement.children().size() > 1) {
                        for (int index = 0; index < newElement.children().size(); index++) {
                            newChildren.add(newElement.child(index));
                        }
                        newEArray.add(newChildren);
                        newChildren = new ArrayList<>();
                        newElement = newElement.child(0);
                    }

                    while (oldElement.children().size() > 0 && newElement.children().size() <= 0) {
                        oldElement = oldElement.child(0);
                        oldElement.parent().append("<font class='FancyDiff'" +
                                " color='orange'>[" + oldElement.tagName() + "]</font>");
                    }

                    if (!oldElement.ownText().isEmpty()) {
                       while (newElement.children().size() > 0) {
                            newElement = newElement.child(0);
                        }

                        if (!oldElement.ownText().equals(newElement.ownText())) {
                            Element help = oldElement.clone();
                            oldElement = oldElement.parent();
                            oldElement.child(0).remove();
                            oldElement.append("<" + help.tagName() + "><font class='FancyDiff' color='red'>[" + help.ownText()
                                    + "]</font></" + help.tagName() + ">");
                            if (newElement.hasText()) {
                                oldElement.append("<" + newElement.tagName() + "><font class='FancyDiff' color='green'>["
                                        + newElement.ownText() + "]</font></" + newElement.tagName() + ">");
                            }
                            oldElement = oldElement.child(0);
                        }
                    }

                    if (newElement.children().size() > 0) {
                        newChildren.add(newElement.child(0));
                        newElement = newElement.child(0);
                    }

                    if (oldElement.children().size() > 0) {
                        oldChildren.add(oldElement.child(0));
                        oldElement = oldElement.child(0);
                    }

                    oldEArray.add(oldChildren);
                    newEArray.add(newChildren);


                }

                for (int array = oldEArray.size() - 1; array > 0; array--) {
                    if (oldEArray.size() > 1) {
                        if (oldEArray.get(array).size() <= 1) {
                            oldEArray.remove(array);
                            if (oldEArray.size() > 1) {
                                if (oldEArray.get(array - 1).size() > 1) {
                                    if (oldEArray.get(array - 1).get(0).children().size() > 0) {
                                        oldElement = oldEArray.get(array - 1).get(0).child(0);
                                    } else {
                                        oldElement = oldEArray.get(array - 1).get(0);
                                    }
                                }
                            }
                        }
                    }
                }

                for (int array = newEArray.size() - 1; array > 0; array--) {
                    if (newEArray.size() > 1) {
                        if (newEArray.get(array).size() <= 1) {
                            newEArray.remove(array);
                            if (newEArray.size() > 1) {
                                if (newEArray.get(array - 1).size() > 1) {
                                    if (newEArray.get(array - 1).get(0).children().size() > 0) {
                                        newElement = newEArray.get(array - 1).get(0).child(0);
                                    } else {
                                        newElement = newEArray.get(array - 1).get(0);
                                    }
                                }
                            }
                        }/* else {
                            if (newEArray.get(array).size() > 1) {
                                newEArray.get(array).remove(0);
                                newElement = newEArray.get(array).get(0);
                                array = 0;
                            }
                        }*/
                    }
                }


                    //Creating difference Html
                if (oldEArray.size() <= 1) {
                    if (oldEArray.get(0).get(0).children().size() > 0) {
                        difference.append(oldEArray.get(0).get(0).child(0).clone().toString());
                        oldEArray.get(0).get(0).child(0).remove();

                        for (int ri = 1; ri < newEArray.size(); ri++) {
                            newEArray.remove(ri);
                        }
                        newEArray.get(0).get(0).child(0).remove();

                        if (oldEArray.get(0).get(0).children().size() > 0) {
                            oldElement = oldEArray.get(0).get(0).child(0);
                        }

                        if (newEArray.get(0).get(0).children().size() > 0) {
                            newElement = newEArray.get(0).get(0).child(0);
                        }
                    }else{
                        oldEArray.remove(0);
                    }
                }
            }
        }

        createFile(tempPath + "/difference.html", difference.html());
        System.out.println("Soft compare done");
    }

    public static void createFile(String filePath, String text) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, text + "\n", "UTF-8");
    }
}
