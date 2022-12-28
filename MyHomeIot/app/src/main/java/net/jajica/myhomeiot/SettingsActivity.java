package net.jajica.myhomeiot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;

import net.jajica.libiot.IOT_LABELS_JSON;
import net.jajica.myhomeiot.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    ActivitySettingsBinding mbinding;
    private final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);
        mbinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(mbinding.getRoot());

        mbinding.imageEdituser.setOnClickListener(this);
        mbinding.imageEditPassword.setOnClickListener(this);
        mbinding.imageEditDni.setOnClickListener(this);
        mbinding.imageEditMail.setOnClickListener(this);
        mbinding.imageEditTelephone.setOnClickListener(this);
        mbinding.editUser.setOnKeyListener(this);
        mbinding.editPassword.setOnKeyListener(this);
        mbinding.buttonSettingSave.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String file = (String )bundle.get(IOT_LABELS_JSON.CONFIGURE_APP.getValorTextoJson());
        Log.i(TAG, file);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.imageEdituser:
                mbinding.editUser.setEnabled(true);
                mbinding.editPassword.setEnabled(false);
                mbinding.editDni.setEnabled(false);
                mbinding.editMail.setEnabled(false);
                mbinding.editTelephone.setEnabled(false);
            break;
            case R.id.imageEditPassword:
                mbinding.editUser.setEnabled(false);
                mbinding.editPassword.setEnabled(true);
                mbinding.editDni.setEnabled(false);
                mbinding.editMail.setEnabled(false);
                mbinding.editTelephone.setEnabled(false);
                break;
            case R.id.imageEditDni:
                mbinding.editUser.setEnabled(false);
                mbinding.editPassword.setEnabled(false);
                mbinding.editDni.setEnabled(true);
                mbinding.editMail.setEnabled(false);
                mbinding.editTelephone.setEnabled(false);
                break;
            case R.id.imageEditMail:
                mbinding.editUser.setEnabled(false);
                mbinding.editPassword.setEnabled(false);
                mbinding.editDni.setEnabled(false);
                mbinding.editMail.setEnabled(true);
                mbinding.editTelephone.setEnabled(false);
                break;
            case R.id.imageEditTelephone:
                mbinding.editUser.setEnabled(false);
                mbinding.editPassword.setEnabled(false);
                mbinding.editDni.setEnabled(false);
                mbinding.editMail.setEnabled(false);
                mbinding.editTelephone.setEnabled(true);
                break;
            case R.id.buttonSettingSave:
                Log.i(TAG, "Guardar");

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
}