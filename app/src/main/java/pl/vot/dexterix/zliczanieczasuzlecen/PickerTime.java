package pl.vot.dexterix.zliczanieczasuzlecen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

public class PickerTime implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    EditText _editText;

    private int _hour;
    private int _minute;

    private Context _context;

    public PickerTime(Context context, int editTextViewID)
    {
        Activity act = (Activity)context;

        this._editText = (EditText)act.findViewById(editTextViewID);
        this._editText.setOnClickListener(this);
        this._context = context;


    }

    @Override
    public void onClick(View v) {
        View dialogView = View.inflate(_context, R.layout.picker_time, null);
        AlertDialog alertDialog = new AlertDialog.Builder(_context).create();
        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        dialogView.findViewById(R.id.buttonSetTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onDateSet(this,);

                _hour = timePicker.getHour();
                _minute = timePicker.getMinute();
                updateDisplay();

                //time = calendar.getTimeInMillis();
                alertDialog.dismiss();
            }});
        alertDialog.setView(dialogView);
        alertDialog.show();

    }

    // updates the date in the birth date EditText
    private void updateDisplay() {
        StringBuilder _time = new StringBuilder();
        if (_hour < 10){
            _time.append("0").append(_hour).append(":");
        }else{
            _time.append(_hour).append(":");
        }
        if(_minute < 10){
            _time.append("0").append(_minute);
        }else{
            _time.append(_minute);
        }
        _editText.setText(_time);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        _hour = hourOfDay;
        _minute = minute;
        Log.d("TimePicker", String.valueOf(_minute));
        updateDisplay();
    }
}