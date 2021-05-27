package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinnerCustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String[]> dane;


    LayoutInflater inflter;

    public SpinnerCustomAdapter(Context applicationContext, ArrayList<String[]> dane) {
        this.context = applicationContext;
        this.dane = dane;

        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return dane.size();//.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_moj_rzeczy, null);
        TextView textViewID = (TextView) view.findViewById(R.id.textViewID);
        TextView textView = (TextView) view.findViewById(R.id.textView);

        textViewID.setText(dane.get(i)[0]);
        textView.setText(dane.get(i)[1]);
        return view;
    }
}
