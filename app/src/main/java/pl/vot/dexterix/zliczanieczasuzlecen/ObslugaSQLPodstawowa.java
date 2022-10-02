package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public abstract class ObslugaSQLPodstawowa extends ObslugaSQL {

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

}
