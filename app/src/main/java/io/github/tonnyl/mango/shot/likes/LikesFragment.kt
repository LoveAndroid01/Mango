package io.github.tonnyl.mango.shot.likes

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import io.github.tonnyl.mango.R
import io.github.tonnyl.mango.data.Like
import io.github.tonnyl.mango.user.UserProfileActivity
import kotlinx.android.synthetic.main.fragment_simple_list.*
import org.jetbrains.anko.startActivity

/**
 * Created by lizhaotailang on 2017/7/8.
 */

class LikesFragment : Fragment(), LikesContract.View {

    private lateinit var mPresenter: LikesContract.Presenter

    private var mLayoutManager: LinearLayoutManager? = null
    private var mAdapter: LikesAdapter? = null
    private var mListSize = 0
    private var mIsLoading = true

    companion object {
        @JvmStatic
        fun newInstance(): LikesFragment {
            return LikesFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.fragment_simple_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        mPresenter.subscribe()

        refresh_layout.setOnRefreshListener {
            mAdapter?.clearData()
            mPresenter.fetchLikes()
            mIsLoading = true
        }

        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && (mLayoutManager?.findLastCompletelyVisibleItemPosition() == mListSize - 1) && !mIsLoading) {
                    mPresenter.fetchLikes()
                }
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.unsubscribe()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            activity.onBackPressed()
        }
        return true
    }

    override fun setPresenter(presenter: LikesContract.Presenter) {
        mPresenter = presenter
    }

    override fun setLoadingIndicator(loading: Boolean) {
        refresh_layout.post({
            refresh_layout.isRefreshing = loading
        })
    }

    override fun showMessage(message: String?) {
        message?.let {
            Snackbar.make(refresh_layout, it, Snackbar.LENGTH_SHORT).show()
        } ?: run {
            Snackbar.make(refresh_layout, R.string.something_wrong, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun showLikes(likes: MutableList<Like>) {
        if (mAdapter == null) {
            mAdapter = LikesAdapter(context, likes)
            mAdapter?.setOnAvatarClickListener({ _, position ->
                context.startActivity<UserProfileActivity>(UserProfileActivity.EXTRA_USER to likes[position].user)
            })
            recycler_view.adapter = mAdapter
        } else {
            mAdapter?.updateData(likes)
        }

        mListSize += likes.size
        mIsLoading = false

        empty_view.visibility = if (likes.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun updateTitle(likeCount: Int) {
        (activity as LikesActivity).title = getString(R.string.likes_formatted).format(likeCount)
    }

    private fun initViews() {
        mLayoutManager = LinearLayoutManager(context)
        recycler_view.layoutManager = mLayoutManager
        refresh_layout.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
    }

}