package com.example.videopoker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

        public static ArrayList<Card> deck = new ArrayList<>();
        public static ArrayList<Card> hand = new ArrayList<>();
        Boolean deal = true;
        public static int bet = 1;
        public static int totalCash;
        public static int pay;
        //create deck of cards
        public static void createDeck()
        {
            //create club cards
            deck = new ArrayList<>();
            for (int i = 0; i <= 12; i++)
            {
                Card card = new Card(i, 0);
                deck.add(card);
            }
            //create diamond cards
            for (int i = 0; i <= 12; i++)
            {
                Card card = new Card(i, 1);
                deck.add(card);
            }
            //create heart cards
            for (int i = 0; i <= 12; i++)
            {
                Card card = new Card(i, 2);
                deck.add(card);
            }
            //create spade cards
            for (int i = 0; i <= 12; i++)
            {
                Card card = new Card(i, 3);
                deck.add(card);
            }
        }

        //randomize deck
        public static void shuffleDeck()
        {
            Collections.shuffle(deck);
        }

        //get first 5 cards from deck
        public static void getHand()
        {
            hand = new ArrayList<>();
            for (int i = 0; i < 5; i++)
            {
                hand.add(deck.get(i));
                hand.get(i).setHeld(true);
            }
        }

        //replace cards flagged for swap
        public static void draw()
        {

            int next = 5;
            for (int i = 0; i < 5; i++)
            {
                if (!hand.get(i).getHeld())
                {
                    Card card = deck.get(next);
                    hand.set(i, card);
                    next++;
                }
            }
        }

        public void evaluateHand()
        {
            String message;
            //booleans for payout determination
            boolean royalflush = false;
            boolean straightflush = false;
            boolean fourkind = false;
            boolean fullhouse = false;
            boolean flush = true;
            boolean straight = false;
            boolean threekind = false;
            boolean pair1 = false;
            boolean pair2 = false;
            boolean royal = false;
            boolean jacks = false;
            Card one = hand.get(0);
            Card two = hand.get(1);
            Card three = hand.get(2);
            Card four = hand.get(3);
            Card five = hand.get(4);
            int[] ranks = {one.rank, two.rank, three.rank, four.rank, five.rank};
            Arrays.sort(ranks);

            //royal check
            if (ranks[0] == 0 && ranks[1] == 9 && ranks[2] == 10 && ranks[3] == 11 && ranks[4] == 12)
            {
                royal = true;
            }

            //straight check
            if (ranks[0] == (ranks[1] - 1)  && ranks[1] == (ranks[2] - 1) && ranks[2] == (ranks[3] - 1) && ranks[3] == (ranks[4] - 1))
            {
                straight = true;
            }

            //flush check
            for (int i = 1; i < 5; i++)
            {
                if (hand.get(0).suit != hand.get(i).suit) {
                    flush = false;
                    break;
                }
            }

            //kind check
            int[] fr = new int[ranks.length];
            int counted = -1;
            for (int i = 0; i < ranks.length; i++)
            {
                int count = 1;
                for (int i2 = i + 1; i2 < ranks.length; i2++)
                {
                    if (ranks[i] == ranks[i2])
                    {
                        System.out.println(ranks[i]);
                        System.out.println(ranks[i2]);
                        count++;
                        fr[i2] = counted;
                    }
                }
                if (fr[i] != counted)
                {
                    fr[i] = count;
                    if (fr[i] == 2)
                    {
                        if (ranks[i] >= 10 || ranks[i] ==0)
                            jacks = true;
                    }
                }

            }
            for (int i : fr) {
                if (i == 4)
                    fourkind = true;
                else if (i == 3)
                    threekind = true;
                else if (i == 2) {
                    if (pair1)
                        pair2 = true;
                    else
                        pair1 = true;
                }
            }

            if (flush && royal)
            {
                royalflush = true;
            }

            if (flush && straight)
            {
                straightflush = true;
            }

            if (threekind && pair1)
            {
                fullhouse = true;
            }

            if (royalflush) {

                if (bet == 5)
                    pay = 4000;
                else
                    pay = 250 * bet;
                message = "Royal Flush! Payout: " + pay;
            }
            else if (straightflush)
            {
                pay = 50 * bet;
                message = "Straight Flush! Payout: " + pay;
            }
            else if (fourkind) {
                pay = 25 * bet;
                message = "Four of a kind! Payout: " + pay;
            }
            else if (fullhouse) {
                pay = 9 * bet;
                message = "Full House! Payout: "+ pay;
            }
            else if (flush) {
                pay = 6 * bet;
                message = "Flush! Payout: " + pay;
            }
            else if (straight) {
                pay = 4 * bet;
                message = "Straight! Payout: " + pay;
            }
            else if (threekind) {
                pay = 3 * bet;
                message = "Three of a kind! Payout: " + pay;
            }
            else if (pair2) {
                pay = 2 * bet;
                message = "Two Pair! Payout: " + pay;
            }
            else if (jacks) {
                pay = bet;
                message = "Jacks or better! Payout: " + pay;
            }
            else {
                pay = 0;
                message = "Sorry, No winnings.";
            }
            totalCash += pay;
            TextView cashTxt = findViewById(R.id.cash);
            cashTxt.setText(Integer.toString(totalCash));
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("winnings", totalCash);
            editor.apply();
            message += "\n Press DEAL NEW HAND to play again.";
            Toast.makeText(this,message, Toast.LENGTH_LONG).show();
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        totalCash = sharedPref.getInt("winnings", 500);
        if (totalCash < 1)
            totalCash = 100;
        TextView cashTxt = findViewById(R.id.cash);
        TextView betTxt = findViewById(R.id.bet);
        cashTxt.setText(Integer.toString(totalCash));
        betTxt.setText(Integer.toString(bet));

        createDeck();

        ImageView card1 = findViewById(R.id.card1);
        ImageView card2 = findViewById(R.id.card2);
        ImageView card3 = findViewById(R.id.card3);
        ImageView card4 = findViewById(R.id.card4);
        ImageView card5 = findViewById(R.id.card5);
        card1.setClickable(false);
        card2.setClickable(false);
        card3.setClickable(false);
        card4.setClickable(false);
        card5.setClickable(false);

        Button betBtn = findViewById(R.id.betBtn);
        betBtn.setOnClickListener(v -> {
            if (bet <= 5)
                bet++;
            if (bet == 6)
                bet = 1;
            betTxt.setText(Integer.toString(bet));

        });
        Button dealBtn = findViewById(R.id.dealBtn);
        dealBtn.setOnClickListener(v -> {
            MediaPlayer shuffle = MediaPlayer.create(this, R.raw.shuffling);
            int resID;
            String imageName;
            if (deal) {
                deal = false;
                shuffleDeck();
                getHand();
                totalCash -= bet;
                cashTxt.setText(Integer.toString(totalCash));
                String s = "drawBtn";
                resID = getResources().getIdentifier(s, "string", "com.example.videopoker");
                dealBtn.setText(resID);
                betBtn.setClickable(false);
                shuffle.start();
                Handler h = new Handler();
                Runnable stopPlaybackRun = new Runnable() {
                    public void run(){
                        shuffle.stop();
                        shuffle.release();
                    }
                };
                h.postDelayed(stopPlaybackRun, 2 * 1000);
                Toast.makeText(this, "Select the cards you would like to swap, and press \"DRAW\".", Toast.LENGTH_SHORT).show();
                card1.setClickable(true);
                card2.setClickable(true);
                card3.setClickable(true);
                card4.setClickable(true);
                card5.setClickable(true);

            }
            else
            {
                draw();
                deal = true;
                String s = "dealBtn";
                resID = getResources().getIdentifier(s, "string", "com.example.videopoker");
                dealBtn.setText(resID);
                evaluateHand();
                betBtn.setClickable(true);
                card1.setClickable(false);
                card2.setClickable(false);
                card3.setClickable(false);
                card4.setClickable(false);
                card5.setClickable(false);

            }
            imageName = hand.get(0).getImage();
            resID = getResources().getIdentifier(imageName, "drawable", "com.example.videopoker");
            card1.setImageResource(resID);
            imageName = hand.get(1).getImage();
            resID = getResources().getIdentifier(imageName, "drawable", "com.example.videopoker");
            card2.setImageResource(resID);
            imageName = hand.get(2).getImage();
            resID = getResources().getIdentifier(imageName, "drawable", "com.example.videopoker");
            card3.setImageResource(resID);
            imageName = hand.get(3).getImage();
            resID = getResources().getIdentifier(imageName, "drawable", "com.example.videopoker");
            card4.setImageResource(resID);
            imageName = hand.get(4).getImage();
            resID = getResources().getIdentifier(imageName, "drawable", "com.example.videopoker");
            card5.setImageResource(resID);
        });

        card1.setOnClickListener(v -> {
            if (hand.get(0).getHeld())
            {
                hand.get(0).setHeld(false);
                card1.setImageResource(R.drawable.red_back200);
            }
            else if (!hand.get(0).getHeld())
            {
                hand.get(0).setHeld(true);
                String imageName = hand.get(0).getImage();
                int resID = getResources().getIdentifier(imageName, "drawable", "com.example.videopoker");
                card1.setImageResource(resID );
            }
        });

        card2.setOnClickListener(v -> {
            if (hand.get(1).getHeld())
            {
                hand.get(1).setHeld(false);
                card2.setImageResource(R.drawable.red_back200);
            }
            else if (!hand.get(1).getHeld())
            {
                hand.get(1).setHeld(true);
                String imageName = hand.get(1).getImage();
                int resID = getResources().getIdentifier(imageName, "drawable", "com.example.videopoker");
                card2.setImageResource(resID );
            }
        });

        card3.setOnClickListener(v -> {
            if (hand.get(2).getHeld())
            {
                hand.get(2).setHeld(false);
                card3.setImageResource(R.drawable.red_back200);
            }
            else if (!hand.get(2).getHeld())
            {
                hand.get(2).setHeld(true);
                String imageName = hand.get(2).getImage();
                int resID = getResources().getIdentifier(imageName, "drawable", "com.example.videopoker");
                card3.setImageResource(resID );
            }
        });

        card4.setOnClickListener(v -> {
            if (hand.get(3).getHeld())
            {
                hand.get(3).setHeld(false);
                card4.setImageResource(R.drawable.red_back200);
            }
            else if (!hand.get(3).getHeld())
            {
                hand.get(3).setHeld(true);
                String imageName = hand.get(3).getImage();
                int resID = getResources().getIdentifier(imageName, "drawable", "com.example.videopoker");
                card4.setImageResource(resID );
            }
        });

        card5.setOnClickListener(v -> {
            if (hand.get(4).getHeld())
            {
                hand.get(4).setHeld(false);
                card5.setImageResource(R.drawable.red_back200);
            }
            else if (!hand.get(4).getHeld())
            {
                hand.get(4).setHeld(true);
                String imageName = hand.get(4).getImage();
                int resID = getResources().getIdentifier(imageName, "drawable", "com.example.videopoker");
                card5.setImageResource(resID );
            }
        });
    }
}