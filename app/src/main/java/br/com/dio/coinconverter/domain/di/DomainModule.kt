package br.com.dio.coinconverter.domain.di

import br.com.dio.coinconverter.domain.GetExchangeValueUseCase
import br.com.dio.coinconverter.domain.ListExchangeUserCase
import br.com.dio.coinconverter.domain.SaveExchangeUserCase
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

object DomainModule {
    fun load() {
        loadKoinModules(useCaseModules())
    }

    private fun useCaseModules(): Module {
        return module {
            factory {
                GetExchangeValueUseCase(get())
            }

            factory {
                ListExchangeUserCase(get())
            }
            factory {
                SaveExchangeUserCase(get())
            }
        }
    }
}