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
            newChildren.add(newDiff.body().clone());

            oldEArray.add((ArrayList<Element>) oldChildren.clone());
            newEArray.add((ArrayList<Element>) oldChildren.clone());

            int childrenSize = oldEArray.get(oldEArray.size()-1).size();

            while (oldEArray.size() > 0) {
                while (childrenSize > 0) {
                    oldChildren = new ArrayList<>();
                    Element element = oldEArray.get(oldEArray.size() - 1).get(childrenSize - 1);
                    for (int index = 0; index < element.children().size() - 1; index++) {
                        oldChildren.add(element.child(index).clone());
                        element.child(index).remove();
                    }
                    oldEArray.add((ArrayList<Element>) oldChildren.clone());
                    childrenSize = oldEArray.get(oldEArray.size() - 1).size();
                }

                childrenSize = oldEArray.get(oldEArray.size()-1).size();

                System.out.println(oldEArray);

                /*
                while (childrenSize > 0) {
                    newChildren = new ArrayList<>();
                    Element element = newEArray.get(newEArray.size() - 1).get(childrenSize - 1);
                    for (int index = 0; index < element.children().size() - 1; index++) {
                        newChildren.add(element.child(index).clone());
                        element.child(index).remove();
                    }
                    newEArray.add((ArrayList<Element>) newChildren.clone());
                    childrenSize = newEArray.get(newEArray.size() - 1).size();
                }


                oldChildren = oldEArray.get(oldEArray.size() - 1);
                newChildren = newEArray.get(newEArray.size() - 1);

                if(oldChildren.size() > 1 && newChildren.size() > 1){
                    for(int in = 0; in < newChildren.size(); in ++){
                        if (!oldChildren.get(in).tagName().equals(newChildren.get(in).tagName())){
                            Element help = oldChildren.get(in).clone();
                            oldChildren.get(in).remove();
                            help.append( "<font color='red'>[" + help.tagName()+"][" + help.ownText() + "]["+ help.tagName()
                                    +"</font>");
                        }

                        if(!oldChildren.get(in).ownText().equals(newChildren.get(in).ownText())){
                            Element help = oldChildren.get(in).clone();
                            oldChildren.get(in).remove();
                            help.append("<" + help.tagName() + "><font color='red'>[" + help.ownText() + "]</font>" +
                                    "</" + help.tagName() + ">");
                        }
                    }
                }else if (oldChildren.size() > 0 && newChildren.size() > 0){
                    if (!oldChildren.get(0).tagName().equals(newChildren.get(0).tagName())){
                        Element help = oldChildren.get(0).clone();
                        oldChildren.get(0).remove();
                        oldChildren.get(0).append( "<font color='red'>[" + help.tagName()+"][" + help.ownText() + "]["+ help.tagName()
                                +"</font>");
                    }

                    if(!oldChildren.get(0).ownText().equals(newChildren.get(0).ownText())){
                        Element help = oldChildren.get(0).clone();
                        oldChildren.get(0).remove();
                        oldChildren.get(0).append("<" + help.tagName() + "><font color='red'>[" + help.ownText() + "]</font>" +
                                "</" + help.tagName() + ">");
                    }
                }*/

                System.out.println(oldEArray.size());

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
                        oldChildren.get(oldChildren.size() - 1).prepend(clone.toString());
                    } else {
                        oldChildren.get(0).prepend(clone.toString());
                    }
                    oldChildren = oldEArray.get(oldEArray.size() - 1);
                    if (oldChildren.size() <= 0) {
                        oldEArray.remove(oldEArray.size() - 1);
                    }
                } else {
                    oldEArray.remove(oldEArray.size() - 1);
                }

                if(oldEArray.size() <= 1){
                    difference.append(oldEArray.get(0).get(0).clone().toString());
                    oldEArray.remove(0);
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
