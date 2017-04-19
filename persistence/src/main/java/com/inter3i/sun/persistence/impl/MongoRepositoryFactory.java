/*
 *   Copyright (c) 2016, inter3i.com. All rights reserved.
 *
 *   Author: dq
 *   Created: 11/18/16 6:00 PM
 *   Description:
 *
 */

package com.inter3i.sun.persistence.impl;

import com.inter3i.sun.persistence.Entity;
import com.inter3i.sun.persistence.Repository;
import com.inter3i.sun.persistence.RepositoryFactory;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

public class MongoRepositoryFactory extends RepositoryFactory {
    private static final Logger logger = LoggerFactory.getLogger(MongoRepositoryFactory.class);

    private MongoTemplate mongoTemplate;

    public MongoRepositoryFactory(String connectionString) {
        Assert.notNull(connectionString);

        MongoClientURI uri = new MongoClientURI(connectionString);
        MongoClient mongoClient = new MongoClient(uri);
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient, uri.getDatabase());

        // TODO 将 _id存成String,目前为ObjectId类型
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, new MongoMappingContext());
        // 去除 _class 列
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        mongoTemplate = new MongoTemplate(mongoDbFactory, converter);
        logger.info("Constructed with connection string: '{}'", connectionString);
    }

    public static MongoRepositoryFactory fromDefaultDb(String dbName) {
        return new MongoRepositoryFactory("mongodb://localhost/" + dbName);
    }

    @Override
    public <E extends Entity, Query> MongoRepository createRepository(Class<E> clazz, String collectionName) {
        return new MongoRepository(mongoTemplate, clazz, collectionName);
    }

}
