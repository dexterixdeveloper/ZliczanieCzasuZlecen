package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class FragmentZadaniaDoZrobienia  extends FragmentPodstawowy {
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
        wypelnijRecyclerView();

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
        zlecenia = daneZleceniaSQL.dajWszystkieDoRecyclerViewNZ("zaw");

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
}