package pl.chipsoft.gesturewand.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pl.chipsoft.gesturewand.R;

public class LoginActivity extends Activity {

    private Button btnWithoutLogin;

    private View.OnClickListener onBtnWithoutLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnWithoutLogin = (Button) findViewById(R.id.alBtnWithoutLogin);

        btnWithoutLogin.setOnClickListener(onBtnWithoutLoginClick);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        MyApp.getInstance().onPause();
        super.onPause();
    }
}
