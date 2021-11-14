package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentPodstawowy extends Fragment {
    private static final String CHANNEL_ID = "PowiadomienieZliczanieCzasuZlecen";
    private static final String TAG = FragmentPodstawowy.class.getSimpleName();

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
}
