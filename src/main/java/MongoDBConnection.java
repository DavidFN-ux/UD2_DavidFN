import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

    private static MongoDatabase database;

    public static MongoDatabase connectToMongoDB() {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://admin:admin123@cluster0.iffbl.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
            database = mongoClient.getDatabase("TrabajoUD2");
            System.out.println("Conexión a MongoDB exitosa.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al conectar con MongoDB.");
        }
        return database;
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            connectToMongoDB();
        }
        return database;
    }


}
