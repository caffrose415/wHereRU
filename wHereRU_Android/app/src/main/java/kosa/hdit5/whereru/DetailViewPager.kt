package kosa.hdit5.whereru

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kosa.hdit5.whereru.databinding.ActivityDetailViewPagerBinding
import kosa.hdit5.whereru.databinding.DetailItemPagerBinding
import kosa.hdit5.whereru.util.retrofit.main.RetrofitBuilder
import kosa.hdit5.whereru.util.retrofit.main.`interface`.WhereRUAPI
import kosa.hdit5.whereru.util.retrofit.main.vo.MissingBoardVo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailPagerViewHolder(val binding: DetailItemPagerBinding) : RecyclerView.ViewHolder(binding.root)

class DetailPagerAdapter(private var DetailData: MutableList<MissingBoardVo>) : RecyclerView.Adapter<DetailPagerViewHolder>() {
    private val imageUrlList = mutableListOf<String>()

    override fun getItemCount(): Int {
        return DetailData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailPagerViewHolder {
        return DetailPagerViewHolder(
            DetailItemPagerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DetailPagerViewHolder, position: Int) {
        val binding = holder.binding

        if (position < imageUrlList.size && URLUtil.isValidUrl(imageUrlList[position])) {
            Log.d("Image URL", imageUrlList[position])
            Glide.with(holder.itemView.context)
                .load(imageUrlList[position])
                .into(binding.detailImg)
        } else {
            Log.d("Image URL", "Invalid URL or position out of range: $position")
        }

    }

    fun setItem(data: MutableList<MissingBoardVo>) {
        Log.d("setItem", data.toString())
        DetailData = data
        buildImageUrlList()
        notifyDataSetChanged()
    }

    private fun buildImageUrlList() {
        Log.d("function", "building image url list")
        imageUrlList.clear()
        DetailData.forEach { MissingBoardVo ->
            imageUrlList.addAll(MissingBoardVo.getImageUrls())  // 문자열을 리스트로 변환하고 추가
        }
        Log.d("function", "list built: $imageUrlList")
    }
}

class DetailViewPager : Fragment() {
    private lateinit var binding: ActivityDetailViewPagerBinding
    private var apiService: WhereRUAPI = RetrofitBuilder.api
    private lateinit var DetailDataAdapter: DetailPagerAdapter
    private val DetailData = mutableListOf<MissingBoardVo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityDetailViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DetailDataAdapter = DetailPagerAdapter(DetailData)
        binding.detailviewpager.adapter = DetailDataAdapter

        getTotalList()
    }

    private fun getTotalList() {
        val call = apiService.getTotalList()
        call.enqueue(object : Callback<List<MissingBoardVo>> {
            override fun onResponse(
                call: Call<List<MissingBoardVo>>,
                response: Response<List<MissingBoardVo>>
            ) {
                Log.d("Hong", "$response")
                if (response.isSuccessful) {
                    val missingPersonimgList = response.body()
                    if (missingPersonimgList != null) {
                        missingPersonimgList.forEach { img ->
                            DetailData.add(img)
                        }
                        DetailDataAdapter.setItem(DetailData)
                    }
                } else {
                    // 서버로부터 응답을 받지 못한 경우 처리
                    Log.d("Hong", "실패")
                }
            }

            override fun onFailure(call: Call<List<MissingBoardVo>>, t: Throwable) {
                // 요청 자체가 실패한 경우 처리
                Log.d("Hong", "ERROR")
            }
        })
    }

}
