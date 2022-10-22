package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.opencsv.CSVWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentSynchronizacja extends FragmentPodstawowy {

    protected String TOKEN_ID_URL = "https://dexterix.vot.pl/zliczazle/sendtokenid.php";
    protected BroadcastReceiver broadcastReceiver;
    protected String SYNCHRONIZE_URL = "https://dexterix.vot.pl/zliczazle/synchronizemessages.php";

    public static final String UI_SYNCHRONIZE_MESSAGE = "dexterix.vot.pl.synchronizemysqltosqlitedatabase.UI_SYNCHRONIZE_SQLITE";

    private boolean czyKoniec = false;

    private void setCzyKoniec(boolean x){
        this.czyKoniec = x;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        changeTitle("Synchronizacja");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_synchronizacja, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Fragment z1 = getVisibleFragment();
        Log.d("Token", "przed daj");
        Log.d("Token", getActualTokenId());
        Log.d("Token", "po daj");
        ukryjFloatingButton();
        addListenerOnButtonSynchronizujWszystko();

    }

    private void addListenerOnButtonSynchronizujWszystko() {
        Button button = getActivity().findViewById(R.id.buttonSynchronizujWszystko);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                synchronizujDane(R.id.textViewSynchronizacja);
                //synchronizujStawki(R.id.textViewSynchronizacja);
                //synchronizujZlecenia(R.id.textViewSynchronizacja);
            }
        });
    }

    public void getActualToken(Context context){
        final String[] token = new String[1];
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Pobieramy token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token[0] = task.getResult();

                        // Log and toast
                        //String msg = getString(token);

                        //Log.d("Pobieramy token", token[0]);
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.FCM_Pref), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getResources().getString(R.string.FCM_TOKEN), token[0]);
                        editor.apply();
                        Toast.makeText(context, token[0], Toast.LENGTH_SHORT).show();
                        //return token;
                    }
                });

    }

    protected String getActiveDataBase(){
        //pobieramy bazę z zapisania
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.ActiveDatabase), Context.MODE_PRIVATE);
        String pref = sharedPreferences.getString(getResources().getString(R.string.ActiveDatabase),"1");
        //Log.d("Token get acktu", token_id);

        return pref;
    }

    private void wyslijToken() {
        getActualToken(getActivity());
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(FragmentSynchronizacja.UI_SYNCHRONIZE_MESSAGE));
        //ReadMessages();

        final String tokenek = getActualTokenId();
        //Log.d("token", tokenek);
        Log.d("###############","############");
        StringRequest SendTokenID = new StringRequest(Request.Method.POST, TOKEN_ID_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                //synchronizujFirmy(R.id.textViewSynchroFirmy);
                Log.d("Odpowiedź", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                Log.d("Bład", error.toString());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<String, String>();

                param.put("tokenid",tokenek );
                return param;
            }
        };
        SQLSynchMySingleton.getmInstance(getActivity()).addToRequestQueue(SendTokenID);

        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(FragmentSynchronizacja.UI_SYNCHRONIZE_MESSAGE));
    }

    protected String getActualTokenId(){
        //pobieramy token z zapisania
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.FCM_Pref), Context.MODE_PRIVATE);
        String token_id= sharedPreferences.getString(getResources().getString(R.string.FCM_TOKEN),"");
        //Log.d("Token get acktu", token_id);
        if (token_id.equals("")){
            getActualToken(getActivity());
            sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.FCM_Pref), Context.MODE_PRIVATE);
            token_id= sharedPreferences.getString(getResources().getString(R.string.FCM_TOKEN),"");
        }
        return token_id;
    }

    private daneFirma getFirmaFromJson(JSONObject Jasonobject){
        daneFirma danaUpdate = new daneFirma();

        danaUpdate.setFromJSONw(Jasonobject);

        danaUpdate.setKalendarz_id(Jasonobject.optInt("kalendarz_id", 0));
        danaUpdate.setTyp(Jasonobject.optString("typ", ""));
        danaUpdate.setMiasto(Jasonobject.optString("miasto", ""));
        danaUpdate.setUlica_nr(Jasonobject.optString("ulica_nr", ""));
        danaUpdate.setNr_telefonu(Jasonobject.optInt("nr_telefonu", 0));
        danaUpdate.setNumer(Jasonobject.optString("numer", ""));
        danaUpdate.setNazwa(Jasonobject.optString("nazwa", ""));

        return danaUpdate;
    }

    protected daneStawka getStawkaFronJson(JSONObject Jasonobject){
        daneStawka danaUpdate = new daneStawka();

        danaUpdate.setFromJSONw(Jasonobject);

        danaUpdate.setKoniec(Jasonobject.optString("koniec",""));
        danaUpdate.setStawka(Jasonobject.optLong("stawka",0));
        danaUpdate.setPoczatek(Jasonobject.optString("poczatek", ""));
        danaUpdate.setFirma_id(Jasonobject.optInt("firma_id", 0));

        return danaUpdate;
    }

    protected daneZlecenia getZlecenieFronJson(JSONObject Jasonobject){
        daneZlecenia danaUpdate = new daneZlecenia();

        danaUpdate.setFromJSONw(Jasonobject);

        danaUpdate.setFirma_id(Jasonobject.optInt("firma_id", 0));
        danaUpdate.setOpis(Jasonobject.optString("opis", ""));
        danaUpdate.setCzas_rozpoczecia(Jasonobject.optLong("czas_rozpoczecia", 0));
        danaUpdate.setCzas_zawieszenia(Jasonobject.optLong("czas_zawieszenia", 0));
        danaUpdate.setCzas_zakonczenia(Jasonobject.optLong("czas_zakonczenia", 0));
        danaUpdate.setStatus(Jasonobject.optString("status", ""));
        danaUpdate.setRozliczona(Jasonobject.optString("rozliczona", ""));
        danaUpdate.setKalendarz_id(Jasonobject.optInt("kalendarz_id", 0));
        danaUpdate.setKalendarz_zadanie_id(Jasonobject.optLong("kalendarz_zadanie_id", 0));

        return danaUpdate;
    }

    private void takaTamSynchronizacja(int RidtextView) throws ClassNotFoundException {
        List<String> tablice = Arrays.asList("Firmy", "Stawki", "Zlecenia");
        //List<String> tablice = Arrays.asList("BZCZBD_Firmy", "BZCZBD_Stawki", "BZCZBD_Zlecenia");
        //List<String[]> tablice = Arrays.asList(("daneFirma", "OSQLdaneFirma"), ("daneStawka", "OSQLdaneStawka"), ("daneZlecenia", "OSQLdaneZlecenia"));
        for (String tablica: tablice){
            synchronizujDane(RidtextView);
        }
    }

    private static void writeCsv(File backupFile, SQLiteDatabase db, List<String> tables){
        CSVWriter csvWrite = null;
        Cursor curCSV = null;
        try {
            csvWrite = new CSVWriter(new FileWriter(backupFile));
            for(String table: tables){
                curCSV = db.rawQuery("SELECT * FROM " + table,null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext()) {
                    int columns = curCSV.getColumnCount();
                    String[] columnArr = new String[columns];
                    for( int i = 0; i < columns; i++){
                        columnArr[i] = curCSV.getString(i);
                    }
                    csvWrite.writeNext(columnArr);
                }
            }
        }
        catch(Exception sqlEx) {
            Log.e("wyjebalo: ", sqlEx.getMessage(), sqlEx);
        }finally {
            if(csvWrite != null){
                try {
                    csvWrite.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if( curCSV != null ){
                curCSV.close();
            }
        }
    }

    protected void synchronizujDane(int RidtextView){
        /*getActualToken(getActivity());*/
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(FragmentSynchronizacja.UI_SYNCHRONIZE_MESSAGE));

        ObslugaSQL daneOSQL = new ObslugaSQL(getActivity());
        TextView textView = (TextView) getActivity().findViewById(RidtextView);

        //Tabele do synchronizacji
        List<String> tablice = daneOSQL.getTabliceDoSynchronizacji();
        Cursor cursorSynchronizacja = null;

        try {
            //tutututu for(String table: tablice){
            String table = "BZCZBD_Firmy";
                Log.d("tabela: ", table);
                String zapytanie = "SELECT * FROM " + table + " WHERE data_synchronizacji IN ('0', '1')";
                cursorSynchronizacja = daneOSQL.rawQuery(zapytanie,null);
                String[] nazwyKolumn = cursorSynchronizacja.getColumnNames();
                int columns = cursorSynchronizacja.getColumnCount();
                String[] columnArr = new String[columns];
                String tabela_nazwa = table;
                textView.append("Synchronizujemy tabelę " + tabela_nazwa + "\n");
                textView.append("Rekordów do synchronizacji " + cursorSynchronizacja.getCount() + "\n");
                 //tutututu1 while(cursorSynchronizacja.moveToNext()) {
                cursorSynchronizacja.moveToFirst();
                int i = 0;
                while (!cursorSynchronizacja.isAfterLast()) {
                    //do {
                    Log.d("ile i: ", String.valueOf(i));
                    Log.d("ile do synchronizacji1: ", String.valueOf(cursorSynchronizacja.getCount()));
                    //a może tak zadziała
                    Map<String, String> dane = new HashMap<String, String>();
                    for( int j = 0; j < columns; j++){
                        //columnArr[i] = cursorSynchronizacja.getString(i);
                        if (!cursorSynchronizacja.isNull(j)) {
                            cursorSynchronizacja.getColumnCount();
                            Log.d("ja i mój jj: ", String.valueOf(j));
                            Log.d("kolumna: ", cursorSynchronizacja.getColumnName(j));
                            //Log.d("kolumna + 1: ", cursorSynchronizacja.getColumnName(j+1));
                            Log.d("ile kolumn: ", String.valueOf(cursorSynchronizacja.getColumnCount()));
                            Log.d(nazwyKolumn[j], cursorSynchronizacja.getString(j));
                            dane.put(nazwyKolumn[j], cursorSynchronizacja.getString(j));
                            //Log.d("nazwy kolumn FS: ", String.valueOf(i));
                            Log.d(nazwyKolumn[j], cursorSynchronizacja.getString(j));
                        }

                    }
                    Log.d("dane: ", dane.toString());
                    //a może tak zadziała -END
                    synchronizujDana(dane, tabela_nazwa, RidtextView, true, false, i);
                    //synchronizujDana(cursorSynchronizacja, tabela_nazwa, RidtextView, true, false, i);
                    Log.d("a dla jaj: ", String.valueOf(cursorSynchronizacja.getString(cursorSynchronizacja.getColumnIndex("data_utworzenia"))));
                    cursorSynchronizacja.moveToNext();
                    dane = null;
                    i++;
                }
                    //cursorSynchronizacja = daneOSQL.rawQuery(zapytanie,null);
                //} while (cursorSynchronizacja.moveToNext());
                //tutututu1 }

                textView.append("Brak danych do wysłania, sprawdzam czy coś do pobrania "  + "\n");
                cursorSynchronizacja = null;
                //Log.d("ile do synchronizacji3: ", String.valueOf(cursorSynchronizacja.getString(cursorSynchronizacja.getColumnIndex("data_utworzenia"))));
                //teraz sobie pobierzemy dane
                /*while(!czyKoniec){
                    synchronizujDana(cursorSynchronizacja, tabela_nazwa, RidtextView, false, false);
                }*/

            //tutututu}
        }catch(Exception sqlEx) {
            Log.e("wyjebalo: ", sqlEx.getMessage(), sqlEx);
        }


    }

    private ContentValues getContentValuesFromJson(JSONObject json, String[] nazwyKolumnWBazie){
        ContentValues cv = new ContentValues();
        //TODO: ale dlaczego tabela nie przylatuje z serwera?

        //Pobieramy nazwykolumn
        //ObslugaSQL daneOSQL = new ObslugaSQL(getActivity());
        //String[][] nazwyKolumnWBazie = daneOSQL.getTablePola(tabela);

        for (int i = 0 ; i < nazwyKolumnWBazie.length; i++){
            if (!nazwyKolumnWBazie[i].equals("_id")) {
                cv.put(nazwyKolumnWBazie[i], json.optString(nazwyKolumnWBazie[i], null));
            }else{//tu niestety kolumna _id z aplikacji inaczej się nazywa na serwerze: id
                cv.put(nazwyKolumnWBazie[i], json.optString("id", null));
            }
        }
        Log.d("cv from json :", cv.toString());
        return cv;
    }

    private void synchronizujDana(Map danaS, String tabela_nazwa, int RidtextView, boolean czyWysylamy, boolean czyOstatni, int d) throws ClassNotFoundException {
        TextView textView = (TextView) getActivity().findViewById(RidtextView);

        Log.d("danas.getcount nap", String.valueOf(danaS.size()));
        Log.d("danas.getcount nap1 ", String.valueOf(d));
        ObslugaSQL daneOSQL = new ObslugaSQL(getActivity());
        //String[] nazwyKolumn = danaS.getColumnNames();
        int columns = danaS.size();
        //String[] columnArr = new String[columns];
        String[][] nazwyKolumnWBazie = daneOSQL.getTablePola(tabela_nazwa);
        //final Cursor danaS = danaS1;
        //boolean czyKoniec = false;
        StringRequest SendTokenID = new StringRequest(Request.Method.POST, SYNCHRONIZE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                Log.d("Odpowiedź", response);
                Log.d("Odpowiedź długość: ", String.valueOf(response.length()));
                JSONObject Jasonobject = null;
                String statusSynchronizacji = null;
                try {
                    Jasonobject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ContentValues daneZserwera = null;
                statusSynchronizacji = Jasonobject.optString("statusSynchronizacji", null);
                Log.d("status", statusSynchronizacji);
                String tabelaNazwa = Jasonobject.optString("tabela_nazwa");
                Log.d("Tabela z JSON: ", tabelaNazwa);

                if (statusSynchronizacji != null){
                    //daneFirma danaUpdate = new daneFirma();

                    String[] nazwyKolumn = new String[0];// = null;
                    switch (statusSynchronizacji){
                        case "konflikt":
                            //konflikt dancyh, dodajemy nowy rekord z wysłanymi danymi i nadpisujemy to id danymi z serwera

                            String zapytanieSql = "";
                            ContentValues cv = new ContentValues();
                            for( int i = 0; i < columns; i++){
                                //columnArr[i] = cursorSynchronizacja.getString(i);

                                        //danaS.getColumnIndex(nazwyKolumnWBazie[0][i])

                                cv.put(nazwyKolumnWBazie[0][i], (String) danaS.get(nazwyKolumnWBazie[0][i]));//getString(danaS.getColumnIndex(nazwyKolumnWBazie[0][i])));
                                //cv.put(nazwyKolumnWBazie[0][i], danaS.getString(danaS.getColumnIndex(nazwyKolumnWBazie[0][i])));
                                //cv.put(nazwyKolumnWBazie[0][i], danaS.getString(i));
                                //cv.put(nazwyKolumn[i], danaS.getString(i));

                            }
                            Log.d("cv: ", cv.toString());
                            Log.d("cv _id: ", String.valueOf(cv.getAsInteger("_id")));
                            textView.append("id = " + cv.get("_id") + " Konflikt: updatujemy rekord i przenosimy nasz na koniec\n");
                            Log.d("cv.getAsInteger(", String.valueOf(cv.get("_id")));
                            Long idnowego = daneOSQL.dodajDaneOSQL(tabela_nazwa, cv);
                            //i tu jesteśmy w pułapce: zadania oraz stawki są zależne od id firmy
                            //trzeb by hurtem to poprawić
                            if (tabela_nazwa.equals("BZCZBD_Firmy")) {
                                OSQLdaneZlecenia updateZlecenia = new OSQLdaneZlecenia(getActivity());
                                updateZlecenia.updateDaneHurtemIdFirmy(cv.getAsInteger("_id"), Math.toIntExact(idnowego));
                                updateZlecenia = null;//robimy pusty
                                OSQLdaneStawka updateStawka = new OSQLdaneStawka(getActivity());
                                updateStawka.updateDaneHurtemIdFirmy(cv.getAsInteger("_id"), Math.toIntExact(idnowego));
                                updateStawka = null; //robimy pusty
                            }
                            //daneFirma danaUpdate = new daneFirma();

                            daneZserwera = getContentValuesFromJson(Jasonobject, nazwyKolumnWBazie[0]);
                            daneOSQL.updateDaneOSQL(tabela_nazwa, daneZserwera, daneZserwera.getAsInteger("_id"));
                            break;
                        case "insertsrv":
                            nazwyKolumn = new String[]{"data_utworzenia", "data_synchronizacji", "_id"};
                            daneZserwera = getContentValuesFromJson(Jasonobject, nazwyKolumn);
                            textView.append("id = " + daneZserwera.getAsInteger("_id")  + " Nowy rekord, dodaję na serwerze\n");
                            daneOSQL.updateDaneOSQL(tabela_nazwa, daneZserwera, daneZserwera.getAsInteger("_id"));
                            nazwyKolumn = null;
                            break;
                        case "error":
                            textView.append("id = " + danaS.get("_id") + " Wystąpił nieoczekiwany błąd. Spróbuj później\n");
                            break;
                        case "zgodne":
                            //textView.append("id = " + danaS.getInt(danaS.getColumnIndex("_id")) + " Rekord zgodny, brak potrzeby synchronizacji\n");

                            nazwyKolumn = new String[]{"data_synchronizacji", "_id"};
                            daneZserwera = getContentValuesFromJson(Jasonobject, nazwyKolumn);
                            textView.append("id = " + daneZserwera.getAsInteger("_id") + " Rekord zgodny, brak potrzeby synchronizacji\n");
                            daneOSQL.updateDaneOSQL(tabela_nazwa, daneZserwera, daneZserwera.getAsInteger("_id"));
                            break;
                        case "updateserv":
                            //textView.append("id = " + danaS.getInt(danaS.getColumnIndex("_id")) + " Zmieniony rekord, zmieniam na serwerze\n");

                            nazwyKolumn = new String[]{"data_synchronizacji", "_id"};
                            daneZserwera = getContentValuesFromJson(Jasonobject, nazwyKolumn);
                            textView.append("id = " + daneZserwera.getAsInteger("_id") + " Zmieniony rekord, zmieniam na serwerze\n");
                            daneOSQL.updateDaneOSQL(tabela_nazwa, daneZserwera, daneZserwera.getAsInteger("_id"));
                            break;
                        case "updatekon":
                            //textView.append("id = " + danaS.getInt(danaS.getColumnIndex("_id")) + " Nieaktualny rekord, zmieniam w bazie\n");
                            daneZserwera = getContentValuesFromJson(Jasonobject, nazwyKolumnWBazie[0]);
                            textView.append("id = " +  daneZserwera.getAsInteger("_id") + " Nieaktualny rekord, zmieniam w bazie\n");
                            daneOSQL.updateDaneOSQL(tabela_nazwa, daneZserwera, daneZserwera.getAsInteger("_id"));
                            break;
                        case "updateadd":
                            daneZserwera = getContentValuesFromJson(Jasonobject, nazwyKolumnWBazie[0]);
                            textView.append("id = " +  daneZserwera.getAsInteger("_id") + " Nowy lub nieaktualny rekord rekord, zmieniam w bazie\n");
                            //Log.d("danaUpdate", danaUpdate.toString());
                            daneOSQL.dodajZastapDaneOSQL(tabela_nazwa, daneZserwera);
                            //????????????
                            /*try {
                                synchronizujDane(RidtextView);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }*/
                            //?????????????
                            break;
                        case "koniec":
                            textView.append("Brak danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            textView.append("Koniec synchronizacji tabeli  " + tabela_nazwa + "\n");
                            Log.d("Synchro", "Koniec synchronizacji");
                            setCzyKoniec(true);
                            //Tu tu tu
                            //???????????
                            //synchronizujStawki(RidtextView);
                            //???????????
                            break;
                        case "test1":
                            textView.append("test danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            break;
                        case "Token Error":
                            textView.append("Problem z autoryzacją, zarejestruj urządzenie\n");
                            wyslijToken();
                            break;
                    }
                    /*if (czyOstatni){
                        // tu upewniamy się czy wszystko zostało wysłane, bo przy dodawaniu jak na serwerze już istniało id to dodawaliśmy nowy rekord, którego nie uwzględniliśmy w for
                        //teraz tylko wykombinować jak pobrać resztę nieposiadanych rekordów i pozmienianych na serwerze
                        //chyba, że się zapętli :P
                        try {
                            synchronizujDane(RidtextView);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }*/
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                Log.d("Bład", error.toString());
                //Log.d("Bład NR", "toString()");
                //Log.d("Bład NR", error.networkResponse.toString());
                //Log.d("Bład NR", "allHeaders.toString()");
               // Log.d("Bład NR", error.networkResponse.allHeaders.toString());
                //Log.d("Bład NR", "data.toString()");
                //Log.d("Bład NR", error.networkResponse.data.toString());
                //Log.d("Bład NR", "headers.toString()");
                Log.d("Bład NR", error.networkResponse.headers.get("X-Android-Response-Source"));//.toString());
                //Log.d("Bład NR", "networkTimeMs");
                //Log.d("Bład NR", String.valueOf(error.networkResponse.networkTimeMs));
                //Log.d("Bład NR", "notModified");
                //Log.d("Bład NR", String.valueOf(error.networkResponse.notModified));

                textView.append(error.toString() + "\n");
                textView.append(error.networkResponse.headers.get("X-Android-Response-Source") + "\n");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("tokenid", getActualTokenId());
                param.put("tabela", tabela_nazwa);
                //dodajemy flagę jaka to baza: 1 - produkcyjna; 0 - testowa
                param.put("flagabazy", getActiveDataBase());
                Log.d("danas.getcount5", String.valueOf(danaS.size()));
                if (czyWysylamy) {
                    Log.d("data utw: ", String.valueOf(danaS.get("data_utworzenia")));
                    Log.d("data synchro:  ", String.valueOf(danaS.get("data_synchronizacji")));//.getString(danaS.getColumnIndex("data_synchronizacji"))));
                    //Log.d("data synchro:  ", String.valueOf(danaS.getString(danaS.getColumnIndex("data_synchronizacji"))));
                    //danaS.get
                    //String[] nazwyKolumn = danaS.getColumnNames();
                    int columns = danaS.size();//.getColumnCount();
                    Log.d("columns FS: ", String.valueOf(columns));
                    String[] columnArr = new String[columns];
                    Log.d("danas.getcount", String.valueOf(danaS.size()));
                    //Log.d("danas.getcount", String.valueOf(danaS.getCount()));

                    param.putAll(danaS);
                    /*for( int i = 0; i < columns; i++){
                        //columnArr[i] = cursorSynchronizacja.getString(i);
                        if (!danaS.getString(i).isEmpty()) {
                            param.put(nazwyKolumn[i], danaS.getString(i));
                            //Log.d("nazwy kolumn FS: ", String.valueOf(i));
                        }
                    }*/
                    Log.d("danas.getcount", String.valueOf(danaS.size()));
                    //if ((danaS.getLong(danaS.getColumnIndex("data_utworzenia")) >= 0) && (danaS.getLong(danaS.getColumnIndex("data_synchronizacji")) <= 0)) {
                    if ((Long.parseLong(String.valueOf(danaS.get("data_utworzenia"))) >= 0) && (Long.parseLong(String.valueOf(danaS.get("data_synchronizacji"))) <= 0)) {
                        param.put("statusSynchronizacji", "add");
                        Log.d("Wstawiam status synchro:", "add");
                    } else if (Long.parseLong(String.valueOf(danaS.get("data_synchronizacji"))) > 0) {
                    //} else if (danaS.getLong(danaS.getColumnIndex("data_synchronizacji")) > 0) {
                        param.put("statusSynchronizacji", "update");
                        Log.d("Wstawiam status synchro:", "update");
                    }

                }else{
                    param.put("statusSynchronizacji", "give");
                    Log.d("Wstawiam status synchro:", "give");
                }
                //extView.append(String.valueOf(param.values()));
                Log.d("param: ", String.valueOf(param.values()));
                Log.d("danas.getcount", String.valueOf(danaS.size()));
                return param;
                //return null;
            }
        };

        Log.d("sendtokenid: ", String.valueOf(SendTokenID));
        textView.append(String.valueOf(SendTokenID));
        SQLSynchMySingleton.getmInstance(getActivity()).addToRequestQueue(SendTokenID);

        //return czyKoniec;
    }//Tutajj*/

    /*protected <T1 extends ObslugaSQLPodstawowa, T2 extends daneKlasaPodstawowa> void synchronizujDane(int RidtextView, String tablica) throws ClassNotFoundException {
        /*getActualToken(getActivity());
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(FragmentSynchronizacja.UI_SYNCHRONIZE_MESSAGE));

        T1 daneOSQL;
        daneOSQL = new T1.;
        //List<daneKlasaPodstawowa> daneDoSynchronizacji;// = null;
        if (tablica.equals("Firmy")){
            daneOSQL = new OSQLdaneFirma(getActivity());
            //List<> daneDoSynchronizacji;
            //danaS = new daneFirma();
        } else if (tablica.equals("Stawki")){
            daneOSQL = new OSQLdaneStawka(getActivity());
            //List<daneStawka> daneDoSynchronizacji;
            //danaS = new daneStawka();
        } else if (tablica.equals("Zlecenia")){
            daneOSQL = new OSQLdaneZlecenia(getActivity());
            //List<daneZlecenia> daneDoSynchronizacji = daneOSQL.dajDoSynchronizacji();;
            //danaS = new daneZlecenia();
        }

        //T1 daneOSQL = null; //new OSQLdaneFirma(getActivity());
        //T1 daneOSQL = null; //new OSQLdaneFirma(getActivity());
        //Class.forName(tablica[0]) ;
       // Class.;
        //daneOSQL.;
        //List<T2> daneDoSynchronizacji;
        //List<Class.forName(tablica[0]) > daneDoSynchronizacji;
        getClass().asSubclass(daneOSQL.getClass());
        List<asSubclass(daneOSQL.getClass())> daneDoSynchronizacji = daneOSQL.dajDoSynchronizacji();
        TextView textView = (TextView) getActivity().findViewById(RidtextView);
        String tabela_nazwa = daneOSQL.getTableName();
        textView.append("Synchronizujemy tabelę " + tabela_nazwa + "\n");
        if (daneDoSynchronizacji.size() > 0){
            for (  danaS : daneDoSynchronizacji) {
                if (!(daneDoSynchronizacji.get(daneDoSynchronizacji.size()-1).equals(danaS))) {
                    Log.d("ile do synchronizacji1: ", String.valueOf(daneDoSynchronizacji.size()));
                    synchronizujDana(danaS, tabela_nazwa, RidtextView, true, false);
                }else{
                    synchronizujDana(danaS, tabela_nazwa, RidtextView, true, true);
                    Log.d("ile do synchronizacji2: ", String.valueOf(daneDoSynchronizacji.size()));
                }
            }
        }else{
            textView.append("Brak danych do wysłania, sprawdzam czy coś do pobrania "  + "\n");
            Log.d("ile do synchronizacji3: ", String.valueOf(daneDoSynchronizacji.size()));
            T2 danaS = null;//new daneFirma();

            synchronizujDana(danaS, tabela_nazwa, RidtextView, false, false);
        }
    }*/

    //private void synchronizujFirme(daneFirma danaS, String tabela_nazwa, int RidtextView, boolean czyWysylamy, boolean czyOstatni){
    /*private <T extends daneKlasaPodstawowa> void synchronizujDana(T danaS, String tabela_nazwa, int RidtextView, boolean czyWysylamy, boolean czyOstatni) throws ClassNotFoundException {
        TextView textView = (TextView) getActivity().findViewById(RidtextView);

        //ObslugaSQLPodstawowa daneOSQL = null;
        //Object danaS;
        //Object newObj = Class.forName("daneFirma").cast(obj);
        //daneFirma danaS = (daneFirma) ale123;
        //Class.forName(ale123.getClass().getSimpleName()) danaS =  ale123;
        //Class danaS = Class.forName(ale123.getClass().getSimpleName());
        //danaS  = ale123;
        /*if (danaS.getClass().getSimpleName().equals("daneFirma")){
            daneOSQL = new OSQLdaneFirma(getActivity());
           //danaS = new daneFirma();
        } else if (danaS.getClass().getSimpleName().equals("daneStawka")){
            daneOSQL = new OSQLdaneStawka(getActivity());
            //danaS = new daneStawka();
        } else if (danaS.getClass().getSimpleName().equals("daneZlecenia")){
            daneOSQL = new OSQLdaneZlecenia(getActivity());
            //danaS = new daneZlecenia();
        }//*/

        /*switch (danaS.getClass().getSimpleName() ){
        //switch (typDanych){
            case "firmy":
                daneOSQL = new OSQLdaneFirma(getActivity());
                //danaS = new daneFirma();
                //OSQLdaneFirma daneOSQL = new OSQLdaneFirma(getActivity());
                break;
            case "stawki":
                daneOSQL = new OSQLdaneStawka(getActivity());
                //danaS = new daneStawka();
                //OSQLdaneStawka daneOSQL = new OSQLdaneStawka(getActivity());
                break;
            case "zlecenia":
                daneOSQL = new OSQLdaneZlecenia(getActivity());
                //danaS = new daneZlecenia();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + typDanych);
        }//

        StringRequest SendTokenID = new StringRequest(Request.Method.POST, SYNCHRONIZE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //OSQLdaneFirma daneOSQL = new OSQLdaneFirma(getActivity());
                ObslugaSQLPodstawowa daneOSQL = null;
                if (danaS.getClass().getSimpleName().equals("daneFirma")){
                    daneOSQL = new OSQLdaneFirma(getActivity());
                    //danaS = new daneFirma();
                } else if (danaS.getClass().getSimpleName().equals("daneStawka")){
                    daneOSQL = new OSQLdaneStawka(getActivity());
                    //danaS = new daneStawka();
                } else if (danaS.getClass().getSimpleName().equals("daneZlecenia")){
                    daneOSQL = new OSQLdaneZlecenia(getActivity());
                    //danaS = new daneZlecenia();
                }//
                //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                Log.d("Odpowiedź", response);
                Log.d("Odpowiedź długość: ", String.valueOf(response.length()));
                JSONObject Jasonobject = null;
                String statusSynchronizacji = null;
                try {
                    Jasonobject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                statusSynchronizacji = Jasonobject.optString("statusSynchronizacji", null);
                Log.d("status", statusSynchronizacji);



                if (statusSynchronizacji != null){
                    //daneFirma danaUpdate = new daneFirma();
                    T danaUpdate = null;

                    switch (statusSynchronizacji){
                        case "konflikt":
                            //konflikt dancyh, dodajemy nowy rekord z wysłanymi danymi i nadpisujemy to id danymi z serwera
                            //OSQLdaneFirma daneOSQL = new OSQLdaneFirma(getActivity());

                            Long idnowego = daneOSQL.dodajDane(daneOSQL.setContentValues(danaS), daneOSQL.getTableName());//takietam123
                            //i tu jesteśmy w pułapce: zadania oraz stawki są zależne od id firmy
                            //trzeb by hurtem to poprawić
                            if (danaS.getClass().getSimpleName().equals("daneFirma")) {
                                OSQLdaneZlecenia updateZlecenia = new OSQLdaneZlecenia(getActivity());
                                updateZlecenia.updateDaneHurtemIdFirmy(danaS.getId(), Math.toIntExact(idnowego));
                                updateZlecenia = null;//robimy pusty
                                OSQLdaneStawka updateStawka = new OSQLdaneStawka(getActivity());
                                updateStawka.updateDaneHurtemIdFirmy(danaS.getId(), Math.toIntExact(idnowego));
                                updateStawka = null; //robimy pusty
                            }
                            //daneFirma danaUpdate = new daneFirma();
                            textView.append("id = " + danaS.getId() + " Konflikt: updatujemy rekord i przenosimy nasz na koniec\n");
                            danaS.setFromJSON(Jasonobject);
                            daneOSQL.updateDane(daneOSQL.setContentValues(danaS), daneOSQL.getTableName());
                            break;
                        case "insertsrv":
                            textView.append("id = " + danaS.getId() + " Nowy rekord, dodaję na serwerze\n");

                            danaS.setData_utworzenia(Jasonobject.optLong("data_utworzenia", 0));
                            danaS.setData_synchronizacji(Jasonobject.optLong("data_synchronizacji", 0));
                            danaS.setSynchron(1);

                            daneOSQL.updateDane(daneOSQL.setContentValues(danaS), daneOSQL.getTableName());

                            break;
                        case "error":
                            textView.append("id = " + danaS.getId() + " Wystąpił nieoczekiwany błąd. Spróbuj później\n");
                            break;
                        case "zgodne":
                            textView.append("id = " + danaS.getId() + " Rekord zgodny, brak potrzeby synchronizacji\n");

                            danaS.setData_synchronizacji(Jasonobject.optLong("data_synchronizacji", 0));

                            danaS.setSynchron(1);

                            daneOSQL.updateDane(daneOSQL.setContentValues(danaS), daneOSQL.getTableName());
                            break;
                        case "updateserv":
                            textView.append("id = " + danaS.getId() + " Zmieniony rekord, zmieniam na serwerze\n");

                            danaS.setData_synchronizacji(Jasonobject.optLong("data_synchronizacji", 0));
                            danaS.setSynchron(1);
                            daneOSQL.updateDane(daneOSQL.setContentValues(danaS), daneOSQL.getTableName());

                            break;
                        case "updatekon":
                            textView.append("id = " + danaS.getId() + " Nieaktualny rekord, zmieniam w bazie\n");
                            danaS.setFromJSON(Jasonobject);
                            daneOSQL.updateDane(daneOSQL.setContentValues(danaS), daneOSQL.getTableName());
                            break;
                        case "updateadd":
                            danaUpdate.setFromJSON(Jasonobject);// = getFirmaFromJson(Jasonobject);
                            textView.append("id = " + danaUpdate.getId() + " Nowy lub nieaktualny rekord rekord, zmieniam w bazie\n");
                            Log.d("danaUpdate", danaUpdate.toString());
                            daneOSQL.dodajZastapDane(daneOSQL.setContentValues(danaUpdate), daneOSQL.getTableName());
                            //????????????
                            try {
                                synchronizujDane(RidtextView);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            //?????????????
                            break;
                        case "koniec":
                            textView.append("Brak danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            textView.append("Koniec synchronizacji tabeli  " + tabela_nazwa + "\n");
                            Log.d("Synchro", "Koniec synchronizacji");
                            //Tu tu tu
                            //???????????
                            //synchronizujStawki(RidtextView);
                            //???????????
                            break;
                        case "test1":
                            textView.append("test danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            break;
                        case "Token Error":
                            textView.append("Problem z autoryzacją, zarejestruj urządzenie\n");
                            wyslijToken();
                            break;
                    }
                    if (czyOstatni){
                        // tu upewniamy się czy wszystko zostało wysłane, bo przy dodawaniu jak na serwerze już istniało id to dodawaliśmy nowy rekord, którego nie uwzględniliśmy w for
                        //teraz tylko wykombinować jak pobrać resztę nieposiadanych rekordów i pozmienianych na serwerze
                        //chyba, że się zapętli :P
                        try {
                            synchronizujDane(RidtextView);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                Log.d("Bład", error.toString());
                textView.append(error.toString() + "\n");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("tokenid", getActualTokenId());
                //param.
                //Log.d("Token", getActualTokenId());
                param.put("tabela", tabela_nazwa);
                //dodajemy flagę jaka to baza: 1 - produkcyjna; 0 - testowa
                param.put("flagabazy", getActiveDataBase());
                if (czyWysylamy) {
                    Log.d("data utw: ", String.valueOf(danaS.getData_utworzenia()));
                    Log.d("data synchro:  ", String.valueOf(danaS.getData_synchronizacji()));
                    if ((danaS.getData_utworzenia() >= 0) && (danaS.getData_synchronizacji() <= 0)) {
                        param.put("statusSynchronizacji", "add");
                        Log.d("Wstawiam status synchro:", "add");
                    } else if (danaS.getData_synchronizacji() > 0) {
                        param.put("statusSynchronizacji", "update");
                        Log.d("Wstawiam status synchro:", "update");
                    }

                    param.putAll(danaS.getMap());

                }else{
                    param.put("statusSynchronizacji", "give");
                    Log.d("Wstawiam status synchro:", "give");
                }
                return param;
            }
        };
        SQLSynchMySingleton.getmInstance(getActivity()).addToRequestQueue(SendTokenID);
    }//Tutajj*/

    private void synchronizujFirme(daneFirma danaS, String tabela_nazwa, int RidtextView, boolean czyWysylamy, boolean czyOstatni){
        TextView textView = (TextView) getActivity().findViewById(RidtextView);
        StringRequest SendTokenID = new StringRequest(Request.Method.POST, SYNCHRONIZE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                OSQLdaneFirma daneOSQL = new OSQLdaneFirma(getActivity());
                //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                Log.d("Odpowiedź", response);
                Log.d("Odpowiedź długość: ", String.valueOf(response.length()));
                JSONObject Jasonobject = null;
                String statusSynchronizacji = null;
                try {
                    Jasonobject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //try {
                statusSynchronizacji = Jasonobject.optString("statusSynchronizacji", null);
                Log.d("status", statusSynchronizacji);

                /*} catch (JSONException e) {
                    e.printStackTrace();
                }*/

                if (statusSynchronizacji != null){
                    daneFirma danaUpdate = new daneFirma();

                    switch (statusSynchronizacji){
                        case "konflikt":
                            //konflikt dancyh, dodajemy nowy rekord z wysłanymi danymi i nadpisujemy to id danymi z serwera
                            //OSQLdaneFirma daneOSQL = new OSQLdaneFirma(getActivity());
                            Long idnowego = daneOSQL.dodajDane(daneOSQL.contentValues(danaS), daneOSQL.getTableName());
                            //i tu jesteśmy w pułapce: zadania oraz stawki są zależne od id firmy
                            //trzeb by hurtem to poprawić
                            OSQLdaneZlecenia updateZlecenia = new OSQLdaneZlecenia(getActivity());
                            updateZlecenia.updateDaneHurtemIdFirmy(danaS.getId(), Math.toIntExact(idnowego));
                            updateZlecenia = null;//robimy pusty
                            OSQLdaneStawka updateStawka = new OSQLdaneStawka(getActivity());
                            updateStawka.updateDaneHurtemIdFirmy(danaS.getId(), Math.toIntExact(idnowego));
                            updateStawka = null; //robimy pusty
                            //daneFirma danaUpdate = new daneFirma();
                            textView.append("id = " + danaS.getId() + " Konflikt: updatujemy rekord i przenosimy nasz na koniec\n");
                            daneOSQL.updateDane(daneOSQL.contentValues(getFirmaFromJson(Jasonobject)), daneOSQL.getTableName());
                            break;
                        case "insertsrv":
                            textView.append("id = " + danaS.getId() + " Nowy rekord, dodaję na serwerze\n");
                            //try{
                            danaS.setData_utworzenia(Jasonobject.optLong("data_utworzenia", 0));
                            danaS.setData_synchronizacji(Jasonobject.optLong("data_synchronizacji", 0));
                            danaS.setSynchron(1);

                            daneOSQL.updateDane(daneOSQL.contentValues(danaS), daneOSQL.getTableName());
                            /*}catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                            break;
                        case "error":
                            textView.append("id = " + danaS.getId() + " Wystąpił nieoczekiwany błąd. Spróbuj później\n");
                            break;
                        case "zgodne":
                            textView.append("id = " + danaS.getId() + " Rekord zgodny, brak potrzeby synchronizacji\n");
                            //try {
                            danaS.setData_synchronizacji(Jasonobject.optLong("data_synchronizacji", 0));
                            /*} catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                            danaS.setSynchron(1);

                            daneOSQL.updateDane(daneOSQL.contentValues(danaS), daneOSQL.getTableName());
                            break;
                        case "updateserv":
                            textView.append("id = " + danaS.getId() + " Zmieniony rekord, zmieniam na serwerze\n");
                            //try{
                            danaS.setData_synchronizacji(Jasonobject.optLong("data_synchronizacji", 0));
                            danaS.setSynchron(1);
                            daneOSQL.updateDane(daneOSQL.contentValues(danaS), daneOSQL.getTableName());
                            /*}catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                            break;
                        case "updatekon":
                            textView.append("id = " + danaS.getId() + " Nieaktualny rekord, zmieniam w bazie\n");
                            daneOSQL.updateDane(daneOSQL.contentValues(getFirmaFromJson(Jasonobject)), daneOSQL.getTableName());
                            break;
                        case "updateadd":
                            danaUpdate = getFirmaFromJson(Jasonobject);
                            textView.append("id = " + danaUpdate.getId() + " Nowy lub nieaktualny rekord rekord, zmieniam w bazie\n");
                            Log.d("danaUpdate", danaUpdate.toString());
                            daneOSQL.dodajZastapDane(daneOSQL.contentValues(danaUpdate), daneOSQL.getTableName());
                            synchronizujFirmy(RidtextView);
                            break;
                        case "koniec":
                            textView.append("Brak danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            textView.append("Koniec synchronizacji tabeli  " + tabela_nazwa + "\n");
                            Log.d("Synchro", "Koniec synchronizacji");
                            //Tu tu tu
                            synchronizujStawki(RidtextView);
                            break;
                        case "test1":
                            textView.append("test danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            break;
                        case "Token Error":
                            textView.append("Problem z autoryzacją, zarejestruj urządzenie\n");
                            wyslijToken();
                            break;
                    }
                    if (czyOstatni){
                        // tu upewniamy się czy wszystko zostało wysłane, bo przy dodawaniu jak na serwerze już istniało id to dodawaliśmy nowy rekord, którego nie uwzględniliśmy w for
                        //teraz tylko wykombinować jak pobrać resztę nieposiadanych rekordów i pozmienianych na serwerze
                        //chyba, że się zapętli :P
                        synchronizujFirmy(RidtextView);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                Log.d("Bład", error.toString());
                textView.append(error.toString() + "\n");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("tokenid", getActualTokenId());
                //Log.d("Token", getActualTokenId());
                param.put("tabela", tabela_nazwa);
                //dodajemy flagę jaka to baza: 1 - produkcyjna; 0 - testowa
                param.put("flagabazy", getActiveDataBase());
                if (czyWysylamy) {
                    Log.d("data utw: ", String.valueOf(danaS.getData_utworzenia()));
                    Log.d("data synchro:  ", String.valueOf(danaS.getData_synchronizacji()));
                    if ((danaS.getData_utworzenia() >= 0) && (danaS.getData_synchronizacji() <= 0)) {
                        param.put("statusSynchronizacji", "add");
                        Log.d("Wstawiam status synchro:", "add");
                    } else if (danaS.getData_synchronizacji() > 0) {
                        param.put("statusSynchronizacji", "update");
                        Log.d("Wstawiam status synchro:", "update");
                    }
                    param.put("_id", String.valueOf(danaS.getId()));
                    param.put("nazwa", danaS.getNazwa());
                    if (danaS.getNumer() != null) {
                        param.put("numer", danaS.getNumer());
                    } else {
                        param.put("numer", "0");
                    }
                    param.put("nr_telefonu", String.valueOf(danaS.getNr_telefonu()));
                    param.put("ulica_nr", danaS.getUlica_nr());
                    param.put("miasto", danaS.getMiasto());
                    param.put("typ", danaS.getTyp());
                    param.put("kalendarz_id", String.valueOf(danaS.getKalendarz_id()));
                    param.put("uwagi", danaS.getUwagi());
                    if (danaS.getPoprzedni_rekord_id() != null) {
                        param.put("poprzedni_rekord_id", String.valueOf(danaS.getPoprzedni_rekord_id()));
                    } else {
                        param.put("poprzedni_rekord_id", "0");
                    }
                    if (danaS.getPoprzedni_rekord_data_usuniecia() != null) {
                        param.put("poprzedni_rekord_data_usuniecia", danaS.getPoprzedni_rekord_data_usuniecia());
                    } else {
                        param.put("poprzedni_rekord_data_usuniecia", "0");
                    }
                    if (danaS.getPoprzedni_rekord_powod_usuniecia() != null) {
                        param.put("poprzedni_rekord_powod_usuniecia", danaS.getPoprzedni_rekord_powod_usuniecia());
                    } else {
                        param.put("poprzedni_rekord_powod_usuniecia", "");
                    }
                    param.put("czy_widoczny", String.valueOf(danaS.getCzy_widoczny()));
                    param.put("data_utworzenia", String.valueOf(danaS.getData_utworzenia()));
                    param.put("data_synchronizacji", String.valueOf(danaS.getData_synchronizacji()));
                }else{
                    param.put("statusSynchronizacji", "give");
                    Log.d("Wstawiam status synchro:", "give");
                }
                return param;
            }
        };
        SQLSynchMySingleton.getmInstance(getActivity()).addToRequestQueue(SendTokenID);
    }

    protected void synchronizujFirmy(int RidtextView) {
        /*getActualToken(getActivity());*/
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(FragmentSynchronizacja.UI_SYNCHRONIZE_MESSAGE));

        OSQLdaneFirma daneOSQL = new OSQLdaneFirma(getActivity());
        List<daneFirma> daneDoSynchronizacji;
        daneDoSynchronizacji = daneOSQL.dajDoSynchronizacji();
        TextView textView = (TextView) getActivity().findViewById(RidtextView);
        String tabela_nazwa = daneOSQL.getTableName();
        textView.append("Synchronizujemy tabelę " + tabela_nazwa + "\n");
        if (daneDoSynchronizacji.size() > 0){
            for (daneFirma danaS: daneDoSynchronizacji) {
                if (!(daneDoSynchronizacji.get(daneDoSynchronizacji.size()-1).equals(danaS))) {
                    Log.d("ile do synchronizacji1: ", String.valueOf(daneDoSynchronizacji.size()));
                    synchronizujFirme(danaS, tabela_nazwa, RidtextView, true, false);
                }else{
                    synchronizujFirme(danaS, tabela_nazwa, RidtextView, true, true);
                    Log.d("ile do synchronizacji2: ", String.valueOf(daneDoSynchronizacji.size()));
                }
            }
        }else{
            textView.append("Brak danych do wysłania, sprawdzam czy coś do pobrania "  + "\n");
            Log.d("ile do synchronizacji3: ", String.valueOf(daneDoSynchronizacji.size()));
            daneFirma danaS = new daneFirma();

            synchronizujFirme(danaS, tabela_nazwa, RidtextView, false, false);
        }
    }

    protected void synchronizujStawki(int RidtextView) {
        /*getActualToken(getActivity());*/
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(FragmentSynchronizacja.UI_SYNCHRONIZE_MESSAGE));

        OSQLdaneStawka daneOSQL = new OSQLdaneStawka(getActivity());
        List<daneStawka> daneDoSynchronizacji;
        daneDoSynchronizacji = daneOSQL.dajDoSynchronizacji();
        TextView textView = (TextView) getActivity().findViewById(RidtextView);
        String tabela_nazwa = daneOSQL.getTableName();
        textView.append("Synchronizujemy tabelę " + tabela_nazwa + "\n");
        if (daneDoSynchronizacji.size() > 0){
            for (daneStawka danaS: daneDoSynchronizacji) {
                if (!(daneDoSynchronizacji.get(daneDoSynchronizacji.size()-1).equals(danaS))) {
                    synchronizujStawke(danaS, tabela_nazwa, RidtextView, true, false);
                }else{
                    synchronizujStawke(danaS, tabela_nazwa, RidtextView, true, true);
                }
            }
        }else{
            textView.append("Brak danych do wysłania, sprawdzam czy coś do pobrania "  + "\n");
            daneStawka danaS = new daneStawka();

            synchronizujStawke(danaS, tabela_nazwa, RidtextView, false, false);
        }
    }

    protected void synchronizujStawke(daneStawka danaS, String tabela_nazwa, int RidtextView, boolean czyWysylamy, boolean czyOstatni) {
        TextView textView = (TextView) getActivity().findViewById(RidtextView);
        StringRequest SendTokenID = new StringRequest(Request.Method.POST, SYNCHRONIZE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                OSQLdaneStawka daneOSQL = new OSQLdaneStawka(getActivity());
                //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                Log.d("Odpowiedź", response);
                Log.d("Odpowiedź długość: ", String.valueOf(response.length()));
                JSONObject Jasonobject = null;
                String statusSynchronizacji = null;
                try {
                    Jasonobject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    statusSynchronizacji = Jasonobject.getString("statusSynchronizacji");
                    Log.d("status", statusSynchronizacji);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (statusSynchronizacji != null){
                    daneStawka danaUpdate = new daneStawka();

                    switch (statusSynchronizacji){
                        case "konflikt":
                            //konflikt dancyh, dodajemy nowy rekord z wysłanymi danymi i nadpisujemy to id danymi z serwera
                            //OSQLdaneFirma daneOSQL = new OSQLdaneFirma(getActivity());
                            Long idnowego = daneOSQL.dodajDane(daneOSQL.contentValues(danaS), daneOSQL.getTableName());
                            textView.append("id = " + danaS.getId() + " Konflikt: updatujemy rekord i przenosimy nasz na koniec\n");
                            daneOSQL.updateDane(daneOSQL.contentValues(getStawkaFronJson(Jasonobject)), daneOSQL.getTableName());
                            break;
                        case "insertsrv":
                            textView.append("id = " + danaS.getId() + " Nowy rekord, dodaję na serwerze\n");
                            try{
                                danaS.setData_utworzenia(Jasonobject.getLong("data_utworzenia"));
                                danaS.setData_synchronizacji(Jasonobject.getLong("data_synchronizacji"));
                                danaS.setSynchron(1);

                                daneOSQL.updateDane(daneOSQL.contentValues(danaS), daneOSQL.getTableName());
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "error":
                            textView.append("id = " + danaS.getId() + " Wystąpił nieoczekiwany błąd. Spróbuj później\n");
                            break;
                        case "zgodne":
                            textView.append("id = " + danaS.getId() + " Rekord zgodny, brak potrzeby synchronizacji\n");
                            break;
                        case "updateserv":
                            textView.append("id = " + danaS.getId() + " Zmieniony rekord, zmieniam na serwerze\n");
                            try{
                                danaS.setData_synchronizacji(Jasonobject.getLong("data_synchronizacji"));
                                danaS.setSynchron(1);
                                daneOSQL.updateDane(daneOSQL.contentValues(danaS), daneOSQL.getTableName());
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "updatekon":
                            textView.append("id = " + danaS.getId() + " Nieaktualny rekord, zmieniam w bazie\n");
                            daneOSQL.updateDane(daneOSQL.contentValues(getStawkaFronJson(Jasonobject)), daneOSQL.getTableName());
                            break;
                        case "updateadd":
                            danaUpdate = getStawkaFronJson(Jasonobject);
                            textView.append("id = " + danaUpdate.getId() + " Nowy lub nieaktualny rekord rekord, zmieniam w bazie\n");
                            daneOSQL.dodajZastapDane(daneOSQL.contentValues(danaUpdate), daneOSQL.getTableName());
                            synchronizujStawki(RidtextView);
                            break;
                        case "koniec":
                            textView.append("Brak danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            textView.append("Koniec synchronizacji tabeli  " + tabela_nazwa + "\n");
                            synchronizujZlecenia(RidtextView);
                            break;
                        case "test1":
                            textView.append("test danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            break;
                        case "Token Error":
                            textView.append("Problem z autoryzacją, zarejestruj urządzenie\n");
                            wyslijToken();
                            break;
                    }
                    if (czyOstatni){
                        // tu upewniamy się czy wszystko zostało wysłane, bo przy dodawaniu jak na serwerze już istniało id to dodawaliśmy nowy rekord, którego nie uwzględniliśmy w for
                        //teraz tylko wykombinować jak pobrać resztę nieposiadanych rekordów i pozmienianych na serwerze
                        //chyba, że się zapętli :P
                        synchronizujStawki(RidtextView);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                Log.d("Bład", error.toString());
                textView.append(error.toString() + "\n");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("tokenid", getActualTokenId());
                //Log.d("Token", getActualTokenId());
                param.put("tabela", tabela_nazwa);
                //dodajemy flagę jaka to baza: 1 - produkcyjna; 0 - testowa
                param.put("flagabazy", getActiveDataBase());
                if (czyWysylamy) {
                    Log.d("data utw: ", String.valueOf(danaS.getData_utworzenia()));
                    Log.d("data synchro:  ", String.valueOf(danaS.getData_synchronizacji()));
                    if ((danaS.getData_utworzenia() >= 0) && (danaS.getData_synchronizacji() <= 0)) {
                        param.put("statusSynchronizacji", "add");
                        Log.d("Wstawiam status synchro:", "add");
                    } else if (danaS.getData_synchronizacji() > 0) {
                        param.put("statusSynchronizacji", "update");
                        Log.d("Wstawiam status synchro:", "update");
                    }

                    param.put("_id", String.valueOf(danaS.getId()));
                    param.put("firma_id", String.valueOf(danaS.getFirma_id()));
                    param.put("stawka", String.valueOf(danaS.getStawka()));
                    param.put("poczatek", danaS.getPoczatek());
                    param.put("koniec", danaS.getKoniec());

                    param.put("uwagi", danaS.getUwagi());
                    if (danaS.getPoprzedni_rekord_id() != null) {
                        param.put("poprzedni_rekord_id", String.valueOf(danaS.getPoprzedni_rekord_id()));
                    } else {
                        param.put("poprzedni_rekord_id", "0");
                    }
                    if (danaS.getPoprzedni_rekord_data_usuniecia() != null) {
                        param.put("poprzedni_rekord_data_usuniecia", danaS.getPoprzedni_rekord_data_usuniecia());
                    } else {
                        param.put("poprzedni_rekord_data_usuniecia", "0");
                    }
                    if (danaS.getPoprzedni_rekord_powod_usuniecia() != null) {
                        param.put("poprzedni_rekord_powod_usuniecia", danaS.getPoprzedni_rekord_powod_usuniecia());
                    } else {
                        param.put("poprzedni_rekord_powod_usuniecia", "");
                    }
                    param.put("czy_widoczny", String.valueOf(danaS.getCzy_widoczny()));
                    param.put("data_utworzenia", String.valueOf(danaS.getData_utworzenia()));
                    param.put("data_synchronizacji", String.valueOf(danaS.getData_synchronizacji()));
                }else{
                    param.put("statusSynchronizacji", "give");
                    Log.d("Wstawiam status synchro:", "give");
                }
                return param;
            }
        };
        SQLSynchMySingleton.getmInstance(getActivity()).addToRequestQueue(SendTokenID);
    }

    protected void synchronizujZlecenia(int RidtextView) {
        /*getActualToken(getActivity());*/

        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(FragmentSynchronizacja.UI_SYNCHRONIZE_MESSAGE));

        OSQLdaneZlecenia daneOSQL = new OSQLdaneZlecenia(getActivity());
        List<daneZlecenia> daneDoSynchronizacji;
        daneDoSynchronizacji = daneOSQL.dajDoSynchronizacji();
        TextView textView = (TextView) getActivity().findViewById(RidtextView);

        String tabela_nazwa = daneOSQL.getTableName();
        textView.append("Synchronizujemy tabelę " + tabela_nazwa + "\n");
        if (daneDoSynchronizacji.size() > 0){
            for (daneZlecenia danaS: daneDoSynchronizacji) {
                if (!(daneDoSynchronizacji.get(daneDoSynchronizacji.size()-1).equals(danaS))) {
                    synchronizujZlecenie(danaS, tabela_nazwa, RidtextView, true, false);
                }else{
                    synchronizujZlecenie(danaS, tabela_nazwa, RidtextView, true, true);
                }
            }
        }else{
            textView.append("Brak danych do wysłania, sprawdzam czy coś do pobrania "  + "\n");
            daneZlecenia danaS = new daneZlecenia();

            synchronizujZlecenie(danaS, tabela_nazwa, RidtextView, false, false);
        }
    }

    protected void synchronizujZlecenie(daneZlecenia danaS, String tabela_nazwa, int RidtextView, boolean czyWysylamy, boolean czyOstatni) {
        TextView textView = (TextView) getActivity().findViewById(RidtextView);
        StringRequest SendTokenID = new StringRequest(Request.Method.POST, SYNCHRONIZE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                OSQLdaneZlecenia daneOSQL = new OSQLdaneZlecenia(getActivity());
                //Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                Log.d("Odpowiedź", response);
                Log.d("Odpowiedź długość: ", String.valueOf(response.length()));
                JSONObject Jasonobject = null;
                String statusSynchronizacji = null;
                try {
                    Jasonobject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    statusSynchronizacji = Jasonobject.getString("statusSynchronizacji");
                    Log.d("status", statusSynchronizacji);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (statusSynchronizacji != null){
                    daneZlecenia danaUpdate = new daneZlecenia();

                    switch (statusSynchronizacji){
                        case "konflikt":
                            //konflikt dancyh, dodajemy nowy rekord z wysłanymi danymi i nadpisujemy to id danymi z serwera
                            //OSQLdaneFirma daneOSQL = new OSQLdaneFirma(getActivity());
                            Long idnowego = daneOSQL.dodajDane(daneOSQL.contentValues(danaS), daneOSQL.getTableName());
                            textView.append("id = " + danaS.getId() + " Konflikt: updatujemy rekord i przenosimy nasz na koniec\n");
                            daneOSQL.updateDane(daneOSQL.contentValues(getZlecenieFronJson(Jasonobject)), daneOSQL.getTableName());
                            break;
                        case "insertsrv":
                            textView.append("id = " + danaS.getId() + " Nowy rekord, dodaję na serwerze\n");
                            try{
                                danaS.setData_utworzenia(Jasonobject.getLong("data_utworzenia"));
                                danaS.setData_synchronizacji(Jasonobject.getLong("data_synchronizacji"));
                                danaS.setSynchron(1);

                                daneOSQL.updateDane(daneOSQL.contentValues(danaS), daneOSQL.getTableName());
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "error":
                            textView.append("id = " + danaS.getId() + " Wystąpił nieoczekiwany błąd. Spróbuj później\n");
                            break;
                        case "zgodne":
                            textView.append("id = " + danaS.getId() + " Rekord zgodny, brak potrzeby synchronizacji\n");
                            break;
                        case "updateserv":
                            textView.append("id = " + danaS.getId() + " Zmieniony rekord, zmieniam na serwerze\n");
                            try{
                                danaS.setData_synchronizacji(Jasonobject.getLong("data_synchronizacji"));
                                danaS.setSynchron(1);
                                daneOSQL.updateDane(daneOSQL.contentValues(danaS), daneOSQL.getTableName());
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "updatekon":
                            textView.append("id = " + danaS.getId() + " Nieaktualny rekord, zmieniam w bazie\n");
                            daneOSQL.updateDane(daneOSQL.contentValues(getZlecenieFronJson(Jasonobject)), daneOSQL.getTableName());
                            break;
                        case "updateadd":
                            danaUpdate = getZlecenieFronJson(Jasonobject);
                            textView.append("id = " + danaUpdate.getId() + " Nowy lub nieaktualny rekord rekord, zmieniam w bazie\n");
                            daneOSQL.dodajZastapDane(daneOSQL.contentValues(danaUpdate), daneOSQL.getTableName());
                            synchronizujZlecenia(RidtextView);
                            break;
                        case "koniec":
                            textView.append("Brak danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            break;
                        case "test1":
                            textView.append("test danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            break;
                        case "Token Error":
                            textView.append("Problem z autoryzacją, zarejestruj urządzenie\n");
                            wyslijToken();
                            break;
                    }
                    if (czyOstatni){
                        // tu upewniamy się czy wszystko zostało wysłane, bo przy dodawaniu jak na serwerze już istniało id to dodawaliśmy nowy rekord, którego nie uwzględniliśmy w for
                        //teraz tylko wykombinować jak pobrać resztę nieposiadanych rekordów i pozmienianych na serwerze
                        //chyba, że się zapętli :P
                        synchronizujZlecenia(RidtextView);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                Log.d("Bład", error.toString());
                textView.append(error.toString() + "\n");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                param.put("tokenid", getActualTokenId());
                //Log.d("Token", getActualTokenId());
                param.put("tabela", tabela_nazwa);
                //dodajemy flagę jaka to baza: 1 - produkcyjna; 0 - testowa
                param.put("flagabazy", getActiveDataBase());
                if (czyWysylamy) {
                    Log.d("data utw: ", String.valueOf(danaS.getData_utworzenia()));
                    Log.d("data synchro:  ", String.valueOf(danaS.getData_synchronizacji()));
                    if ((danaS.getData_utworzenia() >= 0) && (danaS.getData_synchronizacji() <= 0)) {
                        param.put("statusSynchronizacji", "add");
                        Log.d("Wstawiam status synchro:", "add");
                    } else if (danaS.getData_synchronizacji() > 0) {
                        param.put("statusSynchronizacji", "update");
                        Log.d("Wstawiam status synchro:", "update");
                    }

                    param.put("_id", String.valueOf(danaS.getId()));
                    param.put("firma_id", String.valueOf(danaS.getFirma_id()));
                    param.put("czas_rozpoczecia", String.valueOf(danaS.getCzas_rozpoczecia()));
                    param.put("opis", danaS.getOpis());
                    param.put("status", danaS.getStatus());
                    if (danaS.getRozliczona() != null) {
                        param.put("rozliczona", danaS.getRozliczona());
                    }else{param.put("rozliczona","");}
                    param.put("czas_zakonczenia", String.valueOf(danaS.getCzas_zakonczenia()));
                    param.put("kalendarz_id", String.valueOf(danaS.getKalendarz_id()));
                    //param.put("kalendarz_id_long", String.valueOf(danaS.getKalendarz_id_long()));
                    param.put("kalendarz_zadanie_id", String.valueOf(danaS.getKalendarz_zadanie_id()));
                    param.put("czas_zawieszenia", String.valueOf(danaS.getCzas_zawieszenia()));
                    if (danaS.getUwagi() != null) {
                        param.put("uwagi", danaS.getUwagi());
                    }
                    if (danaS.getPoprzedni_rekord_id() != null) {
                        param.put("poprzedni_rekord_id", String.valueOf(danaS.getPoprzedni_rekord_id()));
                    } else {
                        param.put("poprzedni_rekord_id", "0");
                    }
                    if (danaS.getPoprzedni_rekord_data_usuniecia() != null) {
                        param.put("poprzedni_rekord_data_usuniecia", danaS.getPoprzedni_rekord_data_usuniecia());
                    } else {
                        param.put("poprzedni_rekord_data_usuniecia", "0");
                    }
                    if (danaS.getPoprzedni_rekord_powod_usuniecia() != null) {
                        param.put("poprzedni_rekord_powod_usuniecia", danaS.getPoprzedni_rekord_powod_usuniecia());
                    } else {
                        param.put("poprzedni_rekord_powod_usuniecia", "");
                    }
                    param.put("czy_widoczny", String.valueOf(danaS.getCzy_widoczny()));
                    param.put("data_utworzenia", String.valueOf(danaS.getData_utworzenia()));
                    param.put("data_synchronizacji", String.valueOf(danaS.getData_synchronizacji()));
                }else{
                    param.put("statusSynchronizacji", "give");
                    Log.d("Wstawiam status synchro:", "give");
                }
                return param;
            }
        };
        SQLSynchMySingleton.getmInstance(getActivity()).addToRequestQueue(SendTokenID);
    }
}
