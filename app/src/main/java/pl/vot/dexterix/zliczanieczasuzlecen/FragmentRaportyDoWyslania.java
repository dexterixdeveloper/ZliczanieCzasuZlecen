package pl.vot.dexterix.zliczanieczasuzlecen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FragmentRaportyDoWyslania extends FragmentPodstawowy {
    List<daneZlecenia> zlecenia;
    private String TAG = this.getClass().getName();
    private TextInputEditText textInputEditTextDataKoncowa;
    private TextInputEditText textInputEditTextDataPoczatkowa;
   // private TextInputLayout textInputLayoutDataPoczatkowa;
    //private TextInputLayout textInputLayoutDataKoncowa;
    private Integer danaKlasy = 0;
    private Integer danaKlasyRodzajRaportu = 0;
    private Long poczatek =0L; //poczatek raportu
    private Long koniec =0L; //koniec raportu
    protected boolean czyWyslijRaport = true;
    daneData aktualnaData = new daneData();

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        changeTitle("Raporty do wysłania");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_raporty_do_wyslania, container, false);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ukryjFloatingButton();
        textInputEditTextDataKoncowa = (TextInputEditText) view.findViewById(R.id.textInputEditTextDataKoncowa);
        textInputEditTextDataPoczatkowa = (TextInputEditText) view.findViewById(R.id.textInputEditTextDataPoczatkowa);
        //textInputLayoutDataPoczatkowa = (TextInputLayout) view.findViewById(R.id.textInputLayoutDataPoczatkowa);
        //textInputLayoutDataKoncowa = (TextInputLayout) view.findViewById(R.id.textInputLayoutDataKoncowa);

        aktualnaData.podajDate();
        new DateTimePicker(getActivity(), R.id.textInputEditTextDataKoncowa, aktualnaData.getDataMilisekundy("nic"), true);
        new DateTimePicker(getActivity(), R.id.textInputEditTextDataPoczatkowa, aktualnaData.getDataMilisekundy("nic"), true);
        //textInputEditTextUwagi.setText(String.valueOf(przeniesioneID));
        obsluzGuzikWykonajRaport(getView());

        //Powiadomienie
        //pokazPowiadomienie();

        dodajDoSpinnerWybierzRodzajRaportu(R.id.spinnerWybierzRodzajRaportu, R.string.wybierz, 0);

        dodajDoSpinnerWybierzFirme(R.id.spinnerWybierzFirme, R.string.dodaj, R.string.wszystkie_firmy, 0);

    }



    private void dodajDoSpinnerWybierzRodzajRaportu(int rIdSpinner, final Integer wybierz, Integer wybor) {
        Spinner spinner = (Spinner) getActivity().findViewById(rIdSpinner);
        //dodajemy dane do spinnera
        ArrayList<String[]> danaSpinnera = new ArrayList<>();
        String[] wiersz1 = {String.valueOf(1), "Miesięczny"};
        danaSpinnera.add(wiersz1);
        String[] wiersz2 = {String.valueOf(2), "Od daty do daty"};
        danaSpinnera.add(wiersz2);
        String[] wiersz3 = {String.valueOf(3), "Na dziś"};
        danaSpinnera.add(wiersz3);
        //dodajemy sobie na poczatku wyraz wybierz, ale poco? od razu ustawiamy jaiś raport
        //String[] staleSpinnera = {String.valueOf(0), getString(wybierz), "0"};
        //danaSpinnera.add(0, staleSpinnera);

        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(getActivity(), danaSpinnera);
        spinner.setAdapter(adapter);

        //sprawdzamy czy nie mamy narzuconego wyboru z góry
        if (wybor > 0 ) {
            spinner.setSelection(wybor);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //String poszukiwanie = String.valueOf(adapterView.getSelectedItem());
                //Log.d("poszukiwanie: ", poszukiwanie);
                //if (!(poszukiwanie.equals(getString(wybierz)))){
                if (!(danaSpinnera.get(i)[1].equals(getString(wybierz)))){
                    danaKlasyRodzajRaportu = (Integer.valueOf(danaSpinnera.get(i)[0]));
                    //Log.d("FragmentRaportyDoWyslania Spinner if", String.valueOf(danaKlasy));
                }else {//if (poszukiwanie.equals(getString(dodaj))){
                    danaKlasyRodzajRaportu = -1;
                   // Log.d("FragmentRaportyDoWyslania Spinner else", String.valueOf(danaKlasy));
                }
                //ustawiamy sobie z czapy date
                daneData dataAktualna = new daneData();
                dataAktualna.podajDate();
                //więc co wybraliśmy
                switch(danaKlasyRodzajRaportu) {
                    case 1://Miesięczny
                        textInputEditTextDataPoczatkowa.setVisibility(View.VISIBLE);
                        textInputEditTextDataKoncowa.setVisibility(View.GONE);
                        textInputEditTextDataKoncowa.setHint("");
                        textInputEditTextDataPoczatkowa.setHint("Wybierz miesiąc");
                        textInputEditTextDataPoczatkowa.setText(dataAktualna.getDataString());
                        break;
                    case 2://Od daty do daty
                        textInputEditTextDataKoncowa.setVisibility(View.VISIBLE);
                        textInputEditTextDataKoncowa.setText(dataAktualna.getDataString());
                        textInputEditTextDataPoczatkowa.setVisibility(View.VISIBLE);
                        textInputEditTextDataPoczatkowa.setText(dataAktualna.getDataString());
                        textInputEditTextDataPoczatkowa.setHint(R.string.podaj_dat_od_kiedy);
                        textInputEditTextDataKoncowa.setHint(R.string.podaj_dat_do_kiedy);
                        break;
                    case 3://Na dziś
                        textInputEditTextDataKoncowa.setVisibility(View.GONE);
                        textInputEditTextDataPoczatkowa.setVisibility(View.VISIBLE);
                        textInputEditTextDataKoncowa.setHint("");
                        textInputEditTextDataPoczatkowa.setHint("Wybierz dzień");
                        textInputEditTextDataPoczatkowa.setText(dataAktualna.getDataString());
                        break;
                    default:

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //danaKlasy.setFirma_id(-1);
                danaKlasyRodzajRaportu = -1;
                //Log.d("FragmentRaportyDoWyslania Spinner onnothingselected", String.valueOf(danaKlasy));
            }
        });
    }

    public void dodajDoSpinnerWybierzFirme(int rIdSpinner, final Integer dodaj, final Integer wybierz, Integer wybor) {
        Spinner spinner = (Spinner) getActivity().findViewById(rIdSpinner);
        OSQLdaneFirma dA = new OSQLdaneFirma(getActivity());
        //Log.d("Spinner", "2)");
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
                //String poszukiwanie = String.valueOf(adapterView.getSelectedItem());
                //Log.d("poszukiwanie: ", poszukiwanie);
                //if (!(poszukiwanie.equals(getString(wybierz)))){
                if (!(danaSpinnera.get(i)[1].equals(getString(wybierz)))){
                    danaKlasy = (Integer.valueOf(danaSpinnera.get(i)[0]));
                    //Log.d("FragmentRaportyDoWyslania Spinner if", String.valueOf(danaKlasy));
                }else {//if (poszukiwanie.equals(getString(dodaj))){
                    danaKlasy = -1;
                    //Log.d("FragmentRaportyDoWyslania Spinner else", String.valueOf(danaKlasy));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //danaKlasy.setFirma_id(-1);
                danaKlasy = -1;
                //Log.d("FragmentRaportyDoWyslania Spinner onnothingselected", String.valueOf(danaKlasy));
            }
        });
    }//public void dodajDoSpinnerEksploatacjaDodajCzynnosc(Integer rSpinner) {

    private void obsluzGuzikWykonajRaport(View view)  {
        Context context = getActivity();
        view.findViewById(R.id.buttonWykonajRaport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                switch(danaKlasyRodzajRaportu) {
                    case -1:
                        Toast.makeText(context, "Nie wybrałes rodzaju raportu", Toast.LENGTH_SHORT).show();
                        break;
                    case 1: //"Miesięczny"
                        aktualnaData.getDateFromString(String.valueOf(textInputEditTextDataPoczatkowa.getText()));
                        poczatek = aktualnaData.getPoczatekMiesiaca();
                        koniec = aktualnaData.getKoniecMiesiaca();
                        //wykonajWyslijRaport(aktualnaData.getPoczatekMiesiaca(), aktualnaData.getKoniecMiesiaca());
                        break;

                    case 2://"Od daty do daty"
                        aktualnaData.getDateFromString(String.valueOf(textInputEditTextDataPoczatkowa.getText()));
                        poczatek = aktualnaData.getPoczatekDnia();
                        aktualnaData.getDateFromString(String.valueOf(textInputEditTextDataKoncowa.getText()));
                        koniec = aktualnaData.getKoniecDnia();
                        //wykonajWyslijRaport(poczatek, koniec);
                        break;

                    case 3://"Na dziś"
                        aktualnaData.getDateFromString(String.valueOf(textInputEditTextDataPoczatkowa.getText()));
                        poczatek = aktualnaData.getPoczatekDnia();
                        koniec = aktualnaData.getKoniecDnia();
                        //wykonajWyslijRaport(poczatek, koniec);
                        break;
                    default:
                       
                        break;
                }
                wykonajWyslijRaport1();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == WRITE_SEND_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            //Uri uri = null;
            if (resultData != null) {
                uriToFile = resultData.getData();
                Log.i(TAG, "Uri: " + uriToFile.toString());

                wykonajWyslijRaport(poczatek, koniec, czyWyslijRaport);
            }else{
                Toast.makeText(getActivity(), "Nie wybrałeś pliku raportu!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Nie wybrałeś pliku raportu!");
            }
        }
        if (requestCode == SEND_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            //Uri uri = null;
            cofnijDoPoprzedniegoFragmentu();
        }
    }

    private void wykonajWyslijRaport1() {
        if (czyWyslijRaport) {
            //tworzymy plik
            createFile("text/plain", "raport.csv", WRITE_SEND_REQUEST_CODE);
        }else{
            wykonajWyslijRaport(poczatek, koniec, czyWyslijRaport);
        }
    }

    private void wykonajWyslijRaport(Long poczatekRaportu, Long koniecRaportu, boolean wyslij) {
                //Log.d("FragmentRaportyDoWyslania: ", "klik");
                OSQLdaneZlecenia daneZleceniaSQL = new OSQLdaneZlecenia(getActivity());
                //zlecenia = daneZleceniaSQL.dajWszystkieDoRaportu("zak", aktualnaData.getDateFromString(String.valueOf(textInputEditTextDataPoczatkowa.getText())), aktualnaData.getDateFromString(String.valueOf(textInputEditTextDataKoncowa.getText())), danaKlasy);
                zlecenia = daneZleceniaSQL.dajWszystkieDoRaportu("zak", poczatekRaportu, koniecRaportu, danaKlasy);
                if (!zlecenia.isEmpty()) {//czy przypadkiem nie jest pusty raport

                    //tworzymy plik raportu

                    if (czyWyslijRaport) {
                        Log.d("FragmentRaportyDoWyslania: ", "Started to fill the raport file in " + uriToFile.toString());
                    }
                    //Toast.makeText(getActivity(), "Started to fill the raport file in " + uriToFile.toString(), Toast.LENGTH_SHORT).show();
                    //TODO: tu skończyłem
                    long starTime = System.currentTimeMillis();
                    //Log.d("cos chyba", "nie bangla");
                    //pobieramy stawki
                    List<daneStawka> stawki;
                    OSQLdaneStawka daneStawkiSQL = new OSQLdaneStawka(getActivity());
                    stawki = daneStawkiSQL.dajWszystkie();
                    //a co w przypadku gdy nie ma zdefinowanej stawki w danych godzinach, lub nie ma ich wcale?
                    //może z automatu dodajmy domyślne stawki dla każdej firmy?
                    if (stawki.isEmpty()) {
                        Log.d("Raporty", "Brak jakichkolwiek stawek, uzupełnij dane!");
                        Toast.makeText(getActivity(), "Brak jakichkolwiek stawek, uzupełnij dane!", Toast.LENGTH_SHORT).show();
                        //w takim razie dodajmy stawki na cały dzień dla kazdej firmy
                        List<daneFirma> firmy;
                        OSQLdaneFirma daneFirmySQL = new OSQLdaneFirma(getActivity());
                        firmy = daneFirmySQL.dajWszystkie();
                        daneStawka stawka = new daneStawka();
                        stawka.onCreate();
                        for (daneFirma firma : firmy) {
                            stawka.setFirma_id(firma.getId());
                            daneStawkiSQL.dodajDane(daneStawkiSQL.contentValues(stawka), daneStawkiSQL.getTableName());
                        }
                        //w takim razie musimy jeszcze raz odpalić stawki
                        stawki = daneStawkiSQL.dajWszystkie();
                    }

                    //w takim razie musimy jeszcze raz odpalić stawki
                    stawki = daneStawkiSQL.dajWszystkie();
                    //
                    Log.d("Raporty", "Stawki size: " + String.valueOf(stawki.size()));
                    //sprawdzamy jak to jest z godzinami
                    Log.d("ile stawek", String.valueOf(stawki.size()));
                    for (int j = 0; j < stawki.size(); j++) {
                        Log.d("ile stawek for poczatek " + j, String.valueOf(stawki.size()));
                        daneData stawkaPoczatek = new daneData();
                        daneData stawkaKoniec = new daneData();
                        stawkaKoniec.podajDate();
                        //zebysmie mieli pewność że ta sama data
                        stawkaPoczatek.setDataMilisekundy(stawkaKoniec.getDataMilisekundy("nic"));
                        stawkaPoczatek.setGodzina(stawkaPoczatek.getDataMilisekundy("nic"), stawki.get(j).getPoczatek(), false);
                        Log.d("stawka poczatek", stawkaPoczatek.getDataString());
                        stawkaKoniec.setGodzina(stawkaKoniec.getDataMilisekundy("nic"), stawki.get(j).getKoniec(), false);
                        Log.d("stawka koniec", stawkaKoniec.getDataString());
                        //wyłączyłem sprawdzanie stawek, bo się bardacha robiła
                        /*if ((stawkaPoczatek.getDataMilisekundy("nic") > stawkaKoniec.getDataMilisekundy("nic")) && !stawki.get(j).getKoniec().equals("00:00")) {
                            //pobieramy dana problematycznej stawki
                            daneStawka stawka = new daneStawka();
                            stawka.setKoniec("00:00");
                            stawka.setPoczatek(stawki.get(j).getPoczatek());
                            stawka.setStawka(stawki.get(j).getStawka());
                            stawka.setFirma_id(stawki.get(j).getFirma_id());
                            stawka.setFirma_nazwa(stawki.get(j).getFirma_nazwa());
                            //zmieniamy problematyczna stawkę
                            stawki.get(j).setPoczatek("00:00");
                            //dodajemy kolejną stawkę do listy
                            stawki.add(stawka);
                        }*/
                        //jezeli stawka na cały dzień
                        if ((stawkaPoczatek.getDataMilisekundy("nic") == stawkaKoniec.getDataMilisekundy("nic")) && stawki.get(j).getKoniec().equals("00:00")) {

                            stawki.get(j).setKoniec("24:00");
                        }
                        Log.d("ile stawek for koniec " + j, String.valueOf(stawki.size()));
                    }
                    //jeżeli to tylko raport wyliczeniowy
                    List<daneZlecenRaporty> raportZlecen = new LinkedList<>();
                    if (!wyslij){
                        for (daneStawka stawka: stawki){
                            daneZlecenRaporty raportekZlecen = new daneZlecenRaporty(0,0,0L, "");
                            raportekZlecen.setFirmaId(stawka.getFirma_id());
                            Log.d("stawk", String.valueOf(stawka.getId()));
                            raportekZlecen.setStawkaWysokosc(stawka.getStawka());
                            raportekZlecen.setCzas(0L);
                            raportekZlecen.setFirmaNazwa(stawka.getFirma_nazwa());
                            raportZlecen.add(raportekZlecen);
                        }
                    }
                    //sprawdzamy jak to jest z godzinami -END

                    daneData czas = new daneData();
                    /*FileOutputStream plik = new FileOutputStream(raportDir.getAbsolutePath() + "/" + fileName);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(plik);
                    //data= zlecenia.get(0).toStringForRaportNaglowek(), plik);
                    myOutWriter.append(zlecenia.get(0).toStringForRaportNaglowek());*/
                    //zapisujemy nagłówek
                    if (wyslij) {//jeżeli to nie raporty wyliczeniowe
                        Log.d("Raporty: ", String.valueOf(wyslij));
                        alterDocument(uriToFile, zlecenia.get(0).toStringForRaportNaglowek());
                    }
                    for (int i = 0; i < zlecenia.size(); i++) {
                        Log.d("W for", String.valueOf(i));
                        daneZlecenia zlecenie = new daneZlecenia();
                        zlecenie.setCzas_rozpoczecia(zlecenia.get(i).getCzas_rozpoczecia());
                        zlecenie.setFirma_nazwa(zlecenia.get(i).getFirma_nazwa());
                        zlecenie.setOpis(zlecenia.get(i).getOpis());
                        zlecenie.setStatus(zlecenia.get(i).getStatus());
                        zlecenie.setUwagi(zlecenia.get(i).getUwagi());
                        zlecenie.setCzas_zakonczenia(zlecenia.get(i).getCzas_zakonczenia());
                        zlecenie.setFirma_id(zlecenia.get(i).getFirma_id());
                        zlecenie.setCzas_rozpoczecia_string(zlecenia.get(i).getCzas_rozpoczecia_string());
                        zlecenie.setCzas_zakonczenia_string(zlecenia.get(i).getCzas_zakonczenia_string());

                        //Log.d(String.valueOf(i), String.valueOf(zlecenia.get(i).getFirma_id()));

                        //zmienna potrzebna do określenia czy już całość zadania poszła do raportu
                        boolean czyCaloscDoRaportu = false;
                        while (!czyCaloscDoRaportu) {
                            //wyliczamy według stawek

                            for (int j = 0; j < stawki.size(); j++) {

                                //jezeli zgadza się firma dla stawki
                                if ((stawki.get(j).getFirma_id() == zlecenie.getFirma_id()) && !czyCaloscDoRaportu) {
                                    daneStawka stawka = new daneStawka();
                                    stawka.setKoniec(stawki.get(j).getKoniec());
                                    stawka.setPoczatek(stawki.get(j).getPoczatek());
                                    stawka.setStawka(stawki.get(j).getStawka());

                                    daneData czasStawekPoczatek = new daneData();
                                    daneData czasStawekKoniec = new daneData();
                                    czasStawekPoczatek.setGodzina(zlecenie.getCzas_rozpoczecia(), stawka.getPoczatek(), false);
                                    //Log.d("czasStawekPocz", String.valueOf(czasStawekPoczatek.getDataString()));
                                    czasStawekKoniec.setGodzina(zlecenie.getCzas_rozpoczecia(), stawka.getKoniec(), false);
                                    //Log.d("czasStawekKon", String.valueOf(czasStawekKoniec.getDataString()));
                                    if (czasStawekPoczatek.getDataMilisekundy("nic") > czasStawekKoniec.getDataMilisekundy("nic")) {
                                        czasStawekKoniec.setGodzina(zlecenie.getCzas_rozpoczecia(), stawka.getKoniec(), true);
                                        Log.d("czasStawekPoczif", String.valueOf(czasStawekKoniec.getDataString()));
                                    }
                                    //TODO: wykombinować jakoś żeby  dało się pominąć rozdzilenaie stawek do północy
                                    //jeżeli początek zlecenia jest równy większy od początku stawki
                                    //Log.d("zlecenie czas rozpoczecia: ", String.valueOf(zlecenie.getCzas_rozpoczecia_string()));
                                    //Log.d("Stawka Poczatek: ", String.valueOf(czasStawekPoczatek.getDataString()));
                                    //Log.d("Stawka Koniec: ", String.valueOf(czasStawekKoniec.getDataString()));
                                    //jeżeli początek stawki > od końca, czyli gdy np poczatek 16:00 a koniec 00:00
                                    //if (czasStawekPoczatek.getDataMilisekundy() > czasStawekKoniec.setGodzina(zlecenie.getCzas_rozpoczecia(), stawka.getKoniec(), false))
                                    if ((zlecenie.getCzas_rozpoczecia() >= czasStawekPoczatek.getDataMilisekundy("nic")) && (zlecenie.getCzas_rozpoczecia() <= czasStawekKoniec.getDataMilisekundy("nic"))) {
                                        //jeżeli koniec zlecenia mniejszy równy końcu stawki
                                        //Log.d("Przed if", "przed if");
                                        if (zlecenie.getCzas_zakonczenia() <= czasStawekKoniec.getDataMilisekundy("nic")) {
                                            //dajemy do raportu
                                            //jeżeli mieści się pomiędzy czasem rozpoczecia a zakończenia
                                            //myOutWriter.append(zlecenie.toStringForRaport() + ";" + stawka.getStawka() + ";\n");
                                            //zapisujemy dane
                                            if (wyslij) {
                                                alterDocument(uriToFile, zlecenie.toStringForRaport() + ";" + stawka.getStawka() + ";\n");
                                            }else{
                                                for (daneZlecenRaporty raport1: raportZlecen){
                                                    if ((raport1.getFirmaId() == zlecenie.getFirma_id()) && (stawka.getStawka() == raport1.getStawkaWysokosc())){
                                                        raport1.setCzas(raport1.getCzas() + zlecenie.getCzas_zakonczenia() - zlecenie.getCzas_rozpoczecia());
                                                    }
                                                }
                                            }
                                            //Log.d("FragmentRaportyDoWyslania: zapis do pliku:if ", zlecenie.toStringForRaport() + ";" + stawka.getStawka() + ";\n");
                                            //Log.d("W if", "w if");
                                            czyCaloscDoRaportu = true;
                                        } else {
                                            //gdy koniec zadania jednak > koniec stawki
                                            //Log.d("W else", "w else");
                                            //zapisujemy
                                            long czasZakonczeniaTmp = zlecenie.getCzas_zakonczenia();
                                            zlecenie.setCzas_zakonczenia(czasStawekKoniec.getDataMilisekundy());
                                            zlecenie.setCzas_zakonczenia_string(czasStawekKoniec.getDataString());
                                            //myOutWriter.append(zlecenie.toStringForRaport() + ";" + stawka.getStawka() + ";\n");
                                            //zapisujemy dane
                                            if (wyslij) {
                                                alterDocument(uriToFile, zlecenie.toStringForRaport() + ";" + stawka.getStawka() + ";\n");
                                            }else{
                                                for (daneZlecenRaporty raport1: raportZlecen){
                                                    if ((raport1.getFirmaId() == zlecenie.getFirma_id()) && (stawka.getStawka() == raport1.getStawkaWysokosc())){
                                                        raport1.setCzas(raport1.getCzas() + zlecenie.getCzas_zakonczenia() - zlecenie.getCzas_rozpoczecia());
                                                    }
                                                }
                                            }
                                            //Log.d("FragmentRaportyDoWyslania: zapis do pliku: ", zlecenie.toStringForRaport() + ";" + stawka.getStawka() + ";\n");
                                            zlecenie.setCzas_zakonczenia(czasZakonczeniaTmp);
                                            daneData czasTmp = new daneData();
                                            czasTmp.setDataMilisekundy(czasZakonczeniaTmp);
                                            zlecenie.setCzas_zakonczenia_string(czasTmp.getDataString());
                                            //Log.d("Czas zakonczenniaTMP", String.valueOf(czasZakonczeniaTmp));
                                            zlecenie.setCzas_rozpoczecia(czasStawekKoniec.getDataMilisekundy());
                                            //Log.d("czasStawekKoniec", String.valueOf(czasStawekKoniec.getDataMilisekundy()));
                                            zlecenie.setCzas_rozpoczecia_string(czasStawekKoniec.getDataString());

                                        }

                                    }
                                }
                            }
                            //w razie gdyby nie było ustawionej stawki:

                        }//while (czyCaloscDoRaportu == false) { -END
                        //wyliczamy według stawek -END
                    }

                    long endTime = System.currentTimeMillis();
                    Log.d("FragmentRaportyDoWyslania: ", "Creating raport took " + (endTime - starTime) + "ms.");
                    Toast.makeText(getActivity(), "Creating raport took " + (endTime - starTime) + "ms.", Toast.LENGTH_SHORT).show();
                    //automatycznie już wywsyłamy raporty
                    //dodajemy tylko Uri do raportu
                    if (wyslij) {
                        fileListUris.add(uriToFile);
                        sendFiles();
                    }else{
                        //zmianaFragmentu(new FragmentRaportyWyliczeniowePokaz(),"FragmentRaportyWyliczeniowePokaz");
                        StringBuilder sb = new StringBuilder();
                        sb.append("Firma | Wysokość Stawki | Czas | Zarobek\n");
                        for (daneZlecenRaporty dzr: raportZlecen){
                            sb.append(dzr.toString());
                        }
                        pokazOkienkoAlertuRaportu("OK", "Podsumowanie", sb.toString());
                    }
                    //sendRaportsFiles();
                    //cofnijDoPoprzedniegoFragmentu();
                    //zapiszDaneICofnijDoPoprzedniegofragmentu("zak");
                }else{
                    Toast.makeText(getActivity(), "Brak danych do raportu", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "wykonajWyslijRaport: Brak danych do raportu");
                }
            }

    private void pokazOkienkoAlertuRaportu(String sPositive, String sTytul, String sWiadomowsc){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        alertDialog.setTitle(sTytul);
        alertDialog.setMessage(sWiadomowsc);
        /*alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "nNeutralny",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, sPositive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

        alertDialog.show();


    }

    public static String createFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        return "raport_zlecen_" + sdf.format(new Date()) + "_ZliczanieCzasuZlecen.csv";
    }

}
