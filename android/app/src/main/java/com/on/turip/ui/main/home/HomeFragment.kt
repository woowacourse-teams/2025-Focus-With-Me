package com.on.turip.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.on.turip.R
import com.on.turip.databinding.FragmentHomeBinding
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.UsersLikeContent
import com.on.turip.domain.region.RegionCategory
import com.on.turip.ui.common.ItemSpaceDecoration
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.search.keywordresult.SearchActivity
import com.on.turip.ui.search.regionresult.RegionResultActivity
import com.on.turip.ui.trip.detail.TripDetailActivity
import timber.log.Timber

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.provideFactory() }

    private val regionAdapter: RegionAdapter =
        RegionAdapter { regionCategoryName: String ->
            Timber.d("지역 선택 : $regionCategoryName")
            val intent: Intent =
                RegionResultActivity.newIntent(requireContext(), regionCategoryName)
            startActivity(intent)
        }

    private val usersLikeContentAdapter: UsersLikeContentAdapter =
        UsersLikeContentAdapter { content: Content ->
            Timber.d("인기 컨텐츠 선택 : ContentId = ${content.id} CreatorId = ${content.creator.id}")
            val intent: Intent =
                TripDetailActivity.newIntent(
                    context = requireContext(),
                    contentId = content.id,
                    creatorId = content.creator.id,
                )
            startActivity(intent)
        }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupTextHighlighting()
        setupAdapters()
        setupObservers()
        setupListeners()
    }

    private fun setupTextHighlighting() {
        val originalText: String = getString(R.string.home_where_should_we_go_title)
        val highlightText: String = getString(R.string.home_where_should_we_go_highlighting)
        val startIndex: Int = originalText.indexOf(highlightText)
        val endIndex: Int = startIndex + highlightText.length

        val spannableText =
            SpannableString(originalText).apply {
                setSpan(
                    BackgroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.turip_lemon_faff60_50,
                        ),
                    ),
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }

        binding.tvHomeWhereShouldWeGoTitle.text = spannableText
    }

    private fun setupAdapters() {
        binding.rvHomeRegion.adapter = regionAdapter
        binding.rvUsersLikeContent.apply {
            adapter = usersLikeContentAdapter
            addItemDecoration(ItemSpaceDecoration(end = 10))
        }
    }

    private fun setupObservers() {
        viewModel.regionCategories.observe(viewLifecycleOwner) { regionCategories: List<RegionCategory> ->
            regionAdapter.submitList(regionCategories)
        }
        viewModel.isSelectedDomestic.observe(viewLifecycleOwner) { isSelectedDomestic: Boolean ->
            binding.tvHomeDomesticButton.isSelected = isSelectedDomestic
            binding.tvHomeAbroadButton.isSelected = isSelectedDomestic.not()
        }
        viewModel.usersLikeContents.observe(viewLifecycleOwner) { usersLikeContents: List<UsersLikeContent> ->
            usersLikeContentAdapter.submitList(usersLikeContents)
        }
    }

    private fun setupListeners() {
        binding.tvHomeDomesticButton.setOnClickListener {
            Timber.d("국내 클릭")
            viewModel.loadRegionCategories(isDomestic = true)
        }
        binding.tvHomeAbroadButton.setOnClickListener {
            Timber.d("해외 클릭")
            viewModel.loadRegionCategories(isDomestic = false)
        }
        binding.ivHomeSearch.setOnClickListener {
            Timber.d("검색 화면 클릭")
            val intent: Intent = SearchActivity.newIntent(requireContext())
            startActivity(intent)
        }
    }
}
