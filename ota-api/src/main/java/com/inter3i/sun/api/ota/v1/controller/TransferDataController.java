/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2017/05/05
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.controller;


import com.inter3i.sun.api.ota.v1.util.MongoUtils;
import com.inter3i.sun.persistence.RepositoryFactory;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;

@Controller
@RequestMapping("/table")
public class TransferDataController {

    private static final Logger logger = LoggerFactory.getLogger(TransferDataController.class);

    @ResponseBody
    @RequestMapping(value = "/transfer", method = {RequestMethod.GET})
    public String commitData() {
        Document taskData;
        try {
            MongoCollection dbCollection = RepositoryFactory.getMongoClient("3idata", "qiche", "120.27.195.31", 40000);
            MongoCollection dbCollection2 = RepositoryFactory.getMongoClient("3idata", "data_01", "120.27.195.31", 40000);

            Bson filter1 = Filters.ne("trasferStatus", 1);
            FindIterable iterable = dbCollection.find(filter1);
            Iterator<Document> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                taskData = iterator.next();

                String docString = taskData.getString("jsonDocStr");
                JSONObject jsonDocString = new JSONObject(docString);

                JSONArray array = jsonDocString.getJSONArray("datas");

                boolean isChang = false;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    Object floor = obj.get("floor");
                    if (floor == null) {
                        obj.put("floor", 0);
                        isChang = true;
                    }
                }


                if (isChang) {
                    taskData.put("jsonDocStr", jsonDocString.toString());
                }

                insert(taskData, dbCollection2);//迁移

                updateConvertStatus(dbCollection, taskData);//删除
                System.out.println("---成功 -----");
            }
        } catch (Exception e) {
            logger.error("transfer data exception:[" + e.getMessage() + "]", e);
        }
        return "success";
    }


    private void insert(Document taskData, MongoCollection dbCollection2) {
        Document document = new Document();
        document.put("jsonDocStr", taskData.get("jsonDocStr"));
        document.put("importStatus", taskData.get("importStatus"));
        document.put("segmentedStatus", taskData.get("segmentedStatus"));
        document.put("cacheDataTime", taskData.get("cacheDataTime"));
        dbCollection2.insertOne(document);
    }

    private void updateConvertStatus(MongoCollection dbCollection, Document taskData) {
        taskData.put("trasferStatus", 1);
        MongoUtils.updateById(dbCollection, taskData);
    }

}
