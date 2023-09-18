package com.mhss.app.prayfirst.presentation.main

import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.mhss.app.prayfirst.R
import com.mhss.app.prayfirst.ui.theme.PrayFirstTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationCard(
    modifier: Modifier = Modifier,
    currentLocation: String,
    editMode: Boolean,
    loading: Boolean,
    onDetectLocation: () -> Unit,
    onClick: () -> Unit,
    onSearch: (String) -> Unit
) {
    var addressText by rememberSaveable { mutableStateOf("") }
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .defaultMinSize(minHeight = 16.dp, minWidth = 64.dp)
                .animateContentSize(spring(dampingRatio = 0.65f, stiffness = 300f))
                .wrapContentSize()
                .clickable { onClick() }
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!editMode) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.location
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = currentLocation,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                if (loading) {
                    CircularProgressIndicator(
                        Modifier
                            .padding(32.dp)
                            .size(78.dp),
                        strokeWidth = 10.dp
                    )
                } else {
                Button(
                    onClick = { onDetectLocation() },
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.gps), null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.detect_location),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(Modifier.weight(1f))
                    Text(
                        stringResource(R.string.or),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    Divider(Modifier.weight(1f))
                }
                OutlinedTextField(
                    value = addressText,
                    onValueChange = { addressText = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = {
                        Text(
                            stringResource(R.string.search_address),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { onSearch(addressText) }) {
                    Text(
                        text = stringResource(R.string.search),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                }
            }
        }
    }
}

fun Context.isGPSEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(
        LocationManager.GPS_PROVIDER
    )
}

fun Context.requestGPS(onSuccess: () -> Unit, onFailure: (ResolvableApiException) -> Unit) {

    val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 100).build()
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    val client = LocationServices.getSettingsClient(this)
    val task = client.checkLocationSettings(builder.build())

    task.addOnSuccessListener {
        onSuccess()
    }
    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            onFailure(exception)
            try {
            } catch (sendEx: IntentSender.SendIntentException) { }
        }
    }
}

@Preview
@Composable
fun LocationCardPreview() {
    PrayFirstTheme {
        LocationCard(
            currentLocation = "Mansoura, Egypt",
            editMode = false,
            loading = false,
            onDetectLocation = {},
            onClick = {},
            onSearch = {}
        )
    }
}

@Preview
@Composable
fun LocationCardExpandedPreview() {
    PrayFirstTheme {
        LocationCard(
            currentLocation = "Mansoura, Egypt",
            editMode = true,
            loading = true,
            onDetectLocation = {},
            onClick = {},
            onSearch = {}
        )
    }
}