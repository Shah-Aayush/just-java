package com.example.justjava;
//AUTHOR : https://github.com/Shah-Aayush
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    int quantity = 2, pricePerCup = 5, price = 10;
    String message, name = "Aayush Shah";
    boolean addWhippedCream = false;
    boolean addChocolate = false;
    VideoView videoView;
    static boolean active = false;

    @Override
    public void onStart() {     //if user is back to the app then animation video will continue!
        super.onStart();
        active = true;
        videoView.setZOrderOnTop(true);
        videoView.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //rounded corners
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.video_view_container);
        frameLayout.setClipToOutline(true);

        //playing animation at bottom
        videoView = (VideoView) findViewById(R.id.coffee_video_view);
        videoView.setClipToOutline(true);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.justjavacoffeeanimation;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoView.setZOrderOnTop(true);     //getting rid of black screen before the video starts
        videoView.start();
        videoView.setBackground(Drawable.createFromPath("@null"));      // setting bg from matching bgcolor to null.
    }

    public void submitOrder(View view) {
        String mailMessage = createOrderSummary();
        String timerMessage = name + getString(R.string.coffee_is_ready);
        sendOrder(mailMessage);
    }

    public void increment(View view) {
        if(quantity<100){
            quantity++;
            price = calculatePrice();
            displayQuantity();
            updatePrice();
        }
        else{
            Toast.makeText(this, R.string.toast_message_atmost, Toast.LENGTH_LONG).show();
        }
    }

    public void decrement(View view) {
        if (quantity > 1) {
            quantity--;
            displayQuantity();
            updatePrice();
        } else {
            Toast.makeText(this, R.string.toast_message_atleast, Toast.LENGTH_LONG).show();
        }
    }

    private void displayQuantity() {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + quantity);
    }

    private void displayOrderSummary(String message) {
        TextView orderSummaryTextView = (TextView) findViewById(R.id.order_summary_text_view);
        orderSummaryTextView.setText(message);
    }

    private int calculatePrice() { 
        CheckBox chocolateCheckbox = (CheckBox) findViewById(R.id.chocolate_checkbox);
        CheckBox whippedCreamCheckbox = (CheckBox) findViewById(R.id.whipped_cream_checkbox);
        addChocolate = chocolateCheckbox.isChecked();
        addWhippedCream = whippedCreamCheckbox.isChecked();

        return quantity * (pricePerCup + ((addChocolate)?(2):(0)) + ((addWhippedCream)?(1):(0)) );
    }

    private String createOrderSummary() {
        EditText editText = (EditText) findViewById(R.id.name_field);
        name = editText.getText().toString();
        CheckBox whippedCreamCheckbox = (CheckBox) findViewById(R.id.whipped_cream_checkbox);
        CheckBox chocolateCheckbox = (CheckBox) findViewById(R.id.chocolate_checkbox);
        String returnMessage = getString(R.string.name)+ " : " + name + "\n";
        returnMessage += getString(R.string.whipped_cream_choice) + " : " + ((whippedCreamCheckbox.isChecked())?(getString(R.string.yes)):(getString(R.string.no))) + "\n";
        returnMessage += getString(R.string.chocolate_choice) + " : " + ((chocolateCheckbox.isChecked())?(getString(R.string.yes)):(getString(R.string.no))) + "\n";
        returnMessage += getString(R.string.quantity) + " : " + quantity + "\n";
        returnMessage += getString(R.string.total) + price + "\n";
        returnMessage += getString(R.string.thank_you);
        return returnMessage;
    }

    public void updatePrice(View view){
        updatePrice();
    }

    public void updatePrice(){
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        price = calculatePrice();
        message = "$" + (double) price;
        displayOrderSummary(message);
    }

    private void sendOrder(String mailMessage){
        composeEmail(new String[]{"aayushshah349@gmail.com"} , mailMessage);
    }

    private void composeEmail(String[] addresses, String mailMessage) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_for) + " " + name);
        intent.putExtra(Intent.EXTRA_TEXT   , mailMessage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent,getString(R.string.send_via)));
        }
        else{
            Toast.makeText(this, R.string.no_mail_clients, Toast.LENGTH_SHORT).show();
        }
    }

}