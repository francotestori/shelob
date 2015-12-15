package engine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.SocketFactory;

/**
 * Created by lucas on 05/10/15.
 */
public class GoogleSearcher {

    private static boolean isTorRunning = false;

    public static String searchLinkedinUrl(String searchName, String startup, String role)
            throws UnsupportedEncodingException, InterruptedException {

        if (searchName != null && isTorRunning) {

            String[] nameSplit = searchName.split(" ");
            String searcher = "linkedIn";

            //Limpio los nombres que tienen comas u otro caracter
            for (int i = 0; i < nameSplit.length - 1; i++) {
                if (!Character.isLetter(nameSplit[i].charAt(0)))
                    nameSplit[i] = nameSplit[i].substring(1).trim();
                else if (!Character.isLetter(nameSplit[i].charAt(nameSplit[i].length() - 1)))
                    nameSplit[i] = nameSplit[i].substring(0, nameSplit[i].length() - 1).trim();
            }

            int i = 0;
            while (i != nameSplit.length) {
                searcher = searcher + "%20" + nameSplit[i];
                i++;
            }
            if (startup != null)
                searcher = searcher + "%20" + startup;
            if (role != null)
                searcher = searcher + "%20" + role;

            ArrayList<String> result = getDataFromGoogle(nameSplit, searcher);

            return selectCorrectURL(nameSplit, result);
        }
        return "";
    }

    public static void printTime() {
        java.util.Date date= new java.util.Date();
        System.out.println("#####" + new Timestamp(date.getTime()));
    }

    public static void startTorClient() throws InterruptedException {
        System.setProperty("socksProxyHost", "localhost");
        System.setProperty("socksProxyPort", "9050");
        Thread.sleep(1000);
        isTorRunning = true;
    }

    public static void stopTorClient() {
        System.clearProperty("socksProxyHost");
        System.clearProperty("socksProxyPort");
        isTorRunning = false;
    }

    private static ArrayList<String> getDataFromGoogle(String[] name, String query) throws InterruptedException {

        ArrayList<String> result = new ArrayList<>();
        String request = "https://www.google.com.ar/search?q=" + query + "&num=10";

        try {
            // need http protocol, set this as a Google bot agent :)
            Document doc = Jsoup
                    .connect(request)
                    .userAgent(
                            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                    .timeout(50000).get();

            // get all links
            Elements links = doc.select("a[href*=linkedin]");
            for (Element link : links) {

                String temp = link.attr("href");
                if(temp.startsWith("/url?q=")){
                    //use regex to get domain name
                    temp = cleanDomain(temp);
                    if (temp != null && !temp.equals(""))
                        result.add(temp);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            isTorRunning = false;
        } catch (IOException e) {
            if (e.getMessage().equals("HTTP error fetching URL")) {
//                Thread.sleep(1000);
//                getDataFromGoogle(name, query);
            }
        }
        return result;
    }

    private static String cleanDomain(String url){

        String domainName = "";
        String[] domainNameSplitByAmper;

        //Elimino las url que tengan busqueda de usuarios
        if (!url.contains("pub/dir/")) {

            //Borro los caracteres '/url?q='
            domainName = url.substring(7);

            //Limpio los parámetros pasados por url
            if (domainName.contains("&")) {
                domainNameSplitByAmper = domainName.split("&");
                domainName = domainNameSplitByAmper[0];
            }
        }
        return domainName;
    }

    private static String selectCorrectURL(String[] userName, ArrayList<String> manyURLs) {
        String url = "";
        ArrayList<String> possibleAnswers = new ArrayList<>();

        if (!manyURLs.isEmpty()) {

            //Como generalmente el primer resultado es el correcto, lo analizo por separado
            if (isCorrect(userName, manyURLs.get(0)))
                url = manyURLs.get(0);
            else {
                for (String anURL : manyURLs) {
                    if (anURL.substring(8).startsWith("www.linkedin.com")
                            || anURL.substring(11).startsWith("linkedin.com")) {
                        String[] splitURL = anURL.split("/");
                        if (splitURL.length >= 5) {
                            String nameOnURL = splitURL[4];
                            if (nameOnURL.equalsIgnoreCase(String.join("", userName))) {
                                url = anURL;
                                break;
                            } else if (nameOnURL.equalsIgnoreCase(String.join("-", userName))) {
                                url = anURL;
                                break;
                            }
//                        else {
//                            int i = 0;
//                            while (i != userName.length) {
//                                if (nameOnURL.contains(userName[i].toLowerCase())) {
//                                    possibleAnswers.add(anURL);
//                                }
//                                i++;
//                            }
//                        }
                        }
                    }
                }
            }
        }
//        if (!possibleAnswers.isEmpty()) {
//            for (String answer : possibleAnswers) {
//                System.out.println(answer);
//            }
//        }
        return url;
    }

    private static boolean isCorrect(String[] userName, String domain) {
        boolean isCorrect = false;

        if (domain.substring(8).startsWith("www.linkedin.com") || domain.substring(11).startsWith("linkedin.com")) {
            String[] splitURL = domain.split("/");
            if (splitURL.length >= 5) {
                String nameOnURL = splitURL[4];

                if (nameOnURL.equalsIgnoreCase(String.join("", userName))) {
                    isCorrect = true;
                } else if (nameOnURL.equalsIgnoreCase(String.join("-", userName))) {
                    isCorrect = true;
                } else {
                    int i = 0;
                    while (i != userName.length) {
                        if (nameOnURL.contains(userName[i].toLowerCase())) {

                            if (userName.length >= 2 && userName[1].length() != 0) {
                                //Me fijo si en vez del nombre completo solo está una parte de el.
                                //Primero miro si está el nombre y algunas letras del apellido
                                if (i != (userName.length - 1) && nameOnURL.contains("" + userName[i + 1].toLowerCase().charAt(0))) {
                                    if (nameOnURL.contains(userName[i + 1].toLowerCase())) {
                                        if (nameOnURL.length() < (userName[i].length() + userName[i + 1].length() + 3))
                                            isCorrect = true;
                                    }
                                    else if (nameOnURL.length() < (userName[i].length() + 5))
                                        isCorrect = true;
                                }
                                //Luego miro si en vez, está el apellido y algunas letras del nombre
                                else if (i != 0 && nameOnURL.contains("" + userName[i - 1].toLowerCase().charAt(0))) {
                                    if (nameOnURL.length() < (userName[i].length() + 5))
                                        isCorrect = true;
                                }
                            }
                        }
                        i++;
                    }
                }
            }
        }
        return isCorrect;
    }
}