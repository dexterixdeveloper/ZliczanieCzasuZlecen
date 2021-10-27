package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class OSQLdaneZlecenia extends ObslugaSQL {
    private static final String DICTIONARY_TABLE_NAME = "BZCZBD_Zlecenia";
    private static final String[][] DICTIONARY_TABLE_2_ROWS = {{"firma_id", "opis", "czas_rozpoczecia", "czas_zawieszenia", "czas_zakonczenia", "status", "rozliczona", "kalendarz_id", "kalendarz_zadanie_id"},
            {"integer", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "Integer", "TEXT"}};

    private static final String[][] DICTIONARY_TABLE_ROWS_2_FOREIGN = {{"firma_id"},
            {"BZCZBD_Firmy(_id)"}};
    private static final String tag1 = "daneZleceniaWhile";
    private static final String msg1 = "poDajWszystkieZlecenia";
    public OSQLdaneZlecenia(Context context) {
        super(context);
    }

    public void updateDane(daneZlecenia dane){

        ContentValues wartosci = new ContentValues();
        wartosci.put("firma_id", dane.getFirma_id());
        wartosci.put("opis", dane.getOpis());
        wartosci.put("czas_rozpoczecia", dane.getCzas_rozpoczecia());
        wartosci.put("czas_zawieszenia", dane.getCzas_zawieszenia());
        wartosci.put("czas_zakonczenia", dane.getCzas_zakonczenia());
        wartosci.put("status", dane.getStatus());
        wartosci.put("rozliczona", dane.getRozliczona());
        wartosci.put("uwagi", dane.getUwagi());
        wartosci.put("czy_widoczny", dane.getCzy_widoczny());
        wartosci.put("kalendarz_id", dane.getKalendarz_id());
        wartosci.put("kalendarz_zadanie_id", dane.getKalendarz_zadanie_id());

        updateDaneOSQL(DICTIONARY_TABLE_NAME, wartosci, dane.getId());

    }

    public long dodajDane(daneZlecenia dane){

        ContentValues wartosci = new ContentValues();
        wartosci.put("firma_id", dane.getFirma_id());
        wartosci.put("opis", dane.getOpis());
        wartosci.put("czas_rozpoczecia", dane.getCzas_rozpoczecia());
        wartosci.put("czas_zawieszenia", dane.getCzas_zawieszenia());
        wartosci.put("czas_zakonczenia", dane.getCzas_zakonczenia());
        wartosci.put("status", dane.getStatus());
        wartosci.put("rozliczona", dane.getRozliczona());
        wartosci.put("uwagi", dane.getUwagi());
        wartosci.put("czy_widoczny", dane.getCzy_widoczny());
        wartosci.put("kalendarz_id", dane.getKalendarz_id());
        wartosci.put("kalendarz_zadanie_id", dane.getKalendarz_zadanie_id());

        long idRekordu = -1;
        idRekordu = dodajDaneOSQL(DICTIONARY_TABLE_NAME, wartosci);
        return idRekordu;
    }

    public List<daneZlecenia> dajWszystkieDoRecyclerView(String status){
        String zapytanie = "SELECT a._id AS _id, a.opis AS opis, a.czas_rozpoczecia AS czas_rozpoczecia, a.czas_zakonczenia AS czas_zakonczenia," +
                "a.status AS status, p.nazwa AS firma_nazwa, a.uwagi AS uwagi " +
                "FROM " + DICTIONARY_TABLE_NAME + " AS a INNER JOIN " + DICTIONARY_TABLE_NAME_1 + " AS p ON a.firma_id = p._id WHERE a.status = '" + status + "' AND a.czy_widoczny = 1";
        return dajDane(zapytanie);
    }

    public List<daneZlecenia> dajWszystkieDoRaportu(String status, Long czasRozpoczecia, Long czasZakonczenia, int firma_id){
        String zapytanie = "SELECT a._id AS _id, a.opis AS opis, a.czas_rozpoczecia AS czas_rozpoczecia, a.czas_zakonczenia AS czas_zakonczenia," +
                "a.status AS status, p.nazwa AS firma_nazwa, a.uwagi AS uwagi, a.firma_id AS firma_id " +
                "FROM " + DICTIONARY_TABLE_NAME + " AS a INNER JOIN " + DICTIONARY_TABLE_NAME_1 + " AS p ON a.firma_id = p._id WHERE a.status = '" + status + "' AND a.czy_widoczny = 1 " +
                "AND czas_rozpoczecia >= " + czasRozpoczecia + " AND czas_rozpoczecia <= " + czasZakonczenia;
        if (firma_id > -1){
            zapytanie = zapytanie.concat(" AND p._id = " + firma_id);
        }
        return dajDane(zapytanie);
    }

    public List<daneZlecenia> dajWszystkieDoRecyclerViewNZ(String status){
        String zapytanie = "SELECT a._id AS _id, a.opis AS opis, a.czas_rozpoczecia AS czas_rozpoczecia, a.czas_zakonczenia AS czas_zakonczenia," +
                "a.status AS status, p.nazwa AS firma_nazwa, a.uwagi AS uwagi " +
                "FROM " + DICTIONARY_TABLE_NAME + " AS a INNER JOIN " + DICTIONARY_TABLE_NAME_1 + " AS p ON a.firma_id = p._id WHERE a.status != 'zak' AND a.status != 'zakwtle' AND a.czy_widoczny = 1";
        return dajDane(zapytanie);
    }

    public daneZlecenia dajOkreslonyRekord(Integer _id){
        String zapytanie = "SELECT a._id AS _id, a.opis AS opis, a.czas_rozpoczecia AS czas_rozpoczecia, a.czas_zakonczenia AS czas_zakonczenia, " +
                "a.status AS status, p.nazwa AS firma_nazwa, a.firma_id AS firma_id, a.uwagi AS uwagi, a.kalendarz_id AS kalendarz_id, a.kalendarz_zadanie_id AS kalendarz_zadanie_id, z.calendar_id AS calendar_id "  +
                "FROM " + DICTIONARY_TABLE_NAME + " AS a LEFT JOIN " + DICTIONARY_TABLE_NAME_1 + " AS p ON a.firma_id = p._id LEFT JOIN " + DICTIONARY_TABLE_NAME_3 + " AS z ON a.kalendarz_id = z._id WHERE a._id = " + _id;
        return dajDane1(zapytanie);
    }

    public List<daneZlecenia> dajWszystkie(){
        String zapytanie = "SELECT a._id AS _id, a.opis AS opis, a.czas_rozpoczecia AS czas_rozpoczecia,  a.czas_zakonczenia AS czas_zakonczenia," +
                "a.firma_id AS firma_id, a.status AS status, a.rozliczona AS rozliczona, p.nazwa AS firma_nazwa, a.uwagi AS uwagi, a.kalendarz_id AS kalendarz_id, a.kalendarz_zadanie_id AS kalendarz_zadanie_id, " +
                "z.calendar_id AS calendar_id, " +
                "a.poprzedni_rekord_data_usuniecia AS poprzedni_rekord_data_usuniecia, a.poprzedni_rekord_powod_usuniecia AS poprzedni_rekord_powod_usuniecia, " +
                "a.czy_widoczny AS czy_widoczny FROM " + DICTIONARY_TABLE_NAME + " AS a LEFT JOIN " + DICTIONARY_TABLE_NAME_1 + " AS p ON a.firma_id = p._id LEFT JOIN " + DICTIONARY_TABLE_NAME_3 + " AS z ON a.kalendarz_id = z._id";
        return dajDane(zapytanie);
    }

    public daneZlecenia dajDane1(String zapytanie){
        //Pobiera dane okreslonego wiersza
        Log.d("DebugCSQL:", "dajWszystkieZlecenia");
        daneZlecenia dane_funkcji = new daneZlecenia();

        SQLiteDatabase db = getReadableDatabase();

        //Cursor kursor = db.query(DICTIONARY_TABLE_NAME_2, kolumny, null, null, null, null, null);
        Cursor kursor = db.rawQuery(zapytanie, null);
        //Log.d("dlugosc kursora: ", String.valueOf(kursor.getCount()));
        //daneAuta daneAuta = new daneAuta();
        //Log.d("zawartoscKursora",String.valueOf(kursor));
        //int ii =0;
        if (kursor != null) {
            kursor.moveToFirst();
            while (!kursor.isAfterLast()) {
                //Log.d(tag1,String.valueOf(kursor.getCount()));
                dane_funkcji = cursorDane(kursor);

                //Log.d("getColumnIndexOrThrow",String.valueOf(kursor.getColumnIndexOrThrow("_id")));

                //dane_funkcji.add(dana_funkcji);
                kursor.moveToNext();
            }//while (!kursor.isAfterLast()) {
            Log.d("DebugCSQL:", msg1);
            //kursor.close();
            //return auta;
        }//if (kursor.getCount() > 0) {
        else{
            daneZlecenia dana_funkcji = new daneZlecenia();
            dana_funkcji.onCreate();

            //dane_funkcji.add(dana_funkcji);
        }
        db.close();
        return dane_funkcji;
    }//public List<daneAuta> dajWszystkieAuta(){

    public List<daneZlecenia> dajDane(String zapytanie){
        Log.d("DebugCSQL:", "dajWszystkieZlecenia");
        List<daneZlecenia> dane_funkcji = new LinkedList<>();

        SQLiteDatabase db = getReadableDatabase();

        //Cursor kursor = db.query(DICTIONARY_TABLE_NAME_2, kolumny, null, null, null, null, null);
        Cursor kursor = db.rawQuery(zapytanie, null);
        //Log.d("dlugosc kursora: ", String.valueOf(kursor.getCount()));
        //daneAuta daneAuta = new daneAuta();
        //Log.d("zawartoscKursora",String.valueOf(kursor));
        //int ii =0;
        if (kursor != null) {
            kursor.moveToFirst();
            while (!kursor.isAfterLast()) {
                //Log.d(tag1,String.valueOf(kursor.getCount()));
                daneZlecenia dana_funkcji = cursorDane(kursor);
                //daneAuta daneAuta = new daneAuta();
                //Log.d("ileAut: ", String.valueOf(ii));
                //ii++;
                //daneAuta.setId(kursor.getInt(kursor.getColumnIndex("a._id")));

                //Log.d("getColumnIndexOrThrow",String.valueOf(kursor.getColumnIndexOrThrow("_id")));

                dane_funkcji.add(dana_funkcji);
                kursor.moveToNext();
            }//while (!kursor.isAfterLast()) {
            Log.d("DebugCSQL:", msg1);
            //kursor.close();
            //return auta;
        }//if (kursor.getCount() > 0) {
        else{
            daneZlecenia dana_funkcji = new daneZlecenia();
            dana_funkcji.onCreate();
            /*dana_funkcji.setId(0);
            dana_funkcji.setCzas_rozpoczecia(0L);
            dana_funkcji.setCzas_zawieszenia(0L);
            dana_funkcji.setOpis("Brak danych, wprowadź.");
            dana_funkcji.setFirma_id(0);
            dana_funkcji.setFirma_nazwa("Brak danych, wprowadź.");
            dana_funkcji.setCzas_zakonczenia(0L);
            dana_funkcji.setStatus("Brak danych, wprowadź.");
            dana_funkcji.setRozliczona("Brak danych, wprowadź.");*/

            dane_funkcji.add(dana_funkcji);
        }
        db.close();
        return dane_funkcji;
    }//public List<daneAuta> dajWszystkieAuta(){

    //kursor dla odczytu danych z bazy
    private daneZlecenia cursorDane(Cursor kursor) {
        daneZlecenia dane_funkcji = new daneZlecenia();
        daneData potrzebnaData = new daneData();
        if (kursor.getColumnIndex("_id") > -1) {
            dane_funkcji.setId(kursor.getInt(kursor.getColumnIndex("_id")));
        }

        if (kursor.getColumnIndex("czas_rozpoczecia") > -1) {
            dane_funkcji.setCzas_rozpoczecia(kursor.getLong(kursor.getColumnIndexOrThrow("czas_rozpoczecia")));
            dane_funkcji.setCzas_rozpoczecia_string(potrzebnaData.getDataMilisecondsToString(dane_funkcji.getCzas_rozpoczecia()));
        }
        if (kursor.getColumnIndex("czas_zawieszenia") > -1) {
            dane_funkcji.setCzas_zawieszenia(kursor.getLong(kursor.getColumnIndexOrThrow("czas_zawieszenia")));
            dane_funkcji.setCzas_zawieszenia_string(potrzebnaData.getDataMilisecondsToString(dane_funkcji.getCzas_zawieszenia()));
        }
        if (kursor.getColumnIndex("opis") > -1) {
            dane_funkcji.setOpis(kursor.getString(kursor.getColumnIndexOrThrow("opis")));
        }
        if (kursor.getColumnIndex("firma_id") > -1) {
            dane_funkcji.setFirma_id(kursor.getInt(kursor.getColumnIndexOrThrow("firma_id")));
        }

        if (kursor.getColumnIndex("firma_nazwa") > -1) {
            dane_funkcji.setFirma_nazwa(kursor.getString(kursor.getColumnIndexOrThrow("firma_nazwa")));
        }
        if (kursor.getColumnIndex("czas_zakonczenia") > -1) {
            dane_funkcji.setCzas_zakonczenia(kursor.getLong(kursor.getColumnIndexOrThrow("czas_zakonczenia")));
            dane_funkcji.setCzas_zakonczenia_string(potrzebnaData.getDataMilisecondsToString(dane_funkcji.getCzas_zakonczenia()));
        }
        if (kursor.getColumnIndex("status") > -1) {
            dane_funkcji.setStatus(kursor.getString(kursor.getColumnIndexOrThrow("status")));
        }
        if (kursor.getColumnIndex("rozliczona") > -1) {
            dane_funkcji.setRozliczona(kursor.getString(kursor.getColumnIndexOrThrow("rozliczona")));
        }

        if (kursor.getColumnIndex("kalendarz_id") > -1) {
            dane_funkcji.setKalendarz_id(kursor.getInt(kursor.getColumnIndex("kalendarz_id")));
        }

        if (kursor.getColumnIndex("kalendarz_zadanie_id") > -1) {
            dane_funkcji.setKalendarz_zadanie_id(kursor.getLong(kursor.getColumnIndexOrThrow("kalendarz_zadanie_id")));
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

    /*public List<String> podajNazwa(){
        //ObslugaSQL chsql = new ObslugaSQL(kontekst);
        String zapytanie = "SELECT _id, nazwa FROM " + DICTIONARY_TABLE_NAME;
        List<daneZlecenia> daneWszystkich = dajDane(zapytanie);
        String wiersz = "";
        List<String> lista = new ArrayList<>();
        for (daneZlecenia dana : daneWszystkich) {
            wiersz = String.valueOf(dana.getId()) + " " + dana.getNazwa();
            Log.d("wiersz: ", wiersz);
            lista.add(wiersz);
        }//for (daneAuta auta : daneWszystkichAut) {
        return lista;
    }//public List<String> podajNazwa(){*/

}
