package io.github.tonnyl.mango.user

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.*
import io.github.tonnyl.mango.R
import io.github.tonnyl.mango.data.User
import io.github.tonnyl.mango.glide.GlideLoader
import io.github.tonnyl.mango.user.followers.FollowersActivity
import io.github.tonnyl.mango.user.followers.FollowersPresenter
import io.github.tonnyl.mango.user.following.FollowingActivity
import io.github.tonnyl.mango.user.following.FollowingPresenter
import kotlinx.android.synthetic.main.fragment_user_profile.*
import org.jetbrains.anko.startActivity


/**
 * Created by lizhaotailang on 2017/6/28.
 */

class UserProfileFragment : Fragment(), UserProfileContract.View {

    private lateinit var mPresenter: UserProfileContract.Presenter

    companion object {

        @JvmStatic
        fun getInstance(): UserProfileFragment {
            return UserProfileFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        mPresenter.subscribe()

        following.setOnClickListener {
            context.startActivity<FollowingActivity>(
                    FollowingPresenter.EXTRA_USER_ID to mPresenter.getUserId(),
                    FollowingPresenter.EXTRA_FOLLOWING_TITLE to following.text)
        }

        followers.setOnClickListener {
            context.startActivity<FollowersActivity>(
                    FollowersPresenter.EXTRA_USER_ID to mPresenter.getUserId(),
                    FollowersPresenter.EXTRA_FOLLOWERS_TITLE to followers.text)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.unsubscribe()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_user, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == android.R.id.home) {
            activity.onBackPressed()
        } else if (id == R.id.action_open_in_browser) {

        }
        return true
    }

    override fun setPresenter(presenter: UserProfileContract.Presenter) {
        mPresenter = presenter
    }

    override fun showUserInfo(user: User) {
        GlideLoader.loadAvatar(context, avatar, user.avatarUrl)

        if (user.links.twitter.isEmpty()) {
            user_info_twitter.visibility = View.GONE
        } else {
            user_info_twitter.text = user.links.twitter
        }
        if (user.links.web.isEmpty()) {
            user_info_web.visibility = View.GONE
        } else {
            user_info_web.text = user.links.web
        }

        user.location?.let {
            user_info_location.text = it
        } ?: run {
            user_info_location.visibility = View.GONE
        }

        followers.text = getString(R.string.followers_formatted).format(user.followersCount)
        following.text = getString(R.string.following_formatted).format(user.followingsCount)

        if (Build.VERSION.SDK_INT >= 24) {
            bio.text = Html.fromHtml(user.bio, Html.FROM_HTML_MODE_LEGACY)
        } else {
            bio.text = Html.fromHtml(user.bio)
        }

        tab_layout.getTabAt(0)?.text = getString(R.string.tab_title_shots).format(user.shotsCount)
        tab_layout.getTabAt(1)?.text = getString(R.string.tab_title_likes).format(user.likesCount)

        val act = activity as UserProfileActivity
        act.supportActionBar?.title = user.name
        act.supportActionBar?.subtitle = user.username

    }

    private fun initViews() {

        val act = activity as UserProfileActivity
        act.setSupportActionBar(toolbar)
        act.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        view_pager.adapter = UserProfilePagerAdapter(context, mPresenter.getUserId(), childFragmentManager)
        view_pager.offscreenPageLimit = 2

        tab_layout.setupWithViewPager(view_pager)
    }

}