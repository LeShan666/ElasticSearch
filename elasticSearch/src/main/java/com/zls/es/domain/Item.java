package com.zls.es.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Spring Data作用在类，标记实体类为文档对象，一般有两个属性
 *      indexName：对应在索引苦衷的类型
 *      type：对应在索引库中的类型
 *      shards：分片数量，默认为5
 *      replicas：副本数量，默认1
 * @Id  作用在成员变量，标记为文档的字段，并指定字段映射属性
 * @Field 作用在成员变量，标记为文档的字段，并指定字段映射属性：
 *      type：字段类型，取值是枚举：FieldType
 *      index：是否索引，布尔类型，默认是false
 *      analyzer：分词器名称
 * @AllArgsConstructor
 * @NoArgsConstructor
 *      构造函数
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "item",type = "goods",shards = 1,replicas = 1)
public class Item {
    @Id
    Long id;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    String title; //标题
    @Field(type = FieldType.Keyword)
    String category;// 分类
    @Field(type = FieldType.Keyword)
    String brand; // 品牌
    @Field(type = FieldType.Double)
    Double price; // 价格
    @Field(index = false,type = FieldType.Keyword)
    String images; // 图片地址
}
