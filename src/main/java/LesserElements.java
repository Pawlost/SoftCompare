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

     public void changeMainElement() {
         changeMainElement(getMainESize() - 1);
     }

     public Element getFirstElement(){
         return get(0).get(0).child(0);
     }


     public void setMainFromChildren(int childIndex) throws IndexOutOfBoundsException{
         mainElement = getLastChildren().get(childIndex);
     }

     public void changeMainElement(int childIndex) {
         mainElement = getMainChild(childIndex);
     }


     public void addChildren() {
         super.add((ArrayList<Element>) children.clone());
         children = new ArrayList<>();
     }

     public void removeAll() {
         for (int i = 0; i < super.size(); i++) {
             super.remove(i);
         }
     }

     public int getMainESize() {
         return mainElement.children().size();
     }

     public String getHTML() {
         String html = super.get(0).get(0).clone().toString();
         removeAll();
         return html;
     }

     public Element getMainChild(int index) {
         return mainElement.child(index);
     }

     public ArrayList<Element> getLastChildren() {
         return get(size() - 1);
     }
 }
