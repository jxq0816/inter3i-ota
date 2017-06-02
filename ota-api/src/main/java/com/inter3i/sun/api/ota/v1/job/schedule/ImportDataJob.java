/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2017/01/12
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.job.schedule;

import com.inter3i.sun.api.ota.v1.config.ImportDataConfig;
import com.inter3i.sun.api.ota.v1.config.MongoDBServerConfig;
import com.inter3i.sun.api.ota.v1.job.ImportDataAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("importJob")
@Primary
public class ImportDataJob {
    private static final Logger logger = LoggerFactory.getLogger(ImportDataJob.class);

   /* @Autowired
    private MongoDBServerConfig serverConfig;*/

    private MongoDBServerConfig serverConfig = MongoDBServerConfig.getConfig("dataSource2");

    public void importDoc2SolrFromCache1() {
        importDoc4(ImportDataConfig.CACHE_NAME_01, serverConfig);
    }


    public void importDoc2SolrFromCache2() {
        importDoc4(ImportDataConfig.CACHE_NAME_02, serverConfig);
    }

    public void importDoc2SolrFromCache3() {
        importDoc4(ImportDataConfig.CACHE_NAME_03, serverConfig);
    }

    public void importDoc2SolrFromCache4() {
        importDoc4(ImportDataConfig.CACHE_NAME_04, serverConfig);
    }

    private void importDoc4(final String cacheName, final MongoDBServerConfig serverConfig) {
        String collectName = serverConfig.getDataTableNameBy(cacheName);
        logger.info("Job:[importDoc2Solr] cacheName:[" + cacheName + "] from collect:" + collectName + "] start ...");
        ImportDataAdapter importDataAdapter = new ImportDataAdapter(cacheName, ImportDataConfig.DBClinetHolder.getInstance(serverConfig).getDataCollectionBy(cacheName), ImportDataConfig.DBClinetHolder.getInstance(serverConfig).getSplCollectionBy(cacheName), serverConfig);
        importDataAdapter.importDoc2Solr();
        logger.info("Job:[importDoc2Solr] cacheName:[" + cacheName + "] from collect:" + collectName + "] run compelet.");

        // ********************* 测试代码 ********************* //
        /*try {
            MongoCollection mongoCollection = RepositoryFactory.getMongoClient("3idata", "data", "192.168.0.20", 40000);
            MongoCollection mongoCollection2 = RepositoryFactory.getMongoClient("3idata", "supplyIdData", "192.168.0.20", 40000);
            ImportDataAdapter importDataAdapter = new ImportDataAdapter(cacheName, mongoCollection, mongoCollection2, serverConfig);
            importDataAdapter.importDoc2Solr();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/
        // ********************* 测试代码 ********************* //
    }

}
