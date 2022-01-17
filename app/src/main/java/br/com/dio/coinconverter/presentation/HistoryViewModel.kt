package br.com.dio.coinconverter.presentation

import androidx.lifecycle.*
import br.com.dio.coinconverter.data.model.ExchangeResponseValue
import br.com.dio.coinconverter.domain.ListExchangeUserCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val listExchangeUseCase: ListExchangeUserCase
) : ViewModel(), LifecycleObserver {
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state


    // coloco como privado, entao uso um padrao diferente.
    // toda vida que a activity passar no metodo onCreate ela excuta esse codigo da viewModel
    // toda vez que ela passar no onCreate ele vai executar esse metodo
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun getExchanges() {
        viewModelScope.launch {
            listExchangeUseCase()
                .flowOn(Dispatchers.Main)
                .onStart {

                    _state.value = State.Loading
                }
                .catch {
                    _state.value = State.Error(it)

                }
                .collect {
                    _state.value = State.Success(it)

                }
        }

    }


    sealed class State {
        object Loading : State()
        class Success(val listOfExchange: List<ExchangeResponseValue>) : State()
        data class Error(val error: Throwable) : State()
    }

}