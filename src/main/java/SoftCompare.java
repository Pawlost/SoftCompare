package main.java;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class SoftCompare {
    private HighterElements hightE;
    private LesserElements lessE;
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

            hightE = new HighterElements(oldHTMLChapters.get(i).clone());
            lessE = new LesserElements(newHTMLChapters.get(i).clone());

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

                //Creating arrays
                if (hightE.getMainESize() > 0) {
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

                        if (!hightE.mainElement.ownText().isEmpty() && !hightE.mainElement.parent().className().equals("FancyDiff")) {
                            if (!hightE.mainElement.ownText().equals(lessE.mainElement.ownText())) {
                                hightE.createDifference(lessE.mainElement, 0);
                            }
                        }
                    }
                }else if(hightE.getLastChildren().size() > 1 && hightE.get(1).size() == lessE.get(1).size()) {
                    if (!hightE.mainElement.ownText().isEmpty() && !hightE.mainElement.parent().className().equals("FancyDiff")) {
                        if (!hightE.mainElement.ownText().equals(lessE.mainElement.ownText())) {
                            for (int e = 0; e <  hightE.mainElement.parent().children().size(); e++) {
                                if (hightE.mainElement.parent().child(e).equals(hightE.mainElement)) {
                                    hightE.createMultiDifference(lessE.mainElement, e);
                                    hightE = (HighterElements) sort(hightE, e);
                                }
                            }
                        }
                    }
                }

                //Deleting arrays
                deleteArrays(hightE);
                deleteArrays(lessE);

                //See if there is change in all tags
                if (hightE.getLastChildren().size() > lessE.getLastChildren().size() &&
                        !hightE.get(1).equals(hightE.getLastChildren())) {
                    for (int ind = 0; ind < hightE.getLastChildren().size(); ind++) {
                        if (hightE.size() == lessE.size()) {
                            try {
                                hightE.mainElement = hightE.getLastChildren().get(ind);
                                lessE.mainElement = lessE.getLastChildren().get(ind);
                                if (!hightE.mainElement.tagName().equals(lessE.mainElement.tagName())
                                        || !hightE.mainElement.ownText().equals(lessE.mainElement.ownText())) {
                                    lessE.mainElement = lessE.mainElement.parent();
                                    lessE.appendChange(hightE.mainElement.toString());
                                    hightE.missingDifference(ind);

                                    lessE = sort(lessE, ind);
                                    fixChildren(lessE);
                                    hightE = (HighterElements) sort(hightE, ind);
                                    fixChildren(hightE);
                                }
                            } catch (IndexOutOfBoundsException ex) {
                                lessE.mainElement = lessE.getLastChildren().get(ind - 1).parent();
                                lessE.appendChange(hightE.mainElement.toString());
                                hightE.missingDifference(ind);

                                lessE = sort(lessE, ind);
                                lessE = fixChildren(lessE);
                                hightE = (HighterElements) sort(hightE, ind);
                                hightE = (HighterElements) fixChildren(hightE);
                            }
                        }
                    }
                }

                if (hightE.getLastChildren().size() > 1) {
                    if (lessE.getLastChildren().size() > 1) {
                        lessE.getLastChildren().remove(0);
                    }
                    hightE.getLastChildren().remove(0);
                }

                if (hightE.get(1).size() <= 0 && hightE.size() <= 2) {
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
                            lessE.get(1).remove(0);
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
    private LesserElements sort(LesserElements element, int position) {
        for (int k = position; k <  element.mainElement.children().size() - 1; k++) {
            Element help = element.mainElement.child(position).clone();
            element.mainElement.child(position).remove();
            element.mainElement.append(help.toString());
        }
        return (LesserElements) element.clone();
    }

    private LesserElements fixChildren(LesserElements elements){
        elements.removeAllLast();
        for(Element child:elements.mainElement.children()) {
            elements.getLastChildren().add(child);
        }
        return (LesserElements) elements.clone();
    }

    //Add children if main in leserElemets have more under tags
    private void handleChildren(LesserElements lessE) {
        if (lessE.getMainESize() > 1) {
            for (int index = 0; index < lessE.getMainESize(); index++) {
                lessE.addChildren(lessE.getMainChild(index));
            }
            lessE.changeMainElement();
        }else if (lessE.getMainESize() == 1){
            lessE.addChildren(lessE.mainElement);
            lessE.changeMainElement();
        }
    }

    //deleting children as long as there is only 1 element in children
    private void deleteArrays(LesserElements lessE) {
        for (int array = lessE.size() - 1; array > 1; array--) {
            if (lessE.size() > 2 && lessE.get(array).size() < 2) {
                lessE.remove(array);
            }
        }
    }

    private void createFile(String filePath, String text) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, text + "\n", "UTF-8");
    }
}
