package com.kassim.mapspokemongame

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val ACCESSLOCATION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        GetUserLocation()
        loadPockemon()

    }


    fun GetUserLocation() {

        Toast.makeText(this, "User location access", Toast.LENGTH_SHORT).show()

        var myLocation = MyLocationListener()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (Build.VERSION.SDK_INT >= 23) {

            if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)
                return

            }

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 14f, myLocation)

        var myThread = MyThread()
        myThread.start()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            ACCESSLOCATION -> {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetUserLocation()
                } else {

                    Toast.makeText(this, "User did not grant location access", Toast.LENGTH_SHORT).show()

                }

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

    }

    var location: Location? = null

    inner class MyLocationListener : LocationListener {


        constructor() {
            location = Location("Start")
            location!!.latitude = 0.0
            location!!.longitude = 0.0
        }

        override fun onLocationChanged(p0: Location) {
            location = p0
        }


    }


    var oldLocation: Location? = null
    inner class MyThread : Thread {

        constructor() : super() {
            oldLocation = Location("Start")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0

        }

        override fun run() {

            while (true) {

                try {

                    if (oldLocation!!.distanceTo(location)==0f) {
                        continue
                    }

                    oldLocation = location

                    runOnUiThread {

                        // for me
                        mMap!!.clear()
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet(" here is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.star_character)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                        // show pockemon

                        for (pocke in listOfPockemon) {

                            if (!pocke.isCatch) {
                                val pockemonLoc = LatLng(pocke.location!!.latitude, pocke.location!!.longitude)
                                mMap.addMarker(MarkerOptions()
                                        .position(pockemonLoc)
                                        .title(pocke.name)
                                        .snippet(pocke.des + " power ${pocke.power}")
                                        .icon(BitmapDescriptorFactory.fromResource(pocke.image!!)))
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                                if (location!!.distanceTo(pocke.location) < 20) {
                                    pocke.isCatch = true
                                    playerPower += pocke.power!!
                                    Toast.makeText(applicationContext, "You catch a new pockemon your new power is $playerPower", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }

                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    var listOfPockemon = arrayListOf<Pockemon>()

    var playerPower = 0.0
    fun loadPockemon() {
        listOfPockemon.add(Pockemon(R.drawable.villan_one,
                "Villan1", "Villan one living in japan", 55.0, -6.795, 39.266))
        listOfPockemon.add(Pockemon(R.drawable.villan_two, "Villan2",
                "Villan 2 living in USA", 90.5, -6.763743, 39.245342))
        listOfPockemon.add(Pockemon(R.drawable.villan_three, "Villan3",
                "Villan 3 living in Irag", 33.5, -6.769535 , 39.229757))
    }

}