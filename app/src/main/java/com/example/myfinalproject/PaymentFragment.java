package com.example.myfinalproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentFragment extends Fragment {

    Button stripeButton;
    EditText amountEditText;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret, amount;
    PaymentSheet.CustomerConfiguration customerConfig;

    ImageView paymentImage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        stripeButton = view.findViewById(R.id.stripeButton);
        amountEditText = view.findViewById(R.id.amountEditText);
        paymentImage = view.findViewById(R.id.paymentImage);

        paymentImage.setVisibility(View.GONE);

        if (getArguments() != null) {
            String totalPrice = getArguments().getString("totalPrice", "0.00");
            amountEditText.setText(totalPrice);
        }

        stripeButton.setOnClickListener(v -> {
            String amountText = amountEditText.getText().toString();
            if (TextUtils.isEmpty(amountText)) {
                Toast.makeText(getContext(), "Amount cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    double parsedAmount = Double.parseDouble(amountText);
                    int amountInCents = (int) Math.round(parsedAmount * 100);
                    amount = String.valueOf(amountInCents);
                    getDetails();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            }
        });

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        return view;
    }

    void getDetails() {
        Fuel.INSTANCE.post("https://strippayment-zsct3t7h6a-uc.a.run.app?amt=" + amount,null)
                .responseString(new Handler<String>() {
                    @Override
                    public void success(String s) {
                        try {
                            JSONObject result = new JSONObject(s);
                            customerConfig = new PaymentSheet.CustomerConfiguration(
                                    result.getString("customer"),
                                    result.getString("ephemeralKey")
                            );
                            paymentIntentClientSecret = result.getString("paymentIntent");
                            PaymentConfiguration.init(requireContext(), result.getString("publishableKey"));

                            requireActivity().runOnUiThread(() -> showStripePaymentSheet());

                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(@NonNull FuelError fuelError) {
                        Toast.makeText(getContext(), "Network error: " + fuelError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void showStripePaymentSheet() {
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("coDeR")
                .customer(customerConfig)
                .allowsDelayedPaymentMethods(true)
                .build();
        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                configuration
        );
    }

    void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(getContext(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
            amountEditText.setText("");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(getContext(), ((PaymentSheetResult.Failed) paymentSheetResult).getError().toString(), Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(getContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
            amountEditText.setText("");
            if (paymentImage != null) {
                paymentImage.setVisibility(View.VISIBLE);
            }
        }
    }
}
