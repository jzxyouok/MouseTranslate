package com.example.jooff.shuyi.ui;



/**
 * Created by jooff on 16/7/30.
 */

public class RecHistoryItem {
    private String textOriginal;
    private String textResult;

    public RecHistoryItem(String textOriginal, String textResult) {
        this.textOriginal = textOriginal;
        this.textResult = textResult;
    }

    public String getTextOriginal() {
        return textOriginal;
    }

    public String getTextResult() {
        return textResult;
    }
}
