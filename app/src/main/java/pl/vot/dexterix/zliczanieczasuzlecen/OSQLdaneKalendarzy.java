package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OSQLdaneKalendarzy {

    private static final String tag1 = "OSQLdaneKalendarzy";
    private static final String msg1 = "poDajWszystkieUstawienia";

    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    final int PROJECTION_ID_INDEX = 0;
    final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    List<daneKalendarza> dane_klasy = new LinkedList<>();

    public OSQLdaneKalendarzy(Context context) {

        //Poszukiwanie kalendarza
        List<daneKalendarza> dana_wybierz_kalendarz_do_zapisu = new ArrayList<>();

        // Run query
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        Log.d(tag1, uri.toString());
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";

// Submit the query and get a Cursor object back.
        //cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        cur = cr.query(uri, EVENT_PROJECTION, null, null, null);
        // Use the cursor to step through the returned records
        //Log.d(tag1, String.valueOf(cur.getCount()));

        while (cur.moveToNext()) {
            //obiekt do zapisania danych
            daneKalendarza danaFunkcji = new daneKalendarza();
            //Log.d(tag1, String.valueOf(cur.getColumnCount()));
            /*for (int i =0; i < 35; i++) {
                Log.d(tag1 + i + " : ", cur.getColumnName(i));
            }*/
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            //Log.d(tag1, "id " + calID);
            danaFunkcji.setCalendar_id(calID);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            danaFunkcji.setCalendarDisplayName(displayName);
            //Log.d(tag1, displayName.toString());
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            //danaFunkcji.setAccountName(accountName);
            if (!(accountName == null)) {
                danaFunkcji.setAccountName(accountName);
                //Log.d(tag1, accountName.toString());
            }

            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
            if (!(ownerName == null)) {
                danaFunkcji.setOwnerAccount(ownerName);
                //Log.d(tag1, ownerName.toString());
            }
            // Do something with the values...
            dane_klasy.add(danaFunkcji);
            //dana_wybierz_kalendarz_do_zapisu.add(danaFunkcji);
        }

        //return dana_wybierz_kalendarz_do_zapisu;
    }

    public List<daneKalendarza> dajDane(){
        //Log.d("DebugCSQL:", "dajWszystkieAuta");

        return dane_klasy;
    }//public List<daneAuta> dajWszystkieAuta(){

    public ArrayList<String[]> podajNazwa(){
        //ObslugaSQL chsql = new ObslugaSQL(kontekst);
        //String zapytanie = "SELECT _id, calendarDisplayName FROM " + DICTIONARY_TABLE_NAME;

        List<daneKalendarza> daneWszystkich = dajDane();
        //String[] wiersz = new String[];// = "";
        ArrayList<String[]> lista = new ArrayList<>();
        for (daneKalendarza dana : daneWszystkich) {
            String[] wiersz = {String.valueOf(dana.getCalendar_id()), dana.getCalendarDisplayName()};// + " 3: " + dana.getAccountName() + " \n4: " + dana.getOwnerAccount();
            //Log.d("wiersz: ", wiersz);
            lista.add(wiersz);
        }//for (daneAuta auta : daneWszystkichAut) {
        return lista;
    }//public List<String> podajNazwa(){

    public List<daneKalendarza> dajWszystkie() {
        return dane_klasy;
    }
}
