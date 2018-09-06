package main.java;

public class Nothing {

                /*

                                int oldSize = oldElements.children().size();
                int newSize = newElements.children().size();
                int elementsSize = ( newSize < oldSize  ? newSize : oldSize);

                    oldElements = oldElements.parent();
                oldElements.child(0).remove();


                try {

            int oldSize = oldDiff.body().getAllElements().size();

            int newSize = newDiff.body().getAllElements().size();

             if(!element.tagName().equals("body") && !element.tagName().equals("head")) {
                            if (element.hasText()) {
                                difference.body().append(element.toString());
                            } else {
                                String tag = element.tagName();
                                String tags = "<" + tag + ">" + "</" + tag + ">";
                                if(oldElements.parents().size() > 3) {
                                    difference.body().wrap(tags);
                                }else{
                                    difference.body().after(tags);
                                }
                            }
                        }

                try {

                    for (int textIndex = 1; textIndex < elementsSize; textIndex++) {

                        try {
                            Element newElements = newDiff.body().getAllElements().get(textIndex);
                            Element oldElements = oldDiff.body().getAllElements().get(textIndex);

                            if (newDiv2.html().equals(oldDiv2.html())) {
                                if (difference != null) {
                                    difference = Jsoup.parse(difference.html() + "<p>\n</p>" + newDiv2.html()+ "<p>\n</p>");
                                } else {
                                    difference = Jsoup.parse(newDiv2.html());
                                }
                            } else {
                                Document removed = editDiffText(Jsoup.parse(oldDiv2.html()), "removed");
                                Document created = editDiffText(Jsoup.parse(newDiv2.html()), "created");
                                if (difference != null) {
                                    difference = Jsoup.parse(difference.html() + "<p>\n</p>" + removed.html() +
                                            "<p>\n</p>" + created.html() + "<p>\n</p>");
                                } else {
                                    difference = Jsoup.parse(removed.html() + "<p>\n</p>" + created.html() + "<p>\n</p>");
                                }
                            }
                        } catch (IndexOutOfBoundsException ignore) {
                            try {
                                Element newDiv2 = newDiv.body().getAllElements().get(textIndex);
                                Document created = editDiffText(Jsoup.parse(newDiv2.html()), "created");
                                if (difference != null) {
                                    difference = Jsoup.parse(difference.html() + "<p>\n</p>" + created.html() + "<p>\n</p>");
                                } else {
                                    difference = Jsoup.parse(created.html() + "<p>\n</p>");
                                }
                            } catch (IndexOutOfBoundsException ignore2) {
                                Element oldDiv2 = oldDiv.body().getAllElements().get(textIndex);
                                Document removed = editDiffText(Jsoup.parse(oldDiv2.html()), "removed");
                                if (difference != null) {
                                    difference = Jsoup.parse(difference.html() + "<p>\n</p>" + removed.html() + "<p>\n</p>");
                                } else {
                                    difference = Jsoup.parse(removed.html() + "<p>\n</p>");
                                }
                            }
                        }
                    }
                } catch (NullPointerException ignore) {
                    try {
                        oldDiv.getAllElements();
                        oldDiv = editDiffText(oldDiv, "removed");
                        difference = Jsoup.parse(oldDiv.html());
                    } catch (NullPointerException ignore1) {
                        if(newDiv != null) {
                            newDiv = editDiffText(newDiv, "created");
                            difference = Jsoup.parse(newDiv.html());
                        }
                    }
                }

                if(difference != null) {
                    createFile(tempPath + "/difference" + i + ".html", difference.html());
                    difference = null;
                }

            } catch (IOException ignore) {
            }*/
}
