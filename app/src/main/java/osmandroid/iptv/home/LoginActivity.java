package osmandroid.iptv.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    EditText Emailfield,Passwordfield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);

        mAuth = FirebaseAuth.getInstance();
        Emailfield = findViewById(R.id.fieldEmail);
        Passwordfield = findViewById(R.id.fieldPassword);
        findViewById(R.id.SignInButton).setOnClickListener(this);
        findViewById(R.id.signup).setOnClickListener(this);
        findViewById(R.id.btn_frgtpswd).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, Main3Activity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = Emailfield.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Emailfield.setError("Required.");
            valid = false;
        } else {
            Emailfield.setError(null);
        }

        String password = Passwordfield.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Passwordfield.setError("Required.");
            valid = false;
        } else {
            Passwordfield.setError(null);
        }

        return valid;
    }

    //Signin
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);

                                }

                            }
                        }
                );
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.SignInButton:
                signIn(Emailfield.getText().toString(), Passwordfield.getText().toString());
                break;

            case R.id.signup:
                startActivity(new Intent(this,SignUpActivity.class));
                break;

            case R.id.btn_frgtpswd:
                startActivity(new Intent(this,SignUpActivity.class));
                break;
        }


    }


}

