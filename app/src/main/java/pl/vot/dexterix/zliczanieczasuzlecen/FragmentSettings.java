package pl.vot.dexterix.zliczanieczasuzlecen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentSettings extends FragmentPodstawowy{
    private static final int CODE_CREATE_FILE = 1;
    //private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 615;
    //private static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 616;
    //private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 617;
    protected static final int WRITE_SEND_BACKUP_REQUEST_CODE = 46;
    protected static final int WRITE_SEND_BACKUP_REQUEST_CODE1 = 47;
    BackupExportFromSQL befs = new BackupExportFromSQL();

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        changeTitle("Ustawienia");
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //String zz = ActivitySettings.this.getApplicationInfo().dataDir;
        ukryjFloatingButton();
        //poprosOUprawnienia();
        addListenerOnbuttonBackup(R.id.buttonBackup);
        addListenerOnButtonJakis(R.id.buttonRestore);
        addListenerOnButtonRestore(R.id.button3);
        addListenerOnButtonSendBackup(R.id.buttonSendBackup);
        addListenerOnButtonSendRaporty(R.id.buttonSendReports);
        addListenerOnSwitch(R.id.toggleButtonBaza);
    }

    private void addListenerOnButtonRestore(int button) {


        Button buttonGuzik = (Button) getActivity().findViewById(button);
        buttonGuzik.setText("Synchronizacja");
        buttonGuzik.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //renumerujRekordy();
                //createFile("text/plain", "foobar.txt");
                String tagBackStack = "FragmentSynchronizacja";
                zmianaFragmentu(new FragmentSynchronizacja(), tagBackStack);
                //Toast.makeText(getActivity(), "Funkcjonalnośc jeszcze nie działa", Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void addListenerOnButtonJakis(int button) {


        Button buttonGuzik = (Button) getActivity().findViewById(button);
        buttonGuzik.setText("Backup bazy");
        buttonGuzik.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String tagBackStack = "FragmentBackupBazy";
                zmianaFragmentu(new FragmentBackupBazy(), tagBackStack);

            }
        });
    }

    private void addListenerOnButtonJakis1(int button) {


        Button buttonGuzik = (Button) getActivity().findViewById(button);
        buttonGuzik.setText("Zeruj daty synchronizacji");
        buttonGuzik.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //FragmentWyborKalendarza fragmentDoZamiany = new FragmentWyborKalendarza();
                //zmianaFragmentu(fragmentDoZamiany, "FragmentWyborKalendarza");
                Log.d("Zeruję daty synchronizacji dla firm", "");
                OSQLdaneFirma osql = new OSQLdaneFirma(getActivity());
                List<daneFirma> daneTestowe = new ArrayList<>();
                daneTestowe = osql.dajWszystkie();
                for (int i = 0; i < daneTestowe.size(); i++){
                    Log.d("czas synchr ", String.valueOf(daneTestowe.get(i).getData_synchronizacji()));
                }
                osql.zerujDateSynchronizacji();
                List<daneFirma> daneTestowe1 = new ArrayList<>();
                daneTestowe1 = osql.dajWszystkie();
                for (int i = 0; i < daneTestowe1.size(); i++){
                    Log.d("czas synchr 2", String.valueOf(daneTestowe1.get(i).getData_synchronizacji()));
                }
                osql.close();
                Log.d("Daty synchronizacji dla firm ", " wyzerowane");
                osql = null;
                daneTestowe = null;
                daneTestowe1 = null;

                Log.d("Zeruję daty synchronizacji dla Stawek", "");
                OSQLdaneStawka osqlS = new OSQLdaneStawka(getActivity());
                List<daneStawka> daneTestoweS = new ArrayList<>();
                daneTestoweS = osqlS.dajWszystkie();
                for (int i = 0; i < daneTestoweS.size(); i++){
                    Log.d("czas synchr ", String.valueOf(daneTestoweS.get(i).getData_synchronizacji()));
                }
                osqlS.zerujDateSynchronizacji();
                List<daneStawka> daneTestoweSk = new ArrayList<>();
                daneTestoweSk = osqlS.dajWszystkie();
                for (int i = 0; i < daneTestoweSk.size(); i++){
                    Log.d("czas synchr 2", String.valueOf(daneTestoweSk.get(i).getData_synchronizacji()));
                }
                osqlS.close();
                Log.d("Daty synchronizacji dla Stawek ", " wyzerowane");
                osqlS = null;
                daneTestoweS = null;
                daneTestoweSk = null;

                Log.d("Zeruję daty synchronizacji dla Zleceń", "");
                OSQLdaneZlecenia osqlZ = new OSQLdaneZlecenia(getActivity());
                List<daneZlecenia> daneTestoweZ = new ArrayList<>();
                daneTestoweZ = osqlZ.dajWszystkie();
                for (int i = 0; i < daneTestoweZ.size(); i++){
                    Log.d("czas synchr ", String.valueOf(daneTestoweZ.get(i).getData_synchronizacji()));
                }
                osqlZ.zerujDateSynchronizacji();
                List<daneZlecenia> daneTestoweZk = new ArrayList<>();
                daneTestoweZk = osqlZ.dajWszystkie();
                for (int i = 0; i < daneTestoweZk.size(); i++){
                    Log.d("czas synchr 2", String.valueOf(daneTestoweZk.get(i).getData_synchronizacji()));
                }
                osqlZ.close();
                Log.d("Daty synchronizacji dla Zleceń ", " wyzerowane");
                osqlZ = null;
                daneTestoweZ = null;
                daneTestoweZk = null;
                //Toast.makeText(getActivity(), "Funkcjonalnośc jeszcze nie działa", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void addListenerOnSwitch(int RidTogglebutton){

        ToggleButton toggle = (ToggleButton) getActivity().findViewById(RidTogglebutton);
        toggle.setText("Baza Produkcyjna");
        toggle.setTextOff("Baza Produkcyjna");
        toggle.setTextOn("Baza Testowa");
        String activeBase = getActiveDataBase();
        if (activeBase.equals("1")){
            toggle.setChecked(false);
        }else{
            toggle.setChecked(true);
        }
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.ActiveDatabase), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getResources().getString(R.string.ActiveDatabase), "0");
                    editor.apply();
                    Log.d("toggle", "0");
                } else {
                    // The toggle is disabled
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.ActiveDatabase), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getResources().getString(R.string.ActiveDatabase), "1");
                    editor.apply();
                    Log.d("toggle", "1");
                }
            }
        });


    }

    private void addListenerOnButtonSendBackup(int buttonSendBackup) {
        Button buttonBackupSendBackup = (Button) getActivity().findViewById(buttonSendBackup);
        buttonBackupSendBackup.setText("Zeruj daty utworzenia");
        buttonBackupSendBackup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            //Toast.makeText(getActivity(), "Funkcjonalnośc jeszcze nie działa", Toast.LENGTH_SHORT).show();
                Log.d("Zeruję daty utworzenia dla firm", "");
                OSQLdaneFirma osql = new OSQLdaneFirma(getActivity());
                List<daneFirma> daneTestowe = new ArrayList<>();
                daneTestowe = osql.dajWszystkie();
                for (int i = 0; i < daneTestowe.size(); i++){
                    Log.d("czas utworzenia ", String.valueOf(daneTestowe.get(i).getData_utworzenia()));
                }
                osql.zerujDateUtworzenia();
                List<daneFirma> daneTestowe1 = new ArrayList<>();
                daneTestowe1 = osql.dajWszystkie();
                for (int i = 0; i < daneTestowe1.size(); i++){
                    Log.d("czas utworzenia 2", String.valueOf(daneTestowe1.get(i).getData_utworzenia()));
                }
                osql.close();
                Log.d("Daty utworzenia dla firm ", " wyzerowane");
                osql = null;
                daneTestowe = null;
                daneTestowe1 = null;

                Log.d("Zeruję daty utworzenia dla Stawek", "");
                OSQLdaneStawka osqlS = new OSQLdaneStawka(getActivity());
                List<daneStawka> daneTestoweS = new ArrayList<>();
                daneTestoweS = osqlS.dajWszystkie();
                for (int i = 0; i < daneTestoweS.size(); i++){
                    Log.d("czas utworzenia ", String.valueOf(daneTestoweS.get(i).getData_utworzenia()));
                }
                osqlS.zerujDateUtworzenia();
                List<daneStawka> daneTestoweSk = new ArrayList<>();
                daneTestoweSk = osqlS.dajWszystkie();
                for (int i = 0; i < daneTestoweSk.size(); i++){
                    Log.d("czas utworzenia 2", String.valueOf(daneTestoweSk.get(i).getData_utworzenia()));
                }
                osqlS.close();
                Log.d("Daty utworzenia dla Stawek ", " wyzerowane");
                osqlS = null;
                daneTestoweS = null;
                daneTestoweSk = null;

                Log.d("Zeruję daty utworzenia dla Zleceń", "");
                OSQLdaneZlecenia osqlZ = new OSQLdaneZlecenia(getActivity());
                List<daneZlecenia> daneTestoweZ = new ArrayList<>();
                daneTestoweZ = osqlZ.dajWszystkie();
                for (int i = 0; i < daneTestoweZ.size(); i++){
                    Log.d("czas utworzenia ", String.valueOf(daneTestoweS.get(i).getData_utworzenia()));
                }
                osqlZ.zerujDateUtworzenia();
                List<daneZlecenia> daneTestoweZk = new ArrayList<>();
                daneTestoweZk = osqlZ.dajWszystkie();
                for (int i = 0; i < daneTestoweZk.size(); i++){
                    Log.d("czas utworzenia 2", String.valueOf(daneTestoweZk.get(i).getData_utworzenia()));
                }
                osqlZ.close();
                Log.d("Daty utworzenia dla Zleceń ", " wyzerowane");
                osqlZ = null;
                daneTestoweZ = null;
                daneTestoweZk = null;
            /*private File privateRootDir;
            // The path to the "backup" subdirectory
            private File backupDir;
            // Array of files in the backups subdirectory
            File[] backupFiles;

            //Log.d("getFilesDir(): ", getFilesDir());
            public void onClick(View v) {
                ArrayList<Uri> backupUris = new ArrayList<Uri>();

                privateRootDir = new File(getActivity().getApplicationInfo().dataDir);//getFilesDir();
                //Log.d("getFilesDir(): ", getFilesDir().toString());
                // Get the root/backup subdirectory;
                backupDir = new File(privateRootDir, "backup");
                Log.d("niby backup: , ", backupDir.toString());
                // Get the files in the backups subdirectory
                backupFiles = backupDir.listFiles();
                //ListView fileListView = (ListView) findViewById(R.id.listviewSelectFiles);
                String[] imageFilenames = new String[backupFiles.length];
                Log.d("ilość plików: ", String.valueOf(backupFiles.length));
                //generujemy URI dla poszczególnych plików
                for (int i = 0; i < imageFilenames.length; ++i) {
                    Log.d("Plik nr:", String.valueOf(i));
                    Log.d("Nazwa pliku: ", backupFiles[i].toString());
                    Uri fileUri = null;

                    Log.d(getActivity().getPackageName().concat(".").concat("ActivitySettings"), getActivity().getPackageName().concat(".").concat("ActivitySettings"));
                    try {
                        fileUri = FileProvider.getUriForFile(
                                getActivity(),
                                "pl.vot.dexterix.zliczanieczasuzlecen.fileprovider",
                                backupFiles[i]);//.toURI()));
                        Log.d("czy jestem tu?,", "u?");
                        backupUris.add(fileUri);
                        Log.d("czy jestem tu?,", "u?");

                    } catch (IllegalArgumentException e) {
                        Log.e("File Selector",
                                "The selected file can't be shared: " + backupFiles[i].toString() + "a e to: " + e);
                    }


                }
                //przygotowujemy formatkę do dzielenia
                Intent shareIntent = new Intent();
                //ustawiamy flagę do odczytu plików przez URI
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //wysyłamy kilka
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                //wrzucamy co wysyłamy
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, backupUris);
                Log.d("Dotarłem aż do: ", "shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, backupUris);");
                //typ pliku/ów
                shareIntent.setType("text/*");
                //działamy
                startActivity(Intent.createChooser(shareIntent, "Wyśłij do"));*/




            }
        });
    }

    private void addListenerOnButtonSendRaporty(int buttonSendReports) {
        Button buttonBackupSendBackup = (Button) getActivity().findViewById(buttonSendReports);
        buttonBackupSendBackup.setOnClickListener(new View.OnClickListener(){
                                                      @Override
                                                      public void onClick(View v) {
                                                          Toast.makeText(getActivity(), "Funkcjonalnośc jeszcze nie działa", Toast.LENGTH_SHORT).show();
            /*private File privateRootDir;
            // The path to the "backup" subdirectory
            private File backupDir;
            // Array of files in the backups subdirectory
            File[] backupFiles;

            //Log.d("getFilesDir(): ", getFilesDir());
            public void onClick(View v) {
                ArrayList<Uri> backupUris = new ArrayList<Uri>();

                //privateRootDir = new File(getActivity().getApplicationInfo().dataDir);//getFilesDir();
                privateRootDir = new File(String.valueOf(getActivity().getExternalFilesDir(null)));//getFilesDir();
                //File raportDir = FileUtils.createDirIfNotExist(getActivity().getExternalFilesDir(null) + "/raport");
                //Log.d("getFilesDir(): ", getFilesDir().toString());
                // Get the root/backup subdirectory;
                backupDir = new File(privateRootDir, "raport");
                Log.d("niby raport: , ", backupDir.toString());
                // Get the files in the backups subdirectory
                backupFiles = backupDir.listFiles();
                //ListView fileListView = (ListView) findViewById(R.id.listviewSelectFiles);
                String[] imageFilenames = new String[backupFiles.length];
                Log.d("ilość plików: ", String.valueOf(backupFiles.length));
                //generujemy URI dla poszczególnych plików
                for (int i = 0; i < imageFilenames.length; ++i) {
                    Log.d("Plik nr:", String.valueOf(i));
                    Log.d("Nazwa pliku: ", backupFiles[i].toString());
                    Uri fileUri = null;

                    Log.d(getActivity().getPackageName().concat(".").concat("ActivitySettings"), getActivity().getPackageName().concat(".").concat("ActivitySettings"));
                    try {
                        fileUri = FileProvider.getUriForFile(
                                getActivity(),
                                "pl.vot.dexterix.zliczanieczasuzlecen.fileprovider",
                                backupFiles[i]);//.toURI()));
                        Log.d("czy jestem tu?,", "u?");
                        backupUris.add(fileUri);
                        Log.d("czy jestem tu?,", "u?");

                    } catch (IllegalArgumentException e) {
                        Log.e("File Selector",
                                "The selected file can't be shared: " + backupFiles[i].toString() + "a e to: " + e);
                    }


                }
                //przygotowujemy formatkę do dzielenia
                Intent shareIntent = new Intent();
                //ustawiamy flagę do odczytu plików przez URI
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //wysyłamy kilka
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                //wrzucamy co wysyłamy
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, backupUris);
                Log.d("Dotarłem aż do: ", "shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, backupUris);");
                //typ pliku/ów
                shareIntent.setType("text/*");
                //działamy
                startActivity(Intent.createChooser(shareIntent, "Wyśłij do"));*/




            }
        });
    }

    //to żeby pobrać link ?URI do pliku i coś z nim zrobić

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == WRITE_SEND_BACKUP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            //Uri uri = null;
            if (resultData != null) {
                uriToFile = resultData.getData();
                //Log.i("TAG", "Uri: " + uriToFile.toString());
                //ObslugaSQL osql = new ObslugaSQL(getActivity());
                //osql.
                //przekazujejy uri do pliku
                befs.setUriToFile(resultData.getData());
                //odpalamy backup
                befs.writeBackup(getActivity(), uriToFile );
                //dodajemy do listy uri
                fileListUris.add(uriToFile);
                //wyysłamy pliki
                sendFiles();
            }else{
                Toast.makeText(getActivity(), "Nie wybrałeś pliku raportu!", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Nie wybrałeś pliku raportu!");
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

        /*if (requestCode == WRITE_SEND_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("TAG", "Uri: " + uri.toString());
                alterDocument(uri, "bla bla");
            }

        }*/
    }

    private void addListenerOnbuttonBackup(int buttonBackup) {
        Button buttonBackupBackup = (Button) getActivity().findViewById(buttonBackup);
        buttonBackupBackup.setText("Backup i Wyślij");
        buttonBackupBackup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //BackupExportFromSQL befs = new BackupExportFromSQL();
                //befs.export(getActivity());
                createFile("text/plain", befs.createBackupFileName(), WRITE_SEND_BACKUP_REQUEST_CODE);
                /*ObslugaSQL osql = new ObslugaSQL(getActivity());
                Log.d("Katalog: ", "yy");
                osql.zrobKopieBazy("bla", getActivity());*/

            }
        });
    }

    /*private void poprosOUprawnienia() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_CALENDAR)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CALENDAR)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }*/

    private void renumerujRekordy(){
        //pobieramy wszystkie dane do renumeracji
        List<daneZlecenia> zlecenia;
        OSQLdaneZlecenia daneZleceniaSQL = new OSQLdaneZlecenia(getActivity());
        zlecenia = daneZleceniaSQL.dajWszystkie();
        //to zaczynamy
        for (daneZlecenia zlecenie : zlecenia){
            zlecenie.setCzy_widoczny(1);
            OSQLdaneZlecenia osql = new OSQLdaneZlecenia(getActivity());
            osql.updateDane(zlecenie);
        }
    }


}
