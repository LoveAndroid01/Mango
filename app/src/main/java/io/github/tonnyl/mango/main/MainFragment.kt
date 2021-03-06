package io.github.tonnyl.mango.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import io.github.tonnyl.mango.R
import io.github.tonnyl.mango.about.AboutActivity
import io.github.tonnyl.mango.auth.AuthActivity
import io.github.tonnyl.mango.data.User
import io.github.tonnyl.mango.glide.GlideLoader
import io.github.tonnyl.mango.user.UserProfileActivity
import io.github.tonnyl.mango.util.AccountManager

import kotlinx.android.synthetic.main.fragment_shots.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

/**
 * Created by lizhaotailang on 2017/6/28.
 */

class MainFragment : Fragment(), MainContract.View {

    private lateinit var mPresenter: MainContract.Presenter

    companion object {
        @JvmStatic
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.fragment_shots, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        mPresenter.subscribe()

        user_info_layout.setOnClickListener {
            AccountManager.authenticatedUser?.let {
                context.startActivity<UserProfileActivity>(UserProfileActivity.EXTRA_USER to it)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.unsubscribe()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.action_logout) {
            showLogoutDialog()
        } else if (id == R.id.action_about) {
            context.startActivity<AboutActivity>()
        }
        return true
    }

    override fun initViews() {
        (activity as MainActivity).setSupportActionBar(toolbar)

        view_pager.adapter = MainPagerAdapter(context, childFragmentManager)
        view_pager.offscreenPageLimit = 4

        tab_layout.setupWithViewPager(view_pager)
    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        mPresenter = presenter
    }

    override fun showAuthUserInfo(user: User) {
        user_name.text = user.name
        GlideLoader.loadAvatar(context, avatar_drawee, user.avatarUrl)
    }

    override fun navigateToLogin() {
        context.startActivity(context.intentFor<AuthActivity>().newTask().clearTask())
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(context)
                .setTitle(R.string.log_out)
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    mPresenter.logoutUser()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->

                }
                .show()
    }

}