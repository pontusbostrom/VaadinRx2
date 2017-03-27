package org.vaadin.pontus.vaadinrx2.demo;

import java.util.ArrayList;
import java.util.Optional;

import org.vaadin.pontus.vaadinrx2.RingBuffer;
import org.vaadin.pontus.vaadinrx2.RxDataProvider;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DashBoardPresenter {

    HotDataSource datasource = new HotDataSource();
    ColdDataSource datasource2 = new ColdDataSource();
    CompositeDisposable disposables = new CompositeDisposable();

    private Long startTime = System.currentTimeMillis();

    public DashBoardPresenter(DashBoard view) {

        Observable<Optional<String>> clickObs = view.registerClickObserver();
        // Handle button clicks
        Disposable disp = clickObs.subscribe(e -> {
            startTime = System.currentTimeMillis();
            Observable<Double> observable;
            if (e.isPresent()) {
                if (e.get().equals(DashBoard.HOT)) {
                    // Start the data source and cache the results
                    observable = datasource.start().cache();
                } else {
                    // If the data source is cold, subscribe on a new thread and
                    // use a custom Flowable operator that requests 20 items.
                    // Then convert to an observable.
                    observable = datasource2.start()
                            .subscribeOn(Schedulers.newThread())
                            .lift(new RequestFlowable<Double>(20)).toObservable()
                            .cache();
                }
            } else {
                Notification.show("No data source selected!",
                        Type.ERROR_MESSAGE);
                return;
            }

            // First compute the moving average and store the current value and
            // average in a Pair.
            // Then create a TimeData object from the pair and the time.
            // The time is relative to a start time.
            Observable<TimeData> obs = observable
                    .scan(new Pair<Double, Double>(0.0, 0.0), (old, val) -> {
                        return new Pair<Double, Double>(val,
                                0.2 * val + 0.8 * old.getSecond());
                    }).map(pair -> new TimeData(
                            System.currentTimeMillis() - startTime,
                            pair.getFirst(), pair.getSecond()));
            
            // Setup the data providers for the charts and add them as data
            // series to the charts
            RxDataProvider<TimeData> chartProvider = new RxDataProvider<TimeData>(
                    UI.getCurrent(), obs, new ArrayList<TimeData>(512));
            view.connectCharts1(chartProvider);
            disposables.add(chartProvider);
            
            RxDataProvider<Double> chartProvider2 = new RxDataProvider<Double>(
                    UI.getCurrent(),
                    observable, new RingBuffer<Double>(Double.class, 5));
            view.connectCharts2(chartProvider2);
            disposables.add(chartProvider);
            
            chartProvider2 = new RxDataProvider<Double>(UI.getCurrent(),
                    observable,
                    new RingBuffer<Double>(Double.class, 1));
            view.connectCharts3(chartProvider2);
            disposables.add(chartProvider);

            // Hook up the grid.
            RxDataProvider<TimeData> gridProvider = new RxDataProvider<>(
                    UI.getCurrent(), obs,
                    new RingBuffer<TimeData>(TimeData.class, 5));
            disposables.add(gridProvider);
            view.connectGrid(gridProvider);

            // Hook up the text fields that show values from the data source.
            view.connectLabelsWithBinder(obs);

            // Disable the start button.
            view.disableButton();
        });
        disposables.add(disp);
    }

    public void dispose() {
        disposables.dispose();
    }

}
