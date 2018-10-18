package main.java;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class HighterElement extends LesserElement {

    HighterElement(Document diff) {
        super(diff);
    }

    public void createMultiDifference(Element newElement, int childIndex) {
        Element help = mainElement.clone();
        mainElement = mainElement.parent();
        mainElement.child(childIndex).remove();
        String text = "<" + help.tagName() + ">" + help.ownText() + "</" + help.tagName() + ">";
        appendChange(text);
        if (!newElement.ownText().isEmpty()) {
            mainElement.child(mainElement.children().size() - 1).append("<font class='FancyDiff' color='green'><" + newElement.tagName() + ">"
                    + newElement.ownText() + "</" + newElement.tagName() + "></font>");
        }
    }

    public void createMultiDifference(int childIndex) {
        Element help = mainElement.clone();
        mainElement = mainElement.parent();
        mainElement.child(childIndex).remove();
        String text = "<" + help.tagName() + ">" + help.ownText() + "</" + help.tagName() + ">";
        appendChange(text);
    }
}