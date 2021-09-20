package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class FragmentZadanie extends FragmentPodstawowy {

    private daneZlecenia danaKlasy = new daneZlecenia();
    private daneZlecenia danaKlasyPrzeniesiona = new daneZlecenia();
    private TextInputEditText textInputEditTextOpis;
    private TextInputEditText textInputEditTextUwagi;
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
        return inflater.inflate(R.layout.fragment_zadanie, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //odpalamy klasę odpowiedzialną za datę:\

        //inicjujemy dane w klasie
        danaKlasy.onCreate();

            //Odpalamy okienko opis:
        textInputEditTextOpis = (TextInputEditText) view.findViewById(R.id.textInputEditTextOpis);
        textInputEditTextUwagi = (TextInputEditText) view.findViewById(R.id.textInputEditTextUwagi);
        //textInputEditTextUwagi.setText(String.valueOf(przeniesioneID));

        obsluzGuzikZawies(getView());
        obsluzGuzikWTlo(getView());
        obsluzGuzikZakoncz(getView());
        obsluzGuzikZacznij(getView());
        //Powiadomienie
        //pokazPowiadomienie();

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
            Log.d("danePojedyncze", danaKlasyPrzeniesiona.toStringDoRecyclerView());
            Log.d(">0", danaKlasyPrzeniesiona.getCzas_rozpoczecia().toString());
            dodajDoSpinnerWybierzFirme(spinner, R.string.dodaj, R.string.wybierz, danaKlasyPrzeniesiona.getFirma_id());
            if (!danaKlasyPrzeniesiona.getStatus().equals("wtle")) {//o ile to nie dane zlecenia w tle
                //ustawiamy początek zlecenia
                aktualnaData.podajDate();
                danaKlasy.setCzas_rozpoczecia(aktualnaData.getDataMilisekundy());
            }
        }
    }

    //to daje nam możliwość przekazania danych do tego fragmentu
    public static FragmentZadanie newInstance(int someInt) {
        FragmentZadanie fragmentDemo = new FragmentZadanie();
        Bundle args = new Bundle();
        args.putInt("id", someInt);

        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    private void obsluzGuzikZacznij(View view){
        view.findViewById(R.id.buttonZacznij).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aktualnaData.podajDate();
                danaKlasy.setCzas_rozpoczecia(aktualnaData.getDataMilisekundy());

            }
        });
    }

    private void obsluzGuzikZakoncz(View view){
        view.findViewById(R.id.buttonZakoncz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aktualnaData.podajDate();
                danaKlasy.setCzas_zakonczenia(aktualnaData.getDataMilisekundy());

                //odczytujemy kalendarz

                //zapiszDane("zak");
                //cofnijDoPoprzedniegoFragmentu();
                zapiszDaneICofnijDoPoprzedniegofragmentu("zak");
            }
        });
    }

    private void obsluzGuzikZawies(View view){
        view.findViewById(R.id.buttonZawies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aktualnaData.podajDate();
                danaKlasy.setCzas_zakonczenia(aktualnaData.getDataMilisekundy());
                //danaKlasy.setCzy_widoczny(1);
                //zapiszDane("zak");
                //zapiszDane("zaw");
                //cofnijDoPoprzedniegoFragmentu();
                zapiszDaneICofnijDoPoprzedniegofragmentu("zaw");

            }
        });
    }

    private void obsluzGuzikWTlo(View view){
        view.findViewById(R.id.buttonWTlo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (danaKlasy.getCzas_rozpoczecia().equals(0L)){
                    aktualnaData.podajDate();
                    danaKlasy.setCzas_rozpoczecia(aktualnaData.getDataMilisekundy());
                }
                //aktualnaData.podajDate();
                //danaKlasy.setCzas_zakonczenia(aktualnaData.getDataMilisekundy());
                //danaKlasy.setCzy_widoczny(1);
                zapiszDaneICofnijDoPoprzedniegofragmentu("wtle");
                //zapiszDane("wtle");
                //cofnijDoPoprzedniegoFragmentu();
            }
        });
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
                    danaKlasy.setFirma_id(Integer.valueOf(danaSpinnera.get(i)[0]));
                    //OSQLdaneFirma firma = new OSQLdaneFirma(getActivity());
                    danaKlasy.setFirma_nazwa(danaSpinnera.get(i)[1]);
                    danaKlasy.setKalendarz_id_long(Long.valueOf(danaSpinnera.get(i)[2]));
                }else if (poszukiwanie==getString(dodaj)){
                    NavHostFragment.findNavController(FragmentZadanie.this)
                            .navigate(R.id.action_FragmentZadanie_to_FragmentFirma);
                    /*Intent intent = new Intent(ActivityZadanie.this, ActivityPrzegladyOkresoweDodajStacjaKontroliSieci.class);
                    startActivityForResult(intent, REQUEST_CODE);*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                danaKlasy.setFirma_id(-1);
            }
        });
    }//public void dodajDoSpinnerEksploatacjaDodajCzynnosc(Integer rSpinner) {

    private long addEventToCalendar(long calID, long startMillis, long endMillis, String tytul){
        //Dodawanie zadań do kalendarza

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, tytul);
        values.put(CalendarContract.Events.DESCRIPTION, "Zdarzenie wstawione By DeX");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Warsaw");//TODO: to trzeba zmienić za pomocą getAvailableIDs() https://developer.android.com/guide/topics/providers/calendar-provider
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
         long eventID = Long.parseLong(uri.getLastPathSegment());
        //
        // ... do something with event ID
        //
        //
        return eventID;
    }

    private void zapiszDaneICofnijDoPoprzedniegofragmentu(String sStatus){
        Context context = getActivity();
        if (danaKlasy.getFirma_id() > 0) {
            zapiszDane(sStatus);
            cofnijDoPoprzedniegoFragmentu();
        } else{
                Toast.makeText(context, "Nie wybrałes firmy", Toast.LENGTH_SHORT).show();
            }
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
                osql.updateDane(danaKlasyPrzeniesiona);
            }
            danaKlasy.setOpis(String.valueOf(textInputEditTextOpis.getText()));
            danaKlasy.setUwagi(String.valueOf(textInputEditTextUwagi.getText()));
            danaKlasy.setCzy_widoczny(1);
            if (danaKlasy.getCzas_rozpoczecia().equals(0L)) {
                danaKlasy.setCzas_rozpoczecia(danaKlasy.getCzas_zakonczenia());
                danaKlasy.setStatus("dw");
            }
            if (sStatus.equals("zaw")){
                if(danaKlasy.getCzas_zakonczenia()>danaKlasy.getCzas_rozpoczecia()){
                    danaKlasy.setStatus("zak");
                    zapiszDanaZakonczone();
                    osql.dodajDane(danaKlasy);
                    danaKlasy.setStatus("zaw");
                }
            }
            if (danaKlasy.getStatus().equals("zak")) {
                zapiszDanaZakonczone();
            }
            Log.d("dana klasy: ", danaKlasy.toString());

            osql.dodajDane(danaKlasy);
            //setPrzebieg(Integer.parseInt(String.valueOf(textInputEditTextPrzebiegTankowania.getText())));
        }//private void zapiszDane(){

    private void zapiszDanaZakonczone(){
        //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        //OSQLdaneFirma oFirma = new OSQLdaneFirma(getActivity());
        //oFirma.dajOkreslonyRekord(danaKlasy.getFirma_id()).getKalendarz_id();
        //OSQLdaneKalendarzy oKalendarz = new OSQLdaneKalendarzy(getActivity());
        //oKalendarz.dajOkreslonyRekord(oFirma.dajOkreslonyRekord(danaKlasy.getFirma_id()).getKalendarz_id()).getCalendar_id();

        //Long zapisanyKalendarz = oFirma.dajOkreslonyRekord(danaKlasy.getFirma_id()).getKalendarz_id();//= oKalendarz.dajOkreslonyRekord(oFirma.dajOkreslonyRekord(danaKlasy.getFirma_id()).getKalendarz_id()).getCalendar_id();
        //addEventToCalendar(zapisanyKalendarz, danaKlasy.getCzas_rozpoczecia(), danaKlasy.getCzas_zakonczenia(), danaKlasy.toStringDoKalendarza());
        //TODO: dodać kolumny do tablicy zleceń z id wydarzenia z kalendarza, co to za kalendarz, id name ownername, czy może przebudować ustawienia?
        //danaKlasy.setKalendarz_id_long(zapisanyKalendarz);
        danaKlasy.setKalendarz_zadanie_id(addEventToCalendar(danaKlasy.getKalendarz_id_long(), danaKlasy.getCzas_rozpoczecia(), danaKlasy.getCzas_zakonczenia(), danaKlasy.toStringDoKalendarza()));
        Log.d("FragmentZadanie id zadania: ", String.valueOf(danaKlasy.getKalendarz_zadanie_id()));
    }

}
