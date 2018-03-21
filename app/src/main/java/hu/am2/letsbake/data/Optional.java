package hu.am2.letsbake.data;

import android.support.annotation.Nullable;

import java.util.NoSuchElementException;

//based on this article https://medium.com/@joshfein/handling-null-in-rxjava-2-0-10abd72afa0b
public class Optional<T> {
    private final T optional;

    public Optional(@Nullable T optional) {
        this.optional = optional;
    }

    public boolean isEmpty() {
        return this.optional == null;
    }

    public T get() {
        if (optional == null) {
            throw new NoSuchElementException("No value present");
        }
        return optional;
    }
}
