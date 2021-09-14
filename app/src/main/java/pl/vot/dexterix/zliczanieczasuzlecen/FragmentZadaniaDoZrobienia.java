package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class FragmentZadaniaDoZrobienia  extends FragmentPodstawowy {
    List<daneZlecenia> zlecenia;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zadania_do_zrobienia, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clickOnFloatingButton();
        wypelnijRecyclerView();

    }

    private void wypelnijRecyclerView(){
        //setContentView(R.layout.fragment_zadania_recycler);

        // Lookup the recyclerview in activity layout
        RecyclerView rvContacts = (RecyclerView) getActivity().findViewById(R.id.recyclerViewZlecenia);

        //FragmentRecycler adapter = ...;


        // Initialize contacts
        OSQLdaneZlecenia daneZleceniaSQL = new OSQLdaneZlecenia(getActivity());
        zlecenia = daneZleceniaSQL.dajWszystkieDoRecyclerViewNZ("zaw");
        // Create adapter passing in the sample user data
        FragmentRecyclerZadania adapter = new FragmentRecyclerZadania(zlecenia);
        adapter.setOnItemClickListener(new FragmentRecyclerZadania.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int name = zlecenia.get(position).getId();
                Bundle bundleDane = new Bundle();
                bundleDane.putInt("id", name);
                FragmentZadanie fragmentDoZamiany = FragmentZadanie.newInstance(name);
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
