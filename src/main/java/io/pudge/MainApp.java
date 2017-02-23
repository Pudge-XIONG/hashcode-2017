package io.pudge;

import org.apache.camel.main.Main;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A Camel Application
 */
public class MainApp {


    private static final String SEPERATOR = " ";
    public static int VIDEO_ACCOUNT;
    public static int ENDPOINT_ACCOUNT;
    public static int REQ_DESC_ACCOUNT;
    public static int CACHE_ACCOUNT;
    public static int CACHE_SIZE;


    public static List<Cache> cacheList = new ArrayList<>();
    public static List<Endpoint> endpointList = new ArrayList<>();
    public static List<Video> videoList = new ArrayList<>();
    public static List<Request> requestList = new ArrayList<>();

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {

        loadInput("example.in");

    }



    private static void loadInput(String inputFileName){
        try {
            InputStream fis = new FileInputStream(inputFileName);
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);

            String currentLine = br.readLine();
            String[] values = currentLine.split(SEPERATOR);
            VIDEO_ACCOUNT = Integer.parseInt(values[0]);
            ENDPOINT_ACCOUNT = Integer.parseInt(values[1]);
            REQ_DESC_ACCOUNT = Integer.parseInt(values[2]);
            CACHE_ACCOUNT = Integer.parseInt(values[3]);
            CACHE_SIZE = Integer.parseInt(values[4]);

            int cacheIndex = 0;
            while(cacheIndex < CACHE_ACCOUNT){
                Cache cache = new Cache();
                cache.setId(cacheIndex);
                cache.setSize(CACHE_SIZE);
                cacheList.add(cache);
                cacheIndex++;
            }

            currentLine = br.readLine();
            values = currentLine.split(SEPERATOR);
            for(int i = 0; i < VIDEO_ACCOUNT; i++){
                Video video = new Video();
                video.setId(i);
                video.setSize(Integer.parseInt(values[i]));
                videoList.add(video);
            }

            int currentEndpointIndex = 0;
            while(currentEndpointIndex < ENDPOINT_ACCOUNT){
                currentLine = br.readLine();
                values = currentLine.split(SEPERATOR);
                Endpoint endpoint = new Endpoint();
                endpoint.setId(currentEndpointIndex);
                endpoint.setLatencyToCenter(Integer.parseInt(values[0]));
                int currentCacheIndex = 0;
                int connectedCacheAccount = Integer.parseInt(values[1]);
                while(currentCacheIndex < connectedCacheAccount){
                    currentLine = br.readLine();
                    values = currentLine.split(SEPERATOR);
                    Cache cache = cacheList.get(Integer.parseInt(values[0]));
                    cache.getLatencyToEndpoint().put(endpoint.getId(), Integer.parseInt(values[1]));
                    currentCacheIndex ++;
                    endpoint.getLatencyToCacheList().put(cache.getId(), Integer.parseInt(values[1]));
                }
                currentEndpointIndex ++;

                endpointList.add(endpoint);
            }

            int currentRequestIndex = 0;
            while(currentRequestIndex < REQ_DESC_ACCOUNT){
                currentLine = br.readLine();
                values = currentLine.split(SEPERATOR);
                int videoId = Integer.parseInt(values[0]);
                int endPointId = Integer.parseInt(values[1]);
                int requestAccount = Integer.parseInt(values[2]);

                Request request = new Request();
                request.setId(currentRequestIndex);
                request.setAccount(requestAccount);
                request.setVideo(videoList.get(videoId));
                request.setEndpoint(endpointList.get(endPointId));

                requestList.add(request);

                currentRequestIndex ++;
            }

            System.out.println("read done!!!");

        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Error while read line : " + e.getMessage());
        }
    }


}

