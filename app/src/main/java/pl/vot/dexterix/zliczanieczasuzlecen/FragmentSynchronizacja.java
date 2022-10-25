package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentSynchronizacja extends FragmentPodstawowy {

    protected String TOKEN_ID_URL = "https://dexterix.vot.pl/zliczazle/sendtokenid.php";
    protected BroadcastReceiver broadcastReceiver;
    protected String SYNCHRONIZE_URL = "https://dexterix.vot.pl/zliczazle/synchronizemessages.php";

    public static final String UI_SYNCHRONIZE_MESSAGE = "dexterix.vot.pl.synchronizemysqltosqlitedatabase.UI_SYNCHRONIZE_SQLITE";

    private boolean czyKoniec = false;
    private int aktualnaTabela = 0;

    private void setAktualnaTabela(int x){
        this.aktualnaTabela = x;
    }

    private int getAktualnaTabela(){
        return aktualnaTabela;
    }

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

    protected void synchronizujDane(int RidtextView){

        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(FragmentSynchronizacja.UI_SYNCHRONIZE_MESSAGE));

        ObslugaSQL daneOSQL = new ObslugaSQL(getActivity());
        TextView textView = (TextView) getActivity().findViewById(RidtextView);

        //Tabele do synchronizacji
        List<String> tablice = daneOSQL.getTabliceDoSynchronizacji();
        //List<String> tablice = Arrays.asList(new String[]{"BZCZBD_Firmy"});
        //Cursor cursorSynchronizacja = null;
        if (getAktualnaTabela() < tablice.size()) {
            String table = tablice.get(getAktualnaTabela());

            try {
                //for (String table : tablice){
                Log.d("tabela: ", table);
                String zapytanie = "SELECT * FROM " + table + " WHERE data_synchronizacji IN ('0', '1')";
                String zapytanieU = "UPDATE " + table + " SET data_synchronizacji = '2' WHERE _id = ";
                //cursorSynchronizacja = daneOSQL.rawQuery(zapytanie, null);
                List<Map> dane = daneOSQL.getDaneDoSynchronizacjiMap(table);
                //String[] nazwyKolumn = cursorSynchronizacja.getColumnNames();
                //int columns = cursorSynchronizacja.getColumnCount();
                //String[] columnArr = new String[columns];
                String tabela_nazwa = table;
                textView.append("Synchronizujemy tabelę " + tabela_nazwa + "\n");
                int iloscRekordowDoSynchroZTelefonu = daneOSQL.getIloscRekordowDoSynchronizacji(table);
                textView.append("Rekordów do synchronizacji z telefonu: " + iloscRekordowDoSynchroZTelefonu + "\n");
                if (iloscRekordowDoSynchroZTelefonu > 0) {
                    //cursorSynchronizacja.moveToFirst();
                    int i = 0;
                    int dlugosscNaPoczatku = 0;
                    Map<String, String> dana = new HashMap<String, String>();
                    //while (!dane.isEmpty()) {
                    //Log.d("ile i: ", String.valueOf(i));
                    //Log.d("ile do synchronizacji1: ", String.valueOf(dane.size()));
                    dana = dane.get(0);
                    dlugosscNaPoczatku = dane.size();
                    /*Map<String, String> dane = new HashMap<String, String>();
                    for (int j = 0; j < columns; j++) {
                        if (!cursorSynchronizacja.isNull(j)) {
                            //cursorSynchronizacja.getColumnCount();
                            //Log.d("ja i mój jj: ", String.valueOf(j));
                            //Log.d("kolumna: ", cursorSynchronizacja.getColumnName(j));

                            //Log.d("ile kolumn: ", String.valueOf(cursorSynchronizacja.getColumnCount()));
                            //Log.d(nazwyKolumn[j], cursorSynchronizacja.getString(j));
                            dane.put(nazwyKolumn[j], cursorSynchronizacja.getString(j));

                            //Log.d(nazwyKolumn[j], cursorSynchronizacja.getString(j));
                        }
                    }*/
                    Log.d("dane: ", dana.toString());
                    synchronizujDana(dana, tabela_nazwa, RidtextView, true, false, i);
                    Log.d("a dla jaj: ", String.valueOf(dana.get("data_utworzenia")));//.getString(cursorSynchronizacja.getColumnIndex("data_utworzenia"))));
                    //cursorSynchronizacja = null;
                    //cursorSynchronizacja.close();
                    //Log.d("KursorSynschro długość 1", String.valueOf(dane.size()));
                    //cursorSynchronizacja.getInt(cursorSynchronizacja.getColumnIndex("_id"));
                    //ustawiamy dane synchronizacji na 2 dla aktualnego rekordu, zobaczmy co odwali kursor
                    //ContentValues cv1 = new ContentValues();
                    //cv1.put("data_synchronizacji", "2");
                    //daneOSQL.updateDaneOSQL(table, cv1, Integer.valueOf(dana.get("_id")));//.getInt(cursorSynchronizacja.getColumnIndex("_id")));
                    //cv1 = null;
                    //dane = daneOSQL.getDaneDoSynchronizacjiMap(table);
                    //cursorSynchronizacja = daneOSQL.rawQuery(zapytanie, null);
                    //Log.d("KursorSynschro długość 2", String.valueOf(dane.size()));
                    //cursorSynchronizacja.moveToFirst();
                    dana = null;
                    i++;
                        /*if (i > 10) {
                            //break;
                        }*/
                    //}
                    //zatem to co miałem do synchro poszło, teraz te konflikty
                    //cursorSynchronizacja = null;
                } else {
                    textView.append("Brak danych do wysłania, sprawdzam czy coś do pobrania " + "\n");
                    Log.d("Fragment Synchronizacja: ", "Pobieramy dane");
                    //if (i > 10){break;}
                    //i = 0;
                    //while (!czyKoniec) {
                        Map<String, String> danePobierane = new HashMap<String, String>();
                        synchronizujDana(danePobierane, tabela_nazwa, RidtextView, false, false, 1);
                        //i++;
                    //}
                    textView.append("Brak danych do pobrania w tabeli " + table + "\n");
                }
                //cursorSynchronizacja = null;
                //}
            } catch (Exception sqlEx) {
                Log.e("wyjebalo: ", sqlEx.getMessage(), sqlEx);
            }

        }else{
            textView.append("Brak danych do synchronizacji, Koniec");
        }
    }

    private ContentValues getContentValuesFromJson(JSONObject json, String[] nazwyKolumnWBazie){
        ContentValues cv = new ContentValues();

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
        int columns = danaS.size();
        String[][] nazwyKolumnWBazie = daneOSQL.getTablePola(tabela_nazwa);
        StringRequest SendTokenID = new StringRequest(Request.Method.POST, SYNCHRONIZE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Odpowiedź", response);
                Log.d("Odpowiedź długość: ", String.valueOf(response.length()));
                JSONObject Jasonobject = null;
                String statusSynchronizacji = null;
                try {
                    Jasonobject = new JSONObject(response);
                } catch ( JSONException e) {
                    e.printStackTrace();
                }
                ContentValues daneZserwera = null;
                statusSynchronizacji = Jasonobject.optString("statusSynchronizacji", null);
                Log.d("status", statusSynchronizacji);
                String tabelaNazwa = Jasonobject.optString("tabela_nazwa");
                Log.d("Tabela z JSON: ", tabelaNazwa);
                String idZserwera = Jasonobject.optString("id");
                Log.d("id z serwera: ", idZserwera);
                if (statusSynchronizacji != null){
                    String[] nazwyKolumn = new String[0];// = null;
                    switch (statusSynchronizacji){
                        case "konflikt":
                            //konflikt dancyh, dodajemy nowy rekord z wysłanymi danymi i nadpisujemy to id danymi z serwera
                            String zapytanieSql = "";
                            ContentValues cv = new ContentValues();
                            for( int i = 0; i < columns; i++){
                                cv.put(nazwyKolumnWBazie[0][i], (String) danaS.get(nazwyKolumnWBazie[0][i]));//getString(danaS.getColumnIndex(nazwyKolumnWBazie[0][i])));
                            }
                            //tak sobie robimy jaja dodadjemy do cv wartosc czy zamykac zeby wiedziec czy zamknąc baze
                            cv.put("CzyZamykac", 0);
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
                                //updateZlecenia = null;//robimy pusty
                                updateZlecenia.close();
                                OSQLdaneStawka updateStawka = new OSQLdaneStawka(getActivity());
                                updateStawka.updateDaneHurtemIdFirmy(cv.getAsInteger("_id"), Math.toIntExact(idnowego));
                                //updateStawka = null; //robimy pusty
                                updateStawka.close();
                            }
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
                            nazwyKolumn = new String[]{"data_synchronizacji", "_id"};
                            daneZserwera = getContentValuesFromJson(Jasonobject, nazwyKolumn);
                            textView.append("id = " + daneZserwera.getAsInteger("_id") + " Zmieniony rekord, zmieniam na serwerze\n");
                            daneOSQL.updateDaneOSQL(tabela_nazwa, daneZserwera, daneZserwera.getAsInteger("_id"));
                            break;
                        case "updatekon":
                            daneZserwera = getContentValuesFromJson(Jasonobject, nazwyKolumnWBazie[0]);
                            textView.append("id = " +  daneZserwera.getAsInteger("_id") + " Nieaktualny rekord, zmieniam w bazie\n");
                            daneOSQL.updateDaneOSQL(tabela_nazwa, daneZserwera, daneZserwera.getAsInteger("_id"));
                            break;
                        case "updateadd":
                            daneZserwera = getContentValuesFromJson(Jasonobject, nazwyKolumnWBazie[0]);
                            textView.append("id = " +  daneZserwera.getAsInteger("_id") + " Nowy lub nieaktualny rekord rekord, zmieniam w bazie\n");
                            daneOSQL.dodajZastapDaneOSQL(tabela_nazwa, daneZserwera);
                            break;
                        case "koniec":
                            textView.append("Brak danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            textView.append("Koniec synchronizacji tabeli  " + tabela_nazwa + "\n");
                            Log.d("Synchro", "Koniec synchronizacji");
                            setCzyKoniec(true);
                            setAktualnaTabela(getAktualnaTabela() + 1);
                            break;
                        case "test1":
                            textView.append("test danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            break;
                        case "Token Error":
                            textView.append("Problem z autoryzacją, zarejestruj urządzenie\n");
                            wyslijToken();
                            break;
                    }
                    daneOSQL.close();
                    synchronizujDane(RidtextView);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                Log.d("Bład", error.toString());
                Log.d("Bład NR", error.networkResponse.headers.get("X-Android-Response-Source"));//.toString());
                textView.append(error.toString() + "\n");
                textView.append(error.networkResponse.headers.get("X-Android-Response-Source") + "\n");
                daneOSQL.close();
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
                    Log.d("data synchro:  ", String.valueOf(danaS.get("data_synchronizacji")));
                    int columns = danaS.size();//.getColumnCount();
                    //Log.d("columns FS: ", String.valueOf(columns));
                    //Log.d("danas.getcount", String.valueOf(danaS.size()));

                    param.putAll(danaS);

                    Log.d("danas.getcount", String.valueOf(danaS.size()));
                    if ((Long.parseLong(String.valueOf(danaS.get("data_utworzenia"))) >= 0) && (Long.parseLong(String.valueOf(danaS.get("data_synchronizacji"))) <= 0)) {
                        param.put("statusSynchronizacji", "add");
                        Log.d("Wstawiam status synchro:", "add");
                    } else if (Long.parseLong(String.valueOf(danaS.get("data_synchronizacji"))) > 0) {
                        param.put("statusSynchronizacji", "update");
                        Log.d("Wstawiam status synchro:", "update");
                    }

                }else{
                    param.put("statusSynchronizacji", "give");
                    Log.d("Wstawiam status synchro:", "give");
                }
                Log.d("param: ", String.valueOf(param.values()));
                Log.d("danas.getcount", String.valueOf(danaS.size()));
                return param;
            }
        };

        //Log.d("sendtokenid: ", String.valueOf(SendTokenID));
        //textView.append(String.valueOf(SendTokenID));
        SQLSynchMySingleton.getmInstance(getActivity()).addToRequestQueue(SendTokenID);
    }//Tutajj*/

}