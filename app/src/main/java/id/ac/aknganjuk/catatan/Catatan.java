package id.ac.aknganjuk.catatan;

public class Catatan {
    public static final String NAMA_TABEL = "catatan";
    public static final String KOLOM_ID = "id";
    public static final String KOLOM_CATATAN = "catatan";
    public static final String KOLOM_TIMESTAMP = "timestamp";

    //query CREATE TABLE
    public static final String CREATE_TABLE = "CREATE TABLE " + NAMA_TABEL
            + "(" + KOLOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KOLOM_CATATAN + " TEXT, "
            + KOLOM_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    private int id;
    private String catatan;
    private String timestamp;

    public Catatan(int id, String catatan, String timestamp) {
        this.id = id;
        this.catatan = catatan;
        this.timestamp = timestamp;
    }

    public Catatan() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
