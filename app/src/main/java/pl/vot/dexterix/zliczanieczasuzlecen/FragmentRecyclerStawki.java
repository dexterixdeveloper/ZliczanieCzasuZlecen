package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FragmentRecyclerStawki extends
        RecyclerView.Adapter<FragmentRecyclerStawki.ViewHolder> {

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
private FragmentRecyclerStawki.OnItemClickListener listener;
public interface OnItemClickListener {
    void onItemClick(View itemView, int position);
}
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(FragmentRecyclerStawki.OnItemClickListener listener) {
        this.listener = listener;
    }

public class ViewHolder extends RecyclerView.ViewHolder {
    // Your holder should contain a member variable
    // for any view that will be set as you render a row
    public TextView textViewFirma;
    public TextView textViewFirmaID;
    public TextView textViewStawkaID;
    public TextView textViewStawkaWielkosc;
    public TextView textViewStawkaOd;
    public TextView textViewStawkaDo;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public ViewHolder(View itemView) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(itemView);

        textViewFirma = (TextView) itemView.findViewById(R.id.textViewFirma);
        textViewFirmaID = (TextView) itemView.findViewById(R.id.textViewFirmaID);
        textViewStawkaID = (TextView) itemView.findViewById(R.id.textViewStawkaID);
        textViewStawkaWielkosc = (TextView) itemView.findViewById(R.id.textViewStawkaWielkosc);
        textViewStawkaOd = (TextView) itemView.findViewById(R.id.textViewStawkaOd);
        textViewStawkaDo = (TextView) itemView.findViewById(R.id.textViewStawkaDo);
        //buttonZadanieZawieszone = (Button) itemView.findViewById(R.id.buttonZadanieZawieszone);

        // Setup the click listener
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Triggers click upwards to the adapter on click
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            }
        });
    }

}
    private List<daneStawka> daneKlasy;

    // Pass in the contact array into the constructor
    public FragmentRecyclerStawki(List<daneStawka> podaneDane) {
        daneKlasy = podaneDane;
    }

    @Override
    public FragmentRecyclerStawki.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.fragment_stawki_pojedyncza_stawka, parent, false);

        // Return a new holder instance
        FragmentRecyclerStawki.ViewHolder viewHolder = new FragmentRecyclerStawki.ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(FragmentRecyclerStawki.ViewHolder holder, int position) {
        // Get the data model based on position
        daneStawka dana = daneKlasy.get(position);

        // Set item views based on your views and data model
        TextView textViewFirma = holder.textViewFirma;
        TextView textViewFirmaID = holder.textViewFirmaID;
        TextView textViewStawkaID = holder.textViewStawkaID;
        TextView textViewStawkaWielkosc = holder.textViewStawkaWielkosc;
        TextView textViewStawkaOd = holder.textViewStawkaOd;
        TextView textViewStawkaDo = holder.textViewStawkaDo;
        textViewFirma.setText(dana.getFirma_nazwa());
        textViewFirmaID.setText(String.valueOf(dana.getFirma_id()));
        textViewStawkaID.setText(String.valueOf(dana.getId()));
        textViewStawkaWielkosc.setText(String.valueOf(dana.getStawka()));
        textViewStawkaOd.setText(dana.getPoczatek());
        textViewStawkaDo.setText(dana.getKoniec());

        //Button button = holder.buttonZadanieZawieszone;
        //button.setText("Odwieś");
        //button.setEnabled(contact.isOnline());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return daneKlasy.size();
    }
}
