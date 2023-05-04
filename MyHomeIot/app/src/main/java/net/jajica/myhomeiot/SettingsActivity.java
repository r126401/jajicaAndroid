package net.jajica.myhomeiot;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import net.jajica.libiot.IOT_LABELS_JSON;

import org.json.JSONException;
import org.json.JSONObject;

import net.jajica.myhomeiot.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    ActivitySettingsBinding mbinding;
    private final String TAG = "SettingsActivity";
    private String configuration;


    private void createEventEditor(TextInputEditText textView) {


        textView.setOnEditorActionListener(new TextInputEditText.OnEditorActionListener() {

            MyHomeIotTools tool = new MyHomeIotTools(getApplicationContext());

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Log.i(TAG, "hola");
                    tool.showHideSoftKeyboard(textView, false);
                    return true;

                }
                return false;
            }

        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(mbinding.getRoot());
        mbinding.imageEdituser.setOnClickListener(this);
        mbinding.imageEditPassword.setOnClickListener(this);
        mbinding.imageEditDni.setOnClickListener(this);
        mbinding.imageEditMail.setOnClickListener(this);
        mbinding.imageEditTelephone.setOnClickListener(this);
        mbinding.editUser.setOnKeyListener(this);
        mbinding.editPassword.setOnKeyListener(this);
        mbinding.editDni.setOnKeyListener(this);
        mbinding.buttonSettingSave.setOnClickListener(this);
        configuration = null;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        configuration = (String )bundle.get(IOT_LABELS_JSON.CONFIGURE_APP.getValorTextoJson());
        if (configuration.equals("null")) {
            mbinding.textSettings.setText(R.string.settingsNewAccount);
        } else {
            loadDataFromConfiguration(configuration);

        }
        Log.i(TAG, configuration);


    }

    private void loadDataFromConfiguration(String data) {
        JSONObject jsonObject;
        String value;
        try {
            jsonObject = new JSONObject(data);
            extractValue(jsonObject, IOT_LABELS_JSON.USER.getValorTextoJson(), mbinding.editUser);
            extractValue(jsonObject, IOT_LABELS_JSON.PASSWORD.getValorTextoJson(), mbinding.editPassword);
            extractValue(jsonObject, IOT_LABELS_JSON.DNI.getValorTextoJson(), mbinding.editDni);
            extractValue(jsonObject, IOT_LABELS_JSON.MAIL.getValorTextoJson(), mbinding.editMail);
            extractValue(jsonObject, IOT_LABELS_JSON.TELEPHONE.getValorTextoJson(), mbinding.editTelephone);

        } catch (JSONException e) {

        }

    }

    private void extractValue(JSONObject jsonObject, String label, TextInputEditText editText) {

        String value;
        if ((value = jsonObject.optString(label)) != null) {
            editText.setText(value);
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        MyHomeIotTools tools;
        tools = new MyHomeIotTools(getApplicationContext());
        switch (v.getId())
        {
            case R.id.imageEdituser:
                mbinding.editUser.setEnabled(true);
                mbinding.editPassword.setEnabled(false);
                mbinding.editDni.setEnabled(false);
                mbinding.editMail.setEnabled(false);
                mbinding.editTelephone.setEnabled(false);
                mbinding.editUser.requestFocus();
                tools.showHideSoftKeyboard(mbinding.editUser, true);
            break;
            case R.id.imageEditPassword:
                mbinding.editUser.setEnabled(false);
                mbinding.editPassword.setEnabled(true);
                mbinding.editPassword.requestFocus();
                mbinding.editDni.setEnabled(false);
                mbinding.editMail.setEnabled(false);
                mbinding.editTelephone.setEnabled(false);
                tools.showHideSoftKeyboard(mbinding.editPassword,true);
                break;
            case R.id.imageEditDni:
                mbinding.editUser.setEnabled(false);
                mbinding.editPassword.setEnabled(false);
                mbinding.editDni.setEnabled(true);
                mbinding.editDni.requestFocus();
                mbinding.editMail.setEnabled(false);
                mbinding.editTelephone.setEnabled(false);
                tools.showHideSoftKeyboard(mbinding.editDni, true);
                break;
            case R.id.imageEditMail:
                mbinding.editUser.setEnabled(false);
                mbinding.editPassword.setEnabled(false);
                mbinding.editDni.setEnabled(false);
                mbinding.editMail.setEnabled(true);
                mbinding.editMail.requestFocus();
                mbinding.editTelephone.setEnabled(false);
                tools.showHideSoftKeyboard(mbinding.editMail, true);
                break;
            case R.id.imageEditTelephone:
                mbinding.editUser.setEnabled(false);
                mbinding.editPassword.setEnabled(false);
                mbinding.editDni.setEnabled(false);
                mbinding.editMail.setEnabled(false);
                mbinding.editTelephone.setEnabled(true);
                mbinding.editTelephone.requestFocus();
                tools.showHideSoftKeyboard(mbinding.editTelephone, true);
                break;

            case R.id.buttonSettingSave:
                Log.i(TAG, "Guardar");
                saveData();
                break;
        }
    }



    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {


        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            switch (v.getId())
            {
                case R.id.editUser:
                    mbinding.editUser.setEnabled(false);
                    break;
                case R.id.editPassword:
                    mbinding.editPassword.setEnabled(false);
                    break;
                case R.id.editDni:
                    mbinding.editDni.setEnabled(false);
                    break;
                case R.id.editMail:
                    mbinding.editMail.setEnabled(false);
                    break;
                case R.id.editTelephone:
                    mbinding.editTelephone.setEnabled(false);
                    break;

            }

        }
        return false;
    }

    private void saveData() {

        Intent data;
        data = new Intent();
        JSONObject jsonObject = null;
        mbinding.buttonSettingSave.requestFocus();
        if ((!validateUser()) ||
        (!validatePassword()) || (!validateMail())) {
            Log.e(TAG, "Error en boton");
            //setResult(RESULT_CANCELED);
            //finish();
            return;
        }

        try {
            jsonObject = new JSONObject();
            if (validateDni()) jsonObject.put(IOT_LABELS_JSON.DNI.getValorTextoJson(), mbinding.editDni.getText().toString());
            jsonObject.put(IOT_LABELS_JSON.USER.getValorTextoJson(), mbinding.editUser.getText().toString());
            jsonObject.put(IOT_LABELS_JSON.PASSWORD.getValorTextoJson(), mbinding.editPassword.getText().toString());
            jsonObject.put(IOT_LABELS_JSON.MAIL.getValorTextoJson(), mbinding.editMail.getText().toString());
            if (validateTelephone()) jsonObject.put(IOT_LABELS_JSON.TELEPHONE.getValorTextoJson(),mbinding.editTelephone.getText().toString());
        } catch (JSONException e) {
            setResult(RESULT_CANCELED);
            finish();
            return;

        }
        data.setData(Uri.parse(jsonObject.toString()));
        setResult(RESULT_OK, data);
        finish();

    }

    private boolean isEditTextEmpty(TextInputEditText editText, int message) {

        if (editText.getText().toString().isEmpty()) {
            editText.setText("");
            editText.setHint(message);
            return true;
        }
        return false;
    }

    private boolean validateUser() {

        if (isEditTextEmpty(mbinding.editUser, R.string.settingsInvalidUser)) {
            mbinding.imageErrorEditUser.setVisibility(View.VISIBLE);
            return false;
        } else {
            mbinding.imageErrorEditUser.setVisibility(View.INVISIBLE);
        }

        return true;
    }

    private boolean validatePassword() {

        if (isEditTextEmpty(mbinding.editPassword, R.string.settingsInvalidPassword)) {
            mbinding.imageErrorEditPassword.setVisibility(View.VISIBLE);
            return false;
        } else {
            mbinding.imageErrorEditPassword.setVisibility(View.INVISIBLE);
        }

        return true;
    }

    private boolean validateDni() {

        ValidadorDNI dni;
        dni = new ValidadorDNI(mbinding.editDni.getText().toString());

        if (!dni.validar() || mbinding.editDni.getText().toString().isEmpty()) {
            mbinding.editDni.setText("");
            mbinding.editDni.setHint(R.string.settingsInvalidDni);
            mbinding.imageErrorEditDni.setVisibility(View.VISIBLE);
        } else {
            mbinding.imageErrorEditDni.setVisibility(View.INVISIBLE);
        }

        return true;
    }

    private boolean validateMail() {

        String data;
        int i;
        data = mbinding.editMail.getText().toString();
         i= data.indexOf("@");

         if (i <= 0) {
             mbinding.editMail.setText("");
             mbinding.editMail.setHint(R.string.settingsInvalidMail);
             mbinding.imageErrorEditMail.setVisibility(View.VISIBLE);
             return false;
         } else {
             mbinding.imageErrorEditMail.setVisibility(View.INVISIBLE);
         }


        return true;
    }

    private boolean validateTelephone() {


        if (isEditTextEmpty(mbinding.editTelephone, R.string.settingsInvalidTelephone)) {
            mbinding.imageErrorEditTelephone.setVisibility(View.VISIBLE);
            return false;
        } else {
            mbinding.imageErrorEditTelephone.setVisibility(View.INVISIBLE);
        }
        return true;
    }

}