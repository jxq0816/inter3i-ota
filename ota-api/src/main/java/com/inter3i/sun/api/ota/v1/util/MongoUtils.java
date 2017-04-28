/*
 *
 * Copyright (c) 2016, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2016/12/21
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.util;

import com.inter3i.sun.api.ota.v1.config.IMongoDocConverter;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;

public class MongoUtils {

    /**
     * FIXME
     *
     * @param coll
     * @param id
     * @param document
     * @return
     */
    public static Document updateById(MongoCollection<Document> coll, String id, Document document) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Bson filter = Filters.eq("_id", _idobj);
        // coll.replaceOne(filter, newdoc); // 完全替代
        coll.updateOne(filter, new Document("$set", document));
        return document;
    }

    public static final String PRIM_KEY_ID = "_id";

    public static Document updateById(MongoCollection<Document> coll, Document document) {
        if (document.isEmpty()) {
            throw new RuntimeException("updateById for mongo exceptino:[document is empty!]");
        }
        Object _idobj = document.get(PRIM_KEY_ID);
        if (null == _idobj) {
            throw new RuntimeException("updateById for mongo exceptino:[_id is null!]");
        }
        Bson filter = Filters.eq("_id", _idobj);
        coll.updateOne(filter, new Document("$set", document));
        return document;
    }

    /**
     *
     * @param coll
     * @param id
     * @param status
     * @param createTime 创建时间 by jiangxingqi
     * @param updateTime 更新时间 by jiangxingqi
     */
    public static void updateStatusAndCreateTimeById(MongoCollection<Document> coll, Object id, int status,Long createTime,Long updateTime) {
        if (null == id) {
            throw new RuntimeException("updateStatusById for mongo exceptino:[_id is null!]");
        }
        Bson filter = Filters.eq("_id", id);
        Document newdoc = new Document();
        newdoc.put("importStatus", status);
        newdoc.put("createTime", createTime);
        newdoc.put("updateTime", updateTime);
        coll.updateOne(filter, new Document("$set", newdoc));
    }

    /**
     *
     * @param coll
     * @param id
     * @param status
     * @param updateTime by jiangxingqi
     */
    public static void updateStatusAndUpdateTimeById(MongoCollection<Document> coll, Object id, int status,Long updateTime) {
        if (null == id) {
            throw new RuntimeException("updateStatusById for mongo exceptino:[_id is null!]");
        }
        Bson filter = Filters.eq("_id", id);
        Document newdoc = new Document();
        newdoc.put("importStatus", status);
        newdoc.put("updateTime", updateTime);
        coll.updateOne(filter, new Document("$set", newdoc));
    }

    public static void addData(final JSONArray addDatas, final MongoCollection dbCollection, final IMongoDocConverter converter) throws JSONException {
        if (ValidateUtils.isNullOrEmpt(addDatas))
            return;

        Document mogoDbBean = null;
        for (int i = 0; i < addDatas.length(); i++) {
            mogoDbBean = converter.converBean2Doc(addDatas.get(i));
            dbCollection.insertOne(mogoDbBean);
        }
    }
}
