package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FragmentFirmy extends FragmentPodstawowy {


        List<daneFirma> danaKlasy;
        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState
        ) {
            changeTitle("Firmy");
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_firmy, container, false);
        }

        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            //Fragment z1 = getVisibleFragment();

            clickOnFloatingButton(new FragmentFirma(), "FragmentFirma");
            wypelnijRecyclerView();
            //synchronizujSQL();

        }

    private void wypelnijRecyclerView(){
            //setContentView(R.layout.fragment_zadania_recycler);

            // Lookup the recyclerview in activity layout
            RecyclerView rvContacts = (RecyclerView) getActivity().findViewById(R.id.recyclerViewFirmy);

            //FragmentRecycler adapter = ...;


            // Initialize contacts
            OSQLdaneFirma daneOSQL = new OSQLdaneFirma(getActivity());
            danaKlasy = daneOSQL.dajWszystkieDoRecyclerView();
            OSQLdaneKalendarzy daneOSQLKalendarzy = new OSQLdaneKalendarzy(getActivity());
            List<daneKalendarza> daneKalendarzy;
            daneKalendarzy = daneOSQLKalendarzy.dajWszystkie();

            for (int i = 0; i < danaKlasy.size(); i++) {
                for (int j = 0; j < daneKalendarzy.size(); j++) {
                    Long z = (danaKlasy.get(i).getKalendarz_id());
                    if (danaKlasy.get(i).getKalendarz_id() > 0) {
                        if (z.equals(daneKalendarzy.get(j).getCalendar_id())) {
                            //Log.d("Kalendarz: ", daneKalendarzy.get(j).getCalendar_id().toString());
                            ///Log.d("Kalendarz: ", daneKalendarzy.get(j).getCalendarDisplayName().toString());
                            danaKlasy.get(i).setKalendarz_nazwa(daneKalendarzy.get(j).getCalendarDisplayName());
                        }
                    }else{
                        danaKlasy.get(i).setKalendarz_nazwa("Brak Kalendarza");
                    }
                }

            }
            //i tu malutki problem, bo przecież firmy nie mają przypisanych kalendarzy
            /*List<daneFirma> danaKlasyBezKalendarzy;
            //musimy wyświtlić firmy nawet bez kalendarzy
            danaKlasyBezKalendarzy = daneOSQL.dajWszystkieDoRecyclerViewBezKalendarzy();
            if (danaKlasy.size() < danaKlasyBezKalendarzy.size()) {
                //ale poradzimy sobie z tym
                //przeca liczy się od 0, wiec musi byc -1
                for (int i = danaKlasyBezKalendarzy.size() - 1; i > 0; i--) {
                    Log.d("i= ", String.valueOf(i));
                    Log.d("size of dananklawsy: ", String.valueOf(danaKlasy.size()));
                    for (int j = 0; j < danaKlasy.size(); j++) {
                        Log.d("j= ", String.valueOf(j));
                        if (danaKlasyBezKalendarzy.get(i).getId().equals(danaKlasy.get(j).getId())) {
                            //danaKlasyBezKalendarzy.get(i).setUwagi("u1u");//oznaczamy do usunięcia
                            danaKlasyBezKalendarzy.remove(i);
                        }
                    }
                }

                danaKlasy.addAll(danaKlasyBezKalendarzy);

            }*/
            // Create adapter passing in the sample user data
            FragmentRecyclerFirmy adapter = new FragmentRecyclerFirmy(danaKlasy);
            adapter.setOnItemClickListener(new FragmentRecyclerFirmy.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    int name = danaKlasy.get(position).getId();
                    //name =;
                    Bundle bundleDane = new Bundle();
                    bundleDane.putInt("id", name);
                    FragmentFirma fragmentDoZamiany = FragmentFirma.newInstance(name);
                    zmianaFragmentu(fragmentDoZamiany, "FragmentFirma");
                    //Toast.makeText(getActivity(), name + " was clicked!", Toast.LENGTH_SHORT).show();
                }
            });
            // Attach the adapter to the recyclerview to populate items
            rvContacts.setAdapter(adapter);
            // Set layout manager to position the items
            rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
            // That's all!
        }

    /*private void clickOnFloatingButton() {
        FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButtonDodaj);
        fab.setVisibility(View.VISIBLE);
           fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fab.setVisibility(View.INVISIBLE);
                    zmianaFragmentu(new FragmentFirma(), "FragmentFirma");
                }
            });
        }*/

    /*private void synchronizujSQL() {
        getActualToken(getActivity());
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(OSQLdaneFirma.UI_SYNCHRONIZE_MESSAGE));
        ReadMessages();

        final String tokenek = getActualTokenId();
        Log.d("token", tokenek);
        Log.d("###############","############");
        StringRequest SendTokenID = new StringRequest(Request.Method.POST, TOKEN_ID_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                synchronizujFirmy(R.id.textViewSynchroFirmy);
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


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {

                ReadMessages();

            }
        };

        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(OSQLdaneFirma.UI_SYNCHRONIZE_MESSAGE));
    }*/

    private void ReadMessages(){
        danaKlasy.clear();
        OSQLdaneFirma firma = new OSQLdaneFirma(getActivity());
        danaKlasy = firma.dajWszystkie();
        wypelnijRecyclerView();
        /*messagesSQliteOpenHelper = new MessagesSQliteOpenHelper(MainActivity.this);
        SQLiteDatabase database = messagesSQliteOpenHelper.getReadableDatabase();
        Cursor cursor = messagesSQliteOpenHelper.ReadMessages(database);
        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(DbStrings.TITLE));
            String messgae= cursor.getString(cursor.getColumnIndex(DbStrings.MESSAGE));
            MessageData messageData = new MessageData(title,messgae);
            arrayList.add(messageData);
        }

        adapter.notifyDataSetChanged();*/
    }

}

