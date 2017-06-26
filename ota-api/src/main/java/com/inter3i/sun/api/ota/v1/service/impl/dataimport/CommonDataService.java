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

package com.inter3i.sun.api.ota.v1.service.impl.dataimport;

import com.inter3i.sun.api.ota.v1.config.DatasourceConfig;
import com.inter3i.sun.api.ota.v1.config.MongoDBServerConfig;
import com.inter3i.sun.api.ota.v1.config.StoreDataConfig;
import com.inter3i.sun.api.ota.v1.service.dataimport.ICommonDataService;
import com.inter3i.sun.persistence.RepositoryFactory;
import com.inter3i.sun.persistence.dataimport.CommonData;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;

@Service
public class CommonDataService implements ICommonDataService {
    private static final Logger logger = LoggerFactory.getLogger(CommonDataService.class);

    public void savaCommonData(final String cacheName, final CommonData commonData, final MongoDBServerConfig serverConfig,final StoreDataConfig storeDataConfig) throws UnknownHostException {
        if (null == commonData || null == commonData.getJsonDoc() || 0 == commonData.getJsonDoc().size()) {
            logger.warn("--+-savaCommonData into mongoDB failed,Data is null.");
            return;
        }
        logger.info("--+-savaCommonData into mongoDB ...");
        long starTime = System.currentTimeMillis();
        String dataSourceName=storeDataConfig.getDataTableORSourceName(cacheName,StoreDataConfig.DSNAME);

        DatasourceConfig dataSourceConfig=DatasourceConfig.getConfigByDataSourceName(dataSourceName);
        String dbName=dataSourceConfig.getDbName();


        String tableName=storeDataConfig.getDataTableORSourceName(cacheName,StoreDataConfig.TABLENAME);

        MongoCollection dbCollection = RepositoryFactory.getMongoClient(dbName,tableName,dataSourceConfig.getMongoDBIp(), dataSourceConfig.getMongoDBPort());
        Document mogoDbBean = converBean2Doc(commonData);
        dbCollection.insertOne(mogoDbBean);
        long endTime = System.currentTimeMillis();
        //TimeStatisticUtil.getTimeInof(serverConfig.getDataImportUrl(ImportDataConfig.CACHE_NAME_01)).addTime(endTime - starTime);
        logger.info("--+-savaCommonData into mongoDB complete,data:[" + commonData + "]. Spend:[" + (endTime - starTime) + "]ms.");
    }

    /*private DBObject converBean2MongoObj(CommonData commonData) {
        DBObject mogoDbBean = new BasicDBObject();
        mogoDbBean.put("jsonDocStr", commonData.getJsonDocStr());
        mogoDbBean.put("importStatus", commonData.getImportStatus());
        mogoDbBean.put("issegmented", commonData.getImportStatus());
        mogoDbBean.put("cacheDataTime", commonData.getCacheDataTime());
        return mogoDbBean;
    }*/

    private Document converBean2Doc(CommonData commonData) {
        Document mogoDbBean = new Document();
        mogoDbBean.put("importStatus", commonData.getImportStatus());
        mogoDbBean.put("segmentedStatus", commonData.getImportStatus());
        mogoDbBean.put("cacheDataTime", commonData.getCacheDataTime());
        mogoDbBean.put("jsonDoc", commonData.getJsonDoc());
        mogoDbBean.put("dataSmryInfo", commonData.getDataSmryInfo());
        return mogoDbBean;
    }
}
