package pl.vot.dexterix.zliczanieczasuzlecen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static pl.vot.dexterix.zliczanieczasuzlecen.MainActivity.pokazPowiadomienie;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "alarm_test_check";

    @Override
    public void onReceive(Context context, Intent intent) {

        String fragmentDoZmiany = intent.getStringExtra("FragmentDoZmiany");
        String tytul = intent.getStringExtra("tytul");
        String opis = intent.getStringExtra("opis");
        String opis2 = intent.getStringExtra("opis2");
        int notificationID1 = intent.getIntExtra("notificationID1", 0);

        pokazPowiadomienie(tytul, opis, opis2, notificationID1, context, fragmentDoZmiany);

        Toast.makeText(context,"AlarmReceiver called", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onReceive: called ");
        Log.d(TAG, tytul + opis + opis2 + notificationID1);
    }
}

