package com.zls.es.mapper;

import com.zls.es.domain.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ItemRepository extends ElasticsearchRepository<Item,Long> {

    List<Item> findByPriceIsBetween(Double begin,Double end);
}
