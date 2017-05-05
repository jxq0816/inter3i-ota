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

package com.inter3i.sun.api.ota.v1.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inter3i.sun.api.ota.v1.config.MongoDBServerConfig;
import com.inter3i.sun.persistence.RepositoryFactory;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.UnknownHostException;
import java.util.Iterator;

@Controller
@RequestMapping("table")
public class TransferDataController {
    private MongoDBServerConfig serverConfig = MongoDBServerConfig.getConfig();

    @ResponseBody
    @RequestMapping(value = "transfer", method = {RequestMethod.GET})
    public String commitData() {
        String cacheName="cache02";
        Document taskData = null;
        try {
            MongoCollection dbCollection = RepositoryFactory.getMongoClient(serverConfig.getDbName(), serverConfig.getDataTableNamesBy(cacheName), serverConfig.getMongoDBIp(), serverConfig.getMongoDBPort());
            FindIterable iterable= dbCollection.find();
            Iterator<Document> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                taskData = iterator.next();
                String docString=taskData.getString("jsonDocStr");
                JSONObject jsonDocString = (JSONObject) JSON.parse(docString);

                JSONArray array = jsonDocString.getJSONArray("datas");

                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    Integer floor = obj.getInteger("floor");
                    if (floor == null) {
                        obj.put("floor", 0);
                    }
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
       /* MongoDBServerConfig serverConfig = MongoDBServerConfig.getConfig();
        ImportDataAdapter importDataAdapter = new ImportDataAdapter(cacheName, ImportDataConfig.DBClinetHolder.getInstance(serverConfig).getDataCollectionBy(cacheName), ImportDataConfig.DBClinetHolder.getInstance(serverConfig).getSplCollectionBy(cacheName), serverConfig);
        importDataAdapter.importDoc2Solr();*/
        return "success";
    }
}
