package server.helpers;

import java.util.Comparator;

public class RequestComparator implements Comparator<RequestNode> {
    public int compare(RequestNode n1, RequestNode n2) {
        return Long.compare(n1.getPriority(), n2.getPriority());
    }
}
