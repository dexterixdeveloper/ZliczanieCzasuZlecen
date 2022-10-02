package pl.vot.dexterix.zliczanieczasuzlecen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SQLSynchMyFireBaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String Token_ID = FirebaseMessaging.getInstance().getToken().toString();
        //String Token_ID = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN_ID",Token_ID);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.FCM_Pref), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getResources().getString(R.string.FCM_TOKEN),Token_ID);
        editor.commit();

    }

    public String getActualToken(Context context){
        final String[] token = new String[1];
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Pobieramy token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token[0] = task.getResult();

                        // Log and toast
                        //String msg = getString(token);

                        Log.d("Pobieramy token", token[0]);
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.FCM_Pref), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getResources().getString(R.string.FCM_TOKEN), token[0]);
                        editor.commit();
                        Toast.makeText(context, token[0], Toast.LENGTH_SHORT).show();
                        //return token;
                    }
                });
        return token[0];
    }

    //MessagesSQliteOpenHelper messagesSQliteOpenHelper;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String SYNCHRONIZE_URL = "https://dexterix.vot.pl/zliczazle/synchronizemessages.php";
        Map<String,String> data = remoteMessage.getData();

        daneFirma firma = new daneFirma();
        firma.setId(Integer.valueOf(data.get("_id")));
        firma.setNazwa(data.get("nazwa"));
        firma.setNumer(data.get("numer"));
        firma.setNr_telefonu(Integer.valueOf(data.get("nr_telefonu")));
        firma.setUlica_nr(data.get("ulica_nr"));
        firma.setMiasto(data.get("miasto"));
        firma.setTyp(data.get("typ"));
        firma.setKalendarz_id(Long.parseLong(data.get("kalendarz_id")));
        firma.setUwagi(data.get("uwagi"));
        firma.setPoprzedni_rekord_id(Integer.valueOf(data.get("poprzedni_rekord_id")));
        firma.setPoprzedni_rekord_data_usuniecia(data.get("poprzedni_rekord_data_usuniecia"));
        firma.setPoprzedni_rekord_powod_usuniecia(data.get("poprzedni_rekord_powod_usuniecia"));
        firma.setCzy_widoczny(Integer.valueOf(data.get("czy_widoczny")));

        /*String title= data.get("title");
        String message = data.get("message");
        String name = data.get("name");*/



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Channel_ID", "Firmy channel", importance);
            channel.setDescription("SynchronizeFirmy");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification newMessageNotification = new NotificationCompat.Builder(this, "Channel_ID")
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setContentTitle(firma.getNazwa())
                .setContentText(String.valueOf(firma.getId()))
                .setSound(path)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, newMessageNotification);

        StringRequest SynchronizeMessage = new StringRequest(Request.Method.POST, SYNCHRONIZE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("message_response");
                    int count = 0;
                    while (count < jsonArray.length()){
                        JSONObject jo = jsonArray.getJSONObject(count);
                        SaveMessage(jo);
                        count++;
                    }


                    Toast.makeText(getApplicationContext(),"New Message Recieved",Toast.LENGTH_LONG).show();
                    //sendBroadcast(new Intent(OSQLdaneFirma.UI_SYNCHRONIZE_MESSAGE));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        SQLSynchMySingleton.getmInstance(getApplicationContext()).addToRequestQueue(SynchronizeMessage);


    }

    private void SaveMessage(JSONObject data) throws JSONException {
        daneFirma firma = new daneFirma();
        firma.setId(data.getInt("_id"));
        firma.setNazwa(data.getString("nazwa"));
        firma.setNumer(data.getString("numer"));
        firma.setNr_telefonu(data.getInt("nr_telefonu"));
        firma.setUlica_nr(data.getString("ulica_nr"));
        firma.setMiasto(data.getString("miasto"));
        firma.setTyp(data.getString("typ"));
        firma.setKalendarz_id(data.getLong("kalendarz_id"));
        firma.setUwagi(data.getString("uwagi"));
        firma.setPoprzedni_rekord_id(data.getInt("poprzedni_rekord_id"));
        firma.setPoprzedni_rekord_data_usuniecia(data.getString("poprzedni_rekord_data_usuniecia"));
        firma.setPoprzedni_rekord_powod_usuniecia(data.getString("poprzedni_rekord_powod_usuniecia"));
        firma.setCzy_widoczny(Integer.valueOf(data.getString("czy_widoczny")));

        OSQLdaneFirma da = new OSQLdaneFirma(this);
        da.updateDane(da.contentValues(firma), da.getTableName());
    }
    /*private void SaveMessage(String title,String message){
        messagesSQliteOpenHelper = new MessagesSQliteOpenHelper(getApplicationContext());
        SQLiteDatabase database = messagesSQliteOpenHelper.getWritableDatabase();
        messagesSQliteOpenHelper.SaveMessage(title,message,database);
        messagesSQliteOpenHelper.close();
    }*/
}
