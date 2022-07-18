package com.synergics.stb.iptv.leanback

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsFragment
import androidx.leanback.app.DetailsFragmentBackgroundController
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.synergics.stb.iptv.leanback.models.TVItems

class VideoDetailsFragment : DetailsSupportFragment() {
    private var mSelectedMovie: TVItems? = null
    private var mAdapter: ArrayObjectAdapter? = null
    private var mPresenterSelector: ClassPresenterSelector? = null
    private var mDetailsBackground: DetailsSupportFragmentBackgroundController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDetailsBackground = DetailsSupportFragmentBackgroundController(this)

        updateMovie()
    }

    fun updateMovie(){
        mSelectedMovie = Gson().fromJson(
            activity?.intent?.getStringExtra(DetailsActivity.Companion.CHANNEL),
            TVItems::class.java
        )
        if (mSelectedMovie != null) {
            mPresenterSelector = ClassPresenterSelector()
            mAdapter = ArrayObjectAdapter(mPresenterSelector)
            setupDetailsOverviewRow()
            //setupDetailsOverviewRowPresenter();
            //setupRelatedMovieListRow();
            adapter = mAdapter
            initializeBackground(mSelectedMovie!!)
            //setOnItemViewClickedListener(new ItemViewClickedListener());
        } else {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeBackground(data: TVItems) {
        mDetailsBackground!!.enableParallax()
        Glide.with(requireContext())
            .asBitmap()
            .load(data.icon)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    mDetailsBackground?.coverBitmap = resource
                    mAdapter?.notifyArrayItemRangeChanged(0, mAdapter?.size()!!)
                }
            })
    }

    private fun setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mSelectedMovie.toString())
        val row = DetailsOverviewRow(mSelectedMovie)
        row.imageDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.default_background)
        val width = convertDpToPixel(activity?.applicationContext!!, DETAIL_THUMB_WIDTH)
        val height = convertDpToPixel(activity?.applicationContext!!, DETAIL_THUMB_HEIGHT)
        Glide.with(requireContext())
            .load(mSelectedMovie?.icon)
            .into(object : SimpleTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    Log.d(TAG, "details overview card image url ready: $resource")
                    row.imageDrawable = resource
                    mAdapter?.notifyArrayItemRangeChanged(0, mAdapter?.size()!!)
                }
            })
        val actionAdapter = ArrayObjectAdapter()
        actionAdapter.add(
            Action(
                ACTION_WATCH_TRAILER.toLong(),
                resources.getString(R.string.watch_trailer_1),
                resources.getString(R.string.watch_trailer_2)
            )
        )
        actionAdapter.add(
            Action(
                ACTION_RENT.toLong(),
                resources.getString(R.string.rent_1),
                resources.getString(R.string.rent_2)
            )
        )
        actionAdapter.add(
            Action(
                ACTION_BUY.toLong(),
                resources.getString(R.string.buy_1),
                resources.getString(R.string.buy_2)
            )
        )
        row.actionsAdapter = actionAdapter
        mAdapter!!.add(row)
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        detailsPresenter.backgroundColor = ContextCompat.getColor(
            requireContext(),
            R.color.selected_background
        )

        // Hook up transition element.
        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(
            activity, DetailsActivity.Companion.SHARED_ELEMENT_NAME
        )
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true

        /*detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_WATCH_TRAILER) {
                    Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                    intent.putExtra(DetailsActivity.MOVIE, mSelectedMovie);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });*/mPresenterSelector!!.addClassPresenter(
            DetailsOverviewRow::class.java,
            detailsPresenter
        )
    }

    /*private void setupRelatedMovieListRow() {
        String subcategories[] = {getString(R.string.related_movies)};
        List<Movie> list = MovieList.getList();

        Collections.shuffle(list);
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (int j = 0; j < NUM_COLS; j++) {
            listRowAdapter.add(list.get(j % 5));
        }

        HeaderItem header = new HeaderItem(0, subcategories[0]);
        mAdapter.add(new ListRow(header, listRowAdapter));
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }*/
    private fun convertDpToPixel(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    } /*private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {

            if (item instanceof Movie) {
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(getResources().getString(R.string.movie), mSelectedMovie);

                Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(),
                                ((ImageCardView) itemViewHolder.view).getMainImageView(),
                                DetailsActivity.SHARED_ELEMENT_NAME)
                                .toBundle();
                getActivity().startActivity(intent, bundle);
            }
        }
    }*/

    companion object {
        private const val TAG = "VideoDetailsFragment"
        private const val ACTION_WATCH_TRAILER = 1
        private const val ACTION_RENT = 2
        private const val ACTION_BUY = 3
        private const val DETAIL_THUMB_WIDTH = 274
        private const val DETAIL_THUMB_HEIGHT = 274
        private const val NUM_COLS = 10
    }
}