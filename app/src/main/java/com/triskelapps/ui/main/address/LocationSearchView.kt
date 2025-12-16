package com.triskelapps.ui.main.address


import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.model.LatLng
import com.triskelapps.R
import com.triskelapps.databinding.FragmentAddressBinding
import com.triskelapps.databinding.LocationSearchViewBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class LocationResult(
    val name: String,
    val street: String?,
    val housenumber: String?,
    val postcode: String?,
    val city: String?,
    val lat: Double,
    val lon: Double
) {
    fun getLatLng() = LatLng(lat, lon)
}

class LocationSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LocationSearchViewBinding
    private var onLocationSelected: ((LocationResult) -> Unit)? = null
    private var onClear: (() -> Unit)? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {

        binding = LocationSearchViewBinding.inflate(LayoutInflater.from(context), this, true)

        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                performSearch(query)
            } else {
                Toast.makeText(context, "Introduce un término de búsqueda", Toast.LENGTH_SHORT).show()
            }
        }

        binding.icClearSearchText.setOnClickListener {
            binding.searchEditText.setText("")
            onClear?.invoke()
        }
    }

    fun setOnLocationSelectedListener(listener: (LocationResult) -> Unit) {
        onLocationSelected = listener
    }

    fun setOnClearListener(listener: () -> Unit) {
        onClear = listener
    }

    private fun performSearch(query: String) {
        val dialog = createLoadingDialog()
        dialog.show()

        coroutineScope.launch {
            try {
                val results = withContext(Dispatchers.IO) {
                    fetchLocations(query)
                }
                dialog.dismiss()
                if (results.size == 1) {
                    binding.searchEditText.setText(results[0].name)
                    onLocationSelected?.invoke(results[0])
                } else {
                    showResultsDialog(results)
                }
            } catch (e: Exception) {
                dialog.dismiss()
                Toast.makeText(context, "Error al buscar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createLoadingDialog(): AlertDialog {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null)
        return AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()
    }

    private fun fetchLocations(query: String): List<LocationResult> {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val urlString = "https://photon.komoot.io/api/?q=$encodedQuery&lat=36.690907551604724&lon=-6.131568600157266&lang=en"

        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        val responseCode = connection.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Error HTTP: $responseCode")
        }

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        return parseResults(response)
    }

    private fun parseResults(jsonString: String): List<LocationResult> {
        val results = mutableListOf<LocationResult>()
        val jsonObject = JSONObject(jsonString)
        val features = jsonObject.getJSONArray("features")

        for (i in 0 until features.length()) {
            val feature = features.getJSONObject(i)
            val properties = feature.getJSONObject("properties")
            val geometry = feature.getJSONObject("geometry")
            val coordinates = geometry.getJSONArray("coordinates")

            results.add(
                LocationResult(
                    name = properties.optString("name", "Sin nombre"),
                    street = properties.optString("street", null),
                    housenumber = properties.optString("housenumber", null),
                    postcode = properties.optString("postcode", null),
                    city = properties.optString("city", null),
                    lon = coordinates.getDouble(0),
                    lat = coordinates.getDouble(1)
                )
            )
        }
        return results
            .filter { it.city != null && it.city.contains("Jerez") }
            .distinctBy { it.name }
    }

    private fun showResultsDialog(results: List<LocationResult>) {
        if (results.isEmpty()) {
            Toast.makeText(context, "No se encontraron resultados", Toast.LENGTH_SHORT).show()
            return
        }

        val listView = ListView(context)
        val adapter = LocationAdapter(context, results)
        listView.adapter = adapter

        val dialog = AlertDialog.Builder(context)
            .setTitle("Resultados")
            .setView(listView)
            .setNegativeButton("Cancelar", null)
            .create()

        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = results[position]
            binding.searchEditText.setText(selected.name)
            onLocationSelected?.invoke(selected)
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        coroutineScope.cancel()
    }

    fun clearSearchText() {
        binding.searchEditText.setText("")
    }
}

class LocationAdapter(context: Context, private val locations: List<LocationResult>) :
    ArrayAdapter<LocationResult>(context, 0, locations) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.location_item, parent, false)

        val location = locations[position]
        val nameText = view.findViewById<TextView>(R.id.nameText)
        val addressText = view.findViewById<TextView>(R.id.addressText)

        nameText.text = location.name

        val addressParts = mutableListOf<String>()
        location.street?.let { addressParts.add(it) }
        location.housenumber?.let { addressParts.add(it) }
        location.postcode?.let { addressParts.add(it) }
        location.city?.let { addressParts.add(it) }

        addressText.text = addressParts.joinToString(", ")

        return view
    }
}