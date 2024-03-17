package org.opendroneid.android.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.web3auth.core.Web3Auth;
import com.web3auth.core.types.BuildEnv;
import com.web3auth.core.types.LoginParams;
import com.web3auth.core.types.Network;
import com.web3auth.core.types.Provider;
import com.web3auth.core.types.Web3AuthOptions;
import com.web3auth.core.types.Web3AuthResponse;

import org.opendroneid.android.R;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.concurrent.CompletableFuture;

public class Web3AuthLogin {

    private Web3Auth AUTH;
    private Credentials credentials;
    private Web3j web3;

    public Web3Auth getAUTH() {
        return AUTH;
    }

    Web3AuthLogin(Context context, Intent intent){


        this.AUTH = new Web3Auth(new Web3AuthOptions(
                context,
                context.getString(R.string.web3auth_project_id),
                Network.SAPPHIRE_MAINNET,
                BuildEnv.PRODUCTION,
                Uri.parse("org.opendroneid.android://auth"),
                "",
                null,
                null,
                null,
                null,
                null,
                null,
                null));
        this.AUTH.setResultUrl(intent.getData());

        CompletableFuture<Void> session_Response = this.AUTH.initialize();

        session_Response.whenComplete((response,error)->{
            if (error == null) {
                Log.e("PrivKey: " ,this.AUTH.getPrivkey());
                Log.e("ed25519PrivKey: " ,this.AUTH.getEd25519PrivKey());
                Log.e("Web3Auth UserInfo", String.valueOf(this.AUTH.getUserInfo()));
                credentials = Credentials.create(this.AUTH.getPrivkey());
                web3 = Web3j.build(new HttpService("https://rpc.ankr.com/eth_sepolia"));
            } else {
                Log.d("MainActivity_Web3Auth", "Something went wrong");
                // Ideally, you should initiate the login function here.
            }
        });

    }

    void GoogleLogin()
    {
        Provider provider = Provider.GOOGLE;

        CompletableFuture<Web3AuthResponse> loginFuture = this.AUTH.login(new LoginParams(provider,
                null,
                null,
                null,
                null,
                null,
                null,
                null));

        loginFuture.whenComplete((web3AuthResponse, error) -> {
            if (error == null) {
                Log.e("Tag", "Success");
                Log.e("Tag", web3AuthResponse.toString());
                credentials = Credentials.create(this.AUTH.getPrivkey());
                web3 = Web3j.build(new HttpService("https://rpc.ankr.com/eth_sepolia"));
            } else {
                Log.e("Tag", "Error");
            }
        });
    }

    void PasswordlessLogin(String email)
    {
        Provider provider = Provider.EMAIL_PASSWORDLESS;
        LoginParams loginParams = new LoginParams(provider,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        CompletableFuture<Web3AuthResponse> loginFuture = this.AUTH.login(loginParams);

        loginFuture.whenComplete((web3AuthResponse, error) -> {
            if (error == null) {
                Log.e("Tag", "Success");
                Log.e("Tag", web3AuthResponse.toString());
            } else {
                Log.e("Tag", "Error");
            }
        });

    }



}
