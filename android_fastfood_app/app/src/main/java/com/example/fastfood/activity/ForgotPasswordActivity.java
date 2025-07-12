package com.example.fastfood.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fastfood.R;
import com.example.fastfood.fragment.OtpFragment;
import com.example.fastfood.fragment.PhoneEntryFragment;
import com.example.fastfood.fragment.ResetPasswordFragment;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        if (savedInstanceState == null) {
            navigateToFragment(new PhoneEntryFragment(), false);
        }
    }

    private void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void navigateToOtpFragment(String phone) {
        navigateToFragment(OtpFragment.newInstance(phone), true);
    }

    public void navigateToResetPasswordFragment(String phone, String otp) {
        navigateToFragment(ResetPasswordFragment.newInstance(phone, otp), true);
    }

}