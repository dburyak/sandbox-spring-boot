package com.dburyak.sandbox.sandboxspringboot.migration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;

@ChangeLog(order = "001")
@Log4j2
public class Changelog_001 {

    @ChangeSet(order = "001", id = "insert admin user", author = "dmytro.buryak@gmail.com")
    public void insertAdminUser(MongoDatabase mongo) {
        log.info("running mongock changeset: insert admin user");
        /*mongo.getCollection("users").insertOne(Document.parse("{ " +
                "firstName: \"admin\", " +
                "lastName: \"admin\"" +
                "}"));*/
    }
}
