/*
 *   Copyright (c) 2016, inter3i.com. All rights reserved.
 *
 *   Author: dq
 *   Created: 11/18/16 6:00 PM
 *   Description:
 *
 */

package com.inter3i.sun.persistence;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import junit.framework.TestCase;

public abstract class MongoTestBase extends TestCase {

    private static final MongodStarter starter = MongodStarter.getDefaultInstance();
    private static final int PORT = 27017;

    private MongodExecutable mongodExe;
    private MongodProcess mongod;
    private MongoClient mongoClient;

    @Override
    protected void setUp() throws Exception {

        mongodExe = starter.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(PORT, Network.localhostIsIPv6()))
                .build());
        mongod = mongodExe.start();

        super.setUp();

        mongoClient = new MongoClient("localhost", PORT);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        mongod.stop();
        mongodExe.stop();
    }

    public Mongo getMongoClient() {
        return mongoClient;
    }

}
