package net.jajica.myhomeiot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;


import net.jajica.libiot.IOT_CODE_RESULT;
import net.jajica.libiot.IOT_COMMANDS;
import net.jajica.libiot.IOT_SPONTANEOUS_TYPE;
import net.jajica.libiot.IotDevice;


import net.jajica.myhomeiot.databinding.FragmentUpgradeBinding;


public class InteractiveFragment extends DialogFragment {

    final String TAG = "UpgradeFragment";
    AlertDialog.Builder alertDialog;
    Context context;

    IotDevice device;

    private long delay;

    private int interval;

    private Boolean endUpgrade;

    private FragmentUpgradeBinding mBinding;

    private CountDownTimer timer;
    private IOT_COMMANDS command;

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

    public InteractiveFragment(Context context, IotDevice device, int delay, IOT_COMMANDS command) {
        this.context = context;
        this.device = device;
        this.delay = delay;
        this.interval = 1000;
        this.endUpgrade = false;
        this.command = command;


        device.setOnReceivedSpontaneousStartDevice(new IotDevice.OnReceivedSpontaneousStartDevice() {
            @Override
            public void onReceivedSpontaneousStartDevice(IOT_CODE_RESULT resultCode) {

                switch (command) {
                    case UPGRADE_FIRMWARE:
                        if (device.getEndUpgradeFlag() == 1) {
                            Log.i(TAG, "Se ha recibido el fin de upgrade");
                            mBinding.textInteractiveStatus.setText(R.string.upgrade_succesfully);
                            endUpgrade = true;
                            mBinding.textPorcentage.setText("100 %");

                        } else {
                            Log.i(TAG, "upgrade abortado");
                            endUpgrade = false;

                        }
                        timer.cancel();
                        setCancelable(true);
                        mBinding.textInteractiveStatus.setText(R.string.upgrade_unsucessfully);
                        break;
                    case RESET:
                    case FACTORY_RESET:
                        mBinding.textPorcentage.setText("100 %");
                        mBinding.textInteractiveStatus.setText(R.string.succesfull);
                        device.commandGetStatusDevice();
                        timer.cancel();
                        setCancelable(true);

                }


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
        waitingAction((int) delay);
/*
        switch (command) {
            case UPGRADE_FIRMWARE:
                waitingUpgrade();
                break;
            case RESET:
                waitingAction((int) delay);
                break;
            case FACTORY_RESET:
                waitingAction((int) delay);
                break;
        }


 */


        return alertDialog.create();
    }

    private void waitingUpgrade() {



        mBinding.progressAction.setMin(0);
        mBinding.progressAction.setMax((int) delay/1000);
        mBinding.textInteractiveStatus.setText(R.string.upgrading);

        timer = new CountDownTimer(delay, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress;
                String porcentage;
                progress = mBinding.progressAction.getProgress();
                progress = (progress * 100) / mBinding.progressAction.getMax();
                porcentage = String.valueOf(progress);
                porcentage += " %";
                mBinding.textPorcentage.setText(porcentage);
                mBinding.progressAction.setProgress(mBinding.progressAction.getProgress()+1);
            }

            @Override
            public void onFinish() {

                switch (command) {
                    case UPGRADE_FIRMWARE:
                        if (!endUpgrade) {
                            mBinding.textInteractiveStatus.setText(R.string.upgrade_unsucessfully);
                            setCancelable(true);
                        }
                        break;
                    case RESET:
                    case FACTORY_RESET:
                        mBinding.textInteractiveStatus.setText(R.string.unsucessfull);
                        break;

                }





            }
        };

        timer.start();



    }

    private void waitingAction(int delay) {


        switch (command) {

            case UPGRADE_FIRMWARE:
                mBinding.textInteractiveStatus.setText(R.string.upgrading);
                break;
            case RESET:
                mBinding.textInteractiveStatus.setText(R.string.reseting);
                break;
            case FACTORY_RESET:
                mBinding.textInteractiveStatus.setText(R.string.factoring);
                break;

        }
        mBinding.progressAction.setMin(0);
        mBinding.progressAction.setMax((int) delay/1000);

        timer = new CountDownTimer(delay, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress;
                String porcentage;
                progress = mBinding.progressAction.getProgress();
                progress = (progress * 100) / mBinding.progressAction.getMax();
                porcentage = String.valueOf(progress);
                porcentage += " %";
                Log.i(TAG, "min: 0, max: " + delay/1000 + "progress: " + progress + "porcentaje: " + porcentage);
                mBinding.textPorcentage.setText(porcentage);
                mBinding.progressAction.setProgress(mBinding.progressAction.getProgress()+1);
            }

            @Override
            public void onFinish() {

                if (!endUpgrade) {
                    mBinding.textInteractiveStatus.setText(R.string.upgrade_unsucessfully);
                    setCancelable(true);
                }

            }
        };

        timer.start();

    }




}