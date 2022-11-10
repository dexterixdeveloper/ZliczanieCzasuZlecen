package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class FragmentZadanieArchiwalne extends FragmentPodstawowy {

    private daneZlecenia danaKlasy = new daneZlecenia();
    private daneZlecenia danaKlasyPrzeniesiona = new daneZlecenia();
    private TextInputEditText textInputEditTextOpis;
    private TextInputEditText textInputEditTextUwagi;
    private TextInputEditText textInputEditTextCzasRozpoczecia;
    private TextInputEditText textInputEditTextCzasZakonczenia;
    private int przeniesioneID = 0;
    daneData aktualnaData = new daneData();

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Bundle bundleArgumenty = getArguments();
        if (bundleArgumenty != null) {
            przeniesioneID = bundleArgumenty.getInt("id");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zadanie_archiwalne, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inicjujemy dane w klasie
        danaKlasy.onCreate();
        ukryjFloatingButton();
            //Odpalamy okienko opis:
        textInputEditTextOpis = (TextInputEditText) view.findViewById(R.id.textInputEditTextOpis);
        textInputEditTextUwagi = (TextInputEditText) view.findViewById(R.id.textInputEditTextUwagi);
        textInputEditTextCzasRozpoczecia = (TextInputEditText) view.findViewById(R.id.textInputEditTextCzasRozpoczecia);
        textInputEditTextCzasZakonczenia = (TextInputEditText) view.findViewById(R.id.textInputEditTextCzasZakonczenia);

        obsluzGuzikZakoncz(getView());

        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerWybierzFirme);
        dodajDoSpinnerWybierzFirme(spinner, R.string.dodaj, R.string.wybierz, 0);

        //sprawdzamy czy przekazano id z poprzedniej klasy
        if (przeniesioneID > 0) {
            OSQLdaneZlecenia osql = new OSQLdaneZlecenia(getActivity());
            danaKlasyPrzeniesiona = osql.dajOkreslonyRekord(przeniesioneID);
            danaKlasy= osql.dajOkreslonyRekord(przeniesioneID);
            //ustawiamy parametry klasy
            textInputEditTextUwagi.setText(String.valueOf(danaKlasyPrzeniesiona.getUwagi()));
            textInputEditTextOpis.setText(String.valueOf(danaKlasyPrzeniesiona.getOpis()));
            textInputEditTextCzasRozpoczecia.setText(String.valueOf(danaKlasyPrzeniesiona.getCzas_rozpoczecia_string()));
            textInputEditTextCzasZakonczenia.setText(String.valueOf(danaKlasyPrzeniesiona.getCzas_zakonczenia_string()));
            new DateTimePicker(getActivity(), R.id.textInputEditTextCzasRozpoczecia, danaKlasyPrzeniesiona.getCzas_rozpoczecia(), false);
            new DateTimePicker(getActivity(), R.id.textInputEditTextCzasZakonczenia, danaKlasyPrzeniesiona.getCzas_zakonczenia(), false);
            Log.d("danePojedyncze", danaKlasyPrzeniesiona.toStringDoRecyclerView());
            Log.d(">0", danaKlasyPrzeniesiona.getCzas_rozpoczecia().toString());
            dodajDoSpinnerWybierzFirme(spinner, R.string.dodaj, R.string.wybierz, danaKlasyPrzeniesiona.getFirma_id());

        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new FragmentPobierzCzas();
        DialogFragment fragmentDoZamiany = new FragmentPobierzCzas();
        zmianaFragmentu(fragmentDoZamiany, "FragmentPobierzCzas");

    }

    //to daje nam możliwość przekazania danych do tego fragmentu
    public static FragmentZadanieArchiwalne newInstance(int someInt) {
        FragmentZadanieArchiwalne fragmentDemo = new FragmentZadanieArchiwalne();
        Bundle args = new Bundle();
        args.putInt("id", someInt);

        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    private void obsluzGuzikZakoncz(View view){
        view.findViewById(R.id.buttonZakoncz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zapiszDane("zak");
                cofnijDoPoprzedniegoFragmentu();
            }
        });
    }

    public void dodajDoSpinnerWybierzFirme(Spinner spinner, final Integer dodaj, final Integer wybierz, Integer wybor) {
        OSQLdaneFirma dA = new OSQLdaneFirma(getActivity());
        Log.d("Spinner", "2)");
        ArrayList<String[]> danaSpinnera;
        danaSpinnera = dA.podajNazwa();
        //dodajemy sobie na poczatku wyraz wybierz
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
                    //OSQLdaneFirma firma = new OSQLdaneFirma(getActivity());
                    danaKlasy.setFirma_nazwa(danaSpinnera.get(i)[1]);
                    danaKlasy.setKalendarz_id_long(Long.valueOf(danaSpinnera.get(i)[2]));
                }else if (poszukiwanie.equals(getString(dodaj))){
                    NavHostFragment.findNavController(FragmentZadanieArchiwalne.this)
                            .navigate(R.id.action_FragmentZadanie_to_FragmentFirma);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }//public void dodajDoSpinnerEksploatacjaDodajCzynnosc(Integer rSpinner) {

    private void addEventToCalendar(long calID, long startMillis, long endMillis, String tytul, long eventID){
        //Tutaj updatujemy zadanie w kalendarzu

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, tytul);
        values.put(CalendarContract.Events.DESCRIPTION, "Zdarzenie wstawione By DeX");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Warsaw");//TODO: to trzeba zmienić za pomocą getAvailableIDs() https://developer.android.com/guide/topics/providers/calendar-provider
        Uri updateUri = null;

        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        int rows = cr.update(updateUri, values, null, null);

    }

    private void zapiszDane(String sStatus){
        danaKlasy.setStatus(sStatus);
        OSQLdaneZlecenia osql = new OSQLdaneZlecenia(getActivity());
        if (przeniesioneID > 0) {
            if (!danaKlasyPrzeniesiona.getStatus().equals("wtle")) {//o ile to nie dane zlecenia w tle
                Log.d("Status,", danaKlasyPrzeniesiona.getStatus());
                Log.d("Wtle", "jka?");
                //zamykamy poprzendie zlecenie
                danaKlasyPrzeniesiona.setStatus("zak");

            }else{
                danaKlasyPrzeniesiona.setStatus("zakwtle");
            }
            danaKlasyPrzeniesiona.setCzy_widoczny(0);
            osql.updateDane(osql.contentValues(danaKlasyPrzeniesiona), osql.getTableName());
        }

        danaKlasy.setCzas_rozpoczecia(aktualnaData.getDateFromString(String.valueOf(textInputEditTextCzasRozpoczecia.getText())));
        danaKlasy.setCzas_zakonczenia(aktualnaData.getDateFromString(String.valueOf(textInputEditTextCzasZakonczenia.getText())));
        danaKlasy.setOpis(String.valueOf(textInputEditTextOpis.getText()));
        danaKlasy.setUwagi(String.valueOf(textInputEditTextUwagi.getText()));
        danaKlasy.setCzy_widoczny(1);
        if (danaKlasy.getCzas_rozpoczecia().equals(0L)) {
            danaKlasy.setCzas_rozpoczecia(danaKlasy.getCzas_zakonczenia());
            danaKlasy.setStatus("dw");
        }
        if (danaKlasy.getStatus().equals("zak")) {
            //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            //OSQLdaneFirma oFirma = new OSQLdaneFirma(getActivity());
            //oFirma.dajOkreslonyRekord(danaKlasy.getFirma_id()).getKalendarz_id();
            //OSQLdaneKalendarzy oKalendarz = new OSQLdaneKalendarzy(getActivity());
            //oKalendarz.dajOkreslonyRekord(oFirma.dajOkreslonyRekord(danaKlasy.getFirma_id()).getKalendarz_id()).getCalendar_id();

            //Long zapisanyKalendarz = oFirma.dajOkreslonyRekord(danaKlasy.getFirma_id()).getKalendarz_id();//= oKalendarz.dajOkreslonyRekord(oFirma.dajOkreslonyRekord(danaKlasy.getFirma_id()).getKalendarz_id()).getCalendar_id();
            //addEventToCalendar(zapisanyKalendarz, danaKlasy.getCzas_rozpoczecia(), danaKlasy.getCzas_zakonczenia(), danaKlasy.toStringDoKalendarza());
            //TODO: dodać kolumny do tablicy zleceń z id wydarzenia z kalendarza, co to za kalendarz, id name ownername, czy może przebudować ustawienia?
            //danaKlasy.setKalendarz_id_long(zapisanyKalendarz);
            //danaKlasy.setKalendarz_zadanie_id(addEventToCalendar(danaKlasy.getKalendarz_id_long(), danaKlasy.getCzas_rozpoczecia(), danaKlasy.getCzas_zakonczenia(), danaKlasy.toStringDoKalendarza(), danaKlasy.getKalendarz_zadanie_id()));
            addEventToCalendar(danaKlasy.getKalendarz_id_long(), danaKlasy.getCzas_rozpoczecia(), danaKlasy.getCzas_zakonczenia(), danaKlasy.toStringDoKalendarza(), danaKlasy.getKalendarz_zadanie_id());
            Log.d("FragmentZadanie id zadania: ", String.valueOf(danaKlasy.getKalendarz_zadanie_id()));
        }
        Log.d("dana klasy: ", danaKlasy.toString());
        osql.updateDane(osql.contentValues(danaKlasy), osql.getTableName());

    }//private void zapiszDane(){
}
