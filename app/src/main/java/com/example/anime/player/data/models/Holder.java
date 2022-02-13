package com.example.anime.player.data.models;

import java.io.Serializable;
import java.util.StringJoiner;

public final class Holder<T> extends Object implements Serializable {

    private T value;

    public Holder(T value) {
        this.value = value;
    }

    public Holder() {
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Holder.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .toString();
    }
}
