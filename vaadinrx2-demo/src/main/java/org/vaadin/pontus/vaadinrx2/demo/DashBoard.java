package org.vaadin.pontus.vaadinrx2.demo;

import java.util.Optional;

import org.vaadin.pontus.vaadinrx2.RxDataProvider;
import org.vaadin.pontus.vaadinrx2.VaadinRx2;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisTitle;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataProviderSeries;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotBand;
import com.vaadin.addon.charts.model.PlotLine;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsGauge;
import com.vaadin.addon.charts.model.SeriesTooltip;
import com.vaadin.addon.charts.model.TickPosition;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import io.reactivex.Observable;

/**
 * Demo dashboard to illustrate the use of RxJava 2 with Vaadin 8. The dashboard
 * visualizes a single data stream with a double single value. The stream
 * consists of randomly generated values between 0 and 1. 20 values are
 * generated when the user clicks start.
 * 
 * The point of the demo is to demonstrate how easy it is to hook up data
 * streams to charts and various other UI components. Vaadin Push is here
 * essential to ensure that updates take effect immediately on the client side
 * also.
 * 
 * @author Pontus Bostrom
 *
 */
@SuppressWarnings("serial")
public class DashBoard extends CustomComponent {
    
    Chart lineChart;
    Chart columnChart;
    Chart gaugeChart;
    Grid<TimeData> grid;
    TextField timeField;
    TextField valueField;
    TextField avgField;
    ComboBox<String> dataSourceSelector;
    Button button;
    DashBoardPresenter presenter;

    public static final String HOT = "Hot data source";
    public static final String COLD = "Cold data source";

    public DashBoard() {
        final VerticalLayout layout = new VerticalLayout();

        Label title = new Label("<h1>Mock data Inc. Dashboard</h1>",
                ContentMode.HTML);
        layout.addComponent(title);
        // Create the tool bar
        HorizontalLayout toolbar = new HorizontalLayout();
        dataSourceSelector = new ComboBox<>();
        dataSourceSelector.setItems(HOT, COLD);
        dataSourceSelector.setSelectedItem(COLD);
        dataSourceSelector.setEmptySelectionAllowed(false);
        button = new Button("Start collecting");
        Button stop = new Button("Stop");
        stop.addClickListener(e -> presenter.dispose());

        toolbar.addComponents(dataSourceSelector, button, stop);
        layout.addComponent(toolbar);


        // Create the charts
        HorizontalLayout chartLayout = new HorizontalLayout();

        lineChart = createLineChart();
        columnChart = createColumnChart();
        gaugeChart = createGauge();
        chartLayout.addComponentsAndExpand(lineChart, columnChart, gaugeChart);
        layout.addComponent(chartLayout);

        // Create grid and the textfield for showing data
        grid = createGrid();

        timeField = new TextField("Time (ms)");
        timeField.setReadOnly(true);
        valueField = new TextField("Value");
        valueField.setReadOnly(true);
        avgField = new TextField("Moving average");
        avgField.setReadOnly(true);
        VerticalLayout labelLayout = new VerticalLayout();
        labelLayout.addComponents(new Label("Current values"), timeField,
                valueField, avgField);
        HorizontalLayout textCompLayout = new HorizontalLayout();
        textCompLayout.addComponents(grid, labelLayout);
        layout.addComponent(textCompLayout);
        setCompositionRoot(layout);

        presenter = new DashBoardPresenter(this);

    }
    
    void connectCharts1(RxDataProvider<TimeData> chartProvider) {

        DataProviderSeries<TimeData> vSeries = new DataProviderSeries<TimeData>(
                chartProvider);
        vSeries.setY(TimeData::getValue);
        vSeries.setX(TimeData::getTime);
        vSeries.setName("Values");

        DataProviderSeries<TimeData> aSeries = new DataProviderSeries<TimeData>(
                chartProvider);
        aSeries.setY(TimeData::getAvg);
        aSeries.setX(TimeData::getTime);
        aSeries.setName("Moving average");

        lineChart.getConfiguration().addSeries(vSeries);
        lineChart.getConfiguration().addSeries(aSeries);
        lineChart.drawChart();
    }

    void connectCharts2(RxDataProvider<Double> chartProvider) {
        DataProviderSeries<Double> vSeries = new DataProviderSeries<Double>(
                chartProvider);
        vSeries.setName("Values");
        vSeries.setY(v -> v);
        columnChart.getConfiguration().addSeries(vSeries);
        columnChart.drawChart();
    }

    void connectCharts3(RxDataProvider<Double> chartProvider) {
        DataProviderSeries<Double> vSeries = new DataProviderSeries<Double>(
                chartProvider);
        vSeries.setName("Values");
        vSeries.setY(v -> v);
        PlotOptionsGauge plotOptions = new PlotOptionsGauge();
        plotOptions.setTooltip(new SeriesTooltip());
        vSeries.setPlotOptions(plotOptions);
        gaugeChart.getConfiguration().addSeries(vSeries);
        gaugeChart.drawChart();

    }

    void connectGrid(RxDataProvider<TimeData> gridProvider) {
        grid.setDataProvider(gridProvider);
    }

