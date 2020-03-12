package editor;

import java.util.List;

public class Number {
    private int i = 0;
    private int max;
    private List<Integer> numbers;
    private int end;
    private List<Integer> ends;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public List<Integer> getEnds() {
        return ends;
    }

    public void setEnds(List<Integer> ends) {
        this.ends = ends;
    }

    public int getNext(){
        if(i >= max){
            i = 0;
            return i;
        } else
            return ++i;
    }

    public int getPrevious(){
        if(i == 0){
            i = max;
            return i;
        } else
            return --i;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
