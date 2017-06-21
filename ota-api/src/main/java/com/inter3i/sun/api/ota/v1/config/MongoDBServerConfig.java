/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2017/04/21
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.config;

import com.inter3i.sun.api.ota.v1.net.HttpUtils;
import com.inter3i.sun.api.ota.v1.util.ValidateUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/*@Configuration
@ConfigurationProperties(prefix = "di")
*//*@PropertySource("classpath:mongoDBServer.properties")*//*
@PropertySource("file:D:/tmp/config/mongoDBServer.properties")*/
public class MongoDBServerConfig {
    private static Resource resource = new ClassPathResource("/mongoDBServer.properties");

    public static MongoDBServerConfig getConfig() {

        synchronized (MongoDBServerConfig.class) {

            //初始化该配置类
            MongoDBServerConfig tmp = null;
            try {
                tmp = new MongoDBServerConfig();
                Properties props = PropertiesLoaderUtils.loadProperties(resource);
                Iterator it = props.keySet().iterator();
                String key;
                while (it.hasNext()) {
                    key = (String) it.next();

                    switch (key){
                        case "di.nlpServerIp":
                            tmp.setNlpServerIp((String) props.get(key));
                            break;
                        case "di.nlpServerPort":
                            tmp.setNlpServerPort(Integer.valueOf((String) props.get(key)));
                            break;
                        case "di.nlpReqPath":
                            tmp.setNlpReqPath((String) props.get(key));
                            break;
                        case "di.nlpReqTimeOut":
                            tmp.setNlpReqTimeOut(Integer.valueOf((String) props.get(key)));
                            break;
                        case "di.webServerIp":
                            tmp.setWebServerIp((String) props.get(key));
                            break;
                        case "di.importPath":
                            tmp.setImportPath((String) props.get(key));
                            break;
                        case "di.flushPath":
                            tmp.setFlushPath((String) props.get(key));
                            break;
                        case "di.supplyPath":
                            tmp.setSupplyPath((String) props.get(key));
                            break;
                        case "di.docNumPerImport":
                            tmp.setDocNumPerImport(Integer.valueOf((String) props.get(key)));
                            break;
                        case "di.importNumPerFlush":
                            tmp.setImportNumPerFlush(Integer.valueOf((String) props.get(key)));
                            break;
                    }

                    if (key.startsWith("di.cachePortMap[")) {
                        tmp.add4CachePortMap(getKeyIn(key), Integer.valueOf((String) props.get(key)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tmp;
        }
    }

    //获取di.cacheTableMap[cache01] 中的[] 中的key
    private static String getKeyIn(String key) {
        return key.substring(key.indexOf("[") + 1, key.length() - 1);
    }

    private static String importPath;
    private static String flushPath;
    private static String supplyPath;

    private Map<String, String> cacheInfos = new HashMap<>(4);

    private String nlpServerIp;
    private int nlpServerPort;
    private String nlpReqPath;
    private int nlpReqTimeOut;


    private String webServerIp;
    private int docNumPerImport;
    private int importNumPerFlush;
    private Map<String, Integer> cachePortMap = new HashMap<>(4);



    public String getNlpServerIp() {
        return nlpServerIp;
    }

    public void setNlpServerIp(String nlpServerIp) {
        this.nlpServerIp = nlpServerIp;
    }

    public int getNlpServerPort() {
        return nlpServerPort;
    }

    public void setNlpServerPort(int nlpServerPort) {
        this.nlpServerPort = nlpServerPort;
    }

    public String getNlpReqPath() {
        return nlpReqPath;
    }

    public void setNlpReqPath(String nlpReqPath) {
        this.nlpReqPath = nlpReqPath;
    }

    public int getNlpReqTimeOut() {
        return nlpReqTimeOut;
    }

    public void setNlpReqTimeOut(int nlpReqTimeOut) {
        this.nlpReqTimeOut = nlpReqTimeOut;
    }

    public String getNLPServerReqURL() {
        return HttpUtils.HTTP_PROTOCAL_PREFIX + nlpServerIp + ":" + nlpServerPort + nlpReqPath;
    }

    //********************  入库配置 ***********************//

    public String getWebServerIp() {
        return webServerIp;
    }

    public void setWebServerIp(String webServerIp) {
        this.webServerIp = webServerIp;
    }

    public int getDocNumPerImport() {
        return docNumPerImport;
    }

    public void setDocNumPerImport(int docNumPerImport) {
        this.docNumPerImport = docNumPerImport;
    }

    public int getImportNumPerFlush() {
        return importNumPerFlush;
    }

    public void setImportNumPerFlush(int importNumPerFlush) {
        this.importNumPerFlush = importNumPerFlush;
    }

    public Map<String, Integer> getCachePortMap() {
        return cachePortMap;
    }

    public void setCachePortMap(Map<String, Integer> cachePortMap) {
        this.cachePortMap = cachePortMap;
    }

    public void add4CachePortMap(String key, int value) {
        cachePortMap.put(key, value);
    }


    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(String importPath) {
        this.importPath = importPath;
    }

    public String getFlushPath() {
        return flushPath;
    }

    public void setFlushPath(String flushPath) {
        this.flushPath = flushPath;
    }

    public String getSupplyPath() {
        return supplyPath;
    }

    public void setSupplyPath(String supplyPath) {
        this.supplyPath = supplyPath;
    }


    /**
     * 根据不同的配置获取数据提交URL
     *
     * @param serverCacheName
     * @return
     */
    public String getDataImportUrl(final String serverCacheName) {
        validateCacheName(serverCacheName);
        return HttpUtils.HTTP_PROTOCAL_PREFIX + this.webServerIp + ":" + this.cachePortMap.get(serverCacheName)
                + this.importPath + "?cacheName=" + serverCacheName;
    }

    public String getFlushURL(final String serverCacheName) {
        validateCacheName(serverCacheName);
        return HttpUtils.HTTP_PROTOCAL_PREFIX + this.webServerIp + ":" + cachePortMap.get(serverCacheName)
                + flushPath + "?cacheName=" + serverCacheName;
    }

    public String getSupplyIdUrl(final String serverCacheName) {
        //        validateCacheName(serverCacheName);
        //        return HttpUtils.HTTP_PROTOCAL_PREFIX + this.webServerIp + ":" + cachePortMap.get(serverCacheName) + supplyPath;
        return null;
    }

    public void validateCacheName(final String serverCacheName) {
        if (ValidateUtils.isNullOrEmpt(this.cachePortMap)) {
            throw new RuntimeException("cache name <===> apache port mapping is empety!");
        }
        if (!cachePortMap.containsKey(serverCacheName)) {
            throw new RuntimeException("import port for cacheName:[" + serverCacheName + "] is null.");
        }
    }


}
