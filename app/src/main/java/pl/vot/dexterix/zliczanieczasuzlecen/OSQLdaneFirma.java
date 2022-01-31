package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OSQLdaneFirma extends ObslugaSQL{
    //DATABASE_VERSION = 8
    private static final String tag1 = "daneFirmaWhile";
    private static final String msg1 = "poDajWszystkieFirmy";
    private static final String DICTIONARY_TABLE_NAME = "BZCZBD_Firmy";
    private static final String[][] DICTIONARY_TABLE_1_ROWS = {{"nazwa", "numer", "nr_telefonu", "ulica_nr", "miasto", "typ", "kalendarz_id"},
            {"text", "text", "text", "text", "text", "text", "Text"}};
    private static final String[][] DICTIONARY_TABLE_ROWS_1_FOREIGN = {{},
            {}};
    private static final String[][] DICTIONARY_TABLE_3_ROWS = {{"calendar_id", "accountName", "calendarDisplayName", "ownerAccount"},
            {"TEXT", "TEXT", "TEXT", "TEXT"}};
    public OSQLdaneFirma(Context context) {
        super(context);
    }

    /* może kiedyś jakieś sici dojdą....
    public List<daneFirma> dajWszystkie(){
        String zapytanie = "SELECT a._id AS _id, a.nazwa AS nazwa, a.numer AS numer, a.nr_telefonu AS nr_telefonu, a.ulica_nr AS ulica_nr, " +
                "a.miasto AS miasto, a.siec_id AS siec_id, a.typ AS typ, b.nazwa AS nazwa_sieci, " +
                "a.poprzedni_rekord_id AS poprzedni_rekord_id, a.uwagi AS uwagi, a.poprzedni_rekord_data_usuniecia AS poprzedni_rekord_data_usuniecia, " +
                "a.poprzedni_rekord_powod_usuniecia AS poprzedni_rekord_powod_usuniecia, a.czy_widoczny AS czy_widoczny FROM " + DICTIONARY_TABLE_NAME +
                " AS a INNER JOIN ElektronicznaKsiazkaAutaByDexSieci AS b ON a.siec_id = b._id";
        return dajDane(zapytanie);
    }*/

    public List<daneFirma> dajWszystkie(){
        String zapytanie = "SELECT a._id AS _id, a.nazwa AS nazwa, a.numer AS numer, a.nr_telefonu AS nr_telefonu, a.ulica_nr AS ulica_nr, " +
                "a.miasto AS miasto, a.kalendarz_id AS kalendarz_id, " +
                "a.poprzedni_rekord_id AS poprzedni_rekord_id, a.uwagi AS uwagi, a.poprzedni_rekord_data_usuniecia AS poprzedni_rekord_data_usuniecia, " +
                "a.poprzedni_rekord_powod_usuniecia AS poprzedni_rekord_powod_usuniecia, a.czy_widoczny AS czy_widoczny, a.data_utworzenia AS data_utworzenia, a.data_synchronizacji AS data_synchronizacji," +
                "a.synchron AS synchron FROM " + DICTIONARY_TABLE_NAME + " AS a ";
        return dajDane(zapytanie);
    }
    public List<daneFirma> dajDoSynchronizacji(){
            String zapytanie = "SELECT a._id AS _id, a.nazwa AS nazwa, a.numer AS numer, a.nr_telefonu AS nr_telefonu, a.ulica_nr AS ulica_nr, " +
                    "a.miasto AS miasto, a.kalendarz_id AS kalendarz_id, " +
                    "a.poprzedni_rekord_id AS poprzedni_rekord_id, a.uwagi AS uwagi, a.poprzedni_rekord_data_usuniecia AS poprzedni_rekord_data_usuniecia, " +
                    "a.poprzedni_rekord_powod_usuniecia AS poprzedni_rekord_powod_usuniecia, a.czy_widoczny AS czy_widoczny, a.synchron AS synchron, " +
                    "a.data_utworzenia AS data_utworzenia, a.data_synchronizacji AS data_synchronizacji FROM " + DICTIONARY_TABLE_NAME + " AS a ";
                    //"WHERE a.synchron = 0 OR a.synchron IS NULL";
            //a.synchron = 0 OR
            return dajDane(zapytanie);
        }

    public daneFirma dajOkreslonyRekord(Integer _id){
        String zapytanie = "SELECT a._id AS _id, a.nazwa AS nazwa, a.nr_telefonu AS nr_telefonu, a.ulica_nr AS ulica_nr, " +
                "a.miasto AS miasto, a.kalendarz_id AS kalendarz_id, " +
                " a.uwagi AS uwagi FROM " + DICTIONARY_TABLE_NAME + " AS a  WHERE a._id = " + _id;

        return dajDane1(zapytanie);
    }

    public daneFirma dajOkreslonyRekordBezKalendarza(Integer _id){
        String zapytanie = "SELECT a._id AS _id, a.nazwa AS nazwa, a.nr_telefonu AS nr_telefonu, a.ulica_nr AS ulica_nr, " +
                "a.miasto AS miasto, a.kalendarz_id AS kalendarz_id, " +
                " a.uwagi AS uwagi FROM " + DICTIONARY_TABLE_NAME + " AS a  WHERE a._id = " + _id;

        return dajDane1(zapytanie);
    }

    public List<daneFirma> dajWszystkieDoRecyclerView(){
        String zapytanie = "SELECT a._id AS _id, a.nazwa AS nazwa, a.nr_telefonu AS nr_telefonu, a.ulica_nr AS ulica_nr, " +
                "a.miasto AS miasto, a.kalendarz_id AS kalendarz_id, " +
                " a.uwagi AS uwagi FROM " + DICTIONARY_TABLE_NAME + " AS a ";
        return dajDane(zapytanie);
    }

    public List<daneFirma> dajWszystkieDoRecyclerViewBezKalendarzy(){
        String zapytanie = "SELECT a._id AS _id, a.nazwa AS nazwa, a.nr_telefonu AS nr_telefonu, a.ulica_nr AS ulica_nr, " +
                "a.miasto AS miasto, a.kalendarz_id AS kalendarz_id, " +
                " a.uwagi AS uwagi FROM " + DICTIONARY_TABLE_NAME + " AS a";// INNER JOIN " +  DICTIONARY_TABLE_NAME_3 + " AS p ON a.kalendarz_id = p._id";
        return dajDane(zapytanie);
    }

    public void zerujDateSynchronizacji(){
        zerujDateSynchronizacji(DICTIONARY_TABLE_NAME);
    }

    public void updateDane(daneFirma dane_funkcji) {
        ContentValues wartosci = contentValues(dane_funkcji);

        updateDaneOSQL(DICTIONARY_TABLE_NAME, wartosci, dane_funkcji.getId());

    }

    /*public void updateDane(daneFirma dane_funkcji) {
        ContentValues wartosci = new ContentValues();
        wartosci.put("nazwa", dane_funkcji.getNazwa());
        //wartosci.put("numer", dane_funkcji.getNumer());
        wartosci.put("nr_telefonu", dane_funkcji.getNr_telefonu());
        wartosci.put("ulica_nr", dane_funkcji.getUlicaNr());
        wartosci.put("miasto", dane_funkcji.getMiasto());
        //wartosci.put("siec_id", dane_funkcji.getSiec_id());
        //wartosci.put("typ", dane_funkcji.getTyp());
        wartosci.put("uwagi", dane_funkcji.getUwagi());

        updateDaneOSQL(DICTIONARY_TABLE_NAME, wartosci, dane_funkcji.getId());

    }*/

    private ContentValues contentValues(daneFirma dane_funkcji){
        ContentValues wartosci = new ContentValues();
        wartosci.put("nazwa", dane_funkcji.getNazwa());
        //Log.d("OSQLdaneFirma: Nazwa: ", dane_funkcji.getNazwa());
        wartosci.put("numer", dane_funkcji.getNumer());
        wartosci.put("nr_telefonu", dane_funkcji.getNr_telefonu());
        //Log.d("OSQLdaneFirma: Numer Telefonu", String.valueOf(dane_funkcji.getNr_telefonu()));
        wartosci.put("ulica_nr", dane_funkcji.getUlicaNr());
        //Log.d("OSQLdaneFirma: UlicaNr: ", dane_funkcji.getUlicaNr());
        wartosci.put("miasto", dane_funkcji.getMiasto());
        wartosci.put("typ", dane_funkcji.getTyp());
        //Log.d("OSQLdaneFirma: Miasto: ", dane_funkcji.getMiasto());
        wartosci.put("kalendarz_id", dane_funkcji.getKalendarz_id_str());
        //Log.d("OSQLdaneFirma: Kalendarz_id", String.valueOf(dane_funkcji.getKalendarz_id()));
                //wartosci.put("siec_id", dane_funkcji.getSiec_id());
        //wartosci.put("typ", dane_funkcji.getTyp());
        wartosci.put("uwagi", dane_funkcji.getUwagi());
        wartosci.put("poprzedni_rekord_id", dane_funkcji.getPoprzedni_rekord_id());
        wartosci.put("poprzedni_rekord_data_usuniecia", dane_funkcji.getPoprzedni_rekord_data_usuniecia());
        wartosci.put("poprzedni_rekord_powod_usuniecia", dane_funkcji.getPoprzedni_rekord_powod_usuniecia());
        wartosci.put("synchron", dane_funkcji.getSynchron());
        if(dane_funkcji.getData_synchronizacji() > 0){
            wartosci.put("data_synchronizacji", dane_funkcji.getData_synchronizacji());
        }
        if(dane_funkcji.getData_utworzenia() > 0){
            wartosci.put("data_utworzenia", dane_funkcji.getData_utworzenia());
        }
        //Log.d("OSQLdaneFirma: Uwagi: ", dane_funkcji.getUwagi());
        return wartosci;
    }

    /*public void dodajDane(daneFirma dane_funkcji) {
        ContentValues wartosci = new ContentValues();
        wartosci.put("nazwa", dane_funkcji.getNazwa());
        //wartosci.put("numer", dane_funkcji.getNumer());
        wartosci.put("nr_telefonu", dane_funkcji.getNr_telefonu());
        wartosci.put("ulica_nr", dane_funkcji.getUlicaNr());
        wartosci.put("miasto", dane_funkcji.getMiasto());
        //wartosci.put("siec_id", dane_funkcji.getSiec_id());
        //wartosci.put("typ", dane_funkcji.getTyp());
        wartosci.put("uwagi", dane_funkcji.getUwagi());

        Log.d("SQL: dDMT", wartosci.toString());

        dodajDaneOSQL(DICTIONARY_TABLE_NAME, wartosci);
        //dodajDane(DICTIONARY_TABLE_NAME_12, wartosci);
    }//public void dodajDaneFirmy(daneFirma dane_funkcji) {*/

    public long dodajDane(daneFirma dane_funkcji) {
        ContentValues wartosci = contentValues(dane_funkcji);

        //Log.d("SQL: dDMT", wartosci.toString());

        //dodajDaneOSQL(DICTIONARY_TABLE_NAME, wartosci);
        long idRekordu = -1;
        idRekordu = dodajDaneOSQL(DICTIONARY_TABLE_NAME, wartosci);
        return idRekordu;
        //dodajDane(DICTIONARY_TABLE_NAME_12, wartosci);
    }//public void dodajDaneFirmy(daneFirma dane_funkcji) {

    //odczyt danych z bazy SQL do kursora

    public daneFirma dajDane1(String zapytanie){
        //Pobiera dane okreslonego wiersza
        Log.d("DebugCSQL:", "dajWszystkieAuta");
        daneFirma dane_funkcji = new daneFirma();

        SQLiteDatabase db = getReadableDatabase();

        //Cursor kursor = db.query(DICTIONARY_TABLE_NAME_2, kolumny, null, null, null, null, null);
        Cursor kursor = db.rawQuery(zapytanie, null);
        //Log.d("Dane Frimra dlugosc kursora: ", String.valueOf(kursor.getCount()));
        //daneAuta daneAuta = new daneAuta();
        //Log.d("zawartoscKursora",String.valueOf(kursor));
        //int ii =0;
        if (kursor != null) {
            //Log.d("kursor: ", String.valueOf(kursor));
            //kursor.getColumnName(1)
            kursor.moveToFirst();
            //Log.d("kursor: ", String.valueOf(kursor));
            while (!kursor.isAfterLast()) {
                //Log.d("kursor: ", String.valueOf(kursor));
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
            dane_funkcji = new daneFirma();
            //dana_funkcji.onCreate();

            //dane_funkcji.add(dana_funkcji);
        }
        db.close();
        return dane_funkcji;
    }//public List<daneAuta> dajWszystkieAuta(){

    private List<daneFirma> dajDane(String zapytanie){
        List<daneFirma> dane_funkcji = new LinkedList<>();

        //ObslugaSQL osql = new ObslugaSQL(context);

        SQLiteDatabase db = getReadableDatabase();
        Cursor kursor = db.rawQuery(zapytanie, null);
        kursor.moveToFirst();
        while (!kursor.isAfterLast()){
            daneFirma dana_funkcji = cursorDane(kursor);
            dane_funkcji.add(dana_funkcji);
            kursor.moveToNext();
        }
        db.close();
        return dane_funkcji;
    }//public List<daneFirma> dajWszystkieFirmy(){

    //kursor dla odczytu danych z bazy
    private daneFirma cursorDane(Cursor kursor){
        daneFirma dane_funkcji = new daneFirma();

        if (kursor.getColumnIndex("_id") > -1) {
            dane_funkcji.setId(kursor.getInt(kursor.getColumnIndex("_id")));
        }
        /*if (kursor.getColumnIndex("siec_id") > -1) {
            dane_funkcji.setSiec_id(kursor.getInt(kursor.getColumnIndex("siec_id")));
        }*/
        if (kursor.getColumnIndex("nazwa") > -1) {
            dane_funkcji.setNazwa(kursor.getString(kursor.getColumnIndex("nazwa")));
        }
        if (kursor.getColumnIndex("numer") > -1) {
            dane_funkcji.setNumer(kursor.getString(kursor.getColumnIndex("numer")));
        }
        if (kursor.getColumnIndex("nr_telefonu") > -1) {
            dane_funkcji.setNr_telefonu(kursor.getInt(kursor.getColumnIndex("nr_telefonu")));
        }
        if (kursor.getColumnIndex("ulica_nr") > -1) {
            dane_funkcji.setUlicaNr(kursor.getString(kursor.getColumnIndex("ulica_nr")));
        }
        if (kursor.getColumnIndex("miasto") > -1) {
            dane_funkcji.setMiasto(kursor.getString(kursor.getColumnIndex("miasto")));
        }
        if (kursor.getColumnIndex("kalendarz_id") > -1) {
            dane_funkcji.setKalendarz_id(kursor.getLong(kursor.getColumnIndex("kalendarz_id")));
            //Log.d("OSQLdnaeFirma kalendarz_id :", kursor.getString(kursor.getColumnIndex("kalendarz_id")));
        }
        if (kursor.getColumnIndex("calendarDisplayName") > -1) {
            dane_funkcji.setKalendarz_nazwa(kursor.getString(kursor.getColumnIndex("calendarDisplayName")));
        }

        /*if (kursor.getColumnIndex("typ") > -1) {
            dane_funkcji.setMiasto(kursor.getString(kursor.getColumnIndex("typ")));
        }*/

        if (kursor.getColumnIndex("uwagi") > -1) {
            dane_funkcji.setUwagi(kursor.getString(kursor.getColumnIndex("uwagi")));
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
        //Log.d("dane firm", dane_funkcji.toString());
        return dane_funkcji;
    }//private daneFirma cursorDaneFirmy(Cursor kursor){

    public ArrayList<String[]> podajNazwa(){
        //ObslugaSQL chsql = new ObslugaSQL(kontekst);
        String zapytanie = "SELECT _id, nazwa, kalendarz_id FROM " + DICTIONARY_TABLE_NAME;
        List<daneFirma> daneWszystkich = dajDane(zapytanie);
        //String[] wiersz = ("","");
        ArrayList<String[]> lista = new ArrayList<>();
        for (daneFirma dana : daneWszystkich) {
            String[] wiersz = {String.valueOf(dana.getId()), dana.getNazwa(), String.valueOf(dana.getKalendarz_id())};
            //Log.d("wiersz: ", String.valueOf(wiersz));
            lista.add(wiersz);
        }//for (daneAuta auta : daneWszystkichAut) {
        return lista;
    }//public List<String> podajNazwa(){

}
