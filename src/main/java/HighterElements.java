package main.java;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class HighterElements extends LesserElements {

    HighterElements(Document diff) {
        super(diff);
    }

    public void createDifference(Element newElement){
        Element help = mainElement.clone();
        mainElement = mainElement.parent();
        mainElement.child(0).remove();
        String text = "<" + help.tagName() + ">[" + help.ownText() + "]</" + help.tagName() + ">";
        appendChange(text);
        if (!newElement.ownText().isEmpty()) {
            newElement.append("<font class='FancyDiff' color='green'><" + newElement.tagName() + ">["
                    + newElement.ownText() + "]</" + newElement.tagName() + "></font>");
        }
        mainElement = mainElement.child(0);
    }
}