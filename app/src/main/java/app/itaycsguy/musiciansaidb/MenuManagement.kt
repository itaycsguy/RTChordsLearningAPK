package app.itaycsguy.musiciansaidb

import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.content.Context
import android.content.Intent
import android.support.v7.widget.ThemedSpinnerAdapter
import android.content.res.Resources.Theme

import kotlinx.android.synthetic.main.activity_menu_management.*
import kotlinx.android.synthetic.main.fragment_menu_management.view.*
import kotlinx.android.synthetic.main.list_item.view.*

class MenuManagement : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_management)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setup spinner
        spinner.adapter = MyAdapter(toolbar.context,arrayOf("My Profile","Image Actions"))

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_management, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when(id) {
            R.id.action_logout -> { startActivity(Intent(this,StartActivity::class.java)) }
        }
        return super.onOptionsItemSelected(item)
    }

    private class MyAdapter(context: Context, objects: Array<String>) : ArrayAdapter<String>(context, R.layout.list_item, objects), ThemedSpinnerAdapter {
        private val mDropDownHelper: ThemedSpinnerAdapter.Helper = ThemedSpinnerAdapter.Helper(context)

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                val inflater = mDropDownHelper.dropDownViewInflater
                view = inflater.inflate(R.layout.list_item, parent, false)
            } else {
                view = convertView
            }
            view.text1.text = getItem(position)
            return view
        }

        override fun getDropDownViewTheme(): Theme? {
            return mDropDownHelper.dropDownViewTheme
        }

        override fun setDropDownViewTheme(theme: Theme?) {
            mDropDownHelper.dropDownViewTheme = theme
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
            val IMAGE_ACTIONS_CHOOSE = 2
            var rootView = inflater.inflate(R.layout.activity_profile, container, false)
            when(arguments?.getInt(ARG_SECTION_NUMBER)) {
                IMAGE_ACTIONS_CHOOSE -> { rootView = inflater.inflate(R.layout.menu_activity, container, false) }
            }
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
