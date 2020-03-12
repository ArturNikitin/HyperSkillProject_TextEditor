package editor;

public class CurrentCheck {
    private boolean isChecked = false;
    private static CurrentCheck check;

    private CurrentCheck(){

    }

    public static CurrentCheck getCurrentCheck(){
        if (check == null){
            check = new CurrentCheck();
            return check;
        }
        else
            return check;
    }

    public void setIsChecked(int i){
        if (i == 1) {
            this.isChecked = true;
        } else {
            this.isChecked = false;
        }
    }

    public boolean isChecked() {
        return isChecked;
    }
}
