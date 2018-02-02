package mike.young.rxalexa.domain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FullSite {
    private URL url;
    private List<String> headers;
    private String content;

    public FullSite(String url) throws MalformedURLException {
        this.url = new URL("http://" + url);
        this.headers = new ArrayList<>();
        this.content = "";
    }

    public FullSite(String urlStr, String content, List<String> headers) throws MalformedURLException {
        this.url = new URL(urlStr);
        this.headers = headers;
        this.content = content;
    }

    @Override
    public String toString() {
        return this.url.toExternalForm() + " - " + this.content.length() + " - " + this.headers.size();
    }

    public Integer getWordCount(){
        return content.length();
    }

    public List<String> getHeaders(){
        return this.headers;
    }
}
