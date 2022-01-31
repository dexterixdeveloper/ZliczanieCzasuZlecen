package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentRaportyWyliczeniowe extends FragmentRaportyDoWyslania{


    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        changeTitle("Raporty podsumowujące");
        // Inflate the layout for this fragment
        czyWyslijRaport = false;
        return inflater.inflate(R.layout.fragment_raporty_do_wyslania, container, false);

    }

}
