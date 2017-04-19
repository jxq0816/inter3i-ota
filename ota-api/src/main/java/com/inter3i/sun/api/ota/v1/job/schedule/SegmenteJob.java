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
import com.inter3i.sun.api.ota.v1.job.SegmentAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("segmenteJob")
@Primary
public class SegmenteJob {
    private static final Logger logger = LoggerFactory.getLogger(SegmenteJob.class);

    /*@Autowired
    private MongoDBServerConfig serverConfig;*/

    private MongoDBServerConfig serverConfig = MongoDBServerConfig.getConfig();

    public void segmentDocs4Cache1() {
        segmentDocs4(ImportDataConfig.CACHE_NAME_01);
    }

    public void segmentDocs4Cache2() {
        segmentDocs4(ImportDataConfig.CACHE_NAME_02);
    }

    public void segmentDocs4Cache3() {
        segmentDocs4(ImportDataConfig.CACHE_NAME_03);
    }

    public void segmentDocs4Cache4() {
        segmentDocs4(ImportDataConfig.CACHE_NAME_04);
    }

    public void segmentDocs4(final String cacheName) {
        logger.info("Job:[segmentData] for cacheName:[" + cacheName + "] start  DBServerIP:[" + serverConfig.getMongoDBIp() + "] DBServerPort:[" + serverConfig.getMongoDBPort() + "]  ... ");
        SegmentAdapter segmentAdapter = new SegmentAdapter(serverConfig, cacheName, ImportDataConfig.DBClinetHolder.getInstance(serverConfig).getDataCollectionBy(cacheName));
        segmentAdapter.doSegment4Docs();
    }

}
