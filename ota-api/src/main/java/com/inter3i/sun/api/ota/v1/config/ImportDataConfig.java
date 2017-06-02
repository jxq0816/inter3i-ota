/*
 *
 * Copyright (c) 2016, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2016/12/12
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.config;

import com.inter3i.sun.api.ota.v1.job.ImportDataAdapter;
import com.inter3i.sun.persistence.RepositoryFactory;
import com.mongodb.client.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ImportDataConfig {
    private static final Logger logger = LoggerFactory.getLogger(ImportDataConfig.class);

    public static final String TABLE_NAME_PRODUCT = "product";
    public static final String TABLE_NAME_PRODCT_COMMET = "product";

    /**
     * 通用表
     */
//    public static final String TABLE_NAME_COMMON = "data";
//    public static final String TABLE_NAME_SUPPLY_DATA = "supplyIdData";


//    public static final String COMMON_DATA_CONN_URL_KEY = "localhost:8888/3idata/data";


    /**
     * ****************************************** NLP分词相关常量  ******************************************
     */
    public static final int TOKENIZE_DICTTYPE_ALL = 1; //全词典
    public static final int TOKENIZE_DICTTYPE_NOEB = 2;//不包含情感和行业特征

    //数据的分析状态
    public static final int ANALYSIS_STATUS_NORMAL = 0; //正常
    public static final int ANALYSIS_STATUS_NEEDORG = 1; //需要原创
    public static final int ANALYSIS_STATUS_ORGNOTEXIST = 2; //原创已删除
    public static final int ANALYSIS_STATUS_ORGPRIVATE = 3;//原创不适宜公开
    public static final int ANALYSIS_STATUS_OTHERERROR = 4; //其他错误

    //数据的supply satus
    public static final int SUPPLY_STATUS_NOT = 0; //未补充
    public static final int SUPPLY_STATUS_SUCCESS = 1; //补充成功
    public static final int SUPPLY_STATUS_FAILED = 2; //补充失败


    public static final String CACHE_NAME_01 = "cache01";
    public static final String CACHE_NAME_02 = "cache02";
    public static final String CACHE_NAME_03 = "cache03";
    public static final String CACHE_NAME_04 = "cache04";


    public static class DBClinetHolder {
        private final Map<String, MongoCollection> cacheDatatTables = new HashMap<String, MongoCollection>(4);
        private final Map<String, MongoCollection> supplyDocTables = new HashMap<String, MongoCollection>(4);
        private volatile boolean isInited = false;
        private static DBClinetHolder instance;

        private DBClinetHolder() {
        }

        public static DBClinetHolder getInstance(final MongoDBServerConfig serverConfig) {
            if (instance != null) {
                return instance;
            }
            synchronized (DBClinetHolder.class) {
                if (instance != null) {
                    return instance;
                }
                DBClinetHolder tmp = new DBClinetHolder();
                tmp.initConfig(serverConfig);
                instance = tmp;
                return instance;
            }
        }


        //初始话数据库连接

        public void initConfig(final MongoDBServerConfig serverConfig) {
            try {
                logger.info("init all mongoDB config:" + serverConfig.toString());

                Iterator<String> allCacheNames = serverConfig.getAllCacheNames();
                MongoCollection dbCollection = null;
                String cacheName = null;
                String dbName = null;
                MongoDBServerConfig.DBAuth dbAuth = null;
                while (allCacheNames.hasNext()) {
                    //初始化该缓存数据表连接器
                    cacheName = allCacheNames.next();
                    dbName = serverConfig.getDbName();
                    dbAuth = serverConfig.getDBAuthBy(dbName);

                    if (null != dbAuth) {
                        dbCollection = RepositoryFactory.getMongoClient(dbName, serverConfig.getDataTableNameBy(cacheName), dbAuth.getUserName(), dbAuth.getPassword(), serverConfig.getMongoDBIp(), serverConfig.getMongoDBPort());
                        cacheDatatTables.put(cacheName, dbCollection);

                        //初始化该缓存补充表连接器
                        dbCollection = RepositoryFactory.getMongoClient(dbName, serverConfig.geSplTableNamesBy(cacheName), dbAuth.getUserName(), dbAuth.getPassword(), serverConfig.getMongoDBIp(), serverConfig.getMongoDBPort());
                        supplyDocTables.put(cacheName, dbCollection);
                    } else {
                        dbCollection = RepositoryFactory.getMongoClient(dbName, serverConfig.getDataTableNameBy(cacheName), serverConfig.getMongoDBIp(), serverConfig.getMongoDBPort());
                        cacheDatatTables.put(cacheName, dbCollection);

                        //初始化该缓存补充表连接器
                        dbCollection = RepositoryFactory.getMongoClient(dbName, serverConfig.geSplTableNamesBy(cacheName), serverConfig.getMongoDBIp(), serverConfig.getMongoDBPort());
                        supplyDocTables.put(cacheName, dbCollection);
                    }
                }
                isInited = true;
            } catch (Exception e) {
                throw new RuntimeException("ImportDataJob init MongoCollection exception:[" + e.getMessage() + "].");
            }
        }


        public MongoCollection getDataCollectionBy(final String cacheName) {
            return cacheDatatTables.get(cacheName);
        }

        public MongoCollection getSplCollectionBy(final String cacheName) {
            return supplyDocTables.get(cacheName);
        }
    }


}
