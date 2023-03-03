package net.jajica.myhomeiot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;


import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IotDevice;


import java.util.Timer;
import java.util.TimerTask;

import net.jajica.myhomeiot.databinding.FragmentUpgradeBinding;


public class UpgradeFragment extends DialogFragment {

    final String TAG = "UpgradeFragment";
    AlertDialog.Builder alertDialog;
    Context context;

    IotDevice device;

    private long delay;

    private int interval;

    private Boolean endUpgrade;

    private FragmentUpgradeBinding mBinding;

    private CountDownTimer timer;

    public CountDownTimer getTimer() {
        return timer;
    }

    public void setTimer(CountDownTimer timer) {
        this.timer = timer;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Boolean getEndUpgrade() {
        return endUpgrade;
    }

    public void setEndUpgrade(Boolean endUpgrade) {
        this.endUpgrade = endUpgrade;
    }

    public UpgradeFragment(Context context, IotDevice device, int delay) {
        this.context = context;
        this.device = device;
        this.delay = delay;
        this.interval = 1000;
        this.endUpgrade = false;

        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {
                if (device.getEndUpgradeFlag() == 1) {
                    Log.i(TAG, "Se ha recibido el fin de upgrade");
                    mBinding.textUpgradeDevice.setText("Upgrade realizado con exito");
                    endUpgrade = true;

                } else {
                    Log.i(TAG, "upgrade abortado");
                    endUpgrade = false;

                }
                timer.cancel();
                setCancelable(true);
                mBinding.textUpgradeDevice.setText("Upgrade abortado!!!");
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        alertDialog = new AlertDialog.Builder(context);
        setCancelable(false);


        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mBinding = FragmentUpgradeBinding.inflate(inflater);
        alertDialog.setView(mBinding.getRoot());
        mBinding.textPorcentage.setText("0 %");
        waitingUpgrade();
        return alertDialog.create();
    }


    private void waitingUpgrade() {



        mBinding.progressUpgrade.setMin(0);
        mBinding.progressUpgrade.setMax((int) delay/1000);

        timer = new CountDownTimer(delay, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress;
                String porcentage;
                progress = mBinding.progressUpgrade.getProgress();
                progress = (progress * 100) / mBinding.progressUpgrade.getMax();
                porcentage = String.valueOf(progress);
                porcentage += " %";
                mBinding.textPorcentage.setText(porcentage);
                mBinding.progressUpgrade.setProgress(mBinding.progressUpgrade.getProgress()+1);
            }

            @Override
            public void onFinish() {

                if (!endUpgrade) {
                    mBinding.textUpgradeDevice.setText("Upgrade sin exito");
                    setCancelable(true);
                }

            }
        };

        timer.start();



    }




}