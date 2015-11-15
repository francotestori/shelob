package engine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.tototoshi.csv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by lucas on 05/10/15.
 */
public class GoogleSearcher {

    private static PrintWriter writer;


    public static void createTextFile(String fileName) throws FileNotFoundException {
        writer = new PrintWriter(fileName);
        writer.println("ANALIZANDO USUARIO URL VACIO: ");
    }
    public static void searchLinkedinUrl(String searchName) throws UnsupportedEncodingException, InterruptedException {

        if (searchName != null) {

            String[] nameSplit = searchName.split(" ");
            for (int i = 0; i < nameSplit.length - 1; i++) {
                if (!Character.isLetter(nameSplit[i].charAt(0)))
                    nameSplit[i] = nameSplit[i].substring(1);
                else if (!Character.isLetter(nameSplit[i].charAt(nameSplit[i].length() - 1)))
                    nameSplit[i] = nameSplit[i].substring(0, nameSplit[i].length() - 1);
            }
            String searcher = "linkedIn";

            int i = 0;
            while (i != nameSplit.length) {
                searcher = searcher + "%20" + nameSplit[i];
                i++;
            }
            ArrayList<String> result = getDataFromGoogle(nameSplit, searcher);

            String finalURL = selectCorrectURL(nameSplit, result);

//            for (String a : result) {
//                writer.println(searchName + " - " + a);
//            }

            writer.println(searchName + " - " + finalURL);
        }
    }

    public static void closeWriter() {
        writer.close();
        System.getProperties().put("proxyHost", "");
        System.getProperties().put("proxyPort", "");
    }

    public static String selectCorrectURL(String[] userName, ArrayList<String> manyURLs) {
        String url = "";
        ArrayList<String> possibleAnswers = new ArrayList<>();

        if (!manyURLs.isEmpty()) {
            if (isCorrect(userName, manyURLs.get(0)))
                url = manyURLs.get(0);
            else {
                for (String anURL : manyURLs) {
                    if (anURL.substring(8).startsWith("www.linkedin.com")
                            || anURL.substring(11).startsWith("linkedin.com")) {
                        String[] splitURL = anURL.split("/");
                        String nameOnURL = splitURL[4];
                        if (nameOnURL.equalsIgnoreCase(String.join("", userName))) {
                            url = anURL;
                            break;
                        } else if (nameOnURL.equalsIgnoreCase(String.join("-", userName))) {
                            url = anURL;
                            break;
                        } else {
                            int i = 0;
                            while (i != userName.length) {
                                if (nameOnURL.contains(userName[i].toLowerCase())) {
                                    possibleAnswers.add(anURL);
                                }
                                i++;
                            }
                        }
                    }
                }
            }
        }
        if (!possibleAnswers.isEmpty()) {
            System.out.println(String.join(" ", userName));
            for (String answer : possibleAnswers) {
                System.out.println(answer);
            }
        }

        return url;
    }

    private static boolean isCorrect(String[] userName, String domain) {
        boolean isCorrect = false;
        String[] splitURL = domain.split("/");
        String nameOnURL = splitURL[4];

        if (domain.substring(8).startsWith("www.linkedin.com") || domain.substring(11).startsWith("linkedin.com")) {
            if (nameOnURL.equalsIgnoreCase(String.join("", userName))) {
                isCorrect = true;
            } else if (nameOnURL.equalsIgnoreCase(String.join("-", userName))) {
                isCorrect = true;
            } else {
                int i = 0;
                while (i != userName.length) {
                    if (nameOnURL.contains(userName[i].toLowerCase())) {

                        //ACA MIRO SI EN VEZ DEL NOMBRE ENTERO ESTA SOLO UNA PARTE...
                        //MIRO EL NOMBRE Y LA PRIMER LETRA DEL APELLIDO
                        if (i != (userName.length - 1) && nameOnURL.contains("" + userName[i + 1].toLowerCase().charAt(0))) {
                            if (nameOnURL.contains(userName[i + 1].toLowerCase())) {
                                if (nameOnURL.length() < (userName[i].length() + userName[i + 1].length() + 3))
                                    isCorrect = true;
                            }
                            else if (nameOnURL.length() < (userName[i].length() + 5))
                                isCorrect = true;
                        }
                        //MIRO EL APELLIDO Y LA PRIMER LETRA DEL NOMBRE
                        else if (i != 0 && nameOnURL.contains("" + userName[i - 1].toLowerCase().charAt(0))) {
                            if (nameOnURL.length() < (userName[i].length() + 5))
                                isCorrect = true;
                        }
                    }
                    i++;
                }
            }
        }
        return isCorrect;
    }

    public static String getDomainName(String[] name, String url){

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

    public static ArrayList<String> getDataFromGoogle(String[] name, String query) {

        ArrayList<String> result = new ArrayList<>();
        String request = "https://www.google.com.ar/search?q=" + query + "&num=10";

        try {

            System.setProperty("socksProxyHost", "localhost");
            System.setProperty("socksProxyPort", "9050");

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
                    temp = getDomainName(name, temp);
                    if (temp != null && !temp.equals(""))
                        result.add(temp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}