package com.ttti.voting.ui.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloSubscriptionCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.subscription.SubscriptionConnectionParams
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.coreict.models.GetCategoriesQuery
import com.coreict.models.GetRegisteredVotedQuery
import com.coreict.models.GetVotesByCategoryQuery
import com.coreict.models.NewVoteSubscription
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.ttti.voting.R
import com.ttti.voting.ui.LoginActivity
import okhttp3.OkHttpClient
import org.json.JSONException
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors


class DashboardFragment : Fragment() {

    private var BASE_URL = ""
    private var WS_URL= ""
    private var chart: BarChart? = null
    private var piechart: PieChart? = null
    private var scoreList = ArrayList<Score>()
    var userId: String? = null
    var userName: String? = null


    fun createSubscriptionApolloClient(): ApolloClient {
        val okHttpClient = OkHttpClient
            .Builder()
            .build()
        val subscriptionTransportFactory = WebSocketSubscriptionTransport.Factory(WS_URL, okHttpClient)
        val connectionParams: MutableMap<String, Any> = HashMap()
        connectionParams["Authorization"] = "Bearer Mugambi M."
        return ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .subscriptionHeartbeatTimeout(1000, TimeUnit.SECONDS)
            .subscriptionConnectionParams(SubscriptionConnectionParams(connectionParams))
            .subscriptionTransportFactory(subscriptionTransportFactory)
            .build()
    }
    inner class MyAxisFormatter(private val scList: java.util.ArrayList<Score>) : IndexAxisValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < scList.size) {
                scList[index].name
            } else {
                ""+index+" -  "+ scList.size
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root: View =  inflater.inflate(R.layout.fragment_dashboard, container, false)
        val activity = context
        BASE_URL = getString(R.string.graphql_server)
        WS_URL = getString(R.string.graphql_server_ws)

        val client = createSubscriptionApolloClient()

        val labelName = root.findViewById(R.id.txtUserName) as TextView
        var classLoginActivity = LoginActivity()

        userId = classLoginActivity.getPref(context!!, "Pref_User_ID")
        userName = classLoginActivity.getPref(context!!, "Pref_Username")

        if(!userId.isNullOrEmpty()){
            labelName.setText("Hi "+ userName)
        }
        val rl = root.findViewById(R.id.layDynContainer) as LinearLayout
       ////////////
        client.query(
            GetCategoriesQuery
                .builder()
                .build()
        )
            .enqueue(object : ApolloCall.Callback<GetCategoriesQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.e("DEBUG",e.message.toString())
                }
                override fun onResponse(response: Response<GetCategoriesQuery.Data>) {
                    getActivity()?.runOnUiThread {
                        try {
                            val dataCount : Int = response.data()?.data()?.nodes()?.count() ?:0
                            if(dataCount > 0){

                                for (i in 0 until dataCount) {
                                    var categoryName : String = response?.data()?.data()?.nodes()?.get(i)?.category().toString();
                                    var textView: TextView = TextView(activity);
                                    textView.setText(categoryName);
                                    textView.setTypeface(textView.getTypeface(), Typeface.BOLD)
                                    textView.setLayoutParams(
                                        FrameLayout.LayoutParams(
                                            FrameLayout.LayoutParams.MATCH_PARENT,
                                            80
                                        ));

                                    client.query(
                                            GetVotesByCategoryQuery
                                            .builder()
                                            .category(response?.data()?.data()?.nodes()?.get(i)?.id().toString())
                                            .build()
                                      )
                                        .enqueue(object : ApolloCall.Callback<GetVotesByCategoryQuery.Data>() {
                                            override fun onFailure(e: ApolloException) {
                                                Log.e("DEBUG",e.message.toString())
                                            }
                                            override fun onResponse(response: Response<GetVotesByCategoryQuery.Data>) {
                                                getActivity()?.runOnUiThread {
                                                    try {
                                                        val dataCount : Int = response.data()?.data()?.nodes()?.count() ?:0
                                                        // scoreList.clear()
                                                        scoreList = ArrayList<Score>()
                                                        if(dataCount > 0){
                                                            chart = BarChart(context!!)
                                                            chart?.tag = "chart_"+categoryName
                                                            chart?.setLayoutParams(
                                                                FrameLayout.LayoutParams(
                                                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                                                    600
                                                                ));
                                                            for (i in 0 until dataCount) {
                                                                var candidateName : String =
                                                                    response?.data()?.data()?.nodes()?.get(i)?.candidate()?.firstName().toString()
                                                                var candidateVotes : Int = response?.data()?.data()?.nodes()?.get(i)?.votes()!!
                                                                scoreList.add(Score(candidateName, candidateVotes))
                                                            }
                                                            scoreList = scoreList.stream().distinct().collect(Collectors.toList()) as ArrayList
                                                            val entries: ArrayList<BarEntry> = ArrayList()
                                                            for (i in scoreList.indices) {
                                                                val score = scoreList[i]
                                                                entries.add(BarEntry(i.toFloat(), score.score.toFloat()))
                                                            }
                                                            val barDataSet = BarDataSet(entries, "")
                                                            barDataSet.setColors(*ColorTemplate.VORDIPLOM_COLORS)
                                                            val data = BarData(barDataSet)
                                                            chart?.axisLeft?.setDrawGridLines(true)
                                                            chart?.xAxis?.setDrawGridLines(true)
                                                            chart?.xAxis?.setDrawAxisLine(false)
                                                            val xAxis: XAxis = chart!!.xAxis
                                                            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
                                                            xAxis.valueFormatter = MyAxisFormatter(scoreList)
                                                            xAxis.setDrawLabels(true)
                                                            xAxis.granularity = 1f
                                                            xAxis.labelRotationAngle = +90f
                                                            chart?.axisRight?.isEnabled = true
                                                            chart?.legend?.isEnabled = false
                                                            chart?.description?.isEnabled = false
                                                            chart?.animateY(3000)
                                                            chart?.data = data
                                                            rl.addView(textView)
                                                            rl.addView(chart)
                                                            //chart?.invalidate()
                                                        }else{
                                                            //no data fetched!
                                                        }
                                                       // scoreList.clear()
                                                    } catch (e: JSONException) {
                                                        e.printStackTrace()
                                                    }
                                                }
                                            }
                                        })
                                    ///Log.d("Voting App", "______________________________")
                                }

                            }else{
                                //no data fetched!
                                val myToast = Toast.makeText(activity,"No Categories on this location!", Toast.LENGTH_SHORT)
                                myToast.show()
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            })


        piechart = root.findViewById(R.id.piechart) as PieChart
        piechart?.setUsePercentValues(true)
        piechart?.description?.isEnabled = false
        val dataEntries = ArrayList<PieEntry>()

        client.query(
            GetRegisteredVotedQuery
                .builder()
                .build()
        )
            .enqueue(object : ApolloCall.Callback<GetRegisteredVotedQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.e("DEBUG",e.message.toString())
                }
                override fun onResponse(response: Response<GetRegisteredVotedQuery.Data>) {
                    getActivity()?.runOnUiThread {
                        try {
                            val voted : Float = response.data()?.data()?.voted()?.toFloat()!!
                            val registered : Float = response.data()?.data()?.registered()?.toFloat()!!

                            dataEntries.add(PieEntry(registered, "Registered Voters"))
                            dataEntries.add(PieEntry(voted, "Voted"))
                            val colors: ArrayList<Int> = ArrayList()
                            colors.add(Color.parseColor("#4DD0E1"))
                            colors.add(Color.parseColor("#FFF176"))
                            val dataSet = PieDataSet(dataEntries, "")
                            val data = PieData(dataSet)
                            data.setValueFormatter(PercentFormatter())
                            dataSet.sliceSpace = 10f
                            dataSet.colors = colors
                            piechart?.data = data
                            data.setValueTextSize(15f)
                            piechart?.setExtraOffsets(5f, 10f, 5f, 5f)
                            piechart?.animateY(7000, Easing.EaseInOutQuad)
                            piechart?.holeRadius = 58f
                            piechart?.transparentCircleRadius = 401f
                            piechart?.isDrawHoleEnabled = true
                            piechart?.setHoleColor(Color.WHITE)
                            piechart?.setDrawCenterText(true);
                            piechart?.centerText = "Voters"
                            piechart?.invalidate()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            })


       val call = client.subscribe(
            NewVoteSubscription
                .builder()
                .build()
        )
        call?.execute(object: ApolloSubscriptionCall.Callback<NewVoteSubscription.Data> {
            override fun onResponse(response: Response<NewVoteSubscription.Data>) {
                getActivity()?.runOnUiThread(java.lang.Runnable {
                    try{
                        val dataCount : Int = response.data()?.data()?.nodes()?.count() ?:0
                        if(dataCount > 0){
                            var candidateCategory : String =""
                            scoreList = ArrayList<Score>()
                            for (i in 0 until dataCount) {
                                 candidateCategory =
                                    response?.data()?.data()?.nodes()?.get(i)?.category()?.category().toString()
                                var candidateName : String =
                                    response?.data()?.data()?.nodes()?.get(i)?.candidate()?.firstName().toString()
                                var candidateVotes : Int = response?.data()?.data()?.nodes()?.get(i)?.votes()!!
                                scoreList.add(Score(candidateName, candidateVotes))
                            }
                            val barchart = root.findViewWithTag("chart_"+candidateCategory) as BarChart
                            scoreList = scoreList.stream().distinct().collect(Collectors.toList()) as ArrayList
                            val entries: ArrayList<BarEntry> = ArrayList()
                            for (i in scoreList.indices) {
                                entries.add(BarEntry(i.toFloat(), scoreList[i].score.toFloat()))
                            }
                            val barDataSet = BarDataSet(entries, "")
                            barDataSet.setColors(*ColorTemplate.VORDIPLOM_COLORS)
                            var brchtdata = barchart?.data
                            brchtdata.addDataSet(barDataSet)

                            barchart?.axisLeft?.setDrawGridLines(true)
                            barchart?.xAxis?.setDrawGridLines(true)
                            barchart?.xAxis?.setDrawAxisLine(false)
                            val xAxis: XAxis = barchart!!.xAxis
                            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
                            xAxis.valueFormatter = MyAxisFormatter(scoreList)
                            xAxis.setDrawLabels(true)
                            xAxis.granularity = 1f
                            xAxis.labelRotationAngle = +90f

                            barchart?.getData()?.notifyDataChanged();
                            barchart?.notifyDataSetChanged();
                            barchart?.invalidate()
                        }
                        Log.d("Voting App", "Received data!")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                })
            }
            override fun onCompleted() {
                Log.d("Voting App", "Completed!")
            }
            override fun onConnected() {
                Log.d("Voting App", "Connected to WS" )
            }
            override fun onFailure(e: ApolloException) {
                Log.d("Voting App", e.toString())
            }
            override fun onTerminated() {
                Log.d("Voting App", "Dis-connected from WS" )
            }
        })


/*
        val  btnOk: Button = root.findViewById(R.id.btnTest)
        btnOk.setOnClickListener {
            val barchart = root.findViewWithTag("chart_President") as BarChart

            val candidate : String = "Martin"
            val votes : Int = 4

            scoreList = ArrayList<Score>()
            scoreList.add(Score(candidate, votes))

            scoreList = scoreList.stream().distinct().collect(Collectors.toList()) as ArrayList

            val entries: ArrayList<BarEntry> = ArrayList()
            for (i in scoreList.indices) {
                entries.add(BarEntry(i.toFloat(), scoreList[i].score.toFloat()))
            }

            val barDataSet = BarDataSet(entries, "")
            barDataSet.setColors(*ColorTemplate.VORDIPLOM_COLORS)
            var brchtdata = barchart?.data
            brchtdata.addDataSet(barDataSet)

            Log.d("Voting App", brchtdata.dataSets .toString())

            barchart?.axisLeft?.setDrawGridLines(true)
            barchart?.xAxis?.setDrawGridLines(true)
            barchart?.xAxis?.setDrawAxisLine(false)
            val xAxis: XAxis = barchart!!.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
            xAxis.valueFormatter = MyAxisFormatter(scoreList)
            xAxis.setDrawLabels(true)
            xAxis.granularity = 1f
            xAxis.labelRotationAngle = +90f

            barchart?.getData()?.notifyDataChanged();
            barchart?.notifyDataSetChanged();
            barchart?.invalidate()
        }
*/
        return root
    }
}
data class Score(
    val name:String,
    val score: Int,
)
