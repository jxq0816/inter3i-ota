/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: wangchaochao
 * Created: 2017/01/12
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
*//*@PropertySource("classpath:importdata.properties")*//*
@PropertySource("file:D:/tmp/config/importdata.properties")*/
public class MongoDBServerConfig {
    private static MongoDBServerConfig instance = null;

    public static MongoDBServerConfig getConfig() {
        if (instance != null) {
            return instance;
        }

        synchronized (MongoDBServerConfig.class) {
            if (instance != null) {
                return instance;
            }

            //初始化该配置类
            MongoDBServerConfig tmp = null;
            try {
                tmp = new MongoDBServerConfig();
                Resource resource = new ClassPathResource("/importdata.properties");
                Properties props = PropertiesLoaderUtils.loadProperties(resource);
                Iterator it = props.keySet().iterator();
                String key = null;
                while (it.hasNext()) {
                    key = (String) it.next();
                    if ("di.dbName".equals(key)) {
                        tmp.setDbName((String) props.get(key));
                    } else if ("di.mongoDBIp".equals(key)) {
                        tmp.setMongoDBIp((String) props.get(key));
                    } else if ("di.mongoDBPort".equals(key)) {
                        tmp.setMongoDBPort(Integer.valueOf((String) props.get(key)));
                    } else if (key.startsWith("di.cacheTableMap[")) {
                        tmp.add4CacheTableMap(getKeyIn(key), (String) props.get(key));
                    } else if ("di.nlpServerIp".equals(key)) {
                        tmp.setNlpServerIp((String) props.get(key));
                    } else if ("di.nlpServerPort".equals(key)) {
                        tmp.setNlpServerPort(Integer.valueOf((String) props.get(key)));
                    } else if ("di.nlpReqPath".equals(key)) {
                        tmp.setNlpReqPath((String) props.get(key));
                    } else if ("di.nlpReqTimeOut".equals(key)) {
                        tmp.setNlpReqTimeOut(Integer.valueOf((String) props.get(key)));
                    } else if ("di.webServerIp".equals(key)) {
                        tmp.setWebServerIp((String) props.get(key));
                    } else if (key.startsWith("di.cachePortMap[")) {
                        tmp.add4CachePortMap(getKeyIn(key), Integer.valueOf((String) props.get(key)));
                    } else if ("di.importPath".equals(key)) {
                        tmp.setImportPath((String) props.get(key));
                    } else if ("di.flushPath".equals(key)) {
                        tmp.setFlushPath((String) props.get(key));
                    } else if ("di.supplyPath".equals(key)) {
                        tmp.setSupplyPath((String) props.get(key));
                    } else if ("di.docNumPerImport".equals(key)) {
                        tmp.setDocNumPerImport(Integer.valueOf((String) props.get(key)));
                    } else if ("di.importNumPerFlush".equals(key)) {
                        tmp.setImportNumPerFlush(Integer.valueOf((String) props.get(key)));
                    } else if (key.startsWith("di.cacheDataTables[")) {
                        tmp.add4CacheDataTables(getKeyIn(key), (String) props.get(key));
                    } else if (key.startsWith("di.cacheSplTables[")) {
                        tmp.add4CacheSplTables(getKeyIn(key), (String) props.get(key));
                    } else if ("di.dataStorage.ip".equals(key)) {
                        tmp.dataStorageServerIp = (String) props.get(key);
                    }else if("di.dataStorage.port".equals(key)){
                        tmp.dataStorageServerPort = Integer.valueOf((String) props.get(key));
                    }else if("di.dataStorage.flushPath".equals(key)){
                        tmp.dataStorageSolrFlushPath= (String) props.get(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = tmp;
            return instance;
        }
    }

    //获取di.cacheTableMap[cache01] 中的[] 中的key
    private static String getKeyIn(String key) {
        return key.substring(key.indexOf("[") + 1, key.length() - 1);
    }

    private static String importPath;
    private static String flushPath;
    private static String supplyPath;

    private String dbName;
    private String mongoDBIp;
    private int mongoDBPort;
    private Map<String, String> cacheTableMap = new HashMap<>(4);

    private String nlpServerIp;
    private int nlpServerPort;
    private String nlpReqPath;
    private int nlpReqTimeOut;


    private String webServerIp;
    private int docNumPerImport;
    private int importNumPerFlush;
    private Map<String, Integer> cachePortMap = new HashMap<>(4);

    private String dataStorageServerIp;
    private int dataStorageServerPort;
    private String dataStorageSolrFlushPath;


    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getMongoDBIp() {
        return mongoDBIp;
    }

    public void setMongoDBIp(String mongoDBIp) {
        this.mongoDBIp = mongoDBIp;
    }

    public Map<String, String> getCacheDataTables() {
        return cacheDataTables;
    }

    public void setCacheDataTables(Map<String, String> cacheDataTables) {
        this.cacheDataTables = cacheDataTables;
    }

    public void add4CacheDataTables(String key, String value) {
        cacheDataTables.put(key, value);
    }

    public Map<String, String> getCacheSplTables() {
        return cacheSplTables;
    }

    public void setCacheSplTables(Map<String, String> cacheSplTables) {
        this.cacheSplTables = cacheSplTables;
    }

    public void add4CacheSplTables(String key, String value) {
        cacheSplTables.put(key, value);
    }

    public int getMongoDBPort() {
        return mongoDBPort;
    }

    public void setMongoDBPort(int mongoDBPort) {
        this.mongoDBPort = mongoDBPort;
    }

    public Map<String, String> getCacheTableMap() {
        return cacheTableMap;
    }

    public void setCacheTableMap(Map<String, String> cacheTableMap) {
        this.cacheTableMap = cacheTableMap;
    }

    public void add4CacheTableMap(String key, String value) {
        cacheTableMap.put(key, value);
    }

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
        return HttpUtils.HTTP_PROTOCAL_PREFIX + this.dataStorageServerIp + ":" + this.dataStorageServerPort + importPath;
    }

  /*  public String getFlushURL(final String serverCacheName) {
        validateCacheName(serverCacheName);
        return HttpUtils.HTTP_PROTOCAL_PREFIX + this.webServerIp + ":" + cachePortMap.get(serverCacheName) + flushPath;
    }*/

    public String getFlushURL4DataStorege(final String serverCacheName) {
        if (ValidateUtils.isNullOrEmpt(this.cacheDataTables) || !cacheDataTables.containsKey(serverCacheName)) {
            throw new RuntimeException("cache name not exist! CacheName:["+serverCacheName+"].");
        }
        return HttpUtils.HTTP_PROTOCAL_PREFIX + this.dataStorageServerIp + ":" + dataStorageServerPort + dataStorageSolrFlushPath+"?cacheName="+serverCacheName;
    }

    public String getSupplyIdUrl(final String serverCacheName) {
        validateCacheName(serverCacheName);
        return HttpUtils.HTTP_PROTOCAL_PREFIX + this.webServerIp + ":" + cachePortMap.get(serverCacheName) + supplyPath;
    }

    public void validateCacheName(final String serverCacheName) {
        if (ValidateUtils.isNullOrEmpt(this.cachePortMap)) {
            throw new RuntimeException("cache name <===> apache port mapping is empety!");
        }
        if (!cachePortMap.containsKey(serverCacheName)) {
            throw new RuntimeException("import port for cacheName:[" + serverCacheName + "] is null.");
        }
    }


    /**
     * 根据不同的配置 查找缓存对应的数据库表：数据表
     *
     * @param cacheName
     * @return
     */
    private Map<String, String> cacheDataTables = new HashMap<>(4);

    public String getDataTableNamesBy(final String cacheName) {
        validateCacheNameDataTables(cacheName);
        return this.cacheDataTables.get(cacheName);
    }

    private void validateCacheNameDataTables(final String cacheName) {
        if (ValidateUtils.isNullOrEmpt(this.cacheDataTables)) {
            throw new RuntimeException("cache name <===> dataTable mapping is empety!");
        }
        if (!cacheDataTables.containsKey(cacheName)) {
            throw new RuntimeException("dataTable name for cacheName:[" + cacheName + "] is null.");
        }
    }

    /**
     * 根据不同的配置 查找缓存对应的数据库表：supplyDoc表
     *
     * @param cacheName
     * @return
     */
    private Map<String, String> cacheSplTables = new HashMap<>(4);

    public String geSplTableNamesBy(final String cacheName) {
        validateCacheNameSplTables(cacheName);
        return this.cacheSplTables.get(cacheName);
    }

    private void validateCacheNameSplTables(final String cacheName) {
        if (ValidateUtils.isNullOrEmpt(this.cacheSplTables)) {
            throw new RuntimeException("cache name <===> dataTable mapping is empety!");
        }
        if (!cacheSplTables.containsKey(cacheName)) {
            throw new RuntimeException("dataTable name for cacheName:[" + cacheName + "] is null.");
        }
    }
}
