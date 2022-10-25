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
        //ContentValues wartosci = contentValues((pl.vot.dexterix.zliczanieczasuzlecen.daneFirma) t);

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
