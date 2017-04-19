/*
 *
 * Copyright (c) 2016, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: wangchaochao
 * Created: 2016/12/13
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.util;

import java.util.HashMap;
import java.util.Map;

public class TimeStatisticUtil {
    private static Map<String, TimeInfo> timeInfos = new HashMap<String, TimeInfo>();

    public static TimeInfo getTimeInof(String url) {
        if (timeInfos.containsKey(url)) {
            return timeInfos.get(url);
        }

        synchronized (timeInfos) {
            if (!timeInfos.containsKey(url)) {
                timeInfos.put(url, new TimeInfo(url));
            }
            return timeInfos.get(url);
        }
    }

    public static void removeByKey(String url) {
        synchronized (timeInfos) {
            if (!timeInfos.containsKey(url)) {
                return;
            } else {
                timeInfos.remove(url);
            }
        }
    }

    public static class TimeInfo {

        private long maxTime = -1;
        private long minTime = Long.MAX_VALUE;
        private long avrgTime = 0;

        private String url;

        public TimeInfo(String url) {
            this.url = url;
        }


        // 总时间数
        private long timeCount = 0;

        private long times = 0;

        public synchronized void addTime(long time) {
            times++;
            timeCount = timeCount + time;

            if (time > maxTime) {
                maxTime = time;
            }

            if (time < minTime) {
                minTime = time;
            }
        }

        @Override
        public String toString() {
            if (times <= 0) {
                return "no records!";
            }
            return "requetype:[" + url + "] maxTime:[" + maxTime + "] minTime:[" + minTime + "] avrgTime:["
                    + (timeCount / times) + "] times:[" + times + "].";
        }
    }
}
