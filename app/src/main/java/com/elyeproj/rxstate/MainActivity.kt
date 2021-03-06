package com.elyeproj.rxstate

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.elyeproj.rxstate.coordinator.DataSource
import com.elyeproj.rxstate.coordinator.Coordinator
import com.elyeproj.rxstate.coordinator.Container
import com.elyeproj.rxstate.coordinator.Presentation
import com.elyeproj.rxstate.coordinator.ViewPresentation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Container {

    companion object {
        private const val COORDINATOR_STATE = "CoordinatorState"
        const val PRESENTATION_STATE = "PresentationState"
    }

    private val mainCoordinator by lazy {
        Coordinator(this, DataSource())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            restoreFragmentState()
            mainCoordinator.restoreState(savedInstanceState.getSerializable(COORDINATOR_STATE))
        } ?: mainCoordinator.initialize()

        btn_load_success.setOnClickListener {
            mainCoordinator.loadSuccess()
        }

        btn_load_error.setOnClickListener {
            mainCoordinator.loadError()
        }

        btn_load_empty.setOnClickListener {
            mainCoordinator.loadEmpty()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(COORDINATOR_STATE, mainCoordinator.getState())
    }

    override fun onDestroy() {
        super.onDestroy()
        mainCoordinator.destroy()
    }

    override fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun restoreFragmentState() {
        supportFragmentManager.findFragmentById(R.id.status_container)?.let {
            (it as ViewPresentation).coordinator = mainCoordinator
        }
    }

    override fun showView(presentation: Presentation) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                .replace(R.id.status_container, createView(presentation)).commitAllowingStateLoss()
    }

    private fun createView(presentation: Presentation): Fragment? {
        val fragment = presentation.getViewClass().newInstance()
        val arguments = Bundle()
        arguments.putSerializable(PRESENTATION_STATE, presentation.data)
        fragment.arguments = arguments
        (fragment as ViewPresentation).coordinator = mainCoordinator
        return fragment
    }
}
