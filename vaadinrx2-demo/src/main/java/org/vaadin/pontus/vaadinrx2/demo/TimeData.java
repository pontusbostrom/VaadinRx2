package org.vaadin.pontus.vaadinrx2.demo;

class TimeData {

    final private Long time;
    final private Double value;
    final private Double avg;

    public TimeData(Long time, Double v, Double avg) {
        this.time = time;
        this.value = v;
        this.avg = avg;
    }

    public TimeData(Long time, Double v) {
        this(time, v, null);
    }

    public Long getTime() {
        return time;
    }

    public Double getValue() {
        return value;
    }

    public Double getAvg() {
        return avg;
    }

}