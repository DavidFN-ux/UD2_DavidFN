import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MainFrame {

    private JFrame frame;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainFrame window = new MainFrame();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainFrame() {
        // Conectar a la base de datos MongoDB
        MongoDBConnection.connectToMongoDB();
        database = MongoDBConnection.getDatabase();
        collection = database.getCollection("IA");

        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 317, 205);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JButton btnCrear = new JButton("Crear");
        btnCrear.addActionListener(e -> createDocument());
        btnCrear.setBounds(10, 24, 131, 58);
        frame.getContentPane().add(btnCrear);

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> consultDocument());
        btnConsultar.setBounds(10, 93, 131, 58);
        frame.getContentPane().add(btnConsultar);

        JButton btnBorrar = new JButton("Borrar");
        btnBorrar.addActionListener(e -> deleteDocument());
        btnBorrar.setBounds(151, 93, 131, 58);
        frame.getContentPane().add(btnBorrar);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> updateDocument());
        btnActualizar.setBounds(151, 24, 131, 58);
        frame.getContentPane().add(btnActualizar);
    }

    private void createDocument() {
        String id = JOptionPane.showInputDialog(frame, "Introduce el ID:");
        if (id == null) return;

        String nombre = JOptionPane.showInputDialog(frame, "Introduce el nombre de la IA:");
        if (nombre == null) return;

        String tipo = JOptionPane.showInputDialog(frame, "Introduce el tipo de IA:");
        if (tipo == null) return;

        String añoAparicion = JOptionPane.showInputDialog(frame, "Introduce el año de aparición:");
        if (añoAparicion == null) return;

        // Para coger la imagen
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);
        String imageName = null;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            imageName = fileChooser.getSelectedFile().getName();
        } else {
            JOptionPane.showMessageDialog(frame, "Operación cancelada.");
            return;
        }

        // Para crear la nueva IA en la base de datos
        Document newIA = new Document("_id", id)
                .append("id", id)
                .append("nombre", nombre)
                .append("tipo", tipo)
                .append("año_aparicion", añoAparicion)
                .append("imagen", imageName);

        collection.insertOne(newIA);
        JOptionPane.showMessageDialog(frame, "IA creada exitosamente en MongoDB.");
    }

    private void consultDocument() {
        String nameToConsult = JOptionPane.showInputDialog(frame, "Introduce el nombre de la IA a consultar:");
        if (nameToConsult == null) return;

        Document query = new Document("nombre", nameToConsult);
        Document ia = collection.find(query).first();

        if (ia != null) {
            String info = "Información de la IA:\n\n" +
                    "ID: " + ia.getString("id") + "\n" +
                    "Nombre: " + ia.getString("nombre") + "\n" +
                    "Tipo: " + ia.getString("tipo") + "\n" +
                    "Año de Aparición: " + ia.getString("año_aparicion") + "\n" +
                    "Imagen: " + ia.getString("imagen") + "\n";
            JOptionPane.showMessageDialog(frame, info, "Información de la IA", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "No se encontró ninguna IA con ese nombre.");
        }
    }

    private void deleteDocument() {
        String nameToDelete = JOptionPane.showInputDialog(frame, "Introduce el nombre de la IA a borrar:");
        if (nameToDelete == null) return;

        Document query = new Document("nombre", nameToDelete);
        long deletedCount = collection.deleteOne(query).getDeletedCount();

        if (deletedCount > 0) {
            JOptionPane.showMessageDialog(frame, "IA borrada exitosamente de MongoDB.");
        } else {
            JOptionPane.showMessageDialog(frame, "No se encontró ninguna IA con ese nombre.");
        }
    }

    private void updateDocument() {
        String nameToUpdate = JOptionPane.showInputDialog(frame, "Introduce el nombre de la IA a actualizar:");
        if (nameToUpdate == null) return;

        Document query = new Document("nombre", nameToUpdate);
        Document ia = collection.find(query).first();

        if (ia != null) {
            String id = JOptionPane.showInputDialog(frame, "Introduce el nuevo ID:", ia.getString("id"));
            String tipo = JOptionPane.showInputDialog(frame, "Introduce el nuevo tipo de IA:", ia.getString("tipo"));
            String añoAparicion = JOptionPane.showInputDialog(frame, "Introduce el nuevo año de aparición:", ia.getString("año_aparicion"));

            // Para coger la imagen
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(frame);
            String newImageName = null;
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                newImageName = fileChooser.getSelectedFile().getName();
            }

            // Para actualizar la informacion de la base de datos
            Document updatedIA = new Document("_id", id)
                    .append("id:", id)
                    .append("nombre:", nameToUpdate)
                    .append("tipo:", tipo)
                    .append("año_aparicion:", añoAparicion)
                    .append("imagen", newImageName != null ? newImageName : ia.getString("imagen"));

            collection.replaceOne(query, updatedIA);
            JOptionPane.showMessageDialog(frame, "IA actualizada exitosamente en MongoDB.");
        } else {
            JOptionPane.showMessageDialog(frame, "No se encontró ninguna IA con ese nombre.");
        }
    }
}
