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

            hightE.changeMainElement(hightE.get(0).get(0));
            lessE.changeMainElement(lessE.get(0).get(0));

            handleChildren(hightE);
            handleChildren(lessE);

            hightE.addChildren();
            lessE.addChildren();

            //Starting Soft Compare replacement
            while (hightE.size() > 1) {

                hightE.changeMainElement(hightE.get(1).get(0));
                lessE.changeMainElement(lessE.get(1).get(0));

                //Creating arrays
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

                    System.out.println("Here");

                    hightE.addChildren();
                    lessE.addChildren();

                    if (!hightE.mainElement.ownText().isEmpty() && !hightE.mainElement.parent().className().equals("FancyDiff")) {
                        if (!hightE.mainElement.ownText().equals(lessE.mainElement.ownText())) {
                            hightE.createDifference(lessE.mainElement, 0);
                            System.out.println("here");
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
                        try {
                            lessE.setMainFromChildren(ind);
                            hightE.setMainFromChildren(ind);
                        } catch (Exception ex) {
                            if (hightE.getLastChildren().size() > lessE.getLastChildren().size()) {
                                hightE.setMainFromChildren(ind);
                                System.out.println(hightE.mainElement);
                                hightE.missingDiference(ind);
                                sort(hightE, ind);
                            }
                        }
                    }
                    System.out.println("here");
                }

                if (hightE.getLastChildren().size() > 1) {
                    if (lessE.getLastChildren().size() > 1) {
                        lessE.getLastChildren().remove(0);
                    }
                    hightE.getLastChildren().remove(0);
                }

                //Creating difference Html
                if (hightE.size() > 1) {

                    if (hightE.getFirstElement().children().size() > 0) {
                        difference.append(hightE.getFirstElement().child(0).clone().toString());
                        hightE.getFirstElement().child(0).remove();

                        if (lessE.getFirstElement().children().size() > 0) {
                            lessE.getFirstElement().child(0).remove();
                        }

                        if (hightE.get(1).size() > hightE.getFirstElement().children().size()) {
                            hightE.remove(1);
                            lessE.remove(1);
                        }
                    }
                } else {
                    difference.append(hightE.get(0).get(0).child(0).clone().toString());
                    hightE.remove(0);
                }
            }
        }

        createFile(tempPath + "/difference.html", difference.html());
        System.out.println("Soft compare done");
    }


    public void clearElement(LesserElements lessE){
        for (int re = 2; re < lessE.size(); re++) {
            for(int rem = lessE.get(re).size() - 1; rem >= 0; rem--) {
                lessE.get(re).remove(rem);
            }
            lessE.remove(re);
        }
    }

    //Put html tags in correct order (From middle position reverse tags)
    private void sort(LesserElements lessE, int position) {
        for (int k = position; k < lessE.getMainESize() - 1; k++) {
            lessE.mainElement.append(lessE.mainElement.clone().toString());
            lessE.getMainChild(position).remove();
        }
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
        for (int array = lessE.size() - 1; array > 0; array--) {
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
