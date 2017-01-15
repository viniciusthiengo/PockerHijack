package br.com.thiengo.pockerhijack.domain;

/**
 * Created by viniciusthiengo on 15/01/17.
 */

public class Table {
    private int image;
    private String label;

    public Table(int image, String label) {
        this.image = image;
        this.label = label;
    }

    public int getImage() {
        return image;
    }

    public String getLabel() {
        return label;
    }
}
