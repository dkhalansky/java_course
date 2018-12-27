package ru.ifmo.ct.khalansky.coursework.client;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

class RandomArraySupplier implements Supplier<List<Integer>> {

    int n;

    public RandomArraySupplier(int n) {
        this.n = n;
    }

    public List<Integer> get() {
        ArrayList<Integer> arr = new ArrayList<>(n);
        for (int i = 0; i < n; ++i) {
            arr.add(i);
        }
        Collections.shuffle(arr);
        return arr;
    }

}
