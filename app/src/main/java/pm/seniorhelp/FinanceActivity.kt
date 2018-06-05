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
    var despesa = false

    var previousMonth = dt.getMonth() - 1
    var currentMonth = dt.getMonth()
    var nextMonth = dt.getMonth() +1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance)
        options.visibility = View.INVISIBLE

        calculaDados(0)
        groupLesser.setOnClickListener {
            calculaDados(-1)
        }

        groupGreater.setOnClickListener {
            calculaDados(+1)
        }

        addRendimento.setOnClickListener() {
            options.visibility = View.VISIBLE
            despesa =false
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
            despesa = true

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

            if (despesa) {

                //val myRef = database.getReference("/app/finance/despesas/" + dt.year + "/" + dt.month+"/value")
                //myRef.setValue(value.text.toString().replace("€ ","").toDouble())
                val myRef = database.getReference("/app/finance/despesas/" + dt.year + "/" + dt.month+"/")
                myRef.push().setValue(value.text.toString().replace("€ ","").toDouble())

                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var despesa:Double = 0.0
                        snapshot.children.forEach { item : DataSnapshot ->
                            despesa.plus(item.value.toString().toDouble())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

            }
            else {
                val myRef = database.getReference("/app/finance/rendimentos/" + dt.year + "/" + dt.month+"/")
                myRef.push().setValue(value.text.toString().replace("€ ","").toDouble())

                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var rendimento:Double = 0.0
                        snapshot.children.forEach { item : DataSnapshot ->
                            rendimento.plus(item.value.toString().toDouble())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

            }

            options.visibility = View.INVISIBLE
        }


    }

    fun calculaDados(valor : Int)
    {

        if (valor>0) {
            //TODO passar para extenso
            previousMonth = currentMonth
            currentMonth = nextMonth
            nextMonth = nextMonth + 1;
        }

        else if(valor< 0)
        {
            currentMonth = previousMonth
            previousMonth = previousMonth - 1
            nextMonth = currentMonth;

        }
        lesserMonth.text = (previousMonth).toString()
        actualMonth.text = currentMonth.toString()
        greaterMonth.text = (nextMonth).toString()

        //ver se não precisa de estar no metodo principal
        val bounds = progressBar.getProgressDrawable().getBounds()
        progressBar.progressTintList = ColorStateList.valueOf(Color.GREEN)
        progressBar.getProgressDrawable().setBounds(bounds)

        //10 % de saldo Matematica
        progressBar.setProgress(80)
    }


}