package org.vaadin.pontus.vaadinrx2.demo;

public class Pair<F, S> {

    final private F first;
    final private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

}
