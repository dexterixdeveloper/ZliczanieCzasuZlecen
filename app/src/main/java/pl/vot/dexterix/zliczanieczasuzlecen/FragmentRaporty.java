package pl.vot.dexterix.zliczanieczasuzlecen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class FragmentRaporty extends FragmentPodstawowy {
    List<daneZlecenia> zlecenia;
    private TextInputEditText textInputEditTextDataKoncowa;
    private TextInputEditText textInputEditTextDataPoczatkowa;
    private Integer danaKlasy = 0;
    daneData aktualnaData = new daneData();

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_raport, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textInputEditTextDataKoncowa = (TextInputEditText) view.findViewById(R.id.textInputEditTextDataKoncowa);
        textInputEditTextDataPoczatkowa = (TextInputEditText) view.findViewById(R.id.textInputEditTextDataPoczatkowa);
        aktualnaData.podajDate();
        new DateTimePicker(getActivity(), R.id.textInputEditTextDataKoncowa, aktualnaData.getDataMilisekundy());
        new DateTimePicker(getActivity(), R.id.textInputEditTextDataPoczatkowa, aktualnaData.getDataMilisekundy());
        //textInputEditTextUwagi.setText(String.valueOf(przeniesioneID));
        obsluzGuzikWykonajRaport(getView());

        //Powiadomienie
        pokazPowiadomienie();

        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerWybierzFirme);
        dodajDoSpinnerWybierzFirme(spinner, R.string.dodaj, R.string.wybierz, 0);

    }

    public void dodajDoSpinnerWybierzFirme(Spinner spinner, final Integer dodaj, final Integer wybierz, Integer wybor) {
        OSQLdaneFirma dA = new OSQLdaneFirma(getActivity());
        Log.d("Spinner", "2)");
        ArrayList<String[]> danaSpinnera;
        danaSpinnera = dA.podajNazwa();
        //danaSpinnera.add("0 Dodaj");
        //danaSpinnera.add(0, getString(dodaj));
        //dodajemy sobie na poczatku wyraz wybierz
        String[] staleSpinnera = {String.valueOf(0), getString(wybierz), "0"};
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
                if (!(poszukiwanie==getString(dodaj)) && !(poszukiwanie==getString(wybierz))){
                    danaKlasy = (Integer.valueOf(danaSpinnera.get(i)[0]));

                }else if (poszukiwanie==getString(dodaj)){
                    NavHostFragment.findNavController(FragmentRaporty.this)
                            .navigate(R.id.action_FragmentZadanie_to_FragmentFirma);
                    /*Intent intent = new Intent(ActivityZadanie.this, ActivityPrzegladyOkresoweDodajStacjaKontroliSieci.class);
                    startActivityForResult(intent, REQUEST_CODE);*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //danaKlasy.setFirma_id(-1);
            }
        });
    }//public void dodajDoSpinnerEksploatacjaDodajCzynnosc(Integer rSpinner) {

    private void obsluzGuzikWykonajRaport(View view){
        view.findViewById(R.id.buttonWykonajRaport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OSQLdaneZlecenia daneZleceniaSQL = new OSQLdaneZlecenia(getActivity());
                zlecenia = daneZleceniaSQL.dajWszystkieDoRaportu("zak", textInputEditTextDataPoczatkowa.getText(), textInputEditTextDataKoncowa.getText());
                cofnijDoPoprzedniegoFragmentu();
                //zapiszDaneICofnijDoPoprzedniegofragmentu("zak");
            }
        });
    }

}
