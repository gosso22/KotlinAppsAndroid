package com.kassim.sunriseapp

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.kassim.sunriseapp.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        var rootView = binding.root

        setContentView(rootView)
    }

    inner class WeatherAsync: AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {

            try {

                val url = URL(params[0])
                val urlConnect = url.openConnection() as HttpURLConnection
                urlConnect.connectTimeout=7000

                var inString = convertStreamToString(urlConnect.inputStream)
                publishProgress(inString)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return " "

        }



        override fun onProgressUpdate(vararg values: String?) {

            try {

                var json = JSONObject(values[0])
                val query = json.getJSONObject("sys")
                val sunrise = query.getInt("sunrise")
                val timeZ = json.getInt("timezone")
                val date: Date = getDateInTheCountry(sunrise, timeZ)
                val sdf: SimpleDateFormat = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
                val suriseTime = sdf.format(date)
                binding.tvSunset.text = "Sunrise Time is $suriseTime"
                val country = Locale("", query.getString("country")).displayCountry

                binding.tvCountry.text = "In ${binding.etCityName.text.toString().capitalize()}, $country"

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun convertStreamToString(inputStream: InputStream) : String {
        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line: String
        var allString: String = ""

        try {
            do {
                line = bufferReader.readLine()

                if (line != null) {
                    allString += line
                }

            } while (line != null)
            inputStream.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }


        return allString
    }

    fun GetSunset(view: View) {

        var city = binding.etCityName.text.toString()

        // We are using the service from https://openweathermap.org/ you can register and get the api key from their website

        val url= "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=<<Remove the double <> and insert your api key here >>"

        WeatherAsync().execute(url)

    }

    fun getDateInTheCountry(sunrise: Int, timeZonInt: Int): Date {

        return Date(((sunrise.toLong() * 1000) - SimpleTimeZone.getDefault().getOffset(System.currentTimeMillis())) + (timeZonInt * 1000))

    }

}