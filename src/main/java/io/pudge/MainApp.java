package io.pudge;

import org.apache.camel.main.Main;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

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
    public static List<Video> availableVideoList = new ArrayList<>();
    public static List<Request> requestList = new ArrayList<>();

    public static final String fileName = "trending_today";

    public static Map<Integer, Map<Integer, Map<Integer, Double>>> cacheVideoEndpointMap = new HashMap<>();

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {


        loadInput( fileName + ".in");

        generateCacheVideoEndpointMap();

        Map<Integer, Map<Integer, Double>> sortedMap = sortVideoByRatio(cacheVideoEndpointMap);

        insertVideoInCache(sortedMap);

        printResult();

        printResultToFile();

    }


    public static Map<Integer, Map<Integer, Double>> sortVideoByRatio(Map<Integer, Map<Integer, Map<Integer, Double>>> cacheVideoEndpointMap) {


        Map<Integer, Map<Integer, Double>> sortedCacheVideoMap = new HashMap<>();


        for ( int cacheId : cacheVideoEndpointMap.keySet()  ) {



            Set<Integer> videoIdList = cacheVideoEndpointMap.get(cacheId).keySet();

            Map<Integer, Double> videoRatiosumMap = new HashMap<>();

            for ( int videoId : videoIdList ) {
                // map < EndPoint, Ratio >
                double ratioSumPerVideo = 0;
                Set<Integer> EndpointIdList = cacheVideoEndpointMap.get(cacheId).get(videoId).keySet();
                for (int EndpointId : EndpointIdList) {
                    ratioSumPerVideo += cacheVideoEndpointMap.get(cacheId).get(videoId).get(EndpointId);
                }
                videoRatiosumMap.put(videoId,ratioSumPerVideo);
            }
            sortByValue(videoRatiosumMap);

            sortedCacheVideoMap.put(cacheId, videoRatiosumMap );

        }

        return sortedCacheVideoMap;

    }


    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }


    public static void generateCacheVideoEndpointMap() {
        for (Request req : requestList) {
            int reqAccount = req.getAccount();
            Video video = req.getVideo();
            Endpoint endpoint = req.getEndpoint();
            if (video.getSize() <= CACHE_SIZE) {
                //Map<Integer, Double> endpointMap = new HashMap<>();
                // Map<Integer, Map<Integer, Double>> videoEndpointMap = new HashMap<>();
                for (Cache cache : cacheList) {
                    int endPointLatencyToCenter = endpoint.getLatencyToCenter();
                    if(endpoint.getLatencyToCacheList().containsKey(cache.getId())){
                        int endPointLatencyToCache = endpoint.getLatencyToCacheList().get(cache.getId());
                        int size = video.getSize();
                        Double value = 1.0 * reqAccount * (endPointLatencyToCenter - endPointLatencyToCache) / (size + 0.0);
                        Map<Integer, Map<Integer, Double>> videoEndpointMap;
                        if (cacheVideoEndpointMap.containsKey(cache.getId())) {
                            videoEndpointMap = cacheVideoEndpointMap.get(cache.getId());
                        } else {
                            videoEndpointMap = new HashMap<>();
                        }
                        Map<Integer, Double> endpointMap;
                        if (videoEndpointMap.containsKey(video.getId())) {
                            endpointMap = videoEndpointMap.get(video.getId());
                        } else {
                            endpointMap = new HashMap<>();
                        }
                        endpointMap.put(endpoint.getId(), value);
                        videoEndpointMap.put(video.getId(), endpointMap);
                        cacheVideoEndpointMap.put(cache.getId(), videoEndpointMap);
                    }
                }
            }
        }
    }


    private static void loadInput(String inputFileName) {
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
            while (cacheIndex < CACHE_ACCOUNT) {
                Cache cache = new Cache();
                cache.setId(cacheIndex);
                cache.setSize(CACHE_SIZE);
                cacheList.add(cache);
                cacheIndex++;
            }

            currentLine = br.readLine();
            values = currentLine.split(SEPERATOR);
            for (int i = 0; i < VIDEO_ACCOUNT; i++) {
                Video video = new Video();
                video.setId(i);
                video.setSize(Integer.parseInt(values[i]));
                videoList.add(video);
                if (video.getSize() <= CACHE_SIZE) {
                    availableVideoList.add(video);
                }
            }

            int currentEndpointIndex = 0;
            while (currentEndpointIndex < ENDPOINT_ACCOUNT) {
                currentLine = br.readLine();
                values = currentLine.split(SEPERATOR);
                Endpoint endpoint = new Endpoint();
                endpoint.setId(currentEndpointIndex);
                endpoint.setLatencyToCenter(Integer.parseInt(values[0]));
                int currentCacheIndex = 0;
                int connectedCacheAccount = Integer.parseInt(values[1]);
                while (currentCacheIndex < connectedCacheAccount) {
                    currentLine = br.readLine();
                    values = currentLine.split(SEPERATOR);
                    Cache cache = cacheList.get(Integer.parseInt(values[0]));
                    cache.getLatencyToEndpoint().put(endpoint.getId(), Integer.parseInt(values[1]));
                    currentCacheIndex++;
                    endpoint.getLatencyToCacheList().put(cache.getId(), Integer.parseInt(values[1]));
                }
                currentEndpointIndex++;

                endpointList.add(endpoint);
            }

            int currentRequestIndex = 0;
            while (currentRequestIndex < REQ_DESC_ACCOUNT) {
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

                currentRequestIndex++;
            }

            System.out.println("read done!!!");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while read line : " + e.getMessage());
        }
    }

    // insert video in cache
    /*
    public static void insertVideoInCache () {
        for (Cache c : cacheList) {
            for (Integer vId : cacheVideoEndpointMap.get(c.getId()).keySet())  {
                if (c.getSize() >= videoList.get(vId).getSize()) {
                    c.getVideoList().add(videoList.get(vId));
                    c.setSize(c.getSize()-videoList.get(vId).getSize());
                    if (c.getSize()==0) {break;}
                }
            }
        }
    }
    */

    // insert video in cache
    public static void insertVideoInCache(Map<Integer, Map<Integer, Double>> videoRatio) {
        for (Cache c : cacheList) {
            for (Integer vId : videoRatio.get(c.getId()).keySet()) {
                if (c.getSize() >= videoList.get(vId).getSize()) {
                    c.getVideoList().add(videoList.get(vId));
                    c.setSize(c.getSize() - videoList.get(vId).getSize());
                    if (c.getSize() == 0) {
                        break;
                    }
                }
            }
        }
    }


    public static void printResultToFile(){
        try{
            PrintWriter writer = new PrintWriter("result/" + fileName + ".out", "UTF-8");

            int n = 0;

            for (Cache c : cacheList) {
                if (c.getVideoList().size() > 0) {
                    c.printCacheVideoList();
                    n++;
                }
            }
            writer.println(n);
            for (Cache c : cacheList) {
                if (c.getVideoList().size() > 0) {
                    writer.println(c.getId());
                    List<Video> c_videoList = c.getVideoList();
                    int videoListSize = c_videoList.size();
                    for(int i = 0; i < videoListSize; i++) {
                        Video v = c_videoList.get(i);
                        writer.print(v.getId());
                        if( i < videoListSize - 1) {
                            writer.print(' ');
                        }
                    }
                    writer.println();
                    n++;
                }
            }
            writer.close();
        } catch (IOException e) {
            // do something
        }
    }


    public static void printResult() {
        // System.out.println(n);

        int n = 0;

        for (Cache c : cacheList) {
            if (c.getVideoList().size() > 0) {
                c.printCacheVideoList();
                n++;
            }
        }
        System.out.println(n);

    }

}

