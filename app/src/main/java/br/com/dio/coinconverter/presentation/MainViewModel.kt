package br.com.dio.coinconverter.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.dio.coinconverter.data.model.ExchangeResponseValue
import br.com.dio.coinconverter.domain.GetExchangeValueUseCase
import br.com.dio.coinconverter.domain.SaveExchangeUserCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainViewModel(
    private val saveExchangeUserCase: SaveExchangeUserCase,
    private val getExchangeValueUseCase: GetExchangeValueUseCase
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun getExchangeValue(coins: String) {
        viewModelScope.launch {
            getExchangeValueUseCase(coins)
                .flowOn(Dispatchers.IO)
                .onStart {

                    _state.value = State.Loading
                    // toda vez que esse userCase for iniciado ele vai cair no nosso onStart
                    // usar pra mostrar nossa dialog de progress
                }
                .catch {
                    _state.value = State.Error(it)
                    // der um erro

                }
                .collect {
                    _state.value = State.Success(it)
                    // receber o resultado da chamada
                    // sempre que der sucesso, mostrar um resultado


                }
        }

    }

    fun saveExchange(exchange: ExchangeResponseValue) {

        viewModelScope.launch {
            saveExchangeUserCase(exchange)
                .flowOn(Dispatchers.IO)
                .onStart {

                    _state.value = State.Loading
                    // toda vez que esse userCase for iniciado ele vai cair no nosso onStart
                    // usar pra mostrar nossa dialog de progress
                }
                .catch {
                    _state.value = State.Error(it)
                    // der um erro

                }
                .collect {
                    _state.value = State.Saved
                    // receber o resultado da chamada
                    // sempre que der sucesso, mostrar um resultado


                }
        }
    }


    sealed class State {
        object Loading : State()
        object Saved: State()
        data class Success(val exchange: ExchangeResponseValue) : State()
        data class Error(val error: Throwable) : State()
    }

}