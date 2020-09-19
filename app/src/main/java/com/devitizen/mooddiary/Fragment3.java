package com.devitizen.mooddiary;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Creates the Stat menu which has three kinds of statistical charts.
 * For the charts, an external library (com.github.mikephil.charting) is utilized.
 * To display the charts, some data should be called in the SQLiteDatabase.
 */
public class Fragment3 extends Fragment implements OnBackPressedListener {

    private static final String TAG = "Fragment3";

    private MainActivity mainActivity;

    private PieChart chart;
    private BarChart chart2;
    private LineChart chart3;

    private Context context;

    /**
     * Creates views
     *
     * @param inflater           inflater
     * @param container          container
     * @param savedInstanceState saved instance state
     * @return view group
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);

        init(viewGroup);    // creates views
        loadStatData();     // loads data

        return viewGroup;
    }

    /**
     * Attaches to the activity
     *
     * @param context context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * Detaches from the activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if (context != null) {
            context = null;
        }
    }

    /**
     * Resumes on the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        mainActivity.setOnBackPressedListener(this);
    }

    /**
     * Executes when back button is pressed
     */
    @Override
    public void onBackButtonPressed() {
        mainActivity.onMenuSelected(1);
    }

    /**
     * Creates views
     *
     * @param viewGroup view group
     */
    private void init(ViewGroup viewGroup) {
        mainActivity = (MainActivity) getActivity();

        //****************************************************//
        // sets for Ratio by Mood (1st graph)
        chart = viewGroup.findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setUsePercentValues(true);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58.0f);
        chart.setTransparentCircleRadius(61.0f);
        chart.setDrawCenterText(true);
        chart.setHighlightPerTapEnabled(true);
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(10.0f);

        Legend legend1 = chart.getLegend();
        legend1.setEnabled(false);

        //****************************************************//
        // sets for Mood by Days (2nd graph)
        chart2 = viewGroup.findViewById(R.id.chart2);
        chart2.getDescription().setEnabled(false);
        chart2.setDrawGridBackground(false);

        Legend legend2 = chart2.getLegend();
        legend2.setEnabled(false);

