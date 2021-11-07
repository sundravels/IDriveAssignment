package com.example.pinboardassignment.onboarding

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pinboardassignment.R
import com.example.pinboardassignment.base.BaseFragment
import com.example.pinboardassignment.cache.BitmapsCache
import com.example.pinboardassignment.databinding.FragmentFirstBinding
import com.example.pinboardassignment.generics.GenericAdapter
import com.example.pinboardassignment.generics.GenericItemViewHolders
import com.example.pinboardassignment.model.PinBoardResponse
import com.example.pinboardassignment.network.APICallInterface
import com.example.pinboardassignment.network.ErrorResponse
import com.example.pinboardassignment.network.GenericRepository
import com.example.pinboardassignment.network.JsonArrayResponse
import com.example.pinboardassignment.utils.AppUtils
import com.example.pinboardassignment.utils.LoggerClass
import com.example.pinboardassignment.viewmodels.PinBoardViewModel
import com.google.android.material.imageview.ShapeableImageView
import com.imagepreviewer.idriveassignment.CoroutineAsynctask
import kotlinx.android.synthetic.main.adapter_layout_staggered_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class PinBoardFragment : BaseFragment() {

    lateinit var binding: FragmentFirstBinding

    // This property is only valid between onCreateView and
    // onDestroyView.

    lateinit var cache: BitmapsCache

    private val pinBoardResponse by activityViewModels<PinBoardViewModel>()

    private val arrayPinBoardResponse = ArrayList<PinBoardResponse>()

    lateinit var asyncTask: CoroutineAsynctask<String, String, Bitmap>

    //Below hashmap is used to maintain all the job, to cancel and start job.
    private val hashMapJobs = HashMap<String, CoroutineScope>()

    //recycler view adapter
    private val pinBoardAdapter = object : GenericAdapter<PinBoardResponse>(arrayPinBoardResponse) {
        override fun bindCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            return GenericItemViewHolders(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_layout_staggered_list, parent, false)
            )
        }

        override fun bindBindViewHolder(holder: RecyclerView.ViewHolder, model: PinBoardResponse) {
            asyncTask = object : CoroutineAsynctask<String, String, Bitmap>() {

                override suspend fun doInBackground(vararg params: String): Bitmap? {
                    var bitmap: Bitmap? = GenericRepository.genericRepositoryInstance.downloadFile(
                        params[0],
                    )
                    return bitmap
                }

                override fun onCancelled(sErrorMessage: String) {
                    super.onCancelled(sErrorMessage)
                    LoggerClass.getLoggerClass().verbose(sTAG = "onCancelled", data = model.id)
                }

                override fun onPostExecute(result: Bitmap?, view: ShapeableImageView) {
                    LoggerClass.getLoggerClass()
                        .verbose(sTAG = "onPostExecute=${model.id}", "${result}")
                    cache.put(model.id, result)
                    AppUtils.populateGlide(
                        requireActivity(),//pinBoardResponse.idJob =
                        bitmap = result,
                        view
                    )
                    super.onPostExecute(result, view)
                }


                override fun onPreExecute(model: PinBoardResponse, job: CoroutineScope) {
                    //Added job to hashmaps
                    hashMapJobs.put(model.id, job)
                    super.onPreExecute(model, job)
                }

            }

            when {
                cache.get(model.id) != null -> {
                    AppUtils.populateGlide(
                        requireActivity(),
                        bitmap = cache.get(model.id),
                        holder.itemView.sivAdapterLayoutStaggered
                    )
                }
                else -> {
                    asyncTask.execute(
                        model.urls.raw,
                        model = model,
                        shapeableImageView = holder.itemView.sivAdapterLayoutStaggered
                    )
                    AppUtils.populateGlide(
                        requireActivity(),//pinBoardResponse.idJob =
                        bitmap = null,
                        holder.itemView.sivAdapterLayoutStaggered
                    )
                }
            }

            holder.itemView.mtvAdapterLayoutStaggeredTitle.text = model.user.name
            holder.itemView.mtvAdapterLayoutStaggeredLikes.text =
                requireActivity().resources.getString(
                    R.string.like_string,
                    model.likes.toString()
                )

            holder.itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("id", model.id)
                pinBoardResponse._bitmap.value = cache.get(model.id)
                navigateToFragment(bundle, R.id.action_FirstFragment_to_SecondFragment)
            }

            holder.itemView.sivMore.setOnClickListener {
                //creating a popup menu
                val popup = PopupMenu(requireActivity(), it)
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_main)

                popup.menu.getItem(0).title =
                    when (hashMapJobs.get(model.id)?.isActive) {
                        true -> requireActivity().resources.getString(R.string.cancel_download)
                        else -> requireActivity().resources.getString(R.string.start_download)
                    }

                //adding click listener for pop up menu
                popup.setOnMenuItemClickListener { item ->
                    when (item?.getItemId()) {
                        R.id.action_settings -> {
                            LoggerClass.getLoggerClass().verbose("onclicked", "${hashMapJobs}")

                            when (hashMapJobs.get(model.id)?.isActive) {
                                true -> asyncTask.cancelDownload(
                                    job = hashMapJobs.get(model.id) ?: CoroutineScope(
                                        Job()
                                    )
                                )

                                else -> asyncTask.execute(
                                    model.urls.raw,
                                    model = model,
                                    shapeableImageView = holder.itemView.sivAdapterLayoutStaggered
                                )
                            }

                            true
                        }
                        else -> false
                    }
                }
                //displaying the popup
                popup.show()
            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cache = BitmapsCache(AppUtils.getMaxSize(context = requireContext()))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing recycler view
        binding.rvFragmentFirst.apply {
            layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                }
            adapter = pinBoardAdapter
        }



        pinBoardResponse.arrPinBoardResponse.observe(viewLifecycleOwner, {
            binding.srlSwipeToRefresh.isRefreshing = false
            when (it) {
                is JsonArrayResponse -> {
                    arrayPinBoardResponse.clear()
                    arrayPinBoardResponse.addAll(it.data)
                    pinBoardAdapter.notifyDataSetChanged()
                    pinBoardAdapter.setHasStableIds(true)
                    LoggerClass.getLoggerClass().verbose(data = it.data)
                }
                is ErrorResponse -> {
                    LoggerClass.getLoggerClass().verbose(data = it.sData)
                }
            }
        })


        //swipe to refresh listener
        binding.srlSwipeToRefresh.setOnRefreshListener {
            LoggerClass.getLoggerClass().verbose(data = "${cache}")
            pinBoardResponse.callAPI(APICallInterface.sPinBoardResponseUrl)
        }

        pinBoardResponse.callAPI(APICallInterface.sPinBoardResponseUrl)

    }


}