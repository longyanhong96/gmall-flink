use
test;
-- 商品维度表
with sku_info as (
    select id
         , price
         , sku_name
         , sku_desc
         , weight
         , is_sale
         , spu_id
         , tm_id
         , category3_id
         , create_time
    from ods_sku_info
    where dt = '${hiveconf:day}'
),
     category1 as (
         select id
              , name
         from ods_base_category1
         where dt = '${hiveconf:day}'
     ),
     category2 as (
         select id
              , name
              , category1_id
         from ods_base_category2
         where dt = '${hiveconf:day}'
     ),
     category3 as (
         select id
              , name
              , category2_id
         from ods_base_category3
         where dt = '${hiveconf:day}'
     ),
     spu_info as (
         select id
              , spu_name
              , tm_id
         from ods_spu_info
         where dt = '${hiveconf:day}'
     ),
     base_trademark as (
         select id
              , tm_name
         from ods_base_trademark
         where dt = '${hiveconf:day}'
     ),
     sku_attr as (
         select sku_id
              , collect_set(
                 named_struct('attr_id', attr_id, 'value_id', value_id, 'attr_name', attr_name, 'value_name',
                              value_name)) as sku_attr_values
         from ods_sku_attr_value
         where dt = '${hiveconf:day}'
         group by sku_id
     ),
     sku_sale_attr as (
         select sku_id
              , collect_set(
                 named_struct('sale_attr_id', sale_attr_id, 'sale_attr_value_id', sale_attr_value_id, 'sale_attr_name',
                              sale_attr_name, 'sale_attr_value_name', sale_attr_value_name)) as sku_sale_attr_values
         from ods_sku_sale_attr_value
         where dt = '${hiveconf:day}'
         group by sku_id
     )
insert
overwrite table dim_sku_info partition(dt='${hiveconf:day}')
select sku.id
     , sku.price
     , sku.sku_name
     , sku.sku_desc
     , sku.weight
     , sku.is_sale
     , sku.spu_id
     , spu_info.spu_name
     , sku.category3_id
     , category3.name
     , category3.category2_id
     , category2.name
     , category2.category1_id
     , category1.name
     , sku.tm_id
     , base_trademark.tm_name
     , sku_attr.sku_attr_values
     , sku_sale_attr.sku_sale_attr_values
     , sku.create_time
from sku_info sku
         left join category3 on category3.id = sku.category3_id
         left join category2 on category2.id = category3.category2_id
         left join category1 on category1.id = category2.category1_id
         left join spu_info on spu_info.id = sku.spu_id
         left join base_trademark on base_trademark.id = sku.tm_id
         left join sku_attr on sku_attr.sku_id = sku.id
         left join sku_sale_attr on sku_sale_attr.sku_id = sku.id;

-- 优惠券维度表
insert
overwrite table dim_coupon_info partition(dt='${hiveconf:day}')
select id
     , coupon_name
     , coupon_type
     , condition_amount
     , condition_num
     , activity_id
     , benefit_amount
     , benefit_discount
     , create_time
     , range_type
     , limit_num
     , taken_count
     , start_time
     , end_time
     , operate_time
     , expire_time
from ods_coupon_info
where dt = '${hiveconf:day}';


-- 活动信息表
with rule as (
    select *
    from ods_activity_rule
    where dt = '${hiveconf:day}'
),
     info as (
         select *
         from ods_activity_info
         where dt = '${hiveconf:day}'
     )
insert
overwrite table dim_activity_rule_info partition(dt='${hiveconf:day}')
select rule.id
     , rule.activity_id
     , info.activity_name
     , rule.activity_type
     , info.start_time
     , info.end_time
     , info.create_time
     , rule.condition_amount
     , rule.condition_num
     , rule.benefit_amount
     , rule.benefit_discount
     , rule.benefit_level
from rule
         left join info on rule.activity_id = info.id;


-- 地区维度表
insert
overwrite table dim_base_province
select pro.id
     , pro.name
     , pro.area_code
     , pro.iso_code
     , pro.iso_3166_2
     , pro.region_id
     , region.region_name
from ods_base_province pro
         left join ods_base_region region
                   on pro.region_id = region.id;