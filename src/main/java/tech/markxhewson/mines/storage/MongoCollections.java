package tech.markxhewson.mines.storage;

import lombok.Getter;

@Getter
public enum MongoCollections {

    USERS("users"),
    MINES("mines");

    private final String collectionId;

    MongoCollections(String collectionId) {
        this.collectionId = collectionId;
    }
}
