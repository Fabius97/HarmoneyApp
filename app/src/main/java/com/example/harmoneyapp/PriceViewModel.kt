package com.example.harmoneyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import drewcarlson.coingecko.CoinGeckoClient
import drewcarlson.coingecko.models.coins.CoinFullData
import drewcarlson.coingecko.models.coins.CoinMarketsList
import drewcarlson.coingecko.models.coins.CoinPrice
import kotlinx.coroutines.launch


class PriceViewModel() : ViewModel() {

    private val coinGecko = CoinGeckoClient.create()

    private val _coinPrices = MutableLiveData<Map<String, CoinPrice>>()
    private val _coinMarkets = MutableLiveData<CoinMarketsList>()
    private val _coinData = MutableLiveData<CoinFullData>()

    val coinPrices: LiveData<Map<String, CoinPrice>>
        get() = _coinPrices

    val markets: LiveData<CoinMarketsList>
        get() = _coinMarkets

    val coinPrice: LiveData<CoinFullData>
        get() = _coinData



    fun getTokenPrices(ids: String, currencies: String) {
        viewModelScope.launch {
            _coinPrices.value = coinGecko.getPrice(
                ids, currencies,
                includeMarketCap = true,
                include24hrVol = true,
                include24hrChange = true
            )
        }
    }

    fun getCoinMarkets(currency: String) {
        viewModelScope.launch {
            _coinMarkets.value = coinGecko.getCoinMarkets(currency, sparkline = true)
        }
    }

    fun getCoinMarkets(currency: String, tokenIds: String) {
        viewModelScope.launch {
            _coinMarkets.value = coinGecko.getCoinMarkets(currency, sparkline = true, ids = tokenIds)
        }
    }

    fun getAsset(id: String) {
        viewModelScope.launch {
            _coinData.value = coinGecko.getCoinById(id, true, false, true, false, false, false)
        }
    }
}

