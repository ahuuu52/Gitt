package com.example.gototrip

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_current2.*
import java.util.*
import java.util.jar.Manifest

class CurrentActivity : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    private var PERMISSION_ID =1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current2)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getpos.setOnClickListener {
            getLastLocation()
        }

    }

    private fun getLastLocation() {
        if (CheckPermission()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                   
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        getNewLocation()

                    } else {
                        Locationtxt.text =
                            "Your Current Coordinates are :\nLat:" + location.latitude + "; Long" + location.longitude +
                                    "\nYour City:"+getCityName(location.latitude,location.longitude)+", your Country:"+getCountryName(location.latitude,location.longitude)
                    }
                }
            } else {
                Toast.makeText(this, "Please Enabale your Location", Toast.LENGTH_SHORT).show()
            }
        } else {
            RequestPermission()
        }
    }




    private fun getNewLocation(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
           
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation = p0.lastLocation
            Locationtxt.text =  "Your Current Coordinates are :\nLat:" + lastLocation.latitude + "; Long" + lastLocation.longitude +
                    "\nYour City:"+getCityName(lastLocation.latitude,lastLocation.longitude)+", your Country:"+getCountryName(lastLocation.latitude,lastLocation.longitude)

        }
    }



    private fun CheckPermission():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
                 ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
                ){
            return true
        }
        return false
    }

    private fun RequestPermission(){
        //this function will allows us to tell the user to requesut the necessary permsiion if they are not garented
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled():Boolean{


        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getCityName(lat: Double,long: Double):String{
        var CityName =""
        var geoCorder = Geocoder(this,Locale.getDefault())
        var Adress = geoCorder.getFromLocation(lat,long,1)
        CityName = Adress.get(0).locality
        return CityName


    }

    private fun getCountryName(lat: Double,long: Double):String{
        var countryName = ""
        var geocoder = Geocoder(this,Locale.getDefault())
        var Adress = geocoder.getFromLocation(lat,long,1)

        countryName=Adress.get(0).countryName
        return countryName
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:","You have the Permission")
            }
        }
    }




}