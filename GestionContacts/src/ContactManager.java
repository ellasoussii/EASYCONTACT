import java.sql.*;  // classes du paquetage java.sql
import java.util.ArrayList; // des interface liste pour stocker des listes dynamique
import java.util.List; //type de retour pour la méthode getContacts()

public class ContactManager { //gére CRUD
    private static final String DB_URL = "jdbc:sqlite:contacts.db"; // esm el base de donnée te3i

    public ContactManager() {
        createTable(); // Créer la table à l'initialisation
        //pour assurer que le table existe
    }

    // Connexion à la base de données
    public Connection connect() { //une connexion à SQLite
        try {
            return DriverManager.getConnection(DB_URL); 
            //DB_URL fih lien ta3 base te3i
        } catch (SQLException e) {
            e.printStackTrace(); //Affiche la trace de l'erreur dans la console
            return null; //Retourne null si saret mochkla
        }
    }

    // Créer la table contacts si elle n'existe pas
    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS contacts (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "firstName TEXT NOT NULL," +
                     "lastName TEXT NOT NULL," +
                     "phone TEXT," +
                     "email TEXT UNIQUE NOT NULL)";
        
        try (Connection conn = connect(); //t5ou connection avec la base de donnée
             Statement stmt = conn.createStatement()){ //Crée un objet Statement à partir de la connexion conn
        	 //pour exécuter des requêtes SQL statiques
        	stmt.execute(sql); //execute  sql 
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ajouter un contact
    public void addContact(Contact contact) {
        String sql = "INSERT INTO contacts (firstName, lastName, phone, email) VALUES (?, ?, ?, ?)";
         

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	//PreparedStatement permet de définir les valeurs des placeholders en toute sécurité.
            pstmt.setString(1, contact.getNom());
            pstmt.setString(2, contact.getPrenom());
            pstmt.setString(3, contact.getTelephone());
            pstmt.setString(4, contact.getEmail()); //te5OU la quatrième valeur comme l'email, eli 5dhitou via contact.getEmail()
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer un contact par email
    public boolean removeContact(String email) {
        String sql = "DELETE FROM contacts WHERE email = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email); // bch n3awedh ? par email
            int affectedRows = pstmt.executeUpdate(); //Exécute la requête DELETE
            //stocke le nombre de lignes affectées (supprimées) dans affectedRows

            return affectedRows > 0; // si 1 contact est supprimé donc true  
        } catch (SQLException e) {
            e.printStackTrace();
            return false; //snn false
        }
    }

    public boolean contactExists(String email) {
        String sql = "SELECT 1 FROM contacts WHERE email = ?"; //SELECT 1 pour optimiser la requête

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email); //Définit la valeur du placeholder ? comme l'email fourni
            ResultSet rs = pstmt.executeQuery(); //Exécute la requête et récupère le résultat dans un ResultSe
            //executeQuery() est utilisé pour les requêtes SELECT

            return rs.next(); // Si au moins une ligne est retournée → contact existe
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Mettre à jour un contact par email
    public boolean updateContact(String oldEmail, Contact updatedContact) {
        String sql = "UPDATE contacts SET firstName = ?, lastName = ?, phone = ?, email = ? WHERE email = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, updatedContact.getPrenom()); //Définit la première valeur (firstName) comme le prénom du contact mis à jour
            pstmt.setString(2, updatedContact.getNom());
            pstmt.setString(3, updatedContact.getTelephone());
            pstmt.setString(4, updatedContact.getEmail());
            pstmt.setString(5, oldEmail); // L'ancien email (identifiant)

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Si au moins 1 ligne a été modifiée

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Obtenir tous les contacts
    public List<Contact> getContacts() { // récupère tous les contacts de la table contacts 
    	//et les retourne sous forme de List<Contact>
        List<Contact> contacts = new ArrayList<>(); //Crée une nouvelle ArrayList vide
        //pour stocker les objets Contact récupérés
        String sql = "SELECT * FROM contacts";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement(); //Statement pour exécuter la requête SQL
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
            //rs.next() avance le curseur à la ligne suivante et 
            	//retourne true s'il y a une ligne, false sinon
            	{
                Contact contact = new Contact();
                contact.setNom(rs.getString("firstName"));
                contact.setPrenom(rs.getString("lastName")); //Définit le prénom en récupéran
                //t la valeur de la colonne lastName
                contact.setTelephone(rs.getString("phone"));
                contact.setEmail(rs.getString("email"));
                contacts.add(contact); //Ajoute l'objet Contact à la liste contacts
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contacts;
    }
}


