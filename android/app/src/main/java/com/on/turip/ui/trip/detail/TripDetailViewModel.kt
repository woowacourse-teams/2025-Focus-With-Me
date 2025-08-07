package com.on.turip.ui.trip.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.on.turip.di.RepositoryModule
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.repository.ContentRepository
import com.on.turip.domain.creator.Creator
import com.on.turip.domain.creator.repository.CreatorRepository
import com.on.turip.domain.favorite.usecase.UpdateFavoriteUseCase
import com.on.turip.domain.trip.Trip
import com.on.turip.domain.trip.TripCourse
import com.on.turip.domain.trip.repository.TripRepository
import com.on.turip.ui.common.mapper.toUiModel
import com.on.turip.ui.common.model.trip.TripModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import timber.log.Timber

class TripDetailViewModel(
    private val contentId: Long,
    private val creatorId: Long,
    private val contentRepository: ContentRepository,
    private val creatorRepository: CreatorRepository,
    private val tripRepository: TripRepository,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    private val _content: MutableLiveData<Content> = MutableLiveData()
    val content: LiveData<Content> get() = _content

    private val _days: MutableLiveData<List<DayModel>> = MutableLiveData()
    val days: LiveData<List<DayModel>> get() = _days

    private val _places: MutableLiveData<List<PlaceModel>> = MutableLiveData()
    val places: LiveData<List<PlaceModel>> get() = _places

    private val _tripModel: MutableLiveData<TripModel> = MutableLiveData()
    val tripModel: LiveData<TripModel> get() = _tripModel

    private val _videoUri: MutableLiveData<String> = MutableLiveData()
    val videoUri: LiveData<String> get() = _videoUri

    private val _isFavorite: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private var placeCacheByDay: Map<Int, List<PlaceModel>> = emptyMap()

    init {
        loadContent()
        loadTrip()
        handleFavoriteWithDebounce()
    }

    private fun loadContent() {
        viewModelScope.launch {
            val deferredCreator: Deferred<Result<Creator>> =
                async { creatorRepository.loadCreator(creatorId) }
            val deferredVideoData: Deferred<Result<Content>> =
                async { contentRepository.loadContent(contentId) }

            val creatorResult: Result<Creator> = deferredCreator.await()
            val videoDataResult: Result<Content> = deferredVideoData.await()

            creatorResult
                .onSuccess { creator: Creator ->
                    videoDataResult
                        .onSuccess { result: Content ->
                            _content.value = result
                            _videoUri.value = result.videoData.url
                            _isFavorite.value = result.isFavorite
                        }.onFailure {
                            Timber.e("${it.message}")
                        }
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    private fun loadTrip() {
        viewModelScope.launch {
            tripRepository
                .loadTripInfo(contentId)
                .onSuccess { trip: Trip ->
                    setupCached(trip)

                    _days.value =
                        placeCacheByDay.keys
                            .sorted()
                            .mapIndexed { index, day ->
                                DayModel(day = day, isSelected = index == 0)
                            }
                    _places.value = placeCacheByDay[1] ?: emptyList()
                    _tripModel.value = trip.toUiModel()
                    Timber.d("여행 일정 불러오기 성공")
                }.onFailure {
                    Timber.e("${it.message}")
                }
        }
    }

    private fun setupCached(trip: Trip) {
        val dayModels: List<DayModel> = trip.tripDuration.days.initDayModels()

        placeCacheByDay =
            dayModels.associate { dayModel ->
                val day: Int = dayModel.day
                val coursesForDay: List<TripCourse> = trip.tripCourses.filter { it.visitDay == day }
                val placeModels: List<PlaceModel> =
                    coursesForDay.map { course ->
                        PlaceModel(
                            name = course.place.name,
                            category = course.place.category.joinToString(),
                            mapLink = course.place.url,
                        )
                    }
                day to placeModels
            }
    }

    fun updateDay(dayModel: DayModel) {
        _days.value = days.value?.map { it.copy(isSelected = it.day == dayModel.day) }
        _places.value = placeCacheByDay[dayModel.day].orEmpty()
    }

    fun updateFavorite() {
        _isFavorite.value = isFavorite.value?.not()
    }

    @OptIn(FlowPreview::class)
    private fun handleFavoriteWithDebounce() {
        viewModelScope.launch {
            _isFavorite
                .asFlow()
                .debounce(500L)
                .filterNotNull()
                .collectLatest { favoriteStatus: Boolean ->
                    updateFavoriteUseCase(favoriteStatus, contentId)
                        .onSuccess {
                            Timber.d("찜 API 통신 성공")
                        }.onFailure {
                            Timber.e("${it.message}")
                        }
                }
        }
    }

    companion object {
        fun provideFactory(
            contentId: Long,
            creatorId: Long,
            contentRepository: ContentRepository = RepositoryModule.contentRepository,
            creatorRepository: CreatorRepository = RepositoryModule.creatorRepository,
            travelRepository: TripRepository = RepositoryModule.tripRepository,
            updateFavoriteUseCase: UpdateFavoriteUseCase = UpdateFavoriteUseCase(RepositoryModule.favoriteRepository),
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    TripDetailViewModel(
                        contentId,
                        creatorId,
                        contentRepository,
                        creatorRepository,
                        travelRepository,
                        updateFavoriteUseCase,
                    )
                }
            }
    }
}
