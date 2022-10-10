package com.rafaelleal.android.coroutinesproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.rafaelleal.android.coroutinesproject.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext



// Github: https://github.com/Professor-Leal/CoroutinesProject

// Youtube:
// PlayList: Coroutines - Kotlin:                  https://www.youtube.com/playlist?list=PL5MykfKwtHeEx-Yr20aBIwKUj_ZmXDV8a
// Coroutines 01 - Introdução:                     https://youtu.be/O9GUBLB63W8
// Coroutines 02 - Contextos e funções complexas:  https://youtu.be/BmB-ISHhkb4
// Coroutines 03 - Escopos e construtores:         https://youtu.be/30F8j4efBHA
// Coroutines 04 - Jobs e cancelamento:            https://youtu.be/LrrXDVUG3f8

class MainActivity : AppCompatActivity() {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // ViewBinding     /////////////////////////////////////////////////////////////////////////////
    // https://developer.android.com/topic/libraries/view-binding?hl=pt-br#activities  /////////////
    private lateinit var binding: ActivityMainBinding
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Variável para Log
    val TAG = "Coroutines"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Binding /////////////////////////////////////////////////////////////////////////////////
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // Fim do Binding //////////////////////////////////////////////////////////////////////////

        setup()

    }

    // Configuração inicial:
    private fun setup() {
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLoad.setOnClickListener {
            load()
        }
        binding.btnDownload.setOnClickListener {
            downloadFiles()
            baixarImagem()
        }

        binding.btnCronometro.setOnClickListener {
            onCronometroButtonClick()
        }
    }

    // mostra o progressBar por 2 segundos
    fun load() {
        MainScope().launch {
            Log.i(TAG, "Mostra o progressBar")
            setPbVisibility(true)
            // Espera por 2 segundos
            //Thread.sleep(1000) // use delay em coroutines !!!
            delay(1000)
            Log.i(TAG, "Esperou 1 segundo")
            //Thread.sleep(1000) // use delay em coroutines !!!
            delay(1000)
            Log.i(TAG, "Esperou 2 segundos")
            setPbVisibility(false)
            Log.i(TAG, "Esconde o progressBar")
        }
    }

    // Muda a visibilidade do progressBar
    fun setPbVisibility(value: Boolean) {
        if (value) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    // Simula um download
    fun downloadFiles() {
        MainScope().launch(Dispatchers.IO) {
            displayText("Baixando")
            delay(1000)
            displayText("Baixando .")
            delay(1000)
            displayText("Baixando . .")
            delay(1000)
            displayText("Baixando . . .")
            delay(1000)
            displayText("Baixou!!!")
        }
    }

    // mostra texto na tela dentro de uma corrotina
    suspend fun displayText(value: String) {
        withContext(Dispatchers.Main) {
            binding.textView.text = value
            Log.i(TAG, value)
        }
    }

    // simula o download de uma imagem
    fun baixarImagem() {
        lifecycleScope.launch(Dispatchers.IO) {
            val imagem = async {
                delay(3000)
                displayText("baixou imagem")
                return@async R.drawable.ic_launcher_foreground
            }
            withContext(Dispatchers.Main) {
                // use o await() para esperar o retorno assíncrono
                binding.imageView.setImageResource(imagem.await())
            }
        }
    }

    // Cronômetro:
    var job: CoroutineContext? = null
    var tempo = 0

    // inicia o cronômetro parando após 5 segundos se não for cancelado
    fun iniciarCronometro(){
        binding.btnCronometro.text = "Parar"
        job = lifecycleScope.launch {
            try{
                withTimeout(5000) {
                    while (true) {
                        delay(1000)
                        tempo++
                        binding.tvCronometro.text = tempo.toString()
                    }
                }
            } catch (e: Exception){
                Log.i(TAG, "teste = ${e}")
                binding.btnCronometro.text = "Iniciar"
            }
        }
    }

    // Para o cronômetro cancelando o job
    fun finalizarCronometro(){
        if(job?.isActive == true ){
            job!!.cancel(CancellationException("Cancelled by user"))
            binding.btnCronometro.text = "Iniciar"
            job = null
        }
    }

    // evento de clique no botão de cronômetro
    fun onCronometroButtonClick(){
        if (job == null || job?.isActive == false ){
            iniciarCronometro()
        } else {
            finalizarCronometro()
        }
    }

    // inicialização de corrotina junto com o ciclo de vida da Activity
    init {
        lifecycleScope.launchWhenStarted {
            Log.i(TAG, "iniciou lifecycleScope no started")
        }
        lifecycleScope.launchWhenCreated {
            Log.i(TAG, "iniciou lifecycleScope no created")
        }
        lifecycleScope.launchWhenResumed {
            Log.i(TAG, "iniciou lifecycleScope no resumed")
        }
    }

}