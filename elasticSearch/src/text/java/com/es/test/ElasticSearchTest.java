package com.es.test;

import com.zls.ESApplication;
import com.zls.es.domain.Item;
import com.zls.es.mapper.ItemRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * ElasticSearch集群
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ESApplication.class)
public class ElasticSearchTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ItemRepository itemRepository;





    @Test
    public void testCreate(){
        // 创建索引，会根据Item类的@Document注解信息来创建
        elasticsearchTemplate.createIndex(Item.class);
        // 配置映射，会根据Item类中的id，FIeld等字段自动完成映射
        elasticsearchTemplate.putMapping(Item.class);
    }

    @Test
    public void findAll(){
        List<Item> list = new ArrayList<>();
        list.add(new Item(1L, "小米手机7", "手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(4L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13123.jpg"));
        // 接收对象集合，实现批量新增
        itemRepository.saveAll(list);
    }

    @Test
    public  void findBy(){
        List<Item> list = itemRepository.findByPriceIsBetween(2000d, 3800d);
        for(Item item:list){
            System.out.println(item);
        }
    }

    /**
     * 高级查询：自定义查询
     */
    @Test
    public void testQuery(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "title", "price"},null));
        // 添加查询条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("title","小米手机"));
        // 排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));
        // 分页
        queryBuilder.withPageable(PageRequest.of(0,2));

        Page<Item> result = itemRepository.search(queryBuilder.build());
        // 查询总条数
        long totalElements = result.getTotalElements();
        System.out.println(totalElements);
        // 查询的总页数
        int totalPages = result.getTotalPages();
        System.out.println(totalPages);
        List<Item> list = result.getContent();
        for (Item item:list){
            System.out.println(item);
        }
    }

    /**
     * 聚合查询
     */
    @Test
    public void testAgg(){
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        // 1.添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand"));
        // 2.查询，需要把结果强行转为AggregatePage类型
        AggregatedPage<Item> aggregatedPage = (AggregatedPage<Item>)itemRepository.search(nativeSearchQueryBuilder.build());
        // 3.解析
        // 3.1 从结果中取出brands的那个聚合
        //      因为是利用string类型字段来进行的term聚合，所以结果要强行转为StringTerm类型
        StringTerms agg = (StringTerms) aggregatedPage.getAggregation("brands");
        // 3.2获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3便利
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称
            System.out.println(bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.out.println(bucket.getDocCount());
        }

    }
}




































