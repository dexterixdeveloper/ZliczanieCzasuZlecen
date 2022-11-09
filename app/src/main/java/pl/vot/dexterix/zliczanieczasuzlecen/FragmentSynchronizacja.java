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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentSynchronizacja extends FragmentPodstawowy {

    //protected String TOKEN_ID_URL = "https://dexterix.vot.pl/zliczazle/sendtokenid.php";
    protected BroadcastReceiver broadcastReceiver;
    //protected String SYNCHRONIZE_URL = "https://dexterix.vot.pl/zliczazle/synchronizemessages.php";
    protected String synchronize_url = "https://";

    public static final String UI_SYNCHRONIZE_MESSAGE = "dexterix.vot.pl.synchronizemysqltosqlitedatabase.UI_SYNCHRONIZE_SQLITE";

    private daneSynchronizacja daneDostepowe = new daneSynchronizacja();
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
    private boolean getCzyKoniec(){
        return this.czyKoniec;
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
        //Log.d("Token", getActualTokenId());
        Log.d("Token", "po daj");
        ukryjFloatingButton();

        setDaneDostepowe();
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

    private void setDaneDostepowe(){
        OSQLdaneSynchronizacja ds = new OSQLdaneSynchronizacja(getActivity());
        daneDostepowe = ds.dajOkreslonyRekord(1);
        TextView textView = (TextView) getActivity().findViewById(R.id.textViewSynchronizacja);
        Log.d("daneSynchronizacja: ", daneDostepowe.toString());
        if (daneDostepowe.getLink().length() > 0){
            addListenerOnButtonSynchronizujWszystko();

        }else{
            textView.append("Musisz wprowadzić parametry synchronizacji:\n Link\n itd...\nNa ten moment synchronizacja niemożliwa");
        }
        synchronize_url = new StringBuilder(synchronize_url).append(daneDostepowe.getLink()).toString();
    }

    protected String getActiveDataBase(){
        //pobieramy bazę z zapisania
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.ActiveDatabase), Context.MODE_PRIVATE);
        String pref = sharedPreferences.getString(getResources().getString(R.string.ActiveDatabase),"1");
        return pref;
    }

    protected void synchronizujDane(int RidtextView){

        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(FragmentSynchronizacja.UI_SYNCHRONIZE_MESSAGE));

        ObslugaSQL daneOSQL = new ObslugaSQL(getActivity());
        TextView textView = (TextView) getActivity().findViewById(RidtextView);

        //Tabele do synchronizacji
        List<String> tablice = daneOSQL.getTabliceDoSynchronizacji();
        if (getAktualnaTabela() < tablice.size() && !getCzyKoniec()) {
            String table = tablice.get(getAktualnaTabela());

            try {
                //for (String table : tablice){
                Log.d("tabela: ", table);
                String zapytanie = new StringBuilder().append("SELECT * FROM ").append(table).append(" WHERE data_synchronizacji IN ('0', '1')").toString();
                String zapytanieU = "UPDATE " + table + " SET data_synchronizacji = '2' WHERE _id = ";
                //cursorSynchronizacja = daneOSQL.rawQuery(zapytanie, null);
                List<Map> dane = daneOSQL.getDaneDoSynchronizacjiMap(table);
                String tabela_nazwa = table;
                textView.append("Synchronizujemy tabelę " + tabela_nazwa + "\n");
                int iloscRekordowDoSynchroZTelefonu = daneOSQL.getIloscRekordowDoSynchronizacji(table);
                textView.append("Rekordów do synchronizacji z telefonu: " + iloscRekordowDoSynchroZTelefonu + "\n");
                if (iloscRekordowDoSynchroZTelefonu > 0) {
                    //cursorSynchronizacja.moveToFirst();
                    int i = 0;
                    int dlugosscNaPoczatku = 0;
                    Map<String, String> dana = new HashMap<String, String>();
                    dana = dane.get(0);
                    dlugosscNaPoczatku = dane.size();
                    Log.d("dane: ", dana.toString());
                    synchronizujDana(dana, tabela_nazwa, RidtextView, true, false, i);
                    Log.d("a dla jaj: ", String.valueOf(dana.get("data_utworzenia")));//.getString(cursorSynchronizacja.getColumnIndex("data_utworzenia"))));
                    dana = null;
                    i++;

                } else {
                    textView.append("Brak danych do wysłania, sprawdzam czy coś do pobrania " + "\n");
                    Log.d("Fragment Synchronizacja: ", "Pobieramy dane");
                    if (!getCzyKoniec()) {
                        Map<String, String> danePobierane = new HashMap<String, String>();
                        synchronizujDana(danePobierane, tabela_nazwa, RidtextView, false, false, 1);
                    }

                    textView.append("Brak danych do pobrania w tabeli " + table + "\n");
                }

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
        StringRequest SendTokenID = new StringRequest(Request.Method.POST, synchronize_url, new Response.Listener<String>() {

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
                                //no nie do końa jednak możemy hurtem, a co z poprzednimi rekordami?
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
                            //setCzyKoniec(true);
                            setAktualnaTabela(getAktualnaTabela() + 1);
                            break;
                        case "test1":
                            textView.append("test danych do pobrania w tabeli " + tabela_nazwa + "\n");
                            break;
                        case "Token Error":
                            textView.append("Problem z autoryzacją, zarejestruj urządzenie\n");
                            //wyslijToken();
                            break;
                        case "connect_error_brak_autoryzacji":
                            textView.append("Problem z połączeniem - brak autoryzacji\n");
                            String statusAutoryzacji = Jasonobject.optString("status_autoryzacji", null);
                            String statusAutoryzacjiS = "";
                            switch (statusAutoryzacji){
                                case "device_add_not_auth":
                                    statusAutoryzacjiS = "Jest to nowe urządzenie, skonfiguruj i aktywuj je przez stronę";
                                    break;
                                case "device_not_auth":
                                    statusAutoryzacjiS = "Urządzenie nie zostało aktywowane przez stronę, zrób to";
                            }
                            Log.d("w kulki leci", "w kulki leci");
                            textView.append(statusAutoryzacjiS);
                            textView.append("\n");
                            setCzyKoniec(true);
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
                //usuwamy token
                //param.put("tokenid", getActualTokenId());
                param.putAll(daneDostepowe.getDanaMap());
                param.put("tabela", tabela_nazwa);
                //dodajemy flagę jaka to baza: 1 - produkcyjna; 0 - testowa
                param.put("flagabazy", getActiveDataBase());
                Log.d("danas.getcount5", String.valueOf(danaS.size()));
                if (czyWysylamy) {
                    Log.d("data utw: ", String.valueOf(danaS.get("data_utworzenia")));
                    Log.d("data synchro:  ", String.valueOf(danaS.get("data_synchronizacji")));
                    int columns = danaS.size();//.getColumnCount();

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
        SQLSynchMySingleton.getmInstance(getActivity()).addToRequestQueue(SendTokenID);
    }//Tutajj*/

}