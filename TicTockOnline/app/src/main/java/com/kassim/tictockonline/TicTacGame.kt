package com.kassim.tictockonline

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.kassim.tictockonline.databinding.FragmentTicTacGameBinding
import com.kassim.tictockonline.ui.login.LoginViewModel
import com.kassim.tictockonline.ui.login.LoginViewModelFactory
import java.util.*
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "username"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TicTacGame.newInstance] factory method to
 * create an instance of this fragment.
 */
class TicTacGame : Fragment() {

    private lateinit var binding: FragmentTicTacGameBinding
    private var activePlayer = 1
    private var player1Selected = arrayListOf<Int>()
    private var player2Selected = arrayListOf<Int>()
    private var numberOfWinsPlayer1: Int = 0
    private var numberOfWinsPlayer2: Int = 0
    private var player1UserName: String = "Player1"
    private var player2UserName: String = "Player2"
    private lateinit var tvOneWins: TextView
    private lateinit var tvTwoWins: TextView
    private lateinit var loginViewModel: LoginViewModel

    private var username: String? = null
    private var param2: String? = null
    private var loggedInUserName: String? = null
    private var loggedInUserEmail: String? = null
    private var playRequest: String? = null
    private var sessionId: String? = null
    private var playerSymbol: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tic_tac_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        binding = FragmentTicTacGameBinding.inflate(layoutInflater)

        tvOneWins = view.findViewById(R.id.tv_oneWins)
        tvTwoWins = view.findViewById(R.id.tv_twoWins)

        val nameOberver = Observer<String> { loggedInUser ->
            loggedInUserName = loggedInUser
            //tvOneWins.text = loggedInUser

        }

        val emailObserver = Observer<String> { loggedInEmail ->
            loggedInUserEmail = loggedInEmail
        }

        val button1 = view.findViewById<Button>(R.id.bu1)
        val button2 = view.findViewById<Button>(R.id.bu2)
        val button3 = view.findViewById<Button>(R.id.bu3)
        val button4 = view.findViewById<Button>(R.id.bu4)
        val button5 = view.findViewById<Button>(R.id.bu5)
        val button6 = view.findViewById<Button>(R.id.bu6)
        val button7 = view.findViewById<Button>(R.id.bu7)
        val button8 = view.findViewById<Button>(R.id.bu8)
        val button9 = view.findViewById<Button>(R.id.bu9)
        val etEmail = view.findViewById<EditText>(R.id.etOtherEmail)
        val acceptButton = view.findViewById<Button>(R.id.buAccept)
        val requestButton = view.findViewById<Button>(R.id.buRequest)

        var numberOfRequests = 0
        val playRequestObserver = Observer<String> { userRequest ->
            playRequest = userRequest
            etEmail.setText(playRequest)
            val notifyme = Notifications()
            notifyme.notify(requireActivity().applicationContext, "$userRequest wants to play tic tac toe", numberOfRequests)
            numberOfRequests ++

        }

        acceptButton.setOnClickListener {

            val userRequesting = splitString(playRequest.toString())
            loginViewModel.requestToPlay(userRequesting)
            playerOnline(userRequesting + splitString(loggedInUserEmail!!))
            playerSymbol = "O"
            loginViewModel.onlineGamePlay(sessionId!!)
            player1UserName = userRequesting
            tvOneWins.text = player1UserName
            player2UserName = splitString(loggedInUserEmail!!)
            tvTwoWins.text = player2UserName

        }

