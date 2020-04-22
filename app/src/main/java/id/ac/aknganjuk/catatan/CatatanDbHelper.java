package id.ac.aknganjuk.catatan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CatatanDbHelper extends SQLiteOpenHelper {
    //versi database
    private static final int DATABASE_VERSION = 1;
    //nama database
    private static final String DATABASE_NAME = "catatan_db";

    public CatatanDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //membuat tabel
        db.execSQL(Catatan.CREATE_TABLE);
        //masukkan data awal sebagai petunjuk penggunaan aplikasi ini
        String catatan = "Tap-lama utk edit/hapus catatan. Tap plus utk membuat catatan baru";
        db.execSQL("INSERT INTO " + Catatan.NAMA_TABEL
                + "(" + Catatan.KOLOM_CATATAN +") VALUES('" + catatan + "')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //proses upgrade database, khususnya jika ada perubahan struktur tabel:
        //1. drop tabel yang lama jika ada (semua data dihapus juga)
        db.execSQL("DROP TABLE IF EXISTS " + Catatan.NAMA_TABEL);
        //2. Buat tabel baru
        onCreate(db);
    }

    //insert catatan
    public long insertCatatan(String catatan){
        //buka database utk ditulisi
        SQLiteDatabase db = this.getWritableDatabase();
        //proses menulisi ke tabel, id dan timestamp akan di-isi secara otomatis
        ContentValues values = new ContentValues();
        values.put(Catatan.KOLOM_CATATAN, catatan);
        //tambahkan catatan ke tabel
        long id = db.insert(Catatan.NAMA_TABEL, null, values);
        //tutup koneksi ke database
        db.close();
        //return id yg baru ditambahkan
        return id;
    }

    //membaca satu catatan
    public Catatan getCatatan(long id){
        //buka database utk dibaca saja
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Catatan.NAMA_TABEL,
                new String[]{Catatan.KOLOM_ID, Catatan.KOLOM_CATATAN, Catatan.KOLOM_TIMESTAMP},
                Catatan.KOLOM_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        //persiapkan object catatan
        Catatan catatan = new Catatan(
                cursor.getInt(cursor.getColumnIndex(Catatan.KOLOM_ID)),
                cursor.getString(cursor.getColumnIndex(Catatan.KOLOM_CATATAN)),
                cursor.getString(cursor.getColumnIndex(Catatan.KOLOM_TIMESTAMP))
        );
        cursor.close();
        return catatan;
    }

    //ambil semua catatan
    public List<Catatan> getSemuaCatatan(){
        List<Catatan> catatanList = new ArrayList<>();

        //query SELECT semua data
        String selectQuery = "SELECT * FROM " + Catatan.NAMA_TABEL + " ORDER BY "
                + Catatan.KOLOM_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping semua baris dan tambahkan ke list
        if(cursor.moveToFirst()){
            do{
                Catatan catatan = new Catatan();
                catatan.setId(cursor.getInt(cursor.getColumnIndex(Catatan.KOLOM_ID)));
                catatan.setCatatan(cursor.getString(cursor.getColumnIndex(Catatan.KOLOM_CATATAN)));
                catatan.setTimestamp(cursor.getString(cursor.getColumnIndex(Catatan.KOLOM_TIMESTAMP)));

                catatanList.add(catatan);
            } while(cursor.moveToNext());
        }
        db.close();
        return catatanList;
    }

    //mengambil jumlah catatan
    public int getJumlahCatatan(){
        String countQuery = "SELECT * FROM " + Catatan.NAMA_TABEL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int jml = cursor.getCount();
        cursor.close();
        return jml;
    }

    //update catatan
    public int updateCatatan(Catatan catatan){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Catatan.KOLOM_CATATAN, catatan.getCatatan());

        //memperbarui baris catatan
        return db.update(Catatan.NAMA_TABEL, values, Catatan.KOLOM_ID + "=?",
                new String[]{String.valueOf(catatan.getId())});
    }

    //menghapus catatan
    public void deleteCatatan(Catatan catatan){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Catatan.NAMA_TABEL, Catatan.KOLOM_ID + "=?",
                new String[]{String.valueOf(catatan.getId())});
        db.close();
    }
}
