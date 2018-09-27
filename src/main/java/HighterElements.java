package main.java;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class HighterElements extends LesserElements {

    HighterElements(Document diff) {
        super(diff);
    }

    public void createDifference(Element newElement, int childIndex){
        super.getLastChildren().remove(childIndex);
        Element help = mainElement.clone();
        mainElement = mainElement.parent();
        mainElement.child(childIndex).remove();
        String text = "<" + help.tagName() + ">[" + help.ownText() + "]</" + help.tagName() + ">";
        appendChange(text);
        if (!newElement.ownText().isEmpty()) {
            mainElement.append("<font class='FancyDiff' color='green'><" + newElement.tagName() + ">["
                    + newElement.ownText() + "]</" + newElement.tagName() + "></font>");
        }
        mainElement = mainElement.child(0);
    }

    public void missingDifference(int childIndex){
        Element help = super.getLastChildren().get(childIndex).clone();
        mainElement = mainElement.parent();
        super.getLastChildren().remove(childIndex);
        mainElement.child(childIndex).remove();
        appendChange(help.toString());
    }
}