package org.vaadin.pontus.vaadinrx2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

public class RingBufferTest {

    @Test
    public void testInsert() {
        RingBuffer<String> b = new RingBuffer<String>(String.class, 3);
        b.add("a");
        b.add("b");
        b.add("c");
        b.add("d");
        Iterator<String> it = b.iterator();
        assertTrue(it.hasNext());
        assertEquals("b", it.next());
        assertTrue(it.hasNext());
        assertEquals("c", it.next());
        assertTrue(it.hasNext());
        assertEquals("d", it.next());
        assertTrue(!it.hasNext());

    }

}
