package com.example.admin.travellog_ver30.travel;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyDatePickerFragment extends DialogFragment {

    public interface OnMyListener{
        void onReceivedData(int year, int month, int day);
    }

    private OnMyListener mOnMyListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnMyListener = (OnMyListener) getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    Calendar newDate = Calendar.getInstance();
                    final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                    newDate.set(year, month, year);
                    System.out.print(dateFormatter.format(newDate.getTime()));
                    if(mOnMyListener != null) {
                        mOnMyListener.onReceivedData(year, month+1, day);
                    }

//                    Toast.makeText(getActivity(), "selected date is " + view.getYear() +
//                            " / " + (view.getMonth()+1) +
//                            " / " + view.getDayOfMonth(), Toast.LENGTH_SHORT).show();

                    /*String y = String.valueOf(year);
                    String m = String.valueOf(month);
                    String d = String.valueOf(day);
                    System.out.println(y+"년 "+m+"월 "+d+"일");*/

                }

               /* public void getDate(DatePicker view, int year, int month, int day){
                    toString(view.getYear() +
                            " / " + (view.getMonth()+1) +
                            " / " + view.getDayOfMonth());
                }*/
            };


/*
    private void datePicker(){
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                System.out.print(dateFormatter.format(newDate.getTime()));
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }*/



}