package us.codecraft.webmagic.md;

public class Tuple1<T> {
    private T value;

    public Tuple1(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
