package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class OSQLdaneStawka extends ObslugaSQL {

    private static final String DICTIONARY_TABLE_NAME = "BZCZBD_Stawki";
    private static final String[][] DICTIONARY_TABLE_4_ROWS = {{"firma_id", "stawka", "poczatek", "koniec"},
            {"integer", "real", "TEXT", "TEXT"}};
    private static final String[][] DICTIONARY_TABLE_ROWS_4_FOREIGN = {{"firma_id"},
            {"BZCZBD_Firmy(_id)"}};


    public OSQLdaneStawka(Context context) {
        super(context);
    }

    public long dodajDane(daneStawka dane){

        ContentValues wartosci = new ContentValues();
        wartosci.put("firma_id", dane.getFirma_id());
        wartosci.put("stawka", dane.getStawka());
        wartosci.put("poczatek", dane.getPoczatek());
        wartosci.put("koniec", dane.getKoniec());
        wartosci.put("uwagi", dane.getUwagi());
        wartosci.put("czy_widoczny", dane.getCzy_widoczny());

        long idRekordu = -1;
        idRekordu = dodajDaneOSQL(DICTIONARY_TABLE_NAME, wartosci);
        return idRekordu;
    }

    public List<daneStawka> dajWszystkieDoRecyclerView(){
        String zapytanie = "SELECT a._id AS _id, a.stawka AS stawka, a.poczatek AS poczatek, a.koniec AS koniec, " +
                "a.firma_id AS firma_id, a.uwagi AS uwagi, b.nazwa AS firma_nazwa " +
                "FROM " + DICTIONARY_TABLE_NAME + " AS a INNER JOIN " + DICTIONARY_TABLE_NAME_1 + " AS b ON a.firma_id = b._id";
        return dajDane(zapytanie);
    }

    public List<daneStawka> dajWszystkie(){
        String zapytanie = "SELECT a._id AS _id, a.stawka AS stawka, a.poczatek AS poczatek, a.koniec AS koniec, " +
                "a.firma_id AS firma_id, a.uwagi AS uwagi, b.nazwa AS firma_nazwa " +
                "FROM " + DICTIONARY_TABLE_NAME + " AS a INNER JOIN " + DICTIONARY_TABLE_NAME_1 + " AS b ON a.firma_id = b._id";
        return dajDane(zapytanie);
    }

    public daneStawka dajOkreslonyRekord(Integer _id){
        String zapytanie = "SELECT a._id AS _id, a.stawka AS stawka, a.poczatek AS poczatek, a.koniec AS koniec, " +
                "a.firma_id AS firma_id, a.uwagi AS uwagi, b.nazwa AS firma_nazwa " +
                "FROM " + DICTIONARY_TABLE_NAME + " AS a INNER JOIN " + DICTIONARY_TABLE_NAME_1 + " AS b ON a.firma_id = b._id WHERE a._id = " + _id;

        return dajDane1(zapytanie);
    }

    public List<daneStawka> dajDane(String zapytanie){
        Log.d("DebugCSQL:", "dajWszystkieZlecenia");
        List<daneStawka> dane_funkcji = new LinkedList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor kursor = db.rawQuery(zapytanie, null);

        if (kursor != null) {
            kursor.moveToFirst();
            while (!kursor.isAfterLast()) {
                //Log.d(tag1,String.valueOf(kursor.getCount()));
                daneStawka dana_funkcji = cursorDane(kursor);
                dane_funkcji.add(dana_funkcji);
                kursor.moveToNext();
            }//while (!kursor.isAfterLast()) {
            Log.d("DebugCSQL:", "daj dane OSQLStawki");
        }//if (kursor.getCount() > 0) {
        else{
            daneStawka dana_funkcji = new daneStawka();
            dana_funkcji.onCreate();
            dane_funkcji.add(dana_funkcji);
        }
        db.close();
        return dane_funkcji;
    }//public List<daneAuta> dajWszystkieAuta(){

    public daneStawka dajDane1(String zapytanie){
        //Pobiera dane okreslonego wiersza
        Log.d("DebugCSQL:", "dajWszystkieAuta");
        daneStawka dane_funkcji = new daneStawka();

        SQLiteDatabase db = getReadableDatabase();

        //Cursor kursor = db.query(DICTIONARY_TABLE_NAME_2, kolumny, null, null, null, null, null);
        Cursor kursor = db.rawQuery(zapytanie, null);
        if (kursor != null) {
            kursor.moveToFirst();
            //Log.d("kursor: ", String.valueOf(kursor));
            while (!kursor.isAfterLast()) {
                dane_funkcji = cursorDane(kursor);
                kursor.moveToNext();
            }//while (!kursor.isAfterLast()) {
            Log.d("DebugCSQL:", "msg1");
        }//if (kursor.getCount() > 0) {
        else{
            dane_funkcji = new daneStawka();
        }
        db.close();
        return dane_funkcji;
    }//public List<daneAuta> dajWszystkieAuta(){

    public void updateDane(daneStawka dane_funkcji) {
        ContentValues wartosci = contentValues(dane_funkcji);

        updateDaneOSQL(DICTIONARY_TABLE_NAME, wartosci, dane_funkcji.getId());

    }

    private ContentValues contentValues(daneStawka dane_funkcji){
        ContentValues wartosci = new ContentValues();
        wartosci.put("firma_id", dane_funkcji.getFirma_id());
        wartosci.put("stawka", dane_funkcji.getStawka());
        wartosci.put("poczatek", dane_funkcji.getPoczatek());
        wartosci.put("koniec", dane_funkcji.getKoniec());
        wartosci.put("uwagi", dane_funkcji.getUwagi());
        return wartosci;
    }

    //kursor dla odczytu danych z bazy
    private daneStawka cursorDane(Cursor kursor) {
        daneStawka dane_funkcji = new daneStawka();

        if (kursor.getColumnIndex("_id") > -1) {
            dane_funkcji.setId(kursor.getInt(kursor.getColumnIndex("_id")));
        }

        if (kursor.getColumnIndex("firma_id") > -1) {
            dane_funkcji.setFirma_id(kursor.getInt(kursor.getColumnIndexOrThrow("firma_id")));
        }

        if (kursor.getColumnIndex("firma_nazwa") > -1) {
            dane_funkcji.setFirma_nazwa(kursor.getString(kursor.getColumnIndexOrThrow("firma_nazwa")));
        }
        if (kursor.getColumnIndex("stawka") > -1) {
            dane_funkcji.setStawka(kursor.getFloat(kursor.getColumnIndexOrThrow("stawka")));
        }
        if (kursor.getColumnIndex("poczatek") > -1) {
            dane_funkcji.setPoczatek(kursor.getString(kursor.getColumnIndexOrThrow("poczatek")));
        }
        if (kursor.getColumnIndex("koniec") > -1) {
            dane_funkcji.setKoniec(kursor.getString(kursor.getColumnIndexOrThrow("koniec")));
        }

        if (kursor.getColumnIndex("uwagi") > -1) {
            dane_funkcji.setUwagi(kursor.getString(kursor.getColumnIndexOrThrow("uwagi")));
        }
        if (kursor.getColumnIndex("poprzedni_rekord_id") > -1) {
            dane_funkcji.setPoprzedni_rekord_id(kursor.getInt(kursor.getColumnIndex("poprzedni_rekord_id")));
        }
        if (kursor.getColumnIndex("poprzedni_rekord_data_usuniecia") > -1) {
            dane_funkcji.setPoprzedni_rekord_data_usuniecia(kursor.getString(kursor.getColumnIndex("poprzedni_rekord_data_usuniecia")));
        }
        if (kursor.getColumnIndex("poprzedni_rekord_powod_usuniecia") > -1) {
            dane_funkcji.setPoprzedni_rekord_powod_usuniecia(kursor.getString(kursor.getColumnIndex("poprzedni_rekord_powod_usuniecia")));
        }
        if (kursor.getColumnIndex("czy_widoczny") > -1) {
            dane_funkcji.setCzy_widoczny(kursor.getInt(kursor.getColumnIndex("czy_widoczny")));
        }
        return dane_funkcji;
    }//private daneFirma cursorDane(Cursor kursor){

}
