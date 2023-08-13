package br.com.alura.ceep.webclient

import br.com.alura.ceep.model.Nota
import br.com.alura.ceep.webclient.model.NotaResposta

class NotaWebClient {
    suspend fun buscaTodas(): List<Nota> {
        val notasResposta = RetrofitInicializador().notaService
            .buscaTodas()

        return notasResposta.map(NotaResposta::nota)
    }
}