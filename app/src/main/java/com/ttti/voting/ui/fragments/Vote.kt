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
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.subscription.SubscriptionConnectionParams
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.coreict.models.GetCandidateQuery
import com.coreict.models.GetVoteStatQuery
import com.ttti.voting.R
import com.ttti.voting.data.DataTree
import com.ttti.voting.ui.holder.AndroidTreeView
import com.ttti.voting.ui.holder.TreeNode
import com.ttti.voting.ui.holder.TreeNode.BaseNodeViewHolder
import com.ttti.voting.ui.holder.VoteItemHolder
import okhttp3.OkHttpClient
import org.json.JSONException
import java.util.concurrent.TimeUnit
import com.coreict.models.CreateVotesMutation
import com.coreict.models.CreateVoteStatsMutation
import com.ttti.voting.MainActivity
import com.ttti.voting.ui.LoginActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CandidateSelection.newInstance] factory method to
 * create an instance of this fragment.
 */
class Vote : Fragment() {

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
        val rootView: View = inflater.inflate(R.layout.fragment_vote, null, false)
        val containerView = rootView.findViewById<View>(R.id.treeviewcontainer) as ViewGroup

        BASE_URL = getString(R.string.graphql_server)
        WS_URL = getString(R.string.graphql_server_ws)
        val client = createSubscriptionApolloClient()

        var classLoginActivity = LoginActivity()
        userId = classLoginActivity.getPref(context!!, "Pref_User_ID")

