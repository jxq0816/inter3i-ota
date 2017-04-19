/*
 *   Copyright (c) 2016, inter3i.com. All rights reserved.
 *
 *   Author: dq
 *   Created: 11/18/16 6:01 PM
 *   Description:
 *
 */

package com.inter3i.sun.persistence.impl;

import com.inter3i.sun.persistence.Entity;
import com.inter3i.sun.persistence.IQuery;
import com.inter3i.sun.persistence.Repository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import java.util.Collection;

public class MongoRepository<E extends Entity> implements Repository<E, Query> {

    private MongoTemplate mongoTemplate;
    private Class<E> classOfE;
    private String collectionName;

    public MongoRepository(MongoTemplate mongoTemplate, Class<E> classOfE, String collectionName) {
        Assert.notNull(mongoTemplate, "mongoTemplate");
        Assert.notNull(classOfE, "classOfE");
        Assert.notNull(collectionName, "collectionName");

        this.mongoTemplate = mongoTemplate;
        this.classOfE = classOfE;
        this.collectionName = collectionName;
    }

    public E findById(String id) {
        Assert.notNull(id);
        return mongoTemplate.findById(id, classOfE, collectionName);
    }

    public Iterable<E> findAll() {
        return mongoTemplate.findAll(classOfE, collectionName);
    }

    public Iterable<E> findBy(IQuery<Query> query) {
        return mongoTemplate.find(query.buildQuery(), classOfE, collectionName);
    }

    public void insert(E entity) {
        Assert.notNull(entity, "entity");
        if (entity.getId() == null) {
            entity.setId(new ObjectId().toString());
        }
        mongoTemplate.insert(entity, collectionName);
    }

    public void insert(Collection<E> entities) {
        Assert.notNull(entities, "entities");
        mongoTemplate.insert(entities, collectionName);
    }

    public void save(E entity) {
        Assert.notNull(entity);
        mongoTemplate.save(entity, collectionName);
    }

    public void removeById(String id) {
        Assert.notNull(id);
        mongoTemplate.remove(new Query(Criteria.where("_id").is(id)), collectionName);
    }
}
