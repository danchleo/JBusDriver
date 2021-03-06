package me.jbusdriver.mvp.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import me.jbusdriver.common.KLog
import me.jbusdriver.common.urlHost
import me.jbusdriver.http.JAVBusService
import me.jbusdriver.ui.data.DataSourceType
import org.jsoup.nodes.Document
import java.io.Serializable

/**
 * Created by Administrator on 2017/4/16.
 */
data class Movie(
        val type: DataSourceType,
        val title: String,
        val imageUrl: String,
        val code: String, //番号
        val date: String, //日期
        val detailUrl: String,
        val tags: List<String> = listOf()//标签,

) : MultiItemEntity, Serializable {

    override fun getItemType(): Int = type.ordinal

    companion object {
        //图片url host 设置
        fun loadFromDoc(type: DataSourceType, str: Document): List<Movie> {
            return str.select(".movie-box").mapIndexed { index, element ->
                KLog.d(element)
                Movie(
                        type = type,
                        title = element.select("img").attr("title"),
                        imageUrl = element.select("img").attr("src").apply {
                            if (index == 0)  JAVBusService.defaultImageUrlHost = this.urlHost //设置一次
                        },
                        code = element.select("date").first().text(),
                        date = element.select("date").getOrNull(1)?.text() ?: "",
                        detailUrl = element.attr("href"),
                        tags = element.select(".item-tag").firstOrNull()?.children()?.map { it.text() } ?: emptyList()
                )
            }
        }
    }
}

val Movie.detailSaveKey
    inline get() = code + "_" + date

