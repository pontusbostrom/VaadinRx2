package org.vaadin.pontus.vaadinrx2;

import java.util.function.Consumer;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.vaadin.data.HasValue;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * Utility class for creating observables from Vaadin components that emit
 * events and for subscribing components that consume values. Methods for
 * subscribing subscribers that modifies the Vaadin UI components from
 * background threads are also provided. The handling of calls to UI#access is
 * then done automatically.
 * 
 * @author Pontus Bostrom
 *
 */
public class VaadinRx2 {

    /**
     * Creates an observable from a button for observing button clicks.
     * 
     * @param b
     * @return
     */
    public static Observable<ClickEvent> createFrom(Button b) {
        Observable<ClickEvent> clickObservable = Observable.<ClickEvent> create(
                (ObservableEmitter<ClickEvent> observer) -> {
                    Registration reg = b
                            .addClickListener(e -> observer.onNext(e));
                    observer.setCancellable(() -> {
                        reg.remove();
                    });

                });
        return clickObservable;
    }

    /**
     * Creates an observable from a something that has a value for observing
     * value change events.
     * 
     * @param hasValue
     * @return
     */
    public static <T> Observable<ValueChangeEvent<T>> createFrom(
            HasValue<T> hasValue) {
        Observable<ValueChangeEvent<T>> o = Observable
                .<ValueChangeEvent<T>> create(
                        (ObservableEmitter<ValueChangeEvent<T>> observer) -> {
                            Registration reg = hasValue.addValueChangeListener(
                                    e -> observer.onNext(e));
                            observer.setCancellable(() -> {
                                reg.remove();
                            });
                        });
        return o;
    }

    /**
     * Creates an observable from something where a single element can be
     * selected for observing selection events.
     * 
     * @param selector
     * @return
     */
    public static <T> Observable<SingleSelectionEvent<T>> createFrom(
            AbstractSingleSelect<T> selector) {
        Observable<SingleSelectionEvent<T>> o = Observable
                .<SingleSelectionEvent<T>> create((
                        ObservableEmitter<SingleSelectionEvent<T>> observer) -> {
                    Registration reg = selector
                            .addSelectionListener(e -> observer.onNext(e));
                    observer.setCancellable(() -> {
                        reg.remove();
                    });

                });
        return o;
    }

    /**
     * Automatically subscribe a component with a value to a observable
     * producing values.
     * 
     * @param observable
     * @param comp
     * @return
     */
    public static <T> Disposable subscribe(Observable<T> observable,
            HasValue<? super T> comp) {
        return observable.subscribe(v -> comp.setValue(v));
    }

    /**
     * Automatically subscribe a label to a observable producing values.
     * 
     * @param observable
     * @param comp
     * @return
     */
    public static Disposable subscribe(Observable<String> observable,
            Label label) {
        return observable.subscribe(v -> label.setValue(v));
    }

    /**
     * Subscribe a consumer that consumes values on a background thread. Calls
     * to the consumer are automatically done inside UI#access.
     * 
     * @param observable
     * @param comp
     * @return
     */
    public static <T> Disposable subscribeAsynchronously(UI ui,
            Observable<T> observable, Consumer<? super T> onNext) {
        Disposable d = observable.subscribe(v -> {
            ui.access(() -> {
                onNext.accept(v);
            });
        });
        return d;
    }

    public static <T> Disposable subscribeAsynchronously(UI ui,
            Observable<T> observable, Consumer<? super T> onNext,
            Consumer<? super Throwable> onError) {
        io.reactivex.functions.Consumer<T> c = v -> {
            ui.access(() -> {
                onNext.accept(v);
            });
        };
        io.reactivex.functions.Consumer<Throwable> err = e -> {
            ui.access(() -> {
                onError.accept(e);
            });
        };
        Disposable d = observable.subscribe(c, err);
        return d;
    }

    public static <T> Disposable subscribeAsynchronously(UI ui,
            Observable<T> observable, Consumer<? super T> onNext,
            Consumer<? super Throwable> onError, Runnable onComplete) {
        io.reactivex.functions.Consumer<T> c = v -> {
            ui.access(() -> {
                onNext.accept(v);
            });
        };
        io.reactivex.functions.Consumer<Throwable> err = e -> {
            ui.access(() -> {
                onError.accept(e);
            });
        };

        io.reactivex.functions.Action a = () -> {
            ui.access(() -> {
                onComplete.run();
            });
        };
        Disposable d = observable.subscribe(c, err, a);
        return d;
    }

    public static <T> void subscribeAsynchronously(UI ui,
            Observable<T> observable, Observer<? super T> subscriber) {
        observable.subscribe(new Observer<T>() {

            @Override
            public void onComplete() {
                ui.access(() -> {
                    subscriber.onComplete();
                });
            }

            @Override
            public void onError(Throwable arg0) {
                ui.access(() -> {
                    onError(arg0);
                });
            }

            @Override
            public void onNext(T arg0) {
                ui.access(() -> {
                    onNext(arg0);
                });
            }

            @Override
            public void onSubscribe(Disposable d) {
                ui.access(() -> {
                    onSubscribe(d);
                });
            }

        });
    }

    public static <T> void subscribeAsynchronously(UI ui,
            Flowable<T> observable,
            Subscriber<? super T> subscriber) {
        observable.subscribe(new Subscriber<T>() {

            @Override
            public void onComplete() {
                ui.access(() -> {
                    subscriber.onComplete();
                });
            }

            @Override
            public void onError(Throwable arg0) {
                ui.access(() -> {
                    onError(arg0);
                });
            }

            @Override
            public void onNext(T arg0) {
                ui.access(() -> {
                    onNext(arg0);
                });
            }

            @Override
            public void onSubscribe(Subscription arg0) {
                ui.access(() -> {
                    onSubscribe(arg0);
                });
            }

        });
    }

    public static <T> Disposable subscribeAsynchronously(UI ui,
            Observable<T> observable, HasValue<? super T> comp) {
        return subscribeAsynchronously(ui, observable, v -> comp.setValue(v));
    }

    public static Disposable subscribeAsynchronously(UI ui,
            Observable<String> observable, Label comp) {
        return subscribeAsynchronously(ui, observable, v -> comp.setValue(v));
    }
}
