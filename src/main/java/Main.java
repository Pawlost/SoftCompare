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

        //Preparing
        ArrayList<ArrayList<Element>> oldEArray = new ArrayList<>();
        ArrayList<ArrayList<Element>> newEArray = new ArrayList<>();

        Document difference = Jsoup.parse("");
        int chaptersSize = (oldHTMLChapters.size() <= newHTMLChapters.size() ? newHTMLChapters.size() : oldHTMLChapters.size());

        //Dividing to chapters
        for (int i = 1; i <= chaptersSize; i++) {
            Document oldDiff = oldHTMLChapters.get(i);
            Document newDiff = newHTMLChapters.get(i);

            ArrayList<Element> oldChildren = new ArrayList<>();
            ArrayList<Element> newChildren = new ArrayList<>();

            oldChildren.add(oldDiff.body().clone());
            newChildren.add(newDiff.body().clone());

            oldEArray.add((ArrayList<Element>) oldChildren.clone());
            newEArray.add((ArrayList<Element>) newChildren.clone());

            Element oldElement = oldEArray.get(0).get(0);
            Element newElement = newEArray.get(0).get(0);

            //check if there is correct number of main elements
            if (newElement.children().size() == 0) {
                oldElement.children().wrap("<font class='FancyDiff' color='red'>");
                difference.append(oldElement.toString());
            } else if (oldElement.children().size() > newElement.children().size()) {
                for (int ind = 0; ind < oldElement.children().size(); ind++) {
                    if (newElement.children().size() > ind) {
                        if (!oldElement.child(ind).tagName().equals(newElement.child(ind).tagName())) {

                            oldElement.append("<font class='FancyDiff' color='red'>[" +
                                    oldElement.child(ind).clone().toString() + "]</font>");
                            oldElement.child(ind).remove();
                            for (int k = ind; k < oldElement.children().size() - 1; k++) {
                                oldElement.append(oldElement.child(ind).clone().toString());
                                oldElement.child(ind).remove();
                            }

                            newElement.append(oldElement.child(ind).clone().toString());
                            for (int k = ind; k < newElement.children().size() - 1; k++) {
                                newElement.append(newElement.child(ind).clone().toString());
                                newElement.child(ind).remove();
                            }
                        }
                    } else {
                        Element help = oldElement.child(ind).clone();
                        oldElement.child(ind).remove();
                        oldElement.append("<font class='FancyDiff' color='red'>[" + help.toString() + "]</font>");

                        newElement.append("<font class='FancyDiff' color='red'>[" + help.toString() + "]</font>");
                    }
                }
            }

            //Starting Soft Compare replacement
            while (oldEArray.size() > 0) {

                oldElement = oldEArray.get(oldEArray.size() - 1).get(0);
                newElement = newEArray.get(newEArray.size() - 1).get(0);
                //Creating arrays
                while (oldElement.children().size() > 0  && !oldElement.tagName().equals("FancyDiff")) {

                    oldChildren = new ArrayList<>();
                    newChildren = new ArrayList<>();

                    oldElement = oldEArray.get(oldEArray.size() -1).get(0);
                    newElement = newEArray.get(newEArray.size() -1).get(0);

                    for(int test=0; test < oldEArray.get(oldEArray.size() - 1).size(); test++){
                        if(oldEArray.get(oldEArray.size() - 1).get(test).className().equals("FancyDiff")){
                            oldEArray.get(oldEArray.size() - 1).remove(test);
                        }
                        if(oldEArray.get(oldEArray.size() - 1).size() == 0){
                            oldEArray.remove(oldEArray.size() - 1);
                        }
                    }

                    if (oldElement.children().size() > 1) {
                        if (!oldElement.child(0).className().equals("FancyDiff")) {
                            for (int index = 0; index < oldElement.children().size(); index++) {
                                oldChildren.add(oldElement.child(index));
                            }
                            oldEArray.add(oldChildren);
                            oldChildren = new ArrayList<>();
                            oldElement = oldElement.child(0);
                        }
                    }

                    if (newElement.children().size() > 1) {
                        if (!newElement.child(0).className().equals("FancyDiff")) {
                            for (int index = 0; index < newElement.children().size(); index++) {
                                newChildren.add(newElement.child(index));
                            }
                            newEArray.add(newChildren);
                            newChildren = new ArrayList<>();
                            newElement = newElement.child(0);
                        }
                    }

                    if (newElement.children().size() > 0 && oldElement.children().size() > 0) {
                        oldElement = oldElement.child(0);
                        newElement = newElement.child(0);
                    } else if (oldElement.children().size() > 0) {
                        if (!oldElement.className().equals("FancyDiff")) {
                            while (oldElement.children().size() > 0 && !oldElement.className().equals("FancyDiff")) {
                                oldElement = oldElement.child(0);
                                oldElement.parent().parent().append("<font class='FancyDiff'" +
                                        " color='orange'>[" + oldElement.tagName() + "]</font>");
                            }
                        } else {
                            while (oldElement.children().size() > 0) {
                                oldElement = oldElement.child(0);
                            }
                        }
                    }

                    if (!oldElement.ownText().isEmpty() && !oldElement.parent().className().equals("FancyDiff")) {
                        if (!oldElement.ownText().equals(newElement.ownText())) {
                            Element help = oldElement.clone();
                            oldElement = oldElement.parent();
                            oldElement.child(0).remove();
                            oldElement.append("<font class='FancyDiff' color='red'><" + help.tagName() + ">[" + help.ownText()
                                    + "]</" + help.tagName() + "></font>");
                            if (!newElement.ownText().isEmpty()) {
                                oldElement.append("<font class='FancyDiff' color='green'><" + newElement.tagName() + ">"
                                        + newElement.ownText() + "</" + newElement.tagName() + "></font>");
                            }
                            oldElement = oldElement.child(0);
                        }
                    }

                    newChildren.add(newElement);
                    oldChildren.add(oldElement);
                    oldEArray.add(oldChildren);
                    newEArray.add(newChildren);
                }

                //Deleting arrays
                for (int array = oldEArray.size() - 1; array > 0; array--) {
                    if (oldEArray.size() > 1 && oldEArray.get(array).size() < 2) {
                        oldEArray.remove(array);
                    }
                }

                for (int array = newEArray.size() - 1; array > 0; array--) {
                    if (newEArray.size() > 1 && newEArray.get(array).size() < 2) {
                        newEArray.remove(array);
                    }
                }

                if (oldEArray.get(oldEArray.size() - 1).size() > 1) {

                    if (newEArray.get(newEArray.size() - 1).size() > 1) {
                        newEArray.get(newEArray.size() - 1).remove(0);
                    }else{
                        /*for(int rem =0; oldEArray.get(oldEArray.size() - 1).size()) {
                            oldEArray.get(oldEArray.size() - 1).get(1);
                            oldEArray.get(oldEArray.size() - 1).remove(1);
                        }*/
                    }

                    oldEArray.get(oldEArray.size() - 1).remove(0);
                }

                //Creating difference Html
                if (oldEArray.size() <= 1) {
                    if (oldEArray.get(0).get(0).children().size() > 0) {
                        difference.append(oldEArray.get(0).get(0).child(0).clone().toString());
                        oldEArray.get(0).get(0).child(0).remove();

                        for (int ri = 1; ri < newEArray.size(); ri++) {
                            newEArray.remove(ri);
                        }

                        if (newEArray.get(0).get(0).children().size() > 0) {
                            newEArray.get(0).get(0).child(0).remove();
                        }
                    } else {
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