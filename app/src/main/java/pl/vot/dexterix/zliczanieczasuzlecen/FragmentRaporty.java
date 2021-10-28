package pl.vot.dexterix.zliczanieczasuzlecen;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        //pokazPowiadomienie();

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
                //String poszukiwanie = String.valueOf(adapterView.getSelectedItem());
                //Log.d("poszukiwanie: ", poszukiwanie);
                //if (!(poszukiwanie.equals(getString(wybierz)))){
                if (!(danaSpinnera.get(i)[1].equals(getString(wybierz)))){
                    danaKlasy = (Integer.valueOf(danaSpinnera.get(i)[0]));
                    Log.d("FragmentRaporty Spinner if", String.valueOf(danaKlasy));
                }else {//if (poszukiwanie.equals(getString(dodaj))){
                    danaKlasy = -1;
                    Log.d("FragmentRaporty Spinner else", String.valueOf(danaKlasy));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //danaKlasy.setFirma_id(-1);
                danaKlasy = -1;
                Log.d("FragmentRaporty Spinner onnothingselected", String.valueOf(danaKlasy));
            }
        });
    }//public void dodajDoSpinnerEksploatacjaDodajCzynnosc(Integer rSpinner) {

    private void obsluzGuzikWykonajRaport(View view)  {
        view.findViewById(R.id.buttonWykonajRaport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("FragmentRaporty: ", "klik");
                OSQLdaneZlecenia daneZleceniaSQL = new OSQLdaneZlecenia(getActivity());
                zlecenia = daneZleceniaSQL.dajWszystkieDoRaportu("zak", aktualnaData.getDateFromString(String.valueOf(textInputEditTextDataPoczatkowa.getText())), aktualnaData.getDateFromString(String.valueOf(textInputEditTextDataKoncowa.getText())), danaKlasy);
                Log.d("FragmentRaporty: ", zlecenia.get(1).toStringForRaport());

                //zapisujemy dane

                File raportDir = FileUtils.createDirIfNotExist(getActivity().getExternalFilesDir(null) + "/raport");
                //File backupDir = FileUtils.createDirIfNotExist(  context.getApplicationInfo().dataDir + "/backup");
                Log.d("Path: ", getActivity().getExternalFilesDir(null) + "/raport");
                //Log.d("Path: ", context.getApplicationInfo().dataDir + "/backup");
                //Log.d("Path: ", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
                String fileName = createFileName();

                File raportFile = new File(raportDir, fileName);

                //File backupFile = new File(sciezkaB.getPath());

                //File backupFile = new File(sciezkaB);
                //backupFile = sciezkaB;
                try {
                    boolean success = raportFile.createNewFile();
                } catch (IOException e) {
                    Log.d("FragmentRaporty: ", "cos sie zjeblo");
                    e.printStackTrace();
                }


                Log.d("FragmentRaporty: ", "Started to fill the raport file in " + raportFile.getAbsolutePath());
                Toast.makeText(getActivity(), "Started to fill the raport file in " + raportFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                long starTime = System.currentTimeMillis();
                //raportFile.
                //writeCsv(backupFile, db, tables);
                List<daneStawka> stawki;
                OSQLdaneStawka daneStawkiSQL = new OSQLdaneStawka(getActivity());
                stawki = daneStawkiSQL.dajWszystkie();

                //sprawdzamy jak to jest z godzinami
                Log.d("ile stawek", String.valueOf(stawki.size()));
                for (int j = 0; j < stawki.size(); j++) {
                    Log.d("ile stawek for poczatek" + j, String.valueOf(stawki.size()));
                    daneData stawkaPoczatek = new daneData();
                    daneData stawkaKoniec = new daneData();
                    stawkaKoniec.podajDate();
                    //zebysmie mieli pewność że ta sama data
                    stawkaPoczatek.setDataMilisekundy(stawkaKoniec.getDataMilisekundy());
                    stawkaPoczatek.setGodzina(stawkaPoczatek.getDataMilisekundy(), stawki.get(j).getPoczatek(), false);
                    Log.d("stawka poczatek", stawkaPoczatek.getDataString());
                    stawkaKoniec.setGodzina(stawkaKoniec.getDataMilisekundy(), stawki.get(j).getKoniec(), false);
                    Log.d("stawka koniec", stawkaKoniec.getDataString());
                    if ((stawkaPoczatek.getDataMilisekundy() > stawkaKoniec.getDataMilisekundy()) && !stawki.get(j).getKoniec().equals("00:00")) {
                        //pobieramy dana problematycznej stawki
                        daneStawka stawka = new daneStawka();
                        stawka.setKoniec("00:00");
                        stawka.setPoczatek(stawki.get(j).getPoczatek());
                        stawka.setStawka(stawki.get(j).getStawka());
                        stawka.setFirma_id(stawki.get(j).getFirma_id());
                        //zmieniamy problematyczna stawkę
                        stawki.get(j).setPoczatek("00:00");
                        //dodajemy kolejną stawkę do listy
                        stawki.add(stawka);
                    }
                    Log.d("ile stawek for koniec" + j, String.valueOf(stawki.size()));
                }
                //sprawdzamy jak to jest z godzinami -END

                daneData czas = new daneData();
                try {
                    FileOutputStream plik = new FileOutputStream(raportDir.getAbsolutePath() + "/" + fileName);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(plik);
                    //data= zlecenia.get(0).toStringForRaportNaglowek(), plik);
                    myOutWriter.append(zlecenia.get(0).toStringForRaportNaglowek());
                    for (int i = 0; i < zlecenia.size(); i++){
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
                        while (czyCaloscDoRaportu == false) {
                            //wyliczamy według stawek

                            for (int j = 0; j < stawki.size(); j++) {
                                //Log.d("stawki start: ", String.valueOf(j));
                                //Log.d("Zlecenie firma id: ", String.valueOf(zlecenie.getFirma_id()));
                                //Log.d("Stawka firma id: ", String.valueOf(stawki.get(j).getFirma_id()));
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
                                    if (czasStawekPoczatek.getDataMilisekundy() > czasStawekKoniec.getDataMilisekundy()){
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
                                    if ((zlecenie.getCzas_rozpoczecia() >= czasStawekPoczatek.getDataMilisekundy()) && (zlecenie.getCzas_rozpoczecia() <= czasStawekKoniec.getDataMilisekundy())) {
                                        //jeżeli koniec zlecenia mniejszy równy końcu stawki
                                        //Log.d("Przed if", "przed if");
                                        if (zlecenie.getCzas_zakonczenia() <= czasStawekKoniec.getDataMilisekundy()) {
                                            //dajemy do raportu
                                            //jeżeli mieści się pomiędzy czasem rozpoczecia a zakończenia
                                            myOutWriter.append(zlecenie.toStringForRaport() + ";" + stawka.getStawka() + ";\n");
                                            //Log.d("FragmentRaporty: zapis do pliku:if ", zlecenie.toStringForRaport() + ";" + stawka.getStawka() + ";\n");
                                            //Log.d("W if", "w if");
                                            czyCaloscDoRaportu = true;
                                        } else {
                                            //gdy koniec zadania jednak > koniec stawki
                                            //Log.d("W else", "w else");
                                            //zapisujemy
                                            long czasZakonczeniaTmp = zlecenie.getCzas_zakonczenia();
                                            zlecenie.setCzas_zakonczenia(czasStawekKoniec.getDataMilisekundy());
                                            zlecenie.setCzas_zakonczenia_string(czasStawekKoniec.getDataString());
                                            myOutWriter.append(zlecenie.toStringForRaport() + ";" + stawka.getStawka() + ";\n");
                                            //Log.d("FragmentRaporty: zapis do pliku: ", zlecenie.toStringForRaport() + ";" + stawka.getStawka() + ";\n");
                                            zlecenie.setCzas_zakonczenia(czasZakonczeniaTmp);
                                            daneData czasTmp = new daneData();
                                            czasTmp.setDataMilisekundy(czasZakonczeniaTmp);
                                            zlecenie.setCzas_zakonczenia_string(czasTmp.getDataString());
                                            //Log.d("Czas zakonczenniaTMP", String.valueOf(czasZakonczeniaTmp));
                                            zlecenie.setCzas_rozpoczecia(czasStawekKoniec.getDataMilisekundy());
                                            //Log.d("czasStawekKoniec", String.valueOf(czasStawekKoniec.getDataMilisekundy()));
                                            zlecenie.setCzas_rozpoczecia_string(czasStawekKoniec.getDataString());
                                            /*if(czasStawekKoniec.getDataMilisekundy() > zlecenie.getCzas_zakonczenia()){
                                                zlecenie.setCzas_rozpoczecia(czasStawekKoniec.setGodzina(zlecenie.getCzas_zakonczenia(), stawka.getKoniec(), true));
                                                Log.d("zlecenie czas rozpoczecia set if ", String.valueOf(zlecenie.getCzas_rozpoczecia()));
                                            }else{
                                                zlecenie.setCzas_rozpoczecia(czasStawekKoniec.setGodzina(zlecenie.getCzas_zakonczenia(), stawka.getKoniec(), false));
                                                Log.d("zlecenie czas rozpoczecia set else", String.valueOf(zlecenie.getCzas_rozpoczecia()));
                                            }*/

                                        }
                                    }
                                }
                            }
                            //w razie gdyby nie było ustawionej stawki:
                            //czyCaloscDoRaportu = true;
                            //myOutWriter.append(zlecenie.toStringForRaport() + ";" + "0" + ";");
                        }//while (czyCaloscDoRaportu == false) { -END
                        //wyliczamy według stawek -END
                        //myOutWriter.append(zlecenia.get(i).toStringForRaport());

                    }
                    myOutWriter.close();
                    plik.close();
                } catch (IOException e) {

                    Log.d("FragmentRaporty: ", "cos sie sparolilo");
                    Log.e("Exception", "File write failed: " + e.toString());
                    e.printStackTrace();
                }
                Log.d("FragmentRaporty: ", raportFile.getName());

                long endTime = System.currentTimeMillis();
                Log.d("FragmentRaporty: ", "Creating raport took " + (endTime - starTime) + "ms.");
                Toast.makeText(getActivity(), "Creating raport took " + (endTime - starTime) + "ms.", Toast.LENGTH_SHORT).show();
                //automatycznie już wywsyłamy raporty
                sendRaportsFiles();
                cofnijDoPoprzedniegoFragmentu();
                //zapiszDaneICofnijDoPoprzedniegofragmentu("zak");
            }
        });
    }

    private void zapiszDoRaportu(daneZlecenia zlecenie, daneStawka stawka){

    }

    private void writeToFile(String data, FileOutputStream plik) {
        try {
            //FileOutputStream plik = new FileOutputStream(dir + "/" + fileName);//openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(plik);
            myOutWriter.append(data);
            myOutWriter.close();
            //plik.close();


        }
        catch (IOException e) {
            Log.d("FragmentRaporty: ", "cos sie sparolilo");
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String createFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        return "raport_zlecen_" + sdf.format(new Date()) + "_ZliczanieCzasuZlecen.csv";
    }

}
