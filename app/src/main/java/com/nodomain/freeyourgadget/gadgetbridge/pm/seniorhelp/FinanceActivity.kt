package com.nodomain.freeyourgadget.gadgetbridge.pm.seniorhelp

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nodomain.freeyourgadget.gadgetbridge.R
import com.nodomain.freeyourgadget.gadgetbridge.pm.adapters.FinanceRecipe
import com.nodomain.freeyourgadget.gadgetbridge.pm.adapters.FinanceRecipeAdapter
import kotlinx.android.synthetic.main.activity_finance.*
import java.util.*


class FinanceActivity  : Activity() {
//TODO alterar os dados da barra, saldo etc

    var rightNow = Calendar.getInstance();

    val database = FirebaseDatabase.getInstance()
    var isDespesa = false

    var previousMonth = rightNow.get(Calendar.MONTH)
    var currentMonth = rightNow.get(Calendar.MONTH)+1
    var nextMonth = rightNow.get(Calendar.MONTH) + 2

    var rendimento:Double = 0.0
    var despesa:Double = 0.0

    var monthName = arrayOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance)
        options.visibility = View.INVISIBLE

        calculaDados(0)
        calculaFinancas("despesa")
        calculaFinancas("rendimento")

        groupLesser.setOnClickListener {
            calculaDados(-1)
            calculaFinancas("despesa")
            calculaFinancas("rendimento")
        }

        groupGreater.setOnClickListener {
            calculaDados(+1)
            calculaFinancas("despesa")
            calculaFinancas("rendimento")
        }

        addRendimento.setOnClickListener {
            options.visibility = View.VISIBLE
            isDespesa =false
            val myRef = database.getReference("/config/finance/rendimentos/")
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    //Log.d("JSon object",snapshot.value.toString());

                    var recipeList = ArrayList<FinanceRecipe>()
                    for (postSnapshot in snapshot.children)
                    {

                        var aux = FinanceRecipe(postSnapshot.key, postSnapshot.getValue<String>(String::class.java)!!)
                        recipeList.add(aux)
                    }

                    val adapter = FinanceRecipeAdapter(this@FinanceActivity, recipeList)
                    listview.adapter = adapter
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        addDespesa.setOnClickListener {
            options.visibility = View.VISIBLE
            value.isFocusable = true
            isDespesa = true

            val myRef = database.getReference("/config/finance/despesas/")
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    var recipeList = ArrayList<FinanceRecipe>()
                    for (postSnapshot in snapshot.children)
                    {

                        var aux = FinanceRecipe(postSnapshot.key, postSnapshot.getValue<String>(String::class.java)!!)
                        recipeList.add(aux)
                    }

                    val adapter = FinanceRecipeAdapter(this@FinanceActivity, recipeList)
                    listview.adapter = adapter
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }



        listview.setOnItemClickListener { parent, view, position, id ->

            if (isDespesa) {

                //val myRef = database.getReference("/app/finance/despesas/" + dt.year + "/" + dt.month+"/value")
                //myRef.setValue(value.text.toString().replace("€ ","").toDouble())
                calculaFinancas("despesa")

            }
            else {
                calculaFinancas("rendimento")

            }

            options.visibility = View.INVISIBLE
        }


    }

    fun calculaFinancas(valor : String)
    {
        if ("despesa".equals(valor)) {
            val myRef = database.getReference("/app/finance/despesas/" +rightNow.get(Calendar.YEAR)  + "/" + currentMonth+"/")
            val valor = value.text.toString().replace("€ ","")

            if (valor.isNotEmpty()) {
                myRef.push().setValue(valor.toDouble())
            }
                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        despesa = 0.0
                        snapshot.children.forEach { item: DataSnapshot ->
                            despesa = despesa.plus(item.value.toString().toDouble())
                        }
                        calculaDados(0)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })


        }
        else if ("rendimento".equals(valor)){
            val myRef = database.getReference("/app/finance/rendimentos/" + rightNow.get(Calendar.YEAR) + "/" + currentMonth+"/")
            val valor = value.text.toString().replace("€ ","")

            if (valor.isNotEmpty()) {
                myRef.push().setValue(valor.toDouble())
            }

                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        rendimento = 0.0
                        snapshot.children.forEach { item: DataSnapshot ->
                            rendimento = rendimento.plus(item.value.toString().toDouble())
                        }
                        calculaDados(0)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })


        }
    }


     fun calculaDados(valor : Int)
    {
        if (valor>0) {
            if (!nextMonth.equals(13)) {
                //TODO passar para extenso
                previousMonth = currentMonth
                currentMonth = nextMonth
                nextMonth = nextMonth + 1
            }
            else
            {
                previousMonth = 11
                currentMonth = 12
            }
        }

        else if(valor< 0)
        {
            if (!previousMonth.equals(0)) {
                nextMonth = currentMonth
                currentMonth = previousMonth
                previousMonth = previousMonth - 1
            }
            else
            {
                nextMonth = 2
                currentMonth = 1
            }
        }

        rendimentoValor.text = rendimento.toString()
        despesasValor.text = despesa.toString()
        saldoValor.text = (rendimento-despesa).toString()


        if (previousMonth.equals(0))
            lesserMonth.text = ("").toString()
        else
            lesserMonth.text = ( monthName[previousMonth-1]).toString()

        if (nextMonth.equals(13))
            greaterMonth.text = ("").toString()
        else
            greaterMonth.text = (monthName[nextMonth-1]).toString()


        actualMonth.text =  monthName[currentMonth-1].toString()

        //ver se não precisa de estar no metodo principal
        val bounds = progressBar.progressDrawable.bounds
        progressBar.progressTintList = ColorStateList.valueOf(Color.GREEN)
        progressBar.progressDrawable.bounds = bounds

        if (despesa==rendimento) {
            progressBar.progress =0

        }
        else     if (despesa>rendimento) {
            progressBar.progress = 100

            progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
        }
            else
        {
            progressBar.progress = 100-(despesa/rendimento*100).toInt()

        }
    }


}