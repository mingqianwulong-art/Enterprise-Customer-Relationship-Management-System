package com.crm.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 客户Mapper
 *
 * @author CRM
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    /**
     * 清空已逻辑删除记录的信用代码，释放唯一索引占用。
     */
    @Update("UPDATE cus_customer SET credit_code = NULL, update_time = NOW() " +
            "WHERE credit_code = #{creditCode} AND deleted = 1")
    int clearDeletedCreditCode(@Param("creditCode") String creditCode);

    /**
     * 清空指定已逻辑删除记录的信用代码。
     */
    @Update("UPDATE cus_customer SET credit_code = NULL, update_time = NOW() " +
            "WHERE id = #{id} AND deleted = 1")
    int clearDeletedCreditCodeById(@Param("id") Long id);
}
