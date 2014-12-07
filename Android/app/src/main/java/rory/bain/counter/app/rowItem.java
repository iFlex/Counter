package rory.bain.counter.app;

/**
 * Created by rorybain on 17/11/14.
 */
public class rowItem {
    private String title;
    private int icon;

    public rowItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }

}
