package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class OSQLdaneSynchronizacja extends ObslugaSQL {

    protected static final String DICTIONARY_TABLE_NAME = "BZCZBD_DaneSynchronizacja";

    private static final String[][] DICTIONARY_TABLE_5_ROWS = {{"login", "haslo", "link", "kod_urzadzenia"},
            {"text", "text", "text", "text"}};
    private static final String[][] DICTIONARY_TABLE_ROWS_5_FOREIGN = {{},
            {}};

    public OSQLdaneSynchronizacja(Context context) {
        super(context);
    }

    public long dodajDane(daneSynchronizacja dane_funkcji){

        ContentValues wartosci = contentValues(dane_funkcji);
        long idRekordu = -1;
        idRekordu = dodajDaneOSQL(DICTIONARY_TABLE_NAME, wartosci);
        return idRekordu;
    }

    public List<daneSynchronizacja> dajWszystkieDoRecyclerView(){
        String zapytanie = "SELECT _id, login, haslo, link, kod_urzadzenia, " +
                "uwagi " +
                "FROM " + DICTIONARY_TABLE_NAME;
        return dajDane(zapytanie);
    }

    public List<daneSynchronizacja> dajWszystkie(){
        String zapytanie = "SELECT _id, login, haslo, link, kod_urzadzenia, " +
                "uwagi " +
                "FROM " + DICTIONARY_TABLE_NAME;
        return dajDane(zapytanie);
    }

    public daneSynchronizacja dajOkreslonyRekord(Integer _id){
        String zapytanie = "SELECT _id, login, haslo, link, kod_urzadzenia, " +
                "uwagi " +
                "FROM " + DICTIONARY_TABLE_NAME + " WHERE _id = " + _id;

        return dajDane1(zapytanie);
    }

    private List<daneSynchronizacja> dajDane(String zapytanie){
        Log.d("DebugCSQL:", "dajWszystkieUstawienia");
        List<daneSynchronizacja> dane_funkcji = new LinkedList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor kursor = db.rawQuery(zapytanie, null);

        if (kursor != null) {
            kursor.moveToFirst();
            while (!kursor.isAfterLast()) {
                //Log.d(tag1,String.valueOf(kursor.getCount()));
                daneSynchronizacja dana_funkcji = cursorDane(kursor);
                dane_funkcji.add(dana_funkcji);
                kursor.moveToNext();
            }//while (!kursor.isAfterLast()) {
            //Log.d("DebugCSQL:", "daj dane OSQLStawki");
        }//if (kursor.getCount() > 0) {
        else{
            daneSynchronizacja dana_funkcji = new daneSynchronizacja();
            dana_funkcji.onCreate();
            dane_funkcji.add(dana_funkcji);
        }
        db.close();
        return dane_funkcji;
    }//public List<daneAuta> dajWszystkieAuta(){

    private daneSynchronizacja dajDane1(String zapytanie){
        //Pobiera dane okreslonego wiersza
        Log.d("DebugCSQL:", "dajWszystkieUstawineia");
        daneSynchronizacja dane_funkcji = new daneSynchronizacja();

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
            dane_funkcji = new daneSynchronizacja();
        }
        db.close();
        return dane_funkcji;
    }//public List<daneAuta> dajWszystkieAuta(){

    public void updateDane(daneSynchronizacja dane_funkcji) {
        ContentValues wartosci = contentValues(dane_funkcji);

        updateDaneOSQL(DICTIONARY_TABLE_NAME, wartosci, dane_funkcji.getId());

    }

    private ContentValues contentValues(daneSynchronizacja dane_funkcji){
        ContentValues wartosci = new ContentValues();

        wartosci.put("login", dane_funkcji.getLogin());
        wartosci.put("haslo", dane_funkcji.getHaslo());
        wartosci.put("link", dane_funkcji.getLink());
        wartosci.put("uwagi", dane_funkcji.getUwagi());
        wartosci.put("kod_urzadzenia", dane_funkcji.getKod_urzadzenia());

        return wartosci;
    }

    //kursor dla odczytu danych z bazy
    private daneSynchronizacja cursorDane(Cursor kursor) {
        daneSynchronizacja dane_funkcji = new daneSynchronizacja();

        if (kursor.getColumnIndex("_id") > -1) {
            dane_funkcji.setId(kursor.getInt(kursor.getColumnIndex("_id")));
        }

        if (kursor.getColumnIndex("login") > -1) {
            dane_funkcji.setLogin(kursor.getString(kursor.getColumnIndexOrThrow("login")));
        }

        if (kursor.getColumnIndex("haslo") > -1) {
            dane_funkcji.setHaslo(kursor.getString(kursor.getColumnIndexOrThrow("haslo")));
        }

        if (kursor.getColumnIndex("link") > -1) {
            dane_funkcji.setLink(kursor.getString(kursor.getColumnIndexOrThrow("link")));
        }

        if (kursor.getColumnIndex("kod_urzadzenia") > -1) {
            dane_funkcji.setKod_urzadzenia(kursor.getString(kursor.getColumnIndexOrThrow("kod_urzadzenia")));
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
}