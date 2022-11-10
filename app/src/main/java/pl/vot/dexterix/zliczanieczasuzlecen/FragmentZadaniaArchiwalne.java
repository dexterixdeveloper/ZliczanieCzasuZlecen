package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FragmentZadaniaArchiwalne extends FragmentPodstawowy {
    private Long dataPoczatkowa = 0L;
    private Long dataKoncowa = 0L;
    private int firmaId = 0;
    List<daneZlecenia> zlecenia;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        changeTitle("Archiwalne");
        return inflater.inflate(R.layout.fragment_zadania_archiwalne, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ukryjFloatingButton();
        dodajSpinnerOkresCzasu(R.id.spinnerWybierzPrzedzialCzasu, 0);
        dodajSpinnerWybierzFirme(R.id.spinner2WybierzFirme, 0);
        //dodajRecyclerView();

    }

    private void dodajRecyclerView(){
        // Lookup the recyclerview in activity layout
        RecyclerView rvContacts = (RecyclerView) getActivity().findViewById(R.id.recyclerViewZleceniaArchiwalne);

        // Initialize contacts
        OSQLdaneZlecenia daneZleceniaSQL = new OSQLdaneZlecenia(getActivity());
        zlecenia = daneZleceniaSQL.dajWszystkieDoRecyclerView("zak", dataPoczatkowa, dataKoncowa, firmaId);
        // Create adapter passing in the sample user data
        FragmentRecyclerZlecenia adapter = new FragmentRecyclerZlecenia(getContext(), zlecenia);

        adapter.setOnItemClickListener(new FragmentRecyclerZlecenia.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int name = zlecenia.get(position).getId();
                Bundle bundleDane = new Bundle();
                bundleDane.putInt("id", name);
                FragmentZadanieArchiwalne fragmentDoZamiany = FragmentZadanieArchiwalne.newInstance(name);
                zmianaFragmentu(fragmentDoZamiany, "FragmentZadanie");
                Toast.makeText(getActivity(), name + " was clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        // That's all!
    }

    private void dodajSpinnerOkresCzasu(int rIdSpinner, Integer wybor) {
        Spinner spinner = (Spinner) getActivity().findViewById(rIdSpinner);
        //dodajemy dane do spinnera
        ArrayList<String[]> danaSpinnera = new ArrayList<>();
        String[] wiersz1 = {String.valueOf(1), "Aktualny miesiąc"};
        danaSpinnera.add(wiersz1);
        String[] wiersz2 = {String.valueOf(2), "Dziś"};
        danaSpinnera.add(wiersz2);
        String[] wiersz3 = {String.valueOf(3), "Aktualny rok"};
        danaSpinnera.add(wiersz3);
        String[] wiersz4 = {String.valueOf(4), "Wszystko"};
        danaSpinnera.add(wiersz4);

        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(getActivity(), danaSpinnera);
        spinner.setAdapter(adapter);

        //sprawdzamy czy nie mamy narzuconego wyboru z góry
        if (wybor > 0 ) {
            spinner.setSelection(wybor);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int wybrany = Integer.valueOf(danaSpinnera.get(i)[0]);
                //ustawiamy sobie z czapy date
                daneData dataAktualna = new daneData();
                dataAktualna.podajDate();
                //więc co wybraliśmy
                switch(wybrany) {
                    case 1://Aktualny miesiąc
                        dataPoczatkowa = dataAktualna.getPoczatekMiesiaca();
                        dataKoncowa = dataAktualna.getKoniecMiesiaca();
                        break;
                    case 2://Dziś
                        dataPoczatkowa = dataAktualna.getPoczatekDnia();
                        dataKoncowa = dataAktualna.getKoniecDnia();
                        break;
                    case 3://Aktualny rok
                        dataPoczatkowa = dataAktualna.getPoczatekRoku();
                        dataKoncowa = dataAktualna.getKonieckRoku();
                        break;
                    case 4://Wszystko
                        dataPoczatkowa = 0L;
                        dataKoncowa = 0L;
                        break;
                    default:

                        break;

                        //wykonajWyslijRaport(poczatek, koniec);
                }
                dodajRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void dodajSpinnerWybierzFirme(int rIdSpinner, Integer wybor) {
        Spinner spinner = (Spinner) getActivity().findViewById(rIdSpinner);
        //dodajemy dane do spinnera
        OSQLdaneFirma dA = new OSQLdaneFirma(getActivity());
        Log.d("Spinner", "2)");
        ArrayList<String[]> danaSpinnera;
        danaSpinnera = dA.podajNazwa();
        //dodajemy sobie na poczatku wyraz wybierz
        String[] staleSpinnera = {String.valueOf(0), "Wszystkie", "0"};
        danaSpinnera.add(0, staleSpinnera);
        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(getActivity(), danaSpinnera);
        spinner.setAdapter(adapter);
        if (wybor > 0 ) {
            spinner.setSelection(wybor);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String poszukiwanie = String.valueOf(adapterView.getSelectedItem());
                Log.d("poszukiwanie: ", poszukiwanie);
                if (!(poszukiwanie.equals("Wszystkie")) ){
                    firmaId = Integer.valueOf(danaSpinnera.get(i)[0]);
                    //OSQLdaneFirma firma = new OSQLdaneFirma(getActivity());
                }else{
                    firmaId = 0;
                }
                dodajRecyclerView();
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
