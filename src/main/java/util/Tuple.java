package util;

public class Tuple {

    String first;
    String second;

    public Tuple(String first, String second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "Tuple: " + first + " " + second;
    }

    @Override
    public int hashCode() {
        return this.first.hashCode() + this.second.hashCode();
    }

}
