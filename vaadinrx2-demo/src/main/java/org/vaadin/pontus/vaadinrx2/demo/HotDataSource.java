package org.vaadin.pontus.vaadinrx2.demo;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class HotDataSource {


    public Observable<Double> start() {

        // Simulate a hot data source that produces 20 data items.
        Observable<Double> flow = Observable
                .create((ObservableEmitter<Double> emitter) -> {
                    if (emitter == null) {
                        return;
                    }
                    new Thread(() -> {
                        Double d = 1.0;
                        for (int i = 0; i < 20; i++) {
                            emitter.onNext(d);
                            d = Math.random();
                            try {
                                // Simulate that the operation takes some time.
                                Thread.sleep(1000L);
                            } catch (Exception e) {
                                emitter.onError(e);
                            }
                        }
                        emitter.onComplete();
                    }).start();

                });

        return flow;
    }

}
