package org.vaadin.pontus.vaadinrx2.demo;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.FlowableOperator;

public class RequestFlowable<T> implements FlowableOperator<T, T> {

    final int count;

    public RequestFlowable(int count) {
        this.count = count;
    }
    @Override
    public Subscriber<? super T> apply(Subscriber<? super T> observer)
            throws Exception {
        return new Op<T>(observer, count);
    }

    static class Op<T> implements Subscriber<T>, Subscription {

        final Subscriber<? super T> child;
        final int count;

        Subscription s;

        public Op(Subscriber<? super T> child, int count) {
            this.child = child;
            this.count = count;
        }

        @Override
        public void cancel() {
            if (s != null) {
                s.cancel();
            }
        }

        @Override
        public void request(long arg0) {
            if (s != null) {

            }
        }

        @Override
        public void onComplete() {
            child.onComplete();

        }

        @Override
        public void onError(Throwable arg0) {
            child.onError(arg0);

        }

        @Override
        public void onNext(T arg0) {
            child.onNext(arg0);

        }

        @Override
        public void onSubscribe(Subscription arg0) {
            this.s = arg0;
            if (s != null) {
                s.request(count);
            }
            child.onSubscribe(this);

        }

    }


}
