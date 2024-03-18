package org.opendroneid.android.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.web3auth.core.Web3Auth;
import com.web3auth.core.types.BuildEnv;
import com.web3auth.core.types.ExtraLoginOptions;
import com.web3auth.core.types.LoginParams;
import com.web3auth.core.types.Network;
import com.web3auth.core.types.Provider;
import com.web3auth.core.types.Web3AuthOptions;
import com.web3auth.core.types.Web3AuthOptionsKt;
import com.web3auth.core.types.Web3AuthResponse;

import org.opendroneid.android.R;



import java.util.concurrent.CompletableFuture;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;


public class Web3AuthLogin {

    private Web3Auth AUTH;


    public PublishSubject<String> status = PublishSubject.create();
    public Web3Auth getAUTH() {
        return AUTH;
    }

    Web3AuthLogin(Context context, Intent intent){

        this.AUTH = new Web3Auth(new Web3AuthOptions(
                context,
                context.getString(R.string.web3auth_project_id),
                Network.SAPPHIRE_DEVNET,
                null,
                Uri.parse("org.opendroneid.android://auth"),
                Web3AuthOptionsKt.getSdkUrl(null),
                null,
                null,
                null,
                null,
                null,
                null,
                null));
        this.AUTH.setResultUrl(intent.getData());

        checkSession();

    }

    void checkSession()
    {
//        progressCircle.setVisibility(View.VISIBLE);
        CompletableFuture<Void> session_Response = this.AUTH.initialize();

        session_Response.whenComplete((response,error)-> {
            if (error == null) {
                Log.e("Tag", this.AUTH.getWeb3AuthResponse().toString());
                Log.e("Tag", String.valueOf(this.AUTH.getUserInfo()));
//                progressCircle.setVisibility(View.INVISIBLE);
                status.onNext("Success");
            }
            else {
                Log.e("Tag", "Error");
                status.onError(new Throwable("Error"));
//                progressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    Provider createProvider(String providerType)
    {
        Provider provider = null;

        switch (providerType){
            case "Google":
                provider = Provider.GOOGLE;
                break;
            case "Passwordless":
                provider = Provider.EMAIL_PASSWORDLESS;
                break;
        }
        return provider;
    }

    void loginWithProvider(Provider provider,String email)
    {
        ExtraLoginOptions extraLoginOptions = null;

        if(provider == Provider.EMAIL_PASSWORDLESS)
            extraLoginOptions = new ExtraLoginOptions(null,
                    null,null
                    ,null,null,
                    null,null,
                    null,null,
                    null,null,
                    null,email,
                    null,null,
                    null,null,
                    null,null,
                    null,null);


        CompletableFuture<Web3AuthResponse> loginFuture = this.AUTH.login(new LoginParams(provider,
                null,
                extraLoginOptions,
                null,
                null,
                null,
                null,
                null));

        loginFuture.whenComplete((web3AuthResponse, error) -> {
            if (error == null) {
                Log.e("Tag", "Success");
                status.onNext("Success");
            } else {
                Log.e("Tag", "Error");
                status.onError(new Throwable("Error"));
            }
        });

    }


    void logOut()
    {
        CompletableFuture<Void> future = this.AUTH.logout();

        future.whenComplete((web3AuthResponse, error) -> {
            if (error == null) {
                Log.e("Tag", "Successful log out");
                Log.e("Tag", web3AuthResponse.toString());
            } else {
                Log.e("Tag", "Error while log out");
            }
        });

    }



}
