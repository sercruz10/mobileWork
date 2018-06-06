package pm.seniorhelp

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_finance.*
import objects.FinanceElement
import pm.adapters.FinanceRecipe
import pm.adapters.FinanceRecipeAdapter
import java.math.BigDecimal
import java.util.*
import com.google.firebase.FirebaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener




class FinanceActivity  : Activity() {
//TODO alterar os dados da barra, saldo etc
    val dt = Date()
    val database = FirebaseDatabase.getInstance()
    var isDespesa = false

    var previousMonth = dt.getMonth() - 1
    var currentMonth = dt.getMonth()
    var nextMonth = dt.getMonth() +1

    var rendimento:Double = 0.0
    var despesa:Double = 0.0

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

        addRendimento.setOnClickListener() {
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

        addDespesa.setOnClickListener() {
            options.visibility = View.VISIBLE
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

            //val myRef = database.getReference("/app/finance/despesas/" + dt.year + "/" + dt.month+"/value")
            //myRef.setValue(value.text.toString().replace("€ ","").toDouble())
            val myRef = database.getReference("/app/finance/despesas/" + dt.year + "/" + currentMonth+"/")
            myRef.push().setValue(value.text.toString().replace("€ ","").toDouble())

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    despesa = 0.0
                    snapshot.children.forEach { item : DataSnapshot ->
                        despesa = despesa.plus(item.value.toString().toDouble())
                    }
                    calculaDados(0)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        }
        else if ("rendimento".equals(valor)){
            val myRef = database.getReference("/app/finance/rendimentos/" + dt.year + "/" + currentMonth+"/")
            myRef.push().setValue(value.text.toString().replace("€ ","").toDouble())

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    rendimento = 0.0
                    snapshot.children.forEach { item : DataSnapshot ->
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
        value.setText("€ 0")

        if (valor>0) {
            if (!nextMonth.equals(13)) {
                //TODO passar para extenso
                previousMonth = currentMonth
                currentMonth = nextMonth
                nextMonth = nextMonth + 1;
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
                nextMonth = currentMonth;
                currentMonth = previousMonth
                previousMonth = previousMonth - 1
            }
            else
            {
                nextMonth = 2;
                currentMonth = 1
            }
        }

        rendimentoValor.text = rendimento.toString()
        despesasValor.text = despesa.toString()
        saldoValor.text = (rendimento-despesa).toString()


        if (previousMonth.equals(0))
            lesserMonth.text = ("").toString()
        else
            lesserMonth.text = (previousMonth).toString()

        actualMonth.text = currentMonth.toString()


        if (greaterMonth.equals(13))
            greaterMonth.text = ("").toString()
        else
            greaterMonth.text = (nextMonth).toString()

        //ver se não precisa de estar no metodo principal
        val bounds = progressBar.getProgressDrawable().getBounds()
        progressBar.progressTintList = ColorStateList.valueOf(Color.GREEN)
        progressBar.getProgressDrawable().setBounds(bounds)

        if (despesa>=rendimento)
            progressBar.setProgress(0)
        else
            progressBar.setProgress(100-(despesa/rendimento*100).toInt())
    }


}