        requestButton.setOnClickListener {

            val requestUserName = splitString(etEmail.text.toString())

            if (!requestUserName.isEmpty()) {
                loginViewModel.requestToPlay(requestUserName)
                playerOnline(splitString(loggedInUserEmail!!) + requestUserName)
                playerSymbol = "X"
                loginViewModel.onlineGamePlay(sessionId!!)
                player2UserName = requestUserName
                tvTwoWins.text = player2UserName
                player1UserName = splitString(loggedInUserEmail!!)
                tvOneWins.text = player1UserName
            } else {
                Snackbar.make(
                    it,
                    "Please provide the email address of the user",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

        }

        button1.setOnClickListener(MyClickListener())
        button2.setOnClickListener(MyClickListener())
        button3.setOnClickListener(MyClickListener())
        button4.setOnClickListener(MyClickListener())
        button5.setOnClickListener(MyClickListener())
        button6.setOnClickListener(MyClickListener())
        button7.setOnClickListener(MyClickListener())
        button8.setOnClickListener(MyClickListener())
        button9.setOnClickListener(MyClickListener())

        loginViewModel.getUserName()
        loginViewModel.getUserEmail()
        loginViewModel.openPlayRequests()

        loginViewModel.loggedUserDetails.observe(viewLifecycleOwner, nameOberver)
        loginViewModel.loggedInUserEmail.observe(viewLifecycleOwner, emailObserver)
        loginViewModel.playRequests.observe(viewLifecycleOwner, playRequestObserver)
        createNotificationChannel()

    }

    fun playerOnline(sessionId: String) {

        this.sessionId = sessionId
        loginViewModel.currentPlay.observe(viewLifecycleOwner, { currentP ->
            try {
                player1Selected.clear()
                player2Selected.clear()

                if (currentP != null) {

                    var value: String
                    for(key in currentP.keys) {

                        value = currentP[key] as String

                        if (value != loggedInUserEmail) {

                            activePlayer = if (playerSymbol==="X") 2 else 1

                        } else {

                            activePlayer = if (playerSymbol==="X") 1 else 2

                        }
                        val cellID = key.replace("_", "")

                        autoPlay(cellID.toInt())

                    }

                }

            } catch (ex: Exception) {}
        })


    }

    fun gamePlay(cellId: Int, buSelected: Button) {

        if (activePlayer == 1) {

            buSelected.text = "X"
            buSelected.setBackgroundResource(R.color.blue)
            player1Selected.add(cellId)
            activePlayer = 2
            buSelected.isEnabled = false

        } else {

            buSelected.text = "O"
            buSelected.setBackgroundResource(R.color.dark_green)
            player2Selected.add(cellId)
            activePlayer = 1
            buSelected.isEnabled = false
        }

        checkWinner()

    }

    private fun checkWinner() {

        var winner = -1

        // Row 1

        if (player1Selected.contains(1) && player1Selected.contains(2) && player1Selected.contains(3)) {
            winner = 1
        }
        if (player2Selected.contains(1) && player2Selected.contains(2) && player2Selected.contains(3)) {
            winner = 2
        }

        // Row 2
        if (player1Selected.contains(4) && player1Selected.contains(5) && player1Selected.contains(6)) {
            winner = 1
        }
        if (player2Selected.contains(4) && player2Selected.contains(5) && player2Selected.contains(6)) {
            winner = 2
        }

        // Row 3
        if (player1Selected.contains(7) && player1Selected.contains(8) && player1Selected.contains(9)) {
            winner = 1
        }
        if (player2Selected.contains(7) && player2Selected.contains(8) && player2Selected.contains(9)) {
            winner = 2
        }

        // Col 1
        if (player1Selected.contains(1) && player1Selected.contains(4) && player1Selected.contains(7)) {
            winner = 1
        }
        if (player2Selected.contains(1) && player2Selected.contains(4) && player2Selected.contains(7)) {
            winner = 2
        }

        //Col 2
        if (player1Selected.contains(2) && player1Selected.contains(5) && player1Selected.contains(8)) {
            winner = 1
        }
        if (player2Selected.contains(2) && player2Selected.contains(5) && player2Selected.contains(8)) {
            winner = 2
        }

        // Col 3
        if (player1Selected.contains(3) && player1Selected.contains(6) && player1Selected.contains(9)) {
            winner = 1
        }
        if (player2Selected.contains(3) && player2Selected.contains(6) && player2Selected.contains(9)) {
            winner = 2
        }

        if (winner == 1) {

            numberOfWinsPlayer1++
            tvOneWins.text = String.format(
                getString(R.string.player1_win_statement),
                player1UserName,
                numberOfWinsPlayer1.toString()
            )
            resetGame()
        } else if (winner == 2) {
            numberOfWinsPlayer2++
            tvTwoWins.text = String.format(
                getString(R.string.player2_win_statement),
                player2UserName,
                numberOfWinsPlayer2.toString()
            )
            resetGame()
        }

    }

    private fun autoPlay(cellId: Int) {

        val buSelected: Button? = getSelectedButton(cellId)

        if (buSelected != null) {
            gamePlay(cellId, buSelected)
        }
    }

    private fun resetGame() {

        player1Selected.forEach {
            getSelectedButton(it)?.setBackgroundResource(R.color.white_background)
            getSelectedButton(it)?.text = ""
            getSelectedButton(it)?.isEnabled = true
        }

        player2Selected.forEach {
            getSelectedButton(it)?.setBackgroundResource(R.color.white_background)
            getSelectedButton(it)?.text = ""
            getSelectedButton(it)?.isEnabled = true
        }

        activePlayer = 1
        player1Selected.clear()
        player2Selected.clear()
        loginViewModel.resetGame()


    }

    private fun getSelectedButton(cellId: Int): Button? {

        return when (cellId) {

            1 -> view?.findViewById(R.id.bu1)
            2 -> view?.findViewById(R.id.bu2)
            3 -> view?.findViewById(R.id.bu3)
            4 -> view?.findViewById(R.id.bu4)
            5 -> view?.findViewById(R.id.bu5)
            6 -> view?.findViewById(R.id.bu6)
            7 -> view?.findViewById(R.id.bu7)
            8 -> view?.findViewById(R.id.bu8)
            9 -> view?.findViewById(R.id.bu9)
            else -> {
                view?.findViewById(R.id.bu1)
            }

        }

    }

    fun splitString(str: String): String {

        val splittedString = str.split("@")

        return splittedString[0]

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TicTacGame.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TicTacGame().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val CHANNEL_ID = 888
    }

    inner class MyClickListener : View.OnClickListener {
        override fun onClick(v: View?) {

            val buSelected = v as Button
            var cellId = 0
            when (v.id) {
                R.id.bu1 -> cellId = 1
                R.id.bu2 -> cellId = 2
                R.id.bu3 -> cellId = 3
                R.id.bu4 -> cellId = 4
                R.id.bu5 -> cellId = 5
                R.id.bu6 -> cellId = 6
                R.id.bu7 -> cellId = 7
                R.id.bu8 -> cellId = 8
                R.id.bu9 -> cellId = 9
            }

            //gamePlay(cellId, buSelected)
            loginViewModel.addButtonClicked(sessionId!!, cellId.toString(), loggedInUserEmail!!)
            //loginViewModel.onlineGamePlay(sessionId!!)

        }

    }

    fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_descrition)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("tictac_chann", name, importance).apply {
                description = descriptionText
            }

            val nM: NotificationManager =
                requireActivity()
                    .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nM.createNotificationChannel(channel)
        }
    }

}