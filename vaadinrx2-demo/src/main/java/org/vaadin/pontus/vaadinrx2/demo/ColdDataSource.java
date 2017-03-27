package org.vaadin.pontus.vaadinrx2.demo;


import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class ColdDataSource {

    public Flowable<Double> start() {
        // Simulate a cold data source that produces the requested number of
        // items.
        Flowable<Double> flow = Flowable.<Double> create(emitter -> {
            while (emitter.requested() > 0) {
                emitter.onNext(Math.random());
                // Simulate that the operation takes some time.
                Thread.sleep(1000);
            }
            emitter.onComplete();

        }, BackpressureStrategy.BUFFER);

        return flow;
    }

}
