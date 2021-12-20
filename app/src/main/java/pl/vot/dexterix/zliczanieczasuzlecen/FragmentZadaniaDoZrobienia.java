package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FragmentZadaniaDoZrobienia  extends FragmentPodstawowy {
    private Long dataPoczatkowa = 0L;
    private Long dataKoncowa = 0L;
    private int firmaId = 0;

    List<daneZlecenia> zlecenia;// = new LinkedList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Bundle bundleArgumenty = getArguments();
        if (bundleArgumenty != null) {
            if (!(bundleArgumenty.isEmpty())) {
                Log.d("BundleArgumenty", "nienull");
                String fragmanetDoZmiany = bundleArgumenty.getString("FragmentDoZmiany");
                int przeniesioneID = bundleArgumenty.getInt("id");
                Log.d("Bundle ARg przzed clera size: ", String.valueOf(bundleArgumenty.size()));
                if (bundleArgumenty.isEmpty()) {
                    Log.d("Is ", "empty");
                } else {
                    Log.d("Is ", "full");
                }
                bundleArgumenty.clear();
                Log.d("Bundle ARg po clera size: ", String.valueOf(bundleArgumenty.size()));
                if (bundleArgumenty.isEmpty()) {
                    Log.d("Is ", "empty");
                } else {
                    Log.d("Is ", "full");
                }
                if (fragmanetDoZmiany.equals("FragmentZadanie")) {
                    Bundle bundleDane = new Bundle();
                    bundleDane.putInt("id", przeniesioneID);
                    FragmentZadanie fragmentDoZamiany = FragmentZadanie.newInstance(przeniesioneID);
                    zmianaFragmentu(fragmentDoZamiany, "FragmentZadanie");
                }
            }
        }

        changeTitle("Do zrobienia");
        //robimy sobie mały test
        /*int name = 18;

        Bundle bundleDane = new Bundle();
        bundleDane.putInt("id", name);
        FragmentZadanie fragmentDoZamiany = FragmentZadanie.newInstance(name);
        zmianaFragmentu(fragmentDoZamiany, "FragmentZadanie");
        Toast.makeText(getActivity(), name + " was clicked!", Toast.LENGTH_SHORT).show();*/
        //robimy sobie mały test-END

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zadania_do_zrobienia, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clickOnFloatingButton();
        dodajSpinnerOkresCzasu(R.id.spinnerWybierzPrzedzialCzasu, 0);
        dodajSpinnerWybierzFirme(R.id.spinner2WybierzFirme, 0);
        //wypelnijRecyclerView();

    }

    //to daje nam możliwość przekazania danych do tego fragmentu
    public static FragmentZadaniaDoZrobienia newInstance(int someInt, String fragmentDoZmiany) {
        FragmentZadaniaDoZrobienia fragmentDemo = new FragmentZadaniaDoZrobienia();
        Bundle args = new Bundle();
        args.putInt("id", someInt);
        args.putString("FragmentDoZmiany", fragmentDoZmiany);

        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    private void wypelnijRecyclerView(){
        //setContentView(R.layout.fragment_zadania_recycler);

        // Lookup the recyclerview in activity layout
        RecyclerView recyclerViewZlecenia = (RecyclerView) getActivity().findViewById(R.id.recyclerViewZlecenia);

        //FragmentRecycler adapter = ...;

       
        // Initialize contacts
        OSQLdaneZlecenia daneZleceniaSQL = new OSQLdaneZlecenia(getActivity());
        zlecenia = daneZleceniaSQL.dajWszystkieDoRecyclerViewNZ("zaw", dataPoczatkowa, dataKoncowa, firmaId);

        // Create adapter passing in the sample user data
        FragmentRecyclerZlecenia adapter = new FragmentRecyclerZlecenia(getContext(), zlecenia);

        adapter.setOnItemClickListener(new FragmentRecyclerZlecenia.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                int name = zlecenia.get(position).getId();
                Bundle bundleDane = new Bundle();
                bundleDane.putInt("id", name);
                Log.d("id", String.valueOf(name));
                FragmentZadanie fragmentDoZamiany = FragmentZadanie.newInstance(name);
                zmianaFragmentu(fragmentDoZamiany, "FragmentZadanie");
                Toast.makeText(getActivity(), name + " was clicked!", Toast.LENGTH_SHORT).show();
            }
        });



        // Attach the adapter to the recyclerview to populate items
        recyclerViewZlecenia.setAdapter(adapter);
        recyclerViewZlecenia.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                //tutaj sprawdzamy czy istnieje czy nie i pokazujemy i znikamy guzik do masowej anulacji
                if (adapter.getSelected().size() > 0){
                    //Log.d("ItemTouchDziala123", String.valueOf(adapter.getSelected().size()));
                    Button buttonAnulujZlecenia = getActivity().findViewById(R.id.buttonAnulujZlecenia);
                    //Log.d("Visible", String.valueOf(adapter.getSelected().size()));
                    buttonAnulujZlecenia.setVisibility(View.VISIBLE);
                    buttonAnulujZlecenia.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (adapter.getSelected().size() > 0) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("\n");

                                //for (int i = 0; i < adapter.getSelected().size(); i++) {
                                //to jest for each
                                for (daneZlecenia danaSelected : adapter.getSelected()){
                                    //Log.d("Wartosc i", String.valueOf(i));
                                    Log.d("Eartść size ()", String.valueOf(adapter.getSelected().size()));
                                    stringBuilder.append(danaSelected.getOpis());
                                    stringBuilder.append("\n");

                                    danaSelected.setStatus("anuluj");
                                    danaSelected.setCzy_widoczny(0);
                                    //to może na razie bez zmiany danych
                                    //Log.d("Zlecenia1: ", adapter.getSelected().get(i).toString());
                                    //OSQLdaneZlecenia daneZleceniaSQL = new OSQLdaneZlecenia(getActivity());
                                    daneZleceniaSQL.updateDane(danaSelected);
                                    //Log.d("albo albo ", String.valueOf(daneZleceniaSQL.dajOkreslonyRekord(adapter.getSelected().get(i).getId())));
                                    //adapter.getSelected().remove(i);
                                    //usuwamy z listy jako obiekt
                                    zlecenia.remove(danaSelected);
                                    /*for (daneZlecenia dane : zlecenia){
                                        if(dane.getId() == danaSelected.getId()){
                                            zlecenia.remove(dane);
                                        }
                                    }*/
                                    /*for (int j = 0; j < zlecenia.size(); j++){
                                        Log.d("Zlecenia GetID przed if", String.valueOf(zlecenia.get(j).getId()));
                                        if (adapter.getSelected().get(i).getId() == zlecenia.get(j).getId()){
                                            Log.d("Adapter get id", String.valueOf(adapter.getSelected().get(i).getId()));
                                            Log.d("Zlecenia GetID", String.valueOf(zlecenia.get(j).getId()));
                                            //adapter.getSelected().get(i).setChecked(false);
                                            //adapter.getSelected().remove(i);

                                            zlecenia.remove(j);
                                            Log.d("Usunieto", String.valueOf(j));
                                            //przerywamy pętlę bo już i tak nie znajdziemy kolejnego id zlecenia takiego samego
                                            break;
                                        }
                                    }*/

                                }

                                //no niestety musi być 2 fofr, bo inaczej nic mi nie przychodzi do głowy, żeby lista się wyczyściła
                                //zróbmy od dupy strony: znajdzmy te ze statusem anuluj
                                    /*for (int j = 0; j < zlecenia.size(); j++) {
                                        Log.d("Zlecenia GetID przed if", String.valueOf(zlecenia.get(j).getId()));
                                        if (zlecenia.get(j).getStatus().equals("anuluj")) {
                                            //Log.d("Adapter get id", String.valueOf(adapter.getSelected().get(i).getId()));
                                            Log.d("Zlecenia GetID", String.valueOf(zlecenia.get(j).getId()));
                                            //adapter.getSelected().get(i).setChecked(false);
                                            //adapter.getSelected().remove(i);
                                            //adapter.notifyItemRemoved(j);
                                            zlecenia.remove(j);
                                            Log.d("Usunieto", String.valueOf(j));
                                            //przerywamy pętlę bo już i tak nie znajdziemy kolejnego id zlecenia takiego samego
                                            break;
                                        }
                                    }*/


                                adapter.notifyDataSetChanged();
                                //Droga jest zamknięta
                                //Zbudowali ją Ci którzy są umarli
                                //I umarli będą jej jedynymi panami...
                                //musimy ukryć guzik, bo z jakiegoś powodu pomimo wyczyszczenia listy guzik jest widoczny
                                buttonAnulujZlecenia.setVisibility(View.GONE);
                                Log.d("Zaznaczenie", stringBuilder.toString().trim());
                            } else {
                                Log.d("Zaznaczenie", "No Selection");
                            }
                        }
                    });
                }else{
                    Button buttonAnulujZlecenia = getActivity().findViewById(R.id.buttonAnulujZlecenia);
                    //Log.d("Visible", String.valueOf(adapter.getSelected().size()));
                    buttonAnulujZlecenia.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        // Set layout manager to position the items
        recyclerViewZlecenia.setLayoutManager(new LinearLayoutManager(getActivity()));

        // That's all!
        //pokazujemy powiadomienie
        //pokazPowiadomienie("Zadań zawieszonych: " + zlecenia.size(), "", "", 0, getContext());
    }

    /*private void clickOnButtonAnulujZlecenia(){
        Button buttonAnulujZlecenia = getActivity().findViewById(R.id.buttonAnulujZlecenia);
        buttonAnulujZlecenia.setOnClickListener(new View.OnClickListener() {
            if (adapter.getSelected().size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < adapter.getSelected().size(); i++) {
                    stringBuilder.append(adapter.getSelected().get(i).getName());
                    stringBuilder.append("\n");
                }
                Log.d("Zaznaczenie", stringBuilder.toString().trim());
            } else {
                Log.d("Zaznaczenie","No Selection");
            }
        });
    }*/

    private void clickOnFloatingButton() {
        FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButtonDodaj);
        fab.setVisibility(View.VISIBLE);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fab.setVisibility(View.INVISIBLE);
                        zmianaFragmentu(new FragmentZadanie(), "FragmentZadanie");
                    }
                });
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
                wypelnijRecyclerView();
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
        //danaSpinnera.add("0 Dodaj");
        //danaSpinnera.add(0, getString(dodaj));
        //dodajemy sobie na poczatku wyraz wybierz
        String[] staleSpinnera = {String.valueOf(0), "Wszystkie", "0"};
        danaSpinnera.add(0, staleSpinnera);
        //Spinner spinner = (Spinner) findViewById(rSpinner);
        //RFWSpinner.przygotujSpinner(danaSpinnera,this,spinner);
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
                wypelnijRecyclerView();
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}