package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class FragmentStawka extends FragmentPodstawowy {

    daneStawka danaKlasy = new daneStawka();
    private int przeniesioneID = 0;
    private TextInputEditText textInputEditTextStawka;
    private TextInputEditText textInputEditTextPoczatek;
    private TextInputEditText textInputEditTextKoniec;
    private TextInputEditText textInputEditTextUwagi;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //wyłapujemy dane przekazane z formatki wywołującej
        Bundle bundleArgumenty = getArguments();
        if (bundleArgumenty != null) {
            przeniesioneID = bundleArgumenty.getInt("id");
        }
        // Inflate the layout for this fragment
        changeTitle("Stawka");
        return inflater.inflate(R.layout.fragment_stawka, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //odpalamy klasę odpowiedzialną za datę:\
        daneData aktualnaData = new daneData();
        ukryjFloatingButton();
        //Odpalamy okienko opis:
        textInputEditTextStawka = (TextInputEditText) view.findViewById(R.id.textInputEditTextStawka);
        textInputEditTextPoczatek = (TextInputEditText) view.findViewById(R.id.textInputEditTextPoczatek);
        textInputEditTextKoniec = (TextInputEditText) view.findViewById(R.id.textInputEditTextKoniec);
        textInputEditTextUwagi = (TextInputEditText) view.findViewById(R.id.textInputEditTextUwagi);
        //sprawdzamy czy mamy zapisane kalendarze w bazie
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerWybierzFirme);
        dodajDoSpinnerWybierzFirme(spinner, R.string.dodaj, R.string.wybierz, 0);
        new PickerTime(getActivity(), R.id.textInputEditTextPoczatek);
        new PickerTime(getActivity(), R.id.textInputEditTextKoniec);
        Button buttonDodaj = (Button) view.findViewById(R.id.buttonDodaj);
        buttonDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zapiszDaneICofnijDoPoprzedniegofragmentu();
            }
        });
        //sprawdzamy czy przekazano id z poprzedniej klasy
        if (przeniesioneID > 0) {
            buttonDodaj.setText("Aktualizuj");
            OSQLdaneStawka osql = new OSQLdaneStawka(getActivity());
            danaKlasy = osql.dajOkreslonyRekord(przeniesioneID);
            Log.d("FragmentStawka:  ", String.valueOf(przeniesioneID));
            //ustawiamy parametry klasy
            textInputEditTextUwagi.setText(String.valueOf(danaKlasy.getUwagi()));
            textInputEditTextStawka.setText(String.valueOf(danaKlasy.getStawka()));//getNazwa()));
            textInputEditTextPoczatek.setText(String.valueOf(danaKlasy.getPoczatek()));//getUlicaNr()));
            textInputEditTextKoniec.setText(String.valueOf(danaKlasy.getKoniec()));//getMiasto()));
            //new TimePicker();
            dodajDoSpinnerWybierzFirme(spinner, R.string.dodaj, R.string.wybierz, danaKlasy.getFirma_id());//getKalendarz_id());
            Log.d("danePojedyncze", danaKlasy.toStringDoRecyclerView());
        }

    }

    //to daje nam możliwość przekazania danych do tego fragmentu
    public static FragmentStawka newInstance(int someInt) {
        FragmentStawka fragmentDemo = new FragmentStawka();
        Bundle args = new Bundle();
        args.putInt("id", someInt);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public void dodajDoSpinnerWybierzFirme(Spinner spinner, final Integer dodaj, final Integer wybierz, Integer wybor) {
        OSQLdaneFirma dA = new OSQLdaneFirma(getActivity());
        Log.d("Spinner", "2)");
        ArrayList<String[]> danaSpinnera;
        danaSpinnera = dA.podajNazwa();
        String[] staleSpinnera = {String.valueOf(0), getString(wybierz), "0"};
        danaSpinnera.add(0, staleSpinnera);
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
                if (!(poszukiwanie.equals(getString(dodaj))) && !(poszukiwanie.equals(getString(wybierz)))){
                    danaKlasy.setFirma_id(Integer.valueOf(danaSpinnera.get(i)[0]));
                    danaKlasy.setFirma_nazwa(danaSpinnera.get(i)[1]);
                }else if (poszukiwanie.equals(getString(dodaj))){
                    NavHostFragment.findNavController(FragmentStawka.this)
                            .navigate(R.id.action_FragmentZadanie_to_FragmentFirma);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                danaKlasy.setFirma_id(-1);
            }
        });
    }//public void dodajDoSpinnerEksploatacjaDodajCzynnosc(Integer rSpinner) {

    private void zapiszDaneICofnijDoPoprzedniegofragmentu(){
        Context context = getActivity();
        StringBuilder uzupelnijDane = new StringBuilder();
        uzupelnijDane.append("Uzupełnij dane: \n");
        //String uzupelnijDane = "Uzupełnij dane: \n";
        int daneDoUzupelnienia = 0;
        if (String.valueOf(textInputEditTextStawka.getText()).isEmpty()){
            danaKlasy.setStawka(Float.valueOf(0));
        }else{
            danaKlasy.setStawka(Float.valueOf(String.valueOf(textInputEditTextStawka.getText())));
        }
        //danaKlasy.setStawka(Float.valueOf(String.valueOf(textInputEditTextStawka.getText())));
        danaKlasy.setPoczatek(String.valueOf(textInputEditTextPoczatek.getText()));
        danaKlasy.setKoniec(String.valueOf(textInputEditTextKoniec.getText()));
        danaKlasy.setUwagi(String.valueOf(textInputEditTextUwagi.getText()));

        danaKlasy.setCzy_widoczny(1);

        if (danaKlasy.getFirma_id() <= 0) {
            daneDoUzupelnienia++;
            Log.d("danedouzu: ", String.valueOf(daneDoUzupelnienia));
            uzupelnijDane.append("-Firma\n");
        }
        if (danaKlasy.getStawka() < 0){
            daneDoUzupelnienia++;
            uzupelnijDane.append("-Stawka\n");
        }
        if (danaKlasy.getPoczatek().equals("")){
            daneDoUzupelnienia++;
            uzupelnijDane.append("-Początek\n");
        }
        if (danaKlasy.getKoniec().equals("")){
            daneDoUzupelnienia++;
            uzupelnijDane.append("-Koniec\n");
        }

        if (daneDoUzupelnienia > 0) {
            Toast.makeText(context, uzupelnijDane, Toast.LENGTH_SHORT).show();
            Log.d("do uzupelnienia", String.valueOf(uzupelnijDane));
        } else{
            zapiszDane();
            cofnijDoPoprzedniegoFragmentu();
        }
    }

    private void zapiszDane(){

        /*danaKlasy.setStawka(Float.valueOf(String.valueOf(textInputEditTextStawka.getText())));
        danaKlasy.setPoczatek(String.valueOf(textInputEditTextPoczatek.getText()));
        danaKlasy.setKoniec(String.valueOf(textInputEditTextKoniec.getText()));
        danaKlasy.setUwagi(String.valueOf(textInputEditTextUwagi.getText()));*/

        Log.d("dana klasy: ", danaKlasy.toString());
        OSQLdaneStawka osql = new OSQLdaneStawka(getActivity());

        daneData dataUtworzenia = new daneData();
        danaKlasy.setData_utworzenia(dataUtworzenia.getAktualnaData());
        if (przeniesioneID > 0) {
            osql.updateDane(osql.contentValues(danaKlasy), osql.getTableName());
        }else{
            osql.dodajDane(osql.contentValues(danaKlasy), osql.getTableName());
        }
        //setPrzebieg(Integer.parseInt(String.valueOf(textInputEditTextPrzebiegTankowania.getText())));
    }//private void zapiszDane(){
}