package Join;

import java.util.Comparator;

public class RowComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        if (o1 == null) {
            return 1;
        } else if (o2 == null) {
            return -1;
        }
        if (o1.compareTo(o2) < 0) {
            return -1;
        } else if (o1.compareTo(o2) > 0) {
            return 1;
        } else {
            return o1.compareTo(o2);
        }
    }
}
