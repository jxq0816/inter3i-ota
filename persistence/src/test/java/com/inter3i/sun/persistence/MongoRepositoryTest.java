/*
 *   Copyright (c) 2016, inter3i.com. All rights reserved.
 *
 *   Author: dq
 *   Created: 11/18/16 6:00 PM
 *   Description:
 *
 */

package com.inter3i.sun.persistence;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.inter3i.sun.persistence.impl.MongoRepository;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoRepositoryTest extends MongoTestBase {

    @Test
    public void testOneEntityCRUD() {
        RepositoryFactory rf = RepositoryFactory.create(RepositoryType.MONGODB, "mongodb://localhost/testdb");
        MongoRepository<Person> personRepository = (MongoRepository) rf.createRepository(Person.class, "persons");
        Person dq = new Person();
        String id = new ObjectId().toString();
        dq.setId(id);
        dq.setName("dq");
        dq.setAge(30);
        dq.addProperty("weight", "70kg");
        dq.addProperty("sex", "male");
        personRepository.insert(dq);
        Person result = personRepository.findById(id);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getName().equals("dq"));
        Assert.assertSame(result.getAge(), 30);
        Assert.assertNotNull(result.getProperties());
        Assert.assertSame(result.getProperties().size(), 2);
        Assert.assertTrue(result.getProperties().containsKey("weight"));
        Assert.assertTrue(result.getProperties().get("weight").equals("70kg"));
        Assert.assertTrue(result.getProperties().containsKey("sex"));
        Assert.assertTrue(result.getProperties().get("sex").equals("male"));

        result.setAge(31);
        personRepository.save(result);
        result = personRepository.findById(id);
        Assert.assertSame(result.getAge(), 31);

        personRepository.removeById(id);
        result = personRepository.findById(id);
        Assert.assertNull(result);
    }

    @Test
    public void testEntitiesCRUD() {
        RepositoryFactory rf = RepositoryFactory.create(RepositoryType.MONGODB, "mongodb://localhost/testdb");
        MongoRepository<Person> personRepository = (MongoRepository) rf.createRepository(Person.class, "persons");
        Person p1 = new Person();
        String id1 = new ObjectId().toString();
        p1.setId(id1);
        p1.setName("p1");
        p1.setAge(30);
        p1.addProperty("weight", "70kg");
        p1.addProperty("sex", "male");
        personRepository.insert(p1);

        Person p2 = new Person();
        String id2 = new ObjectId().toString();
        p2.setId(id2);
        p2.setName("p2");
        p2.setAge(31);
        p2.addProperty("weight", "60kg");
        p2.addProperty("sex", "female");
        personRepository.insert(p2);

        Person p1Result = personRepository.findById(id1);
        Assert.assertNotNull(p1Result);
        Assert.assertTrue(p1Result.getName().equals("p1"));
        Assert.assertSame(p1Result.getAge(), 30);
        Assert.assertNotNull(p1Result.getProperties());
        Assert.assertSame(p1Result.getProperties().size(), 2);
        Assert.assertTrue(p1Result.getProperties().containsKey("weight"));
        Assert.assertTrue(p1Result.getProperties().get("weight").equals("70kg"));
        Assert.assertTrue(p1Result.getProperties().containsKey("sex"));
        Assert.assertTrue(p1Result.getProperties().get("sex").equals("male"));

        Person p2Result = personRepository.findById(id2);
        Assert.assertNotNull(p2Result);
        Assert.assertTrue(p2Result.getName().equals("p2"));
        Assert.assertSame(p2Result.getAge(), 31);
        Assert.assertNotNull(p2Result.getProperties());
        Assert.assertSame(p2Result.getProperties().size(), 2);
        Assert.assertTrue(p2Result.getProperties().containsKey("weight"));
        Assert.assertTrue(p2Result.getProperties().get("weight").equals("60kg"));
        Assert.assertTrue(p2Result.getProperties().containsKey("sex"));
        Assert.assertTrue(p2Result.getProperties().get("sex").equals("female"));

        Iterable<Person> persons = personRepository.findAll();
        List<String> names = Lists.newArrayList("p1", "p2");
        for (Person person : persons) {
            Assert.assertTrue(Iterables.any(names, Predicates.equalTo(person.getName())));
            if (person.getName().equals("p1")) {
                Assert.assertSame(person.getAge(), 30);
                Assert.assertNotNull(person.getProperties());
                Assert.assertSame(person.getProperties().size(), 2);
                Assert.assertTrue(person.getProperties().containsKey("weight"));
                Assert.assertTrue(person.getProperties().get("weight").equals("70kg"));
                Assert.assertTrue(person.getProperties().containsKey("sex"));
                Assert.assertTrue(person.getProperties().get("sex").equals("male"));
            } else {
                Assert.assertSame(person.getAge(), 31);
                Assert.assertNotNull(person.getProperties());
                Assert.assertSame(person.getProperties().size(), 2);
                Assert.assertTrue(person.getProperties().containsKey("weight"));
                Assert.assertTrue(person.getProperties().get("weight").equals("60kg"));
                Assert.assertTrue(person.getProperties().containsKey("sex"));
                Assert.assertTrue(person.getProperties().get("sex").equals("female"));
            }
        }

        p1Result.setAge(31);
        personRepository.save(p1Result);
        p1Result = personRepository.findById(id1);
        Assert.assertSame(p1Result.getAge(), 31);

        personRepository.removeById(id1);
        p1Result = personRepository.findById(id1);
        Assert.assertNull(p1Result);
        personRepository.removeById(id2);
        p2Result = personRepository.findById(id2);
        Assert.assertNull(p2Result);
    }


    private static class Person extends Entity {
        private String name;
        private int age;
        private Map<String, String> properties = new HashMap<String, String>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public void addProperty(String key, String value) {
            this.properties.put(key, value);
        }
    }
}
