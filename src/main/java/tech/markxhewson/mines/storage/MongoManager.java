package tech.markxhewson.mines.storage;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import tech.markxhewson.mines.PrivateMines;

@Getter
public class MongoManager {

    private final PrivateMines plugin;

    private final String username, password, address, database;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private MongoCollection<Document> minesCollection;

    public MongoManager(PrivateMines plugin) {
        this.plugin = plugin;

        this.username = plugin.getConfig().getString("mongo.username");
        this.password = plugin.getConfig().getString("mongo.password");
        this.address = plugin.getConfig().getString("mongo.address");
        this.database = plugin.getConfig().getString("mongo.database");

        connect();
    }

    void connect() {
        ServerApi api = ServerApi.builder().version(ServerApiVersion.V1).build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://" + getUsername() + ":" + getPassword() + "@" + getAddress()))
                .serverApi(api)
                .build();

        mongoClient = MongoClients.create(settings);
        mongoDatabase = mongoClient.getDatabase(getDatabase());

        minesCollection = mongoDatabase.getCollection(MongoCollections.MINES.getCollectionId());
    }

    public boolean isConnected() {
        return getMongoClient() != null;
    }

    public void disconnect() {
        if (getMongoClient() == null) {
            return;
        }

        try {
            getMongoClient().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
