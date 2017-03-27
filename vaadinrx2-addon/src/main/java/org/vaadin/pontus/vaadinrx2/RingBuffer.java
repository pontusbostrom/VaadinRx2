package org.vaadin.pontus.vaadinrx2;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * A ring buffer implementation intended to be used as the buffer in the
 * RxDataProvider.
 * 
 * @author Pontus Bostrom
 *
 * @param <T>
 */
public class RingBuffer<T> implements Collection<T> {

    int start;
    int size;
    T[] store;

    public RingBuffer(Class<T> clazz, int cap) {
        store = (T[]) Array.newInstance(clazz, cap);
        size = 0;
        start = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (T item : this) {
            if (item == null) {
                if (o == null) {
                    return true;
                }
            } else {
                if (item.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new BufferIterator();
    }

    private class BufferIterator implements Iterator<T> {

        int pos;

        public BufferIterator() {
            pos = 0;
        }

        @Override
        public boolean hasNext() {
            return pos < size;
        }

        @Override
        public T next() {
            return store[(start + pos++) % store.length];
        }

    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        int i = 0;
        for (T item : this) {
            arr[i] = item;
            i++;
        }
        return arr;
    }

    @Override
    public <E> E[] toArray(E[] a) {
        E[] arr;
        if (size > 0) {
            if (!a.getClass().getComponentType()
                    .isAssignableFrom(store[start].getClass())) {
                arr = a;
            } else {
                throw new ArrayStoreException();
            }
            if (a.length < size) {
                arr = (E[]) Array.newInstance(a.getClass(), size);
            }
            int i = 0;
            for (T item : this) {
                arr[i] = (E) item;
                i++;
            }
            return arr;
        } else {
            return a;
        }

    }

    @Override
    public boolean add(T e) {
        if (size == store.length) {
            store[start] = e;
            start = (start + 1) % store.length;
        } else {
            store[(start + size) % store.length] = e;
            size++;
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T item : c) {
            add(item);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        Arrays.setAll(store, v -> null);
        size = 0;
        start = 0;
    }

}