        XAxis xAxis = chart2.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12.0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value <= 0.0f) {
                    return "Sun";
                } else if (value <= 1.0f) {
                    return "Mon";
                } else if (value <= 2.0f) {
                    return "Tue";
                } else if (value <= 3.0f) {
                    return "Wed";
                } else if (value <= 4.0f) {
                    return "Thu";
                } else if (value <= 5.0f) {
                    return "Fri";
                } else if (value <= 6.0f) {
                    return "Sat";
                } else

                    return "";
            }
        });

        YAxis leftAxis2 = chart2.getAxisLeft();
        leftAxis2.setLabelCount(6, false);
        leftAxis2.setAxisMinimum(0.0f);
        leftAxis2.setAxisMaximum(5.0f);
        leftAxis2.setGranularity(1.0f);
        leftAxis2.setTextSize(12.0f);

        YAxis rightAxis2 = chart2.getAxisRight();
        rightAxis2.setEnabled(false);

        chart2.animateXY(1500, 1500);

        //****************************************************//
        // sets for Mood Changes (3rd graph)
        chart3 = viewGroup.findViewById(R.id.chart3);
        chart3.getDescription().setEnabled(false);

        Legend legend3 = chart3.getLegend();
        legend3.setEnabled(false);

        XAxis xAxis3 = chart3.getXAxis();
        xAxis3.setDrawGridLines(false);
        xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis3.setTextSize(11.0f);
        xAxis3.setLabelCount(7, true);
        xAxis3.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int days = (int) ((value / 24.0f));
                Date todayDate = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(todayDate);
                cal.add(Calendar.DATE, (days));

                return AppConstants.dateFormat3.format(cal.getTime());
            }
        });

        YAxis leftAxis3 = chart3.getAxisLeft();
        leftAxis3.setAxisMinimum(0.0f);
        leftAxis3.setAxisMaximum(5.0f);
        leftAxis3.setGranularity(1.0f);
        leftAxis3.setTextSize(12.0f);

        YAxis rightAxis3 = chart3.getAxisRight();
        rightAxis3.setEnabled(false);

    }

    /**
     * Sets chart 1
     *
     * @param dataHash1 HashMap parameter
     */
    private void setData1(HashMap<String, Integer> dataHash1) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        String[] keys = {"0", "1", "2", "3", "4"};
        int[] icons = {R.drawable.smile1_24, R.drawable.smile2_24, R.drawable.smile3_24,
                R.drawable.smile4_24, R.drawable.smile5_24};
        for (int i = 0; i < keys.length; i++) {
            int value = 0;
            Integer outValue = dataHash1.get(keys[i]);
            if (outValue != null) {
                value = outValue.intValue();
            }
            if (value > 0) {
                entries.add(new PieEntry(value, "", getResources().getDrawable(icons[i])));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, getResources().getString(R.string.graph1_title));
        dataSet.setDrawIcons(true);
        dataSet.setSliceSpace(3.0f);
        dataSet.setIconsOffset(new MPPointF(0, -40));
        dataSet.setSelectionShift(5.0f);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int i : AppConstants.graphColor) {
            colors.add(i);
        }
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14.0f);

        chart.setData(data);
        chart.invalidate();
    }

    /**
     * Sets chart 2
     *
     * @param dataHash2 HashMap parameter
     */
    private void setData2(HashMap<String, Float> dataHash2) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        String[] keys = {"0", "1", "2", "3", "4", "5", "6"};
        for (int i = 0; i < keys.length; i++) {
            float value = 0.0f;
            Float outValue = dataHash2.get(keys[i]);
            println("#" + i + " -> " + outValue);

            if (outValue != null) {
                value = outValue;
            }

            // i : 0 ~ 6 means Sun ~ Sat
            entries.add(new BarEntry(i, value));
        }

        BarDataSet barDataSet = new BarDataSet(entries, getResources().getString(R.string.graph2_title));

        ArrayList<Integer> colors = new ArrayList<>();
        for (int i : AppConstants.graphColor) {
            colors.add(i);
        }
        barDataSet.setColors(colors);

        final BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(14.0f);
        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex,
                                            ViewPortHandler viewPortHandler) {
                return hideZeroValue(value);
            }
        });
        barData.setBarWidth(0.6f);

        chart2.setData(barData);
        chart2.invalidate();
    }

    /**
     * Sets Chart 3
     *
     * @param dataKeys3   keys
     * @param dataValues3 values
     */
    private void setData3(ArrayList<Float> dataKeys3, ArrayList<Float> dataValues3) {
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < dataKeys3.size(); i++) {
            try {
                float outKey = dataKeys3.get(i);
                float outValue = dataValues3.get(i);

                println("#" + i + " -> " + outKey + ", " + outValue);

                values.add(new Entry(outKey, new Float(outValue)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LineDataSet lineDataSet = new LineDataSet(values, getResources().getString(R.string.graph3_title));
        lineDataSet.setLineWidth(4.0f);
        lineDataSet.setColors(AppConstants.graphColor[4]);
        lineDataSet.setCircleRadius(8.0f);
        lineDataSet.setCircleColors(AppConstants.graphColor[4]);

        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        final LineData lineData = new LineData(iLineDataSets);

        lineData.setValueTextSize(14.0f);
        lineData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex,
                                            ViewPortHandler viewPortHandler) {
                return hideZeroValue(value);
            }
        });

        chart3.setData(lineData);
        chart3.invalidate();
    }

    /**
     * Hides value with zero in the chart
     *
     * @param value number value
     * @return string value
     */
    public String hideZeroValue(float value) {
        if (value == 0.0f) {
            return "";
        }
        return String.valueOf(value);
    }


    /**
     * Fetches data from database
     */
    public void loadStatData() {
        NoteDatabase database = NoteDatabase.getInstance(context);

        //****************************************************//
        // Ratio by Mood (1st graph)
        String sql = "select mood, count(mood) from "
                + AppConstants.TABLE_NOTE
                + " where create_date > '" + getMonthBefore(1) + "' "
                + " and create_date < '" + getTomorrow() + "' "
                + " group by mood";
        println("Ratio by Mood : " + sql);

        Cursor cursor = database.rawQuery(sql);
        int recordCount = cursor.getCount();

        HashMap<String, Integer> dataHash1 = new HashMap<>();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String moodName = cursor.getString(0);
            int moodCount = cursor.getInt(1);
            println("#" + i + " -> " + moodName + ", " + moodCount);

            dataHash1.put(moodName, moodCount);
        }

        setData1(dataHash1);

        //****************************************************//
        // Mood by Days (2nd graph)

        // calculates average score (1 to 5) while the stored score band is 0 to 4
        String avgMoodScore = "round( round(sum(mood)+count(mood), 3) / count(mood) , 1)";

        sql = "select strftime('%w', create_date) , " + avgMoodScore + " from "
                + AppConstants.TABLE_NOTE
                + " where create_date > '" + getMonthBefore(1) + "' "
                + " and create_date < '" + getTomorrow() + "' "
                + " group by strftime('%w', create_date)";
        println("Mood by Days : " + sql);

        cursor = database.rawQuery(sql);
        recordCount = cursor.getCount();
        HashMap<String, Float> dataHash2 = new HashMap<>();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();

            String weekday = cursor.getString(0);
            float moodCount = cursor.getFloat(1);

            println("#" + i + " -> " + weekday + ", " + moodCount);
            dataHash2.put(weekday, moodCount);
        }

        setData2(dataHash2);

        //****************************************************//
        // Mood Changes (3rd graph)
        sql = "select strftime('%Y-%m-%d', create_date) , " + avgMoodScore + " from "
                + AppConstants.TABLE_NOTE
                + " where create_date > '" + getDayBefore(7) + "' "
                + " and create_date < '" + getTomorrow() + "' "
                + " group by strftime('%Y-%m-%d', create_date) ";
        println("Mood Changes : " + sql);

        cursor = database.rawQuery(sql);
        recordCount = cursor.getCount();

        HashMap<String, Float> dataHash3 = new HashMap<>();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String monthDate = cursor.getString(0);
            float moodCount = cursor.getFloat(1);
            println("#" + i + " -> " + monthDate + ", " + moodCount);

            dataHash3.put(monthDate, moodCount);
        }

        ArrayList<Float> dataKeys3 = new ArrayList<>();
        ArrayList<Float> dataValues3 = new ArrayList<>();

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -7);

        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            String monthDate = AppConstants.dateFormat5.format(cal.getTime());
            Object moodCount = dataHash3.get(monthDate);

            dataKeys3.add((i - 6) * 24.0f);
            if (moodCount == null) {
                dataValues3.add(0.0f);
            } else {
                dataValues3.add((Float) moodCount);
            }
            println("#" + i + " -> " + monthDate + ", " + moodCount);
        }

        setData3(dataKeys3, dataValues3);
    }

    /**
     * Gets date of tomorrow
     *
     * @return date of tomorrow
     */
    public String getTomorrow() {
        Date todayDate = new Date();
        Calendar cal = Calendar.getInstance();

        cal.setTime(todayDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);

        return AppConstants.dateFormat5.format(cal.getTime());
    }

    /**
     * Gets the date days ago
     *
     * @param period how many days ago
     * @return date of past
     */
    public String getDayBefore(int period) {
        Date todayDate = new Date();
        Calendar cal = Calendar.getInstance();

        cal.setTime(todayDate);
        cal.add(Calendar.DAY_OF_MONTH, (period * -1));

        return AppConstants.dateFormat5.format(cal.getTime());
    }

    /**
     * Gets the date months ago
     *
     * @param period how many months ago
     * @return date of past
     */
    public String getMonthBefore(int period) {
        Date todayDate = new Date();
        Calendar cal = Calendar.getInstance();

        cal.setTime(todayDate);
        cal.add(Calendar.MONTH, (period * -1));

        return AppConstants.dateFormat5.format(cal.getTime());
    }

    /**
     * Prints log
     *
     * @param msg input message
     */
    public void println(String msg) {
        Log.d(TAG, msg);
    }

}
