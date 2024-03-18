package org.opendroneid.android.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.web3auth.core.types.Provider;

import org.opendroneid.android.R;
import org.opendroneid.android.app.DebugActivity;

import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class LoginActivity extends AppCompatActivity {

    private Web3AuthLogin authLogin;
    private ProgressBar loginProgress;

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

        loginProgress = findViewById(R.id.login_progress);
        ConstraintLayout googleLogin = findViewById(R.id.btn_login_with_google);
        ConstraintLayout passwordlessLogin = findViewById(R.id.btn_login_passwordless);
        authLogin = new Web3AuthLogin(this,getIntent());


        googleLogin.setOnClickListener(btn->{
            loginProgress.setVisibility(View.VISIBLE);
            Provider provider = authLogin.createProvider("Google");
            authLogin.loginWithProvider(provider,null);
        });

        passwordlessLogin.setOnClickListener(btn->{
            EditText edt_mail = findViewById(R.id.edtTxt_mail);
            String email = edt_mail.getText().toString();

            if(!email.isEmpty()) {
                loginProgress.setVisibility(View.VISIBLE);
                Provider provider = authLogin.createProvider("Passwordless");
                authLogin.loginWithProvider(provider,email);
            }
        });

        observeStatus();
    }

    void observeStatus()
    {
        Disposable d = authLogin.status.subscribe(value->{
            if(value.equals("Success")) {
                startDebugActivity();
                loginProgress.setVisibility(View.INVISIBLE);
            }
        }, throwable -> {
            Log.e("Tag","Error");
            loginProgress.setVisibility(View.INVISIBLE);
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
        });

    }

    void startDebugActivity(){
        Intent debugActivityIntent = new Intent(this, DebugActivity.class);
        startActivity(debugActivityIntent);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        authLogin.getAUTH().setResultUrl(intent.getData());
    }
}