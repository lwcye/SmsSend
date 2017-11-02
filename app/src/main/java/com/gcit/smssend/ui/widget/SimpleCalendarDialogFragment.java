package com.gcit.smssend.ui.widget;

/**
 * <p>describe</p><br>
 *
 * @author - lwc
 * @date - 2017/11/2
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;
import com.gcit.smssend.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SimpleCalendarDialogFragment extends AppCompatDialogFragment implements OnDateSelectedListener {
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    public long mSelectDate;
    private Dialog mDialog;
    private OnSureListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_basic, null);
        MaterialCalendarView widget = (MaterialCalendarView) view.findViewById(R.id.calendarView_dialog_basic);

        widget.setOnDateChangedListener(this);
        widget.state().edit()
                .setMaximumDate(TimeUtils.getNowDate())
                .commit();
        widget.setShowOtherDates(MaterialCalendarView.SHOW_DECORATED_DISABLED | MaterialCalendarView.SHOW_OUT_OF_RANGE);

        long day = System.currentTimeMillis() % TimeConstants.DAY + 8 * TimeConstants.HOUR;
        mSelectDate = System.currentTimeMillis() - day;
        mDialog = new AlertDialog.Builder(getActivity())
                .setTitle("请选择日期")
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.sureListener(mSelectDate);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        long day = System.currentTimeMillis() % TimeConstants.DAY + 8 * TimeConstants.HOUR;
                        mSelectDate = System.currentTimeMillis() - day;
                        mListener.sureListener(mSelectDate);
                    }
                })
                .create();
        return mDialog;
    }

    /**
     * @param listener 监听确定按钮
     */
    public void setListener(OnSureListener listener) {
        mListener = listener;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        mSelectDate = date.getCalendar().getTimeInMillis();
        mDialog.setTitle(FORMATTER.format(date.getDate()));
    }

    public interface OnSureListener {
        void sureListener(long date);
    }
}