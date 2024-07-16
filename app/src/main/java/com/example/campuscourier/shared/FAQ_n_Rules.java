package com.example.campuscourier.shared;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.campuscourier.R;

public class FAQ_n_Rules extends AppCompatActivity {

    Button buttonBackToProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.set(this, "NeutralAppTheme");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_nrules);

        buttonBackToProfile = findViewById(R.id.buttonBackToProfile);
        buttonBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
                finish();
            }
        });

        TextView faqTitle = findViewById(R.id.faqTitle);
        faqTitle.setText("Frequently Asked Questions");

        TextView telegramLinkTextView = findViewById(R.id.telegramLinkTextView);
        String fullText = "Our Customer Service can be accessed by clicking on Telegram:@Lightbulbq";

        SpannableString spannableStringTelegram = new SpannableString(fullText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent telegramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Lightbulbq"));
                startActivity(telegramIntent);
            }
        };
        int startIndex = fullText.indexOf("@Lightbulbq");
        int endIndex = startIndex + "@Lightbulbq".length();
        spannableStringTelegram.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        telegramLinkTextView.setText(spannableStringTelegram);
        telegramLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView textView = findViewById(R.id.payment_content);
        String text = textView.getText().toString();

        SpannableString spannableStringBold = new SpannableString(text);
        int boldStartIndex = text.indexOf("Additional $1.50 For Delivery Fee!");
        int boldEndIndex = boldStartIndex + "Additional $1.50 For Delivery Fee!".length();
        spannableStringBold.setSpan(new StyleSpan(Typeface.BOLD), boldStartIndex, boldEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableStringBold);



        TextView reportContentTextView = findViewById(R.id.report_points);

        String bulletPoints = "<ul>" +
                "<li><b>Late:</b> -5 points</li>" +
                "<li><b>No Show:</b> -20 points</li>" +
                "<li><b>Wrong Item:</b> -10 points</li>" +
                "<li><b>Payment Amount:</b> -15 points</li>" +
                "<li><b>Others:</b> Subjected to Severity</li>" +
                "</ul>";

        Spanned spannedText = Html.fromHtml(bulletPoints);
        reportContentTextView.setText(spannedText);
    }
}
