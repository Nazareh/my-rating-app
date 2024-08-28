package com.turminaz.myratingapp.rating;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

interface PlayerRatingRepository extends FirestoreReactiveRepository<PlayerRating> {

}
