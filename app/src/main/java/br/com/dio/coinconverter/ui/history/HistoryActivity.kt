package br.com.dio.coinconverter.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import br.com.dio.coinconverter.core.extensions.createDialog
import br.com.dio.coinconverter.core.extensions.createProgressDialog
import br.com.dio.coinconverter.databinding.ActivityHistoryBinding
import br.com.dio.coinconverter.presentation.HistoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryActivity : AppCompatActivity() {
    private val adapter by lazy { HistoryListAdapter() }
    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    private val viewModel by viewModel<HistoryViewModel>()
    private val dialog by lazy { createProgressDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvHistory.adapter = adapter
        binding.rvHistory.addItemDecoration(
            DividerItemDecoration(
                this, DividerItemDecoration.HORIZONTAL
            )
        )

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




        bindObserve()

        // eu tenho que dizer quem e esse lifecycle dessa activity
        // pro lifecycle dessa activity eu tenho que observar esse viewModel
        lifecycle.addObserver(viewModel)

    }

    private fun bindObserve() {

        viewModel.state.observe(this) {
            when (it) {
                is HistoryViewModel.State.Error -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage(it.error.message)
                    }.show()
                }
                HistoryViewModel.State.Loading -> dialog.show()
                is HistoryViewModel.State.Success -> {
                    dialog.dismiss()
                    adapter.submitList(it.listOfExchange)
                }
            }

        }
    }
}