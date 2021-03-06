package iann91.uw.tacoma.edu.myfridge.Authenticate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import iann91.uw.tacoma.edu.myfridge.Dashboard.DashboardActivity;
import iann91.uw.tacoma.edu.myfridge.R;

/**
 * A login screen that offers login via email/password.
 * @author iann91 munkh92
 * @version 1.0
 */
public class LoginActivity extends FragmentActivity implements LoginFragment.OnListFragmentInteractionListener
        , RegistrationFragment.UserRegisterListener {
    private boolean mRegisterationSuccessfull = false;

    /** SharedPreferences by MIKE.  */
    private SharedPreferences mSharedPreferences;


    /**
     * Initializes fields and updates view for login.
     * Launches login fragment.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        if (!mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {

            // Check that the activity is using the layout version with
            // the fragment_container FrameLayout
            if (findViewById(R.id.fragment_container) != null) {

                // However, if we're being restored from a previous state,
                // then we don't need to do anything and should return or else
                // we could end up with overlapping fragments.
                if (savedInstanceState != null) {
                    return;
                }

                // Create a new Fragment to be placed in the activity layout
                LoginFragment loginFragment = new LoginFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                loginFragment.setArguments(getIntent().getExtras());
                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, loginFragment).commit();
            }
        }else{
            Intent i = new Intent(this, DashboardActivity.class);
            startActivity(i);
            finish();
        }


    }

    /**
     * Replaces current fragment with the fragment passed in.
     * @param fragment to change to.
     */
    @Override
    public void onListFragmentInteraction(Fragment fragment) {
        // Capture the course fragment from the activity layout

        FragmentManager fragmentManager = getSupportFragmentManager();;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    /**
     * Login Fragment Interface's overriden method.
     * it notifies activity that user logged in. Then,
     * Login Activity leads the user to next activity.
     * @param email
     */
    @Override
    public void login(String email, int id) {
        mSharedPreferences
                .edit()
                .putBoolean(getString(R.string.LOGGEDIN), true)
                .commit();
        Intent i = new Intent(this, DashboardActivity.class);
        i.putExtra("username", email);
        i.putExtra("id", id);
        startActivity(i);
        finish();

    }

    /**
     * Creates a new Registration task and executes it.
     * Starts dashboard activity if successful.
     * @param url for registration php file.
     */
    @Override
    public void registerUser(String url) {

            RegisterTask task = new RegisterTask();
            task.execute(new String[]{url.toString()});

            // Takes you back to the previous fragment by popping the current fragment out.
            //getSupportFragmentManager().popBackStackImmediate();
        if(mRegisterationSuccessfull) {
            Intent goToDashBoard = new Intent(this, DashboardActivity.class);
            mSharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), true).commit();
            startActivity(goToDashBoard);
            finish();
        }

    }

    /**
     * Task for registering a user.
     */
    private class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Attempts to register a user. Returns Json object.
         * @param urls url for registration php file.
         * @return json object.
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
                    for (String url : urls) {
                        try {
                            URL urlObject = new URL(url);
                            urlConnection = (HttpURLConnection) urlObject.openConnection();

                            InputStream content = urlConnection.getInputStream();

                            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                            String s = "";
                            while ((s = buffer.readLine()) != null) {
                                response += s;
                            }

                        } catch (Exception e) {
                    response = "Unable to add course, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * Receives json object and checks if registration was successful.
         * @param result json object returned.
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Registration is successful!"
                            , Toast.LENGTH_LONG)
                            .show();
                    mRegisterationSuccessfull = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to register: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
    }
}