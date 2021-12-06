package pl.vot.dexterix.zliczanieczasuzlecen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.TimeZone;

public class DateTimePicker  implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    EditText _editText;
    private int _day;
    private int _month;
    private int _year;
    private int _hour = 0;
    private int _minute = 0;
    private long _date;
    private Context _context;
    boolean czyTylkoData;

    public DateTimePicker(Context context, int editTextViewID, long dateTimeString, boolean czyTylkoData)
    {
        Activity act = (Activity)context;
        this._editText = (EditText)act.findViewById(editTextViewID);
        this._editText.setOnClickListener(this);
        this._context = context;
        this._date = dateTimeString;
        this.czyTylkoData = czyTylkoData;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _year = year;
        _month = monthOfYear;
        _day = dayOfMonth;
        Log.d("DatePicker", String.valueOf(_year));
        updateDisplay();
    }
    @Override
    public void onClick(View v) {
        java.util.Calendar calendar = java.util.Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(_date);
        View dialogView;
        if (this.czyTylkoData) {
            dialogView = View.inflate(_context, R.layout.date_picker, null);
        } else {
            dialogView = View.inflate(_context, R.layout.date_time_picker, null);
        }
        AlertDialog alertDialog = new AlertDialog.Builder(_context).create();
        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onDateSet(this,);
                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                if (!czyTylkoData) {
                    TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
                    timePicker.setIs24HourView(true);
                    _hour = timePicker.getHour();
                    _minute = timePicker.getMinute();
                }
                _year = datePicker.getYear();
                _month = datePicker.getMonth();
                _day = datePicker.getDayOfMonth();


                Calendar calendar = new GregorianCalendar(_year,
                        _month,
                        _day,
                        _hour,
                        _minute);
                        Log.d("DatePicker", String.valueOf(datePicker.getMonth()));
                        //Log.d("DatePicker", String.valueOf(timePicker.getMinute()));
                        Log.d("DateTimePicker", String.valueOf(calendar.getTimeInMillis()));
                        updateDisplay();

                //time = calendar.getTimeInMillis();
                alertDialog.dismiss();
            }});
        alertDialog.setView(dialogView);
        alertDialog.show();

    }

    // updates the date in the birth date EditText
    private void updateDisplay() {
        String prefixDay = "";
        String prefixMonth = "";
        String prefixHour = "";
        String prefixMinute = "";
        if (_day < 10){
            prefixDay = "0";
        }
        if (_month < 9){
            prefixMonth = "0";
        }
        if (_hour < 10){
            prefixHour = "0";
        }
        if (_minute < 10){
            prefixMinute = "0";
        }
        _editText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(prefixDay).append(_day).append("-").append(prefixMonth).append(_month + 1).append("-").append(_year).append(" ").append(prefixHour).append(_hour).append(":").append(prefixMinute).append(_minute));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        _hour = hourOfDay;
        _minute = minute;
        Log.d("TimePicker", String.valueOf(_minute));
        updateDisplay();
    }
}