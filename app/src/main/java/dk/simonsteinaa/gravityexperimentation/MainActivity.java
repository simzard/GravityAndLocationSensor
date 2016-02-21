package dk.simonsteinaa.gravityexperimentation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    private String latitudeText = "";
    private String longitudeText = "";

    private SensorManager sensorManager;
    private Sensor gravitySensor;

    private String returnString = "";

    private Vibrator v;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up the google api client for GPS
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // setup the gravity sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            // success! there is a gravity sensor
            gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        }

        // setup the vibrator
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        // setup the media player
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // only if phone is resting at a table do "nothing"
        if ( (int) Math.abs(event.values[0]) == 0 &&
              (int) Math.abs(event.values[1]) == 0 &&
                (int) Math.abs(event.values[2]) == 9) {
            returnString = "There IS a GRAVITY sensor in this phone :) \n";
            TextView tv = (TextView) findViewById(R.id.gravity);
            tv.setTextColor(Color.BLUE);
            tv.setTextSize(20);
            tv.setText(returnString);
            ;

        } else { // start alarm!!!
            if (!mediaPlayer.isPlaying())
                mediaPlayer.start();
            v.vibrate(50);
            returnString = "   ALERT ALERT !!! \n\n\n Don't take MY phone!!\n\n\n " +
                    "The Police have been informed and should be arriving at\n" +
                    "Your current location:\n" +
                     "Longitude: " + longitudeText + ", Latitude: " + latitudeText + " soon... \\o/!";
            TextView tv = (TextView) findViewById(R.id.gravity);
            tv.setTextColor(Color.RED);
            tv.setTextSize(30);
            tv.setText(returnString);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (lastLocation != null) {
            latitudeText = (String.valueOf(lastLocation.getLatitude()));
            longitudeText = String.valueOf(lastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

