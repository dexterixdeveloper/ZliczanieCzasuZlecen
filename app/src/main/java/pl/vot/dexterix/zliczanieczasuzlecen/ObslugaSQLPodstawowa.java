package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public abstract class ObslugaSQLPodstawowa extends ObslugaSQL implements InterfejsDostepDoDanych {

    public ObslugaSQLPodstawowa(Context context) {
        super(context);
    }

    protected String wspolnaCzescZapytania = "a.poprzedni_rekord_id AS poprzedni_rekord_id, a.poprzedni_rekord_data_usuniecia AS poprzedni_rekord_data_usuniecia, " +
            "a.poprzedni_rekord_powod_usuniecia AS poprzedni_rekord_powod_usuniecia, a.czy_widoczny AS czy_widoczny, a.data_utworzenia AS data_utworzenia, a.data_synchronizacji AS data_synchronizacji," +
            "a.synchron AS synchron ";

     protected <T> List<T> dajDane(String zapytanie){
        List<T> dane_funkcji = new LinkedList<>();

        //ObslugaSQL osql = new ObslugaSQL(context);

        SQLiteDatabase db = getReadableDatabase();
        Cursor kursor = db.rawQuery(zapytanie, null);

        if (kursor != null) {
            kursor.moveToFirst();
            while (!kursor.isAfterLast()) {
                T dana_funkcji = cursorDane(kursor);
                dane_funkcji.add(dana_funkcji);
                kursor.moveToNext();
            }
        }else{
            T dana_funkcji = null;// = new <T>();
            dane_funkcji.add(dana_funkcji);
        }
        kursor.close();
        db.close();
        return dane_funkcji;
    }//public List<daneFirma> dajWszystkieFirmy(){

    protected <T> T cursorDane(Cursor kursor){

        T dana_klasy = null;// = new T();
        return dana_klasy;
    }//private daneFirma cursorDaneFirmy(Cursor kursor){

    protected <T> T dajDane1(String zapytanie){
        //Pobiera dane okreslonego wiersza
        Log.d("DebugCSQL:", "dajWszystkieAuta");
        T dane_funkcji = null;

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

        }//if (kursor.getCount() > 0) {
        else{
            dane_funkcji = null;

        }
        kursor.close();
        db.close();
        return dane_funkcji;
    }//public List<daneAuta> dajWszystkieAuta(){

    protected long dodajDane(ContentValues wartosci, String nazwa_tabeli) {

        long idRekordu = -1;
        idRekordu = dodajDaneOSQL(nazwa_tabeli, wartosci);
        return idRekordu;

    }//public void dodajDaneFirmy(daneFirma dane_funkcji) {

    protected long dodajZastapDane(ContentValues wartosci, String nazwa_tabeli){
        long idRekordu = -1;
        idRekordu = dodajZastapDaneOSQL(nazwa_tabeli, wartosci);
        return idRekordu;
        //dodajDane(DICTIONARY_TABLE_NAME_12, wartosci);
    }

    protected void updateDane(ContentValues wartosci, String nazwa_tabeli) {
        //zapisujemy stary rekord
        SQLiteDatabase db = getWritableDatabase();
        String[] selectionArgs = new String[]{String.valueOf(wartosci.get("_id"))} ;
        Cursor kursor = db.query(nazwa_tabeli, null, "_id=?", selectionArgs, null, null, null);
        kursor.moveToFirst();
        String[] nazwyKolumn = kursor.getColumnNames();
        ContentValues cv = new ContentValues();
        for (String kolumna: nazwyKolumn){
            cv.put(kolumna, kursor.getString(kursor.getColumnIndex(kolumna)));
        }
        cv.remove("_id");
        cv.put("czy_widoczny", 0);
        cv.put("data_synchronizacji", 0);
        long k = dodajDane(cv, nazwa_tabeli);
        selectionArgs  = null;
        nazwyKolumn = null;
        cv.clear();
        cv = null;
        kursor.close();
        db.close();
        //zapisujemy stary rekord -END
        wartosci.put("poprzedni_rekord_id", k);
        wartosci.put("poprzedni_rekord_powod_usuniecia", "Update");
        daneData aktualnaData = new daneData();
        wartosci.put("poprzedni_rekord_data_usuniecia", aktualnaData.getAktualnaData());
        if (wartosci.getAsInteger("data_synchronizacji") > 1) {
            wartosci.put("data_synchronizacji", 1);
        }
        updateDaneOSQL(nazwa_tabeli, wartosci, Integer.valueOf((Integer) wartosci.get("_id")));
    }

    protected ContentValues contentValuesW(daneKlasaPodstawowa dane_funkcji){
        ContentValues wartosci = new ContentValues();

        wartosci.put("_id", dane_funkcji.getId());

        wartosci.put("uwagi", dane_funkcji.getUwagi());
        wartosci.put("poprzedni_rekord_id", dane_funkcji.getPoprzedni_rekord_id());
        wartosci.put("poprzedni_rekord_data_usuniecia", dane_funkcji.getPoprzedni_rekord_data_usuniecia());
        wartosci.put("poprzedni_rekord_powod_usuniecia", dane_funkcji.getPoprzedni_rekord_powod_usuniecia());
        wartosci.put("synchron", dane_funkcji.getSynchron());
        wartosci.put("czy_widoczny", dane_funkcji.getCzy_widoczny());
        if(dane_funkcji.getData_synchronizacji() > 0){
            wartosci.put("data_synchronizacji", dane_funkcji.getData_synchronizacji());
        }else{
            wartosci.put("data_synchronizacji", 0);
        }
        if(dane_funkcji.getData_utworzenia() > 0){
            wartosci.put("data_utworzenia", dane_funkcji.getData_utworzenia());
        }else{
            wartosci.put("data_utworzenia", 0);
        }
        //Log.d("OSQLdaneFirma: Uwagi: ", dane_funkcji.getUwagi());
        return wartosci;
    }

    protected <T> ContentValues setContentValues(T dane_funkcji){
        ContentValues wartosci = new ContentValues();

        return wartosci;
    }

}
