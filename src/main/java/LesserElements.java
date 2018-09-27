package main.java;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

 public class LesserElements extends ArrayList<ArrayList<Element>> {

     private ArrayList<Element> children;
     protected Element mainElement;

     LesserElements(Document diff) {
         children = new ArrayList<>();
         children.add(diff.body());
         addChildren();
         mainElement = getFirstElement();
     }

     public void appendChange(String text) {
         mainElement.append("<font class='FancyDiff' color='red'>" + text + "</font>");
     }

     public void setMainLast(int childIndex) throws IndexOutOfBoundsException{
         mainElement = getLastChildren().get(childIndex);
     }

     public void removeAllLast(){
         while(!getLastChildren().isEmpty()){
            getLastChildren().remove(0);
         }
     }

     public void changeMainElement() {
         mainElement = getMainChild(0);
     }

     public void changeMainElement(Element change) {
        mainElement = change;
     }

     public Element getFirstElement(){
         return get(0).get(0);
     }

     public void addChildren() {
         super.add((ArrayList<Element>) children.clone());
         children = new ArrayList<>();
     }

     public void addChildren(Element child) {
         children.add(child);
     }

     public int getMainESize() {
         return mainElement.children().size();
     }

     public Element getMainChild(int index) {
         return mainElement.child(index);
     }

     public ArrayList<Element> getLastChildren() {
         return get(size() - 1);
     }
 }
