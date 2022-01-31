package pl.vot.dexterix.zliczanieczasuzlecen;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.icu.util.Calendar.DAY_OF_YEAR;

public class daneData {
    private SimpleDateFormat dataCzasSDF = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private SimpleDateFormat dataSDF = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat czasSDF = new SimpleDateFormat("HH:mm");

    private long dataMilisekundy;
    private String dataString;

    public long getDataMilisekundy() {
        return dataMilisekundy;
    }

    public long getDataMilisekundy(String zaokraglenie) {
        if (zaokraglenie.equals("dol")){
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            calendar.setTimeInMillis(this.dataMilisekundy);
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            this.setDataMilisekundy(calendar.getTimeInMillis());
        }else if(zaokraglenie.equals("gora")){
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            calendar.setTimeInMillis(this.dataMilisekundy);
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            this.setDataMilisekundy(calendar.getTimeInMillis());
        }
        return dataMilisekundy;
    }

    public void setDataMilisekundy(long dataMilisekundy) {
        this.dataMilisekundy = dataMilisekundy;
        this.dataString = this.dataCzasSDF.format(this.dataMilisekundy);
    }
    public String getDataMilisecondsToString(long dataMilisekundy){
        this.dataMilisekundy = dataMilisekundy;
        this.dataString = this.dataCzasSDF.format(this.dataMilisekundy);
        return dataString;
    }

    public String getDataString() {
        //SimpleDateFormat dataCzasSDF = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        //dataString = this.dataCzasSDF.format(this.dataMilisekundy);
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
        konwertujDateZeStringa(dataString);
    }

    public long getAktualnaData(){
        String aktualnaData = "";
        //textInputEditTextDataTankowania.setText(String.valueOf(aktualnaData));
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Long calendarDataWMiliSekundach = calendar.getTimeInMillis();
        //calendarT

        //SimpleDateFormat dataCzasSDF = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        aktualnaData = this.dataCzasSDF.format(calendar.getTime());
        //classData aktualnaDataWylot = new classData();
        this.setDataMilisekundy(calendarDataWMiliSekundach);
        this.setDataString(aktualnaData);
        return this.dataMilisekundy;
    }

    public void podajDate(){
        String aktualnaData = "";
        //textInputEditTextDataTankowania.setText(String.valueOf(aktualnaData));
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Long calendarDataWMiliSekundach = calendar.getTimeInMillis();
        //calendarT

        //SimpleDateFormat dataCzasSDF = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        aktualnaData = this.dataCzasSDF.format(calendar.getTime());
        //classData aktualnaDataWylot = new classData();
        this.setDataMilisekundy(calendarDataWMiliSekundach);
        this.setDataString(aktualnaData);

    }

    public void konwertujDateZeStringa(String przekazanaData){
        try {
            Date dataCzas = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH).parse(przekazanaData);
            Long dataMilisekundy1 = dataCzas.getTime();
            this.setDataMilisekundy(dataMilisekundy1);
            //this.setDataString(przekazanaData);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }//private void konwertujDateZeStringa(){

    public long getDateFromString(String przekazanaData){
        try {
            Date dataCzas = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH).parse(przekazanaData);
            Long dataMilisekundy1 = dataCzas.getTime();
            Log.d("daneData", String.valueOf(dataMilisekundy1));
            Log.d("danaeData", String.valueOf(this.dataMilisekundy));
            this.setDataMilisekundy(dataMilisekundy1);
            Log.d("daneData", String.valueOf(getDataMilisekundy()));
            Log.d("danaeData", String.valueOf(this.dataMilisekundy));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dataMilisekundy;
    }//private void konwertujDateZeStringa(){

    public void setGodzina(Long dataPoczatkowa, String czas, boolean przesuniecieDnia){
        int godzina = Integer.parseInt(czas.substring(0, czas.indexOf(":")));
        //Log.d("dataCzas Godzina: ", String.valueOf(godzina));
        int minuta = Integer.parseInt(czas.substring(czas.indexOf(":") + 1, czas.length()));
        //Log.d("dataCzas Minuta: ", String.valueOf(minuta));
        this.setDataMilisekundy(dataPoczatkowa);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(this.dataMilisekundy);
        if(przesuniecieDnia){
            calendar.set(DAY_OF_YEAR, calendar.get(DAY_OF_YEAR) + 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, godzina);
        calendar.set(Calendar.MINUTE, minuta);
        //calendar.set(Calendar.HOUR_OF_DAY, 12);
        this.setDataMilisekundy(calendar.getTimeInMillis());
        //Log.d("dataCzas kompletna data: ", this.dataString);
        //return this.dataMilisekundy;
    }

    public long getPoczatekRoku() {
        Log.d("danaeData", String.valueOf(this.dataMilisekundy));
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(this.dataMilisekundy);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.setDataMilisekundy(calendar.getTimeInMillis());

        return dataMilisekundy;
    }

    public long getKonieckRoku() {
        Log.d("danaeData", String.valueOf(this.dataMilisekundy));
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(this.dataMilisekundy);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.setDataMilisekundy(calendar.getTimeInMillis());

        return dataMilisekundy;
    }

    public long getPoczatekMiesiaca() {
        Log.d("danaeData", String.valueOf(this.dataMilisekundy));
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(this.dataMilisekundy);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.setDataMilisekundy(calendar.getTimeInMillis());

        return dataMilisekundy;
    }

    public Long getKoniecMiesiaca() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(this.dataMilisekundy);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.setDataMilisekundy(calendar.getTimeInMillis());

        return dataMilisekundy;
    }

    public Long getPoczatekDnia() {
        Log.d("danaeData", String.valueOf(this.dataMilisekundy));
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Log.d("1", String.valueOf(calendar.getTimeInMillis()));
        calendar.setTimeInMillis(this.dataMilisekundy);
        Log.d("2", String.valueOf(calendar.getTimeInMillis()));
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR));
        Log.d("3", String.valueOf(calendar.getTimeInMillis()));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        Log.d("4", String.valueOf(calendar.getTimeInMillis()));
        calendar.set(Calendar.MINUTE, 0);
        Log.d("5", String.valueOf(calendar.getTimeInMillis()));
        calendar.set(Calendar.SECOND, 0);
        Log.d("6", String.valueOf(calendar.getTimeInMillis()));
        calendar.set(Calendar.MILLISECOND, 0);
        Log.d("7", String.valueOf(calendar.getTimeInMillis()));
        this.setDataMilisekundy(calendar.getTimeInMillis());

        return dataMilisekundy;
    }

    public Long getKoniecDnia() {
        Log.d("danaeData", String.valueOf(this.dataMilisekundy));
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(this.dataMilisekundy);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR)+1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.setDataMilisekundy(calendar.getTimeInMillis());

        return dataMilisekundy;
    }
}
