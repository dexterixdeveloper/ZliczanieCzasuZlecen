package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

public class FragmentRaporty extends FragmentPodstawowy{
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        changeTitle("Raporty");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_raporty, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ukryjFloatingButton();
        setActionOnButtonRaportyDoWyslania();
        setActionOnButtonRaportyWyliczeniowe();
    }

    private void setActionOnButtonRaportyWyliczeniowe() {
        Button button = getActivity().findViewById(R.id.buttonRaportyWyliczeniowe);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tagBackStack = "FragmentRaportyWyliczeniowe";
                zmianaFragmentu(new FragmentRaportyWyliczeniowe(), tagBackStack);
            }
        });
    }

    private void setActionOnButtonRaportyDoWyslania() {
        Button button = getActivity().findViewById(R.id.buttonRaportyDoWyslania);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tagBackStack = "FragmentRaportyDoWyslania";
                zmianaFragmentu(new FragmentRaportyDoWyslania(), tagBackStack);
            }
        });

    }
}
