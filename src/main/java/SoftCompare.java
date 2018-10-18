package main.java;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SoftCompare {
    private HighterElement hightE;
    private LesserElement lessE;
    private int chaptersSize;
    private HashMap<Integer, Document> oldHTMLChapters;
    private HashMap<Integer, Document> newHTMLChapters;
    private String tempPath;
    private Document difference;

    public SoftCompare(HashMap<Integer, Document> oldHTMLChapters, HashMap<Integer, Document> newHTMLChapters,
                       String tempPath) {
        difference = Jsoup.parse("");
        this.tempPath = tempPath;
        this.oldHTMLChapters = oldHTMLChapters;
        this.newHTMLChapters = newHTMLChapters;
        chaptersSize = (oldHTMLChapters.size() <= newHTMLChapters.size() ? newHTMLChapters.size() : oldHTMLChapters.size());
    }

    //argorithm itself, frontend of resources
    public void softCompare() throws IOException {

        System.out.println("Starting soft compare");

        //Dividing to chapters
        for (int i = 1; i <= chaptersSize; i++) {

            hightE = new HighterElement(oldHTMLChapters.get(i).clone());
            lessE = new LesserElement(newHTMLChapters.get(i).clone());

            //check if there is correct number of main elements
            if (lessE.getMainESize() == 0) {
                hightE.mainElement.wrap("<font class='FancyDiff' color='red'>");
                difference.append(hightE.toString());

            }

            hightE.changeMainElement(hightE.get(0).get(0));
            lessE.changeMainElement(lessE.get(0).get(0));

            handleChildren(hightE);
            handleChildren(lessE);

            hightE.addChildren();
            lessE.addChildren();

            //Starting Soft Compare replacement
            while (hightE.size() > 0) {
                if (hightE.getLastChildren().size() > 0) {
                    hightE.changeMainElement(hightE.getLastChildren().get(0));
                }

                if (lessE.getLastChildren().size() > 0) {
                    lessE.changeMainElement(lessE.getLastChildren().get(0));
                }

                //Checking undertags
                while (hightE.getMainESize() > 0) {
                    handleChildren(hightE);
                    handleChildren(lessE);

                    //Removes all tags with class FancyDiff
                    for (int tag = 0; tag < hightE.getLastChildren().size(); tag++) {
                        if (hightE.getLastChildren().get(tag).className().equals("FancyDiff")) {
                            hightE.getLastChildren().remove(tag);
                        }

                        if (hightE.getLastChildren().size() == 0) {
                            hightE.remove(hightE.size() - 1);
                        }
                    }

                    for (int tag = 0; tag < lessE.getLastChildren().size(); tag++) {
                        if (lessE.getLastChildren().get(tag).className().equals("FancyDiff")) {
                            lessE.getLastChildren().remove(tag);
                        }

                        if (lessE.getLastChildren().size() == 0) {
                            lessE.remove(lessE.size() - 1);
                        }
                    }

                    hightE.addChildren();
                    lessE.addChildren();

                    if (!hightE.mainElement.ownText().isEmpty() && hightE.mainElement.parents().size() > 0 &&
                            !hightE.mainElement.ownText().equals(lessE.mainElement.ownText()) &&
                            hightE.getLastChildren().size() == lessE.getLastChildren().size()) {
                        hightE.createMultiDifference(lessE.mainElement, 0);
                        break;
                    }
                }

                //Deleting arrays
                    deleteArrays(hightE);
                    deleteArrays(lessE);

                        //See if there is change in all tags
                        System.out.println("here");
                        if (hightE.getLastChildren().size() > lessE.getLastChildren().size() &&
                                !hightE.get(1).equals(hightE.getLastChildren())) {

                            hightE.mainElement = hightE.getLastChildren().get(0);
                            if (lessE.getLastChildren().size() > 0) {
                                lessE.mainElement = lessE.getLastChildren().get(0);
                            }
                            Element mainElementParent = hightE.mainElement.parent();
                            ArrayList<Element> clone = (ArrayList<Element>) fixChildren(hightE.cloneE(), mainElementParent).getLastChildren().clone();
                            int is = findChildren(clone, lessE.mainElement);

                            if (is < 0) {
                                if (hightE.size() == lessE.size()) {
                                    if (hightE.getLastChildren().size() > lessE.getLastChildren().size()) {
                                        if (findAllChildren(hightE.getLastChildren(), lessE.getLastChildren()) >= lessE.getLastChildren().size()) {
                                            is = findChildren(clone, hightE.mainElement);
                                            hightE.createMultiDifference(is);
                                            sortMainE(hightE, is);
                                            updateChildren(hightE, hightE.mainElement);
                                            hightE.getLastChildren().remove(0);
                                        } else {
                                            is = findChildren(clone, hightE.mainElement);
                                            hightE.createMultiDifference(lessE.mainElement, is);
                                            hightE.getLastChildren().remove(1);
                                            hightE.mainElement.child(is).remove();
                                            sortMainE(hightE, is - 1);
                                            updateChildren(hightE, hightE.mainElement);
                                            hightE.getLastChildren().remove(0);
                                            lessE.getLastChildren().remove(0);
                                        }
                                    }
                                }
                            } else {
                                hightE.getLastChildren().remove(0);
                                if (lessE.getLastChildren().size() > 0) {
                                    lessE.getLastChildren().remove(0);
                                }
                            }
                        } else if (hightE.size() > lessE.size()) {
                            int size = hightE.getLastChildren().size();
                            for (int ir = 0; ir < size; ir++) {
                                hightE.mainElement = hightE.getLastChildren().get(0);
                                Element mainElementParent = hightE.mainElement.parent();
                                ArrayList<Element> clone = (ArrayList<Element>) fixChildren(hightE.cloneE(), mainElementParent).getLastChildren().clone();
                                int is = findChildren(clone, hightE.mainElement);
                                hightE.createMultiDifference(is);
                                sortMainE(hightE, is);
                                updateChildren(hightE, hightE.mainElement);
                                hightE.getLastChildren().remove(0);
                            }
                            hightE.remove(hightE.size() - 1);
                        } else if (!hightE.mainElement.ownText().isEmpty() && hightE.mainElement.parents().size() > 0 &&
                                !hightE.mainElement.ownText().equals(lessE.mainElement.ownText()) &&
                                hightE.getLastChildren().size() == lessE.getLastChildren().size()) {
                            hightE.createMultiDifference(lessE.mainElement, 0);
                        } else if (hightE.size() > 2) {
                            if (hightE.getLastChildren().size() > 0) {
                                hightE.getLastChildren().remove(0);
                            }

                            if (lessE.getLastChildren().size() > 0) {
                                lessE.getLastChildren().remove(0);
                            }
                        }

                if (hightE.get(1).size() == 0 && hightE.size() == 2) {
                    hightE.remove(1);
                }

                //Creating difference Html
                if (hightE.size() > 1) {
                    if (hightE.getFirstElement().children().size() > 0 && hightE.size() < 3) {
                        difference.append(hightE.getFirstElement().child(0).clone().toString());
                        hightE.getFirstElement().child(0).remove();

                        if (lessE.getFirstElement().children().size() > 0) {
                            lessE.getFirstElement().child(0).remove();
                        }

                        if (hightE.get(1).size() > hightE.getFirstElement().children().size()) {
                            hightE.get(1).remove(0);
                            if (lessE.get(1).size() > 0) {
                                lessE.get(1).remove(0);
                            }
                        }
                    }
                } else {
                    hightE.remove(0);
                }
            }
        }

        createFile(tempPath + "/difference.html", difference.html());
        System.out.println("Soft compare done");
    }

    //Put html tags in correct order (From middle position reverse tags)
    private void sortMainE(LesserElement element, int position) {
        for (int k = 0; k < element.mainElement.children().size() - 1; k++) {
            Element help = element.mainElement.child(position).clone();
            element.mainElement.child(position).remove();
            element.mainElement.append(help.toString());
        }
    }

    private LesserElement fixChildren(LesserElement element, Element replacement) {
        element.removeAllLast();
        for (Element child : replacement.children()) {
            element.getLastChildren().add(child);
        }
        return element.cloneE();
    }

    private void updateChildren(LesserElement element, Element mainElement) {
        int lenght = element.getLastChildren().size();
        for (int k = 0; k < lenght; k++) {
            ArrayList<Element> clone = (ArrayList<Element>) fixChildren(element.cloneE(), element.mainElement).getLastChildren().clone();
            int index = -1;
            while (index < 0) {
                index = findChildren(clone, element.getLastChildren().get(k));
            }
            element.getLastChildren().set(k, mainElement.child(index));
        }
    }

    //Add children if main in leserElemets have more under tags
    private void handleChildren(LesserElement lessE) {
        if (lessE.getMainESize() > 1) {
            for (int index = 0; index < lessE.getMainESize(); index++) {
                lessE.addChildren(lessE.getMainChild(index));
            }
            lessE.changeMainElement();
        } else if (lessE.getMainESize() == 1) {
            lessE.addChildren(lessE.mainElement);
            lessE.changeMainElement();
        }
    }

    //deleting children as long as there is only 1 element in children
    private void deleteArrays(LesserElement firstE) {
        for (int array = firstE.size() - 1; array > 1; array--) {
            if (firstE.size() > 2 && firstE.get(array).size() < 2) {
                firstE.remove(array);
            }
        }
    }

    private void createFile(String filePath, String text) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, text + "\n", "UTF-8");
    }

    private int findChildren(ArrayList<Element> mainElement, Element search) {
        for (int i = 0; i < mainElement.size(); i++) {
            if (mainElement.get(i).ownText().equals(search.ownText())) {
                return i;
            } else if (mainElement.get(i).text().equals(search.text())) {
                return i;
            } else if (mainElement.get(i).children().size() > 0 && mainElement.get(i).child(0).ownText().equals(search.text())) {
                return i;
            }
        }
        return -1;
    }

    private int findAllChildren(ArrayList<Element> hightElement, ArrayList<Element> lesserElement) {
        int count = 0;
        for (int hindex = 0; hindex < hightElement.size(); hindex++) {
            for (int lindex = 0; lindex < lesserElement.size(); lindex++) {
                if (hightElement.get(hindex).text().equals(lesserElement.get(lindex).text())) {
                    count++;
                }
            }
        }
        return count;
    }
}