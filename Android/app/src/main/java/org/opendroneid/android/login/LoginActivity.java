package org.opendroneid.android.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.opendroneid.android.R;

public class LoginActivity extends AppCompatActivity {

    Web3AuthLogin authLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ConstraintLayout googleLogin = findViewById(R.id.btn_login_with_google);
        ConstraintLayout passwordlessLogin = findViewById(R.id.btn_login_passwordless);
        authLogin = new Web3AuthLogin(this,getIntent());

        googleLogin.setOnClickListener(btn->{
            authLogin.GoogleLogin();
//            Intent debugActivityIntent = new Intent(this, DebugActivity.class);
//            startActivity(debugActivityIntent);
        });

        passwordlessLogin.setOnClickListener(btn->{
            EditText edt_mail = findViewById(R.id.edtTxt_mail);
            String email = edt_mail.getText().toString();

            if(!email.isEmpty())
                authLogin.PasswordlessLogin(email);

        });



    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        authLogin.getAUTH().setResultUrl(intent.getData());
    }
}