package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FragmentRecyclerKalendarze  extends
        RecyclerView.Adapter<FragmentRecyclerKalendarze.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    private FragmentRecyclerKalendarze.OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(FragmentRecyclerKalendarze.OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView textViewFirmy;
        public TextView textViewFirmyID;
        public Button buttonZadanieZawieszone;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            textViewFirmy = (TextView) itemView.findViewById(R.id.textViewZadanieZawieszone);
            textViewFirmyID = (TextView) itemView.findViewById(R.id.textViewZadanieZawieszoneID);
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
    private List<daneKalendarza> daneKlasy;

    // Pass in the contact array into the constructor
    public FragmentRecyclerKalendarze(List<daneKalendarza> podaneDane) {
        daneKlasy = podaneDane;
    }

    @Override
    public FragmentRecyclerKalendarze.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.fragment_zadania_pojedyncze_zadanie, parent, false);

        // Return a new holder instance
        FragmentRecyclerKalendarze.ViewHolder viewHolder = new FragmentRecyclerKalendarze.ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(FragmentRecyclerKalendarze.ViewHolder holder, int position) {
        // Get the data model based on position
        daneKalendarza dana = daneKlasy.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.textViewFirmy;
        TextView textViewID = holder.textViewFirmyID;
        textView.setText(dana.toStringDoRecyclerView());
        textViewID.setText(dana.getCalendar_id().toString());
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