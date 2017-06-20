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
import com.inter3i.sun.api.ota.v1.job.SegmentAdapter;
import com.inter3i.sun.api.ota.v1.service.TaskScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("segmenteJob")
@Primary
public class SegmenteJob {
    private static final Logger logger = LoggerFactory.getLogger(SegmenteJob.class);

    @Autowired
    private TaskScheduledService taskScheduledService;

    private final String jobType="1";

    private final String jobName="segment";

    private String dataSourceName;

    /*@Autowired
    private MongoDBServerConfig serverConfig;*/

    private MongoDBServerConfig serverConfig;

    public void segmentDocs4Cache1() {
        Boolean status=taskScheduledService.getStatus(jobType, DataConfig.CACHE_NAME_01);
        if(status) {
            dataSourceName=JobConfig.getConfig().getDataSourceName(DataConfig.CACHE_NAME_01,jobName);
            serverConfig = MongoDBServerConfig.getConfigByDataSourceName(dataSourceName);
            segmentDocs4(DataConfig.CACHE_NAME_01,dataSourceName, serverConfig);
        }
    }

    public void segmentDocs4Cache2() {
        Boolean status=taskScheduledService.getStatus(jobType,DataConfig.CACHE_NAME_02);
        if(status) {
            dataSourceName=JobConfig.getConfig().getDataSourceName(DataConfig.CACHE_NAME_01,jobName);
            serverConfig = MongoDBServerConfig.getConfigByDataSourceName(dataSourceName);
            segmentDocs4(DataConfig.CACHE_NAME_02,dataSourceName, serverConfig);
        }
    }

    public void segmentDocs4Cache3() {
        Boolean status=taskScheduledService.getStatus(jobType,DataConfig.CACHE_NAME_03);
        if(status) {
            dataSourceName=JobConfig.getConfig().getDataSourceName(DataConfig.CACHE_NAME_01,jobName);
            serverConfig = MongoDBServerConfig.getConfigByDataSourceName(dataSourceName);
            segmentDocs4(DataConfig.CACHE_NAME_03,dataSourceName, serverConfig);
        }
    }

    public void segmentDocs4Cache4() {
        Boolean status=taskScheduledService.getStatus(jobType,DataConfig.CACHE_NAME_04);
        if(status) {
            dataSourceName=JobConfig.getConfig().getDataSourceName(DataConfig.CACHE_NAME_01,jobName);
            serverConfig = MongoDBServerConfig.getConfigByDataSourceName(dataSourceName);
            segmentDocs4(DataConfig.CACHE_NAME_04,dataSourceName, serverConfig);
        }
    }

    public void segmentDocs4(final String cacheName,final String dataSourceName, final MongoDBServerConfig serverConfig) {
        String collectName = JobConfig.getConfig().getDataTableNameBy(cacheName,jobName);

        logger.info("Job:[segmentData] for cacheName:[" + cacheName + "]  from datasource:["+dataSourceName+"] from collect:[" + collectName + "] start. DBServerIP:[" + serverConfig.getMongoDBIp() + "] DBServerPort:[" + serverConfig.getMongoDBPort() + "]  ... ");
        SegmentAdapter segmentAdapter = new SegmentAdapter(serverConfig, cacheName, DataConfig.DBClinetHolder.getInstance(serverConfig,jobName).getDataCollectionBy(cacheName));
        segmentAdapter.doSegment4Docs();
        logger.info("Job:[segmentData] for cacheName:[" + cacheName + "] from datasource:["+dataSourceName+"] from collect:[" + collectName + "] run complete. DBServerIP:[" + serverConfig.getMongoDBIp() + "] DBServerPort:[" + serverConfig.getMongoDBPort() + "].");
    }

}
