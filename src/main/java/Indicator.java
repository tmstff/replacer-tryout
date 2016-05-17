public class Indicator {

    private char[] start;
    private char[] end;

    public Indicator(String start, String end) {
        this.start = start.toCharArray();
        this.end = end.toCharArray();
    }

    public char[] getStart() {
        return start;
    }

    public char[] getEnd() {
        return end;
    }

}