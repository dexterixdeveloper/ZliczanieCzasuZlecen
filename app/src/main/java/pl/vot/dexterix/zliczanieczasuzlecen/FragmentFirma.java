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

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class FragmentFirma extends FragmentPodstawowy {

    daneFirma danaKlasy = new daneFirma();
    private int przeniesioneID = 0;
    private TextInputEditText textInputEditTextNazwa;
    private TextInputEditText textInputEditTextUlicaNr;
    private TextInputEditText textInputEditTextMiasto;
    private TextInputEditText textInputEditTextUwagi;
    private TextInputEditText textInputEditTextNrTelefonu;

    private TabLayout allTabs;

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
        return inflater.inflate(R.layout.fragment_firma, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //odpalamy klasę odpowiedzialną za datę:\
        daneData aktualnaData = new daneData();
        //Odpalamy okienko opis:
        textInputEditTextNazwa = (TextInputEditText) view.findViewById(R.id.textInputEditTextNazwa);
        textInputEditTextUlicaNr = (TextInputEditText) view.findViewById(R.id.textInputEditTextUlicaNr);
        textInputEditTextMiasto = (TextInputEditText) view.findViewById(R.id.textInputEditTextMiasto);
        textInputEditTextUwagi = (TextInputEditText) view.findViewById(R.id.textInputEditTextUwagi);
        textInputEditTextNrTelefonu = (TextInputEditText) view.findViewById(R.id.textInputEditTextNrTelefonu);
        textInputEditTextNrTelefonu.setText("0");
        //sprawdzamy czy mamy zapisane kalendarze w bazie
        OSQLdaneKalendarzy osqlk = new OSQLdaneKalendarzy(getActivity());
        List<daneKalendarza> kalendarze_w_bazie = osqlk.dajWszystkie();
        ukryjFloatingButton();
        dodajDoSpinnerWybierzKalendarz(R.string.wybierz, 0L);

        //Fragment z1 = getVisibleFragment();
        obsluzGuzikDodaj(R.id.buttonDodaj);

        //sprawdzamy czy przekazano id z poprzedniej klasy
        if (przeniesioneID > 0) {
            //(Button) view.findViewById(R.id.buttonDodaj).setText("Aktualizuj");
            OSQLdaneFirma osql = new OSQLdaneFirma(getActivity());
            danaKlasy = osql.dajOkreslonyRekord(przeniesioneID);
            Log.d("FragmentFirma:  ", String.valueOf(przeniesioneID));
            /*if (danaKlasy instanceof daneFirma){//.getNazwa().isEmpty()){
                //tu podobna sytuacja, nie ma kalendarza wiec sie niew wyswietli
                Log.d("FragmentFirma1:  ", String.valueOf(przeniesioneID));
                danaKlasy = osql.dajOkreslonyRekordBezKalendarza(przeniesioneID);
            }*/
            //ustawiamy parametry klasy
            textInputEditTextUwagi.setText(String.valueOf(danaKlasy.getUwagi()));
            textInputEditTextNazwa.setText(String.valueOf(danaKlasy.getNazwa()));
            textInputEditTextUlicaNr.setText(String.valueOf(danaKlasy.getUlicaNr()));
            textInputEditTextMiasto.setText(String.valueOf(danaKlasy.getMiasto()));
            textInputEditTextNrTelefonu.setText(String.valueOf(danaKlasy.getNr_telefonu()));
            dodajDoSpinnerWybierzKalendarz(R.string.wybierz, danaKlasy.getKalendarz_id());
            Log.d("danePojedyncze", danaKlasy.toStringDoRecyclerView());
        }

    }

    private void obsluzGuzikDodaj(int button) {
        Button buttonDodaj = (Button) getActivity().findViewById(button);
        if (przeniesioneID > 0) {
            buttonDodaj.setText("Aktualizuj");
        }
        buttonDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    zapiszDaneICofnijDoPoprzedniegofragmentu();

            }
        });
    }

    //to daje nam możliwość przekazania danych do tego fragmentu
    public static FragmentFirma newInstance(int someInt) {
        FragmentFirma fragmentDemo = new FragmentFirma();
        Bundle args = new Bundle();
        args.putInt("id", someInt);

        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public void dodajDoSpinnerWybierzKalendarz(final Integer wybierz, long wybor) {
        Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinnerWybierzKalendarz);
        Log.d("Spinner", "2)");
        //pobieramy kalendarze z bazy
        OSQLdaneKalendarzy osql = new OSQLdaneKalendarzy(getActivity());
        ArrayList<String[]> danaSpinnera = osql.podajNazwa();
        String[] staleSpinnera = {String.valueOf(0), getString(wybierz)};
        danaSpinnera.add(0, staleSpinnera);
        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(getActivity(), danaSpinnera);
        spinner.setAdapter(adapter);
        Log.d("FragmentFirma: " + "wybor: ", String.valueOf(wybor));
        if (wybor > 0L ) {
            Log.d("Firma wybor", String.valueOf(wybor));
            for (int i = 1; i < danaSpinnera.size(); i++) {
                Log.d("FragmentFirma: " + "wybor: " + "dana: " , danaSpinnera.get(i)[0]);
                if (danaSpinnera.get(i)[0].equals(String.valueOf(wybor))){
                    Log.d("Firma wybor 2", String.valueOf(i));
                    spinner.setSelection(i);
                }
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String poszukiwanie = String.valueOf(adapterView.getSelectedItem());
                Log.d("poszukiwanie: ", poszukiwanie);
                if (!(poszukiwanie.equals(getString(wybierz)))){

                    danaKlasy.setKalendarz_id(Long.valueOf(danaSpinnera.get(i)[0]));
                    Log.d("ID Kalendarza ", String.valueOf(danaKlasy.getKalendarz_id()));

                    danaKlasy.setKalendarz_nazwa(danaSpinnera.get(i)[1]);
                    Log.d("ID Kalendarza ", danaKlasy.getKalendarz_nazwa());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }//public void dodajDoSpinnerEksploatacjaDodajCzynnosc(Integer rSpinner) {

    private void zapiszDaneICofnijDoPoprzedniegofragmentu(){
        Context context = getActivity();
        StringBuilder uzupelnijDane = new StringBuilder();
        uzupelnijDane.append("Uzupełnij dane: \n");
        int daneDoUzupelnienia = 0;

        danaKlasy.setNazwa(String.valueOf(textInputEditTextNazwa.getText()));
        danaKlasy.setUlicaNr(String.valueOf(textInputEditTextUlicaNr.getText()));
        danaKlasy.setMiasto(String.valueOf(textInputEditTextMiasto.getText()));
        Log.d("FragmentFirma: set Miasto: ", danaKlasy.getMiasto());
        danaKlasy.setUwagi(String.valueOf(textInputEditTextUwagi.getText()));
        danaKlasy.setNr_telefonu(Integer.valueOf(String.valueOf(textInputEditTextNrTelefonu.getText())));
        danaKlasy.setCzy_widoczny(1);
        if (danaKlasy.getNazwa().equals("")){
            daneDoUzupelnienia++;
            uzupelnijDane.append("-Nazwa Firmy\n");
        }
        if (danaKlasy.getKalendarz_id() < 1){
            danaKlasy.setKalendarz_id(0);
            //danaKlasy.setKalendarz_nazwa("Kalendarz nie wybrany");
            //daneDoUzupelnienia++;
            uzupelnijDane.append("-Kalendarz, ale nie musisz;)\n");
        }

        if (daneDoUzupelnienia > 0) {
            Toast.makeText(context, uzupelnijDane, Toast.LENGTH_SHORT).show();
            Log.d("do uzupelnienia", "Nazwa Firmy");
        }else{
            Log.d("Kalendarzid: ", String.valueOf(danaKlasy.getKalendarz_id()));
            Log.d("Kalendarznazwa: ", String.valueOf(danaKlasy.getKalendarz_nazwa()));
            zapiszDane();
            cofnijDoPoprzedniegoFragmentu();
        }
    }

    private void zapiszDane(){

        Log.d("dana klasy: ", danaKlasy.toString());
        OSQLdaneFirma osql = new OSQLdaneFirma(getActivity());

        if (przeniesioneID > 0) {
            daneData dataUtworzenia = new daneData();
            danaKlasy.setData_utworzenia(dataUtworzenia.getAktualnaData());
            //osql.updateDane(osql.contentValues(danaKlasy), osql.getTableName());
            osql.updateDane(danaKlasy.getContentValues1(), osql.getTableName());
        }else{
            long idRekordu = -1;
            daneData dataUtworzenia = new daneData();
            danaKlasy.setData_utworzenia(dataUtworzenia.getAktualnaData());
            //idRekordu = osql.dodajDane(osql.contentValues(danaKlasy), osql.getTableName());
            idRekordu = osql.dodajDane(danaKlasy.getContentValues1(), osql.getTableName());
            //ustawiamy stawkę początkową
            OSQLdaneStawka osqls = new OSQLdaneStawka(getActivity());
            daneStawka stawka = new daneStawka();
            stawka.onCreate();

            stawka.setData_utworzenia(dataUtworzenia.getAktualnaData());
            stawka.setFirma_id(Integer.valueOf((int) idRekordu));
            osqls.dodajDane(osqls.contentValues(stawka), osqls.getTableName());

        }
        //setPrzebieg(Integer.parseInt(String.valueOf(textInputEditTextPrzebiegTankowania.getText())));
    }//private void zapiszDane(){


}