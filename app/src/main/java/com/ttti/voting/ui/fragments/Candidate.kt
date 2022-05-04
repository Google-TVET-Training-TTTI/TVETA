package com.ttti.voting.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.toJson
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.subscription.SubscriptionConnectionParams
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.coreict.models.CreateCandidateMutation
import com.coreict.models.GetCategoriesQuery
import com.ttti.voting.R
import com.ttti.voting.data.DataTree
import com.ttti.voting.ui.LoginActivity
import com.ttti.voting.ui.holder.AndroidTreeView
import com.ttti.voting.ui.holder.CandidateItemHolder
import com.ttti.voting.ui.holder.TreeNode
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Candidate.newInstance] factory method to
 * create an instance of this fragment.
 */
class Candidate : Fragment() {

    private var BASE_URL = ""
    private var WS_URL= ""
    var userId: String? = null

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var tView: AndroidTreeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_candidate, null, false)
        val containerView = rootView.findViewById<View>(R.id.treeviewcontainer) as ViewGroup

        BASE_URL = getString(R.string.graphql_server)
        WS_URL = getString(R.string.graphql_server_ws)
        val client = createSubscriptionApolloClient()

        var classLoginActivity = LoginActivity()
        userId = classLoginActivity.getPref(context!!, "Pref_User_ID")

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
                                val root = TreeNode.root()
                                for (i in 0 until dataCount) {
                                    root.addChildren(TreeNode(CandidateItemHolder.IconTreeItem(R.string.ic_person,
                                        DataTree(response?.data()?.data()?.nodes()?.get(i)?.id().toString(),
                                        response?.data()?.data()?.nodes()?.get(i)?.category().toString(),
                                            "",
                                            "",
                                            ""))))
                                }
                                tView = AndroidTreeView(activity, root)
                                tView!!.setDefaultAnimation(true)
                                tView!!.setDefaultContainerStyle(R.style.TreeNodeStyleCustom)
                                tView!!.setDefaultViewHolder(CandidateItemHolder::class.java)
                                containerView.addView(tView!!.view)

                                val  btnCast: TextView = rootView.findViewById(R.id.btnSelect)
                                var selectedCandidatePosition: CharSequence? = null
                                btnCast.setOnClickListener {
                                    val arrerror = ArrayList<Boolean>()
                                    for (node in root.children){
                                        val tViewMain: TreeNode.BaseNodeViewHolder<*> = node.getViewHolder()
                                        val nodeviewMain = tViewMain.view
                                        val nodecheckbox = nodeviewMain.findViewById(R.id.voteCheckBox) as CheckBox
                                        val categoryId = nodeviewMain.findViewById(R.id.node_tag) as TextView
                                        arrerror.add(nodecheckbox.isChecked)
                                       if(nodecheckbox.isChecked){
                                           selectedCandidatePosition = categoryId.text;
                                        }
                                    }
                                    if(arrerror.contains(true)){
                                        //do save to db
                                        if(!userId.isNullOrEmpty()){
                                            client.mutate(
                                                CreateCandidateMutation
                                                    .builder()
                                                    .candidate(userId!!)
                                                    .category(selectedCandidatePosition as String)
                                                    .build()
                                            )
                                                .enqueue(object : ApolloCall.Callback<CreateCandidateMutation.Data>() {
                                                    override fun onFailure(e: ApolloException) {
                                                        Log.e("DEBUG",e.message.toString())
                                                    }
                                                    override fun onResponse(response: Response<CreateCandidateMutation.Data>) {
                                                            try {
                                                                val err : String? = response.data()?.data()?.__typename()
                                                                if(err == "CreateError"){

                                                                    var errormessage = response.data()?.toJson()
                                                                    val jObjectUserDetails = JSONObject(errormessage)
                                                                    val Obj =
                                                                        JSONObject(jObjectUserDetails.getString("data"))
                                                                    var res : JSONObject = Obj.get("data") as JSONObject
                                                                    var error = res.get("message")
                                                                    Handler(Looper.getMainLooper()).post {
                                                                        val myToast = Toast.makeText(
                                                                            activity,
                                                                            error.toString(),
                                                                            Toast.LENGTH_SHORT
                                                                        )
                                                                        myToast.show()
                                                                    }
                                                                }else{
                                                                    Handler(Looper.getMainLooper()).post {
                                                                        val myToast = Toast.makeText(
                                                                            activity,
                                                                            "Successfully saved choice!",
                                                                            Toast.LENGTH_SHORT
                                                                        )
                                                                        myToast.show()
                                                                    }
                                                                    Log.d("Voting App", "User"+ userId +"Selected Candidate : " + selectedCandidatePosition)
                                                                }
                                                            } catch (e: JSONException) {
                                                                e.printStackTrace()
                                                         }
                                                    }
                                                })
                                        }else{
                                            val intent = Intent(activity,LoginActivity::class.java).apply {
                                            }
                                            startActivity(intent)
                                        }

                                    }else{
                                        val myToast = Toast.makeText(activity,"Please select a candidate choice!", Toast.LENGTH_SHORT)
                                        myToast.show()
                                    }
                                }
                            }else{
                                //no data fetched!
                                val myToast = Toast.makeText(activity,"No Candidates on this location!", Toast.LENGTH_SHORT)
                                myToast.show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Candidate.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Candidate().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
