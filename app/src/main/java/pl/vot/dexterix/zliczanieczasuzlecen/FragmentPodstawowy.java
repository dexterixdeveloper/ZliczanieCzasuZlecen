package pl.vot.dexterix.zliczanieczasuzlecen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FragmentPodstawowy extends Fragment {
    private static final String CHANNEL_ID = "PowiadomienieZliczanieCzasuZlecen";
    private static final String TAG = FragmentPodstawowy.class.getSimpleName();
    //dla operacji na plikach
    protected static final int WRITE_SEND_REQUEST_CODE = 43;
    private static final int EDIT_REQUEST_CODE = 44;
    protected static final int SEND_REQUEST_CODE = 45;
    protected Uri uriToFile = null;
    //lista Uri do wysłania
    protected ArrayList<Uri> fileListUris = new ArrayList<Uri>();

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

            }


        }
    }

    public void setUriToFile(Uri uriToFile) {
        this.uriToFile = uriToFile;
    }

    protected void zmianaFragmentu(Fragment fragmencik, String tagBackStack, Boolean addToBackStack){
        // Begin the transaction
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment

        ft.replace(R.id.fragment_container_main, fragmencik, tagBackStack);
        Log.d("BackToStack", "Bez Add to back");

// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();

    }

    protected void changeTitle(String name){
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name_short) + ": " + name);
        //toolbar.setTitle(getString(R.string.app_name_skrot) + ": " + name);
    }

    protected void zmianaFragmentu(Fragment fragmencik, String tagBackStack){
        // Begin the transaction
        Log.d("Back2", "2");
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

// Replace the contents of the container with the new fragment
        ft.replace(R.id.fragment_container_main, fragmencik, tagBackStack);
        ft.addToBackStack(tagBackStack);

// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();
    }

    protected Fragment getVisibleFragment(){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        Log.d("Ilość fragmentów: ", String.valueOf(fragments.size()));
        if(fragments != null){
            for(Fragment fragment : fragments){
                Log.d("fragment1", fragment.getTag() + " " + fragment.getId());
                if(fragment != null && fragment.isVisible())
                    Log.d("fragment", fragment.toString());
                return fragment;
            }
        }
        return null;
    }

    void dodajFragmentGdyNieMaZadnego(){
        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        String tagBackStack = "FragmentStart";
        Log.d(TAG, "chyba się nie ale moza udało");

        //ft.add(new FragmentZadaniaDoZrobienia(), tagBackStack);
        //ft.addToBackStack(tagBackStack);
        ft.attach(new FragmentZadaniaDoZrobienia());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
    }

    protected void cofnijDoPoprzedniegoFragmentu() {
        //fragment Manager przenosi nas do poprzedniego fragmentu
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Log.d("FragmentPodstawowy fm.getilosc: ", String.valueOf(fm.getBackStackEntryCount()));
        Log.d("Back2", "2");
        //if (fm.getBackStackEntryCount() < 1) dodajFragmentGdyNieMaZadnego();

        //fm.putFragment();
        fm.popBackStack();

    }
    //wysyłanie pliku na zewnątrz: DropBox, Dysk Google, email, itd
    protected void sendRaportsFile(){

    }

    //ukrywamy floating button
    protected void ukryjFloatingButton() {
        FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButtonDodaj);
        fab.setVisibility(View.INVISIBLE);
    }

    //wysyłanie plików na dropbox, mail, itd
    protected void sendFiles(){
        //przygotowujemy formatkę do dzielenia
        Intent shareIntent = new Intent();
        //ustawiamy flagę do odczytu plików przez URI
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //wysyłamy kilka
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        //wrzucamy co wysyłamy
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileListUris);
        Log.d("Dotarłem aż do: ", "shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, backupUris);");
        //typ pliku/ów
        shareIntent.setType("text/*");
        //działamy
        startActivityForResult(Intent.createChooser(shareIntent, "Wyśłij do"), SEND_REQUEST_CODE);
    }

    protected void sendRaportsFiles(){
       File privateRootDir;
        // The path to the "backup" subdirectory
        File backupDir;
        // Array of files in the backups subdirectory
        File[] backupFiles;

        //Log.d("getFilesDir(): ", getFilesDir());
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
            //Log.d("Plik nr:", String.valueOf(i));
            //Log.d("Nazwa pliku: ", backupFiles[i].toString());
            Uri fileUri = null;

            Log.d(getActivity().getPackageName().concat(".").concat("ActivitySettings"), getActivity().getPackageName().concat(".").concat("ActivitySettings"));
            try {
                fileUri = FileProvider.getUriForFile(
                        getActivity(),
                        "pl.vot.dexterix.zliczanieczasuzlecen.fileprovider",
                        backupFiles[i]);//.toURI()));
                //Log.d("czy jestem tu?,", "u?");
                backupUris.add(fileUri);
                //Log.d("czy jestem tu?,", "u?");

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
        startActivity(Intent.createChooser(shareIntent, "Wyśłij do"));
    }

    /*protected void pokazPowiadomienie(String tytul, String opis, String opis2, int notificationId1 ){
        //takie tam powiadominie sobie wrzucamy
        //TODO: wyjaśnić sprawę z powiedomieniami dlaczego 2 albo 3 linie wyświetlają się losowo 2 albo 3
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                //.setSmallIcon(R.drawable.notification_icon)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(tytul)
                .setContentText(opis)
                //.setContentText(opis2)
                .setStyle(new NotificationCompat.BigTextStyle()
                        //.bigText("345"))
                        .bigText(opis2))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //powiadomienie

        //to pokazujemy powiadmomienie
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

        // notificationId is a unique int for each notification that you must define
        int notificationId = notificationId1;
        notificationManager.notify(notificationId, builder.build());
        //to pokazaliśmy
    }*/

    //operacje na plikach
    //tworzenie nowego pliku
    protected void createFile(String mimeType, String fileName, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);

        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, requestCode);

    }
    protected void createFile(String mimeType, String fileName, Context context) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);

        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_SEND_REQUEST_CODE);

    }
    //zapis do pliku: wybieramy plik, który już istnieje
    protected void editDocument() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
        // file browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only text files.
        intent.setType("text/plain");

        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }
    //zapis do pliku, tylko zapis
    protected void alterDocument(Uri uri, String dane, Context context) {
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().
                    openFileDescriptor(uri,"wa");//mode a: dodajemy dane do końca pliku
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            //fileOutputStream.write(("Overwritten by MyCloud at " + System.currentTimeMillis() + "\n").getBytes());
            fileOutputStream.write((dane).getBytes());
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //zapis do pliku, tylko zapis
    protected void alterDocument(Uri uri, String dane) {
        try {
            ParcelFileDescriptor pfd = getActivity().getContentResolver().
                    openFileDescriptor(uri,"wa");//mode a: dodajemy dane do końca pliku
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            //fileOutputStream.write(("Overwritten by MyCloud at " + System.currentTimeMillis() + "\n").getBytes());
            fileOutputStream.write((dane).getBytes());
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //usuwanie plik
    protected void deleteDocument(Uri uri){
        try {
            DocumentsContract.deleteDocument(getActivity().getContentResolver(), uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //czytanie z pliku
    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        inputStream.close();
        //parcelFileDescriptor.close();
        return stringBuilder.toString();
    }
}
