/*
 *   Copyright (c) 2016, inter3i.com. All rights reserved.
 *
 *   Author: dq
 *   Created: 11/18/16 6:01 PM
 *   Description:
 *
 */

package com.inter3i.sun.persistence;

import com.inter3i.sun.persistence.impl.MongoRepositoryFactory;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class RepositoryFactory {
    //private static Map<String, Repository> repositoryMap = new HashMap<String, Repository>(8);
    private static Map<String, MongoCollection> mogoClientsMap = new HashMap<String, MongoCollection>(8);


    /*public static Repository getRepository(String dbName, String tableName, Class<? extends Entity> clazz) {
        String repositoryName = dbName + "_+_" + tableName;
        if (repositoryMap.containsKey(repositoryName)) {
            return repositoryMap.get(repositoryName);
        }
        synchronized (repositoryMap) {
            if (!repositoryMap.containsKey(repositoryName)) {
                //MongoRepositoryFactory factory = MongoRepositoryFactory.fromDefaultDb(dbName);
                MongoRepositoryFactory factory = new MongoRepositoryFactory("mongodb://127.0.0.1:27017/" + dbName);

                //MongoRepositoryFactory.fromDefaultDb(dbName);
                //"mongodb://localhost/" + dbName;
                Repository repository = factory.createRepository(clazz, tableName);
                repositoryMap.put(repositoryName, repository);
                return repository;
            } else {
                return repositoryMap.get(repositoryName);
            }
        }
    }*/

    public static MongoCollection getMongoClient(String dbName, String tableName, final String serverAdds, final int port) throws UnknownHostException {
        String repositoryName = dbName + "_+_" + tableName;
        if (mogoClientsMap.containsKey(repositoryName)) {
            return mogoClientsMap.get(repositoryName);
        }
        synchronized (mogoClientsMap) {
            if (!mogoClientsMap.containsKey(repositoryName)) {
                //MongoRepositoryFactory factory = MongoRepositoryFactory.fromDefaultDb(dbName);
                //40000
                //MongoRepositoryFactory.fromDefaultDb(dbName);
                //"mongodb://localhost/" + dbName;
                // DBCollection collection = initClient(dbName, tableName);
                MongoCollection collection = getMongoClientNew(dbName, tableName, serverAdds, port);
                mogoClientsMap.put(repositoryName, collection);
                return collection;
            } else {
                return mogoClientsMap.get(repositoryName);
            }
        }
    }

    public static void destoryAllClient() {
        Iterator<String> iterator = mogoClientsMap.keySet().iterator();
    }



   /* public static DBCollection initClient(String dbName, String tableName) throws UnknownHostException {
        ServerAddress ssAddress = new ServerAddress("127.0.0.1", 27017);
        // MongoOptions options1 = new MongoOptions();
        // options1.connectionsPerHost = 100;
        Mongo mongo = new Mongo(ssAddress);
        MongoOptions options = mongo.getMongoOptions();
        options.connectionsPerHost = 100;
        // get db connection error
        // options.autoConnectRetry = true;
        // options.slaveOk = true;
        options.threadsAllowedToBlockForConnectionMultiplier = 1000;
        DB db = mongo.getDB(dbName);
        return db.getCollection(tableName);
    }*/

    public static MongoCollection getMongoClientNew(String dbName, String tableName, final String serverAdds, final int port) {
        //ServerAddress ssAddress = new ServerAddress("127.0.0.1", 27017);
        ServerAddress ssAddress = new ServerAddress(serverAdds, port);
        //ServerAddress ssAddress = new ServerAddress("192.168.0.20", 40000);
        MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(100).
                threadsAllowedToBlockForConnectionMultiplier(50).build();
        MongoClient mongoClient = new MongoClient(ssAddress, options);
        // 连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
        MongoCollection collection = mongoDatabase.getCollection(tableName);
        return collection;
    }


    public static RepositoryFactory create(RepositoryType repositoryType, String connectionString) {
        switch (repositoryType) {
            case MONGODB:
                return new MongoRepositoryFactory(connectionString);
            default:
                throw new NonSupportException("no supported repository type");
        }
    }

    public abstract <E extends Entity, Q> Repository<E, Q> createRepository(Class<E> clazz, String collectionName);
}
