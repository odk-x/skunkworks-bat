package org.odk.odknotifications.Model;

import java.util.Comparator;

public class TitleCompare implements Comparator<Notification> {
    @Override
    public int compare(Notification o1, Notification o2) {
        return o1.getTitle().compareTo(o2.getTitle());
    }
}
