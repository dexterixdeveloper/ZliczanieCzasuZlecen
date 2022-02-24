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

    public String getTableName(){
        return DICTIONARY_TABLE_NAME;
    }

    public OSQLdaneStawka(Context context) {
        super(context);
    }

    public long dodajDane(daneStawka dane_funkcji){

        ContentValues wartosci = contentValues(dane_funkcji);

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
                "a.firma_id AS firma_id, a.uwagi AS uwagi, a.synchron AS synchron, b.nazwa AS firma_nazwa, a.poprzedni_rekord_id AS poprzedni_rekord_id, " +
                "a.poprzedni_rekord_data_usuniecia AS poprzedni_rekord_data_usuniecia, " +
                "a.poprzedni_rekord_powod_usuniecia AS poprzedni_rekord_powod_usuniecia, a.czy_widoczny AS czy_widoczny " +
                "FROM " + DICTIONARY_TABLE_NAME + " AS a INNER JOIN " + DICTIONARY_TABLE_NAME_1 + " AS b ON a.firma_id = b._id";
        return dajDane(zapytanie);
    }

    public List<daneStawka> dajDoSynchronizacji(){
        String zapytanie = "SELECT a._id AS _id, a.stawka AS stawka, a.poczatek AS poczatek, a.koniec AS koniec, " +
                "a.firma_id AS firma_id, a.uwagi AS uwagi, a.synchron AS synchron, b.nazwa AS firma_nazwa, a.poprzedni_rekord_id AS poprzedni_rekord_id, " +
                "a.poprzedni_rekord_data_usuniecia AS poprzedni_rekord_data_usuniecia, " +
                "a.poprzedni_rekord_powod_usuniecia AS poprzedni_rekord_powod_usuniecia, a.czy_widoczny AS czy_widoczny, a.data_utworzenia AS data_utworzenia, a.data_synchronizacji AS data_synchronizacji " +
                "FROM " + DICTIONARY_TABLE_NAME + " AS a INNER JOIN " + DICTIONARY_TABLE_NAME_1 + " AS b ON a.firma_id = b._id " +// WHERE a.synchron = 0 OR a.synchron IS NULL";
                "WHERE a.data_synchronizacji IN ('0', '1')";
                //< 2 bo 0 oznacza nie zsynchronizowany rekord, 1 zaktualizowany rekord, a jakakolwiek liczba oznacza datę synchronizacji
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

    public long dodajZastapDane(daneStawka dane_funkcji){

        ContentValues wartosci = contentValues(dane_funkcji);

        //Log.d("SQL: dDMT", wartosci.toString());

        //dodajDaneOSQL(DICTIONARY_TABLE_NAME, wartosci);
        long idRekordu = -1;
        idRekordu = dodajZastapDaneOSQL(DICTIONARY_TABLE_NAME, wartosci);
        return idRekordu;
        //dodajDane(DICTIONARY_TABLE_NAME_12, wartosci);
    }

    public void zerujDateSynchronizacji(){
        zerujDateSynchronizacji(DICTIONARY_TABLE_NAME);
    }

    public void zerujDateUtworzenia(){
        zerujDateUtworzenia(DICTIONARY_TABLE_NAME);
    }

    private ContentValues contentValues(daneStawka dane_funkcji){
        ContentValues wartosci = new ContentValues();
        wartosci.put("firma_id", dane_funkcji.getFirma_id());
        wartosci.put("stawka", dane_funkcji.getStawka());
        wartosci.put("poczatek", dane_funkcji.getPoczatek());
        wartosci.put("koniec", dane_funkcji.getKoniec());
        wartosci.put("uwagi", dane_funkcji.getUwagi());
        wartosci.put("synchron", dane_funkcji.getSynchron());
        if(dane_funkcji.getData_synchronizacji() > 0){
            wartosci.put("data_utworzenia", dane_funkcji.getData_utworzenia());
        }
        if(dane_funkcji.getData_utworzenia() > 0){
            wartosci.put("data_synchronizacji", dane_funkcji.getData_synchronizacji());
        }
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
        if (kursor.getColumnIndex("synchron") > -1) {
            dane_funkcji.setSynchron(kursor.getInt(kursor.getColumnIndex("synchron")));
        }
        if (kursor.getColumnIndex("data_utworzenia") > -1) {
            dane_funkcji.setData_utworzenia(kursor.getLong(kursor.getColumnIndex("data_utworzenia")));
        }
        if (kursor.getColumnIndex("data_synchronizacji") > -1) {
            dane_funkcji.setData_synchronizacji(kursor.getLong(kursor.getColumnIndex("data_synchronizacji")));
        }
        return dane_funkcji;
    }//private daneFirma cursorDane(Cursor kursor){

    protected void updateDaneHurtemIdFirmy(Integer stare_id, Integer nowe_id) {
        ContentValues wartosci = new ContentValues();
        wartosci.put("firma_id", nowe_id);
        String[] args = {String.valueOf(stare_id)};
        String warunek = "firma_id = ?";
        updateDaneHurtem(DICTIONARY_TABLE_NAME, wartosci, warunek, args);
    }
}
