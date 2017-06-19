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

import java.io.IOException;
import java.util.*;

/*@Configuration
@ConfigurationProperties(prefix = "di")
*//*@PropertySource("classpath:importdata.properties")*//*
@PropertySource("file:D:/tmp/config/importdata.properties")*/
public class MongoDBServerConfig {
    private static Resource resource = new ClassPathResource("/importdata.properties");
    private static Map configMap=new HashMap();

    public static MongoDBServerConfig getConfigByDataSourceName(String dataSourceName) {
        MongoDBServerConfig rs=(MongoDBServerConfig)configMap.get(dataSourceName);
        if(rs!=null){
            return rs;
        }


        Properties props = null;
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<Map.Entry<Object, Object>> set = props.entrySet();
        for (Map.Entry<Object, Object> m : set) {
            if(m.getValue().equals(dataSourceName)){
                String key=(String)m.getKey();
                String[] array=key.split("\\.");
                String dataSourceNum=array[array.length-1];
                MongoDBServerConfig config=getConfig(dataSourceNum);
                configMap.put(dataSourceName,config);
                return config;
            }
        }
        return null;
    }

    public static MongoDBServerConfig getConfig(String dataSourceNum) {
        String dbNameKey="di.dbName."+dataSourceNum;
        String ipKey="di.mongoDBIp."+dataSourceNum;
        String portKey="di.mongoDBPort."+dataSourceNum;
        String userNameKey="di.dbAuth.userName."+dataSourceNum;
        String passwordKey="di.dbAuth.password."+dataSourceNum;

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

                    if(ipKey.equals(key)){
                        tmp.setMongoDBIp((String) props.get(key));
                    } else if(dbNameKey.equals(key)){
                        tmp.setDbName((String) props.get(key));
                    }else if(portKey.equals(key)){
                        tmp.setMongoDBPort(Integer.valueOf((String) props.get(key)));
                    }else if (key.equals(userNameKey)) {
                        //提取用户名
                        String dbName = getKeyInLastSpliteTerm(key, ".");
                        tmp.addAuthUserName4(dbName, (String) props.get(key));
                    } else if (key.equals(passwordKey)) {
                        //提取密码
                        String dbName = getKeyInLastSpliteTerm(key, ".");
                        tmp.addAuthPassword4(dbName, (String) props.get(key));
                    }  else if (key.startsWith("di.cachePortMap[")) {
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

    //获取di.dbAuth.password.3idata 中的 3idata
    private static String getKeyInLastSpliteTerm(String key, String splitChar) {
        if (!key.contains(splitChar)) {
            return null;
        }
        if (key.endsWith(splitChar)) {
            throw new RuntimeException("key can not end with splitChar:[" + splitChar + "].");
        }
        return key.substring(key.lastIndexOf(splitChar) + 1);
    }

    private static String importPath;
    private static String flushPath;
    private static String supplyPath;

    private String dbName;
    private String mongoDBIp;
    private int mongoDBPort;
    private Map<String, String> cacheInfos = new HashMap<>(4);

    private String nlpServerIp;
    private int nlpServerPort;
    private String nlpReqPath;
    private int nlpReqTimeOut;


    private String webServerIp;
    private int docNumPerImport;
    private int importNumPerFlush;
    private Map<String, Integer> cachePortMap = new HashMap<>(4);

    /**
     * 根据不同的配置 查找缓存对应的数据库表：数据表
     */
    private Map<String, String> cacheNameDataTableMap = new HashMap<>(4);


    public Iterator<String> getAllCacheNames() {
        return cacheNameDataTableMap.keySet().iterator();
    }

    /**
     * 所有的缓存名称 ---> 描述的 映射
     */
    private Map<String, String> cacheNameCacheDescMap = new HashMap<>(4);

    public String getDataTableNameBy(final String cacheName) {
        validateCacheNameDataTables(cacheName);
        return cacheNameDataTableMap.get(cacheName);
    }

    private void validateCacheNameDataTables(final String cacheName) {
        if (ValidateUtils.isNullOrEmpt(this.cacheNameDataTableMap)) {
            throw new RuntimeException("cache name <===> dataTable mapping is empety!");
        }
        if (!cacheNameDataTableMap.containsKey(cacheName)) {
            throw new RuntimeException("dataTable name for cacheName:[" + cacheName + "] is null.");
        }
    }

    public DBAuth getDBAuthBy(final String dbName) {
        if (!dbAuths.containsKey(dbName)) {
//            throw new RuntimeException("getUserName for:" + dbName + "] exception, the config of dbAuth not contains the config for this dbname.");
            return null;
        }
        return dbAuths.get(dbName);
    }

    /**
     * 根据不同的配置 查找缓存对应的数据库表：数据表
     */
    private Map<String, DBAuth> dbAuths = new HashMap<String, DBAuth>(2);

    public static final class DBAuth {
        private String userName;
        private String password;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public void addAuthUserName4(final String dbName, final String userName) {
        DBAuth dbAuth = null;
        if (!dbAuths.containsKey(dbName)) {
            dbAuth = new DBAuth();
            dbAuths.put(dbName, dbAuth);
        } else {
            dbAuth = dbAuths.get(dbName);
        }
        dbAuth.setUserName(userName);
    }

    public void addAuthPassword4(final String dbName, final String password) {
        DBAuth dbAuth = null;
        if (!dbAuths.containsKey(dbName)) {
            dbAuth = new DBAuth();
            dbAuths.put(dbName, dbAuth);
        } else {
            dbAuth = dbAuths.get(dbName);
        }
        dbAuth.setPassword(password);
    }


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


    public void add4CacheSplTables(String key, String value) {
        cacheSplTables.put(key, value);
    }

    public int getMongoDBPort() {
        return mongoDBPort;
    }

    public void setMongoDBPort(int mongoDBPort) {
        this.mongoDBPort = mongoDBPort;
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
        return HttpUtils.HTTP_PROTOCAL_PREFIX + this.webServerIp + ":" + this.cachePortMap.get(serverCacheName)
                + this.importPath + "?cacheName=" + serverCacheName;
    }

    public String getFlushURL(final String serverCacheName) {
        validateCacheName(serverCacheName);
        return HttpUtils.HTTP_PROTOCAL_PREFIX + this.webServerIp + ":" + cachePortMap.get(serverCacheName)
                + flushPath + "?cacheName=" + serverCacheName;
    }

    //    public String getFlushURL4DataStorege(final String serverCacheName) {
    //        if (ValidateUtils.isNullOrEmpt(this.cacheDataTables) || !cacheDataTables.containsKey(serverCacheName)) {
    //            throw new RuntimeException("cache name not exist! CacheName:[" + serverCacheName + "].");
    //        }
    //        return HttpUtils.HTTP_PROTOCAL_PREFIX + this.dataStorageServerIp + ":" + dataStorageServerPort + dataStorageSolrFlushPath + "?cacheName=" + serverCacheName;
    //    }

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
