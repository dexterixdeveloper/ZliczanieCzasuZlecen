package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.Context;
import android.os.Bundle;
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

    private void obsluzGuzikWykonajRaport(View view)  {
        view.findViewById(R.id.buttonWykonajRaport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("FragmentRaporty: ", "klik");
                OSQLdaneZlecenia daneZleceniaSQL = new OSQLdaneZlecenia(getActivity());
                zlecenia = daneZleceniaSQL.dajWszystkieDoRaportu("zak", aktualnaData.getDateFromString(String.valueOf(textInputEditTextDataPoczatkowa.getText())), aktualnaData.getDateFromString(String.valueOf(textInputEditTextDataKoncowa.getText())));
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
                writeToFile(zlecenia.get(0).toStringForRaportNaglowek(), getActivity(), fileName, raportDir.getAbsolutePath());
                for (int i = 0; i < zlecenia.size(); i++){
                    writeToFile(zlecenia.get(i).toStringForRaport(), getActivity(), fileName, raportDir.getAbsolutePath());
                    Log.d("FragmentRaporty: zapis do pliku: ", zlecenia.get(i).toStringForRaport());
                }
                Log.d("FragmentRaporty: ", raportFile.getName());

                long endTime = System.currentTimeMillis();
                Log.d("FragmentRaporty: ", "Creating raport took " + (endTime - starTime) + "ms.");
                Toast.makeText(getActivity(), "Creating raport took " + (endTime - starTime) + "ms.", Toast.LENGTH_SHORT).show();

                cofnijDoPoprzedniegoFragmentu();
                //zapiszDaneICofnijDoPoprzedniegofragmentu("zak");
            }
        });
    }

    private void writeToFile(String data, Context context, String fileName, String dir) {
        try {
            FileOutputStream plik = new FileOutputStream(dir + "/" + fileName);//openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(plik);
            myOutWriter.append(data);
            myOutWriter.close();
            plik.close();


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
