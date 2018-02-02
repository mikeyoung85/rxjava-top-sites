package mike.young.rxalexa;

import mike.young.rxalexa.domain.Count;
import mike.young.rxalexa.domain.FullSite;
import mike.young.rxalexa.service.AlexaDataService;
import org.xml.sax.SAXException;
import rx.Observable;
import rx.math.operators.OperatorAverageDouble;
import rx.observables.MathObservable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class AlexaSites {
    private static AlexaDataService alexaDataService;

    public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        System.out.println("hello");
        alexaDataService = new AlexaDataService();

        Observable<FullSite> allSites = alexaDataService.findAllSites()
                .flatMap(url -> fillSiteData(url));

        findAverageWordCount(allSites).subscribe(count -> {
            System.out.println("Average word count: " + count);
        });

        findTopHeaders(allSites)
                .toSortedList()
                .subscribe(list -> {
                    System.out.println("Top 20 Headers");
                    System.out.println(list.subList(0, 19));
                });

    }

    public static Observable<FullSite> fillSiteData(String urlStr) {
        List<String> headers = new ArrayList<>();
        String content;
        HttpURLConnection conn = null;
        try{
            URL url = new URL("http://" + urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setInstanceFollowRedirects(true);

            int status = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer siteContent = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                siteContent.append(inputLine);
            }
            content = siteContent.toString();

            for(Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()){
                if(entry.getKey() != null) {
                    headers.add(entry.getKey());
                }
            }
            in.close();

            return Observable.just(new FullSite(url.toExternalForm(), content, headers));
        }catch (Exception e){
            e.printStackTrace();
            return Observable.empty();
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static Observable<Integer> findAverageWordCount(Observable<FullSite> allSites){
        Observable<Integer> wordCountObv = allSites
                .map(fullSite -> fullSite.getWordCount());

        return MathObservable.from(wordCountObv).averageInteger(i -> i);
    }

    public static Observable<Count> findTopHeaders(Observable<FullSite> allSites){
        Observable<Count> headers = allSites
                .map(site -> site.getHeaders())
                .flatMapIterable(header -> header)
                .groupBy(str -> str.toLowerCase())
                .flatMap(
                        gr -> gr.count()
                        .map(count -> new Count(gr.getKey(), count))
                );

        return headers;
    }
}
