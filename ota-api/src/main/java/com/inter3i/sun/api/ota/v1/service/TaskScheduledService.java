package com.inter3i.sun.api.ota.v1.service;

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
    private MongoCollection collection = RepositoryFactory.getMongoClient("3idata", "task_schedule", "192.168.0.20", 40000);

    public TaskScheduledService() throws UnknownHostException {

    }

    public Document findOne(String jobName, String cacheName) {

        Bson filter1 = Filters.eq("jobName", jobName);
        Bson filter2 = Filters.eq("cacheName", cacheName);
        Bson bson = Filters.and(filter1, filter2);
        FindIterable iterable = collection.find(bson);
        Iterator<Document> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public String insert(String jobName, String cacheName) {
        Document document = new Document();
        document.put("jobName", jobName);
        document.put("cacheName", cacheName);
        document.put("status", true);
        collection.insertOne(document);
        return "success";
    }

    public String updateOne(String jobName, String cacheName, Boolean status) {
        Document document = this.findOne(jobName, cacheName);
        if(document==null){
            return "Document is not exist";
        }
        Object idObj = document.get(PRIM_KEY_ID);
        Bson filter = Filters.eq("_id", idObj);
        Document newDoc=new Document();
        newDoc.put("jobName",jobName);
        newDoc.put("cacheName",cacheName);
        newDoc.put("status",status);
        collection.updateOne(filter, new Document("$set", newDoc));
        return "success";
    }

    public Iterator<Document> findList() {
        FindIterable iterable = collection.find();
        Iterator<Document> iterator = iterable.iterator();
        return iterator;
    }
}
