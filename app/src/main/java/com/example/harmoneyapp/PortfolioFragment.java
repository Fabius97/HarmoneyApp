package com.example.harmoneyapp;


import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import drewcarlson.coingecko.models.coins.CoinMarkets;


public class PortfolioFragment extends Fragment {

    public PortfolioFragment() {

    }

    RecyclerView portfolioRecyclerView;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);

        portfolioRecyclerView = view.findViewById(R.id.recyclerView2);
        portfolioRecyclerView.setHasFixedSize(true);
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        assert user != null;
        fetchAssets(user, db, this, getViewLifecycleOwner(), items -> portfolioRecyclerView.setAdapter(new AdapterPortfolio(items)));

        return view;
    }


    private static void fetchAssets(FirebaseUser user, FirebaseFirestore db, ViewModelStoreOwner viewModelStoreOwner, LifecycleOwner lifecycleOwner, PortfolioItemCallback callback) {

        List<GetItemPortfolio> portfolioList = new ArrayList<>();

        DocumentReference docRef = db.collection("users").document(user.getUid())
                .collection("portfolio").document("asset_list");
        docRef.get().addOnCompleteListener(task -> {


            if (task.isSuccessful() && task.isComplete()) {
                Task<DocumentSnapshot> collectionReference = docRef.get();
                Log.d(TAG, "collectionReference data: " + collectionReference);
                DocumentSnapshot document = task.getResult();


                if (document.exists() && task.getResult() != null) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    PriceViewModel viewModel = new ViewModelProvider(viewModelStoreOwner).get(PriceViewModel.class);
                    Map<String, Object> data = document.getData();

                    List<String> tokenIdsList = new ArrayList<String>();

                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        String logo = "";
                        String symbol = "";

                        portfolioList.add(new GetItemPortfolio(logo, value.toString(), key, 0.0, symbol));
                        tokenIdsList.add(key.toLowerCase(Locale.ROOT));
                    }

                    String tokenIds = String.join(",", tokenIdsList).trim();

                    viewModel.getCoinMarkets("eur", tokenIds);
                    viewModel.getMarkets().observe(lifecycleOwner, markets -> {

                        List<CoinMarkets> m = markets.getMarkets();

                        m.forEach(market -> {
                            portfolioList.forEach(getItemPortfolio -> {
                                String key = getItemPortfolio.getNamePortfolio().toLowerCase(Locale.ROOT);

                                if (market.getName().toLowerCase(Locale.ROOT).equals(key)) {
                                    getItemPortfolio.setLogo(market.getImage());

                                    double price = market.getCurrentPrice();
                                    getItemPortfolio.setPrice(price);

                                    getItemPortfolio.setSymbol(market.getSymbol());
                                }
                            });
                        });

                        callback.onCallback(portfolioList);
                    });

                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
}