       client.query(
                GetCandidateQuery
                    .builder()
                    .build()
            )
            .enqueue(object : ApolloCall.Callback<GetCandidateQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.e("DEBUG",e.message.toString())
                }
                override fun onResponse(response: Response<GetCandidateQuery.Data>) {
                    getActivity()?.runOnUiThread {
                        try {
                            val dataCount : Int = response.data()?.data()?.nodes()?.count() ?:0
                            if(dataCount > 0){
                                var mainTree:MutableList<Any> = ArrayList()
                                val root = TreeNode.root()
                                var categoryRoot = TreeNode.root()

                                for (i in 0 until dataCount) {
                                   var category : String = response?.data()?.data()?.nodes()?.get(i)?.category()?.category().toString()
                                   if(mainTree.contains(category)){
                                       val candidate = TreeNode(VoteItemHolder.IconTreeItem(R.string.none,
                                           DataTree(
                                               response?.data()?.data()?.nodes()?.get(i)?.candidate()?.id().toString(),
                                               response?.data()?.data()?.nodes()?.get(i)?.category()?.id().toString(),
                                               response?.data()?.data()?.nodes()?.get(i)?.candidate()?.firstName().toString(),
                                               response?.data()?.data()?.nodes()?.get(i)?.candidate()?.lastName().toString(),
                                               ""
                                           )))
                                       categoryRoot.addChildren(candidate)
                                   }else{
                                       mainTree.add(category)
                                       categoryRoot = TreeNode(VoteItemHolder.IconTreeItem(R.string.ic_person,
                                           DataTree(
                                               response?.data()?.data()?.nodes()?.get(i)?.category()?.id().toString(),
                                               response?.data()?.data()?.nodes()?.get(i)?.category()?.category().toString(),
                                               "",
                                               "",
                                               "")))
                                       val candidate = TreeNode(VoteItemHolder.IconTreeItem(R.string.none,
                                           DataTree(
                                               response?.data()?.data()?.nodes()?.get(i)?.candidate()?.id().toString(),
                                               response?.data()?.data()?.nodes()?.get(i)?.category()?.id().toString(),
                                               response?.data()?.data()?.nodes()?.get(i)?.candidate()?.firstName().toString(),
                                               response?.data()?.data()?.nodes()?.get(i)?.candidate()?.lastName().toString(),
                                           ""
                                           )))
                                       categoryRoot.addChildren(candidate)
                                       root.addChildren(categoryRoot)
                                   }
                                }
                                tView = AndroidTreeView(activity, root)
                                tView!!.setDefaultAnimation(true)
                                tView!!.setDefaultContainerStyle(R.style.TreeNodeStyleCustom)
                                tView!!.setDefaultViewHolder(VoteItemHolder::class.java)
                                containerView.addView(tView!!.view)
                                tView?.expandAll()

                                val  btnCast: TextView = rootView.findViewById(R.id.btnCast)
                                btnCast.setOnClickListener {
                                    var voteCast:ArrayList<Any> = ArrayList()
                                    var voteCategory:ArrayList<Any> = ArrayList()

                                    var notCast = ArrayList<String>()
                                    for (node in root.children){ // loops through category
                                        val tViewMain: BaseNodeViewHolder<*> = node.getViewHolder()
                                        val nodeviewMain = tViewMain.view
                                        val tvValue = nodeviewMain.findViewById(R.id.node_value) as TextView
                                        val categoryId = nodeviewMain.findViewById(R.id.node_tag) as TextView
                                        val arrerror = ArrayList<Boolean>()
                                        for (candidatenode in node.children){//loops through children
                                            val tView: BaseNodeViewHolder<*> = candidatenode.getViewHolder()
                                            val nodeview = tView.view
                                            val nodecheckbox = nodeview.findViewById(R.id.voteCheckBox) as CheckBox
                                            val candidateId = nodeview.findViewById(R.id.node_tag) as TextView
                                            arrerror.add(nodecheckbox.isChecked)
                                            if(nodecheckbox.isChecked){
                                                voteCategory.add(categoryId.text)
                                                voteCast.add(candidateId.text)
                                            }
                                        }
                                        if(arrerror.contains(true)){
                                        }else{
                                            notCast.add("Please cast a vote for " + tvValue.text + " category !")
                                        }
                                    }
                                    val errorOnCast = notCast.joinToString(separator = "\n")
                                    if(errorOnCast.isEmpty() || errorOnCast == null){
                                        //push to VoteCast Mutation and return if already voted
                                        client.query(
                                            GetVoteStatQuery
                                                .builder()
                                                .user(userId.toString())
                                                .build()
                                        )
                                            .enqueue(object : ApolloCall.Callback<GetVoteStatQuery.Data>() {
                                                override fun onFailure(e: ApolloException) {
                                                    Log.e("DEBUG",e.message.toString())
                                                }
                                                override fun onResponse(response: Response<GetVoteStatQuery.Data>) {
                                                    activity?.runOnUiThread {
                                                        try {
                                                            val dataCount : Int = response.data()?.data()?.nodes()?.count() ?:0
                                                            if(dataCount > 0){
                                                                val myToast = Toast.makeText(activity,"User already voted!", Toast.LENGTH_SHORT)
                                                                myToast.show()
                                                            }else{
                                                                client.mutate(
                                                                   CreateVoteStatsMutation
                                                                       .builder()
                                                                       .vote("true")
                                                                       .user(userId.toString())
                                                                       .build()
                                                                ).enqueue(object : ApolloCall.Callback<CreateVoteStatsMutation.Data>() {
                                                                    override fun onFailure(e: ApolloException) {
                                                                        Log.e("DEBUG",e.message.toString())
                                                                    }
                                                                    override fun onResponse(response: Response<CreateVoteStatsMutation.Data>) {
                                                                        try {
                                                                            val err : String? = response.data()?.data()?.__typename()
                                                                            if(err == "CreateError"){
                                                                                //error handler here
                                                                            }else{
                                                                                //after stat push votes

                                                                                for (i in voteCategory.indices){
                                                                                    //push vote
                                                                                    client.mutate(
                                                                                        CreateVotesMutation
                                                                                            .builder()
                                                                                            .category(voteCategory[i].toString())
                                                                                            .candidate(voteCast[i].toString())
                                                                                            .votes("1")
                                                                                            .build()
                                                                                    ).enqueue(object : ApolloCall.Callback<CreateVotesMutation.Data>() {
                                                                                        override fun onFailure(e: ApolloException) {
                                                                                            Log.e("DEBUG",e.message.toString())
                                                                                        }
                                                                                        override fun onResponse(response: Response<CreateVotesMutation.Data>) {
                                                                                            try {
                                                                                                val err : String? = response.data()?.data()?.__typename()
                                                                                                if(err == "CreateError"){
                                                                                                    //error handler here
                                                                                                }else{
                                                                                                    //successalert here
                                                                                                    Handler(Looper.getMainLooper()).post {
                                                                                                        val intent = Intent(activity,MainActivity::class.java).apply {
                                                                                                        }
                                                                                                        startActivity(intent)
                                                                                                    }
                                                                                                    //Log.d("Voting App",voteCategory[i].toString()+ "   Casted : "+ voteCast[i].toString())
                                                                                                }
                                                                                            } catch (e: JSONException) {
                                                                                                e.printStackTrace()
                                                                                            }
                                                                                        }
                                                                                    })
                                                                                }
                                                                                Handler(Looper.getMainLooper()).post {
                                                                                    val myToast = Toast.makeText(
                                                                                        activity,
                                                                                        "Successfully casted your vote!",
                                                                                        Toast.LENGTH_SHORT
                                                                                    )
                                                                                    myToast.show()
                                                                                }
                                                                            }
                                                                        } catch (e: JSONException) {
                                                                            e.printStackTrace()
                                                                        }
                                                                    }
                                                                })
                                                            }
                                                        } catch (e: JSONException) {
                                                            e.printStackTrace()
                                                        }
                                                    }
                                                }
                                            })

                                    }else{
                                        val myToast = Toast.makeText(activity, errorOnCast, Toast.LENGTH_SHORT)
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
         * @return A new instance of fragment CandidateSelection.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Vote().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

