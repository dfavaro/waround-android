package com.danielefavaro.waround.maps.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielefavaro.waround.R
import com.danielefavaro.waround.WaroundApp
import com.danielefavaro.waround.base.OneTimeEvent
import com.danielefavaro.waround.base.StatefulBottomSheetDialogFragment
import com.danielefavaro.waround.databinding.ArticleDetailFragmentBinding
import com.danielefavaro.waround.maps.ui.adapter.ArticleImageAdapter
import com.danielefavaro.waround.maps.ui.model.ArticleDetailFragmentEvents
import com.danielefavaro.waround.maps.ui.model.ArticleDetailFragmentViewState
import com.danielefavaro.waround.maps.ui.viewmodel.ArticleDetailViewModel
import com.danielefavaro.waround.maps.ui.viewmodel.MapsViewModel
import kotlinx.android.synthetic.main.article_detail_fragment.*


private const val ARG_PAGE_ID = "ARG_PAGE_ID"

// TODO move as bottomsheet layout item into MapsFragment
class ArticleDetailFragment :
    StatefulBottomSheetDialogFragment<ArticleDetailFragmentViewState, ArticleDetailViewModel>() {

    override val viewModel: ArticleDetailViewModel by viewModels { viewModelsFactory }
    val mapsViewModel: MapsViewModel by activityViewModels { viewModelsFactory }

    private var pageId: Long? = null
    private var articleImageAdapter = ArticleImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pageId = it.getLong(ARG_PAGE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ArticleDetailFragmentBinding.inflate(inflater, container, false).apply {
        viewmodel = this@ArticleDetailFragment.viewModel
    }.root

    override fun onAttach(context: Context) {
        (activity?.application as? WaroundApp)?.appComponent?.mapsFractory()?.create()?.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = articleImageAdapter
        }

        pageId?.let { pageId ->
            viewModel.getPageDetail(pageId)
        } ?: run {
            dismissAllowingStateLoss()
        }
    }

    override fun updateUi(stateDetail: ArticleDetailFragmentViewState) {
        loadingLayout.visibility = if (stateDetail.isLoading) View.VISIBLE else View.GONE

        titleTextview.text = stateDetail.articleUI.title
        descriptionTextview.visibility =
            if (stateDetail.articleUI.description.isNotEmpty()) View.VISIBLE
            else View.GONE
        descriptionTextview.text = stateDetail.articleUI.description
        articleImageAdapter.setImages(stateDetail.articleUI.imagesUrl)
        urlButton.text = getString(R.string.wikipedia_page_url, stateDetail.articleUI.title)
    }

    override fun onEvent(event: OneTimeEvent) {
        when (event) {
            is ArticleDetailFragmentEvents.OnGenericError -> dismissAllowingStateLoss()
            is ArticleDetailFragmentEvents.OnArticleDetailLoaded -> detailItemsGroup.visibility =
                View.VISIBLE
            is ArticleDetailFragmentEvents.OnUrlClick -> {
                // TODO in-app webview -> chrome custom view
                val httpIntent = Intent(Intent.ACTION_VIEW)
                httpIntent.data = event.url.toUri()
                startActivity(httpIntent)
            }
            is ArticleDetailFragmentEvents.OnRouteStart -> {
                dismissAllowingStateLoss()
                mapsViewModel.onRouteStart()
            }
        }
    }

    companion object {
        const val TAG = "ArticleDetailFragment"

        @JvmStatic
        fun newInstance(pageId: Long) = ArticleDetailFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_PAGE_ID, pageId)
            }
        }
    }
}