    void connectLabelsWithBinder(Observable<TimeData> obs) {
        Binder<TimeData> binder = new Binder<>();
        binder.forField(timeField).withConverter((String s) -> {
            return Long.parseLong(s);
        }, v -> String.valueOf(v))
                .bind(TimeData::getTime, (o, v) -> {
                });
        binder.forField(valueField)
                .withConverter(new StringToDoubleConverter("Not a number"))
                .bind(TimeData::getValue, (o, v) -> {
                });

        binder.forField(avgField)
                .withConverter(new StringToDoubleConverter("Not a number"))
                .bind(TimeData::getAvg, (o, v) -> {
                });

        VaadinRx2.subscribeAsynchronously(UI.getCurrent(), obs,
                d -> binder.readBean(d));
    }


    Observable<Optional<String>> registerClickObserver() {
        Observable<ClickEvent> clickObservable = VaadinRx2
                .createFrom(button);
        return clickObservable.map(e -> dataSourceSelector.getSelectedItem());
    }


    protected static Chart createLineChart() {
        final Chart chart = new Chart();
        chart.setHeight("450px");
        chart.setWidth("100%");

        Configuration configuration = chart.getConfiguration();
        configuration.getTitle().setText("Values form data source");

        YAxis yAxis = new YAxis();
        Labels label = new Labels();
        label.setFormatter("this.value");
        yAxis.setLabels(label);
        yAxis.setTitle("Value (unit)");
        XAxis xAxis = new XAxis();
        xAxis.setTitle("Time (ms)");

        PlotLine plotLine = new PlotLine();
        plotLine.setValue(2);
        plotLine.setWidth(2);
        plotLine.setColor(SolidColor.SILVER);
        yAxis.setPlotLines(plotLine);
        configuration.addyAxis(yAxis);
        configuration.addxAxis(xAxis);

        Tooltip tooltip = new Tooltip();
        tooltip.setPointFormat(
                "<span style=\"color:{series.color}\">{series.name}</span>: <b>{point.y}</b> <br/>");
        tooltip.setValueDecimals(2);
        configuration.setTooltip(tooltip);

        chart.drawChart(configuration);
        return chart;
    }

    protected static Chart createColumnChart() {
        Chart chart = new Chart(ChartType.COLUMN);
        chart.setHeight("450px");
        chart.setWidth("100%");

        Configuration configuration = chart.getConfiguration();
        configuration.getTitle()
                .setText("Values form data source (5 last values)");

        XAxis x = new XAxis();
        x.setCategories("v(k-4)", "v(k-3)", "v(k-2)", "v(k-1)", "v(k)");
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        Labels label = new Labels();
        label.setFormatter("this.value");
        y.setLabels(label);
        y.setMin(0);
        y.setTitle("Value (unit)");

        configuration.addyAxis(y);

        Tooltip tooltip = new Tooltip();
        tooltip.setPointFormat(
                "<span style=\"color:{series.color}\">{series.name}</span>: <b>{point.y}</b><br/>");
        tooltip.setValueDecimals(2);
        configuration.setTooltip(tooltip);

        PlotOptionsColumn plot = new PlotOptionsColumn();
        plot.setPointPadding(0.2);
        plot.setBorderWidth(0);
        configuration.addPlotOptions(plot);

        chart.drawChart(configuration);
        return chart;
    }

    protected static Chart createGauge() {
        final Chart chart = new Chart();
        chart.setWidth("500px");

        final Configuration configuration = chart.getConfiguration();
        configuration.getChart().setType(ChartType.GAUGE);
        configuration.getChart().setPlotBackgroundColor(null);
        configuration.getChart().setPlotBackgroundImage(null);
        configuration.getChart().setPlotBorderWidth(0);
        configuration.getChart().setPlotShadow(false);
        configuration.setTitle("Current value");

        configuration.getPane().setStartAngle(-150);
        configuration.getPane().setEndAngle(150);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle(new AxisTitle("Value (unit)"));
        yAxis.setMin(0);
        yAxis.setMax(5);
        yAxis.setMinorTickInterval("auto");
        yAxis.setMinorTickWidth(1);
        yAxis.setMinorTickLength(10);
        yAxis.setMinorTickPosition(TickPosition.INSIDE);
        yAxis.setMinorTickColor(new SolidColor("#666"));
        yAxis.setGridLineWidth(0);
        yAxis.setTickPixelInterval(30);
        yAxis.setTickWidth(2);
        yAxis.setTickPosition(TickPosition.INSIDE);
        yAxis.setTickLength(10);
        yAxis.setTickColor(new SolidColor("#666"));

        Labels labels = new Labels();
        labels.setStep(2);
        labels.setRotationPerpendicular();
        yAxis.setLabels(labels);

        PlotBand[] plotBands = new PlotBand[3];
        plotBands[0] = new PlotBand(0, 120, new SolidColor("#55BF3B"));
        plotBands[1] = new PlotBand(120, 160, new SolidColor("#DDDF0D"));
        plotBands[2] = new PlotBand(160, 200, new SolidColor("#DF5353"));
        yAxis.setPlotBands(plotBands);

        return chart;
    }

    protected Grid<TimeData> createGrid() {
        Grid<TimeData> grid = new Grid<>();
        grid.addColumn((TimeData td) -> {
            return new Integer(
                    (int) ((long) td.getTime()));
        }).setCaption("Time (ms)");

        grid.addColumn(TimeData::getValue).setCaption("Value");
        grid.addColumn(TimeData::getAvg).setCaption("Average");

        return grid;
    }

    public void disableButton() {
        button.setEnabled(false);
    }
}

