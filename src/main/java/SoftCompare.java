package main.java;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SoftCompare {
    HighterElements hightE;
    LesserElements lessE;

    public SoftCompare() {
    }

    //argorithm itself, frontend of resources
    public void softCompare(HashMap<Integer, Document> oldHTMLChapters, HashMap<Integer, Document> newHTMLChapters,
                            String tempPath) throws IOException {

        System.out.println("Starting soft compare");

        Document difference = Jsoup.parse("");
        int chaptersSize = (oldHTMLChapters.size() <= newHTMLChapters.size() ? newHTMLChapters.size() : oldHTMLChapters.size());

        //Dividing to chapters
        for (int i = 1; i <= chaptersSize; i++) {

            hightE = new HighterElements(oldHTMLChapters.get(i).clone());
            lessE = new LesserElements(newHTMLChapters.get(i).clone());

            //check if there is correct number of main elements
            if (lessE.getMainESize() == 0) {
                hightE.mainElement.wrap("<font class='FancyDiff' color='red'>");
                difference.append(hightE.toString());
                hightE.removeAll();

            } else if (hightE.getMainESize() > lessE.getMainESize()) {
                //Add main tag if numbers doesn fit
                for (int ind = 0; ind < hightE.getMainESize(); ind++) {
                    if (lessE.getMainESize() > ind) {
                        if (!hightE.getMainChild(ind).tagName().equals(lessE.getMainChild(ind).tagName())) {

                            String text = hightE.mainElement.child(ind).toString();
                            hightE.appendChange(text);
                            hightE.mainElement.child(ind).remove();
                            sort(hightE, ind);

                            lessE.mainElement.append(hightE.getMainChild(ind).toString());
                            sort(lessE, ind);
                        }
                    } else {
                        Element help = hightE.getMainChild(ind).clone();

                        hightE.getMainChild(ind).remove();
                        hightE.appendChange(help.toString());

                        lessE.appendChange(help.toString());
                    }
                }
            }

            //Starting Soft Compare replacement
            while (hightE.size() > 0) {

                try {
                    hightE.changeMainElement(hightE.getFirstElement().child(0));
                    lessE.changeMainElement(lessE.getFirstElement().child(0));
                } catch (IndexOutOfBoundsException nothing) {
                    System.out.println("Almost done");
                }

                //Creating arrays
                while (hightE.getMainESize() > 0) {

                    if (hightE.getMainESize()> 0) {
                        hightE.changeMainElement();
                    }
                    if(lessE.getMainESize() > 0) {
                        lessE.changeMainElement();
                    }

                    //Removes all tags with class FancyDiff
                    for (int test = 0; test < hightE.getLastChildren().size(); test++) {
                        if (hightE.getLastChildren().get(test).className().equals("FancyDiff")) {
                            hightE.getLastChildren().remove(test);
                        }

                        if (hightE.getLastChildren().size() == 0) {
                            hightE.remove(hightE.size() - 1);
                        }
                    }

                    handleChildren(hightE);
                    handleChildren(lessE);

                    if (lessE.getMainESize() > 0 && hightE.getMainESize() > 0) {
                        lessE.changeMainElement();
                        hightE.changeMainElement();

                    } else if (hightE.getMainESize() > 0) {
                        while (hightE.getMainESize() > 0) {
                            hightE.changeMainElement();
                        }
                    }

                    if (!hightE.mainElement.ownText().isEmpty() && !hightE.mainElement.parent().className().equals("FancyDiff")) {
                        if (!hightE.mainElement.ownText().equals(lessE.mainElement.ownText())) {
                            hightE.createDifference(lessE.mainElement);
                        }
                    }

                    hightE.addChildren(hightE.mainElement);
                    lessE.addChildren(lessE.mainElement);

                    hightE.addChildren();
                    lessE.addChildren();
                }

                //Deleting arrays
                deleteArrays(hightE);
                deleteArrays(lessE);

                while (hightE.getLastChildren().size() > lessE.getLastChildren().size()) {
                    for (int ind = 0; ind < hightE.getLastChildren().size(); ind++) {
                        try {
                            lessE.setMainFromChildren(ind);
                            hightE.setMainFromChildren(ind);

                            while (hightE.getMainESize() > 0 && lessE.getMainESize() > 0) {
                                hightE.createDifference(lessE.mainElement.clone());
                            }
                        } catch (Exception ex) {
                            hightE.createDifference(lessE.mainElement.clone());
                        }
                    }
                }

                while (hightE.getLastChildren().size() > 1) {
                    if (lessE.getLastChildren().size() > 1) {
                        lessE.getLastChildren().remove(0);
                    }
                    hightE.getLastChildren().remove(0);
                }

                //Creating difference Html
                if (hightE.size() > 0) {
                    if (hightE.getFirstElement().children().size() > 0) {
                        difference.append(hightE.getFirstElement().child(0).clone().toString());
                        hightE.getFirstElement().child(0).remove();
                        lessE.getFirstElement().child(0).remove();

                        for (int ri = 1; ri < hightE.size(); ri++) {
                            hightE.remove(ri);
                        }

                        for (int ri = 1; ri < lessE.size(); ri++) {
                            lessE.remove(ri);
                        }
                    } else {
                        hightE.remove(0);
                    }
                }
            }
        }

        createFile(tempPath + "/difference.html", difference.html());
        System.out.println("Soft compare done");
    }

    //Put html tags in correct order (From middle position reverse tags)
    private void sort(LesserElements lessE, int position) {
        for (int k = position; k < lessE.getMainESize() - 1; k++) {
            lessE.mainElement.append(lessE.mainElement.clone().toString());
            lessE.getMainChild(position).remove();
        }
    }

    //Add children if main in hightE have more under tags
    private void handleChildren(LesserElements lessE) {
        if (lessE.getMainESize() > 1) {
            for (int index = 0; index < lessE.getMainESize(); index++) {
                lessE.addChildren(lessE.getMainChild(index));
            }
            lessE.addChildren();
            lessE.changeMainElement();
        }
    }

    //deleting children as long as there is only 1 element in children
    private void deleteArrays(LesserElements lessE) {
        for (int array = lessE.size() - 1; array > 0; array--) {
            if (lessE.size() > 1 && lessE.get(array).size() < 2) {
                lessE.remove(array);
            }
        }
    }

    private void createFile(String filePath, String text) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, text + "\n", "UTF-8");
    }
}
