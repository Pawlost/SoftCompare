package main.java;

public class Nothing {

             /*
                        if (oldA.size() > 0 && newA.size() > 0) {
                            System.out.println("here");
                            for (int in = 0; in < newA.size(); in++) {
                                if (!oldA.get(in).tagName().equals(newA.get(in).tagName())) {
                                    Element help = oldA.get(in);
                                    oldA.get(in).append("<font color='red'>[" + help.tagName() + "][" + help.ownText()
                                            + "][" + help.tagName() + "]</font>");
                                   // oldA.get(in).parent().child(0).remove();
                                    System.out.println("ok pico1");
                                }

                                if (!oldA.get(in).ownText().equals(newA.get(in).ownText())) {
                                    Element help = oldA.get(in);
                                    oldA.get(in).append("<" + help.tagName() + "><font color='red'>[" + help.ownText()
                                            + "]</font>" + "</" + help.tagName() + ">");
                                  //  oldA.get(in).parent().child(0).remove();
                                    System.out.println("ok pico2");
                                }
                            }
                        } else if (oldA.size() > 0 && newA.size() > 0) {
                            if (!oldA.get(0).tagName().equals(newA.get(0).tagName())) {
                                Element help = oldA.get(0).clone();
                                oldA.get(0).remove();
                                oldA.get(0).append("<font color='red'>[" + help.tagName() + "][" + help.ownText() + "][" + help.tagName()
                                        + "</font>");
                                System.out.println("ok pico3");
                            }

                            if (!oldA.get(0).ownText().equals(newA.get(0).ownText())) {
                                Element help = oldA.get(0).clone();
                                oldA.get(0).remove();
                                oldA.get(0).append("<" + help.tagName() + "><font color='red'>[" + help.ownText() + "]</font>" +
                                        "</" + help.tagName() + ">");
                                System.out.println("ok pico4");
                            }
                        }*/


                /*

                                int oldSize = oldElements.children().size();
                int newSize = newElements.children().size();
                int elementsSize = ( newSize < oldSize  ? newSize : oldSize);

                    oldElements = oldElements.parent();
                oldElements.child(0).remove();


                try {

            ize();

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
