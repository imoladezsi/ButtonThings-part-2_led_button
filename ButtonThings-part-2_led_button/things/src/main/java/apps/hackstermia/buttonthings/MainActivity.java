package apps.hackstermia.buttonthings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Gpio mLedGpio;
 //   private Gpio mLedGpio2;
    private ButtonInputDriver mButtonInputDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting ButtonActivity");

        PeripheralManager pioService = PeripheralManager.getInstance();
        try {
            Log.i(TAG, "Configuring GPIO pins");

            try{
                mLedGpio.close();
                Log.i(TAG, "Pin was already closed");
            }catch(Exception e){
                Log.i(TAG, "Closed open pin");
            }
            mLedGpio = pioService.openGpio(BoardDefaults.getGPIOForLED());
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);

            //     mLedGpio2 = pioService.openGpio(BoardDefaults.getGPIOForLED());
            //     mLedGpio2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            Log.i(TAG, "Registering button driver");
            // Initialize and register the InputDriver that will emit SPACE key events
            // on GPIO state changes.
            mButtonInputDriver = new ButtonInputDriver(
                    BoardDefaults.getGPIOForButton(),
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_SPACE);
        } catch (IOException e) {
            Log.e(TAG, "Error configuring GPIO pins", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mButtonInputDriver.register();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.i(TAG, "Dispatched key event");

            setLedValue(!getLedValue());
            return true;
        }
        return false;
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.i(TAG,  "Some button pressed");
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // Turn on the LED
            Log.i(TAG,  "Button pressed");
            setLedValue(true);
           // setLedValueSecond(false);

            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            // Turn off the LED
            Log.i(TAG, "On key up called");
            setLedValue(false);
      //      setLedValueSecond(true);
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * Update the value of the LED output.
     */
    private void setLedValue(boolean value) {
        try {
            mLedGpio.setValue(value);
        } catch (IOException e) {
            Log.e(TAG, "Error updating GPIO value", e);
        }
    }
    private boolean getLedValue(){
        try {
            return mLedGpio.getValue();
        }catch(IOException e){
            Log.e(TAG, "Error geting LED value");
        }
        return false;
    }

    /*
    private void setLedValueSecond(boolean value) {
        try {
        //    mLedGpio2.setValue(value);

        } catch (IOException e) {
            Log.e(TAG, "Error updating GPIO2 value", e);
        }
    }
*/
    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (mButtonInputDriver != null) {
            mButtonInputDriver.unregister();
            try {
                mButtonInputDriver.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Button driver", e);
            } finally{
                mButtonInputDriver = null;
            }
        }

        if (mLedGpio != null) {
            try {
                mLedGpio.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing LED GPIO", e);
            } finally{
                mLedGpio = null;
            }
            mLedGpio = null;
        }
        /*
        if (mLedGpio2 != null) {
            try {
                mLedGpio2.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing LED GPIO", e);
            } finally{
                mLedGpio2 = null;
            }
            mLedGpio2 = null;
        }
        */
    }
}
