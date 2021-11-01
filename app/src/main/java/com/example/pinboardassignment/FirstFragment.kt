package com.example.pinboardassignment

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pinboardassignment.cache.BitmapsCache
import com.example.pinboardassignment.databinding.FragmentFirstBinding
import com.example.pinboardassignment.generics.GenericAdapter
import com.example.pinboardassignment.generics.GenericItemViewHolders
import com.example.pinboardassignment.model.PinBoardResponse
import com.example.pinboardassignment.network.APICallInterface
import com.example.pinboardassignment.network.ErrorResponse
import com.example.pinboardassignment.network.JsonArrayResponse
import com.example.pinboardassignment.utils.LoggerClass
import com.example.pinboardassignment.viewmodels.PinBoardViewModel
import com.imagepreviewer.idriveassignment.CoroutineAsynctask
import kotlinx.android.synthetic.main.adapter_layout_staggered_list.view.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var cache: BitmapsCache

    private val pinBoardResponse by viewModels<PinBoardViewModel>()

    private val arrayPinBoardResponse = ArrayList<PinBoardResponse>()

    lateinit var asyncTask: CoroutineAsynctask<String, String, Bitmap>


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

                override fun doInBackground(vararg params: String): Bitmap? {
                    val bitmap = downloadBitmap(params[0], model)
                    cache.put(model.id, bitmap)

                    return bitmap

                }

                override fun onCancelled(sErrorMessage: String) {
                    super.onCancelled(sErrorMessage)
                    LoggerClass.getLoggerClass().verbose(data = sErrorMessage)
                }

                override fun onPostExecute(result: Bitmap?) {
                    super.onPostExecute(result)
                    result?.let {
                        Glide.with(requireActivity()).asBitmap().load(it)
                            .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                            .into(holder.itemView.sivAdapterLayoutStaggered)
                    }
                }
            }

            when {
                cache.get(model.id) != null -> {
                    Glide.with(requireActivity()).asBitmap().load(cache.get(model.id))
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .into(holder.itemView.sivAdapterLayoutStaggered)

                }
                else -> when {
                    !model.status -> asyncTask.execute(model.urls.raw)
                    else -> {
                        asyncTask.cancelDownload()
                        holder.itemView.sivAdapterLayoutStaggered.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.image_placeholder
                            )
                        );
                    }
                }
            }

            holder.itemView.sivAdapterLayoutStaggered.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.image_placeholder
                )
            )

            holder.itemView.mtvAdapterLayoutStaggeredTitle.text = model.user.name
            holder.itemView.mtvAdapterLayoutStaggeredLikes.text = "${model.likes} Likes"

            holder.itemView.sivMore.setOnClickListener {
                //creating a popup menu
                val popup = PopupMenu(requireActivity(), it)
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_main)

                when (arrayPinBoardResponse.get(holder.adapterPosition).status) {
                    true -> popup.menu.getItem(0).title =
                        requireActivity().resources.getString(R.string.start_download)
                    false -> popup.menu.getItem(0).title =
                        requireActivity().resources.getString(R.string.cancel_download)
                }

                //adding click listener for pop up menu
                popup.setOnMenuItemClickListener { item ->
                    when (item?.getItemId()) {
                        R.id.action_settings -> {
                            arrayPinBoardResponse.get(holder.adapterPosition).status =
                                when (arrayPinBoardResponse.get(holder.adapterPosition).status) {
                                    true -> false
                                    false -> true
                                }
                            notifyItemChanged(holder.adapterPosition)
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val am = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val availMemInBytes = (am?.memoryClass ?: 0) * 1024 * 1024
        cache = BitmapsCache(availMemInBytes)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }


        //initializing recycler view
        binding.rvFragmentFirst.apply {
            layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                }
            adapter = pinBoardAdapter
        }


        //observer for pin board response
        pinBoardResponse.arrPinBoardResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is JsonArrayResponse -> {
                    binding.srlSwipeToRefresh.isRefreshing = false
                    arrayPinBoardResponse.clear()
                    arrayPinBoardResponse.addAll(it.data)
                    pinBoardAdapter.notifyDataSetChanged()
                    LoggerClass.getLoggerClass().verbose(data = it.data)
                }
                is ErrorResponse -> LoggerClass.getLoggerClass().verbose(data = it.sData)

            }

        })


        //swipe to refresh listener
        binding.srlSwipeToRefresh.setOnRefreshListener {
            LoggerClass.getLoggerClass().verbose(data = "${cache}")
            pinBoardResponse.callAPI(APICallInterface.sPinBoardResponseUrl)
        }

        pinBoardResponse.callAPI(APICallInterface.sPinBoardResponseUrl)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**
     * @author: SundravelS on 31-10-2021
     *
     * @param sUrl:String
     * @param model:PinBoardResponse
     * @desc: Below Class download bitmap from server
     *
     */

    fun downloadBitmap(sUrl: String, model: PinBoardResponse): Bitmap? {
        LoggerClass.getLoggerClass().verbose(data = sUrl)
        var urlConnection: HttpsURLConnection? = null
        try {

            val uri = URL(sUrl)
            urlConnection = uri.openConnection() as HttpsURLConnection


            when {
                model.status -> {
                    urlConnection.disconnect()
                    return null
                }

            }
            val statusCode = urlConnection.responseCode

            if (statusCode != HttpsURLConnection.HTTP_OK) {
                return null
            }
            val inputStream = urlConnection.inputStream
            if (inputStream != null) {

                val bitmap = BitmapFactory.decodeStream(inputStream)

                return bitmap
            }
        } catch (e: Exception) {
            urlConnection?.disconnect()
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect()
            }

        }

        return null
    }

}