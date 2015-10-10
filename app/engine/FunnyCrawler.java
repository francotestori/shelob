package engine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
public class FunnyCrawler {

    private static PrintWriter writer;


    public static void createTextFile(String fileName) throws FileNotFoundException {
        writer = new PrintWriter(fileName);
    }
    public static void searchLinkedinUrl(String searchName) throws UnsupportedEncodingException, InterruptedException {

        if (searchName != null) {

            String[] nameSplit = searchName.split(" ");
            String searcher = "linkedin";

            int i = 0;
            while (i != nameSplit.length) {
                searcher = searcher + "%20" + nameSplit[i];
                i++;
            }
            Set<String> result = getDataFromGoogle(searcher);

            writer.println("ANALIZANDO USUARIO URL VACIO: ");
            writer.println(searchName);
            for(String temp : result){
                writer.println(temp);
            }
            writer.println(result.size());
            writer.println("");
        }
    }

    public static void closeWriter() { writer.close(); }

    public static String getDomainName(String url){

        String domainName = "";
        String[] domainNameSplitByAmper;
        String[] domainNameSplitByPerc;

        //Elimino las url que tengan busqueda de usuarios
        if (!url.contains("pub/dir/")) {

            //Borro los caracteres '/url?q='
            domainName = url.substring(7);

            //Limpio los parámetros pasados por url
            if (domainName.contains("&")) {
                domainNameSplitByAmper = domainName.split("&");
                domainName = domainNameSplitByAmper[0];
            }
            if (domainName.contains("%")) {
                domainNameSplitByPerc = domainName.split("%");
                domainName = domainNameSplitByPerc[0];
            }
        }

        return domainName;
    }

    public static Set<String> getDataFromGoogle(String query) {

        Set<String> result = new HashSet<String>();
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
                    temp = getDomainName(temp);
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