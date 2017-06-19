package com.inter3i.sun.api.ota.v1.service;

import com.inter3i.sun.api.ota.v1.config.MongoDBServerConfig;
import com.inter3i.sun.persistence.RepositoryFactory;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;
import java.util.Iterator;

/**
 * Created by boxiaotong on 2017/6/5.
 */
@Service
public class TaskScheduledService {

    private static final String PRIM_KEY_ID = "_id";
    private static final String JOB_TYPE="jobType";
    private static final String CACHE_NAME="cacheName";
    private static final String STATUS="status";
    private static final String COLLECTION_NAME="task_schedule";
    private MongoCollection collection;

    public TaskScheduledService() throws UnknownHostException {


        MongoDBServerConfig config = MongoDBServerConfig.getConfigByDataSourceName("status");
        String ip=config.getMongoDBIp();
        int port=config.getMongoDBPort();
        String database=config.getDbName();
        collection = RepositoryFactory.getMongoClient(database, COLLECTION_NAME, ip,port);
    }

    public Document findOne(String jobName, String cacheName) {

        Bson filter1 = Filters.eq(JOB_TYPE, jobName);
        Bson filter2 = Filters.eq(CACHE_NAME, cacheName);
        Bson bson = Filters.and(filter1, filter2);
        FindIterable iterable = collection.find(bson);
        Iterator<Document> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
    public Boolean getStatus(String jobType, String cacheName) {
        Document document=findOne(jobType,cacheName);
        if(document==null){
            return true;//默认开启
        }
        return document.getBoolean(STATUS);
    }

    public String insert(String jobType, String cacheName) {
        Document docDB = findOne(jobType,cacheName);
        if(docDB!=null){
            return "Document is exist,jobType="+jobType+",cacheName="+cacheName;
        }
        Document document = new Document();
        document.put(JOB_TYPE, jobType);
        document.put(CACHE_NAME, cacheName);
        document.put(STATUS, true);
        collection.insertOne(document);
        return "success";
    }

    public String updateOne(String jobType, String cacheName, Boolean status) {
        Document document = this.findOne(jobType, cacheName);
        if(document==null){
            return "Document is not exist";
        }
        Object idObj = document.get(PRIM_KEY_ID);
        Bson filter = Filters.eq(PRIM_KEY_ID, idObj);
        Document newDoc=new Document();
        newDoc.put(JOB_TYPE,jobType);
        newDoc.put(CACHE_NAME,cacheName);
        newDoc.put(STATUS,status);
        collection.updateOne(filter, new Document("$set", newDoc));
        return "success";
    }

    public Iterator<Document> findList() {
        FindIterable iterable = collection.find();
        Iterator<Document> iterator = iterable.iterator();
        return iterator;
    }
}
