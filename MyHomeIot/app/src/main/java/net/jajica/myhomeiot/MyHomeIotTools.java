package net.jajica.myhomeiot;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class MyHomeIotTools {

    Context context;

    public MyHomeIotTools(Context context) {
        this.context = context;
    }

    public void showKeyboard(int action) {

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(action, 0);


    }
}
