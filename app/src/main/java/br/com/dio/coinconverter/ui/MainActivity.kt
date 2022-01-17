package br.com.dio.coinconverter.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import br.com.dio.coinconverter.R
import br.com.dio.coinconverter.core.extensions.*
import br.com.dio.coinconverter.data.model.Coin
import br.com.dio.coinconverter.databinding.ActivityMainBinding
import br.com.dio.coinconverter.presentation.MainViewModel
import br.com.dio.coinconverter.ui.history.HistoryActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val dialog by lazy {
        createProgressDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bindingAdapters()
        bindListeners()
        bindObserver()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }


    private fun bindingAdapters() {
        val list = Coin.values()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)

        with(binding) {
            tvFrom.setAdapter(adapter)
            tvTo.setAdapter(adapter)

            tvFrom.setText(Coin.BRL.name, false)
            tvTo.setText(Coin.USD.name, false)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_history) {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }


    private fun bindListeners() {
        with(binding) {
            btConverter.setOnClickListener {
                it.hideSoftKeyboard()
                val search = "${tilFrom.text}-${tilTo.text}"

                viewModel.getExchangeValue(search)

            }

            tilValue.editText?.doAfterTextChanged {
                btConverter.isEnabled = it != null && it.toString().isNotEmpty()
                binding.btSave.isEnabled = false

            }
        }
    }

    private fun bindObserver() {
        viewModel.state.observe(this) {
            when (it) {
                is MainViewModel.State.Error -> {
                    // usando o is ele vai transformar o nosso viewModelState no viewModel erro por exemplo
                    dialog.dismiss()
                    createDialog {
                        setMessage(it.error.message)
                    }.show()
                }
                MainViewModel.State.Loading -> dialog.show()
                is MainViewModel.State.Success -> responseSuccess(it)
                MainViewModel.State.Saved -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage("Item salvo com sucesso!")
                    }.show()

                }
            }
        }
        binding.btSave.setOnClickListener {
            val value = viewModel.state.value
            (value as? MainViewModel.State.Success)?.let {
                // se o valor que estiver sendo carregado for um sucesso
                // entao eu vou simplesmente

                val bidValue = it.exchange.copy(bid = it.exchange.bid * binding.tilValue.text.toDouble())
                viewModel.saveExchange(bidValue)
            }
        }
    }

    private fun responseSuccess(it: MainViewModel.State.Success) {
        dialog.dismiss()
        binding.btSave.isEnabled = true

        val selectCoin = binding.tilTo.text
        val coin = Coin.getByName(selectCoin)
//        val coin = Coin.values().find { it.name == selectCoin } ?: Coin.BRL
        val result = it.exchange.bid * binding.tilValue.text.toDouble()
        binding.tvResult.text = result.formatCurrency(coin.locale)
    }

}