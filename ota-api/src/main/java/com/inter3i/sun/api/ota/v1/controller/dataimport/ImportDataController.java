/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2017/03/28
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.controller.dataimport;


import com.inter3i.sun.api.ota.v1.config.CollectionManage;
import com.inter3i.sun.api.ota.v1.config.DatasourceConfig;
import com.inter3i.sun.api.ota.v1.config.MongoDBServerConfig;
import com.inter3i.sun.api.ota.v1.config.StoreDataConfig;
import com.inter3i.sun.api.ota.v1.controller.dataimport.travel.CommonDataController;
import com.inter3i.sun.api.ota.v1.job.ImportDataAdapter;
import com.mongodb.client.MongoCollection;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController("/datacommit")
@RequestMapping("/data")
public class ImportDataController {
    //分词结束以后入mongo
    private static final Logger logger = LoggerFactory.getLogger(CommonDataController.class);
    private static final String dataSourceName="ds_import";

    private MongoDBServerConfig serverConfig = MongoDBServerConfig.getConfig();

    /*private static final Runnable innerThread = () -> {
        while (true) {
            System.out.println("import ... ");
        }
        synchronized (InnerLock) {
            if (!isRunning) {
                throw new RuntimeException("inner thread is not running,cannot set isrun flag to false.");
            }
            isRunning = false;
        }
    };*/

    /**
     * mongo-> SOlR
     * @param cacheName
     * @param requestData
     * @return
     */
    @ResponseBody
    @RequestMapping(produces = "application/x-www-form-urlencoded;charset=utf8", value = "/commit", method = {RequestMethod.POST})
    public String commitData(@RequestParam("cacheName") String cacheName, @RequestBody String requestData) {


        JSONObject responseData = new JSONObject();
        try {
            responseData.put("success", true);
            logger.info("commit data manual  from datasource:["+dataSourceName+"] for Cache:[" + cacheName + "] ...");


            if (!ImportDataAdapter.getLockBy(cacheName).tryRun()) {
                logger.info("commit data manual  from datasource:["+dataSourceName+"] for Cache:[" + cacheName + "] 有任务正在入库！");
                responseData.put("msg", "有任务正在入库!");
                return responseData.toString();
            } else {
                Thread importThread = new Thread(() -> {
                    try {
//                        while (true) {
//                            System.out.println("import ... ");
//                        }
                        DatasourceConfig datasourceConfig=DatasourceConfig.getConfigByDataSourceName(dataSourceName);
                        String collectionName=StoreDataConfig.getConfig().getDataTableORSourceName(cacheName,StoreDataConfig.TABLENAME);

                        MongoCollection collection=CollectionManage.getCollection(datasourceConfig,collectionName);
                        ImportDataAdapter importDataAdapter = new ImportDataAdapter(cacheName,collection ,collection ,serverConfig, false);
                        importDataAdapter.importDoc2Solr();
                    } finally {
                        ImportDataAdapter.getLockBy(cacheName).runComplete();
                    }
                });
                importThread.start();
            }

            /*Thread importThread = new Thread(() -> {
                while (true) {
                    System.out.println("import ... ");
                }
            });*/
           /* ImportDataAdapter importDataAdapter = new ImportDataAdapter(cacheName, ImportDataConfig.DBClinetHolder.getInstance(serverConfig).getDataCollectionBy(cacheName), ImportDataConfig.DBClinetHolder.getInstance(serverConfig).getSplCollectionBy(cacheName), serverConfig);
            importDataAdapter.importDoc2Solr();*/
            logger.info("commit data manual  from datasource:["+dataSourceName+"] for Cache:[" + cacheName + "] complete.");
        } catch (Exception e) {
            logger.error("commit data manual  from datasource:["+dataSourceName+"] for Cache:[" + cacheName + "] exception:[" + e.getMessage() + "].", e);
            responseData.put("success", false);
            responseData.put("errorMsg", "commit data manual  from datasource:["+dataSourceName+"] for Cache:[" + cacheName + "] exception:[" + e.getMessage() + "].");
        } finally {
            return responseData.toString();
        }
    }

}
