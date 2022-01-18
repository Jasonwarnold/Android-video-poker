package com.example.videopoker;

public class Card {public int rank;
    public int suit;
    public String name;
    public String image;
    public Boolean held;
    String[] rankname = {"Ace", "Deuce", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King"};
    String[] suitname = {"Clubs", "Diamonds", "Hearts", "Spades"};

    public Card()
    {
        rank = 0;
        suit = 0;
        name = "";
        image = "";
        held = true;
    }

    public Card(int r, int s)
    {
        rank = r;
        suit = s;
        setImage();
        setName();
        held = true;
    }

    public void setRank(int rnk)
    {
        rank = rnk;
    }

    public int getRank()
    {
        return rank;
    }

    public void setSuit(int st)
    {
        suit = st;
    }

    public int getSuit()
    {
        return suit;
    }



    public void setName()
    {
        int r = getRank();
        int s = getSuit();
        name = rankname[r] + " of " + suitname[s];
    }

    public String getName()
    {
        return name;
    }

    public void setImage()
    {
        String r = Integer.toString(getRank());
        String s = "c";
        int si = getSuit();
        switch (si) {
            case 0:
                s = "c";
                break;
            case 1:
                s = "d";
                break;
            case 2:
                s = "h";
                break;
            case 3:
                s = "s";
                break;
            default:
                break;
        }
        image = s + r;
    }

    public String getImage()
    {
        return image;
    }

    public void setHeld(Boolean h)
    {
        held = h;
    }

    public Boolean getHeld()
    {
        return held;
    }

    public void display()
    {
        System.out.println(name);
    }
}