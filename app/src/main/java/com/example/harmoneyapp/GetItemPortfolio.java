package com.example.harmoneyapp;

public class GetItemPortfolio {
    private String logo;
    private String amount;
    private String name;
    private double price;
    private String symbol;


    GetItemPortfolio(String logo, String amount, String name, double price, String symbol) {
        this.logo = logo;
        this.amount = amount;
        this.name = name;
        this.price = price;
        this.symbol = symbol;
    }

    public String getImageUrlPortfolio() {
        return logo;
    }

    public String getNamePortfolio() { return name; }

    public double getAssetPricePortfolio() {
        return price;
    }

    public String getAssetSymbolPortfolio() {
        return symbol;
    }

    public String getAmount() {
        return amount;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
