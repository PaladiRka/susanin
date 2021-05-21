package com.bignerdranch.android.susanin.ui.history

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.susanin.PointDBHelper
import com.bignerdranch.android.susanin.R
import com.bignerdranch.android.susanin.SusaninPoint

class HistoryFragment : Fragment() {

    private lateinit var pointRecyclerView: RecyclerView
    private lateinit var historyViewModel: HistoryViewModel
    private var adapter: PointAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyViewModel =
            ViewModelProvider(this).get(HistoryViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_point_list, container, false)

        pointRecyclerView = root.findViewById(R.id.point_recycler_view)
        pointRecyclerView.layoutManager = LinearLayoutManager(activity)

        updateUI()
        return root
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
            adapter!!.susaninPoints = PointDBHelper.get(requireActivity()).getSusaninPoints()
        } else {
            adapter = PointAdapter(PointDBHelper.get(requireActivity()).getSusaninPoints())
            pointRecyclerView.adapter = adapter
        }
    }

    private inner class PointHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private lateinit var popupView: ConstraintLayout
        private lateinit var popupWindow: PopupWindow
        var susaninPoint = SusaninPoint(0.0, 0.0, "")

        init {
            itemView.setOnClickListener {
                // create the popup window
                popupWindow = PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true // lets taps outside the popup also dismiss it
                )

                popupWindow.animationStyle = R.style.AnimationPopup
                popupWindow.showAsDropDown(itemView)
            }
//            TODO("Редактирование и удаление на нажатие через popup")
        }

        private fun popupPreparation(view: View) {
            popupView =
                View.inflate(requireContext(), R.layout.popup_view, null) as ConstraintLayout

            val editButton: Button = popupView.findViewById(R.id.edit_popup)
            editButton.setOnClickListener {
                popupWindow.dismiss()
                val editText = EditText(context)
                val alert = AlertDialog.Builder(activity)
                    .setTitle("Creating point")
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, which -> }
                    .setPositiveButton(
                        "Edit"
                    ) { dialog, which ->
                        susaninPoint = susaninPoint.copy(
                            susaninPoint.latitude,
                            susaninPoint.longitude,
                            editText.text.toString(),
                            susaninPoint.id
                        )
                        PointDBHelper.get(requireContext()).updateSusaninPoint(susaninPoint)
                        updateUI()
                    }
                    .setView(editText)

                editText.setText(view.findViewById<TextView>(R.id.point_title).text)
                alert.create().show()
            }

            val deleteButton: Button = popupView.findViewById(R.id.delete_popup)
            deleteButton.setOnClickListener {
                popupWindow.dismiss()
                val alert = AlertDialog.Builder(activity)
                alert.setTitle("Delete point")
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, which -> }
                    .setPositiveButton(
                        "Delete"
                    ) { dialog, which ->
                        PointDBHelper.get(requireContext()).deleteSusaninPoint(susaninPoint)
                        updateUI()
                    }
                    .setMessage("Are you sure you want to delete this point?")
                alert.create().show()
//            myDialogFragment
            }
        }


        private val titleTextView: TextView = itemView.findViewById(R.id.point_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.point_coordination)
        fun bind(susaninPoint: SusaninPoint) {
            this.susaninPoint = susaninPoint
            popupPreparation(itemView)
            titleTextView.text = susaninPoint.name
            dateTextView.text =
                String.format(
                    "%f°, %f°",
                    susaninPoint.latitude,
                    susaninPoint.longitude
                )
        }

        override fun onClick(v: View?) {
            // create the popup window
            popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true // lets taps outside the popup also dismiss it
            )

            popupWindow.animationStyle = R.style.AnimationPopup
            popupWindow.showAsDropDown(itemView)
        }

    }

    private inner class PointAdapter(var susaninPoints: List<SusaninPoint>) :
        RecyclerView.Adapter<PointHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointHolder {
            val inflater = LayoutInflater.from(activity)

            val view = inflater.inflate(R.layout.list_item_point, parent, false)
            return PointHolder(view)
        }

        override fun onBindViewHolder(holder: PointHolder, position: Int) {
            val susaninPoint = susaninPoints[position]
            holder.bind(susaninPoint)
        }

        override fun getItemCount(): Int {
            return susaninPoints.size
        }
    }
}