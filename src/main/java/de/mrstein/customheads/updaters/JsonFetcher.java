package de.mrstein.customheads.updaters;

/*
 *  Project: CustomHeads in JsonFetcher
 *     by LikeWhat
 *
 *  created on 13.04.2019 at 00:13
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.mrstein.customheads.CustomHeads;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

@Getter
public class JsonFetcher {

    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko";
    private static final JsonParser PARSER = new JsonParser();

    private String url;
    private Proxy proxy;
    private String userAgent;

    public JsonFetcher(String url) {
        userAgent = DEFAULT_USER_AGENT;
        this.url = url;
    }

    public JsonFetcher useProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public JsonFetcher useAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public void fetch(FetchResult<JsonElement> fetchResult) {
        new BukkitRunnable() {
            public void run() {
                try {
                    URL urlInst = new URL(url);
                    JsonElement parsedData;
                    if (url.startsWith("https:")) {
                        HttpsURLConnection connection;
                        if (proxy == null) {
                            connection = (HttpsURLConnection) urlInst.openConnection();
                        } else {
                            connection = (HttpsURLConnection) urlInst.openConnection(proxy);
                        }
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            fetchResult.error(new Exception("Server returned Code " + connection.getResponseCode()));
                            return;
                        }
                        parsedData = PARSER.parse(new InputStreamReader(connection.getInputStream()));
                    } else {
                        HttpURLConnection connection;
                        if (proxy == null) {
                            connection = (HttpURLConnection) urlInst.openConnection();
                        } else {
                            connection = (HttpURLConnection) urlInst.openConnection(proxy);
                        }
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            fetchResult.error(new Exception("Server returned Code " + connection.getResponseCode()));
                            return;
                        }
                        parsedData = PARSER.parse(new InputStreamReader(connection.getInputStream()));
                    }
                    if (parsedData == null) {
                        fetchResult.error(new NullPointerException("Parsed Data is null"));
                        return;
                    }
                    fetchResult.success(parsedData);
                } catch (Exception e) {
                    fetchResult.error(e);
                }
            }
        }.runTaskAsynchronously(CustomHeads.getInstance());
    }

}
