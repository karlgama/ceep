package br.com.alura.ceep.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.alura.ceep.database.AppDatabase
import br.com.alura.ceep.databinding.ActivityListaNotasBinding
import br.com.alura.ceep.extensions.vaiPara
import br.com.alura.ceep.ui.recyclerview.adapter.ListaNotasAdapter
import br.com.alura.ceep.webclient.NotaWebClient
import br.com.alura.ceep.webclient.RetrofitInicializador
import br.com.alura.ceep.webclient.model.NotaResposta
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaNotasActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityListaNotasBinding.inflate(layoutInflater)
    }
    private val adapter by lazy {
        ListaNotasAdapter(this)
    }
    private val webClient by lazy{
        NotaWebClient()
    }
    private val dao by lazy {
        AppDatabase.instancia(this).notaDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configuraFab()
        configuraRecyclerView()
        lifecycleScope.launch {
            val notas = webClient.buscaTodas()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                buscaNotas()
            }
        }


//        retrofitSemCoroutines()
    }

//    private fun retrofitSemCoroutines() {
//        val call = RetrofitInicializador().notaService.buscaTodas()
//
//        lifecycleScope.launch(IO) {
//            val resposta = call.execute()
//            resposta.body()?.let { notasRespostas ->
//                var notas = notasRespostas.map { it.nota }
//                Log.i("ListaNotas", "onCreate: $notas")
//
//            }
//        }
//        call.enqueue(object : Callback<List<NotaResposta>?> {
//            override fun onResponse(
//                call: Call<List<NotaResposta>?>,
//                resposta: Response<List<NotaResposta>?>
//            ) {
//                resposta.body()?.let { notasRespostas ->
//                    var notas = notasRespostas.map { it.nota }
//                    Log.i("ListaNotas", "onCreate: $notas")
//
//                }
//            }
//
//            override fun onFailure(call: Call<List<NotaResposta>?>, t: Throwable) {
//                Log.e("ListaNotas", "onFailure:", t)
//            }
//        })
//    }

    private fun configuraFab() {
        binding.activityListaNotasFab.setOnClickListener {
            Intent(this, FormNotaActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun configuraRecyclerView() {
        binding.activityListaNotasRecyclerview.adapter = adapter
        adapter.quandoClicaNoItem = { nota ->
            vaiPara(FormNotaActivity::class.java) {
                putExtra(NOTA_ID, nota.id)
            }
        }
    }

    private suspend fun buscaNotas() {
        dao.buscaTodas()
            .collect { notasEncontradas ->
                binding.activityListaNotasMensagemSemNotas.visibility =
                    if (notasEncontradas.isEmpty()) {
                        binding.activityListaNotasRecyclerview.visibility = GONE
                        VISIBLE
                    } else {
                        binding.activityListaNotasRecyclerview.visibility = VISIBLE
                        adapter.atualiza(notasEncontradas)
                        GONE
                    }
            }
    }
}