package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import pl.vot.dexterix.zliczanieczasuzlecen.R;

import java.util.List;

public class FragmentRecyclerZadania extends
        RecyclerView.Adapter<FragmentRecyclerZadania.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView textViewZadanieZawieszone;
        public TextView textViewZadanieZawieszoneID;
        public Button buttonZadanieZawieszone;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textViewZadanieZawieszone = (TextView) itemView.findViewById(R.id.textViewZadanieZawieszone);
            textViewZadanieZawieszoneID = (TextView) itemView.findViewById(R.id.textViewZadanieZawieszoneID);
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
    private List<daneZlecenia> zleceniaZawieszone;

    // Pass in the contact array into the constructor
    public FragmentRecyclerZadania(List<daneZlecenia> zlecenia) {
        zleceniaZawieszone = zlecenia;
    }

    @Override
    public FragmentRecyclerZadania.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.fragment_zadania_pojedyncze_zadanie, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(FragmentRecyclerZadania.ViewHolder holder, int position) {
        // Get the data model based on position
        daneZlecenia zlecenie = zleceniaZawieszone.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.textViewZadanieZawieszone;
        TextView textViewID = holder.textViewZadanieZawieszoneID;
        textView.setText(zlecenie.toStringDoRecyclerView());
        textViewID.setText(zlecenie.getId().toString());
        //Button button = holder.buttonZadanieZawieszone;
        //button.setText("Odwieś");
        //button.setEnabled(contact.isOnline());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return zleceniaZawieszone.size();
    }
}