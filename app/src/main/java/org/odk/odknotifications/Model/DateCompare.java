package org.odk.odknotifications.Model;

import java.util.Comparator;

public class DateCompare implements Comparator<Notification> {
    @Override
    public int compare(Notification o1, Notification o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}
