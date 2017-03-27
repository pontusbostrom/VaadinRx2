package org.vaadin.pontus.vaadinrx2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.UI;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Data Provider that collects values from an observable. The provider can be
 * instantiated with a buffer that gives the buffering behaviour of the data
 * provider. When new values are provided from the observable, refreshAll is
 * called. This means that e.g. charts and grids that use the data provider are
 * automatically updated when new items arrive.
 * 
 * @author Pontus Bostrom
 *
 * @param <T>
 */
public class RxDataProvider<T>
        extends AbstractDataProvider<T, SerializablePredicate<T>>
        implements Disposable {

    private final Collection<T> cache;
    private final Disposable disp;

    public RxDataProvider(UI ui, Observable<T> observable,
            Collection<T> buffer) {
        cache = buffer;
        disp = observable.subscribe(v -> {
            ui.access(() -> {
                cache.add(v);
                refreshAll();
            });
        });
    }

    public RxDataProvider(UI ui, Observable<T> observable) {
        this(ui, observable, new ArrayList<T>());
    }

    public RxDataProvider(UI ui, Observable<List<T>> observable,
            Collection<T> buffer,
            boolean append) {
        cache = buffer;
        disp = observable.subscribe(col -> {
            ui.access(() -> {
                if (append) {
                    cache.addAll(col);
                    refreshAll();
                } else {
                    cache.clear();
                    cache.addAll(col);
                    refreshAll();
                }
            });
        });
    }


    public RxDataProvider(UI ui, Observable<List<T>> observable,
            boolean append) {
        this(ui, observable, new ArrayList<T>(), append);
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public int size(Query<T, SerializablePredicate<T>> t) {
        return (int) getFilteredStream(t).count();
    }

    @Override
    public Stream<T> fetch(Query<T, SerializablePredicate<T>> query) {
        Stream<T> stream = getFilteredStream(query);
        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    private Stream<T> getFilteredStream(
            Query<T, SerializablePredicate<T>> query) {
        Stream<T> stream = cache.stream();
        stream = query.getFilter().map(stream::filter).orElse(stream);
        return stream;
    }


    @Override
    public void dispose() {
        disp.dispose();

    }

    @Override
    public boolean isDisposed() {
        return disp.isDisposed();
    }

}
