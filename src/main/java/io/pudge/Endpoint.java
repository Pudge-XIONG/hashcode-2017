package io.pudge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xq on 23/02/2017.
 */
public class Endpoint {
    int id;
    Map<Integer, Integer> latencyToCacheList = new HashMap<>();

    int latencyToCenter;

    List<Video> videoList = new ArrayList<>();

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, Integer> getLatencyToCacheList() {
        return latencyToCacheList;
    }

    public void setLatencyToCacheList(Map<Integer, Integer> latencyToCacheList) {
        this.latencyToCacheList = latencyToCacheList;
    }

    public int getLatencyToCenter() {
        return latencyToCenter;
    }

    public void setLatencyToCenter(int latencyToCenter) {
        this.latencyToCenter = latencyToCenter;
    }
}
