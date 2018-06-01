package pm.seniorhelp

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_finance.*
import objects.FinanceElement
import pm.adapters.FinanceRecipe
import pm.adapters.FinanceRecipeAdapter
import java.util.*


class FinanceActivity  : Activity() {

    private val menu: MutableList<FinanceElement> = mutableListOf()
    private val TAB = "Create Event"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance)
        options.visibility = View.INVISIBLE

        //val dateTime = this.intent.extras.get("DateTime") as Calendar.
        val dt = Date()
        val month = dt.getMonth() - 1
        val currentMonth = dt.getMonth() +1
        val mgreate = dt.getMonth() + 1
        Log.d(TAB,"OKOK: "+mgreate.toString())
        lesserMonth.text = month.toString()
        actualMonth.text = currentMonth.toString()
        greaterMonth.text = mgreate.toString()


        val bounds = progressBar.getProgressDrawable().getBounds()
        progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
        progressBar.getProgressDrawable().setBounds(bounds)
        //10 % de saldo Matematica
        progressBar.setProgress(10)

        //Write on database
        /*
        myRef.setValue("Hello, World!")


        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                Log.d("TESTE", "Value is: " + value!!)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TESTE", "Failed to read value.", error.toException())
            }
        })
        */


        addRendimento.setOnClickListener() {
            options.visibility = View.VISIBLE

            //TODO DO THIS WITH A LINK TO FIREBASE
            val recipeList = FinanceRecipe.getRecipesFromFile("pms-mei-export.json", this, "rendimentos")

            val adapter = FinanceRecipeAdapter(this, recipeList)
            listview.adapter = adapter
        }

        addDespesa.setOnClickListener() {
            options.visibility = View.VISIBLE

            //TODO DO THIS WITH A LINK TO FIREBASE
            val recipeList = FinanceRecipe.getRecipesFromFile("pms-mei-export.json", this, "despesas")

            val adapter = FinanceRecipeAdapter(this, recipeList)
            listview.adapter = adapter

        }

        listview.setOnItemClickListener { parent, view, position, id ->

            options.visibility = View.INVISIBLE
            //TODO link to database and insert values and update all structure of the page
        }


    }


}