package pl.vot.dexterix.zliczanieczasuzlecen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentPodstawowy extends Fragment {
    private static final String CHANNEL_ID = "PowiadomienieZliczanieCzasuZlecen";
    private static final String TAG = FragmentPodstawowy.class.getSimpleName();
    //dla operacji na plikach
    protected static final int WRITE_SEND_REQUEST_CODE = 43;
    private static final int EDIT_REQUEST_CODE = 44;
    protected static final int SEND_REQUEST_CODE = 45;
    //do synchronizacaji
    protected String TOKEN_ID_URL = "https://dexterix.vot.pl/zliczazle/sendtokenid.php";
    protected BroadcastReceiver broadcastReceiver;
    protected String SYNCHRONIZE_URL = "https://dexterix.vot.pl/zliczazle/synchronizemessages.php";
    //String SYNCHRONIZE_URL = "https://dexterix.vot.pl/zliczazle/sendmessage.php";

    protected Uri uriToFile = null;
    //lista Uri do wysłania
    protected ArrayList<Uri> fileListUris = new ArrayList<Uri>();

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == WRITE_SEND_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            //Uri uri = null;
            if (resultData != null) {
                uriToFile = resultData.getData();
                Log.i(TAG, "Uri: " + uriToFile.toString());

            }


        }
    }

    public void setUriToFile(Uri uriToFile) {
        this.uriToFile = uriToFile;
    }

    protected void zmianaFragmentu(Fragment fragmencik, String tagBackStack, Boolean addToBackStack){
        // Begin the transaction
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment

        ft.replace(R.id.fragment_container_main, fragmencik, tagBackStack);
        Log.d("BackToStack", "Bez Add to back");

// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();

    }

    protected void changeTitle(String name){
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name_short) + ": " + name);
        //toolbar.setTitle(getString(R.string.app_name_skrot) + ": " + name);
    }

    protected void zmianaFragmentu(Fragment fragmencik, String tagBackStack){
        // Begin the transaction
        Log.d("Back2", "2");
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

// Replace the contents of the container with the new fragment
        ft.replace(R.id.fragment_container_main, fragmencik, tagBackStack);
        ft.addToBackStack(tagBackStack);

// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();
    }

    protected Fragment getVisibleFragment(){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        Log.d("Ilość fragmentów: ", String.valueOf(fragments.size()));
        if(fragments != null){
            for(Fragment fragment : fragments){
                Log.d("fragment1", fragment.getTag() + " " + fragment.getId());
                if(fragment != null && fragment.isVisible())
                    Log.d("fragment", fragment.toString());
                return fragment;
            }
        }
        return null;
    }

    void dodajFragmentGdyNieMaZadnego(){
        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        String tagBackStack = "FragmentStart";
        Log.d(TAG, "chyba się nie ale moza udało");

        //ft.add(new FragmentZadaniaDoZrobienia(), tagBackStack);
        //ft.addToBackStack(tagBackStack);
        ft.attach(new FragmentZadaniaDoZrobienia());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
    }

    protected void cofnijDoPoprzedniegoFragmentu() {
        //fragment Manager przenosi nas do poprzedniego fragmentu
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Log.d("FragmentPodstawowy fm.getilosc: ", String.valueOf(fm.getBackStackEntryCount()));
        Log.d("Back2", "2");
        //if (fm.getBackStackEntryCount() < 1) dodajFragmentGdyNieMaZadnego();

        //fm.putFragment();
        fm.popBackStack();

    }
    //wysyłanie pliku na zewnątrz: DropBox, Dysk Google, email, itd
    protected void sendRaportsFile(){

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

                        Log.d("Pobieramy token", token[0]);
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.FCM_Pref), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getResources().getString(R.string.FCM_TOKEN), token[0]);
                        editor.commit();
                        Toast.makeText(context, token[0], Toast.LENGTH_SHORT).show();
                        //return token;
                    }
                });

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

    protected void synchronizujFirmy(int RidtextView) {
        /*getActualToken(getActivity());*/
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(OSQLdaneFirma.UI_SYNCHRONIZE_MESSAGE));

        OSQLdaneFirma daneOSQL = new OSQLdaneFirma(getActivity());
        List<daneFirma> daneDoSynchronizacji;
        daneDoSynchronizacji = daneOSQL.dajDoSynchronizacji();
        TextView textView = (TextView) getActivity().findViewById(RidtextView);
        for (daneFirma danaS: daneDoSynchronizacji) {
            StringRequest SendTokenID = new StringRequest(Request.Method.POST, SYNCHRONIZE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    Log.d("Odpowiedź", response);
                    Log.d("Odpowiedź długość: ", String.valueOf(response.length()));
                    JSONObject Jasonobject = null;
                    String statusSynchronizacji = null;
                    try {
                        Jasonobject = new JSONObject(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //JSONArray array = null;

                    /*try {
                        array = new JSONArray(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/



                    /*Statusy synchronizacji:
                           -konflikt
                           -insertsrv
                            -error
                            -zgodne
                            -updateserv
                            -updatekon
                     */

                    try {
                        statusSynchronizacji = Jasonobject.getString("statusSynchronizacji");
                        Log.d("status", statusSynchronizacji);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (statusSynchronizacji != null){
                        daneFirma danaUpdate = new daneFirma();

                        switch (statusSynchronizacji){
                            case "konflikt":
                                //konflikt dancyh, dodajemy nowy rekord z wysłanymi danymi i nadpisujemy to id danymi z serwera
                                daneOSQL.dodajDane(danaS);
                                //daneFirma danaUpdate = new daneFirma();
                                textView.append("id = " + danaS.getId() + " Konflikt: updatujemy rekord i przenosimy nasz na koniec\n");
                                try{
                                    danaUpdate.setData_utworzenia(Jasonobject.getLong("data_utworzenia"));
                                    danaUpdate.setData_synchronizacji(Jasonobject.getLong("data_synchronizacji"));
                                    danaUpdate.setSynchron(1);
                                    danaUpdate.setCzy_widoczny(Jasonobject.getInt("czy_widoczny"));
                                    danaUpdate.setPoprzedni_rekord_powod_usuniecia(Jasonobject.getString("poprzedni_rekord_powod_usuniecia"));
                                    danaUpdate.setPoprzedni_rekord_data_usuniecia(Jasonobject.getString("poprzedni_rekord_data_usuniecia"));
                                    danaUpdate.setPoprzedni_rekord_id(Jasonobject.getInt("poprzedni_rekord_id"));
                                    danaUpdate.setUwagi(Jasonobject.getString("uwagi"));
                                    danaUpdate.setKalendarz_id(Jasonobject.getInt("kalendarz_id"));
                                    danaUpdate.setTyp(Jasonobject.getString("typ"));
                                    danaUpdate.setMiasto(Jasonobject.getString("miasto"));
                                    danaUpdate.setUlica_nr(Jasonobject.getString("ulica_nr"));
                                    danaUpdate.setNr_telefonu(Jasonobject.getInt("nr_telefonu"));
                                    danaUpdate.setNumer(Jasonobject.getString("numer"));
                                    danaUpdate.setNazwa(Jasonobject.getString("nazwa"));
                                    danaUpdate.setId(Jasonobject.getInt("id"));
                                    daneOSQL.updateDane(danaUpdate);
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "insertsrv":
                                textView.append("id = " + danaS.getId() + " Nowy rekord, dodaję na serwerze\n");
                                try{
                                    danaS.setData_utworzenia(Jasonobject.getLong("data_utworzenia"));
                                    danaS.setData_synchronizacji(Jasonobject.getLong("data_synchronizacji"));
                                    danaS.setSynchron(1);

                                    daneOSQL.updateDane(danaS);
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
                                    daneOSQL.updateDane(danaS);
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "updatekon":
                                textView.append("id = " + danaS.getId() + "Nieaktualny rekord, zmieniam w bazie\n");
                                try{
                                    danaUpdate.setData_utworzenia(Jasonobject.getLong("data_utworzenia"));
                                    danaUpdate.setData_synchronizacji(Jasonobject.getLong("data_synchronizacji"));
                                    danaUpdate.setSynchron(1);
                                    danaUpdate.setCzy_widoczny(Jasonobject.getInt("czy_widoczny"));
                                    danaUpdate.setPoprzedni_rekord_powod_usuniecia(Jasonobject.getString("poprzedni_rekord_powod_usuniecia"));
                                    danaUpdate.setPoprzedni_rekord_data_usuniecia(Jasonobject.getString("poprzedni_rekord_data_usuniecia"));
                                    danaUpdate.setPoprzedni_rekord_id(Jasonobject.getInt("poprzedni_rekord_id"));
                                    danaUpdate.setUwagi(Jasonobject.getString("uwagi"));
                                    danaUpdate.setKalendarz_id(Jasonobject.getInt("kalendarz_id"));
                                    danaUpdate.setTyp(Jasonobject.getString("typ"));
                                    danaUpdate.setMiasto(Jasonobject.getString("miasto"));
                                    danaUpdate.setUlica_nr(Jasonobject.getString("ulica_nr"));
                                    danaUpdate.setNr_telefonu(Jasonobject.getInt("nr_telefonu"));
                                    danaUpdate.setNumer(Jasonobject.getString("numer"));
                                    danaUpdate.setNazwa(Jasonobject.getString("nazwa"));
                                    danaUpdate.setId(Jasonobject.getInt("id"));
                                    daneOSQL.updateDane(danaUpdate);
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;

                        }
                    }

                    //textView.append(response);
                    //textView.append("\n");

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
                    Log.d("data utw: ", String.valueOf(danaS.getData_utworzenia()));
                    Log.d("data synchro:  ", String.valueOf(danaS.getData_synchronizacji()));
                    if ((danaS.getData_utworzenia() >= 0) && (danaS.getData_synchronizacji() <= 0)) {
                        param.put("statusSynchronizacji", "add");
                        Log.d("Wstawiam status synchro:", "add");
                    }else{
                        param.put("statusSynchronizacji", "update");
                        Log.d("Wstawiam status synchro:", "update");
                    }
                    param.put("tokenid", getActualTokenId());
                    param.put("tabela", "BZCZBD_Firmy");
                    param.put("_id", String.valueOf(danaS.getId()));
                    param.put("nazwa", danaS.getNazwa());
                    if(danaS.getNumer() != null) {
                        param.put("numer", danaS.getNumer());
                    }else{param.put("numer", "0");}
                    param.put("nr_telefonu", String.valueOf(danaS.getNr_telefonu()));
                    param.put("ulica_nr", danaS.getUlica_nr());
                    param.put("miasto", danaS.getMiasto());
                    param.put("typ", danaS.getTyp());
                    param.put("kalendarz_id", String.valueOf(danaS.getKalendarz_id()));
                    param.put("uwagi", danaS.getUwagi());
                    if(danaS.getPoprzedni_rekord_id() != null) {
                        param.put("poprzedni_rekord_id", String.valueOf(danaS.getPoprzedni_rekord_id()));
                    }else{param.put("poprzedni_rekord_id", "0");}
                    if (danaS.getPoprzedni_rekord_data_usuniecia() != null) {
                        param.put("poprzedni_rekord_data_usuniecia", danaS.getPoprzedni_rekord_data_usuniecia());
                    }else{ param.put("poprzedni_rekord_data_usuniecia", "0");}
                    if(danaS.getPoprzedni_rekord_powod_usuniecia() != null) {
                        param.put("poprzedni_rekord_powod_usuniecia", danaS.getPoprzedni_rekord_powod_usuniecia());
                    }else{param.put("poprzedni_rekord_powod_usuniecia", "");}
                    param.put("czy_widoczny", String.valueOf(danaS.getCzy_widoczny()));
                    param.put("data_utworzenia", String.valueOf(danaS.getData_utworzenia()));
                    param.put("data_synchronizacji", String.valueOf(danaS.getData_synchronizacji()));

                    return param;
                }
            };
            SQLSynchMySingleton.getmInstance(getActivity()).addToRequestQueue(SendTokenID);
        }

        /*broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {

                ReadMessages();

            }
        };*/

        //getActivity().registerReceiver(broadcastReceiver,new IntentFilter(OSQLdaneFirma.UI_SYNCHRONIZE_MESSAGE));
    }

    protected void synchronizujStawki(int RidtextView) {
        /*getActualToken(getActivity());*/
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(OSQLdaneStawka.UI_SYNCHRONIZE_MESSAGE));

        OSQLdaneStawka daneOSQL = new OSQLdaneStawka(getActivity());
        List<daneStawka> daneDoSynchronizacji;
        daneDoSynchronizacji = daneOSQL.dajDoSynchronizacji();
        TextView textView = (TextView) getActivity().findViewById(RidtextView);
        for (daneStawka danaS: daneDoSynchronizacji) {
            StringRequest SendTokenID = new StringRequest(Request.Method.POST, SYNCHRONIZE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    Log.d("Odpowiedź", response);
                    textView.append(response);
                    textView.append("\n");
                    if (response.contains("Successfully")){//.equals("Zsynchronizowano")){
                        danaS.setSynchron(1);
                        daneOSQL.updateDane(danaS);
                        textView.append(danaS.getStawka() + " dodano synchronizację\n");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    Log.d("Bład", error.toString());
                    textView.append(error.toString());
                    textView.append("\n");

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<String, String>();
                    param.put("tokenid", getActualTokenId());
                    param.put("tabela", "BZCZBD_Stawki");
                    param.put("_id", String.valueOf(danaS.getId()));

                    param.put("firma_id", String.valueOf(danaS.getFirma_id()));
                    param.put("stawka", String.valueOf(danaS.getStawka()));
                    param.put("poczatek", danaS.getPoczatek());
                    param.put("koniec", danaS.getKoniec());

                    param.put("uwagi", danaS.getUwagi());
                    if(danaS.getPoprzedni_rekord_id() != null) {
                        param.put("poprzedni_rekord_id", String.valueOf(danaS.getPoprzedni_rekord_id()));
                    }else{param.put("poprzedni_rekord_id", "0");}
                    if (danaS.getPoprzedni_rekord_data_usuniecia() != null) {
                        param.put("poprzedni_rekord_data_usuniecia", danaS.getPoprzedni_rekord_data_usuniecia());
                    }else{ param.put("poprzedni_rekord_data_usuniecia", "0");}
                    if(danaS.getPoprzedni_rekord_powod_usuniecia() != null) {
                        param.put("poprzedni_rekord_powod_usuniecia", danaS.getPoprzedni_rekord_powod_usuniecia());
                    }else{param.put("poprzedni_rekord_powod_usuniecia", "");}
                    param.put("czy_widoczny", String.valueOf(danaS.getCzy_widoczny()));
                    param.put("data_utworzenia", String.valueOf(danaS.getData_utworzenia()));
                    param.put("data_synchronizacji", String.valueOf(danaS.getData_synchronizacji()));
                    return param;
                }
            };
            SQLSynchMySingleton.getmInstance(getActivity()).addToRequestQueue(SendTokenID);
        }

        /*broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {

                ReadMessages();

            }
        };*/

        //getActivity().registerReceiver(broadcastReceiver,new IntentFilter(OSQLdaneFirma.UI_SYNCHRONIZE_MESSAGE));
    }

    protected void synchronizujZlecenia(int RidtextView) {
        /*getActualToken(getActivity());*/
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(OSQLdaneZlecenia.UI_SYNCHRONIZE_MESSAGE));

        OSQLdaneZlecenia daneOSQL = new OSQLdaneZlecenia(getActivity());
        List<daneZlecenia> daneDoSynchronizacji;
        daneDoSynchronizacji = daneOSQL.dajDoSynchronizacji();
        TextView textView = (TextView) getActivity().findViewById(RidtextView);
        for (daneZlecenia danaS: daneDoSynchronizacji) {
            StringRequest SendTokenID = new StringRequest(Request.Method.POST, SYNCHRONIZE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    Log.d("Odpowiedź", response);
                    textView.append(response);
                    textView.append("\n");
                    if (response.contains("Successfully")){//.equals("Zsynchronizowano")){
                        danaS.setSynchron(1);
                        daneOSQL.updateDane(danaS);
                        textView.append(danaS.getOpis() + " dodano synchronizację\n");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    Log.d("Bład", error.toString());
                    textView.append(error.toString());
                    textView.append("\n");
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<String, String>();
                    param.put("tokenid", getActualTokenId());
                    param.put("tabela", "BZCZBD_Zlecenia");
                    param.put("_id", String.valueOf(danaS.getId()));

                    param.put("firma_id", String.valueOf(danaS.getFirma_id()));
                    param.put("czas_rozpoczecia", String.valueOf(danaS.getCzas_zawieszenia()));
                    param.put("opis", danaS.getOpis());
                    param.put("status", danaS.getStatus());
                    if (danaS.getRozliczona() != null) {
                        param.put("rozliczona", danaS.getRozliczona());
                    }else{param.put("rozliczona","");}
                    param.put("czas_zakonczenia", String.valueOf(danaS.getCzas_zakonczenia()));
                    param.put("kalendarz_id", String.valueOf(danaS.getKalendarz_id()));
                    param.put("kalendarz_id_long", String.valueOf(danaS.getKalendarz_id_long()));
                    param.put("kalendarz_zadanie_id", String.valueOf(danaS.getKalendarz_zadanie_id()));
                    param.put("czas_zawieszenia", String.valueOf(danaS.getCzas_zawieszenia()));

                    param.put("uwagi", danaS.getUwagi());
                    if(danaS.getPoprzedni_rekord_id() != null) {
                        param.put("poprzedni_rekord_id", String.valueOf(danaS.getPoprzedni_rekord_id()));
                    }else{param.put("poprzedni_rekord_id", "0");}
                    if (danaS.getPoprzedni_rekord_data_usuniecia() != null) {
                        param.put("poprzedni_rekord_data_usuniecia", danaS.getPoprzedni_rekord_data_usuniecia());
                    }else{ param.put("poprzedni_rekord_data_usuniecia", "0");}
                    if(danaS.getPoprzedni_rekord_powod_usuniecia() != null) {
                        param.put("poprzedni_rekord_powod_usuniecia", danaS.getPoprzedni_rekord_powod_usuniecia());
                    }else{param.put("poprzedni_rekord_powod_usuniecia", "");}
                    param.put("czy_widoczny", String.valueOf(danaS.getCzy_widoczny()));
                    param.put("data_utworzenia", String.valueOf(danaS.getData_utworzenia()));
                    param.put("data_synchronizacji", String.valueOf(danaS.getData_synchronizacji()));
                    return param;
                }
            };
            SQLSynchMySingleton.getmInstance(getActivity()).addToRequestQueue(SendTokenID);
        }

        /*broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {

                ReadMessages();

            }
        };*/

        //getActivity().registerReceiver(broadcastReceiver,new IntentFilter(OSQLdaneFirma.UI_SYNCHRONIZE_MESSAGE));
    }

    //ukrywamy floating button
    protected void ukryjFloatingButton() {
        FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButtonDodaj);
        fab.setVisibility(View.INVISIBLE);
    }

    //wysyłanie plików na dropbox, mail, itd
    protected void sendFiles(){
        //przygotowujemy formatkę do dzielenia
        Intent shareIntent = new Intent();
        //ustawiamy flagę do odczytu plików przez URI
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //wysyłamy kilka
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        //wrzucamy co wysyłamy
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileListUris);
        Log.d("Dotarłem aż do: ", "shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, backupUris);");
        //typ pliku/ów
        shareIntent.setType("text/*");
        //działamy
        startActivityForResult(Intent.createChooser(shareIntent, "Wyśłij do"), SEND_REQUEST_CODE);
    }

    protected void sendRaportsFiles(){
       File privateRootDir;
        // The path to the "backup" subdirectory
        File backupDir;
        // Array of files in the backups subdirectory
        File[] backupFiles;

        //Log.d("getFilesDir(): ", getFilesDir());
        ArrayList<Uri> backupUris = new ArrayList<Uri>();

        //privateRootDir = new File(getActivity().getApplicationInfo().dataDir);//getFilesDir();
        privateRootDir = new File(String.valueOf(getActivity().getExternalFilesDir(null)));//getFilesDir();
        //File raportDir = FileUtils.createDirIfNotExist(getActivity().getExternalFilesDir(null) + "/raport");
        //Log.d("getFilesDir(): ", getFilesDir().toString());
        // Get the root/backup subdirectory;
        backupDir = new File(privateRootDir, "raport");
        Log.d("niby raport: , ", backupDir.toString());
        // Get the files in the backups subdirectory
        backupFiles = backupDir.listFiles();
        //ListView fileListView = (ListView) findViewById(R.id.listviewSelectFiles);
        String[] imageFilenames = new String[backupFiles.length];
        Log.d("ilość plików: ", String.valueOf(backupFiles.length));
        //generujemy URI dla poszczególnych plików
        for (int i = 0; i < imageFilenames.length; ++i) {
            //Log.d("Plik nr:", String.valueOf(i));
            //Log.d("Nazwa pliku: ", backupFiles[i].toString());
            Uri fileUri = null;

            Log.d(getActivity().getPackageName().concat(".").concat("ActivitySettings"), getActivity().getPackageName().concat(".").concat("ActivitySettings"));
            try {
                fileUri = FileProvider.getUriForFile(
                        getActivity(),
                        "pl.vot.dexterix.zliczanieczasuzlecen.fileprovider",
                        backupFiles[i]);//.toURI()));
                //Log.d("czy jestem tu?,", "u?");
                backupUris.add(fileUri);
                //Log.d("czy jestem tu?,", "u?");

            } catch (IllegalArgumentException e) {
                Log.e("File Selector",
                        "The selected file can't be shared: " + backupFiles[i].toString() + "a e to: " + e);
            }


        }
        //przygotowujemy formatkę do dzielenia
        Intent shareIntent = new Intent();
        //ustawiamy flagę do odczytu plików przez URI
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //wysyłamy kilka
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        //wrzucamy co wysyłamy
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, backupUris);
        Log.d("Dotarłem aż do: ", "shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, backupUris);");
        //typ pliku/ów
        shareIntent.setType("text/*");
        //działamy
        startActivity(Intent.createChooser(shareIntent, "Wyśłij do"));
    }

    /*protected void pokazPowiadomienie(String tytul, String opis, String opis2, int notificationId1 ){
        //takie tam powiadominie sobie wrzucamy
        //TODO: wyjaśnić sprawę z powiedomieniami dlaczego 2 albo 3 linie wyświetlają się losowo 2 albo 3
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                //.setSmallIcon(R.drawable.notification_icon)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(tytul)
                .setContentText(opis)
                //.setContentText(opis2)
                .setStyle(new NotificationCompat.BigTextStyle()
                        //.bigText("345"))
                        .bigText(opis2))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //powiadomienie

        //to pokazujemy powiadmomienie
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

        // notificationId is a unique int for each notification that you must define
        int notificationId = notificationId1;
        notificationManager.notify(notificationId, builder.build());
        //to pokazaliśmy
    }*/

    //operacje na plikach
    //tworzenie nowego pliku
    protected void createFile(String mimeType, String fileName, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);

        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, requestCode);

    }
    protected void createFile(String mimeType, String fileName, Context context) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);

        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_SEND_REQUEST_CODE);

    }
    //zapis do pliku: wybieramy plik, który już istnieje
    protected void editDocument() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
        // file browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only text files.
        intent.setType("text/plain");

        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }
    //zapis do pliku, tylko zapis
    protected void alterDocument(Uri uri, String dane, Context context) {
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().
                    openFileDescriptor(uri,"wa");//mode a: dodajemy dane do końca pliku
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            //fileOutputStream.write(("Overwritten by MyCloud at " + System.currentTimeMillis() + "\n").getBytes());
            fileOutputStream.write((dane).getBytes());
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //zapis do pliku, tylko zapis
    protected void alterDocument(Uri uri, String dane) {
        try {
            ParcelFileDescriptor pfd = getActivity().getContentResolver().
                    openFileDescriptor(uri,"wa");//mode a: dodajemy dane do końca pliku
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            //fileOutputStream.write(("Overwritten by MyCloud at " + System.currentTimeMillis() + "\n").getBytes());
            fileOutputStream.write((dane).getBytes());
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //usuwanie plik
    protected void deleteDocument(Uri uri){
        try {
            DocumentsContract.deleteDocument(getActivity().getContentResolver(), uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //czytanie z pliku
    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        inputStream.close();
        //parcelFileDescriptor.close();
        return stringBuilder.toString();
    }
}
