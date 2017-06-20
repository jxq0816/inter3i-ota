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

import com.inter3i.sun.persistence.RepositoryFactory;
import com.mongodb.client.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionManage {
    private static final Logger logger = LoggerFactory.getLogger(CollectionManage.class);

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


    public static MongoCollection getCollection(final DatasourceConfig datasourceConfig, String collectionName) {
        try {

            MongoCollection dbCollection;
            String dbName = datasourceConfig.getDbName();
            DatasourceConfig.DBAuth dbAuth = datasourceConfig.getDBAuthBy(dbName);

            if (null != dbAuth) {
                dbCollection = RepositoryFactory.getMongoClient(dbName, collectionName, dbAuth.getUserName(), dbAuth.getPassword(), datasourceConfig.getMongoDBIp(), datasourceConfig.getMongoDBPort());

            } else {
                dbCollection = RepositoryFactory.getMongoClient(dbName, collectionName, datasourceConfig.getMongoDBIp(), datasourceConfig.getMongoDBPort());
            }
            return dbCollection;
        } catch (Exception e) {
            throw new RuntimeException("ImportDataJob init MongoCollection exception:[" + e.getMessage() + "].");
        }

    }
}
