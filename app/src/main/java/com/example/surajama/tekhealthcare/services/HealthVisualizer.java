package com.example.surajama.tekhealthcare.services;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.sql.Date;
import java.util.Calendar;
import java.text.*;

import com.example.surajama.tekhealthcare.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.ArrayList;

public class HealthVisualizer extends Activity {

    private PieChart mChart;
    private LineChart lChart,bpChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_visualizer);

        TextView tv1 = (TextView) findViewById(R.id.tv1);
        TextView tv2 = (TextView) findViewById(R.id.tv2);
        TextView tv3 = (TextView) findViewById(R.id.tv3);

        tv1.setPaintFlags(tv1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv2.setPaintFlags(tv2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv3.setPaintFlags(tv3.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM");
        String[] Days = new String[7];
        for(int i=0;i<7;i++)
        {
            Days[i] = dateFormat.format(cal.getTime());
            cal.add(Calendar.DATE, -1);

        }
        // Configure Steps Activity - Pie Chart--------------------------------------------------------------------------------------------------------

        mChart = (PieChart) findViewById(R.id.pieChart);
        mChart.setBackgroundColor(Color.TRANSPARENT);
        mChart.setExtraOffsets(8,10,5,5);
        mChart.setDragDecelerationFrictionCoef(0.99f);
        mChart.getDescription().setEnabled(true);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.TRANSPARENT);
        mChart.setTransparentCircleRadius(61f);
        mChart.setMaxAngle(360);
        mChart.setRotationAngle(180);
        setData();


        // Configure Heart Health Chart------------------------------------------------------------------------------------------------------------------

        lChart = (LineChart) findViewById(R.id.lineChart);
        lChart.setDragEnabled(true);
        lChart.setScaleEnabled(false);
        lChart.setScaleMinima(0f,0f);
        lChart.fitScreen();

        LimitLine upper_limit = new LimitLine(85f, "DANGER LEVEL");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f,10f,10f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(15f);

        LimitLine lower_limit = new LimitLine(55f, "Too Low");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f,10f,10f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(15f);

        YAxis leftAxis = lChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upper_limit);
        leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaximum(120);
        leftAxis.setAxisMinimum(20);
        leftAxis.enableGridDashedLine(10f,10f,0);
        leftAxis.setDrawLimitLinesBehindData(true);

        lChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> lvalues = new ArrayList<>();

        lvalues.add(new Entry(0,67));
        lvalues.add(new Entry(1,87));
        lvalues.add(new Entry(2,57));
        lvalues.add(new Entry(3,102));
        lvalues.add(new Entry(4,80));
        lvalues.add(new Entry(5,69));
        lvalues.add(new Entry(6,74));

        LineDataSet set1 = new LineDataSet(lvalues,"Heart Rate");
        set1.setFillAlpha(110);
        set1.setColor(Color.BLACK);
        set1.setLineWidth(3);
        set1.setValueTextColor(Color.BLACK);
        set1.setValueTextSize(14);
        set1.setCircleColor(Color.GREEN);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data =  new LineData(dataSets);
        lChart.setData(data);

        String[] xvalues = Days;
        XAxis xAxis = lChart.getXAxis();
        xAxis.setValueFormatter(new myAxisFormatter(xvalues));


        // Configure Blood Pressure Chart-----------------------------------------------------------------------------------------------------

        bpChart = (LineChart) findViewById(R.id.BpChart);
        bpChart.setDragEnabled(true);
        bpChart.setScaleEnabled(false);
        bpChart.setScaleMinima(0f,0f);
        bpChart.fitScreen();

        LimitLine upper_limit1 = new LimitLine(120f, "High B.P");
        upper_limit1.setLineWidth(6f);
        upper_limit1.enableDashedLine(10f,10f,10f);
        upper_limit1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit1.setTextSize(15f);
        upper_limit1.setLineWidth(5f);


        LimitLine lower_limit1 = new LimitLine(80f, "Low B.P");
        lower_limit1.setLineWidth(4f);
        lower_limit1.enableDashedLine(10f,10f,10f);
        lower_limit1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit1.setTextSize(15f);

        YAxis leftAxis1 = bpChart.getAxisLeft();
        leftAxis1.removeAllLimitLines();
        leftAxis1.addLimitLine(upper_limit1);
        leftAxis1.addLimitLine(lower_limit1);
        leftAxis1.setAxisMaximum(200);
        leftAxis1.setAxisMinimum(20);
        leftAxis1.enableGridDashedLine(10f,10f,0);
        leftAxis1.setDrawLimitLinesBehindData(true);

        bpChart.getAxisRight().setEnabled(false);

        // Systolic pressure values
        ArrayList<Entry> spvalues = new ArrayList<>();

        spvalues.add(new Entry(0,130));
        spvalues.add(new Entry(1,140));
        spvalues.add(new Entry(2,115));
        spvalues.add(new Entry(3,160));
        spvalues.add(new Entry(4,170));
        spvalues.add(new Entry(5,110));
        spvalues.add(new Entry(6,125));

        LineDataSet spset = new LineDataSet(spvalues,"Systolic Pressure");
        spset.setFillAlpha(110);
        spset.setColor(Color.RED);
        spset.setLineWidth(3);
        spset.setValueTextColor(Color.BLACK);
        spset.setValueTextSize(14);
        spset.setCircleColor(Color.BLACK);
        spset.setCircleColorHole(10);

        ArrayList<ILineDataSet> spdataSets = new ArrayList<>();
        spdataSets.add(spset);


        // Diastolic pressure values
        ArrayList<Entry> dpvalues = new ArrayList<>();

        dpvalues.add(new Entry(0,90));
        dpvalues.add(new Entry(1,100));
        dpvalues.add(new Entry(2,65));
        dpvalues.add(new Entry(3,100));
        dpvalues.add(new Entry(4,110));
        dpvalues.add(new Entry(5,82));
        dpvalues.add(new Entry(6,78));

        LineDataSet dpset = new LineDataSet(dpvalues,"Diastolic Pressure");
        dpset.setFillAlpha(110);
        dpset.setColor(Color.BLACK);
        dpset.setLineWidth(3);
        dpset.setValueTextColor(Color.BLACK);
        dpset.setValueTextSize(14);
        dpset.setCircleColor(Color.RED);
        spset.setCircleColorHole(20);

        spdataSets.add(dpset);

        LineData bpdata =  new LineData(spdataSets);
        bpChart.setData(bpdata);

        String[] xvalues2 = Days;
        XAxis xAxis2 = bpChart.getXAxis();
        xAxis2.setValueFormatter(new myAxisFormatter(xvalues2));

    }

    private void setData() {
        ArrayList<PieEntry> values = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM");
        String[] Days = new String[7];
        for(int i=0;i<7;i++)
        {
            Days[i] = dateFormat.format(cal.getTime());
            cal.add(Calendar.DATE, -1);

        }

        values.add(new PieEntry(1.19f, Days[0]));
        values.add(new PieEntry(2.34f, Days[1]));
        values.add(new PieEntry(5.36f, Days[2]));
        values.add(new PieEntry(1.6f,  Days[3]));
        values.add(new PieEntry(2.72f, Days[4]));
        values.add(new PieEntry(2.91f, Days[5]));
        values.add(new PieEntry(2.98f, Days[6]));

        PieDataSet dataSet = new PieDataSet(values, "Days");
        //dataSet.setSelectionShift(5f);

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);


        Description description = new Description();
        description.setText("Day wise - Distance walked by you in km.");
        description.setTextSize(20);
        description.setTextColor(Color.BLACK);

        mChart.setDescription(description);
        mChart.animateY(2000);
        mChart.getLegend().setTextColor(Color.BLACK);



        PieData data = new PieData(dataSet);

        data.setValueTextSize(15f);
        // data.setValueTextColor(Color.BLACK);

        mChart.setData(data);
        // mChart.invalidate();
    }


}

class myAxisFormatter implements IAxisValueFormatter
{
    private String[] mValues;
    public myAxisFormatter(String[] values)
    {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mValues[(int)value];
    }
}

