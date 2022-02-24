package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class OSQLdaneUstawienia extends ObslugaSQL {

    protected static final String DICTIONARY_TABLE_NAME = "BZCZBD_Settings";
    private static final String[][] DICTIONARY_TABLE_ROWS = {{"ustawienie", "wartosc", "typ_danych"},
            {"TEXT", "TEXT", "TEXT"}};
    private static final String[][] DICTIONARY_TABLE_ROWS_FOREIGN = {{},
            {}};
    
    public OSQLdaneUstawienia(Context context) {
        super(context);
    }

    public long dodajDane(daneUstawienia dane_funkcji){

        ContentValues wartosci = contentValues(dane_funkcji);
        long idRekordu = -1;
        idRekordu = dodajDaneOSQL(DICTIONARY_TABLE_NAME, wartosci);
        return idRekordu;
    }

    public List<daneUstawienia> dajWszystkieDoRecyclerView(){
        String zapytanie = "SELECT _id, ustawienie, wartosc, typ_danych, " +
                "uwagi " +
                "FROM " + DICTIONARY_TABLE_NAME;
        return dajDane(zapytanie);
    }

    public List<daneUstawienia> dajWszystkie(){
        String zapytanie = "SELECT _id, ustawienie, wartosc, typ_danych, " +
                "uwagi, synchron " +
                "FROM " + DICTIONARY_TABLE_NAME;
        return dajDane(zapytanie);
    }

    public daneUstawienia dajOkreslonyRekord(Integer _id){
        String zapytanie = "SELECT _id, ustawienie, wartosc, typ_danych, " +
                "uwagi " +
                "FROM " + DICTIONARY_TABLE_NAME + " WHERE _id = " + _id;

        return dajDane1(zapytanie);
    }

    private List<daneUstawienia> dajDane(String zapytanie){
        Log.d("DebugCSQL:", "dajWszystkieUstawienia");
        List<daneUstawienia> dane_funkcji = new LinkedList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor kursor = db.rawQuery(zapytanie, null);

        if (kursor != null) {
            kursor.moveToFirst();
            while (!kursor.isAfterLast()) {
                //Log.d(tag1,String.valueOf(kursor.getCount()));
                daneUstawienia dana_funkcji = cursorDane(kursor);
                dane_funkcji.add(dana_funkcji);
                kursor.moveToNext();
            }//while (!kursor.isAfterLast()) {
            //Log.d("DebugCSQL:", "daj dane OSQLStawki");
        }//if (kursor.getCount() > 0) {
        else{
            daneUstawienia dana_funkcji = new daneUstawienia();
            dana_funkcji.onCreate();
            dane_funkcji.add(dana_funkcji);
        }
        db.close();
        return dane_funkcji;
    }//public List<daneAuta> dajWszystkieAuta(){

    private daneUstawienia dajDane1(String zapytanie){
        //Pobiera dane okreslonego wiersza
        Log.d("DebugCSQL:", "dajWszystkieUstawineia");
        daneUstawienia dane_funkcji = new daneUstawienia();

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
            dane_funkcji = new daneUstawienia();
        }
        db.close();
        return dane_funkcji;
    }//public List<daneAuta> dajWszystkieAuta(){

    public void updateDane(daneUstawienia dane_funkcji) {
        ContentValues wartosci = contentValues(dane_funkcji);

        updateDaneOSQL(DICTIONARY_TABLE_NAME, wartosci, dane_funkcji.getId());

    }

    private ContentValues contentValues(daneUstawienia dane_funkcji){
        ContentValues wartosci = new ContentValues();
        wartosci.put("ustawienie", dane_funkcji.getUstawienie());
        wartosci.put("wartosc", dane_funkcji.getWartosc());
        wartosci.put("typ_danych", dane_funkcji.getTypDanych());
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
    private daneUstawienia cursorDane(Cursor kursor) {
        daneUstawienia dane_funkcji = new daneUstawienia();

        if (kursor.getColumnIndex("_id") > -1) {
            dane_funkcji.setId(kursor.getInt(kursor.getColumnIndex("_id")));
        }

        if (kursor.getColumnIndex("ustawienie") > -1) {
            dane_funkcji.setUstawienie(kursor.getString(kursor.getColumnIndexOrThrow("ustawienie")));
        }

        if (kursor.getColumnIndex("wartosc") > -1) {
            dane_funkcji.setWartosc(kursor.getString(kursor.getColumnIndexOrThrow("wartosc")));
        }

        if (kursor.getColumnIndex("typ_danych") > -1) {
            dane_funkcji.setTypDanych(kursor.getString(kursor.getColumnIndexOrThrow("typ_danych")));
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
