package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import com.google.android.gms.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback{
    companion object {
        const val TAG = "SelectLocationFragment"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_select_location
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        // TODO: add the map setup implementation (x)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // TODO: zoom to the user location after taking his permission
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "locationSettingsLauncher - location settings request accepted")
                enableMyLocation()
            } else {
                Log.i(TAG, "locationSettingsLauncher - location settings request denied")
                Snackbar.make(
                    requireView(),
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartLocation()
                }.show()
            }
        }
        checkPermissionsAndEnableLocation()


        // TODO: add style to the map
        // TODO: put a marker to location that the user selected

        // TODO: call this function after the user confirms on the selected location
        onLocationSelected()
        return binding.root
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.i(TAG, "requestPermissionLauncher - granted")
                checkPermissionsAndEnableLocation()
            } else {
                Log.i(TAG, "requestPermissionLauncher - NOT granted")
                _viewModel.navigationCommand.value = NavigationCommand.Back
                _viewModel.showSnackBar.value = "Turn on location permission on application settings"
            }
        }

    private fun foregroundLocationPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun checkPermissionsAndEnableLocation() {
        Log.i(TAG, "checkPermissionsAndEnableLocation")
        if (foregroundLocationPermissionApproved()) {
            Log.i(TAG, "checkPermissionsAndEnableLocation- foreground permissions are ON")
            checkDeviceLocationSettingsAndStartLocation()
        } else {
            Log.i(TAG, "checkPermissionsAndEnableLocation - foreground permissions are OFF")
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun checkDeviceLocationSettingsAndStartLocation(resolve:Boolean = true) {
        Log.i(TAG, "checkDeviceLocationSettingsAndStartLocation")
        val locationRequest = LocationRequest.Builder(10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            Log.i(TAG, "checkDeviceLocationSettingsAndStartLocation - addOnFailureListener")
            if (exception is ResolvableApiException && resolve){
                try {
                    Log.i(TAG, "checkDeviceLocationSettingsAndStartLocation - attempting REQUEST_TURN_DEVICE_LOCATION_ON")
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    locationSettingsLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.i(TAG, "checkDeviceLocationSettingsAndStartLocation - attempt to REQUEST_TURN_DEVICE_LOCATION_ON failed")
                    _viewModel.showToast.value = "Error getting location settings resolution"
                }
            } else {
                _viewModel.navigationCommand.value = NavigationCommand.Back
                _viewModel.showToast.value = "Device location settings is required"
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            Log.i(TAG, "checkDeviceLocationSettingsAndStartLocation - addOnCompleteListener")
            if ( it.isSuccessful ) {
                Log.i(TAG, "checkDeviceLocationSettingsAndStartLocation - enabling myLocation")
                enableMyLocation()
            }
        }
    }

    private fun enableMyLocation() {
        Log.i(TAG, "enableMyLocation")
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            Log.i(TAG, "enableMyLocation - enabled")

            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                }
            }

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i(TAG, "onMapReady")
        mMap = googleMap
        setMapStyle(googleMap)
    }

    private fun onLocationSelected() {
        // TODO: When the user confirms on the selected location,
        //  send back the selected location details to the view model
        //  and navigate back to the previous fragment to save the reminder and add the geofence
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            true
        }
        R.id.hybrid_map -> {
            true
        }
        R.id.satellite_map -> {
            true
        }
        R.id.terrain_map -> {
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}

private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29