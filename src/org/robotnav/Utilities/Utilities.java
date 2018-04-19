package org.robotnav.Utilities;

import java.util.ArrayList;

public class Utilities {
    public static void ReverseArray(ArrayList array) {
        for(int i = 0; i < array.size() / 2; i++)
        {
            Object temp = array.get(i);
            array.set(i, array.get(array.size() - i - 1));
            array.set(array.size()- i - 1, temp);
        }
    }
}
