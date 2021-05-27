package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_firmy, container, false);
        }

        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            //Fragment z1 = getVisibleFragment();
        /*extends AppCompatActivity {

    List<daneZlecenia> zlecenia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...
        super.onCreate(savedInstanceState);*/
            //setContentView(R.layout.fragment_zadania_recycler);

            // Lookup the recyclerview in activity layout
            RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.recyclerViewFirmy);

            //FragmentRecycler adapter = ...;


            // Initialize contacts
            OSQLdaneFirma daneOSQL = new OSQLdaneFirma(getActivity());
            danaKlasy = daneOSQL.dajWszystkieDoRecyclerView();
            OSQLdaneKalendarzy daneOSQLKalendarzy = new OSQLdaneKalendarzy(getActivity());
            List<daneKalendarza> daneKalendarzy;
            daneKalendarzy = daneOSQLKalendarzy.dajWszystkie();

            for (int i = 0; i < danaKlasy.size(); i++){
                for (int j = 0; j < daneKalendarzy.size(); j++){
                    Long z = (danaKlasy.get(i).getKalendarz_id());
                    if (z.equals(daneKalendarzy.get(j).getCalendar_id())){
                        danaKlasy.get(i).setKalendarz_nazwa(daneKalendarzy.get(j).getCalendarDisplayName());
                    }
                }

            }
            //i tu malutki problem, bo przecież firmy nie mają przypisanych kalendarzy
            List<daneFirma> danaKlasyBezKalendarzy;
            //musimy wyświtlić firmy nawet bez kalendarzy
            danaKlasyBezKalendarzy = daneOSQL.dajWszystkieDoRecyclerViewBezKalendarzy();
            if (danaKlasy.size() < danaKlasyBezKalendarzy.size()){
                //ale poradzimy sobie z tym
                //przeca liczy się od 0, wiec musi byc -1
                for (int i =danaKlasyBezKalendarzy.size() - 1; i > 0; i--){
                    Log.d("i= ", String.valueOf(i));
                    Log.d("size of dananklawsy: ", String.valueOf(danaKlasy.size()));
                    for (int j = 0; j < danaKlasy.size(); j++){
                        Log.d("j= ", String.valueOf(j));
                        if (danaKlasyBezKalendarzy.get(i).getId().equals(danaKlasy.get(j).getId()) ){
                            //danaKlasyBezKalendarzy.get(i).setUwagi("u1u");//oznaczamy do usunięcia
                            danaKlasyBezKalendarzy.remove(i);
                        }
                    }
                }

                danaKlasy.addAll(danaKlasyBezKalendarzy);

            }
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
                    Toast.makeText(getActivity(), name + " was clicked!", Toast.LENGTH_SHORT).show();
                }
            });
            // Attach the adapter to the recyclerview to populate items
            rvContacts.setAdapter(adapter);
            // Set layout manager to position the items
            rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
            // That's all!

            view.findViewById(R.id.buttonDodajFirme).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    zmianaFragmentu(new FragmentFirma(), "FragmentFirma");
                }
            });

        }
    }

