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

import com.inter3i.sun.api.ota.v1.config.DataConfig;
import com.inter3i.sun.api.ota.v1.config.JobConfig;
import com.inter3i.sun.api.ota.v1.config.MongoDBServerConfig;
import com.inter3i.sun.api.ota.v1.job.ImportDataAdapter;
import com.inter3i.sun.api.ota.v1.service.TaskScheduledService;
import com.mongodb.client.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("importJob")
@Primary
public class ImportDataJob {

    @Autowired
    private TaskScheduledService taskScheduledService;

    private final String jobType="0";

    private final String jobName="import";

    private static final Logger logger = LoggerFactory.getLogger(ImportDataJob.class);

   /* @Autowired
    private MongoDBServerConfig serverConfig;*/

    private MongoDBServerConfig serverConfig;

    public void importDoc2SolrFromCache1() {
        Boolean status=taskScheduledService.getStatus(jobType, DataConfig.CACHE_NAME_01);
        if(status){
            String dataSourceName=JobConfig.getConfig().getDataSourceName(DataConfig.CACHE_NAME_01,jobName);
            serverConfig = MongoDBServerConfig.getConfigByDataSourceName(dataSourceName);
            importDoc4(DataConfig.CACHE_NAME_01,dataSourceName, serverConfig);
        }
    }


    public void importDoc2SolrFromCache2() {
        Boolean status=taskScheduledService.getStatus(jobType,DataConfig.CACHE_NAME_02);
        if(status) {
            String dataSourceName=JobConfig.getConfig().getDataSourceName(DataConfig.CACHE_NAME_02,jobName);
            serverConfig = MongoDBServerConfig.getConfigByDataSourceName(dataSourceName);
            importDoc4(DataConfig.CACHE_NAME_02,dataSourceName, serverConfig);
        }
    }

    public void importDoc2SolrFromCache3() {
        Boolean status=taskScheduledService.getStatus(jobType,DataConfig.CACHE_NAME_03);
        if(status) {
            String dataSourceName=JobConfig.getConfig().getDataSourceName(DataConfig.CACHE_NAME_03,jobName);
            serverConfig = MongoDBServerConfig.getConfigByDataSourceName(dataSourceName);
            importDoc4(DataConfig.CACHE_NAME_03,dataSourceName, serverConfig);
        }
    }

    public void importDoc2SolrFromCache4() {
        Boolean status=taskScheduledService.getStatus(jobType,DataConfig.CACHE_NAME_04);
        if(status) {
            String dataSourceName=JobConfig.getConfig().getDataSourceName(DataConfig.CACHE_NAME_04,jobName);
            serverConfig = MongoDBServerConfig.getConfigByDataSourceName(dataSourceName);
            importDoc4(DataConfig.CACHE_NAME_04,dataSourceName, serverConfig);
        }
    }

    private void importDoc4(final String cacheName,final String dataSourceName, final MongoDBServerConfig serverConfig) {
        String collectName = JobConfig.getConfig().getDataTableNameBy(cacheName,JobConfig.IMPORT);

        logger.info("Job:[importDoc2Solr] cacheName:[" + cacheName + "]  from datasource:["+dataSourceName+"] from collect:" + collectName + "] start ...");
        MongoCollection collection=DataConfig.DBClinetHolder.getInstance(serverConfig,jobName).getDataCollectionBy(cacheName);
        MongoCollection sqlCollection=DataConfig.DBClinetHolder.getInstance(serverConfig,jobName).getSplCollectionBy(cacheName);
        ImportDataAdapter importDataAdapter = new ImportDataAdapter(cacheName, collection, sqlCollection, serverConfig);
        importDataAdapter.importDoc2Solr();
        logger.info("Job:[importDoc2Solr] cacheName:[" + cacheName + "]  from datasource:["+dataSourceName+"] from collect:" + collectName + "] run complete.");

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
