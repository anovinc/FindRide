package com.example.findride.interactor

import android.content.Context
import androidx.compose.ui.text.toUpperCase
import com.example.findride.common.*
import com.example.findride.data.SharedPrefs
import com.example.findride.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RidesInteractorImpl @Inject constructor(
  private val firestore: FirebaseFirestore, private val sharedPreferences:
  SharedPrefs
) : RidesInteractor {
  private var name = ""
  private var surname = ""
  private var profileImage = ""
  private var phoneNumber = ""
  private lateinit var requestor: User
  private lateinit var bookingRide: Ride
  private lateinit var bookingRequest: RideBooking
  
  override suspend fun createNewRide(newRideState: NewRideState, onResult: (Boolean) -> Unit) {
    withContext(Dispatchers.IO) {
      val userRef = sharedPreferences.getUser()?.let { firestore.collection(USERS).document(it) }
      userRef?.get()?.addOnSuccessListener { documentSnapshot ->
        val user = documentSnapshot.toObject<User>()
        if (user != null) {
          name = user.name
          surname = user.surname
          phoneNumber = user.phoneNumber
          profileImage = user.profileImage
          
        }
        val id = UUID.randomUUID().toString()
        val ride = sharedPreferences.getUserId()?.let {
          Ride(
            id,
            it,
            name,
            surname,
            phoneNumber,
            profileImage,
            newRideState.day,
            newRideState.month,
            newRideState.year,
            newRideState.timeInSec,
            newRideState.startDestinationLat,
            newRideState.startDestinationLong,
            newRideState.endDestinationLat,
            newRideState.endDestinationLong,
            newRideState.numOfAvailableSeats,
            newRideState.desc,
            newRideState.price
          )
        }
        if (ride != null) {
          firestore.collection(RIDES).document(id).set(ride).addOnSuccessListener {
            onResult(true)
          }
            .addOnFailureListener {
              onResult(false)
            }
        }
      }
    }
    
  }
  
  override suspend fun getAllRides() = withContext(Dispatchers.IO) {
    suspendCoroutine<List<Ride>> { continuation ->
      firestore.collection(RIDES).get()
        .addOnFailureListener {
          continuation.resumeWithException(it)
        }
        .addOnSuccessListener { documents ->
          val rides = arrayListOf<Ride>()
          val _rides = documents.toObjects<Ride>()
          rides.addAll(_rides)
          rides.sortWith(compareBy({ it.year }, { it.month }, { it.day }, { it.time }))
          rides.removeIf { ride ->
            ride.authorId == sharedPreferences.getUserId()
          }
          rides.removeIf {
            !isDateValid(it.day, it.month, it.year)
          }
          continuation.resume(rides)
        }
    }
  }
  
  
  override suspend fun getRideDetails(id: String) = withContext(Dispatchers.IO) {
    suspendCoroutine<Ride> { continuation ->
      firestore.collection(RIDES).whereEqualTo(ID, id).get()
        .addOnFailureListener {
          continuation.resumeWithException(it)
        }
        .addOnSuccessListener { documents ->
          val _rides = documents.toObjects<Ride>()
          val ride = _rides[0]
          continuation.resume(ride)
        }
    }
  }
  
  override suspend fun getUserRides() = withContext(Dispatchers.IO) {
    suspendCoroutine<List<Ride>> { continuation ->
      firestore.collection(RIDES).whereEqualTo(AUTHOR_ID, sharedPreferences.getUserId()).get()
        .addOnFailureListener {
          continuation.resumeWithException(it)
        }
        .addOnSuccessListener { documents ->
          val rides = arrayListOf<Ride>()
          val _rides = documents.toObjects<Ride>()
          rides.addAll(_rides)
          rides.sortWith(compareBy({ it.year }, { it.month }, { it.day }, { it.time }))
          rides.removeIf {
            !isDateValid(it.day, it.month, it.year)
          }
          continuation.resume(rides)
        }
    }
  }
  
  override suspend fun deleteRide(rideId: String) {
    withContext(Dispatchers.IO) {
      firestore.collection(RIDES).document(rideId).delete().addOnSuccessListener {
        var id = EMPTY
        firestore.collection(BOOKING_RIDES).get().addOnSuccessListener { documents ->
          for (document in documents) {
            bookingRequest = document.toObject<RideBooking>()
            if (bookingRequest.ride.id == rideId) {
              id = bookingRequest.id
              firestore.collection(BOOKING_RIDES).document(id).delete()
            }
          }
        }
      }
    }
  }
  
  override suspend fun bookRide(rideId: String, onResult: (Boolean) -> Unit) {
    withContext(Dispatchers.IO) {
      val userRef = sharedPreferences.getUser()?.let { firestore.collection(USERS).document(it) }
      userRef?.get()?.addOnSuccessListener { documentSnapshot ->
        val user = documentSnapshot.toObject<User>()
        if (user != null) {
          requestor = user
        }
        val rideRef = firestore.collection(RIDES).document(rideId)
        rideRef.get().addOnSuccessListener { documentSnapshot ->
          val ride = documentSnapshot.toObject<Ride>()
          if (ride != null) {
            bookingRide = ride
          }
          val idRide = bookingRide.id
          val seats = bookingRide.numOfAvailableSeats
          val id = UUID.randomUUID().toString()
          val bookingRide = RideBooking(id, bookingRide, requestor)
          if (bookingRide != null) {
            firestore.collection(BOOKING_RIDES).document(id).set(bookingRide).addOnFailureListener {
              makeToast(it.message.toString())
            }.addOnSuccessListener {
              firestore.collection(RIDES).document(idRide).update("numOfAvailableSeats", seats - 1).addOnSuccessListener {
                onResult(true)
              }.addOnFailureListener {
              }
            }
          }
        }
      }
    }
  }
  
  override suspend fun getBookedRides() = withContext(Dispatchers.IO) {
    suspendCoroutine<List<RideBooking>> { continuation ->
      firestore.collection(BOOKING_RIDES).get().addOnFailureListener {
        continuation.resumeWithException(it)
      }.addOnSuccessListener { documents ->
        val rides = arrayListOf<RideBooking>()
        for (document in documents) {
          val doc = document.toObject<RideBooking>()
          if (doc != null) {
            bookingRide = doc.ride
            requestor = doc.requester
            val isInProcess = doc.inProcess
            if (sharedPreferences.getUserId() == requestor.id) {
              if (isInProcess) {
                rides.add(
                  RideBooking(
                    document.get("id").toString(),
                    bookingRide,
                    requestor,
                    document.get("inProcess").toString().toBoolean(),
                    document.get("accepted").toString().toBoolean()
                  )
                )
              }
            }
          }
        }
        continuation.resume(rides)
      }
    }
  }
  
  override suspend fun isRideBooked(rideId: String) = withContext(Dispatchers.IO) {
    suspendCoroutine<Boolean> { continuation ->
      firestore.collection(BOOKING_RIDES).get().addOnFailureListener {
        continuation.resumeWithException(it)
      }.addOnSuccessListener { documents ->
        var value = false
        val rides = arrayListOf<Ride>()
        for (document in documents) {
          val doc = document.toObject<RideBooking>()
          if (doc != null) {
            bookingRide = doc.ride
            requestor = doc.requester
            rides.add(bookingRide)
          }
          for (ride in rides) {
            if ((ride.id == rideId) && (requestor.id == sharedPreferences.getUserId())) {
              value = true
              break
            }
          }
        }
        continuation.resume(value)
        
      }
    }
  }
  
  override suspend fun getRequestedBookingRides() = withContext(Dispatchers.IO) {
    suspendCoroutine<List<RideBooking>> { continuation ->
      firestore.collection(BOOKING_RIDES).get().addOnFailureListener {
        continuation.resumeWithException(it)
      }.addOnSuccessListener { documents ->
        val rides = arrayListOf<RideBooking>()
        for (document in documents) {
          val doc = document.toObject<RideBooking>()
          if (doc != null) {
            bookingRide = doc.ride
            requestor = doc.requester
            val isInProcess = doc.inProcess
            if (sharedPreferences.getUserId() == bookingRide.authorId) {
              if (isInProcess) {
                rides.add(
                  RideBooking(
                    document.get("id").toString(),
                    bookingRide,
                    requestor,
                    document.get("inProcess").toString().toBoolean(),
                    document.get("accepted").toString().toBoolean()
                  )
                )
              }
            }
          }
        }
        rides.sortWith(compareBy({ it.ride.year }, { it.ride.month }, { it.ride.day }, { it.ride.time }))
        rides.removeIf {
          !isDateValid(it.ride.day, it.ride.month, it.ride.year)
        }
        continuation.resume(rides)
      }
    }
  }
  
  override suspend fun getUserBookingRides() = withContext(Dispatchers.IO) {
    suspendCoroutine<List<RideBooking>> { continuation ->
      firestore.collection(BOOKING_RIDES).get().addOnFailureListener {
        continuation.resumeWithException(it)
      }.addOnSuccessListener { documents ->
        val rides = arrayListOf<RideBooking>()
        for (document in documents) {
          val doc = document.toObject<RideBooking>()
          if (doc != null) {
            bookingRide = doc.ride
            requestor = doc.requester
            if (sharedPreferences.getUserId() == requestor.id) {
              rides.add(
                RideBooking(
                  document.get("id").toString(),
                  bookingRide,
                  requestor,
                  document.get("inProcess").toString().toBoolean(),
                  document.get("accepted").toString().toBoolean()
                )
              )
            }
          }
        }
        rides.sortWith(compareBy({ it.ride.year }, { it.ride.month }, { it.ride.day }, { it.ride.time }))
        rides.removeIf {
          !isDateValid(it.ride.day, it.ride.month, it.ride.year)
        }
        continuation.resume(rides)
      }
    }
  }
  
  override suspend fun declineBooking(bookingRideId: String) {
    withContext(Dispatchers.IO) {
      firestore.collection(BOOKING_RIDES).document(bookingRideId).get().addOnSuccessListener {
        val ride = it.toObject<RideBooking>()
        if (ride != null) {
          bookingRequest = ride
        }
        val idRide = bookingRequest.ride.id
        firestore.collection(RIDES).document(idRide).get().addOnSuccessListener {
          val ride = it.toObject<Ride>()
          if (ride != null) {
            bookingRide = ride
          }
          val seats = bookingRide.numOfAvailableSeats
          firestore.collection(BOOKING_RIDES).document(bookingRideId).update(
            "inProcess", false,
            "accepted", false
          )
            .addOnSuccessListener {
              firestore.collection(RIDES).document(idRide).update("numOfAvailableSeats", seats + 1).addOnSuccessListener {
                makeToast("Declined ride")
              }
            }
        }
      }
    }
  }
  
  override suspend fun acceptBooking(bookingRideId: String) {
    withContext(Dispatchers.IO) {
      firestore.collection(BOOKING_RIDES).document(bookingRideId).update(
        "inProcess", false,
        "accepted", true
      ).addOnSuccessListener {
        makeToast("Accepted ride")
      }
    }
  }
  
  override suspend fun getPassengers(rideId: String) = withContext(Dispatchers.IO) {
    suspendCoroutine<List<User>> { continuation ->
      firestore.collection(BOOKING_RIDES).get().addOnSuccessListener {
        val users = arrayListOf<User>()
        for (document in it) {
          val doc = document.toObject<RideBooking>()
          if (doc.accepted) {
            if (doc.ride.id == rideId) {
              users.add(doc.requester)
            }
          }
        }
        continuation.resume(users)
      }
        .addOnFailureListener {
          continuation.resumeWithException(it)
        }
    }
  }
  
  override suspend fun filterRides(context: Context, filterRide: FilterRide) = withContext(Dispatchers.IO) {
    suspendCoroutine<List<Ride>> { continuation ->
      firestore.collection(RIDES).whereEqualTo("day", filterRide.day)
        .whereEqualTo("month", filterRide.month)
        .whereEqualTo("year", filterRide.year)
        .whereLessThan("price", filterRide.endPrice)
        .whereGreaterThan("price", filterRide.startPrice).get().addOnSuccessListener {
          val rides = arrayListOf<Ride>()
          for (document in it) {
            val doc = document.toObject<Ride>()
            if (getRideStartRouteAddress(context, doc).uppercase(Locale.getDefault()).contains(filterRide.start.uppercase())
              &&
              getRideEndRouteAddress(context, doc).uppercase(Locale.getDefault()).contains(filterRide.end.uppercase())
            )
              rides.add(doc)
            rides.removeIf { ride ->
              ride.authorId == sharedPreferences.getUserId()
            }
            rides.removeIf {
              !isDateValid(it.day, it.month, it.year)
            }
          }
          continuation.resume(rides)
        }
        .addOnFailureListener {
          continuation.resumeWithException(it)
        }
    }
  }
  
  override suspend fun cancelBookingRide(bookingRideId: String, onResult: (Boolean) -> Unit) {
    withContext(Dispatchers.IO) {
      firestore.collection(BOOKING_RIDES).document(bookingRideId).get().addOnSuccessListener {
        val ride = it.toObject<RideBooking>()
        if (ride != null) {
          bookingRequest = ride
        }
        val idRide = bookingRequest.ride.id
        firestore.collection(RIDES).document(idRide).get().addOnSuccessListener {
          val ride = it.toObject<Ride>()
          if (ride != null) {
            bookingRide = ride
          }
          val seats = bookingRide.numOfAvailableSeats
          firestore.collection(RIDES).document(idRide).update("numOfAvailableSeats", seats + 1).addOnSuccessListener {
            firestore.collection(BOOKING_RIDES).document(bookingRideId).delete().addOnSuccessListener {
              onResult(true)
            }
          }
        }
      }
    }
  }
}