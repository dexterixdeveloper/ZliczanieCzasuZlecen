package pl.vot.dexterix.zliczanieczasuzlecen;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "PowiadomienieZliczanieCzasuZlecen";
    public final FragmentManager fm = getSupportFragmentManager();
    private String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 615;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 616;
    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 617;
    //taki tam test tworzenia pliku

    private static final int CREATE_FILE = 1;

    //ustawienia aplikacji
    private Boolean FirstRun;//czy 1 uruchomienie
        //uprawnienia
    private Boolean AccessCalendar;//czy dostęp do kalendarza
    private Boolean AccessFiles;//czy dostęp do plików zewnętrznych

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //synchronizacja SQL

        //Synchronizacja SQL END

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        createNotificationChannel();
        clickOnFloatingButton();
        getUstawienia();
        if (FirstRun){
            poprosOUprawnienia();
        }
        //toolbar.setTitle("456");
        //sprobujmy zrobic backup na starcie
        //ObslugaSQL osql = new ObslugaSQL(this);
        //Log.d("Katalog: ", "yy");
        //TODO: Właczyć backup
        //osql.zrobKopieBazy("bla", this);
        //koniec prob

        //częćś do uruchomienia fragmentu z powiadomienia

        String fragmentDoZmiany = getIntent().getStringExtra("FragmentDoZmiany");

        //FragmentManager fragmentManager = getSupportFragmentManager();
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // If menuFragment is defined, then this activity was launched with a fragment selection
        if (fragmentDoZmiany != null) {
            Log.d(TAG, "null");
            Log.d("MAin", fragmentDoZmiany);
            // Here we can decide what do to -- perhaps load other parameters from the intent extras such as IDs, etc
            if (fragmentDoZmiany.equals("FragmentZadanie")) {
                Log.d(TAG, "czyzby sie udalo?");
                //FragmentZadanie favoritesFragment = new FragmentZadanie();
                int name = getIntent().getIntExtra("id", 0);
                Bundle bundleDane = new Bundle();
                bundleDane.putInt("id", name);
                bundleDane.putString("FragmentDoZmiany", fragmentDoZmiany);
                Log.d("Main", fragmentDoZmiany);
                //FragmentZadanie fragmentDoZamiany = FragmentZadanie.newInstance(name);
                //fragmentDoZamiany.
                //tutaj jakoś musimy wssadzić poczatkowy fragment
                FragmentZadaniaDoZrobienia fragmentDoZamiany = FragmentZadaniaDoZrobienia.newInstance(name, fragmentDoZmiany);
                //do tąd
                String tagBackStack = "FragmentStart";
                zmianaFragmentu(fragmentDoZamiany, tagBackStack, 0);
                //fragmentTransaction.replace(android.R.id.content, favoritesFragment);
            }
        } else {
            // Activity was not launched with a menuFragment selected -- continue as if this activity was opened from a launcher (for example)
            String tagBackStack = "FragmentStart";
            Log.d(TAG, "chyba się nie udało");
            zmianaFragmentu(new FragmentZadaniaDoZrobienia(), tagBackStack, 0);
            //StandardFragment standardFragment = new StandardFragment();
            //fragmentTransaction.replace(android.R.id.content, standardFragment);
        }

        //testowanie zapisu pliku
        //createFile();
        

    }


    //test na tworzenie pliku w lokalizacji wybranej przez usera
    private void createFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, CREATE_FILE);
    }


    private void getUstawienia(){
        OSQLdaneUstawienia osql = new OSQLdaneUstawienia(this);
        List<daneUstawienia> ustawienia = osql.dajWszystkie();
        if (!ustawienia.isEmpty()) {
            for (daneUstawienia ustawienie : ustawienia) {

                switch (ustawienie.getUstawienie()) {
                    case "FirstRun":
                        FirstRun = Boolean.valueOf(ustawienie.getWartosc());//czy 1 uruchomienie
                        break;
                    case "AccessCalendar":
                        AccessCalendar = Boolean.valueOf(ustawienie.getWartosc());//czy dostęp do kalendarza
                        break;
                    case "AccessFiles":
                        AccessFiles = Boolean.valueOf(ustawienie.getWartosc());//czy dostęp do plików zewnętrznych
                        break;

                }

            }
        }else{
            String[][] danePoczatkowe = {
                    {"FirstRun", "true", "Boolean"},//czy 1 uruchomienie
                    {"AccessCalendar", "false", "Boolean"},//czy dostęp do kalendarza
                    {"AccessFiles", "false", "Boolean"}//czy dostęp do plików zewnętrznych
            };
            for (String[] dana : danePoczatkowe){
                daneUstawienia ustawienie = new daneUstawienia();
                ustawienie.setTypDanych(dana[2]);
                ustawienie.setWartosc(dana[1]);
                ustawienie.setUstawienie(dana[0]);

                osql.dodajDane(ustawienie);
            }
            getUstawienia();
        }
    }

    private void dodajDanePoczatkowe(){

        String[][] danePoczatkowe = {
                {"FirstRun", "true", "Boolean"},//czy 1 uruchomienie
                {"AccessCalendar", "false", "Boolean"},//czy dostęp do kalendarza
                {"AccessFiles", "false", "Boolean"}//czy dostęp do plików zewnętrznych
        };
        for (String[] dana : danePoczatkowe){
            daneUstawienia ustawienie = new daneUstawienia();
            ustawienie.setTypDanych(dana[2]);
            ustawienie.setWartosc(dana[1]);
            ustawienie.setUstawienie(dana[0]);


        }

    }

    private void poprosOUprawnienia() {
        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }*/

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_CALENDAR)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            String tagBackStack = "FragmentSettings";
            zmianaFragmentu(new FragmentSettings(), tagBackStack, 1);
            return true;
        }

        if (id == R.id.action_firmy) {
            String tagBackStack = "FragmentFirmy";
            zmianaFragmentu(new FragmentFirmy(), tagBackStack, 1);
            return true;
        }

        if (id == R.id.action_zadania_archiwalne) {
            String tagBackStack = "FragmentZadaniaArchiwalne";
            zmianaFragmentu(new FragmentZadaniaArchiwalne(), tagBackStack, 1);
            return true;
        }

        if (id == R.id.action_raporty) {
            String tagBackStack = "FragmentRaporty";
            zmianaFragmentu(new FragmentRaporty(), tagBackStack, 1);
            return true;
        }

        if (id == R.id.action_stawki) {
            String tagBackStack = "FragmentStawki";
            zmianaFragmentu(new FragmentStawki(), tagBackStack, 1);
            return true;
        }

        if (id == R.id.action_ustawienia_synchronizacji) {
            String tagBackStack = "FragmentSynchronizacjaDane";
            zmianaFragmentu(new FragmentSynchronizacjaDane(), tagBackStack, 1);
            return true;
        }

        if (id == R.id.action_about) {
            String tagBackStack = "FragmentAbout";
            zmianaFragmentu(new FragmentAbout(), tagBackStack, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void zmianaFragmentu(Fragment fragmencik, String tagBackStack, Integer iCzyBackStack){
        // Begin the transaction
        Log.d("Back2", "2");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //sprawdzamy czy cos juz jest w backstack
        /*FragmentManager fm = getSupportFragmentManager();
        if ((fm.getBackStackEntryCount() < 1) && !(tagBackStack.equals("FragmentStart"))){
            ft.replace(R.id.fragment_container_main, new FragmentZadaniaDoZrobienia(), "FragmentStart");
        }*/

        // Replace the contents of the container with the new fragment
        ft.replace(R.id.fragment_container_main, fragmencik);

        //Log.d(TAG + " zmianaFragmentu ",fragmencik.getTag());
        if (iCzyBackStack > 0) {
            ft.addToBackStack(tagBackStack);
        }
// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();
    }

    protected String getVisibleFragment(FragmentManager fragmentManager){
        //FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        Log.d("Ilość fragmentów: ", String.valueOf(fragments.size()));
        if(fragments != null){
            for(Fragment fragment : fragments){
                //fragment.
                Log.d("fragment1", fragment.toString());
                if(fragment != null && fragment.isVisible()) {
                    Log.d("fragment", String.valueOf(fragment.getTag()));
                    return fragment.getTag();
                }
            }
        }
        return null;
    }

    @Override
    public void onBackPressed(){
        //FragmentManager fm = getSupportFragmentManager();

        //fm.findFragmentByTag("FragmentZadanie");
        //Log.d("MainActivity", getVisibleFragment(fm));

        if (fm.getBackStackEntryCount() > 0) {
            if (("FragmentZadanie").equals(getVisibleFragment(fm)) || ("FragmentFirma").equals(getVisibleFragment(fm))) {

                Log.d("MaaiActivity", "chyba się udało");
                //otwieramy okienko dialogowe
                pokazOkienkoAlertuZeNieZapisalesDanych("Chcę wyjść", "Chcę zapisać","Uwaga", "Nie zapisałeś danych. Czy na pewno chcesz wyjść?", false);


            }else {

                Log.i("MainActivity", "cofamy się do poprzedniego fragmentu");
                //Log.d("MainActivity 3", String.valueOf(wynikGuzika));
                fm.popBackStack();
            }
        } else {
            Log.i("MainActivity", "nic w backstack, calling super");
            pokazOkienkoAlertuZeNieZapisalesDanych("Chcę wyjść", "Zostaję", "Koniec programu?", "Czy chcesz zakończyć program?", true);

                //super.onBackPressed();

        }
    }

    private void pokazOkienkoAlertuZeNieZapisalesDanych(String sPositive, String sNegative, String sTytul, String sWiadomowsc, boolean bCzyKoniec){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

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
                        if (!bCzyKoniec) {
                            fm.popBackStack();
                        }else {
                            finish();
                        }
                        dialog.dismiss();

                        //onBackPressed()..super.onBackPressed();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, sNegative,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

        alertDialog.show();


    }

    private void clickOnFloatingButton() {
        FloatingActionButton fab = findViewById(R.id.floatingActionButtonDodaj);
        fab.setVisibility(View.INVISIBLE);
    }

    //otwieramy kanał dla powiadomień : Andek 8+
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void cancelPowiadomienie(int powiadomienieID, Context context){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(powiadomienieID);
    }

    public static void pokazPowiadomienie(String tytul, String opis, String opis2, int notificationId1, Context context, String fragmentDoZmiany){
        //Kombinujemy jak z powiadomienia odpalić formatkę
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("FragmentDoZmiany", fragmentDoZmiany);
        resultIntent.putExtra("id", notificationId1);
        resultIntent.putExtra("NotificationID", notificationId1);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        
        //stackBuilder.add
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //Kombinujemy jak z powiadomienia odpalić formatkę - tu na razie tyle

        //takie tam powiadominie sobie wrzucamy
        //TODO: wyjaśnić sprawę z powiedomieniami dlaczego 2 albo 3 linie wyświetlają się losowo 2 albo 3
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setContentIntent(resultPendingIntent);
                //.setSmallIcon(R.drawable.notification_icon)
                builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(tytul)
                .setContentText(opis)
                        .setLights(Color.BLUE, 500, 500)
                .setSound(alarmSound)
                //.setContentText(opis2)
                //.setLargeIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle()
                        //.bigText("345"))
                        .bigText(opis2))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //powiadomienie

        //to pokazujemy powiadmomienie
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        int notificationId = notificationId1;
        notificationManager.notify(notificationId, builder.build());
        //to pokazaliśmy
    }
    //to przerabiamy

}