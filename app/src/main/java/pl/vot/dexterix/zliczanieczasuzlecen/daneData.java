package pl.vot.dexterix.zliczanieczasuzlecen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class daneData {
    private SimpleDateFormat dataCzasSDF = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private long dataMilisekundy;

    public long getDataMilisekundy() {
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

    private String dataString;
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
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }//private void konwertujDateZeStringa(){

    public long getDateFromString(String przekazanaData){
        try {
            Date dataCzas = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH).parse(przekazanaData);
            Long dataMilisekundy1 = dataCzas.getTime();
            this.setDataMilisekundy(dataMilisekundy1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dataMilisekundy;
    }//private void konwertujDateZeStringa(){
}
