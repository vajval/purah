package org.purah.core.checker.result;

import org.purah.core.checker.combinatorial.ExecMode;

import java.util.*;


/**
 * An ordered collection (also known as a <i>sequence</i>).  The user of this
 * interface has precise control over where in the list each element is
 * inserted.  The user can access elements by their integer index (position in
 * the list), and search for elements in the list.<p>
 *
 * @author Josh Bloch
 * @author Neal Gafter
 * @see Collection
 * @see Set
 * @see ArrayList
 * @see LinkedList
 * @see Vector
 * @see Arrays#asList(Object[])
 * @see Collections#nCopies(int, Object)
 * @see Collections#EMPTY_LIST
 * @see AbstractList
 * @see AbstractSequentialList
 * @since 1.2
 */
public enum ExecInfo {
    /**
     * @see ExecMode.Main
     *
     *
     */
    ignore("IGNORE"),
    success("SUCCESS"),
    failed("FAILED"),
    error("ERROR");

    final String value;

    ExecInfo(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
