package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.vot.dexterix.zliczanieczasuzlecen.R;

import java.util.List;

public class FragmentZadaniaArchiwalne extends Fragment {

    List<daneZlecenia> zlecenia;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zadania_archiwalne, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*extends AppCompatActivity {

    List<daneZlecenia> zlecenia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...
        super.onCreate(savedInstanceState);*/
        //setContentView(R.layout.fragment_zadania_recycler);

        // Lookup the recyclerview in activity layout
        RecyclerView rvContacts = (RecyclerView) view.findViewById(R.id.recyclerViewZleceniaArchiwalne);

        //FragmentRecycler adapter = ...;


        // Initialize contacts
        OSQLdaneZlecenia daneZleceniaSQL = new OSQLdaneZlecenia(getActivity());
        zlecenia = daneZleceniaSQL.dajWszystkieDoRecyclerView("zak");
        // Create adapter passing in the sample user data
        FragmentRecyclerZadania adapter = new FragmentRecyclerZadania(zlecenia);
        /*adapter.setOnItemClickListener(new FragmentRecycler.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int name = zlecenia.get(position).getId();
                Bundle bundleDane = new Bundle();
                bundleDane.putInt("id", name);
                NavHostFragment.findNavController(FragmentZadaniaArchiwalne.this)
                        .navigate(R.id.action_FragmentZadania_to_FragmentZadanie, bundleDane);
                Toast.makeText(getActivity(), name + " was clicked!", Toast.LENGTH_SHORT).show();
            }
        });*/
        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        // That's all!

        /*iew.findViewById(R.id.buttonDodajZadanie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FragmentZadaniaArchiwalne.this)
                        .navigate(R.id.action_FragmentZadania_to_FragmentZadanie);
            }
        });*/

    }
}
