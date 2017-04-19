/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: wangchaochao
 * Created: 2017/01/20
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.importdata;

import com.inter3i.sun.api.ota.v1.config.MongoDBServerConfig;
import com.inter3i.sun.api.ota.v1.util.MongoUtils;
import com.inter3i.sun.persistence.RepositoryFactory;
import com.inter3i.sun.persistence.dataimport.CommonData;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.Iterator;

@Component("importJobTest")
public class ImportDataTest {
    private static final Logger logger = LoggerFactory.getLogger(ImportDataTest.class);

    //@Autowired
    //private MongoDBServerConfig serverConfig;

//    public void segmentDocs4CacheTest() {
//        importDoc4();
//    }

    private void importDoc4() {
        MongoCollection mongoCollection = null;
        try {
            logger.info("Job:[importDoc2Solr] cacheName:[default] start ...");
            mongoCollection = RepositoryFactory.getMongoClient("3idata", "data", "192.168.0.20", 40000);

            //查询出所有的没有入库以及分词成功的文章
            //Bson fileter1 = Filters.eq("importStatus", CommonData.IMPORTSTATUS_NO_IMPORT); //没有入库
            Bson fileter2 = Filters.gt("cacheDataTime", Long.valueOf("1484621989975"));//分词成功
            //Bson conds = Filters.and(fileter2);
            //没有分词
            FindIterable iterable = mongoCollection.find(fileter2);

            Iterator<Document> iterator = iterable.iterator();

            //针对大批次里面的小批次里面的文档，在整个大批次里面的偏移
            Document tempDoc = null;
            int testDocNum = 0;
            while (iterator.hasNext()) {
                tempDoc = iterator.next();
                if (tempDoc.containsKey("importStatus") && ((Integer) (tempDoc.get("importStatus"))) == CommonData.IMPORTSTATUS_NO_IMPORT) {
                    continue;
                }
                tempDoc.put("importStatus", CommonData.IMPORTSTATUS_NO_IMPORT);
                MongoUtils.updateById(mongoCollection, tempDoc);
                testDocNum++;
            }
            System.out.println("获取测试数据:[" + testDocNum + "] 条!");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void testJobNum() throws Exception {
        System.out.println("我起来了...");
        Thread.currentThread().sleep(1 * 1000);
        System.out.println("我要死了.");
    }
}
