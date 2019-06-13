package org.odk.odknotifications.Model;

import java.util.Comparator;

public class MessageCompare implements Comparator<Notification> {
    @Override
    public int compare(Notification o1, Notification o2) {
        return o1.getMessage().compareTo(o2.getMessage());
    }
}
